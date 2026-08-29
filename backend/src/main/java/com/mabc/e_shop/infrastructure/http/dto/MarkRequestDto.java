package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de petición para registrar o actualizar una marca.
 *
 * @param name   nombre de la marca; obligatorio.
 * @param active estado de activación de la marca; obligatorio.
 */
public record MarkRequestDto(
    @Schema(description = "Nombre de la marca.", example = "Lenovo")
    @NotBlank(message = "El nombre de la marca es obligatorio.") String name,
    @Schema(description = "Estado de activación de la marca.", example = "true")
    @NotNull(message = "El estado de activación es obligatorio.") Boolean active
) {
}
