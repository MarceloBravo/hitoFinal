import { apiClient } from '../api/apiClient.js';
import type { CartResponseApi } from '../interfaces/cartResponseApi.js';
import type { CheckoutResponseApi } from '../interfaces/checkoutResponseApi.js';
import type { ResponseInterface } from '../interfaces/responseInterface.js';
import { handleError } from '../utils/errorHandler.js';

/**
 * URI base de los endpoints de carritos de la API.
 */
const URI = '/carts';

/**
 * Servicio encargado de consumir los endpoints de carritos de compras.
 *
 * El carrito se identifica con un `id` persistido en el navegador (cookie),
 * por lo que no se requiere autenticación (flujo de compra como invitado).
 */
export class CartService {

    /**
     * Crea un carrito de compras vacío.
     *
     * @returns respuesta normalizada con el carrito creado o el error ocurrido.
     */
    static createCart = async (): Promise<ResponseInterface<CartResponseApi>> => {
        try {
            return await apiClient<CartResponseApi>(URI, { method: 'POST' });
        } catch (error) {
            return handleError<CartResponseApi>(error, 'No se pudo crear el carrito');
        }
    };

    /**
     * Obtiene un carrito por su identificador.
     *
     * @param id identificador del carrito.
     * @returns respuesta normalizada con el carrito o el error ocurrido.
     */
    static getCart = async (id: number): Promise<ResponseInterface<CartResponseApi>> => {
        try {
            return await apiClient<CartResponseApi>(`${URI}/${id}`);
        } catch (error) {
            return handleError<CartResponseApi>(error, `No se pudo cargar el carrito con id ${id}`);
        }
    };

    /**
     * Agrega un producto a un carrito con la cantidad indicada.
     *
     * @param cartId    identificador del carrito.
     * @param productId identificador del producto a agregar.
     * @param quantity  cantidad de unidades (por defecto 1).
     * @returns respuesta normalizada con el carrito actualizado o el error ocurrido.
     */
    static addItem = async (cartId: number, productId: number, quantity = 1): Promise<ResponseInterface<CartResponseApi>> => {
        try {
            return await apiClient<CartResponseApi>(`${URI}/${cartId}/items`, {
                method: 'POST',
                body: JSON.stringify({ productId, quantity }),
            });
        } catch (error) {
            return handleError<CartResponseApi>(error, 'No se pudo agregar el producto al carrito');
        }
    };

    /**
     * Elimina un ítem específico de un carrito.
     *
     * @param cartId identificador del carrito.
     * @param itemId identificador del ítem a eliminar.
     * @returns respuesta normalizada con el carrito actualizado o el error ocurrido.
     */
    static removeItem = async (cartId: number, itemId: number): Promise<ResponseInterface<CartResponseApi>> => {
        try {
            return await apiClient<CartResponseApi>(`${URI}/${cartId}/items/${itemId}`, { method: 'DELETE' });
        } catch (error) {
            return handleError<CartResponseApi>(error, 'No se pudo eliminar el producto del carrito');
        }
    };

    /**
     * Disminuye en una unidad la cantidad de un ítem de un carrito.
     *
     * Si la cantidad llegaba a una, el ítem se elimina del carrito.
     *
     * @param cartId identificador del carrito.
     * @param itemId identificador del ítem a disminuir.
     * @returns respuesta normalizada con el carrito actualizado o el error ocurrido.
     */
    static decrementItem = async (cartId: number, itemId: number): Promise<ResponseInterface<CartResponseApi>> => {
        try {
            return await apiClient<CartResponseApi>(`${URI}/${cartId}/items/${itemId}`, { method: 'PATCH' });
        } catch (error) {
            return handleError<CartResponseApi>(error, 'No se pudo disminuir la cantidad del producto');
        }
    };

    /**
     * Ejecuta una compra ficticia: rebaja el stock de los productos y
     * elimina el carrito.
     *
     * @param cartId identificador del carrito a comprar.
     * @returns respuesta normalizada con el resultado de la compra o el error.
     */
    static checkout = async (cartId: number): Promise<ResponseInterface<CheckoutResponseApi>> => {
        try {
            return await apiClient<CheckoutResponseApi>(`${URI}/${cartId}/checkout`, { method: 'POST' });
        } catch (error) {
            return handleError<CheckoutResponseApi>(error, 'No se pudo concretar la compra');
        }
    };

    /**
     * Elimina un carrito completo.
     *
     * @param id identificador del carrito a eliminar.
     * @returns respuesta normalizada (sin datos) o el error ocurrido.
     */
    static deleteCart = async (id: number): Promise<ResponseInterface<CartResponseApi>> => {
        try {
            return await apiClient<CartResponseApi>(`${URI}/${id}`, { method: 'DELETE' });
        } catch (error) {
            return handleError<CartResponseApi>(error, 'No se pudo eliminar el carrito');
        }
    };
}
