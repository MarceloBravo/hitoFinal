package com.mabc.e_shop.infrastructure.http.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de petición para registrar un usuario nuevo.
 *
 * @param name     nombre completo del usuario; obligatorio.
 * @param email    correo del usuario; obligatorio, con formato válido y único.
 * @param password contraseña del usuario; obligatoria, con al menos 8 caracteres.
 */
public record RegisterRequestDto(
    @Schema(description = "Nombre completo del usuario.", example = "Ana Rivera")
    @NotBlank(message = "El nombre es obligatorio.") String name,
    @Schema(description = "Correo electrónico del usuario.", example = "ana@tienda.cl")
    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.") String email,
    @Schema(description = "Contraseña del usuario.", example = "secreta123")
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.") String password
) {
}