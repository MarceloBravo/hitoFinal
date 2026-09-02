import { apiClient } from '../api/apiClient.js';
import type { CategoryRequestInterface } from '../interfaces/CategoryRequestInterface.js';
import type { CategoryResponseApi } from '../interfaces/categoryResponseApi.js';
import type { CategoriesResponseApi } from '../interfaces/categoriesResponseApi.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { AuthStore } from '../store/authStore.js';
import { handleError } from '../utils/errorHandler.js';

const URI = '/categories';

/**
 * Construye el encabezado de autorización con el access token del admin,
 * requerido por los endpoints de escritura de categorías.
 */
const authHeaders = (): Record<string, string> => {
  const token = AuthStore.getAccessToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
};

/**
 * Servicio encargado de consumir los endpoints de categorías de la API.
 */
export class categoriesService {
    /**
     * Obtiene el listado de categorías disponibles.
     *
     * @returns Respuesta normalizada con las categorías o el error ocurrido.
     */
    static getAll = async (): Promise<ResponseInterface<CategoriesResponseApi>> => {
        try {
            return await apiClient<CategoriesResponseApi>(URI);
        } catch (error) {
            return handleError<CategoriesResponseApi>(error, 'No se pudieron cargar las categorías');
        }
    }

    /**
     * Obtiene una categoría según su identificador.
     *
     * @param id Identificador de la categoría.
     * @returns Respuesta normalizada con la categoría o el error ocurrido.
     */
    static getById = async (id: number): Promise<ResponseInterface<CategoryResponseApi>> => {
        try {
            return await apiClient<CategoryResponseApi>(`${URI}/${id}`);
        } catch (error) {
            return handleError<CategoryResponseApi>(error, `No se pudo cargar la categoría con id ${id}`);
        }
    }

    /**
     * Registra una categoría nueva.
     *
     * @param category Datos de la categoría a registrar.
     * @returns Respuesta normalizada con la categoría creada o el error ocurrido.
     */
    static create = async (category: CategoryRequestInterface): Promise<ResponseInterface<CategoryResponseApi>> => {
        try {
            return await apiClient<CategoryResponseApi>(URI, {
                method: 'POST',
                headers: authHeaders(),
                body: JSON.stringify(category),
            });
        } catch (error) {
            return handleError<CategoryResponseApi>(error, 'No se pudo registrar la categoría');
        }
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param id       Identificador de la categoría a actualizar.
     * @param category Nuevos datos de la categoría.
     * @returns Respuesta normalizada con la categoría actualizada o el error ocurrido.
     */
    static update = async (id: number, category: CategoryRequestInterface): Promise<ResponseInterface<CategoryResponseApi>> => {
        try {
            return await apiClient<CategoryResponseApi>(`${URI}/${id}`, {
                method: 'PUT',
                headers: authHeaders(),
                body: JSON.stringify(category),
            });
        } catch (error) {
            return handleError<CategoryResponseApi>(error, `No se pudo actualizar la categoría con id ${id}`);
        }
    }

    /**
     * Elimina una categoría existente.
     *
     * @param id Identificador de la categoría a eliminar.
     * @returns Respuesta normalizada con el resultado de la operación o el error.
     */
    static delete = async (id: number): Promise<ResponseInterface<CategoryResponseApi>> => {
        try {
            return await apiClient<CategoryResponseApi>(`${URI}/${id}`, {
                method: 'DELETE',
                headers: authHeaders(),
            });
        } catch (error) {
            return handleError<CategoryResponseApi>(error, `No se pudo eliminar la categoría con id ${id}`);
        }
    }
}
