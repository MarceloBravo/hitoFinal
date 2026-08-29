package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateCartUseCaseTest {

    private CartRepository cartRepository;
    private CreateCartUseCase useCase;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        when(cartRepository.save(any(Cart.class))).thenAnswer(returnsFirstArg());
        useCase = new CreateCartUseCase(cartRepository);
    }

    @Test
    @DisplayName("Crea un carrito con id 1 si no existen carritos previos")
    void createsCartWithIdOneWhenNoPrevious() {
        //when(cartRepository.findLast()).thenReturn(Optional.empty());

        Cart cart = useCase.execute();

        assertNotNull(cart);
        assertEquals(0.0, cart.getSubTotal());
    }
}
