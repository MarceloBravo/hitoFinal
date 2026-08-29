package com.mabc.e_shop.infrastructure.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Objeto estándar de respuesta HTTP de la API.
 *
 * <p>Toda respuesta exitosa o de error se representa con esta estructura:
 * el código de estado HTTP, un mensaje descriptivo y, cuando corresponde,
 * los datos resultantes de la operación.
 *
 * @param <T> tipo de los datos retornados.
 * @param statusCode código de estado HTTP de la respuesta.
 * @param message mensaje descriptivo; cadena vacía en consultas y detalle del error en fallos.
 * @param data datos retornados por la operación; {@code null} en caso de error.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    int statusCode,
    String message,
    T data
) {
}
