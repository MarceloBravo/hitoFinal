package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.repository.ProductRepository.PageResult;

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

    /**
     * Obtiene una página de productos.
     *
     * @param page número de página (base 0).
     * @param size cantidad de elementos por página.
     * @return el resultado paginado con el contenido y el total de registros.
     */
    public PageResult execute(int page, int size) {
        return productRepository.findAll(page, size);
    }

    /**
     * Obtiene una página de productos, opcionalmente filtrados por categoría,
     * marca y rango de precio de venta. Los filtros se combinan con AND y los
     * parámetros {@code null} se ignoran.
     *
     * @param page       número de página (base 0).
     * @param size       cantidad de elementos por página.
     * @param categoryId identificador de la categoría para filtrar, o {@code null}.
     * @param markId     identificador de la marca para filtrar, o {@code null}.
     * @param minPrice   precio de venta mínimo, o {@code null}.
     * @param maxPrice   precio de venta máximo, o {@code null}.
     * @return el resultado paginado con el contenido y el total de registros.
     */
    public PageResult execute(int page, int size, Long categoryId, Long markId, Double minPrice, Double maxPrice) {
        return productRepository.findAll(page, size, categoryId, markId, minPrice, maxPrice);
    }

    /**
     * Obtiene una página de productos, opcionalmente filtrados por categoría,
     * marca, rango de precio de venta y un término de búsqueda de texto. Los
     * filtros se combinan con AND y los parámetros {@code null} se ignoran.
     *
     * @param page       número de página (base 0).
     * @param size       cantidad de elementos por página.
     * @param categoryId identificador de la categoría para filtrar, o {@code null}.
     * @param markId     identificador de la marca para filtrar, o {@code null}.
     * @param minPrice   precio de venta mínimo, o {@code null}.
     * @param maxPrice   precio de venta máximo, o {@code null}.
     * @param search     término de búsqueda de texto, o {@code null}.
     * @return el resultado paginado con el contenido y el total de registros.
     */
    public PageResult execute(
            int page, int size, Long categoryId, Long markId, Double minPrice, Double maxPrice, String search) {
        return productRepository.findAll(page, size, categoryId, markId, minPrice, maxPrice, search);
    }
}
