package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;

/**
 * Caso de uso que elimina una marca por su identificador.
 *
 * <p>Rechaza la eliminación si la marca está asociada a algún producto.
 */
public class DeleteMarkUseCase {

    private final MarkRepository markRepository;
    private final ProductRepository productRepository;

    /**
     * Crea el caso de uso con los repositorios de marcas y productos.
     *
     * @param markRepository     repositorio de marcas.
     * @param productRepository  repositorio de productos.
     */
    public DeleteMarkUseCase(
        MarkRepository markRepository,
        ProductRepository productRepository
    ) {
        this.markRepository = markRepository;
        this.productRepository = productRepository;
    }

    /**
     * Elimina la marca correspondiente al identificador entregado.
     *
     * @param id identificador de la marca a eliminar.
     * @return {@code true} si la marca fue eliminada.
     * @throws IllegalArgumentException  si el identificador es {@code null}.
     * @throws ResourceNotFoundException si la marca no existe.
     * @throws IllegalStateException     si la marca está en uso por algún producto.
     */
    public boolean execute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la marca a eliminar es obligatorio.");
        }
        markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La marca no existe."));

        if (productRepository.existsProductWithMark(id)) {
            throw new IllegalStateException("La marca no se puede eliminar porque está asociada a productos.");
        }

        markRepository.deleteById(id);
        return true;
    }
}
