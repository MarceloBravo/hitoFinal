package com.mabc.hitoFinal.infrastructure.persistence.repositories;

import com.mabc.hitoFinal.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data para la entidad {@link ProductEntity}.
 *
 * <p>Proporciona operaciones CRUD sobre la tabla {@code products}.
 */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
}
