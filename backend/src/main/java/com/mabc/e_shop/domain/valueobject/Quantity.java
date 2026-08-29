package com.mabc.e_shop.domain.valueobject;

import com.mabc.e_shop.domain.exception.InvalidQuantityException;

/**
 * Value object que representa una cantidad.
 *
 * <p>Valida que la cantidad sea mayor a cero.
 *
 * @param value valor de la cantidad.
 */
public record Quantity(int value) {

    /**
     * Constructor compacto que valida la cantidad.
     *
     * @throws InvalidQuantityException si la cantidad es menor o igual a cero.
     */
    public Quantity {
        if (value <= 0) {
            throw new InvalidQuantityException("La cantidad debe ser mayor a cero.");
        }
    }
}
