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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CartItemTest {

    private CartItem item;
    private Product product;

    @BeforeEach
    void setUp() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        product = new Product(1L, mark, List.of(),
                new Name("Notebook Lenovo"), new Description("Notebook Lenovo IdeaPad 310"),
                new Stock(12), new Weight(1500), new Price(650000), new Price(800000),
                new ImagePath("https://images.example.com/products/notebook.png"));
        item = new CartItem(1L, product, new Quantity(3));
    }

    @Test
    @DisplayName("Constructor: calcula el subtotal como precio de venta por cantidad")
    void constructorComputesSubTotal() {
        assertEquals(1L, item.getId());
        assertSame(product, item.getProduct());
        assertEquals(3, item.getQuantity().value());
        assertEquals(2400000, item.getSubTotal());
    }

    @Test
    @DisplayName("changeQuantity: actualiza la cantidad y recalcula el subtotal")
    void changeQuantityUpdatesSubTotal() {
        item.changeQuantity(new Quantity(2));

        assertEquals(2, item.getQuantity().value());
        assertEquals(1600000, item.getSubTotal());
    }

    @Test
    @DisplayName("calculateSubTotal: recalcula el subtotal a partir del precio de venta")
    void calculateSubTotalRecomputesValue() {
        item.changeQuantity(new Quantity(5));
        assertEquals(4000000, item.getSubTotal());

        item.changeQuantity(new Quantity(1));
        assertEquals(800000, item.getSubTotal());

        item.calculateSubTotal();
        assertEquals(800000, item.getSubTotal());
    }

    @Test
    @DisplayName("changeQuantity: rechaza cantidad nula")
    void changeQuantityRejectsNull() {
        assertThrows(NullPointerException.class, () -> item.changeQuantity(null));
    }

    @Test
    @DisplayName("Constructor: almacena id nulo para ítems nuevos no persistidos")
    void constructorAcceptsNullId() {
        CartItem newItem = new CartItem(null, product, new Quantity(1));
        assertNull(newItem.getId());
    }

    @Test
    @DisplayName("Constructor: rechaza producto nulo")
    void constructorRejectsNullProduct() {
        assertThrows(NullPointerException.class,
                () -> new CartItem(1L, null, new Quantity(1)));
    }

    @Test
    @DisplayName("Constructor: rechaza cantidad nula")
    void constructorRejectsNullQuantity() {
        assertThrows(NullPointerException.class, () -> new CartItem(1L, product, null));
    }
}
