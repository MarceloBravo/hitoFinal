/**
 * Cuerpo de petición para registrar o actualizar una marca (MarkRequestDto).
 */
export interface MarkRequestInterface {
    /** Nombre de la marca. */
    name: string;
    /** Estado de activación de la marca. */
    active: boolean;
}
