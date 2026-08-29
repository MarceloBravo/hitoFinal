package com.mabc.e_shop.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ProductEntityTest {

    @Test
    @DisplayName("Constructor por defecto: deja los campos nulos y categorias vacias")
    void defaultConstructorLeavesFieldsNull() {
        ProductEntity entity = new ProductEntity();

        assertNull(entity.getId());
        assertNull(entity.getMark());
        assertEquals(0, entity.getCategories().size());
        assertNull(entity.getName());
        assertNull(entity.getDescription());
        assertEquals(0, entity.getStock());
        assertEquals(0.0, entity.getWeight());
        assertEquals(0.0, entity.getPriceCost());
        assertEquals(0.0, entity.getPriceSale());
        assertNull(entity.getImagePath());
    }

    @Test
    @DisplayName("Getters y setters: asignan y recuperan todos los campos")
    void gettersAndSetters() {
        ProductEntity entity = new ProductEntity();

        MarkEntity mark = new MarkEntity(1L, "Lenovo", true);
        CategoryEntity category = new CategoryEntity(1L, "Computacion", true);

        entity.setId(9L);
        entity.setMark(mark);
        entity.setCategories(List.of(category));
        entity.setName("Notebook Lenovo");
        entity.setDescription("Notebook Lenovo IdeaPad 310");
        entity.setStock(12);
        entity.setWeight(1500.0);
        entity.setPriceCost(650000.0);
        entity.setPriceSale(800000.0);
        entity.setImagePath("https://images.example.com/products/notebook.png");

        assertEquals(9L, entity.getId());
        assertSame(mark, entity.getMark());
        assertSame(category, entity.getCategories().get(0));
        assertEquals("Notebook Lenovo", entity.getName());
        assertEquals("Notebook Lenovo IdeaPad 310", entity.getDescription());
        assertEquals(12, entity.getStock());
        assertEquals(1500.0, entity.getWeight());
        assertEquals(650000.0, entity.getPriceCost());
        assertEquals(800000.0, entity.getPriceSale());
        assertEquals("https://images.example.com/products/notebook.png", entity.getImagePath());
    }
}
