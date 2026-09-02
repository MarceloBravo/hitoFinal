import { apiClient } from '../api/apiClient.js';
import type { MarkRequestInterface } from '../interfaces/MarkRequestInterface.js';
import type { MarkResponseApi } from '../interfaces/markResponseApi.js';
import type { MarksResponseApi } from '../interfaces/marksResponseApi.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { AuthStore } from '../store/authStore.js';
import { handleError } from '../utils/errorHandler.js';

const URI = '/marks';

/**
 * Construye el encabezado de autorización con el access token del admin,
 * requerido por los endpoints de escritura de marcas.
 */
const authHeaders = (): Record<string, string> => {
  const token = AuthStore.getAccessToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
};

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

    /**
     * Obtiene una marca según su identificador.
     *
     * @param id Identificador de la marca.
     * @returns Respuesta normalizada con la marca o el error ocurrido.
     */
    static getById = async (id: number): Promise<ResponseInterface<MarkResponseApi>> => {
        try {
            return await apiClient<MarkResponseApi>(`${URI}/${id}`);
        } catch (error) {
            return handleError<MarkResponseApi>(error, `No se pudo cargar la marca con id ${id}`);
        }
    }

    /**
     * Registra una marca nueva.
     *
     * @param mark Datos de la marca a registrar.
     * @returns Respuesta normalizada con la marca creada o el error ocurrido.
     */
    static create = async (mark: MarkRequestInterface): Promise<ResponseInterface<MarkResponseApi>> => {
        try {
            return await apiClient<MarkResponseApi>(URI, {
                method: 'POST',
                headers: authHeaders(),
                body: JSON.stringify(mark),
            });
        } catch (error) {
            return handleError<MarkResponseApi>(error, 'No se pudo registrar la marca');
        }
    }

    /**
     * Actualiza una marca existente.
     *
     * @param id   Identificador de la marca a actualizar.
     * @param mark Nuevos datos de la marca.
     * @returns Respuesta normalizada con la marca actualizada o el error ocurrido.
     */
    static update = async (id: number, mark: MarkRequestInterface): Promise<ResponseInterface<MarkResponseApi>> => {
        try {
            return await apiClient<MarkResponseApi>(`${URI}/${id}`, {
                method: 'PUT',
                headers: authHeaders(),
                body: JSON.stringify(mark),
            });
        } catch (error) {
            return handleError<MarkResponseApi>(error, `No se pudo actualizar la marca con id ${id}`);
        }
    }

    /**
     * Elimina una marca existente.
     *
     * @param id Identificador de la marca a eliminar.
     * @returns Respuesta normalizada con el resultado de la operación o el error.
     */
    static delete = async (id: number): Promise<ResponseInterface<MarkResponseApi>> => {
        try {
            return await apiClient<MarkResponseApi>(`${URI}/${id}`, {
                method: 'DELETE',
                headers: authHeaders(),
            });
        } catch (error) {
            return handleError<MarkResponseApi>(error, `No se pudo eliminar la marca con id ${id}`);
        }
    }
}
