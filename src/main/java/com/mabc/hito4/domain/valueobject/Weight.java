package com.mabc.hito5.domain.valueobject;

import com.mabc.hito5.domain.exception.InvalidWeightException;

/**
 * Value object que representa un peso.
 *
 * <p>Valida que el peso sea mayor a cero.
 *
 * @param value valor del peso.
 */
public record Weight(double value) {

    /**
     * Constructor compacto que valida el peso.
     *
     * @throws InvalidWeightException si el peso es menor o igual a cero.
     */
    public Weight {
        if (value <= 0) {
            throw new InvalidWeightException("El peso debe ser mayor a cero.");
        }
    }
}
