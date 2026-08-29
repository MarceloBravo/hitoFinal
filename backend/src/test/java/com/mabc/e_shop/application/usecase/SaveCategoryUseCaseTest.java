package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.exception.InvalidNameException;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SaveCategoryUseCaseTest {

    private List<Category> storedCategories;
    private SaveCategoryUseCase useCase;

    @BeforeEach
    void setUp() {
        storedCategories = new ArrayList<>();
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        when(categoryRepository.findAll()).thenAnswer(invocation -> List.copyOf(storedCategories));
        when(categoryRepository.findById(any())).thenAnswer(invocation -> storedCategories.stream()
                .filter(category -> category.getId().equals(invocation.getArgument(0)))
                .findFirst());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            if(category.getId() == null){
                category = new Category(1L, category.getName());
            }
            storedCategories.add(category);
            return category;
        });
        useCase = new SaveCategoryUseCase(categoryRepository);
    }

    @Test
    @DisplayName("Crea una categoria nueva activa")
    void createsCategory() {
        Category category = useCase.execute(null, "Computacion", true);

        assertEquals("Computacion", category.getName().value());
        assertTrue(category.isActive());
    }

    @Test
    @DisplayName("Actualiza una categoria existente")
    void updatesCategory() {
        Category created = useCase.execute(null, "Computacion", true);

        Category updated = useCase.execute(created.getId(), "Gaming", false);

        assertEquals(created.getId(), updated.getId());
        assertEquals("Gaming", updated.getName().value());
        assertFalse(updated.isActive());
    }

    @Test
    @DisplayName("Rechaza nombre nulo o vacio")
    void rejectsBlankName() {
        assertThrows(InvalidNameException.class, () -> useCase.execute(null, "   ", true));
    }

    @Test
    @DisplayName("Lanza excepcion si se actualiza una categoria inexistente")
    void rejectsUpdatingMissingCategory() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L, "Gaming", true));
    }

    @Test
    @DisplayName("Activa y desactiva la categoria segun el estado recibido")
    void togglesActiveState() {
        Category created = useCase.execute(null, "Computacion", false);
        assertTrue(created.isActive());

        Category reactivated = useCase.execute(created.getId(), "Computacion", false);
        assertFalse(reactivated.isActive());
    }
}
