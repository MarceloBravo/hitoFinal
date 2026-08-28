/**
 * Representa una opción configurable del panel de filtros laterales.
 */
export interface AsideOptions {
    /** Nombre visible de la opción. */
    label: string;
    /** Tipo de control del input (checkbox o radio). */
    type: 'checkbox' | 'radio';
    /** Indica si la opción está seleccionada por defecto. */
    checked?: boolean
}