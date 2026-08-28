import type { Product } from "../models/Product";

/**
 * Contrato de la respuesta paginada de productos devuelta por la API.
 */
export interface ProductResponseApi {
    limit: number;
    skip: number;
    total: number;
    products: Product[];
    ok: boolean;
    status: number;
}