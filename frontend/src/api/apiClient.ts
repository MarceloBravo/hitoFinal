import type { ApiError, ResponseInterface } from "../interfaces/responseInterface";

//const BASE_URL = 'https://dummyjson.com';
const BASE_URL = import.meta.env.VITE_API_URL;

/**
 * Construye una respuesta de error normalizada para el cliente HTTP.
 *
 * @param message Mensaje de error a devolver.
 * @param status  Código de estado HTTP (por defecto 500).
 * @returns Objeto `ApiError` listo para propagarse.
 */
const buildErrorResponse = (message: string, status = 500): ApiError => ({
  data: message,
  ok: false,
  status,
});

/**
 * Cliente HTTP genérico basado en `fetch`.
 *
 * Realiza peticiones asíncronas contra `VITE_API_URL`, envía y recibe JSON,
 * y normaliza tanto las respuestas exitosas como los errores en una
 * `ResponseInterface<T>`.
 *
 * @template T Tipo de dato esperado en el cuerpo de la respuesta.
 * @param endpoint Ruta relativa a la API, p. ej. `/products`.
 * @param options  Opciones de `fetch` (method, headers, body, etc.).
 * @returns Promesa con la respuesta normalizada.
 * @throws {ApiError} Si la petición falla o el servidor responde con un error.
 */
export const apiClient = async <T,>(endpoint: string, options: RequestInit = {}): Promise<ResponseInterface<T>> => {
  try {
    const url = `${BASE_URL}${endpoint}`;

    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });

    if (!response.ok) {
      throw Object.assign(new Error(`HTTP error! status: ${response.status}`), {
        status: response.status,
      });
    }

    const responseData = (await response.json()) as T;

    return {
      data: responseData,
      ok: true,
      status: response.status,
    } as ResponseInterface<T>;
  } catch (error) {
    const status = error instanceof Error && 'status' in error && typeof error.status === 'number'
      ? error.status
      : 500;
    const message = error instanceof Error ? error.message : 'Unknown error';

    console.error('Error in apiClient:', message);
    throw buildErrorResponse(message, status);
  }
};
