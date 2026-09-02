package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.infrastructure.http.dto.UserResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.AuthResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.LoginRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.RegisterRequestDto;
import com.mabc.e_shop.infrastructure.security.AdminUserService;
import com.mabc.e_shop.infrastructure.security.AuthService;
import com.mabc.e_shop.infrastructure.security.JwtCookieManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, UserController.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private static final AuthResponseDto TOKENS = new AuthResponseDto(
            "access-token", "refresh-token", "Bearer", 900L, "ana@tienda.cl", "USER");

    private static final String REFRESH_COOKIE = "refresh_token=refresh-token; HttpOnly; Path=/api/v1/auth; Max-Age=604800; SameSite=Strict";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AdminUserService adminUserService;

    @MockitoBean
    private JwtCookieManager cookieManager;

    @Test
    @DisplayName("POST /register registra, setea la cookie de refresco y responde 201 sin refresh en el body")
    void registerReturns201WithCookieAndNoRefreshInBody() throws Exception {
        when(authService.register(any(RegisterRequestDto.class))).thenReturn(TOKENS);
        when(cookieManager.createRefreshCookie(TOKENS.refreshToken())).thenReturn(REFRESH_COOKIE);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana Rivera", "email": "ana@tienda.cl", "password": "secreta123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")));
    }

    @Test
    @DisplayName("POST /register con datos inválidos responde 400")
    void registerRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "email": "correo-invalido", "password": "corta"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message", containsString("contraseña")));
    }

    @Test
    @DisplayName("POST /login responde 200 con access token, cookie HttpOnly y sin refresh en el body")
    void loginReturns200WithCookieAndNoRefreshInBody() throws Exception {
        when(authService.login(any(LoginRequestDto.class))).thenReturn(TOKENS);
        when(cookieManager.createRefreshCookie(TOKENS.refreshToken())).thenReturn(REFRESH_COOKIE);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "ana@tienda.cl", "password": "secreta123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.email").value("ana@tienda.cl"))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")));
    }

    @Test
    @DisplayName("POST /refresh con cookie de refresco responde 200 y rota la cookie")
    void refreshReturns200WithCookie() throws Exception {
        when(cookieManager.readRefreshToken(any())).thenReturn("refresh-token");
        when(authService.refresh("refresh-token")).thenReturn(TOKENS);
        when(cookieManager.createRefreshCookie(TOKENS.refreshToken())).thenReturn(REFRESH_COOKIE);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", "refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")));
    }

    @Test
    @DisplayName("POST /refresh sin cookie de refresco responde 401")
    void refreshRejectsMissingCookie() throws Exception {
        when(cookieManager.readRefreshToken(any())).thenReturn(null);
        when(authService.refresh(null)).thenThrow(new BadCredentialsException("Refresh token inválido."));

        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401));
    }

    @Test
    @DisplayName("POST /logout expira la cookie de refresco y responde 200")
    void logoutExpiresRefreshCookie() throws Exception {
        when(cookieManager.clearRefreshCookie()).thenReturn(
                "refresh_token=; Path=/api/v1/auth; Max-Age=0; HttpOnly; SameSite=Strict");

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));
    }

    @Test
    @DisplayName("GET /users responde 200 con el listado sin contraseña")
    void listUsersReturns200WithoutPassword() throws Exception {
        when(adminUserService.list()).thenReturn(List.of(
                new UserResponseDto(1L, "Ana Rivera", "ana@tienda.cl", "USER", true)));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].email").value("ana@tienda.cl"))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());
    }
}