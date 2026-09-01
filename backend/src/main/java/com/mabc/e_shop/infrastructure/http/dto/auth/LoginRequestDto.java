package com.mabc.e_shop.infrastructure.http.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de petición para iniciar sesión.
 *
 * @param email    correo del usuario; obligatorio.
 * @param password contraseña del usuario; obligatoria.
 */
public record LoginRequestDto(
    @Schema(description = "Correo electrónico del usuario.", example = "ana@tienda.cl")
    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.") String email,
    @Schema(description = "Contraseña del usuario.", example = "secreta123")
    @NotBlank(message = "La contraseña es obligatoria.") String password
) {
}