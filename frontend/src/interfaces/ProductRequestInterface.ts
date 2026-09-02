/**
 * Cuerpo de petición para registrar o actualizar un producto (ProductRequestDto).
 */
export interface ProductRequestInterface {
    /** Identificador de la marca. */
    markId: number;
    /** Identificadores de las categorías. */
    categoryIds: number[];
    /** Nombre del producto. */
    name: string;
    /** Descripción del producto. */
    description: string;
    /** Unidades en stock. */
    stock: number;
    /** Peso en kilogramos. */
    weight: number;
    /** Precio de costo. */
    priceCost: number;
    /** Precio de venta. */
    priceSale: number;
}
