package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CategoryRepository;

/**
 * Caso de uso que consulta una categoría por su identificador.
 */
public class GetCategoryByIdUseCase {

    private final CategoryRepository categoryRepository;

    /**
     * Crea el caso de uso con el repositorio de categorías.
     *
     * @param categoryRepository repositorio de categorías.
     */
    public GetCategoryByIdUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Busca la categoría correspondiente al identificador entregado.
     *
     * @param id identificador de la categoría.
     * @return la categoría encontrada.
     * @throws ResourceNotFoundException si la categoría no existe.
     */
    public Category execute(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe."));
    }
}
