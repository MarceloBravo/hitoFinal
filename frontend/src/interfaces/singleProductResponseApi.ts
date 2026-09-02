import type { ProductInterface } from './ProductInterface';

/**
 * Contrato de la respuesta de un producto devuelto por la API
 * (ApiResponse<ProductResponseDto>).
 */
export interface SingleProductResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Producto obtenido. */
    data: ProductInterface | null;
}
