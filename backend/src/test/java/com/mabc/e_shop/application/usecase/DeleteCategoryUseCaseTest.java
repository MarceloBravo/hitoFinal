package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CategoryRepository;
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

class DeleteCategoryUseCaseTest {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private DeleteCategoryUseCase useCase;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        productRepository = mock(ProductRepository.class);
        useCase = new DeleteCategoryUseCase(categoryRepository, productRepository);
    }

    @Test
    @DisplayName("Elimina una categoría existente y no usada, y responde true")
    void deletesExistingCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, new Name("Gaming"))));
        when(productRepository.existsProductWithCategory(1L)).thenReturn(false);

        assertTrue(useCase.execute(1L));
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Lanza IllegalArgumentException cuando el id es nulo")
    void rejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando la categoría no existe")
    void rejectsWhenCategoryNotExists() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
        verify(categoryRepository, never()).deleteById(99L);
    }

    @Test
    @DisplayName("Lanza IllegalStateException cuando la categoría está asociada a productos")
    void rejectsWhenCategoryInUse() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, new Name("Gaming"))));
        when(productRepository.existsProductWithCategory(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> useCase.execute(1L));
        verify(categoryRepository, never()).deleteById(1L);
    }
}
