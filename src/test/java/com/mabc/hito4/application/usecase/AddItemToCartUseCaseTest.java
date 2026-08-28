package com.mabc.hito5.application.usecase;

import com.mabc.hito5.domain.entity.Cart;
import com.mabc.hito5.domain.entity.Mark;
import com.mabc.hito5.domain.entity.Product;
import com.mabc.hito5.domain.exception.ResourceNotFoundException;
import com.mabc.hito5.domain.repository.CartRepository;
import com.mabc.hito5.domain.repository.ProductRepository;
import com.mabc.hito5.domain.valueobject.Description;
import com.mabc.hito5.domain.valueobject.Name;
import com.mabc.hito5.domain.valueobject.Price;
import com.mabc.hito5.domain.valueobject.Stock;
import com.mabc.hito5.domain.valueobject.Weight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddItemToCartUseCaseTest {

    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private AddItemToCartUseCase useCase;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        productRepository = mock(ProductRepository.class);
        useCase = new AddItemToCartUseCase(cartRepository, productRepository);

        Mark mark = new Mark(1L, new Name("Lenovo"));
        product = new Product(1L, mark, List.of(),
                new Name("Notebook Lenovo"), new Description("Notebook Lenovo IdeaPad 310"),
                new Stock(12), new Weight(1500), new Price(650000), new Price(800000));

        cart = new Cart(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(returnsFirstArg());
    }

    @Test
    @DisplayName("Agrega un producto al carrito y recalcula el subtotal")
    void addsProductToCart() {
        Cart result = useCase.execute(1L, 1L, 2);

        assertEquals(1, result.getItems().size());
        assertEquals(1600000, result.getSubTotal());
    }

    @Test
    @DisplayName("Lanza excepcion si el carrito no existe")
    void rejectsWhenCartNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(999L, 1L, 1));
    }

    @Test
    @DisplayName("Lanza excepcion si el producto no existe")
    void rejectsWhenProductNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(1L, 999L, 1));
    }

    @Test
    @DisplayName("Lanza excepcion si no hay stock suficiente y no guarda cambios")
    void rejectsWhenStockInsufficient() {
        assertThrows(IllegalStateException.class, () -> useCase.execute(1L, 1L, 50));
        verify(cartRepository, never()).save(any(Cart.class));
    }
}
