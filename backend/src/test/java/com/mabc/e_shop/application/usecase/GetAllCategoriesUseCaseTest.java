package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetAllCategoriesUseCaseTest {

    private CategoryRepository categoryRepository;
    private GetAllCategoriesUseCase useCase;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        useCase = new GetAllCategoriesUseCase(categoryRepository);
    }

    @Test
    @DisplayName("Retorna todas las categorías del repositorio")
    void returnsAllCategories() {
        List<Category> categories = List.of();
        when(categoryRepository.findAll()).thenReturn(categories);

        assertEquals(categories, useCase.execute());
        verify(categoryRepository).findAll();
    }
}
