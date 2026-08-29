package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta HTTP con los datos de un carrito de compras.
 *
 * @param id           identificador del carrito.
 * @param creationDate fecha de creación del carrito.
 * @param items        ítems que componen el carrito.
 * @param subTotal     subtotal acumulado del carrito.
 */
public record CartResponseDto(
    @Schema(description = "Identificador del carrito.", example = "7") Long id,
    @Schema(description = "Fecha de creación del carrito.") LocalDateTime creationDate,
    @Schema(description = "Ítems que componen el carrito.") List<CartItemResponseDto> items,
    @Schema(description = "Subtotal acumulado del carrito.", example = "100.0") double subTotal
) {
}
