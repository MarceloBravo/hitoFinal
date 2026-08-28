package com.mabc.hito5.infrastructure.persistence.repositories;

import com.mabc.hito5.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data para la entidad {@link ProductEntity}.
 *
 * <p>Proporciona operaciones CRUD sobre la tabla {@code products}.
 */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
}
