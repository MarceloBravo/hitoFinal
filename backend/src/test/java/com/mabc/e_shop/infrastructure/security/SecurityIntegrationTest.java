package com.mabc.e_shop.infrastructure.security;

import com.jayway.jsonpath.JsonPath;
import com.mabc.e_shop.infrastructure.persistence.entity.MarkEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.User;
import com.mabc.e_shop.infrastructure.persistence.repositories.MarkJpaRepository;
import com.mabc.e_shop.infrastructure.persistence.repositories.UserJpaRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración que valida las reglas de acceso de {@link SecurityConfig}
 * con la cadena de filtros real: endpoints públicos sin token, endpoints de
 * mantención protegidos (401 anónimo, 403 sin rol ADMIN, 201/200 con admin) y
 * el flujo completo de tokens (register → usar token → refresh → logout).
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    private static final String ADMIN_EMAIL = "admin@tienda.cl";
    private static final String CLIENT_EMAIL = "cliente@tienda.cl";
    private static final String PASSWORD = "secreta123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private MarkJpaRepository markRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void seedUsers() {
        userRepository.deleteAll();
        userRepository.save(new User(null, "Admin", ADMIN_EMAIL, passwordEncoder.encode(PASSWORD), "ADMIN", true));
        userRepository.save(new User(null, "Cliente", CLIENT_EMAIL, passwordEncoder.encode(PASSWORD), "USER", true));
    }

    @Test
    @DisplayName("Los endpoints de lectura del catálogo responden 200 sin token")
    void publicReadEndpointsWorkWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").isNumber());
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
        mockMvc.perform(get("/api/v1/marks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("Crear categorías sin token responde 401")
    void anonymousCannotManageCatalog() throws Exception {
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Gaming", "active": true}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Crear categorías con rol USER responde 201 (cualquier autenticado puede gestionar)")
    void authenticatedUserCanManageCatalog() throws Exception {
        String token = loginAccessToken(CLIENT_EMAIL);

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Gaming", "active": true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201));
    }

    @Test
    @DisplayName("Crear categorías con rol ADMIN responde 201")
    void adminCanManageCatalog() throws Exception {
        String token = loginAccessToken(ADMIN_EMAIL);

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Gaming", "active": true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.name").value("Gaming"));
    }

    @Test
    @DisplayName("Login con credenciales inválidas responde 401")
    void invalidCredentialsReturn401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "admin@tienda.cl", "password": "incorrecta"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un usuario inactivo no puede iniciar sesión")
    void disabledUserCannotLogin() throws Exception {
        userRepository.save(new User(null, "Inactivo", "inactivo@tienda.cl",
                passwordEncoder.encode(PASSWORD), "USER", false));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "inactivo@tienda.cl", "password": "secreta123"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("El refresh endpoint rechaza un access token (tipo equivocado)")
    void refreshEndpointRejectsAccessToken() throws Exception {
        String token = loginAccessToken(ADMIN_EMAIL);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie(JwtCookieManager.REFRESH_COOKIE, token)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Flujo completo: register, usar token, refresh por cookie y logout")
    void fullAuthFlow() throws Exception {
        MvcResult register = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Nuevo", "email": "nuevo@tienda.cl", "password": "secreta123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(result -> result.getResponse().containsHeader(HttpHeaders.SET_COOKIE))
                .andReturn();
        String access = JsonPath.read(register.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.accessToken");

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + access)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Gaming", "active": true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201));

        MvcResult refreshResult = mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(refreshCookieFrom(register)))
                .andExpect(status().isOk())
                .andReturn();
        String newAccess = JsonPath.read(refreshResult.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.accessToken");

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Listar usuarios exige sesión y rol ADMIN: 401 anónimo, 403 USER, 200 ADMIN")
    void listUsersRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());

        String clientToken = loginAccessToken(CLIENT_EMAIL);
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());

        String adminToken = loginAccessToken(ADMIN_EMAIL);
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());
    }

    @Test
    @DisplayName("CRUD usuarios exige rol ADMIN: 401 anónimo, 403 USER, 201 ADMIN al crear")
    void userManagementRequiresAdminRole() throws Exception {
        String body = """
                {"name": "Nuevo", "email": "nuevo@tienda.cl", "password": "secreta123", "role": "USER", "active": true}
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());

        String clientToken = loginAccessToken(CLIENT_EMAIL);
        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());

        String adminToken = loginAccessToken(ADMIN_EMAIL);
        MvcResult create = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andReturn();
        Number newId = JsonPath.read(create.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.id");

        mockMvc.perform(delete("/api/v1/users/" + newId.longValue())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Eliminar marcas requiere autenticación (401 anónimo, 200 USER, 200 admin)")
    void deleteMarksRequiresAuthentication() throws Exception {
        mockMvc.perform(delete("/api/v1/marks/999"))
                .andExpect(status().isUnauthorized());

        MarkEntity userMark = markRepository.save(new MarkEntity(null, "Xiaomi", true));
        String clientToken = loginAccessToken(CLIENT_EMAIL);
        mockMvc.perform(delete("/api/v1/marks/" + userMark.getId())
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Marca eliminada correctamente."));

        MarkEntity adminMark = markRepository.save(new MarkEntity(null, "Lenovo", true));
        String adminToken = loginAccessToken(ADMIN_EMAIL);
        mockMvc.perform(delete("/api/v1/marks/" + adminMark.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Marca eliminada correctamente."));
    }

    private Cookie refreshCookieFrom(MvcResult result) {
        String setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        String nameValue = setCookie.split(";")[0];
        int separator = nameValue.indexOf('=');
        return new Cookie(nameValue.substring(0, separator), nameValue.substring(separator + 1));
    }

    private String loginAccessToken(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.accessToken");
    }
}