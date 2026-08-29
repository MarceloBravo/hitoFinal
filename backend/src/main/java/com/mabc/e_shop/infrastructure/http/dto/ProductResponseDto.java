package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Respuesta HTTP con los datos de un producto.
 *
 * @param id          identificador del producto.
 * @param markId      identificador de la marca del producto.
 * @param markName    nombre de la marca del producto.
 * @param categoryIds identificadores de las categorías del producto.
 * @param name        nombre del producto.
 * @param description descripción del producto.
 * @param stock       unidades en stock.
 * @param weight      peso del producto en kilogramos.
 * @param priceCost   precio de costo del producto.
 * @param priceSale   precio de venta del producto.
 * @param imagePath   ubicación de la imagen del producto
 */
public record ProductResponseDto(
    @Schema(description = "Identificador del producto.", example = "5") Long id,
    @Schema(description = "Identificador de la marca.", example = "1") Long markId,
    @Schema(description = "Nombre de la marca.", example = "Lenovo") String markName,
    @Schema(description = "Identificadores de las categorías.", example = "[2, 3]") List<Long> categoryIds,
    @Schema(description = "Nombre del producto.", example = "Notebook") String name,
    @Schema(description = "Descripción del producto.", example = "Equipo portátil") String description,
    @Schema(description = "Unidades en stock.", example = "10") int stock,
    @Schema(description = "Peso en kilogramos.", example = "2.5") double weight,
    @Schema(description = "Precio de costo.", example = "500.0") double priceCost,
    @Schema(description = "Precio de venta.", example = "700.0") double priceSale,
    @Schema(description = "Ubicación de la imagen del producto.", example = "https://images.example.com/products/notebook.png") String imagePath
) {
}
