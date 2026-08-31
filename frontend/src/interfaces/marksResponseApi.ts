import type { MarkInterface } from './MarkInterface';

/**
 * Contrato de la respuesta de marcas devuelta por la API
 * (ApiResponse<List<MarkResponseDto>>).
 */
export interface MarksResponseApi {
    /** Código de estado HTTP de la respuesta. */
    statusCode: number;
    /** Mensaje descriptivo de la operación. */
    message: string;
    /** Lista de marcas obtenidas. */
    data: MarkInterface[];
}
