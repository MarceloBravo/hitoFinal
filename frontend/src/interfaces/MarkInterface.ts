/**
 * Marca obtenida desde el backend (MarkResponseDto).
 */
export interface MarkInterface {
    /** Identificador de la marca. */
    id: number;
    /** Nombre de la marca. */
    name: string;
    /** Indica si la marca está activa. */
    active: boolean;
}
