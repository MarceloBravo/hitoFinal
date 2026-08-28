import { apiClient } from '../api/apiClient.js';
import type { CategoriesResponseApi } from '../interfaces/categoriesResponseApi.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { handleError } from '../utils/errorHandler.js';

const URI = '/products/categories';

/**
 * Servicio encargado de consumir los endpoints de categorías de la API.
 */
export class categoriesService {
    /**
     * Obtiene el listado de categorías disponibles.
     *
     * @returns Respuesta normalizada con las categorías o el error ocurrido.
     */
    static getAll = async (): Promise<ResponseInterface<CategoriesResponseApi['data']>> => {
        try{
            return await apiClient<CategoriesResponseApi['data']>(URI);
        } catch (error) {
           return handleError<CategoriesResponseApi['data']>(error, 'No se pudieron cargar las categorías');
        }
    }
}
