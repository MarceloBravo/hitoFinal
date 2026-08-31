package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;
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

class RemoveItemFromCartUseCaseTest {

    private CartRepository cartRepository;
    private RemoveItemFromCartUseCase useCase;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        useCase = new RemoveItemFromCartUseCase(cartRepository);
    }

    private Product buildProduct(Long id) {
        return new Product(id, new Mark(1L, new Name("Lenovo")), List.of(new Category(2L, new Name("Gaming"))),
                new Name("Notebook"), new Description("Equipo portátil"),
                new Stock(10), new Weight(2.5), new Price(500.0), new Price(50.0),
                new ImagePath("https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("Elimina el item del carrito y persiste el carrito actualizado")
    void removesItemFromCart() {
        Cart cart = new Cart(7L);
        cart.addItemWithId(10L, buildProduct(3L), new Quantity(2));
        cart.addItemWithId(11L, buildProduct(4L), new Quantity(1));

        when(cartRepository.findById(7L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart result = useCase.execute(7L, 10L);

        assertEquals(1, result.getItems().size());
        assertEquals(11L, result.getItems().get(0).getId());
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el carrito no existe")
    void rejectsWhenCartNotExists() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L, 10L));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el item no existe")
    void rejectsWhenItemNotExists() {
        Cart cart = new Cart(7L);
        cart.addItemWithId(10L, buildProduct(3L), new Quantity(2));
        when(cartRepository.findById(7L)).thenReturn(Optional.of(cart));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(7L, 99L));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el id del carrito es nulo")
    void rejectsNullCartId() {
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(null, 10L));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el id del item es nulo")
    void rejectsNullItemId() {
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(7L, null));
    }
}
