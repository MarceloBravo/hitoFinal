import styles from './style.css?inline';
import type { ProductInterface } from '../../../interfaces/ProductInterface';

const BASE_URL = import.meta.env.VITE_IMAGE_URL;

/**
 * Genera el HTML y los estilos del listado de productos en el light DOM.
 */
export class Template {
    private host: HTMLElement;

    constructor(host: HTMLElement) {
        this.host = host;
    }

    render(products: ProductInterface[], totalPages: number, activePage: number) {
        const htmlString: string = `
            <div class="products-list">
                <div class="products-list__header">
                    <h2 class="products-list__title">Productos</h2>
                    <a href="/admin_home/products/new" data-link>
                        <button type="button" class="products-list__new-btn" id="btn-new">+ Nuevo Producto</button>
                    </a>
                    <div class="products-list__search">
                        <input type="text" id="search-products" class="products-list__search-input" placeholder="Buscar por nombre..." aria-label="Buscar productos" />
                    </div>
                </div>
                <div class="products-list__table-wrap">
                    <table class="products-list__table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Imagen</th>
                                <th>Nombre</th>
                                <th>Marca</th>
                                <th>Precio</th>
                                <th>Stock</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="products-tbody">${this.rowsHtml(products)}</tbody>
                    </table>
                </div>
                <pagination-nav total-pages="${totalPages}" active-page="${activePage}"></pagination-nav>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        const style = document.createElement('style');
        style.textContent = styles;

        this.host.replaceChildren(style, fragment);

        return this.host;
    }

    rowsHtml(products: ProductInterface[]): string {
        if (!products.length) {
            return `<tr><td colspan="7" class="products-list__empty">No se encontraron productos.</td></tr>`;
        }

        return products
            .map((product) => {
                const imgSrc = this.resolveImagePath(product.imagePath);
                const imgHtml = imgSrc
                    ? `<img src="${imgSrc}" alt="${this.escapeHtml(product.name)}" class="products-list__thumb" />`
                    : `<span class="products-list__no-img">Sin imagen</span>`;

                return `
                <tr>
                    <td>${product.id}</td>
                    <td>${imgHtml}</td>
                    <td>${this.escapeHtml(product.name)}</td>
                    <td>${this.escapeHtml(product.markName)}</td>
                    <td>$${product.priceSale.toLocaleString('es-CL')}</td>
                    <td>${product.stock}</td>
                    <td class="products-list__actions">
                        <button class="products-list__icon-btn" type="button" data-action="edit" data-id="${product.id}" title="Editar" aria-label="Editar ${this.escapeHtml(product.name)}">✎</button>
                        <button class="products-list__icon-btn products-list__icon-btn--danger" type="button" data-action="delete" data-id="${product.id}" title="Eliminar" aria-label="Eliminar ${this.escapeHtml(product.name)}">🗑</button>
                    </td>
                </tr>`;
            })
            .join('');
    }

    private resolveImagePath(imagePath: string | null): string | null {
        if (!imagePath) {
            return null;
        }
        if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
            return imagePath;
        }
        return `${BASE_URL}${imagePath}`;
    }

    private escapeHtml(value: string): string {
        return value
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }
}
