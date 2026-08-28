package com.mabc.hitoFinal.infrastructure.persistence.repositories;

import com.mabc.hitoFinal.infrastructure.persistence.entity.MarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data para la entidad {@link MarkEntity}.
 *
 * <p>Proporciona operaciones CRUD sobre la tabla {@code marks}.
 */
public interface MarkJpaRepository extends JpaRepository<MarkEntity, Long> {
}
