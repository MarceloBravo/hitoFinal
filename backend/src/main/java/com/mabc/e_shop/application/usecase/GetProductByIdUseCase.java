package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.ProductRepository;

/**
 * Caso de uso que consulta un producto por su identificador.
 */
public class GetProductByIdUseCase {

    private final ProductRepository productRepository;

    /**
     * Crea el caso de uso con el repositorio de productos.
     *
     * @param productRepository repositorio de productos.
     */
    public GetProductByIdUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Busca el producto correspondiente al identificador entregado.
     *
     * @param id identificador del producto.
     * @return el producto encontrado.
     * @throws ResourceNotFoundException si el producto no existe.
     */
    public Product execute(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no existe."));
    }
}
