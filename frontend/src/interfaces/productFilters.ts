/**
 * Estado de los filtros del catálogo de productos aplicables al endpoint
 * de listado. Se persiste en el localStorage para conservar la selección
 * entre recargas y paginación.
 */
export interface ProductFilters {
    /** Identificador de la categoría seleccionada (una sola). */
    categoryId?: number;
    /** Identificador de la marca seleccionada (una sola). */
    markId?: number;
    /** Precio de venta mínimo del rango seleccionado. */
    minPrice?: number;
    /** Precio de venta máximo del rango seleccionado. */
    maxPrice?: number;
}
