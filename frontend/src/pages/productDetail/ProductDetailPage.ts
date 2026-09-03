import { Template } from './template';
import { ProductService } from '../../services/productService';
import { CartStore } from '../../store/cartStore';
import type { ProductInterface } from '../../interfaces/ProductInterface';

/**
 * Web Component de la página de detalle de un producto.
 *
 * Lee el identificador desde la ruta `/product/{id}`, lo busca en la API y
 * renderiza la información del producto (incluida su foto) junto con un botón
 * para agregarlo al carrito. Usa Light DOM.
 */
export class ProductDetailPage extends HTMLElement {

    /** Datos del producto cargado, o `null` si no se encontró. */
    private product: ProductInterface | null = null;

    /**
     * Atributos observados para reaccionar a cambios en el DOM.
     */
    static get observedAttributes() {
        return ['title'];
    }

    /**
     * Se ejecuta cuando el componente se inserta en el DOM.
     */
    connectedCallback() {
        this.classList.add('product-detail-page');
        const id = this.resolveIdFromPath();
        if (id === null) {
            this.renderPage();
            return;
        }
        void this.loadProduct(id);
    }

    /**
     * Extrae el identificador del producto desde la ruta `/product/{id}`.
     *
     * @returns El id parseado o `null` si la ruta no lo contiene.
     */
    private resolveIdFromPath(): number | null {
        const segments = window.location.pathname.split('/').filter(Boolean);
        const id = Number(segments[segments.length - 1]);
        return Number.isInteger(id) && id > 0 ? id : null;
    }

    /**
     * Carga el producto desde la API y renderiza la página.
     *
     * @param id Identificador del producto.
     */
    private async loadProduct(id: number): Promise<void> {
        const response = await ProductService.getById(id);

        if (response.ok && response.data?.data) {
            this.product = response.data.data;
        } else {
            this.product = null;
        }

        this.renderPage();
    }

    /**
     * Renderiza la plantilla de detalle y configura el listener del botón
     * "Agregar al carrito".
     */
    private renderPage(): void {
        (new Template(this, this.product)).render();
        this.configListeners();
    }

    /**
     * Configura el listener del botón de "Agregar al carrito" para añadir el
     * producto al carrito una vez se pulsado.
     */
    private configListeners(): void {
        if (!this.product) {
            return;
        }
        const addButton = this.querySelector<HTMLButtonElement>('#detail-add-to-cart');
        addButton?.addEventListener('click', () => {
            void CartStore.addItem(this.product!.id);
        });
    }
}

if (!customElements.get('product-detail-page')) {
    customElements.define('product-detail-page', ProductDetailPage);
}