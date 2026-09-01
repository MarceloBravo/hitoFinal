/**
 * Ítem de un carrito de compras devuelto por la API.
 */
export interface CartItemResponseApi {
    /** Identificador del ítem dentro del carrito. */
    id: number;
    /** Identificador del producto agregado. */
    productId: number;
    /** Nombre del producto agregado. */
    productName: string;
    /** Cantidad de unidades del producto en el carrito. */
    quantity: number;
    /** Subtotal del ítem (precio de venta por cantidad). */
    subTotal: number;
}

/**
 * Carrito de compras devuelto por la API (ApiResponse<CartResponseDto>).
 */
export interface CartResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Datos del carrito. */
    data: {
        /** Identificador del carrito. */
        id: number;
        /** Fecha de creación del carrito. */
        creationDate: string;
        /** Ítems que componen el carrito. */
        items: CartItemResponseApi[];
        /** Subtotal acumulado del carrito. */
        subTotal: number;
    };
}
