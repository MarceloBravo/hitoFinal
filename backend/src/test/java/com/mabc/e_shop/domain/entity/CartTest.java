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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartTest {

    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        product = new Product(1L, mark, List.of(),
                new Name("Notebook Lenovo"), new Description("Notebook Lenovo IdeaPad 310"),
                new Stock(12), new Weight(1500), new Price(650000), new Price(800000),
                new ImagePath("https://images.example.com/products/notebook.png"));
        cart = new Cart(1L);
    }

    @Test
    @DisplayName("Carrito nuevo: inicia vacio con subtotal en cero")
    void newCartStartsWithZeroSubTotal() {
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getSubTotal());
        assertNotNull(cart.getCreationDate());
    }

    @Test
    @DisplayName("addItem: agrega un item y recalcula el subtotal")
    void addItemAddsAndRecalculatesSubTotal() {
        cart.addItem(product, new Quantity(2));

        assertEquals(1, cart.getItems().size());
        assertEquals(1600000, cart.getSubTotal());
    }

    @Test
    @DisplayName("addItem: lanza excepcion si no hay stock suficiente")
    void addItemRejectsInsufficientStock() {
        assertThrows(IllegalStateException.class, () -> cart.addItem(product, new Quantity(99)));
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    @DisplayName("addItem: si el producto ya existe suma la cantidad sin duplicar el item")
    void addItemSumsQuantityWhenProductAlreadyExists() {
        cart.addItem(product, new Quantity(2));
        CartItem updated = cart.addItem(product, new Quantity(3));

        assertEquals(1, cart.getItems().size());
        assertEquals(5, updated.getQuantity().value());
        assertEquals(5, cart.getItems().get(0).getQuantity().value());
        assertEquals(4000000, cart.getSubTotal());
    }

    @Test
    @DisplayName("addItem: valida el stock contra la cantidad acumulada")
    void addItemValidatesStockAgainstAccumulatedQuantity() {
        cart.addItem(product, new Quantity(10));

        assertThrows(IllegalStateException.class, () -> cart.addItem(product, new Quantity(5)));
        assertEquals(1, cart.getItems().size());
        assertEquals(10, cart.getItems().get(0).getQuantity().value());
    }

    @Test
    @DisplayName("addItem: rechaza producto nulo")
    void addItemRejectsNullProduct() {
        assertThrows(NullPointerException.class, () -> cart.addItem(null, new Quantity(1)));
    }

    @Test
    @DisplayName("addItem: rechaza cantidad nula")
    void addItemRejectsNullQuantity() {
        assertThrows(NullPointerException.class, () -> cart.addItem(product, null));
    }

    @Test
    @DisplayName("addItem: acumula el subtotal con varios items")
    void subTotalAccumulatesAcrossItems() {
        Product other = new Product(2L, new Mark(1L, new Name("Lenovo")), List.of(),
                new Name("Mouse"), new Description("Mouse inalambrico"),
                new Stock(20), new Weight(100), new Price(5000), new Price(10000),
                new ImagePath("https://images.example.com/products/mouse.png"));

        cart.addItem(product, new Quantity(2));
        cart.addItem(other, new Quantity(3));

        assertEquals(2, cart.getItems().size());
        assertEquals(1630000, cart.getSubTotal());
    }

    @Test
    @DisplayName("calculateSubTotal: recalcula el subtotal desde los items")
    void calculateSubTotalRecomputes() {
        cart.addItem(product, new Quantity(2));
        assertEquals(1600000, cart.getSubTotal());

        cart.calculateSubTotal();
        assertEquals(1600000, cart.getSubTotal());
    }

    @Test
    @DisplayName("getItems: devuelve una lista no modificable")
    void itemsAreUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> cart.getItems().add(null));
    }

    @Test
    @DisplayName("removeItemById: elimina el item y recalcula el subtotal")
    void removeItemByIdRemovesAndRecalculatesSubTotal() {
        Product other = new Product(2L, new Mark(1L, new Name("Lenovo")), List.of(),
                new Name("Mouse"), new Description("Mouse inalambrico"),
                new Stock(20), new Weight(100), new Price(5000), new Price(10000),
                new ImagePath("https://images.example.com/products/mouse.png"));

        cart.addItemWithId(10L, product, new Quantity(2));
        cart.addItemWithId(11L, other, new Quantity(3));

        cart.removeItemById(10L);

        assertEquals(1, cart.getItems().size());
        assertEquals(11L, cart.getItems().get(0).getId());
        assertEquals(30000, cart.getSubTotal());
    }

    @Test
    @DisplayName("removeItemById: lanza excepcion si el item no existe")
    void removeItemByIdRejectsMissingItem() {
        cart.addItemWithId(10L, product, new Quantity(2));

        assertThrows(IllegalArgumentException.class, () -> cart.removeItemById(99L));
        assertEquals(1, cart.getItems().size());
    }

    @Test
    @DisplayName("removeItemById: rechaza id nulo")
    void removeItemByIdRejectsNullId() {
        assertThrows(NullPointerException.class, () -> cart.removeItemById(null));
    }

    @Test
    @DisplayName("decrementItemQuantity: disminuye en una unidad y recalcula el subtotal")
    void decrementItemQuantityDecreasesByOne() {
        cart.addItemWithId(10L, product, new Quantity(3));

        cart.decrementItemQuantity(10L);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity().value());
        assertEquals(1600000, cart.getSubTotal());
    }

    @Test
    @DisplayName("decrementItemQuantity: elimina el item si la cantidad llegaba a uno")
    void decrementItemQuantityRemovesLastUnit() {
        cart.addItemWithId(10L, product, new Quantity(1));

        cart.decrementItemQuantity(10L);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getSubTotal());
    }

    @Test
    @DisplayName("decrementItemQuantity: lanza excepcion si el item no existe")
    void decrementItemQuantityRejectsMissingItem() {
        cart.addItemWithId(10L, product, new Quantity(2));

        assertThrows(IllegalArgumentException.class, () -> cart.decrementItemQuantity(99L));
        assertEquals(1, cart.getItems().size());
    }

    @Test
    @DisplayName("decrementItemQuantity: rechaza id nulo")
    void decrementItemQuantityRejectsNullId() {
        assertThrows(NullPointerException.class, () -> cart.decrementItemQuantity(null));
    }
}
