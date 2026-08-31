import type { CategoriesInterface } from "./CategoriesInterface";

/**
 * Contrato de la respuesta de categorías devuelta por la API
 * (ApiResponse<List<CategoryResponseDto>>).
 */
export interface CategoriesResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Lista de categorías obtenidas. */
    data: CategoriesInterface[];
}
