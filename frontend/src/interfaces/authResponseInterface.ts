/**
 * Datos de la sesión devueltos por los endpoints de autenticación.
 *
 * El backend entrega el access token y los datos básicos del usuario
 * autenticado (correo y rol), que permiten al frontend distinguir entre
 * los roles `ADMIN` (todos los mantenedores) y `USER` (todos excepto el
 * mantenedor de usuarios).
 *
 * El refresh token se omite del body a propósito: viaja únicamente en una
 * cookie `HttpOnly` que el backend rota en cada renovación de sesión.
 */
export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  email: string;
  role: string;
}

/**
 * Envoltorio estándar de respuesta de la API.
 *
 * @template T Tipo de dato contenido en el campo `data`.
 */
export interface AuthEnvelope<T> {
  statusCode: number;
  message: string;
  data: T | null;
}