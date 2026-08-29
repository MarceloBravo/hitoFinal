package com.mabc.e_shop.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
class CartEntityTest {

    @Test
    @DisplayName("Constructor por defecto: deja los campos nulos y los items vacios")
    void defaultConstructorLeavesFieldsNull() {
        CartEntity entity = new CartEntity();

        assertNull(entity.getId());
        assertEquals(0, entity.getItems().size());
        assertNull(entity.getCreationDate());
        assertNull(entity.getSubTotal());
    }

    @Test
    @DisplayName("Getters y setters: asignan y recuperan todos los campos")
    void gettersAndSetters() {
        CartEntity entity = new CartEntity();

        CartItemEntity item = new CartItemEntity();
        LocalDateTime date = LocalDateTime.of(2026, 8, 11, 10, 30);

        entity.setId(7L);
        entity.setItems(List.of(item));
        entity.setCreationDate(date);
        entity.setSubTotal(2400000.0);

        assertEquals(7L, entity.getId());
        assertSame(item, entity.getItems().get(0));
        assertEquals(date, entity.getCreationDate());
        assertEquals(2400000.0, entity.getSubTotal());
    }
}
