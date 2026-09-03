import styles from './style.css?inline';
import type { CartItemResponseApi } from '../../interfaces/cartResponseApi';

/**
 * Formatea un número como precio en pesos chilenos (sin decimales).
 *
 * @param value valor numérico a formatear.
 * @returns cadena formateada, p. ej. "$1.250.000".
 */
const formatPrice = (value: number): string => `$${Math.round(value).toLocaleString('es-CL')}`;

/**
 * Genera el HTML y aplica los estilos del componente `cart-drawer`
 * dentro de su shadow DOM.
 *
 * Dado que el carrito puede estar vacío, se muestra un mensaje y se
 * ocultan las acciones. Cada ítem permite aumentar cantidad y eliminarlo;
 * además hay una acción para vaciar el carrito completo.
 */
export class Render {
    private root: ShadowRoot;
    private items: CartItemResponseApi[];
    private subTotal: number;
    private count: number;
    private open: boolean;
    private ready: boolean;

    /**
     * @param root    Shadow root del componente donde se renderiza.
     * @param items   Ítems actuales del carrito.
     * @param subTotal Subtotal del carrito.
     * @param count   Cantidad total de unidades.
     * @param open    Indica si el drawer está abierto.
     * @param ready   Indica si el carrito ya se inicializó.
     */
    constructor(root: ShadowRoot, items: CartItemResponseApi[], subTotal: number, count: number, open: boolean, ready: boolean) {
        this.root = root;
        this.items = items;
        this.subTotal = subTotal;
        this.count = count;
        this.open = open;
        this.ready = ready;
    }

    /**
     * Construye el HTML del drawer, lo inserta en el shadow root
     * y adjunta los estilos del componente.
     *
     * Si el estado de apertura cambió, el elemento se inserta en posición
     * cerrada y la clase `open` se aplica en el frame siguiente; así el
     * navegador pinta el estado inicial y la transición CSS anima la
     * entrada desde la derecha. Si solo cambió el contenido (drawer ya
     * abierto), se conserva la clase para no re-animar.
     *
     * @param animate Indica si debe aplicarse la transición de apertura.
     * @returns El shadow root con el contenido renderizado.
     */
    render(animate = false) {
        const isEmpty = this.items.length === 0;
        const bodyHtml = isEmpty
            ? `<p class="empty-msg">${this.ready ? 'Tu carrito está vacío' : 'Cargando carrito…'}</p>`
            : `
                <ul class="cart-items">
                    ${this.items.map((item) => `
                        <li class="cart-item" data-item-id="${item.id}">
                            <div class="item-info">
                                <span class="item-name">${item.productName}</span>
                                <span class="item-price">${formatPrice(item.subTotal)}</span>
                            </div>
                            <div class="item-actions">
                                <button class="qty-btn" type="button" data-action="increase" aria-label="Aumentar cantidad">+</button>
                                <span class="item-qty">${item.quantity}</span>
                                <button class="qty-btn" type="button" data-action="decrease" aria-label="Disminuir cantidad">−</button>
                            </div>
                        </li>
                    `).join('')}
                </ul>
                <div class="cart-footer">
                    <div class="cart-total">
                        <span>Total</span>
                        <strong>${formatPrice(this.subTotal)}</strong>
                    </div>
                    <button class="checkout-btn" type="button" data-action="checkout">Finalizar compra</button>
                    <button class="clear-btn" type="button" data-action="clear">Vaciar carrito</button>
                </div>
            `;

        // Al animar la apertura, el panel se dibuja cerrado (sin `open`) y la clase
        // se agrega en el frame siguiente; al animar el cierre, se dibuja abierto
        // (`open`) y se retira en el frame siguiente. Así el navegador pinta el
        // estado de partida y la transición CSS desplaza el panel lateralmente.
        const startingOpen: boolean = !animate ? this.open : false;
        const animateClose: boolean = animate && !this.open;
        const showOpen: boolean = startingOpen || animateClose;
        const htmlString: string = `
            <div class="drawer-overlay ${showOpen ? 'open' : ''}" data-overlay></div>
            <aside class="drawer-panel ${showOpen ? 'open' : ''}">
                <header class="drawer-header">
                    <h2>Tu carrito</h2>
                    <span class="drawer-count">${this.count} ${this.count === 1 ? 'artículo' : 'artículos'}</span>
                    <button class="close-btn" type="button" data-action="close" aria-label="Cerrar carrito">✕</button>
                </header>
                <div class="drawer-body">
                    ${bodyHtml}
                </div>
            </aside>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        this.root.replaceChildren(fragment);

        const style = document.createElement('style');
        style.textContent = styles;
        this.root.appendChild(style);

        if (animate && this.open) {
            // Entrada: se presiona el estado cerrado en el frame siguiente (doble
            // requestAnimationFrame) para que el navegador pinte el panel fuera de
            // pantalla antes de animarlo hacia su posición final.
            requestAnimationFrame(() => {
                requestAnimationFrame(() => {
                    this.root.querySelector('.drawer-panel')?.classList.add('open');
                    this.root.querySelector('.drawer-overlay')?.classList.add('open');
                });
            });
        } else if (animate && !this.open) {
            // Salida: se presiona el estado abierto, y en el frame siguiente se
            // retira la clase. La transición CSS desplaza el panel hacia la derecha.
            requestAnimationFrame(() => {
                requestAnimationFrame(() => {
                    this.root.querySelector('.drawer-panel')?.classList.remove('open');
                    this.root.querySelector('.drawer-overlay')?.classList.remove('open');
                });
            });
        }

        return this.root;
    }
}
