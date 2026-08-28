package com.mabc.hitoFinal.infrastructure.persistence.repositories;

import com.mabc.hitoFinal.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data para la entidad {@link CategoryEntity}.
 *
 * <p>Proporciona operaciones CRUD sobre la tabla {@code categories}.
 */
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}
