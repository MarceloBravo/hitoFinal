/**
 * Cuerpo de petición para registrar o actualizar una categoría (CategoryRequestDto).
 */
export interface CategoryRequestInterface {
    /** Nombre de la categoría. */
    name: string;
    /** Estado de activación de la categoría. */
    active: boolean;
}
