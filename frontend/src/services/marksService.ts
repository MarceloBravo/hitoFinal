import { apiClient } from '../api/apiClient.js';
import type { MarksResponseApi } from '../interfaces/marksResponseApi.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { handleError } from '../utils/errorHandler.js';

const URI = '/marks';

/**
 * Servicio encargado de consumir los endpoints de marcas de la API.
 */
export class marksService {
    /**
     * Obtiene el listado de marcas disponibles.
     *
     * @returns Respuesta normalizada con las marcas o el error ocurrido.
     */
    static getAll = async (): Promise<ResponseInterface<MarksResponseApi>> => {
        try {
            return await apiClient<MarksResponseApi>(URI);
        } catch (error) {
            return handleError<MarksResponseApi>(error, 'No se pudieron cargar las marcas');
        }
    }
}
