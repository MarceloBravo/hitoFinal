package com.mabc.e_shop.domain.exception;

/**
 * Excepción de negocio que indica que el recurso buscado no existe en el
 * sistema.
 *
 * <p>El manejador global de errores la traduce a una respuesta HTTP 404 con
 * el DTO unificado de la API.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crea la excepción con el mensaje descriptivo del recurso ausente.
     *
     * @param message detalle del recurso no encontrado.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
