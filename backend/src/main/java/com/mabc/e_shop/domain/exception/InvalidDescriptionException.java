package com.mabc.e_shop.domain.exception;

/**
 * Excepción que indica que una descripción no es válida.
 *
 * <p>Se lanza cuando una descripción es nula o está vacía.
 */
public class InvalidDescriptionException extends DomainException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe el error de validación.
     */
    public InvalidDescriptionException(String message) {
        super(message);
    }
}
