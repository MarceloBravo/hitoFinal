package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;

/**
 * Caso de uso que disminuye en una unidad la cantidad de un ítem de un carrito.
 *
 * <p>Valida que el carrito exista, disminuye la cantidad del ítem con el
 * identificador indicado (eliminándolo si llegaba a una sola unidad) y
 * persiste el carrito con su subtotal recalculado.
 */
public class DecrementItemQuantityFromCartUseCase {

    private final CartRepository cartRepository;

    /**
     * Crea el caso de uso con el repositorio de carritos.
     *
     * @param cartRepository repositorio de carritos de compras.
     */
    public DecrementItemQuantityFromCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Disminuye en una unidad la cantidad del ítem correspondiente al
     * identificador entregado del carrito indicado.
     *
     * @param cartId identificador del carrito.
     * @param itemId identificador del ítem a disminuir.
     * @return el carrito actualizado y persistido.
     * @throws ResourceNotFoundException si el carrito o el ítem no existen.
     */
    public Cart execute(Long cartId, Long itemId) {
        if (cartId == null) {
            throw new ResourceNotFoundException("El carrito no existe o no es válido.");
        }
        if (itemId == null) {
            throw new ResourceNotFoundException("El ítem no existe en el carrito.");
        }
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("El carrito no existe o no es válido."));

        try {
            cart.decrementItemQuantity(itemId);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("El ítem no existe en el carrito.");
        }
        return cartRepository.save(cart);
    }
}
