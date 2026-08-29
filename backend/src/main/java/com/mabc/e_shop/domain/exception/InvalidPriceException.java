package com.mabc.e_shop.domain.exception;

/**
 * Excepción que indica que un precio no es válido.
 *
 * <p>Se lanza cuando un precio es negativo.
 */
public class InvalidPriceException extends DomainException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe el error de validación.
     */
    public InvalidPriceException(String message) {
        super(message);
    }
}
