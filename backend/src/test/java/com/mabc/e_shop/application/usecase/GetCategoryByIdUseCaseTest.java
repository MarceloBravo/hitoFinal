package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import com.mabc.e_shop.domain.valueobject.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetCategoryByIdUseCaseTest {

    private CategoryRepository categoryRepository;
    private GetCategoryByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        useCase = new GetCategoryByIdUseCase(categoryRepository);
    }

    @Test
    @DisplayName("Retorna la categoría correspondiente al identificador")
    void returnsCategoryById() {
        Category category = new Category(1L, new Name("Gaming"));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertEquals(category, useCase.execute(1L));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando la categoría no existe")
    void rejectsWhenCategoryNotExists() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
    }
}
