import type { ApiError, ResponseInterface } from "../interfaces/responseInterface";
import { AuthStore } from "../store/authStore.js";

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
 * Renueva la sesión ante un 401. Se comparte entre todas las peticiones
 * concurrentes para evitar llamadas simultáneas a `/auth/refresh` (que rota la
 * cookie HttpOnly del refresh token).
 */
let refreshPromise: Promise<boolean> | null = null;

const refreshSession = (): Promise<boolean> => {
  if (!refreshPromise) {
    refreshPromise = AuthStore.tryRestoreSession()
      .then(() => AuthStore.isAuthenticated())
      .finally(() => {
        refreshPromise = null;
      });
  }
  return refreshPromise;
};

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
const performRequest = async <T,>(url: string, options: RequestInit): Promise<ResponseInterface<T>> => {
  const { headers, body, ...rest } = options;

  const isFormData = body instanceof FormData;

  const response = await fetch(url, {
    credentials: 'include',
    headers: {
      // Si el body es FormData se omite Content-Type para que el navegador
      // lo fije automáticamente con el boundary multipart correcto.
      ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
      ...(headers as Record<string, string> | undefined),
    },
    body,
    ...rest,
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
};

export const apiClient = async <T,>(endpoint: string, options: RequestInit = {}): Promise<ResponseInterface<T>> => {
  try {
    const url = `${BASE_URL}${endpoint}`;
    const headers = options.headers as Record<string, string> | undefined;

    // Adjunta el access token vigente en memoria, si existe.
    const existingToken = AuthStore.getAccessToken();
    const mergedHeaders = {
      ...headers,
      ...(existingToken ? { Authorization: `Bearer ${existingToken}` } : {}),
    };

    const initialOptions: RequestInit = { ...options, headers: mergedHeaders };

    try {
      return await performRequest<T>(url, initialOptions);
    } catch (error) {
      const status = error instanceof Error && 'status' in error && typeof error.status === 'number'
        ? error.status
        : 500;

      // Solo si el access token caducó (401) se renueva la sesión mediante la
      // cookie HttpOnly del refresh token y se reintenta la petición una vez.
      if (status !== 401) {
        throw error;
      }

      await refreshSession();
      if (!AuthStore.getAccessToken()) {
        throw error;
      }

      const retryOptions: RequestInit = {
        ...options,
        headers: {
          ...headers,
          Authorization: `Bearer ${AuthStore.getAccessToken()}`,
        },
      };
      return await performRequest<T>(url, retryOptions);
    }
  } catch (error) {
    const status = error instanceof Error && 'status' in error && typeof error.status === 'number'
      ? error.status
      : 500;
    const message = error instanceof Error ? error.message : 'Unknown error';

    console.error('Error in apiClient:', message);
    throw buildErrorResponse(message, status);
  }
};
