import type { AuthEnvelope, AuthResponse } from '../interfaces/authResponseInterface.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { handleError } from '../utils/errorHandler.js';

const BASE_URL = import.meta.env.VITE_API_URL;

/**
 * Servicio encargado de consumir los endpoints de autenticación de la API.
 *
 * Las peticiones se realizan con `fetch` directo (no con {@link import('../api/apiClient.js').apiClient})
 * para poder leer el `message` que el backend envía en el cuerpo de las
 * respuestas de error (p. ej. "Credenciales inválidas"), y con
 * `credentials: 'include'` para que la cookie `HttpOnly` del refresh token
 * viaje y se almacene automáticamente.
 */
export class AuthService {

  /**
   * Inicia sesión con credenciales válidas.
   *
   * @param email    correo del usuario.
   * @param password contraseña del usuario.
   * @returns respuesta normalizada con los tokens de sesión o el error ocurrido.
   */
  static login = async (email: string, password: string): Promise<ResponseInterface<AuthResponse>> => {
    try {
      const response = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      const envelope = (await response.json()) as AuthEnvelope<AuthResponse>;

      if (!response.ok) {
        return {
          data: envelope.message || 'No se pudo iniciar sesión.',
          ok: false,
          status: response.status,
        };
      }

      return {
        data: envelope.data as AuthResponse,
        ok: true,
        status: response.status,
      } as ResponseInterface<AuthResponse>;
    } catch (error) {
      return handleError<AuthResponse>(error, 'No se pudo iniciar sesión');
    }
  };

  /**
   * Renueva la sesión a partir de la cookie `HttpOnly` del refresh token.
   *
   * El backend lee la cookie, valida el token y rota la cookie con uno nuevo,
   * devolviendo en el body únicamente el access token (se mantiene en memoria).
   *
   * @returns respuesta normalizada con la sesión renovada o el error ocurrido.
   */
  static refresh = async (): Promise<ResponseInterface<AuthResponse>> => {
    try {
      const response = await fetch(`${BASE_URL}/auth/refresh`, {
        method: 'POST',
        credentials: 'include',
      });

      const envelope = (await response.json()) as AuthEnvelope<AuthResponse>;

      if (!response.ok) {
        return {
          data: envelope.message || 'La sesión ha caducado. Inicia sesión de nuevo.',
          ok: false,
          status: response.status,
        };
      }

      return {
        data: envelope.data as AuthResponse,
        ok: true,
        status: response.status,
      } as ResponseInterface<AuthResponse>;
    } catch (error) {
      return handleError<AuthResponse>(error, 'No se pudo renovar la sesión');
    }
  };

  /**
   * Cierra la sesión de forma stateless.
   *
   * El backend expira la cookie de refresco; el cliente descarta el access
   * token que tiene en memoria.
   *
   * @returns respuesta normalizada con el resultado de la operación.
   */
  static logout = async (): Promise<ResponseInterface<null>> => {
    try {
      const response = await fetch(`${BASE_URL}/auth/logout`, {
        method: 'POST',
        credentials: 'include',
      });

      if (!response.ok) {
        return {
          data: 'No se pudo cerrar sesión.',
          ok: false,
          status: response.status,
        };
      }

      return {
        data: null,
        ok: true,
        status: response.status,
      };
    } catch (error) {
      return handleError<null>(error, 'No se pudo cerrar sesión');
    }
  };
}