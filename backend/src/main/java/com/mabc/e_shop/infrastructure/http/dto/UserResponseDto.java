package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta HTTP con los datos de un usuario registrado.
 *
 * <p>Omite deliberadamente la contraseña para nunca exponer el hash.
 *
 * @param id     identificador del usuario.
 * @param name   nombre completo del usuario.
 * @param email  correo electrónico del usuario.
 * @param role   rol asignado al usuario.
 * @param active estado de actividad de la cuenta.
 */
public record UserResponseDto(
    @Schema(description = "Identificador del usuario.", example = "1") Long id,
    @Schema(description = "Nombre completo del usuario.", example = "Ana Rivera") String name,
    @Schema(description = "Correo electrónico del usuario.", example = "ana@tienda.cl") String email,
    @Schema(description = "Rol asignado al usuario.", example = "USER") String role,
    @Schema(description = "Estado de actividad de la cuenta.", example = "true") Boolean active
) {
}