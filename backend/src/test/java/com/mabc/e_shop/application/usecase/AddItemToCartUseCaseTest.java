package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;
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
import static org.mockito.Mockito.times;
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
                new Stock(12), new Weight(1500), new Price(650000), new Price(800000),
                new ImagePath("https://images.example.com/products/notebook.png"));

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

    @Test
    @DisplayName("Agregar el mismo producto dos veces suma la cantidad en un unico item")
    void addsSameProductTwiceSumsQuantity() {
        useCase.execute(1L, 1L, 2);
        Cart result = useCase.execute(1L, 1L, 3);

        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity().value());
        assertEquals(4000000, result.getSubTotal());
        verify(cartRepository, times(2)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Rechaza si la cantidad acumulada supera el stock y no guarda cambios")
    void rejectsWhenAccumulatedQuantityExceedsStock() {
        useCase.execute(1L, 1L, 10);

        assertThrows(IllegalStateException.class, () -> useCase.execute(1L, 1L, 3));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}
