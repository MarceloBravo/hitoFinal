package com.mabc.hitoFinal.domain.repository;

import com.mabc.hitoFinal.domain.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de productos.
 *
 * <p>Define el contrato de persistencia para la entidad {@link Product},
 * permitiendo consultar, guardar, eliminar y verificar la existencia de
 * productos.
 */
public interface ProductRepository {

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto.
     * @return un {@link Optional} con el producto encontrado o vacío si no existe.
     */
    Optional<Product> findById(Long id);

    /**
     * Obtiene todos los productos registrados.
     *
     * @return lista de todos los productos.
     */
    List<Product> findAll();

    /**
     * Guarda o actualiza un producto.
     *
     * @param product producto a persistir.
     * @return el producto persistido.
     */
    Product save(Product product);

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto a eliminar.
     */
    void deleteById(Long id);

    /**
     * Indica si existe un producto con el identificador entregado.
     *
     * @param id identificador del producto.
     * @return {@code true} si el producto existe, {@code false} en caso contrario.
     */
    boolean existsById(Long id);
}
