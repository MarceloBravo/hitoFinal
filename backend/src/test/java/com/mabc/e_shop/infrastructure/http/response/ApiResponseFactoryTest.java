package com.mabc.e_shop.infrastructure.http.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiResponseFactoryTest {

    @Test
    @DisplayName("created construye una respuesta 201 con mensaje y datos")
    void buildsCreatedResponse() {
        ApiResponse<String> response = ApiResponseFactory.created("Marca registrada correctamente.", "data");

        assertEquals(201, response.statusCode());
        assertEquals("Marca registrada correctamente.", response.message());
        assertEquals("data", response.data());
    }

    @Test
    @DisplayName("updated construye una respuesta 200 con mensaje y datos")
    void buildsUpdatedResponse() {
        ApiResponse<String> response = ApiResponseFactory.updated("Marca actualizada correctamente.", "data");

        assertEquals(200, response.statusCode());
        assertEquals("Marca actualizada correctamente.", response.message());
        assertEquals("data", response.data());
    }

    @Test
    @DisplayName("queried construye una respuesta 200 con mensaje vacío")
    void buildsQueriedResponse() {
        ApiResponse<String> response = ApiResponseFactory.queried("data");

        assertEquals(200, response.statusCode());
        assertTrue(response.message().isEmpty());
        assertEquals("data", response.data());
    }

    @Test
    @DisplayName("error construye una respuesta sin datos")
    void buildsErrorResponse() {
        ApiResponse<Void> response = ApiResponseFactory.error(404, "La marca no existe.");

        assertEquals(404, response.statusCode());
        assertEquals("La marca no existe.", response.message());
        assertNull(response.data());
    }
}
