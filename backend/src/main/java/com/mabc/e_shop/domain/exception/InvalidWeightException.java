package com.mabc.e_shop.domain.exception;

/**
 * Excepción que indica que un peso no es válido.
 *
 * <p>Se lanza cuando el peso es menor o igual a cero.
 */
public class InvalidWeightException extends DomainException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe el error de validación.
     */
    public InvalidWeightException(String message) {
        super(message);
    }
}
