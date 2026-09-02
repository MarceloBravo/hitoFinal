package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.Name;
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

class DeleteMarkUseCaseTest {

    private MarkRepository markRepository;
    private ProductRepository productRepository;
    private DeleteMarkUseCase useCase;

    @BeforeEach
    void setUp() {
        markRepository = mock(MarkRepository.class);
        productRepository = mock(ProductRepository.class);
        useCase = new DeleteMarkUseCase(markRepository, productRepository);
    }

    @Test
    @DisplayName("Elimina una marca existente y no usada, y responde true")
    void deletesExistingMark() {
        when(markRepository.findById(1L)).thenReturn(Optional.of(new Mark(1L, new Name("Lenovo"))));
        when(productRepository.existsProductWithMark(1L)).thenReturn(false);

        assertTrue(useCase.execute(1L));
        verify(markRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Lanza IllegalArgumentException cuando el id es nulo")
    void rejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando la marca no existe")
    void rejectsWhenMarkNotExists() {
        when(markRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
        verify(markRepository, never()).deleteById(99L);
    }

    @Test
    @DisplayName("Lanza IllegalStateException cuando la marca está asociada a productos")
    void rejectsWhenMarkInUse() {
        when(markRepository.findById(1L)).thenReturn(Optional.of(new Mark(1L, new Name("Lenovo"))));
        when(productRepository.existsProductWithMark(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> useCase.execute(1L));
        verify(markRepository, never()).deleteById(1L);
    }
}