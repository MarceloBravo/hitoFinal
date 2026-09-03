import '../../../components';
import { Template } from './template';
import { ProductService } from '../../../services/productService';
import { confirm, alertMessage } from '../../../utils/dialog';
import type { ProductInterface } from '../../../interfaces/ProductInterface';

const PAGE_SIZE = 8;

/**
 * Web Component de la página del mantenedor de productos (listado).
 *
 * Muestra una grilla paginada de productos con búsqueda por nombre y botones
 * de acción (editar/eliminar) por fila. Usa Light DOM y la paginación
 * client-side a partir del listado completo entregado por la API.
 */
export class ProductsList extends HTMLElement {

    /** Todos los productos cargados desde la API. */
    private products: ProductInterface[] = [];
    /** Productos tras aplicar el filtro de búsqueda. */
    private filtered: ProductInterface[] = [];
    /** Texto de búsqueda vigente. */
    private searchTerm = '';
    /** Página activa (1-indexada). */
    private activePage = 1;
    /** Cantidad total de páginas. */
    private totalPages = 1;

    static get observedAttributes() {
        return ['title'];
    }

    connectedCallback() {
        this.classList.add('products-list-page');
        this.configurePersistentListeners();
        void this.loadProducts();
    }

    private configurePersistentListeners(): void {
        this.addEventListener('input', this.handleSearchInput);
        this.addEventListener('click', this.handleClick);
        this.addEventListener('page-change', this.handlePageChange);
    }

    private handleSearchInput = (event: Event): void => {
        const input = event.target as HTMLElement | null;
        if (!input || input.id !== 'search-products') {
            return;
        }
        this.searchTerm = (input as HTMLInputElement).value;
        this.activePage = 1;
        this.applyFilter();
    };

    private handleClick = (event: MouseEvent): void => {
        const button = (event.target as HTMLElement).closest<HTMLButtonElement>('[data-action]');
        if (!button) {
            return;
        }

        const id = Number(button.dataset.id);
        if (button.dataset.action === 'edit') {
            this.navigate(`/admin_home/products/edit/${id}`);
        } else if (button.dataset.action === 'delete') {
            void this.deleteProduct(id);
        }
    };

    private handlePageChange = (event: Event): void => {
        const page = Number((event as CustomEvent).detail?.page);
        if (Number.isNaN(page) || page < 1) {
            return;
        }
        this.activePage = page;
        this.renderTable();
    };

    private async loadProducts(): Promise<void> {
        const response = await ProductService.getAllForAdmin();

        if (response.ok && response.data) {
            this.products = response.data.products;
        } else {
            this.products = [];
        }

        this.activePage = 1;
        this.searchTerm = '';
        this.applyFilter();
    }

    private applyFilter(): void {
        const term = this.searchTerm.trim().toLowerCase();
        this.filtered = term
            ? this.products.filter((p) => p.name.toLowerCase().includes(term))
            : this.products;

        this.totalPages = Math.max(1, Math.ceil(this.filtered.length / PAGE_SIZE));
        if (this.activePage > this.totalPages) {
            this.activePage = this.totalPages;
        }

        const template = new Template(this);
        if (this.querySelector('#products-tbody')) {
            this.renderTable();
        } else {
            template.render(this.currentPageProducts(), this.totalPages, this.activePage);
            this.restoreSearchValue();
        }
    }

    private renderTable(): void {
        const tbody = this.querySelector<HTMLElement>('#products-tbody');
        if (tbody) {
            tbody.innerHTML = (new Template(this)).rowsHtml(this.currentPageProducts());
        }

        const pagination = this.querySelector('pagination-nav');
        if (pagination) {
            pagination.setAttribute('total-pages', String(this.totalPages));
            pagination.setAttribute('active-page', String(this.activePage));
        }
    }

    private restoreSearchValue(): void {
        const searchInput = this.querySelector<HTMLInputElement>('#search-products');
        if (searchInput) {
            searchInput.value = this.searchTerm;
        }
    }

    private currentPageProducts(): ProductInterface[] {
        const start = (this.activePage - 1) * PAGE_SIZE;
        return this.filtered.slice(start, start + PAGE_SIZE);
    }

    private navigate(path: string): void {
        window.history.pushState({}, '', path);
        window.dispatchEvent(new PopStateEvent('popstate'));
    }

    private async deleteProduct(id: number): Promise<void> {
        const product = this.products.find((p) => p.id === id);
        const confirmMessage = product
            ? `¿Estás seguro de eliminar el producto "${product.name}"?`
            : '¿Estás seguro de eliminar este producto?';

        if (!(await confirm(confirmMessage))) {
            return;
        }

        const response = await ProductService.delete(id);

        if (response.ok) {
            await this.loadProducts();
        } else {
            await alertMessage(response.data || 'No se pudo eliminar el producto.');
        }
    }
}

if (!customElements.get('products-list-page')) {
    customElements.define('products-list-page', ProductsList);
}
