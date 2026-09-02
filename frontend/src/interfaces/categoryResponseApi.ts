import type { CategoriesInterface } from './CategoriesInterface';

/**
 * Contrato de la respuesta de una categoría devuelta por la API
 * (ApiResponse<CategoryResponseDto>).
 */
export interface CategoryResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Categoría obtenida. */
    data: CategoriesInterface | null;
}
