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
     * Obtiene una página de productos pertenecientes a la categoría entregada.
     *
     * @param categoryId identificador de la categoría.
     * @param pageable   información de paginación.
     * @return página de productos de la categoría.
     */
    @Query("select distinct p from ProductEntity p join p.categories c where c.id = :categoryId")
    Page<ProductEntity> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Obtiene una página de productos de la marca entregada.
     *
     * @param markId   identificador de la marca.
     * @param pageable información de paginación.
     * @return página de productos de la marca.
     */
    @Query("select p from ProductEntity p where p.mark.id = :markId")
    Page<ProductEntity> findAllByMarkId(@Param("markId") Long markId, Pageable pageable);

    /**
     * Obtiene una página de productos de la categoría y la marca entregadas.
     *
     * @param categoryId identificador de la categoría.
     * @param markId     identificador de la marca.
     * @param pageable   información de paginación.
     * @return página de productos que pertenecen a la categoría y a la marca.
     */
    @Query("select distinct p from ProductEntity p join p.categories c where c.id = :categoryId and p.mark.id = :markId")
    Page<ProductEntity> findAllByCategoryIdAndMarkId(@Param("categoryId") Long categoryId,
                                                     @Param("markId") Long markId,
                                                     Pageable pageable);
}
