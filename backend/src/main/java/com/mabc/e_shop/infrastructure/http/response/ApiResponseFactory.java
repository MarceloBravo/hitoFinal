package com.mabc.e_shop.infrastructure.http.response;

/**
 * Fábrica dedicada a la construcción del objeto estándar de respuesta
 * {@link ApiResponse}.
 *
 * <p>Centraliza en un único punto las reglas de construcción de respuestas:
 * códigos de estado según el tipo de operación, mensajes descriptivos y el
 * tratamiento de los datos retornados.
 *
 * <p>Clase utilitaria con métodos estáticos, no instanciable.
 */
public final class ApiResponseFactory {

    private static final String EMPTY_MESSAGE = "";

    private ApiResponseFactory() {
    }

    /**
     * Construye la respuesta de una creación exitosa de recursos.
     *
     * @param message mensaje descriptivo de la operación realizada.
     * @param data    recurso creado.
     * @param <T>     tipo de los datos retornados.
     * @return la respuesta estándar con estado HTTP 201.
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data);
    }

    /**
     * Construye la respuesta de una actualización exitosa de recursos.
     *
     * @param message mensaje descriptivo de la operación realizada.
     * @param data    recurso actualizado.
     * @param <T>     tipo de los datos retornados.
     * @return la respuesta estándar con estado HTTP 200.
     */
    public static <T> ApiResponse<T> updated(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * Construye la respuesta de una consulta exitosa de datos.
     *
     * @param data datos consultados; puede ser un objeto o una lista.
     * @param <T>  tipo de los datos retornados.
     * @return la respuesta estándar con estado HTTP 200 y mensaje vacío.
     */
    public static <T> ApiResponse<T> queried(T data) {
        return new ApiResponse<>(200, EMPTY_MESSAGE, data);
    }

    /**
     * Construye la respuesta de una operación fallida.
     *
     * @param statusCode código de estado HTTP del error.
     * @param message    detalle del error ocurrido.
     * @param <T>        tipo de los datos retornados.
     * @return la respuesta estándar sin datos.
     */
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(statusCode, message, null);
    }

    /**
     * Construye la respuesta de una eliminación exitosa de recursos.
     *
     * @param message mensaje descriptivo de la operación realizada.
     * @param data    datos de la operación; puede ser {@code null}.
     * @param <T>     tipo de los datos retornados.
     * @return la respuesta estándar con estado HTTP 200.
     */
    public static <T> ApiResponse<T> deleted(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }
}
