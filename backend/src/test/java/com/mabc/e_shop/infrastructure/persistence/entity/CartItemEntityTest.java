package com.mabc.e_shop.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class CartItemEntityTest {

    @Test
    @DisplayName("Constructor por defecto: deja todos los campos nulos")
    void defaultConstructorLeavesFieldsNull() {
        CartItemEntity entity = new CartItemEntity();

        assertNull(entity.getId());
        assertNull(entity.getCart());
        assertNull(entity.getProduct());
        assertNull(entity.getCant());
        assertNull(entity.getSubTotal());
    }

    @Test
    @DisplayName("Getters y setters: asignan y recuperan todos los campos")
    void gettersAndSetters() {
        CartItemEntity entity = new CartItemEntity();

        CartEntity cart = new CartEntity();
        ProductEntity product = new ProductEntity();

        entity.setId(5L);
        entity.setCart(cart);
        entity.setProduct(product);
        entity.setCant(3);
        entity.setSubTotal(2400000.0);

        assertEquals(5L, entity.getId());
        assertSame(cart, entity.getCart());
        assertSame(product, entity.getProduct());
        assertEquals(3, entity.getCant());
        assertEquals(2400000.0, entity.getSubTotal());
    }
}
