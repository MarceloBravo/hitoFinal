/**
 * Contrato de la respuesta de categorías devuelta por la API.
 */
export interface CategoriesResponseApi {
  data: {
    /** Nombre de la categoría. */
    name: string;
    /** Slug identificador de la categoría. */
    slug: string;
    /** URL del endpoint de la categoría. */
    url: string;
  }[];
  ok: boolean;
  status: number;
}
