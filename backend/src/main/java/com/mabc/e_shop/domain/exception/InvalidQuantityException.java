package com.mabc.e_shop.domain.exception;

/**
 * Excepción que indica que una cantidad no es válida.
 *
 * <p>Se lanza cuando la cantidad es menor o igual a cero.
 */
public class InvalidQuantityException extends DomainException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe el error de validación.
     */
    public InvalidQuantityException(String message) {
        super(message);
    }
}
