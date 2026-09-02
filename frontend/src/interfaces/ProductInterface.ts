/**
 * Producto obtenido desde el backend (ProductResponseDto).
 */
export interface ProductInterface {
    /** Identificador del producto. */
    id: number;
    /** Identificador de la marca. */
    markId: number;
    /** Nombre de la marca. */
    markName: string;
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
    /** Ubicación de la imagen del producto. */
    imagePath: string | null;
}
