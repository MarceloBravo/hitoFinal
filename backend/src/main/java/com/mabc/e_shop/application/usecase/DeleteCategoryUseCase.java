package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;

/**
 * Caso de uso que elimina una categoría por su identificador.
 *
 * <p>Rechaza la eliminación si la categoría está asociada a algún producto.
 */
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Crea el caso de uso con los repositorios de categorías y productos.
     *
     * @param categoryRepository repositorio de categorías.
     * @param productRepository  repositorio de productos.
     */
    public DeleteCategoryUseCase(
        CategoryRepository categoryRepository,
        ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Elimina la categoría correspondiente al identificador entregado.
     *
     * @param id identificador de la categoría a eliminar.
     * @return {@code true} si la categoría fue eliminada.
     * @throws IllegalArgumentException si el identificador es {@code null}.
     * @throws ResourceNotFoundException si la categoría no existe.
     * @throws IllegalStateException    si la categoría está en uso por algún producto.
     */
    public boolean execute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la categoría a eliminar es obligatorio.");
        }
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe."));

        if (productRepository.existsProductWithCategory(id)) {
            throw new IllegalStateException("La categoría no se puede eliminar porque está asociada a productos.");
        }

        categoryRepository.deleteById(id);
        return true;
    }
}
