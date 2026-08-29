package com.mabc.e_shop.infrastructure.http.mapper;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Quantity;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;
import com.mabc.e_shop.infrastructure.http.dto.CartItemResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.CartResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartHttpMapperTest {

    private Product buildProduct() {
        return new Product(
                3L, new Mark(1L, new Name("Lenovo")), List.of(new Category(2L, new Name("Gaming"))),
                new Name("Notebook"), new Description("Equipo portátil"),
                new Stock(10), new Weight(2.5), new Price(500.0), new Price(50.0),
                new ImagePath("https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("Convierte un carrito vacío a su DTO de respuesta")
    void mapsEmptyCart() {
        Cart cart = new Cart(7L);

        CartResponseDto response = CartHttpMapper.toResponse(cart);

        assertEquals(7L, response.id());
        assertEquals(cart.getCreationDate(), response.creationDate());
        assertTrue(response.items().isEmpty());
        assertEquals(0.0, response.subTotal());
    }

    @Test
    @DisplayName("Convierte el ítem del carrito y recalcula el subtotal total")
    void mapsCartWithItems() {
        Cart cart = new Cart(7L);
        cart.addItem(buildProduct(), new Quantity(2));

        CartResponseDto response = CartHttpMapper.toResponse(cart);

        assertEquals(1, response.items().size());
        assertEquals(100.0, response.subTotal());

        CartItemResponseDto item = response.items().get(0);
        assertEquals(3L, item.productId());
        assertEquals("Notebook", item.productName());
        assertEquals(2, item.quantity());
        assertEquals(100.0, item.subTotal());
    }
}
