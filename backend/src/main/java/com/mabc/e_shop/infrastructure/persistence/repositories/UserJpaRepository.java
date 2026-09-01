package com.mabc.e_shop.infrastructure.persistence.repositories;

import com.mabc.e_shop.infrastructure.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data para la entidad {@link User}.
 *
 * <p>Proporciona operaciones CRUD y consultas derivadas sobre la tabla
 * {@code users}. Se usa tanto por el flujo de autenticación como por
 * cualquier mantención futura de usuarios (el correo es único).
 */
public interface UserJpaRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo del usuario (único).
     * @return un {@link Optional} con el usuario o vacío si no existe.
     */
    Optional<User> findByEmail(String email);

    /**
     * Indica si existe un usuario con el correo entregado.
     *
     * @param email correo del usuario a verificar.
     * @return {@code true} si ya existe un usuario con ese correo.
     */
    boolean existsByEmail(String email);
}