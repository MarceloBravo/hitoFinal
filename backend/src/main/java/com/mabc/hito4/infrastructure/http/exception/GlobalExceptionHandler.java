package com.mabc.hitoFinal.infrastructure.http.exception;

import com.mabc.hitoFinal.domain.exception.DomainException;
import com.mabc.hitoFinal.domain.exception.ResourceNotFoundException;
import com.mabc.hitoFinal.infrastructure.http.response.ApiResponse;
import com.mabc.hitoFinal.infrastructure.http.response.ApiResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones que traduce los errores del dominio y de
 * la aplicación a respuestas HTTP estándar.
 *
 * <p>Toda respuesta de error se construye mediante
 * {@link ApiResponseFactory#error(int, String)} para mantener el contrato de
 * respuesta uniforme de la API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Traduce excepciones de validación del dominio a respuestas HTTP 400.
     *
     * @param exception excepción de dominio lanzada durante la operación.
     * @return la respuesta estándar con el detalle del error.
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * Traduce argumentos inválidos por recurso inexistente a respuestas HTTP 404.
     *
     * @param exception excepción lanzada al operar sobre un recurso inexistente.
     * @return la respuesta estándar con el detalle del error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Traduce la ausencia de un recurso consultado a respuestas HTTP 404.
     *
     * @param exception excepción lanzada al no encontrar el recurso solicitado.
     * @return la respuesta estándar con el detalle del error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Traduce estados inválidos de negocio a respuestas HTTP 409.
     *
     * @param exception excepción lanzada por un estado incompatible, por
     *                  ejemplo stock insuficiente.
     * @return la respuesta estándar con el detalle del error.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException exception) {
        return build(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * Traduce fallos de validación de los DTOs de petición a respuestas HTTP 400,
     * agregando el detalle de cada campo inválido en el mensaje.
     *
     * @param exception excepción con los errores de validación detectados.
     * @return la respuesta estándar con el detalle de los campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .distinct()
                .reduce((a, b) -> a + "; " + b)
                .orElse("Petición inválida.");
        return build(HttpStatus.BAD_REQUEST, detail);
    }

    private ResponseEntity<ApiResponse<Void>> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponseFactory.error(status.value(), message));
    }
}
