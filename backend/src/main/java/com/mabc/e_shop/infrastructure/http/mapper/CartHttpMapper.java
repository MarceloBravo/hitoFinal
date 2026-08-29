package com.mabc.e_shop.infrastructure.http.mapper;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.entity.CartItem;
import com.mabc.e_shop.infrastructure.http.dto.CartItemResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.CartResponseDto;

/**
 * Mapper que convierte las entidades de dominio {@link Cart} y
 * {@link CartItem} en sus DTOs de respuesta HTTP {@link CartResponseDto} y
 * {@link CartItemResponseDto}.
 *
 * <p>Clase utilitaria con métodos estáticos, no instanciable.
 */
public final class CartHttpMapper {

    private CartHttpMapper() {
    }

    /**
     * Convierte un carrito de dominio en su DTO de respuesta HTTP,
     * incluyendo sus ítems y subtotal.
     *
     * @param cart carrito de dominio a convertir.
     * @return el DTO de respuesta resultante.
     */
    public static CartResponseDto toResponse(Cart cart) {
        return new CartResponseDto(
                cart.getId(),
                cart.getCreationDate(),
                cart.getItems().stream().map(CartHttpMapper::toResponse).toList(),
                cart.getSubTotal());
    }

    /**
     * Convierte un ítem de carrito de dominio en su DTO de respuesta HTTP.
     *
     * @param item ítem de carrito a convertir.
     * @return el DTO de respuesta del ítem resultante.
     */
    public static CartItemResponseDto toResponse(CartItem item) {
        return new CartItemResponseDto(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName().value(),
                item.getQuantity().value(),
                item.getSubTotal());
    }
}
