import type { CategoriesInterface } from "./CategoriesInterface";

/**
 * Contrato de la respuesta de categorías devuelta por la API.
 */
export interface CategoriesResponseApi {
  data: {
    data: CategoriesInterface[];
    message: string;
    statusCode: number;
  }[];
  ok: boolean;
  status: number;
}
