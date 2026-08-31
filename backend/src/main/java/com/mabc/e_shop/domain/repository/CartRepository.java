package com.mabc.e_shop.domain.repository;

import com.mabc.e_shop.domain.entity.Cart;

import java.util.Optional;

/**
 * Repositorio de carritos de compras.
 *
 * <p>Define el contrato de persistencia para la entidad {@link Cart},
 * permitiendo consultar, guardar y recuperar el último carrito registrado.
 */
public interface CartRepository {

    /**
     * Busca un carrito por su identificador.
     *
     * @param id identificador del carrito.
     * @return un {@link Optional} con el carrito encontrado o vacío si no existe.
     */
    Optional<Cart> findById(Long id);

    /**
     * Busca el último carrito registrado según su identificador.
     *
     * @return un {@link Optional} con el último carrito o vacío si no hay carritos.
     */
    Optional<Cart> findLast();

    /**
     * Guarda o actualiza un carrito.
     *
     * @param cart carrito a persistir.
     * @return el carrito persistido.
     */
    Cart save(Cart cart);

    /**
     * Elimina un carrito por su identificador.
     *
     * @param id identificador del carrito a eliminar.
     */
    void deleteById(Long id);
}
