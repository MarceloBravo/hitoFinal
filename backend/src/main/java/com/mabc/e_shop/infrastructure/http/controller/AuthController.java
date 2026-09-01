package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.infrastructure.http.dto.auth.AuthResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.LoginRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.RefreshTokenRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.RegisterRequestDto;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import com.mabc.e_shop.infrastructure.http.response.ApiResponseFactory;
import com.mabc.e_shop.infrastructure.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de autenticación.
 *
 * <p>Expone el registro, el inicio de sesión y la renovación de tokens
 * delegando en {@link AuthService}. El cierre de sesión es stateless: el
 * cliente descarta sus tokens y el servidor no necesita estado.
 */
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y renovación de tokens.")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Crea el controlador con el servicio de autenticación.
     *
     * @param authService servicio que orquesta la autenticación.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un usuario nuevo y devuelve sus tokens de sesión.
     *
     * @param request datos del usuario a registrar.
     * @return la respuesta estándar con los tokens y estado HTTP 201.
     */
    @Operation(summary = "Registra un usuario nuevo",
            description = "Crea un usuario con rol USER y devuelve sus tokens de acceso y refresco.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201", description = "Usuario registrado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Datos inválidos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409", description = "El correo ya está registrado.")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(
        @Valid @RequestBody RegisterRequestDto request
    ) {
        return ResponseEntity.status(201).body(ApiResponseFactory.created(
                "Usuario registrado correctamente.", authService.register(request)));
    }

    /**
     * Inicia sesión validando las credenciales.
     *
     * @param request credenciales del usuario.
     * @return la respuesta estándar con los tokens y estado HTTP 200.
     */
    @Operation(summary = "Inicia sesión",
            description = "Valida las credenciales y devuelve los tokens de acceso y refresco.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Sesión iniciada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Datos inválidos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Credenciales inválidas o usuario inactivo.")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
        @Valid @RequestBody LoginRequestDto request
    ) {
        return ResponseEntity.ok().body(ApiResponseFactory.queried(authService.login(request)));
    }

    /**
     * Renueva la sesión entregando un nuevo par de tokens.
     *
     * @param request refresh token vigente.
     * @return la respuesta estándar con los nuevos tokens y estado HTTP 200.
     */
    @Operation(summary = "Renueva los tokens de sesión",
            description = "Valida el refresh token y devuelve un nuevo par de tokens sin volver a autenticar.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Tokens renovados correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Datos inválidos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Refresh token inválido o expirado.")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(
        @Valid @RequestBody RefreshTokenRequestDto request
    ) {
        return ResponseEntity.ok().body(ApiResponseFactory.queried(authService.refresh(request)));
    }

    /**
     * Cierra la sesión de forma stateless.
     *
     * <p>El servidor no conserva estado de sesión; el cliente debe descartar
     * los tokens almacenados.
     *
     * @return la respuesta estándar confirmando el cierre.
     */
    @Operation(summary = "Cierra la sesión",
            description = "Operación stateless: el cliente descarta los tokens. El servidor no conserva sesión.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Sesión cerrada correctamente.")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Cierre de sesión exitoso.", null));
    }
}