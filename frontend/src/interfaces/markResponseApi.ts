import type { MarkInterface } from './MarkInterface';

/**
 * Contrato de la respuesta de una marca devuelta por la API
 * (ApiResponse<MarkResponseDto>).
 */
export interface MarkResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Marca obtenida. */
    data: MarkInterface | null;
}
