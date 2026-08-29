package com.mabc.e_shop.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainExceptionsTest {

    @Test
    @DisplayName("DomainException: propaga el mensaje del error")
    void domainExceptionCarriesMessage() {
        DomainException exception = new DomainException("Error de dominio");
        assertEquals("Error de dominio", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Excepciones especificas: todas propagan su mensaje")
    void specificExceptionsCarryMessage() {
        String message = "mensaje";

        assertEquals(message, new InvalidDescriptionException(message).getMessage());
        assertEquals(message, new InvalidImageException(message).getMessage());
        assertEquals(message, new InvalidNameException(message).getMessage());
        assertEquals(message, new InvalidPriceException(message).getMessage());
        assertEquals(message, new InvalidQuantityException(message).getMessage());
        assertEquals(message, new InvalidStockException(message).getMessage());
        assertEquals(message, new InvalidWeightException(message).getMessage());
    }
}
