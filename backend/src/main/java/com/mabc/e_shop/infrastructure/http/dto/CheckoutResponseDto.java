package com.mabc.e_shop.infrastructure.http.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Respuesta HTTP con el resumen de un checkout (compra concretada).
 *
 * @param cartId    identificador del carrito concretado.
 * @param total     monto total de la compra.
 * @param itemCount cantidad total de unidades compradas.
 * @param products  identificadores de los productos cuyas existencias se rebajaron.
 */
public record CheckoutResponseDto(
    @Schema(description = "Identificador del carrito concretado.", example = "7") Long cartId,
    @Schema(description = "Monto total de la compra.", example = "1400.0") double total,
    @Schema(description = "Cantidad total de unidades compradas.", example = "2") int itemCount,
    @Schema(description = "Identificadores de los productos con stock rebajado.") List<Long> products
) {
}
