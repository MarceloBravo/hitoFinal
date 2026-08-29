package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de petición para registrar o actualizar una categoría.
 *
 * @param name   nombre de la categoría; obligatorio.
 * @param active estado de activación de la categoría; obligatorio.
 */
public record CategoryRequestDto(
    @Schema(description = "Nombre de la categoría.", example = "Gaming")
    @NotBlank(message = "El nombre de la categoría es obligatorio.") String name,
    @Schema(description = "Estado de activación de la categoría.", example = "true")
    @NotNull(message = "El estado de activación es obligatorio.") Boolean active
) {
}
