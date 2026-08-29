package com.mabc.e_shop.domain.entity;

import com.mabc.e_shop.domain.valueobject.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryTest {

    @Test
    @DisplayName("Categoria nueva inicia activa")
    void newCategoryStartsActive() {
        Category category = new Category(1L, new Name("Computacion"));
        assertTrue(category.isActive());
    }

    @Test
    @DisplayName("deactivate y activate cambian el estado")
    void deactivateAndActivate() {
        Category category = new Category(1L, new Name("Computacion"));

        category.deactivate();
        assertFalse(category.isActive());

        category.activate();
        assertTrue(category.isActive());
    }

    @Test
    @DisplayName("rename actualiza el nombre manteniendo el id")
    void renameKeepsId() {
        Category category = new Category(1L, new Name("Computacion"));

        category.rename(new Name("Gaming"));

        assertEquals(1L, category.getId());
        assertEquals("Gaming", category.getName().value());
    }
}
