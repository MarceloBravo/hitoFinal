package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.infrastructure.http.dto.UserResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.AuthResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.LoginRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.RefreshTokenRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.RegisterRequestDto;
import com.mabc.e_shop.infrastructure.security.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, UserController.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private static final AuthResponseDto TOKENS = new AuthResponseDto(
            "access-token", "refresh-token", "Bearer", 900L, "ana@tienda.cl", "USER");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /register registra y responde 201 con los tokens")
    void registerReturns201WithTokens() throws Exception {
        when(authService.register(any(RegisterRequestDto.class))).thenReturn(TOKENS);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana Rivera", "email": "ana@tienda.cl", "password": "secreta123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.role").value("USER"));
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
    @DisplayName("POST /login responde 200 con los tokens")
    void loginReturns200WithTokens() throws Exception {
        when(authService.login(any(LoginRequestDto.class))).thenReturn(TOKENS);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "ana@tienda.cl", "password": "secreta123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("POST /refresh responde 200 con los nuevos tokens")
    void refreshReturns200WithTokens() throws Exception {
        when(authService.refresh(any(RefreshTokenRequestDto.class))).thenReturn(TOKENS);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken": "refresh-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("POST /refresh con token vacío responde 400")
    void refreshRejectsBlankToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("POST /logout responde 200")
    void logoutReturns200() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("GET /users responde 200 con el listado sin contraseña")
    void listUsersReturns200WithoutPassword() throws Exception {
        when(authService.listUsers()).thenReturn(List.of(
                new UserResponseDto(1L, "Ana Rivera", "ana@tienda.cl", "USER", true)));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].email").value("ana@tienda.cl"))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());
    }
}