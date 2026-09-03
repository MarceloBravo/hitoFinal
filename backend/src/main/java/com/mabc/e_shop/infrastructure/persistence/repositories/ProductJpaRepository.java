package com.mabc.e_shop.infrastructure.persistence.repositories;

import com.mabc.e_shop.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio Spring Data para la entidad {@link ProductEntity}.
 *
 * <p>Proporciona operaciones CRUD sobre la tabla {@code products}.
 */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * Cuenta los productos asociados a la categoría entregada.
     *
     * @param categoryId identificador de la categoría.
     * @return cantidad de productos que referencian la categoría.
     */
    @Query("select count(p) from ProductEntity p join p.categories c where c.id = :categoryId")
    long countProductsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Cuenta los productos asociados a la marca entregada.
     *
     * @param markId identificador de la marca.
     * @return cantidad de productos que referencian la marca.
     */
    @Query("select count(p) from ProductEntity p where p.mark.id = :markId")
    long countProductsByMarkId(@Param("markId") Long markId);

    /**
     * Obtiene una página de productos aplicando los filtros opcionales de
     * categoría, marca y rango de precio de venta. Los parámetros {@code null}
     * se ignoran, permitiendo combinar cualquier subconjunto de filtros.
     *
     * @param categoryId identificador de la categoría, o {@code null}.
     * @param markId     identificador de la marca, o {@code null}.
     * @param minPrice   precio de venta mínimo, o {@code null}.
     * @param maxPrice   precio de venta máximo, o {@code null}.
     * @param pageable   información de paginación.
     * @return página de productos que cumplen los filtros entregados.
     */
    @Query("""
            select distinct p from ProductEntity p
            left join p.categories c
            where (:categoryId is null or c.id = :categoryId)
              and (:markId is null or p.mark.id = :markId)
              and (:minPrice is null or p.priceSale >= :minPrice)
              and (:maxPrice is null or p.priceSale <= :maxPrice)
            """)
    Page<ProductEntity> findFiltered(@Param("categoryId") Long categoryId,
                                     @Param("markId") Long markId,
                                     @Param("minPrice") Double minPrice,
                                     @Param("maxPrice") Double maxPrice,
                                     Pageable pageable);

    /**
     * Obtiene una página de productos aplicando los filtros opcionales de
     * categoría, marca, rango de precio de venta y un término de búsqueda de
     * texto. Los parámetros {@code null} se ignoran, permitiendo combinar
     * cualquier subconjunto de filtros con {@code AND}.
     *
     * <p>El término {@code search} coincide de forma parcial e insensible a
     * mayúsculas sobre el nombre de la marca ({@code mark.name}), el nombre del
     * producto ({@code name}) y su descripción ({@code description}).
     *
     * @param categoryId identificador de la categoría, o {@code null}.
     * @param markId     identificador de la marca, o {@code null}.
     * @param minPrice   precio de venta mínimo, o {@code null}.
     * @param maxPrice   precio de venta máximo, o {@code null}.
     * @param search     término de búsqueda de texto, o {@code null}.
     * @param pageable   información de paginación.
     * @return página de productos que cumplen los filtros entregados.
     */
    @Query("""
            select distinct p from ProductEntity p
            left join p.categories c
            where (:categoryId is null or c.id = :categoryId)
              and (:markId is null or p.mark.id = :markId)
              and (:minPrice is null or p.priceSale >= :minPrice)
              and (:maxPrice is null or p.priceSale <= :maxPrice)
              and (:search is null
                   or lower(p.mark.name) like lower(concat('%', cast(:search as string), '%'))
                   or lower(p.name) like lower(concat('%', cast(:search as string), '%'))
                   or lower(p.description) like lower(concat('%', cast(:search as string), '%')))
            """)
    Page<ProductEntity> findFilteredWithSearch(@Param("categoryId") Long categoryId,
                                     @Param("markId") Long markId,
                                     @Param("minPrice") Double minPrice,
                                     @Param("maxPrice") Double maxPrice,
                                     @Param("search") String search,
                                     Pageable pageable);
}
