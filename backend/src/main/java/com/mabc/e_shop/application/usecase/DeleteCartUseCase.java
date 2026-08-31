package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;

/**
 * Caso de uso que elimina un carrito de compras por su identificador.
 */
public class DeleteCartUseCase {

    private final CartRepository cartRepository;

    /**
     * Crea el caso de uso con el repositorio de carritos.
     *
     * @param cartRepository repositorio de carritos de compras.
     */
    public DeleteCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Elimina el carrito correspondiente al identificador entregado.
     *
     * @param id identificador del carrito a eliminar.
     * @return {@code true} si el carrito fue eliminado.
     * @throws IllegalArgumentException si el identificador es {@code null}.
     * @throws ResourceNotFoundException si el carrito no existe.
     */
    public boolean execute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del carrito a eliminar es obligatorio.");
        }
        cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El carrito no existe o no es válido."));
        cartRepository.deleteById(id);
        return true;
    }
}
