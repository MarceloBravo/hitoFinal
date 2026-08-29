package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta HTTP con los datos de una categoría.
 *
 * @param id     identificador de la categoría.
 * @param name   nombre de la categoría.
 * @param active estado de activación de la categoría.
 */
public record CategoryResponseDto(
    @Schema(description = "Identificador de la categoría.", example = "1") Long id,
    @Schema(description = "Nombre de la categoría.", example = "Gaming") String name,
    @Schema(description = "Estado de activación.", example = "true") boolean active
) {
}
