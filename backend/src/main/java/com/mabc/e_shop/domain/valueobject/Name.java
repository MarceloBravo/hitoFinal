package com.mabc.e_shop.domain.valueobject;

import com.mabc.e_shop.domain.exception.InvalidNameException;

/**
 * Value object que representa un nombre.
 *
 * <p>Valida que el nombre no sea nulo ni esté vacío y lo normaliza
 * eliminando espacios en blanco al inicio y al final.
 *
 * @param value texto del nombre.
 */
public record Name(String value) {

    /**
     * Constructor compacto que valida y normaliza el nombre.
     *
     * @throws InvalidNameException si el nombre es nulo o está vacío.
     */
    public Name {
        if (value == null || value.isBlank()) {
            throw new InvalidNameException("El nombre no puede ser nulo o estar vacío.");
        }
        value = value.trim();
    }
}
