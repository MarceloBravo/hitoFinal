package com.mabc.e_shop.infrastructure.http.mapper;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.infrastructure.http.dto.CategoryResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryHttpMapperTest {

    @Test
    @DisplayName("Convierte una categoría activa a su DTO de respuesta")
    void mapsActiveCategory() {
        Category category = new Category(1L, new Name("Gaming"));

        CategoryResponseDto response = CategoryHttpMapper.toResponse(category);

        assertEquals(1L, response.id());
        assertEquals("Gaming", response.name());
        assertTrue(response.active());
    }

    @Test
    @DisplayName("Conserva el estado de desactivación de la categoría")
    void mapsInactiveCategory() {
        Category category = new Category(2L, new Name("Oficina"));
        category.deactivate();

        assertFalse(CategoryHttpMapper.toResponse(category).active());
    }
}
