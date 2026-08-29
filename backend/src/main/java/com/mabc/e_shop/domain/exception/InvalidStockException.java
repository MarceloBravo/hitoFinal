package com.mabc.e_shop.domain.exception;

/**
 * Excepción que indica que un stock no es válido.
 *
 * <p>Se lanza cuando el stock es negativo.
 */
public class InvalidStockException extends DomainException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe el error de validación.
     */
    public InvalidStockException(String message) {
        super(message);
    }
}
