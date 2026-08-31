package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteCartUseCaseTest {

    private CartRepository cartRepository;
    private DeleteCartUseCase useCase;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        useCase = new DeleteCartUseCase(cartRepository);
    }

    @Test
    @DisplayName("Elimina un carrito existente y responde true")
    void deletesExistingCart() {
        when(cartRepository.findById(7L)).thenReturn(Optional.of(new Cart(7L)));

        assertTrue(useCase.execute(7L));
        verify(cartRepository).deleteById(7L);
    }

    @Test
    @DisplayName("Lanza IllegalArgumentException cuando el id es nulo")
    void rejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el carrito no existe")
    void rejectsWhenCartNotExists() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
        verify(cartRepository, never()).deleteById(99L);
    }
}
