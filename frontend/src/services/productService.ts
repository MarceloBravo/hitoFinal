import { apiClient } from '../api/apiClient.js';
import type { ProductResponseApi } from '../interfaces/productResponseApi.js';
import type { ProductsResponseApi } from '../interfaces/productsResponseApi.js';
import type { SingleProductResponseApi } from '../interfaces/singleProductResponseApi.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { AuthStore } from '../store/authStore.js';
import { handleError } from '../utils/errorHandler.js';

const URI = '/products';

/**
 * Construye el encabezado de autorización con el access token del admin.
 */
const authHeaders = (): Record<string, string> => {
  const token = AuthStore.getAccessToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
};

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
   * Obtiene el listado de productos para el mantenedor (backoffice), tipado
   * con la estructura real de {@link ProductInterface}.
   *
   * @returns Respuesta normalizada con los productos o el error ocurrido.
   */
  static getAllForAdmin = async (): Promise<ResponseInterface<ProductsResponseApi>> => {
    try {
      return await apiClient<ProductsResponseApi>(`${URI}?limit=1000`);
    } catch (error) {
      return handleError<ProductsResponseApi>(error, 'No se pudieron cargar los productos');
    }
  };

  /**
   * Obtiene un producto según su identificador.
   *
   * @param id Identificador del producto.
   * @returns Respuesta normalizada con el producto o el error ocurrido.
   */
  static getById = async (id: number): Promise<ResponseInterface<SingleProductResponseApi>> => {
    try {
      return await apiClient<SingleProductResponseApi>(`${URI}/${id}`);
    } catch (error) {
      return handleError<SingleProductResponseApi>(error, `No se pudo cargar el producto con id ${id}`);
    }
  };

  /**
   * Registra un producto nuevo (multipart/form-data).
   *
   * @param formData Datos del producto incluyendo la imagen.
   * @returns Respuesta normalizada con el producto creado o el error ocurrido.
   */
  static create = async (formData: FormData): Promise<ResponseInterface<SingleProductResponseApi>> => {
    try {
      return await apiClient<SingleProductResponseApi>(URI, {
        method: 'POST',
        headers: authHeaders(),
        body: formData,
      });
    } catch (error) {
      return handleError<SingleProductResponseApi>(error, 'No se pudo registrar el producto');
    }
  };

  /**
   * Actualiza un producto existente (multipart/form-data).
   *
   * @param id       Identificador del producto a actualizar.
   * @param formData Nuevos datos del producto incluyendo la imagen opcional.
   * @returns Respuesta normalizada con el producto actualizado o el error ocurrido.
   */
  static update = async (id: number, formData: FormData): Promise<ResponseInterface<SingleProductResponseApi>> => {
    try {
      return await apiClient<SingleProductResponseApi>(`${URI}/${id}`, {
        method: 'PUT',
        headers: authHeaders(),
        body: formData,
      });
    } catch (error) {
      return handleError<SingleProductResponseApi>(error, `No se pudo actualizar el producto con id ${id}`);
    }
  };

  /**
   * Elimina un producto existente.
   *
   * @param id Identificador del producto a eliminar.
   * @returns Respuesta normalizada con el resultado de la operación o el error.
   */
  static delete = async (id: number): Promise<ResponseInterface<SingleProductResponseApi>> => {
    try {
      return await apiClient<SingleProductResponseApi>(`${URI}/${id}`, {
        method: 'DELETE',
        headers: authHeaders(),
      });
    } catch (error) {
      return handleError<SingleProductResponseApi>(error, `No se pudo eliminar el producto con id ${id}`);
    }
  };
}
