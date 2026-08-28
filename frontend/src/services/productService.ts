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
   * Obtiene una lista paginada de productos.
   *
   * @param limit Cantidad máxima de productos a solicitar.
   * @param page  Número de página a solicitar (1-indexado).
   * @returns Respuesta normalizada con los productos o el error ocurrido.
   */
  static getAll = async (limit?: number, page?: number): Promise<ResponseInterface<ProductResponseApi>> => {
    try {
      let strURI: string = URI;
      if(limit && page){
        const skip: number = (page - 1) * limit;
        strURI +=  `?limit=${limit}&skip=${skip}`;
      }
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
