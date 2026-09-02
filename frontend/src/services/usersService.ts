import { apiClient } from '../api/apiClient.js';
import type { UserRequestInterface } from '../interfaces/UserRequestInterface.js';
import type { UserResponseApi } from '../interfaces/userResponseApi.js';
import type { UsersResponseApi } from '../interfaces/usersResponseApi.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { AuthStore } from '../store/authStore.js';
import { handleError } from '../utils/errorHandler.js';

const URI = '/users';

/**
 * Construye el encabezado de autorización con el access token del admin,
 * requerido por los endpoints de usuarios (acceso exclusivo para ADMIN).
 */
const authHeaders = (): Record<string, string> => {
  const token = AuthStore.getAccessToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
};

/**
 * Servicio encargado de consumir los endpoints de usuarios de la API.
 */
export class usersService {
    /**
     * Obtiene el listado de usuarios registrados.
     *
     * @returns Respuesta normalizada con los usuarios o el error ocurrido.
     */
    static getAll = async (): Promise<ResponseInterface<UsersResponseApi>> => {
        try {
            return await apiClient<UsersResponseApi>(URI);
        } catch (error) {
            return handleError<UsersResponseApi>(error, 'No se pudieron cargar los usuarios');
        }
    }

    /**
     * Obtiene un usuario según su identificador.
     *
     * @param id Identificador del usuario.
     * @returns Respuesta normalizada con el usuario o el error ocurrido.
     */
    static getById = async (id: number): Promise<ResponseInterface<UserResponseApi>> => {
        try {
            return await apiClient<UserResponseApi>(`${URI}/${id}`);
        } catch (error) {
            return handleError<UserResponseApi>(error, `No se pudo cargar el usuario con id ${id}`);
        }
    }

    /**
     * Registra un usuario nuevo.
     *
     * @param user Datos del usuario a registrar.
     * @returns Respuesta normalizada con el usuario creado o el error ocurrido.
     */
    static create = async (user: UserRequestInterface): Promise<ResponseInterface<UserResponseApi>> => {
        try {
            return await apiClient<UserResponseApi>(URI, {
                method: 'POST',
                headers: authHeaders(),
                body: JSON.stringify(user),
            });
        } catch (error) {
            return handleError<UserResponseApi>(error, 'No se pudo registrar el usuario');
        }
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id   Identificador del usuario a actualizar.
     * @param user Nuevos datos del usuario.
     * @returns Respuesta normalizada con el usuario actualizado o el error ocurrido.
     */
    static update = async (id: number, user: UserRequestInterface): Promise<ResponseInterface<UserResponseApi>> => {
        try {
            return await apiClient<UserResponseApi>(`${URI}/${id}`, {
                method: 'PUT',
                headers: authHeaders(),
                body: JSON.stringify(user),
            });
        } catch (error) {
            return handleError<UserResponseApi>(error, `No se pudo actualizar el usuario con id ${id}`);
        }
    }

    /**
     * Elimina (desactiva) un usuario existente.
     *
     * @param id Identificador del usuario a eliminar.
     * @returns Respuesta normalizada con el resultado de la operación o el error.
     */
    static delete = async (id: number): Promise<ResponseInterface<UserResponseApi>> => {
        try {
            return await apiClient<UserResponseApi>(`${URI}/${id}`, {
                method: 'DELETE',
                headers: authHeaders(),
            });
        } catch (error) {
            return handleError<UserResponseApi>(error, `No se pudo eliminar el usuario con id ${id}`);
        }
    }
}
