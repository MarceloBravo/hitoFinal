package com.mabc.e_shop.domain.exception;

/**
 * Excepción que indica que un nombre no es válido.
 *
 * <p>Se lanza cuando un nombre es nulo o está vacío.
 */
public class InvalidNameException extends DomainException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe el error de validación.
     */
    public InvalidNameException(String message) {
        super(message);
    }
}
