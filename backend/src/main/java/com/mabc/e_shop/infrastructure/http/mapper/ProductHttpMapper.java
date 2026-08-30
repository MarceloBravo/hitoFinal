package com.mabc.e_shop.infrastructure.http.mapper;

import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.infrastructure.http.dto.ProductResponseDto;

/**
 * Mapper que convierte la entidad de dominio {@link Product} en su DTO de
 * respuesta HTTP {@link ProductResponseDto}.
 *
 * <p>Clase utilitaria con métodos estáticos, no instanciable.
 */
public final class ProductHttpMapper {

    private ProductHttpMapper() {
    }

    /**
     * Convierte un producto de dominio en su DTO de respuesta HTTP.
     *
     * @param product producto de dominio a convertir.
     * @return el DTO de respuesta resultante.
     */
    public static ProductResponseDto toResponse(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getMark().getId(),
                product.getMark().getName().value(),
                product.getCategories().stream().map(category -> category.getId()).toList(),
                product.getName().value(),
                product.getDescription().value(),
                product.getStock().value(),
                product.getWeight().value(),
                product.getPriceCost().value(),
                product.getPriceSale().value(),
                product.getImagePath() == null ? null : product.getImagePath().value());
    }
}
