import { Render } from './render';
import { CartStore, CART_OPEN_EVENT, CART_UPDATED_EVENT } from '../../store/cartStore';
import { toast } from '../../utils/toast';
import type { CartItemResponseApi } from '../../interfaces/cartResponseApi';

/**
 * Web Component del panel lateral (drawer) del carrito de compras.
 *
 * Se renderiza como un panel fijo al borde derecho (1/3 del ancho en
 * desktop y 100% en móvil) con un overlay que lo oscurece. Se abre con el
 * evento `cart-open`, se actualiza con el evento `cart-updated` (ambos en
 * `window`) y delega sus acciones (aumentar, eliminar ítem, vaciar) en el
 * {@link CartStore}.
 */
export class CartDrawer extends HTMLElement {
    private isOpen = false;

    /**
     * Estado de apertura del último render. Permite detectar cuándo cambia
     * el estado para animar la entrada desde la derecha; si solo cambia el
     * contenido del carrito (con el drawer ya abierto) no se re-anima.
     */
    private wasOpen = false;

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    /**
     * Se ejecuta cuando el componente se inserta en el DOM.
     */
    connectedCallback() {
        this.render();
        window.addEventListener(CART_OPEN_EVENT, this.handleOpenEvent);
        window.addEventListener(CART_UPDATED_EVENT, this.handleUpdatedEvent);
        this.shadowRoot?.addEventListener('click', this.handleClick);
    }

    /**
     * Se ejecuta cuando el componente se retira del DOM.
     */
    disconnectedCallback() {
        window.removeEventListener(CART_OPEN_EVENT, this.handleOpenEvent);
        window.removeEventListener(CART_UPDATED_EVENT, this.handleUpdatedEvent);
        this.shadowRoot?.removeEventListener('click', this.handleClick);
    }

    /**
     * Abre el drawer al recibir el evento `cart-open`.
     */
    private handleOpenEvent = (): void => {
        this.isOpen = true;
        this.render();
    };

    /**
     * Re-renderiza con los datos actualizados del carrito.
     */
    private handleUpdatedEvent = (): void => {
        this.render();
    };

    /**
     * Gestiona los clics sobre los botones y el overlay dentro del drawer.
     *
     * @param event evento de clic dentro del shadow root.
     */
    private handleClick = (event: Event): void => {
        const target = event.target as HTMLElement;

        if (target.closest('[data-action="close"]') || target.closest('[data-overlay]')) {
            this.isOpen = false;
            this.render();
            return;
        }

        const item = target.closest<HTMLElement>('[data-item-id]');
        const actionBtn = target.closest<HTMLButtonElement>('[data-action]');
        if (!actionBtn) {
            return;
        }

        const action = actionBtn.dataset.action;
        const cartId = CartStore.getCartId();

        if (cartId === null) {
            return;
        }

        if (action === 'increase' && item) {
            const productId = this.productIdOf(item.dataset.itemId);
            if (productId !== undefined) {
                void CartStore.addItem(productId);
            }
        } else if (action === 'decrease' && item) {
            const itemId = Number(item.dataset.itemId);
            if (Number.isInteger(itemId)) {
                void CartStore.decrementItem(itemId);
            }
        } else if (action === 'checkout') {
            void this.handleCheckout();
        } else if (action === 'clear') {
            void CartStore.clear();
        }
    };

    /**
     * Concreta la compra del carrito y notifica el resultado mediante un
     * toast de éxito o error.
     *
     * El drawer se cierra de inmediato al pulsar "Finalizar compra" y solo se
     * vuelve a abrir si la operación falla; así se evita el "flicker" de un
     * carrito vacío visible antes de ocultarse. Tras el éxito, el toast queda
     * en pantalla con el carrito cerrado.
     */
    private async handleCheckout(): Promise<void> {
        this.isOpen = false;
        this.render();

        if (await CartStore.checkout()) {
            toast('La venta se ha realizado exitosamente', 'success');
        } else {
            this.isOpen = true;
            this.render();
            toast('No se pudo completar la venta.', 'error');
        }
    }

    /**
     * Obtiene el id de producto asociado a un id de ítem del carrito.
     *
     * @param itemIdStr id (string) del ítem del carrito.
     * @returns id del producto, o `undefined` si no se encuentra.
     */
    private productIdOf(itemIdStr: string | undefined): number | undefined {
        const itemId = Number(itemIdStr);
        if (!Number.isInteger(itemId)) {
            return undefined;
        }
        const item = CartStore.getItems().find((it) => it.id === itemId);
        return item?.productId;
    }

    /**
     * Genera el HTML y los estilos del drawer dentro de su shadow DOM.
     */
    render() {
        const root: ShadowRoot | null = this.shadowRoot;
        if (!root) {
            return;
        }

        const items: CartItemResponseApi[] = CartStore.getItems();
        const subTotal: number = CartStore.getSubTotal();
        const count: number = CartStore.getItemCount();
        const ready: boolean = CartStore.isReady();

        const openChanged: boolean = this.isOpen !== this.wasOpen;
        this.wasOpen = this.isOpen;

        const render = new Render(root, items, subTotal, count, this.isOpen, ready);
        render.render(openChanged);
    }
}

if (!customElements.get('cart-drawer')) {
    customElements.define('cart-drawer', CartDrawer);
}
