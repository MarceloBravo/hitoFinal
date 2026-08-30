package com.mabc.e_shop.infrastructure.http.mapper;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;
import com.mabc.e_shop.infrastructure.http.dto.ProductResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductHttpMapperTest {

    @Test
    @DisplayName("Convierte un producto con marca y categorías a su DTO de respuesta")
    void mapsProduct() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        List<Category> categories = List.of(
                new Category(2L, new Name("Gaming")),
                new Category(3L, new Name("Oficina")));
        Product product = new Product(
                5L, mark, categories, new Name("Notebook"), new Description("Equipo portátil"),
                new Stock(10), new Weight(2.5), new Price(500.0), new Price(700.0),
                new ImagePath("https://images.example.com/products/notebook.png"));

        ProductResponseDto response = ProductHttpMapper.toResponse(product);

        assertEquals(5L, response.id());
        assertEquals(1L, response.markId());
        assertEquals("Lenovo", response.markName());
        assertEquals(List.of(2L, 3L), response.categoryIds());
        assertEquals("Notebook", response.name());
        assertEquals("Equipo portátil", response.description());
        assertEquals(10, response.stock());
        assertEquals(2.5, response.weight());
        assertEquals(500.0, response.priceCost());
        assertEquals(700.0, response.priceSale());
        assertEquals("https://images.example.com/products/notebook.png", response.imagePath());
    }

    @Test
    @DisplayName("Mapea un producto sin categorías como lista vacía")
    void mapsProductWithoutCategories() {
        Product product = new Product(
                6L, new Mark(1L, new Name("Lenovo")), null, new Name("Mouse"), new Description("Periférico"),
                new Stock(5), new Weight(0.2), new Price(10.0), new Price(15.0),
                new ImagePath("https://images.example.com/products/mouse.png"));

        assertTrue(ProductHttpMapper.toResponse(product).categoryIds().isEmpty());
    }

    @Test
    @DisplayName("Mapea un producto sin imagen con imagePath nulo")
    void mapsProductWithoutImage() {
        Product product = new Product(
                7L, new Mark(1L, new Name("Lenovo")), null, new Name("Teclado"), new Description("Periférico"),
                new Stock(5), new Weight(0.8), new Price(20.0), new Price(30.0), null);

        assertNull(ProductHttpMapper.toResponse(product).imagePath());
    }
}
