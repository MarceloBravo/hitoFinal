package com.mabc.e_shop.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryEntityTest {

    @Test
    @DisplayName("Constructor por defecto: deja todos los campos nulos")
    void defaultConstructorLeavesFieldsNull() {
        CategoryEntity entity = new CategoryEntity();

        assertNull(entity.getId());
        assertNull(entity.getName());
        assertNull(entity.getActive());
    }

    @Test
    @DisplayName("Constructor parametrizado: asigna id, nombre y estado")
    void parametrizedConstructorAssignsFields() {
        CategoryEntity entity = new CategoryEntity(1L, "Computacion", true);

        assertEquals(1L, entity.getId());
        assertEquals("Computacion", entity.getName());
        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("Getters y setters: asignan y recuperan todos los campos")
    void gettersAndSetters() {
        CategoryEntity entity = new CategoryEntity();

        entity.setId(2L);
        entity.setName("Gaming");
        entity.setActive(false);

        assertEquals(2L, entity.getId());
        assertEquals("Gaming", entity.getName());
        assertFalse(entity.getActive());
    }
}
