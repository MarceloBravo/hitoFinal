package com.mabc.e_shop.infrastructure.http.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta HTTP con los tokens de sesión de un usuario.
 *
 * <p>Se entrega tras el registro, el inicio de sesión y la renovación
 * de tokens. El access token es de corta duración y el refresh token
 * permite obtener un nuevo par sin volver a autenticar.
 *
 * @param accessToken  token JWT de corta duración para autorizar peticiones.
 * @param refreshToken token JWT de larga duración para renovar la sesión.
 * @param tokenType    tipo de token.
 * @param expiresIn    vigencia del access token en segundos.
 * @param email        correo del usuario autenticado.
 * @param role         rol del usuario autenticado.
 */
public record AuthResponseDto(
    @Schema(description = "Access token para autorizar peticiones.")
    String accessToken,
    @Schema(description = "Refresh token para renovar la sesión.")
    String refreshToken,
    @Schema(description = "Tipo de token devuelto.", example = "Bearer")
    String tokenType,
    @Schema(description = "Vigencia del access token en segundos.", example = "900")
    long expiresIn,
    @Schema(description = "Correo del usuario autenticado.", example = "ana@tienda.cl")
    String email,
    @Schema(description = "Rol del usuario autenticado.", example = "ADMIN")
    String role
) {
}