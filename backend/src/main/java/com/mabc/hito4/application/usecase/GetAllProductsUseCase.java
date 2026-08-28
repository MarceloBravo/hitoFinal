package com.mabc.hitoFinal.application.usecase;

import com.mabc.hitoFinal.domain.entity.Product;
import com.mabc.hitoFinal.domain.repository.ProductRepository;

import java.util.List;

/**
 * Caso de uso que consulta todos los productos registrados.
 */
public class GetAllProductsUseCase {

    private final ProductRepository productRepository;

    /**
     * Crea el caso de uso con el repositorio de productos.
     *
     * @param productRepository repositorio de productos.
     */
    public GetAllProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Obtiene todos los productos registrados.
     *
     * @return lista con todos los productos; vacía si no hay registros.
     */
    public List<Product> execute() {
        return productRepository.findAll();
    }
}
