/**
 * Contrato de una respuesta exitosa de la API.
 */
export interface ApiSuccess<T> {
  data: T;
  ok: true;
  status: number;
}

/**
 * Contrato de una respuesta de error de la API.
 */
export interface ApiError {
  data: string;
  ok: false;
  status: number;
}

/**
 * Respuesta normalizada de la API: éxito con datos tipados o error con mensaje.
 */
export type ResponseInterface<T = unknown> = ApiSuccess<T> | ApiError;
