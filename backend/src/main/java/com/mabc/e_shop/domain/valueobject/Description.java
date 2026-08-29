package com.mabc.e_shop.domain.valueobject;

import com.mabc.e_shop.domain.exception.InvalidDescriptionException;

/**
 * Value object que representa una descripción.
 *
 * <p>Valida que la descripción no sea nula ni esté vacía y la normaliza
 * eliminando espacios en blanco al inicio y al final.
 *
 * @param value texto de la descripción.
 */
public record Description(String value) {

    /**
     * Constructor compacto que valida y normaliza la descripción.
     *
     * @throws InvalidDescriptionException si la descripción es nula o está vacía.
     */
    public Description {
        if (value == null || value.isBlank()) {
            throw new InvalidDescriptionException("La descripción no puede ser nula o estar vacía.");
        }
        value = value.trim();
    }
}
