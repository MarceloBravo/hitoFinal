import type { ProductInterface } from './ProductInterface';

/**
 * Contrato de la respuesta paginada de productos devuelta por la API
 * (ProductPaginatedResponseDto).
 */
export interface ProductsResponseApi {
    /** Cantidad máxima de productos por página. */
    limit: number;
    /** Cantidad de productos omitidos. */
    skip: number;
    /** Cantidad total de productos. */
    total: number;
    /** Lista de productos de la página actual. */
    products: ProductInterface[];
}
