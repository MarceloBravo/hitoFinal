package com.mabc.e_shop.domain.repository;

import com.mabc.e_shop.domain.entity.Product;

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
     * Resultado de una consulta paginada.
     *
     * @param content contenido de la página.
     * @param total   cantidad total de registros.
     */
    record PageResult(List<Product> content, long total) {
    }

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
     * Obtiene una página de productos.
     *
     * @param page número de página (base 0).
     * @param size cantidad de elementos por página.
     * @return el resultado paginado con el contenido y el total de registros.
     */
    PageResult findAll(int page, int size);

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
    PageResult findAll(int page, int size, Long categoryId, Long markId, Double minPrice, Double maxPrice);

    /**
     * Obtiene una página de productos, opcionalmente filtrados por categoría,
     * marca, rango de precio de venta y un término de búsqueda de texto. Los
     * filtros se combinan con AND y los parámetros {@code null} se ignoran.
     *
     * <p>El término {@code search} coincide de forma parcial e insensible a
     * mayúsculas sobre el nombre de la marca, el nombre del producto y su
     * descripción.
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
    PageResult findAll(int page, int size, Long categoryId, Long markId, Double minPrice, Double maxPrice, String search);

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

    /**
     * Indica si existe al menos un producto asociado a la categoría entregada.
     *
     * @param categoryId identificador de la categoría.
     * @return {@code true} si algún producto referencia la categoría, {@code false} en caso contrario.
     */
    boolean existsProductWithCategory(Long categoryId);

    /**
     * Indica si existe al menos un producto asociado a la marca entregada.
     *
     * @param markId identificador de la marca.
     * @return {@code true} si algún producto referencia la marca, {@code false} en caso contrario.
     */
    boolean existsProductWithMark(Long markId);
}
