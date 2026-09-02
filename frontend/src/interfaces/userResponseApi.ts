import type { UserInterface } from './UserInterface';

/**
 * Contrato de la respuesta de un usuario devuelta por la API
 * (ApiResponse<UserResponseDto>).
 */
export interface UserResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Usuario obtenido. */
    data: UserInterface | null;
}
