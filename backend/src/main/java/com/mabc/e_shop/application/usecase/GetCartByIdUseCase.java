package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;

/**
 * Caso de uso que consulta un carrito de compras por su identificador.
 */
public class GetCartByIdUseCase {

    private final CartRepository cartRepository;

    /**
     * Crea el caso de uso con el repositorio de carritos.
     *
     * @param cartRepository repositorio de carritos de compras.
     */
    public GetCartByIdUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Busca el carrito correspondiente al identificador entregado, incluyendo
     * sus ítems y subtotal.
     *
     * @param id identificador del carrito.
     * @return el carrito encontrado.
     * @throws ResourceNotFoundException si el carrito no existe.
     */
    public Cart execute(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El carrito no existe o no es válido."));
    }
}
