package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de petición para crear o actualizar un usuario.
 *
 * <p>El campo {@code password} es obligatorio al crear, pero opcional al
 * actualizar (si viene {@code null} o en blanco, la contraseña no cambia).
 *
 * @param name     nombre completo del usuario; obligatorio.
 * @param email    correo del usuario; obligatorio, con formato válido y único.
 * @param password contraseña del usuario; obligatoria al crear, opcional al
 *                 actualizar.
 * @param role     rol asignado al usuario (p. ej. {@code USER} o {@code ADMIN}).
 * @param active   estado de actividad de la cuenta.
 */
public record UserRequestDto(
    @Schema(description = "Nombre completo del usuario.", example = "Ana Rivera")
    @NotBlank(message = "El nombre es obligatorio.") String name,
    @Schema(description = "Correo electrónico del usuario.", example = "ana@tienda.cl")
    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.") String email,
    @Schema(description = "Contraseña del usuario; obligatoria al crear, opcional al actualizar.", example = "secreta123")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.") String password,
    @Schema(description = "Rol asignado al usuario.", example = "USER")
    @NotBlank(message = "El rol es obligatorio.") String role,
    @Schema(description = "Estado de actividad de la cuenta.", example = "true") Boolean active
) {
}
