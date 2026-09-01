package com.mabc.e_shop.infrastructure.http.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de petición para renovar los tokens de sesión.
 *
 * @param refreshToken token de refresco vigente; obligatorio.
 */
public record RefreshTokenRequestDto(
    @Schema(description = "Refresh token vigente para renovar la sesión.")
    @NotBlank(message = "El refresh token es obligatorio.") String refreshToken
) {
}