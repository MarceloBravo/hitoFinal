package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Respuesta paginada del listado de productos.
 *
 * <p>Estructura diseñada para ser consumida directamente por el frontend,
 * que espera los campos {@code limit}, {@code skip}, {@code total} y
 * {@code products} en el raíz del cuerpo de la respuesta.
 *
 * @param limit   cantidad máxima de productos por página.
 * @param skip    cantidad de productos omitidos desde el inicio.
 * @param total   cantidad total de productos disponibles.
 * @param products lista de productos de la página actual.
 */
public record ProductPaginatedResponseDto(
    @Schema(description = "Cantidad máxima de productos por página.", example = "10") int limit,
    @Schema(description = "Cantidad de productos omitidos.", example = "0") int skip,
    @Schema(description = "Cantidad total de productos.", example = "50") int total,
    @Schema(description = "Lista de productos de la página actual.") List<ProductResponseDto> products
) {
}
