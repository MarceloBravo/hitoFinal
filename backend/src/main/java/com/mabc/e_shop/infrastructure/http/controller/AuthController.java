package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.infrastructure.http.dto.auth.AuthResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.LoginRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.RegisterRequestDto;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import com.mabc.e_shop.infrastructure.http.response.ApiResponseFactory;
import com.mabc.e_shop.infrastructure.security.AuthService;
import com.mabc.e_shop.infrastructure.security.JwtCookieManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
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
    private final JwtCookieManager cookieManager;

    /**
     * Crea el controlador con el servicio de autenticación y el administrador
     * de la cookie de refresco.
     *
     * @param authService  servicio que orquesta la autenticación.
     * @param cookieManager administrador de la cookie {@code HttpOnly} del refresh.
     */
    public AuthController(AuthService authService, JwtCookieManager cookieManager) {
        this.authService = authService;
        this.cookieManager = cookieManager;
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
        @Valid @RequestBody RegisterRequestDto request,
        HttpServletResponse response
    ) {
        AuthResponseDto tokens = authService.register(request);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieManager.createRefreshCookie(tokens.refreshToken()));
        return ResponseEntity.status(201).body(ApiResponseFactory.created(
                "Usuario registrado correctamente.", withoutRefreshToken(tokens)));
    }

    /**
     * Inicia sesión validando las credenciales.
     *
     * @param request credenciales del usuario.
     * @return la respuesta estándar con los tokens y estado HTTP 200.
     */
    @Operation(summary = "Inicia sesión",
            description = "Valida las credenciales y devuelve el access token. El refresh token se entrega por cookie HttpOnly.")
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
        @Valid @RequestBody LoginRequestDto request,
        HttpServletResponse response
    ) {
        AuthResponseDto tokens = authService.login(request);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieManager.createRefreshCookie(tokens.refreshToken()));
        return ResponseEntity.ok().body(ApiResponseFactory.queried(withoutRefreshToken(tokens)));
    }

    /**
     * Renueva la sesión a partir del refresh token de la cookie.
     *
     * <p>Valida el refresh token (cookie {@code HttpOnly}) y rota la cookie
     * con un token nuevo, devolviendo únicamente el access token en el body.
     *
     * @param request  petición con la cookie de refresco.
     * @param response respuesta donde se dispone la cookie renovada.
     * @return la respuesta estándar con el access token nuevo y estado HTTP 200.
     */
    @Operation(summary = "Renueva los tokens de sesión",
            description = "Valida el refresh token de la cookie HttpOnly y rota la sesión: devuelve un access token nuevo y una cookie renovada, sin volver a autenticar.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Tokens renovados correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Refresh token inválido o expirado.")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        String refreshToken = cookieManager.readRefreshToken(request);
        AuthResponseDto tokens = authService.refresh(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieManager.createRefreshCookie(tokens.refreshToken()));
        return ResponseEntity.ok().body(ApiResponseFactory.queried(withoutRefreshToken(tokens)));
    }

    /**
     * Cierra la sesión de forma stateless.
     *
     * <p>El servidor no conserva estado de sesión; el cliente debe descartar
     * el access token. La cookie de refresco se expira inmediatamente.
     *
     * @param response respuesta donde se expira la cookie de refresco.
     * @return la respuesta estándar confirmando el cierre.
     */
    @Operation(summary = "Cierra la sesión",
            description = "Operación stateless que expira la cookie de refresco; el cliente descarta el access token.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Sesión cerrada correctamente.")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieManager.clearRefreshCookie());
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Cierre de sesión exitoso.", null));
    }

    /**
     * Oculta el refresh token del body: la sesión se refresca por cookie y el
     * refresh token no debe ser leíble por JavaScript.
     *
     * @param tokens tokens generados por el servicio.
     * @return el mismo par con el refresh token en {@code null}.
     */
    private AuthResponseDto withoutRefreshToken(AuthResponseDto tokens) {
        return new AuthResponseDto(
            tokens.accessToken(),
            null,
            tokens.tokenType(),
            tokens.expiresIn(),
            tokens.email(),
            tokens.role()
        );
    }
}