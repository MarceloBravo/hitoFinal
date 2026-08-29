package com.mabc.e_shop.domain.valueobject;

import com.mabc.e_shop.domain.exception.InvalidPriceException;

/**
 * Value object que representa un precio.
 *
 * <p>Valida que el precio no sea negativo.
 *
 * @param value valor del precio.
 */
public record Price(double value) {

    /**
     * Constructor compacto que valida el precio.
     *
     * @throws InvalidPriceException si el precio es negativo.
     */
    public Price {
        if (value < 0) {
            throw new InvalidPriceException("El precio no puede ser negativo.");
        }
    }
}
