package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import com.mabc.e_shop.domain.valueobject.Name;

/**
 * Caso de uso que crea o actualiza una categoría.
 *
 * <p>Si se entrega un {@code id} nulo se crea una categoría nueva; en caso
 * contrario se actualiza la categoría existente. También permite activarla o
 * desactivarla según el estado recibido.
 */
public class SaveCategoryUseCase {

    private final CategoryRepository categoryRepository;

    /**
     * Crea el caso de uso con el repositorio de categorías.
     *
     * @param categoryRepository repositorio de categorías.
     */
    public SaveCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea o actualiza una categoría y la persiste.
     *
     * @param id     identificador de la categoría; si es {@code null} se crea una nueva.
     * @param name   nombre de la categoría.
     * @param active {@code true} para activar la categoría, {@code false} para desactivarla.
     * @return la categoría creada o actualizada.
     * @throws IllegalArgumentException si se entrega un {@code id} y la categoría no existe.
     */
    public Category execute(Long id, String name, boolean active) {
        Name categoryName = new Name(name);

        Category category;
        if (id == null) {
            category = new Category(null, categoryName);
        } else {
            category = categoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("La categoría no existe."));
            category.rename(categoryName);
        }

        if (active) {
            category.activate();
        } else {
            category.deactivate();
        }

        return categoryRepository.save(category);
    }
}
