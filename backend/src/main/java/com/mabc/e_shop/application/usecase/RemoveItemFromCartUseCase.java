package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;

/**
 * Caso de uso que elimina un ítem específico de un carrito de compras.
 *
 * <p>Valida que el carrito exista, elimina el ítem con el identificador
 * indicado y persiste el carrito con su subtotal recalculado.
 */
public class RemoveItemFromCartUseCase {

    private final CartRepository cartRepository;

    /**
     * Crea el caso de uso con el repositorio de carritos.
     *
     * @param cartRepository repositorio de carritos de compras.
     */
    public RemoveItemFromCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Elimina el ítem correspondiente al identificador entregado del carrito
     * correspondiente al identificador dado.
     *
     * @param cartId identificador del carrito.
     * @param itemId identificador del ítem a eliminar.
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
            cart.removeItemById(itemId);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("El ítem no existe en el carrito.");
        }
        return cartRepository.save(cart);
    }
}
