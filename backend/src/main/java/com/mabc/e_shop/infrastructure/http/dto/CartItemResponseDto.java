package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta HTTP con los datos de un ítem dentro de un carrito.
 *
 * @param id          identificador del ítem dentro del carrito.
 * @param productId   identificador del producto agregado.
 * @param productName nombre del producto agregado.
 * @param quantity    cantidad de unidades del producto.
 * @param subTotal    subtotal del ítem (precio de venta por cantidad).
 */
public record CartItemResponseDto(
    @Schema(description = "Identificador del ítem.", example = "10") Long id,
    @Schema(description = "Identificador del producto.", example = "3") Long productId,
    @Schema(description = "Nombre del producto.", example = "Notebook") String productName,
    @Schema(description = "Cantidad de unidades.", example = "2") int quantity,
    @Schema(description = "Subtotal del ítem.", example = "100.0") double subTotal
) {
}
