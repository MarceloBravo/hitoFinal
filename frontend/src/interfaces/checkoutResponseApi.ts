/**
 * Resultado de una compra (checkout) devuelto por la API.
 */
export interface CheckoutData {
    /** Identificador del carrito comprado. */
    cartId: number;
    /** Total pagado por la compra. */
    total: number;
    /** Cantidad total de unidades compradas. */
    itemCount: number;
    /** Identificadores de los productos comprados. */
    products: number[];
}

/**
 * Respuesta de compra (ApiResponse<CheckoutData>).
 */
export interface CheckoutResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Datos de la compra realizada. */
    data: CheckoutData;
}
