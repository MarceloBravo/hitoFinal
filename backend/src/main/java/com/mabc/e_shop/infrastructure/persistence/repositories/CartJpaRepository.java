package com.mabc.e_shop.infrastructure.persistence.repositories;

import com.mabc.e_shop.infrastructure.persistence.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data para la entidad {@link CartEntity}.
 *
 * <p>Proporciona operaciones CRUD y consultas derivadas sobre la tabla
 * {@code carts}.
 */
public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {

    /**
     * Busca el carrito con el mayor identificador.
     *
     * @return un {@link Optional} con el último carrito o vacío si no hay carritos.
     */
    Optional<CartEntity> findTopByOrderByIdDesc();
}
