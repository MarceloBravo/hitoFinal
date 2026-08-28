import type { ApiError, ResponseInterface } from "../interfaces/responseInterface";

/**
 * Normaliza un error desconocido en una `ResponseInterface<T>` de error.
 *
 * Si el error ya es un `ApiError` lo devuelve tal cual; en caso contrario
 * extrae el código de estado y el mensaje cuando es posible.
 *
 * @template T Tipo de dato de la respuesta en caso de éxito.
 * @param error           Error lanzado (puede ser cualquier valor).
 * @param fallbackMessage Mensaje por defecto si el error no trae texto.
 * @returns Respuesta normalizada con `ok: false`.
 */
export const handleError = <T,>(error: unknown, fallbackMessage: string): ResponseInterface<T> => {
    if (typeof error === 'object' && error !== null && 'ok' in error && 'status' in error && 'data' in error) {
        return error as ApiError;
    }

    const status = typeof error === 'object' && error !== null && 'status' in error && typeof (error as { status?: unknown }).status === 'number'
      ? (error as { status: number }).status
      : 500;

    const message = error instanceof Error ? error.message : fallbackMessage;

    return {
      data: message,
      ok: false,
      status,
    };
  }
