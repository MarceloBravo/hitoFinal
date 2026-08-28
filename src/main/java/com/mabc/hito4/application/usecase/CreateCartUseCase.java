package com.mabc.hito5.application.usecase;

import com.mabc.hito5.domain.entity.Cart;
import com.mabc.hito5.domain.repository.CartRepository;

/**
 * Caso de uso que crea un nuevo carrito de compras.
 *
 * <p>Calcula el siguiente identificador disponible a partir del último
 * carrito registrado y lo persiste vacío.
 */
public class CreateCartUseCase {

    private final CartRepository cartRepository;

    /**
     * Crea el caso de uso con el repositorio de carritos.
     *
     * @param cartRepository repositorio de carritos de compras.
     */
    public CreateCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Crea y persiste un nuevo carrito de compras vacío.
     *
     * @return el carrito recién creado.
     */
    public Cart execute() {
        Cart cart = new Cart(null);
        return cartRepository.save(cart);
    }
}
