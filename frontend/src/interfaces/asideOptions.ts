/**
 * Representa una opción configurable del panel de filtros laterales.
 */
export interface AsideOptions {
    /** Identificador del elemento (categoría o marca), cuando aplica. */
    id?: number;
    /** Clave interna de la opción para identificar filtros sin id (ej. rangos de precio). */
    value?: string;
    /** Límite inferior del rango de precio, cuando la opción es de precios. */
    priceMin?: number;
    /** Límite superior del rango de precio, cuando la opción es de precios. */
    priceMax?: number;
    /** Nombre visible de la opción. */
    label: string;
    /** Tipo de control del input (checkbox o radio). */
    type: 'checkbox' | 'radio';
    /** Indica si la opción está seleccionada por defecto. */
    checked?: boolean
}
