package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Cuerpo de petición, en multipart/form-data, para crear o actualizar un producto.
 *
 * @param markId      identificador de la marca del producto; obligatorio.
 * @param categoryIds identificadores de las categorías del producto; al menos una.
 * @param name        nombre del producto; obligatorio.
 * @param description descripción del producto; obligatoria.
 * @param stock       unidades en stock; cero o positivo.
 * @param weight      peso del producto en kilogramos; positivo.
 * @param priceCost   precio de costo del producto; cero o positivo.
 * @param priceSale   precio de venta del producto; cero o positivo.
 * @param image       archivo de imagen del producto.
 */
public record ProductRequestDto(
    @Schema(description = "Identificador de la marca del producto.", example = "1")
    @NotNull(message = "El identificador de la marca es obligatorio.") Long markId,
    @Schema(description = "Identificadores de las categorías del producto.", example = "[2, 3]")
    @NotEmpty(message = "El producto requiere al menos una categoría.") List<Long> categoryIds,
    @Schema(description = "Nombre del producto.", example = "Notebook")
    @NotBlank(message = "El nombre del producto es obligatorio.") String name,
    @Schema(description = "Descripción comercial del producto.", example = "Equipo portátil")
    @NotBlank(message = "La descripción del producto es obligatoria.") String description,
    @Schema(description = "Unidades disponibles en stock.", example = "10")
    @NotNull(message = "El stock del producto es obligatorio.")
    @PositiveOrZero(message = "El stock no puede ser negativo.") Integer stock,
    @Schema(description = "Peso del producto en kilogramos.", example = "2.5")
    @NotNull(message = "El peso del producto es obligatorio.")
    @Positive(message = "El peso debe ser mayor que cero.") Double weight,
    @Schema(description = "Precio de costo del producto.", example = "500.0")
    @NotNull(message = "El precio de costo es obligatorio.")
    @PositiveOrZero(message = "El precio de costo no puede ser negativo.") Double priceCost,
    @Schema(description = "Precio de venta al público del producto.", example = "700.0")
    @NotNull(message = "El precio de venta es obligatorio.")
    @PositiveOrZero(message = "El precio de venta no puede ser negativo.") Double priceSale,
    @Schema(description = "Archivo de imagen del producto (JPG, PNG o WebP).", format = "binary")
    MultipartFile image
) {
}
