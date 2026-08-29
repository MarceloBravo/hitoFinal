package com.mabc.e_shop.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkEntityTest {

    @Test
    @DisplayName("Constructor por defecto: deja todos los campos nulos")
    void defaultConstructorLeavesFieldsNull() {
        MarkEntity entity = new MarkEntity();

        assertNull(entity.getId());
        assertNull(entity.getName());
        assertNull(entity.getActive());
    }

    @Test
    @DisplayName("Constructor parametrizado: asigna id, nombre y estado")
    void parametrizedConstructorAssignsFields() {
        MarkEntity entity = new MarkEntity(1L, "Lenovo", true);

        assertEquals(1L, entity.getId());
        assertEquals("Lenovo", entity.getName());
        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("Getters y setters: asignan y recuperan todos los campos")
    void gettersAndSetters() {
        MarkEntity entity = new MarkEntity();

        entity.setId(2L);
        entity.setName("Asus");
        entity.setActive(false);

        assertEquals(2L, entity.getId());
        assertEquals("Asus", entity.getName());
        assertFalse(entity.getActive());
    }
}
