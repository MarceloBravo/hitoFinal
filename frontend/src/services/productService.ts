import { apiClient } from '../api/apiClient.js';
import type { ProductResponseApi } from '../interfaces/productResponseApi.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { handleError } from '../utils/errorHandler.js';

const URI = '/products';

/**
 * Servicio encargado de consumir los endpoints de productos de la API.
 */
export class ProductService {

  /**
   * Obtiene una lista paginada de productos, opcionalmente filtrada por
   * categoría, marca y rango de precio de venta.
   *
   * @param limit      Cantidad máxima de productos a solicitar.
   * @param page       Número de página a solicitar (1-indexado).
   * @param categoryId Identificador de la categoría para filtrar (opcional).
   * @param markId     Identificador de la marca para filtrar (opcional).
   * @param minPrice   Precio de venta mínimo para filtrar (opcional).
   * @param maxPrice   Precio de venta máximo para filtrar (opcional).
   * @returns Respuesta normalizada con los productos o el error ocurrido.
   */
  static getAll = async (
    limit?: number,
    page?: number,
    categoryId?: number,
    markId?: number,
    minPrice?: number,
    maxPrice?: number
  ): Promise<ResponseInterface<ProductResponseApi>> => {
    try {
      const params = new URLSearchParams();
      if (limit) params.set('limit', String(limit));
      if (page) params.set('page', String(page));
      if (categoryId !== undefined && categoryId !== null) params.set('categoryId', String(categoryId));
      if (markId !== undefined && markId !== null) params.set('markId', String(markId));
      if (minPrice !== undefined && minPrice !== null) params.set('minPrice', String(minPrice));
      if (maxPrice !== undefined && maxPrice !== null) params.set('maxPrice', String(maxPrice));
      const queryString: string = params.toString();
      const strURI: string = queryString ? `${URI}?${queryString}` : URI;
      return await apiClient<ProductResponseApi>(strURI);
    } catch (error) {
      return handleError<ProductResponseApi>(error, 'No se pudieron cargar los productos');
    }
  };

  /**
   * Obtiene un producto según su identificador.
   *
   * @param id Identificador del producto.
   * @returns Respuesta normalizada con el producto o el error ocurrido.
   */
  static getById = async (id: number): Promise<ResponseInterface<ProductResponseApi>> => {
    try {
      return await apiClient<ProductResponseApi>(`${URI}/${id}`);
    } catch (error) {
      return handleError<ProductResponseApi>(error, `No se pudo cargar el producto con id ${id}`);
    }
  };
}
