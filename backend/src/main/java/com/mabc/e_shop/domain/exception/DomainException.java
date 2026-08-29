package com.mabc.e_shop.domain.exception;

/**
 * Excepción base del dominio para errores de negocio.
 *
 * <p>Agrupa todas las excepciones específicas de validación de las reglas de
 * negocio del sistema e-shoping.
 */
public class DomainException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message mensaje que describe el error de dominio.
     */
    public DomainException(String message) {
        super(message);
    }
}
