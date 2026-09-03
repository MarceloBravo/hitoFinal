import styles from './style.css?inline';
import type { ProductInterface } from '../../interfaces/ProductInterface';

/**
 * Genera el HTML y los estilos de la página de detalle de producto en el light DOM.
 */
export class Template {
    private host: HTMLElement;
    private product: ProductInterface | null;

    /**
     * @param host    Elemento anfitrión donde se renderiza la plantilla.
     * @param product Datos del producto a mostrar, o {@code null} si no se encontró.
     */
    constructor(host: HTMLElement, product: ProductInterface | null) {
        this.host = host;
        this.product = product;
    }

    /**
     * Construye la URL pública de la imagen del producto.
     *
     * @returns URL de la imagen o del placeholder si no hay imagen disponible.
     */
    private imageUrl(): string {
        const path = this.product?.imagePath;
        if (path) {
            return `${import.meta.env.VITE_IMAGE_URL}${path}`;
        }
        return import.meta.env.VITE_PLACEHOLDER_IMAGE;
    }

    /**
     * Renderiza la página de detalle: si no hay producto muestra un error;
     * en caso contrario, los datos del producto incluyendo su foto y un botón
     * para agregarlo al carrito.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render() {
        const htmlString = this.product ? this.buildDetail() : this.buildNotFound();
        const fragment = document.createRange().createContextualFragment(htmlString);
        const style = document.createElement('style');
        style.textContent = styles;

        this.host.replaceChildren(style, fragment);

        return this.host;
    }

    /**
     * Construye el HTML del detalle del producto.
     *
     * @returns HTML del detalle con imagen, datos y botón de carrito.
     */
    private buildDetail(): string {
        const p = this.product!;
        const price = new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(p.priceSale);
        return `
            <div class="product-detail__container">
                <a href="/home" data-link class="product-detail__back">← Volver a la tienda</a>
                <div class="product-detail__layout">
                    <div class="product-detail__image-wrap">
                        <img class="product-detail__image" src="${this.imageUrl()}" alt="${this.escapeHtml(p.name)}"/>
                    </div>
                    <div class="product-detail__info">
                        <h2 class="product-detail__title">${this.escapeHtml(p.name)}</h2>
                        ${p.markName ? `<p class="product-detail__mark">Marca: ${this.escapeHtml(p.markName)}</p>` : ''}
                        <p class="product-detail__description">${this.escapeHtml(p.description)}</p>
                        <p class="product-detail__price">${price}</p>
                        <div class="product-detail__meta">
                            <span class="product-detail__badge ${p.stock > 0 ? 'product-detail__badge--available' : 'product-detail__badge--out'}">
                                ${p.stock > 0 ? `Stock disponible: ${p.stock} u.` : 'Sin stock'}
                            </span>
                            <span class="product-detail__badge product-detail__badge--neutral">Peso: ${p.weight} kg</span>
                        </div>
                        <button type="button" class="product-detail__add" id="detail-add-to-cart"
                            ${p.stock > 0 ? '' : 'disabled'}>Agregar al carrito</button>
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * Construye el HTML cuando no se encuentra el producto.
     *
     * @returns HTML del mensaje de producto no encontrado.
     */
    private buildNotFound(): string {
        return `
            <div class="product-detail__container">
                <a href="/home" data-link class="product-detail__back">← Volver a la tienda</a>
                <div class="product-detail__not-found">
                    <p>No se pudo cargar el producto solicitado.</p>
                </div>
            </div>
        `;
    }

    /**
     * Escapa caracteres especiales del HTML para evitar inyección.
     *
     * @param value Texto a escapar.
     * @returns Texto seguro para insertar en el HTML.
     */
    private escapeHtml(value: string): string {
        return value
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }
}