/**
 * Categoría obtenida desde el backend (CategoryResponseDto).
 */
export interface CategoriesInterface {
    /** Identificador de la categoría. */
    id: number;
    /** Nombre de la categoría. */
    name: string;
    /** Indica si la categoría está activa. */
    active: boolean;
}
