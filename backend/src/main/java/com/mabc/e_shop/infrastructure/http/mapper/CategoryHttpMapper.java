package com.mabc.e_shop.infrastructure.http.mapper;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.infrastructure.http.dto.CategoryResponseDto;

/**
 * Mapper que convierte la entidad de dominio {@link Category} en su DTO de
 * respuesta HTTP {@link CategoryResponseDto}.
 *
 * <p>Clase utilitaria con métodos estáticos, no instanciable.
 */
public final class CategoryHttpMapper {

    private CategoryHttpMapper() {
    }

    /**
     * Convierte una categoría de dominio en su DTO de respuesta HTTP.
     *
     * @param category categoría de dominio a convertir.
     * @return el DTO de respuesta resultante.
     */
    public static CategoryResponseDto toResponse(Category category) {
        return new CategoryResponseDto(category.getId(), category.getName().value(), category.isActive());
    }
}
