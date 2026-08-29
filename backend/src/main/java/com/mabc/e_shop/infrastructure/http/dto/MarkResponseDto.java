package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta HTTP con los datos de una marca.
 *
 * @param id     identificador de la marca.
 * @param name   nombre de la marca.
 * @param active estado de activación de la marca.
 */
public record MarkResponseDto(
    @Schema(description = "Identificador de la marca.", example = "1") Long id,
    @Schema(description = "Nombre de la marca.", example = "Lenovo") String name,
    @Schema(description = "Estado de activación.", example = "true") boolean active
) {
}
