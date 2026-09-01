import { CartService } from '../services/cartService';
import { clearCartId, getCartId, setCartId } from '../utils/cookie';
import type { CartItemResponseApi } from '../interfaces/cartResponseApi';
/**
 * Nombre del evento que se despacha en `window` cuando el carrito cambia.
 */
export const CART_UPDATED_EVENT = 'cart-updated';

/**
 * Nombre del evento que solicita la apertura del drawer del carrito.
 */
export const CART_OPEN_EVENT = 'cart-open';

/**
 * Estado reactivo del carrito de compras (singleton).
 *
 * Mantiene en memoria el carrito del invitado identificado por un `cartId`
 * persistido en cookie. Expone operaciones que delegan en {@link CartService}
 * y notifican los cambios a través del evento `cart-updated` en `window`
 * (compatible con el patrón de Web Components del proyecto).
 */
class CartStoreImpl {
    private cartId: number | null = null;
    private items: CartItemResponseApi[] = [];
    private subTotal = 0;

    /** Indica que el carrito aún no está listo (inicialización en curso). */
    private ready = false;

    /**
     * Obtiene el identificador del carrito actual.
     *
     * @returns id del carrito o `null` si aún no se ha iniciado.
     */
    getCartId(): number | null {
        return this.cartId;
    }

    /**
     * Obtiene una copia de los ítems del carrito.
     *
     * @returns lista de ítems del carrito.
     */
    getItems(): CartItemResponseApi[] {
        return [...this.items];
    }

    /**
     * Obtiene el subtotal actual del carrito.
     *
     * @returns subtotal acumulado.
     */
    getSubTotal(): number {
        return this.subTotal;
    }

    /**
     * Obtiene la cantidad total de unidades del carrito (suma de las cantidades).
     *
     * @returns total de unidades.
     */
    getItemCount(): number {
        return this.items.reduce((acc, item) => acc + item.quantity, 0);
    }

    /**
     * Indica si el carrito ya se inicializó.
     *
     * @returns `true` si la inicialización terminó.
     */
    isReady(): boolean {
        return this.ready;
    }

    /**
     * Inicializa el carrito leyendo la cookie y cargándolo desde el backend.
     *
     * Si no existe un `cartId` válido o el carrito no se encuentra, se crea uno
     * nuevo y su id se guarda en la cookie.
     */
    async init(): Promise<void> {
        const storedId = getCartId();
        if (storedId !== null) {
            const response = await CartService.getCart(storedId);
            if (response.ok) {
                this.applyResponse(response.data.data);
                this.notify();
                return;
            }
            // Carrito inexistente o error: se limpia y se crea uno nuevo.
            clearCartId();
        }
        await this.newCart();
    }

    /**
     * Crea un carrito vacío nuevo y lo persiste en la cookie.
     */
    private async newCart(): Promise<void> {
        const response = await CartService.createCart();
        if (response.ok) {
            this.applyResponse(response.data.data);
            this.cartId !== null && setCartId(this.cartId);
        }
        this.ready = true;
        this.notify();
    }

    /**
     * Agrega un producto (una unidad) al carrito.
     *
     * Si no hay un carrito disponible, lo crea antes de agregar.
     *
     * @param productId identificador del producto a agregar.
     * @returns `true` si la operación fue exitosa, `false` en caso de error.
     */
    async addItem(productId: number): Promise<boolean> {
        if (this.cartId === null) {
            await this.newCart();
            if (this.cartId === null) {
                return false;
            }
        }
        const response = await CartService.addItem(this.cartId, productId, 1);
        if (response.ok) {
            this.applyResponse(response.data.data);
            this.notify();
            return true;
        }
        return false;
    }

    /**
     * Elimina un ítem del carrito.
     *
     * @param itemId identificador del ítem a eliminar.
     * @returns `true` si la operación fue exitosa, `false` en caso de error.
     */
    async removeItem(itemId: number): Promise<boolean> {
        if (this.cartId === null) {
            return false;
        }
        const response = await CartService.removeItem(this.cartId, itemId);
        if (response.ok) {
            this.applyResponse(response.data.data);
            this.notify();
            return true;
        }
        return false;
    }

    /**
     * Disminuye en una unidad la cantidad de un ítem del carrito.
     *
     * @param itemId identificador del ítem a disminuir.
     * @returns `true` si la operación fue exitosa, `false` en caso de error.
     */
    async decrementItem(itemId: number): Promise<boolean> {
        if (this.cartId === null) {
            return false;
        }
        const response = await CartService.decrementItem(this.cartId, itemId);
        if (response.ok) {
            this.applyResponse(response.data.data);
            this.notify();
            return true;
        }
        return false;
    }

    /**
     * Vacía el carrito actual: lo elimina del backend y crea uno nuevo vacío.
     */
    async clear(): Promise<void> {
        if (this.cartId !== null) {
            await CartService.deleteCart(this.cartId);
            clearCartId();
            this.cartId = null;
        }
        await this.newCart();
    }

    /**
     * Concreta la compra del carrito actual: rebaja el stock en el backend
     * y abre un carrito nuevo vacío para la siguiente compra.
     *
     * @returns `true` si la compra fue exitosa, `false` en caso de error.
     */
    async checkout(): Promise<boolean> {
        if (this.cartId === null) {
            return false;
        }
        const response = await CartService.checkout(this.cartId);
        if (response.ok) {
            clearCartId();
            this.cartId = null;
            this.items = [];
            this.subTotal = 0;
            await this.newCart();
            return true;
        }
        return false;
    }

    /**
     * Aplica la respuesta de un carrito al estado local.
     */
    private applyResponse(cart: { id: number; items: CartItemResponseApi[]; subTotal: number }): void {
        this.cartId = cart.id;
        this.items = cart.items ?? [];
        this.subTotal = cart.subTotal;
        this.ready = true;
    }

    /**
     * Despacha el evento `cart-updated` en `window` con el estado actual.
     */
    private notify(): void {
        window.dispatchEvent(new CustomEvent(CART_UPDATED_EVENT, {
            detail: {
                cartId: this.cartId,
                items: this.getItems(),
                subTotal: this.subTotal,
                count: this.getItemCount(),
            },
            bubbles: true,
            composed: true,
        }));
    }
}

/**
 * Instancia única del estado del carrito.
 */
export const CartStore = new CartStoreImpl();
