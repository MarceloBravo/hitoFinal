package com.mabc.e_shop.infrastructure.http.exception;

import com.mabc.e_shop.domain.exception.InvalidNameException;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Las excepciones de dominio responden con estado 400")
    void handlesDomainException() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleDomainException(new InvalidNameException("El nombre es inválido."));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().statusCode());
        assertEquals("El nombre es inválido.", response.getBody().message());
        assertNull(response.getBody().data());
    }

    @Test
    @DisplayName("Los recursos inexistentes responden con estado 404")
    void handlesIllegalArgument() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("La marca no existe."));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().statusCode());
        assertEquals("La marca no existe.", response.getBody().message());
    }

    @Test
    @DisplayName("Los recursos no encontrados responden con estado 404")
    void handlesResourceNotFound() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleResourceNotFound(new ResourceNotFoundException("El producto no existe."));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().statusCode());
        assertEquals("El producto no existe.", response.getBody().message());
    }

    @Test
    @DisplayName("Los estados inválidos de negocio responden con estado 409")
    void handlesIllegalState() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleIllegalState(new IllegalStateException("Stock insuficiente."));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().statusCode());
        assertEquals("Stock insuficiente.", response.getBody().message());
    }

    @Test
    @DisplayName("La validación de DTOs responde 400 agregando el detalle por campo")
    void handlesValidationErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "El nombre es obligatorio."));
        bindingResult.addError(new FieldError("request", "stock", "El stock no puede ser negativo."));
        Method method = getClass().getDeclaredMethod("sample", String.class);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().statusCode());
        assertTrue(response.getBody().message().contains("name: El nombre es obligatorio."));
        assertTrue(response.getBody().message().contains("; stock: El stock no puede ser negativo."));
    }

    @SuppressWarnings("unused")
    private void sample(String name) {
    }
}
