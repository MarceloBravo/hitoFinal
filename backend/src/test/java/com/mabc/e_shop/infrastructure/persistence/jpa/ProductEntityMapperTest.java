package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.infrastructure.persistence.entity.CategoryEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.MarkEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductEntityMapperTest {

    private MarkEntity markEntity(boolean active) {
        return new MarkEntity(1L, "Lenovo", active);
    }

    private CategoryEntity categoryEntity(long id, String name, boolean active) {
        return new CategoryEntity(id, name, active);
    }

    private ProductEntity productEntity(boolean markActive, boolean withCategories) {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setMark(markEntity(markActive));
        if (withCategories) {
            entity.setCategories(List.of(
                    categoryEntity(1L, "Computacion", true),
                    categoryEntity(2L, "Gaming", false)));
        }
        entity.setName("Notebook Lenovo");
        entity.setDescription("Notebook Lenovo IdeaPad 310");
        entity.setStock(12);
        entity.setWeight(1500);
        entity.setPriceCost(650000);
        entity.setPriceSale(800000);
        entity.setImagePath("https://images.example.com/products/notebook.png");
        return entity;
    }

    @Test
    @DisplayName("toDomain: convierte todos los campos de la entidad JPA al dominio")
    void toDomainMapsAllFields() {
        Product product = ProductEntityMapper.toDomain(productEntity(true, true));

        assertEquals(1L, product.getId());
        assertEquals("Lenovo", product.getMark().getName().value());
        assertTrue(product.getMark().isActive());
        assertEquals(2, product.getCategories().size());
        assertEquals("Computacion", product.getCategories().get(0).getName().value());
        assertFalse(product.getCategories().get(1).isActive());
        assertEquals("Notebook Lenovo", product.getName().value());
        assertEquals("Notebook Lenovo IdeaPad 310", product.getDescription().value());
        assertEquals(12, product.getStock().value());
        assertEquals(1500, product.getWeight().value());
        assertEquals(650000, product.getPriceCost().value());
        assertEquals(800000, product.getPriceSale().value());
        assertEquals("https://images.example.com/products/notebook.png", product.getImagePath().value());
    }

    @Test
    @DisplayName("toDomain: propaga el estado inactivo de la marca")
    void toDomainMapsInactiveMark() {
        Product product = ProductEntityMapper.toDomain(productEntity(false, false));

        assertFalse(product.getMark().isActive());
    }

    @Test
    @DisplayName("toDomain: maneja categorias nulas como lista vacia")
    void toDomainHandlesNullCategories() {
        ProductEntity entity = productEntity(true, false);
        entity.setCategories(null);

        Product product = ProductEntityMapper.toDomain(entity);

        assertTrue(product.getCategories().isEmpty());
    }

    @Test
    @DisplayName("toEntity: convierte todos los campos del dominio a la entidad JPA")
    void toEntityMapsAllFields() {
        Product product = ProductEntityMapper.toDomain(productEntity(true, true));

        ProductEntity entity = ProductEntityMapper.toEntity(product);

        assertEquals(product.getId(), entity.getId());
        assertEquals("Lenovo", entity.getMark().getName());
        assertTrue(entity.getMark().getActive());
        assertEquals(2, entity.getCategories().size());
        assertEquals("Computacion", entity.getCategories().get(0).getName());
        assertFalse(entity.getCategories().get(1).getActive());
        assertEquals("Notebook Lenovo", entity.getName());
        assertEquals("Notebook Lenovo IdeaPad 310", entity.getDescription());
        assertEquals(12, entity.getStock());
        assertEquals(1500.0, entity.getWeight());
        assertEquals(650000.0, entity.getPriceCost());
        assertEquals(800000.0, entity.getPriceSale());
        assertEquals("https://images.example.com/products/notebook.png", entity.getImagePath());
    }

    @Test
    @DisplayName("toEntity: convierte una marca inactiva manteniendo el estado")
    void toEntityKeepsInactiveMark() {
        Product product = ProductEntityMapper.toDomain(productEntity(false, false));

        ProductEntity entity = ProductEntityMapper.toEntity(product);

        assertFalse(entity.getMark().getActive());
    }
}
