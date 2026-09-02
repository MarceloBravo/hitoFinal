import type { UserInterface } from './UserInterface';

/**
 * Contrato de la respuesta de usuarios devuelta por la API
 * (ApiResponse<List<UserResponseDto>>).
 */
export interface UsersResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Lista de usuarios obtenidos. */
    data: UserInterface[];
}
