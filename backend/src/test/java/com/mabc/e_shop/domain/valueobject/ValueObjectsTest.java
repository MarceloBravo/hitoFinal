package com.mabc.e_shop.domain.valueobject;

import com.mabc.e_shop.domain.exception.InvalidDescriptionException;
import com.mabc.e_shop.domain.exception.InvalidImageException;
import com.mabc.e_shop.domain.exception.InvalidNameException;
import com.mabc.e_shop.domain.exception.InvalidPriceException;
import com.mabc.e_shop.domain.exception.InvalidQuantityException;
import com.mabc.e_shop.domain.exception.InvalidStockException;
import com.mabc.e_shop.domain.exception.InvalidWeightException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValueObjectsTest {

    @Test
    @DisplayName("Name: acepta un valor valido y lo normaliza")
    void nameAcceptsValidValue() {
        assertEquals("Lenovo", new Name(" Lenovo ").value());
    }

    @Test
    @DisplayName("Name: rechaza valores nulos o vacios")
    void nameRejectsNullOrBlank() {
        assertThrows(InvalidNameException.class, () -> new Name(null));
        assertThrows(InvalidNameException.class, () -> new Name("   "));
    }

    @Test
    @DisplayName("Description: acepta un valor valido")
    void descriptionAcceptsValidValue() {
        assertEquals("Notebook", new Description(" Notebook ").value());
    }

    @Test
    @DisplayName("Description: rechaza valores nulos o vacios")
    void descriptionRejectsNullOrBlank() {
        assertThrows(InvalidDescriptionException.class, () -> new Description(null));
        assertThrows(InvalidDescriptionException.class, () -> new Description(""));
    }

    @Test
    @DisplayName("Price: rechaza precios negativos")
    void priceRejectsNegative() {
        assertThrows(InvalidPriceException.class, () -> new Price(-1));
        assertEquals(0, new Price(0).value());
    }

    @Test
    @DisplayName("Stock: rechaza stock negativo")
    void stockRejectsNegative() {
        assertThrows(InvalidStockException.class, () -> new Stock(-1));
        assertEquals(0, new Stock(0).value());
    }

    @Test
    @DisplayName("Weight: rechaza pesos menores o iguales a cero")
    void weightRejectsZeroOrLess() {
        assertThrows(InvalidWeightException.class, () -> new Weight(0));
        assertThrows(InvalidWeightException.class, () -> new Weight(-5));
        assertEquals(1.5, new Weight(1.5).value());
    }

    @Test
    @DisplayName("ImagePath: acepta una URL o ruta absoluta valida")
    void imagePathAcceptsValidValue() {
        assertEquals("https://images.example.com/products/notebook.png",
                new ImagePath("https://images.example.com/products/notebook.png").value());
        assertEquals("file:///uploads/foto.png", new ImagePath("file:///uploads/foto.png").value());
        assertEquals("/uploads/foto.png", new ImagePath("/uploads/foto.png").value());
    }

    @Test
    @DisplayName("ImagePath: acepta rutas de archivo locales absolutas")
    void imagePathAcceptsAbsoluteLocalPath() {
        String absolute = java.nio.file.Path.of("uploads").toAbsolutePath().resolve("foto.png").toString();

        assertTrue(java.nio.file.Path.of(absolute).isAbsolute());
        assertEquals(absolute, new ImagePath(absolute).value());
    }

    @Test
    @DisplayName("ImagePath: acepta una ruta nula (producto sin imagen)")
    void imagePathAcceptsNull() {
        assertNull(new ImagePath(null).value());
        assertEquals(new ImagePath(null), new ImagePath(null));
    }

    @Test
    @DisplayName("ImagePath: rechaza valores vacios o rutas relativas")
    void imagePathRejectsNullOrRelative() {
        assertThrows(InvalidImageException.class, () -> new ImagePath("   "));
        assertThrows(InvalidImageException.class, () -> new ImagePath("uploads/foto.png"));
    }

    @Test
    @DisplayName("Quantity: rechaza cantidades menores o iguales a cero")
    void quantityRejectsZeroOrLess() {
        assertThrows(InvalidQuantityException.class, () -> new Quantity(0));
        assertThrows(InvalidQuantityException.class, () -> new Quantity(-2));
        assertEquals(3, new Quantity(3).value());
    }

    @Test
    @DisplayName("Name: equals y hashCode se basan en el valor normalizado")
    void nameEqualityUsesValue() {
        Name a = new Name("Lenovo");
        Name b = new Name("  Lenovo  ");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.toString().contains("Lenovo"));
    }

    @Test
    @DisplayName("Name: valores distintos no son iguales")
    void nameInequality() {
        assertNotEquals(new Name("Lenovo"), new Name("Asus"));
    }

    @Test
    @DisplayName("Description: equals y hashCode se basan en el valor")
    void descriptionEqualityUsesValue() {
        assertEquals(new Description("Notebook"), new Description(" Notebook "));
        assertEquals(new Description("Notebook").hashCode(), new Description(" Notebook ").hashCode());
    }

    @Test
    @DisplayName("ImagePath: equals y hashCode se basan en el valor")
    void imagePathEqualityUsesValue() {
        assertEquals(new ImagePath("https://images.example.com/products/notebook.png"),
                new ImagePath("https://images.example.com/products/notebook.png"));
        assertNotEquals(new ImagePath("https://images.example.com/products/notebook.png"),
                new ImagePath("https://images.example.com/products/mouse.png"));
        assertEquals(new ImagePath("https://images.example.com/products/notebook.png").hashCode(),
                new ImagePath("https://images.example.com/products/notebook.png").hashCode());
    }

    @Test
    @DisplayName("Price: equals y hashCode se basan en el valor")
    void priceEqualityUsesValue() {
        assertEquals(new Price(100), new Price(100));
        assertNotEquals(new Price(100), new Price(200));
        assertEquals(new Price(100).hashCode(), new Price(100).hashCode());
    }

    @Test
    @DisplayName("Quantity: equals y hashCode se basan en el valor")
    void quantityEqualityUsesValue() {
        assertEquals(new Quantity(5), new Quantity(5));
        assertNotEquals(new Quantity(5), new Quantity(6));
        assertEquals(new Quantity(5).hashCode(), new Quantity(5).hashCode());
    }

    @Test
    @DisplayName("Stock: equals y hashCode se basan en el valor")
    void stockEqualityUsesValue() {
        assertEquals(new Stock(10), new Stock(10));
        assertNotEquals(new Stock(10), new Stock(11));
        assertEquals(new Stock(10).hashCode(), new Stock(10).hashCode());
    }

    @Test
    @DisplayName("Weight: equals y hashCode se basan en el valor")
    void weightEqualityUsesValue() {
        assertEquals(new Weight(1.5), new Weight(1.5));
        assertNotEquals(new Weight(1.5), new Weight(2.5));
        assertEquals(new Weight(1.5).hashCode(), new Weight(1.5).hashCode());
    }

    @Test
    @DisplayName("toString: todos los records incluyen el valor")
    void toStringIncludesValue() {
        assertTrue(new Name("Lenovo").toString().contains("Lenovo"));
        assertTrue(new Description("Notebook").toString().contains("Notebook"));
        assertTrue(new Price(100).toString().contains("100.0"));
        assertTrue(new Quantity(3).toString().contains("3"));
        assertTrue(new Stock(8).toString().contains("8"));
        assertTrue(new Weight(1.5).toString().contains("1.5"));
        assertTrue(new ImagePath("https://images.example.com/products/notebook.png")
                .toString().contains("notebook"));
    }
}
