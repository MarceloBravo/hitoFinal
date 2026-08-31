package com.mabc.e_shop.infrastructure.persistence.repositories;

import com.mabc.e_shop.infrastructure.persistence.entity.ProductEntity;
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
}
