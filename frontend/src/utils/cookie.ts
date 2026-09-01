/**
 * Helpers para el manejo de cookies del navegador.
 *
 * Se usan para persistir el identificador del carrito (`cart_id`) entre
 * recargas sin requerir autenticación (flujo de compra como invitado).
 */

/** Nombre de la cookie que guarda el identificador del carrito del invitado. */
export const CART_ID_COOKIE = 'cart_id';

/**
 * Lee el valor de una cookie por su nombre.
 *
 * @param name nombre de la cookie a leer.
 * @returns el valor de la cookie, o `null` si no existe.
 */
export const getCookie = (name: string): string | null => {
    const match = document.cookie
        .split('; ')
        .find((row) => row.startsWith(`${name}=`));
    return match ? decodeURIComponent(match.split('=')[1]) : null;
};

/**
 * Escribe (o actualiza) una cookie con expiración en número de días.
 *
 * @param name   nombre de la cookie.
 * @param value  valor a guardar.
 * @param days   días de validez (por defecto 30).
 * @param path   ruta para la que es válida (por defecto '/').
 */
export const setCookie = (name: string, value: string, days = 30, path = '/'): void => {
    const expires = new Date();
    expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
    document.cookie = `${name}=${encodeURIComponent(value)}; expires=${expires.toUTCString()}; path=${path}`;
};

/**
 * Elimina una cookie.
 *
 * @param name nombre de la cookie a eliminar.
 */
export const removeCookie = (name: string, path = '/'): void => {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=${path}`;
};

/**
 * Lee el identificador del carrito persistido en la cookie.
 *
 * @returns el id del carrito, o `null` si no existe.
 */
export const getCartId = (): number | null => {
    const raw = getCookie(CART_ID_COOKIE);
    if (!raw) {
        return null;
    }
    const parsed = Number(raw);
    return Number.isInteger(parsed) && parsed > 0 ? parsed : null;
};

/**
 * Guarda el identificador del carrito en la cookie.
 *
 * @param id identificador del carrito a persistir.
 */
export const setCartId = (id: number): void => {
    setCookie(CART_ID_COOKIE, String(id));
};

/**
 * Elimina el identificador del carrito de la cookie.
 */
export const clearCartId = (): void => {
    removeCookie(CART_ID_COOKIE);
};
