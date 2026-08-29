package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de petición para agregar un producto a un carrito de compras.
 *
 * @param productId identificador del producto a agregar; obligatorio.
 * @param quantity  cantidad de unidades del producto; al menos una.
 */
public record CartItemRequestDto(
    @Schema(description = "Identificador del producto a agregar.", example = "3")
    @NotNull(message = "El identificador del producto es obligatorio.") Long productId,
    @Schema(description = "Cantidad de unidades del producto.", example = "2")
    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser al menos una unidad.") Integer quantity
) {
}
