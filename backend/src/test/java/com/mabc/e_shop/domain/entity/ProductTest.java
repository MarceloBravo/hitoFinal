package com.mabc.e_shop.domain.entity;

import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Quantity;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        Category category = new Category(1L, new Name("Computacion"));
        product = new Product(1L, mark, List.of(category),
                new Name("Notebook Lenovo"), new Description("Notebook Lenovo IdeaPad 310"),
                new Stock(12), new Weight(1500), new Price(650000), new Price(800000),
                new ImagePath("https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("Identidad: el id permanece inmutable aunque cambien los atributos")
    void identityIsImmutable() {
        product.rename(new Name("Notebook Lenovo 2"));
        product.restock(new Stock(5));

        assertEquals(1L, product.getId());
        assertEquals("Notebook Lenovo 2", product.getName().value());
        assertEquals(5, product.getStock().value());
    }

    @Test
    @DisplayName("hasStock: true si hay stock suficiente, false si no")
    void hasStockEvaluatesAvailability() {
        assertTrue(product.hasStock(new Quantity(5)));
        assertFalse(product.hasStock(new Quantity(13)));
    }

    @Test
    @DisplayName("reduceStock: descuenta stock disponible")
    void reduceStockDecrements() {
        product.reduceStock(new Quantity(3));
        assertEquals(9, product.getStock().value());
    }

    @Test
    @DisplayName("reduceStock: lanza excepcion si no hay stock suficiente")
    void reduceStockRejectsExcess() {
        assertThrows(IllegalStateException.class, () -> product.reduceStock(new Quantity(50)));
    }

    @Test
    @DisplayName("updatePrices: actualiza precios de costo y venta")
    void updatePricesUpdatesValues() {
        product.updatePrices(new Price(600000), new Price(900000));

        assertEquals(600000, product.getPriceCost().value());
        assertEquals(900000, product.getPriceSale().value());
    }

    @Test
    @DisplayName("Getters: recuperan marca, peso y precio de costo")
    void gettersReturnValues() {
        assertEquals("Lenovo", product.getMark().getName().value());
        assertEquals(1500, product.getWeight().value());
        assertEquals(650000, product.getPriceCost().value());
    }

    @Test
    @DisplayName("updateDescription: actualiza la descripcion")
    void updateDescriptionUpdatesValue() {
        product.updateDescription(new Description("Descripcion nueva"));

        assertEquals("Descripcion nueva", product.getDescription().value());
    }

    @Test
    @DisplayName("getImagePath: recupera la ruta de la imagen")
    void gettersReturnImagePath() {
        assertEquals("https://images.example.com/products/notebook.png", product.getImagePath().value());
    }

    @Test
    @DisplayName("updateImagePath: actualiza la ruta de la imagen")
    void updateImagePathUpdatesValue() {
        product.updateImagePath(new ImagePath("https://images.example.com/products/notebook-v2.png"));

        assertEquals("https://images.example.com/products/notebook-v2.png", product.getImagePath().value());
    }

    @Test
    @DisplayName("updateImagePath: acepta una ruta nula (producto sin imagen)")
    void updateImagePathAcceptsNull() {
        product.updateImagePath(null);

        assertNull(product.getImagePath());
    }

    @Test
    @DisplayName("Mutadores: rechazan argumentos nulos")
    void mutatorsRejectNullArguments() {
        assertThrows(NullPointerException.class, () -> product.rename(null));
        assertThrows(NullPointerException.class, () -> product.updateDescription(null));
        assertThrows(NullPointerException.class, () -> product.restock(null));
        assertThrows(NullPointerException.class, () -> product.updatePrices(null, new Price(1)));
        assertThrows(NullPointerException.class, () -> product.updatePrices(new Price(1), null));
        assertThrows(NullPointerException.class, () -> product.updateMark(null));
    }

    @Test
    @DisplayName("getCategories: devuelve una lista no modificable")
    void categoriesAreUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> product.getCategories().add(null));
    }

    @Test
    @DisplayName("updateMark: actualiza la marca del producto")
    void updateMarkUpdatesValue() {
        Mark newMark = new Mark(2L, new Name("HP"));

        product.updateMark(newMark);

        assertEquals("HP", product.getMark().getName().value());
        assertEquals(1L, product.getId());
    }

    @Test
    @DisplayName("updateCategories: actualiza las categorias del producto")
    void updateCategoriesUpdatesValue() {
        Category cat1 = new Category(2L, new Name("Electronica"));
        Category cat2 = new Category(3L, new Name("Oficina"));

        product.updateCategories(List.of(cat1, cat2));

        assertEquals(2, product.getCategories().size());
        assertEquals("Electronica", product.getCategories().get(0).getName().value());
        assertEquals("Oficina", product.getCategories().get(1).getName().value());
    }

    @Test
    @DisplayName("updateCategories: acepta una lista nula (producto sin categorias)")
    void updateCategoriesAcceptsNull() {
        product.updateCategories(null);

        assertTrue(product.getCategories().isEmpty());
    }

    @Test
    @DisplayName("updateCategories: no expone la lista interna (inmutabilidad)")
    void updateCategoriesDoesNotExposeInternalList() {
        Category cat = new Category(2L, new Name("Electronica"));
        List<Category> newCategories = new java.util.ArrayList<>(List.of(cat));

        product.updateCategories(newCategories);
        newCategories.clear();

        assertEquals(1, product.getCategories().size());
    }
}
