package com.mabc.hito5.application.usecase;

import com.mabc.hito5.domain.entity.Category;
import com.mabc.hito5.domain.repository.CategoryRepository;

import java.util.List;

/**
 * Caso de uso que consulta todas las categorías registradas.
 */
public class GetAllCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    /**
     * Crea el caso de uso con el repositorio de categorías.
     *
     * @param categoryRepository repositorio de categorías.
     */
    public GetAllCategoriesUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Obtiene todas las categorías registradas.
     *
     * @return lista con todas las categorías; vacía si no hay registros.
     */
    public List<Category> execute() {
        return categoryRepository.findAll();
    }
}
