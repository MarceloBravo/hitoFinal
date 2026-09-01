package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CheckoutCartUseCaseTest {

    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private CheckoutCartUseCase useCase;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        productRepository = mock(ProductRepository.class);
        useCase = new CheckoutCartUseCase(cartRepository, productRepository);
    }

    private Product buildProduct(Long id, int stock) {
        return new Product(id, new Mark(1L, new Name("Lenovo")), List.of(new Category(2L, new Name("Gaming"))),
                new Name("Notebook " + id), new Description("Equipo portátil"),
                new Stock(stock), new Weight(2.5), new Price(500.0), new Price(50.0),
                new ImagePath("https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("Rebaja el stock de cada producto y elimina el carrito")
    void checkOutDecrementsStockAndDeletesCart() {
        Product p1 = buildProduct(3L, 10);
        Product p2 = buildProduct(4L, 5);

        Cart cart = new Cart(7L);
        cart.addItemWithId(10L, p1, new Quantity(2));
        cart.addItemWithId(11L, p2, new Quantity(1));

        when(cartRepository.findById(7L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(3L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(4L)).thenReturn(Optional.of(p2));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckoutCartUseCase.CheckoutResult result = useCase.execute(7L);

        assertEquals(7L, result.cartId());
        assertEquals(150.0, result.total());
        assertEquals(3, result.itemCount());
        assertEquals(List.of(3L, 4L), result.products());
        assertEquals(8, p1.getStock().value());
        assertEquals(4, p2.getStock().value());
        verify(productRepository).save(p1);
        verify(productRepository).save(p2);
        verify(cartRepository).deleteById(7L);
    }

    @Test
    @DisplayName("Lanza excepcion si el carrito no existe")
    void rejectsWhenCartNotExists() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
        verify(productRepository, never()).save(any(Product.class));
        verify(cartRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("Lanza excepcion si un producto no existe y no descuenta otros")
    void rejectsWhenProductNotExists() {
        Product p1 = buildProduct(3L, 10);
        Cart cart = new Cart(7L);
        cart.addItemWithId(10L, p1, new Quantity(2));

        when(cartRepository.findById(7L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(7L));
        verify(productRepository, never()).save(any(Product.class));
        verify(cartRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("Lanza excepcion si no hay stock suficiente y no descuenta nada")
    void rejectsWhenStockInsufficient() {
        Product p1 = buildProduct(3L, 2);
        Cart cart = new Cart(7L);
        cart.addItemWithId(10L, p1, new Quantity(5));

        when(cartRepository.findById(7L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(3L)).thenReturn(Optional.of(p1));

        assertThrows(IllegalStateException.class, () -> useCase.execute(7L));
        verify(productRepository, never()).save(any(Product.class));
        verify(cartRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el id del carrito es nulo")
    void rejectsNullCartId() {
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(null));
    }
}
