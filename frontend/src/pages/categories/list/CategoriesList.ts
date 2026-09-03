import '../../../components';
import { Template } from './template';
import { categoriesService } from '../../../services/categoriesService';
import { confirm, alertMessage } from '../../../utils/dialog';
import type { CategoriesInterface } from '../../../interfaces/CategoriesInterface';

const PAGE_SIZE = 8;

/**
 * Web Component de la página del mantenedor de categorías (listado).
 *
 * Muestra una grilla paginada de categorías con búsqueda por nombre y botones
 * de acción (editar/eliminar) por fila. Usa Light DOM y la paginación
 * client-side a partir del listado completo entregado por la API.
 *
 * Los listeners de la grilla, la búsqueda y la paginación se registran una
 * sola vez sobre el host; los cambios de página y de término de búsqueda
 * actualizan únicamente el cuerpo de la tabla y el componente de paginación,
 * preservando el foco del campo de búsqueda.
 */
export class CategoriesList extends HTMLElement {

    /** Todas las categorías cargadas desde la API. */
    private categories: CategoriesInterface[] = [];
    /** Categorías tras aplicar el filtro de búsqueda. */
    private filtered: CategoriesInterface[] = [];
    /** Texto de búsqueda vigente. */
    private searchTerm = '';
    /** Página activa (1-indexada). */
    private activePage = 1;
    /** Cantidad total de páginas. */
    private totalPages = 1;

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
        this.classList.add('categories-list-page');
        this.configurePersistentListeners();
        void this.loadCategories();
    }

    /**
     * Configura los listeners que sobreviven a los re-renderizados del host.
     */
    private configurePersistentListeners(): void {
        this.addEventListener('input', this.handleSearchInput);
        this.addEventListener('click', this.handleClick);
        this.addEventListener('page-change', this.handlePageChange);
    }

    /**
     * Maneja el evento `input` del campo de búsqueda.
     *
     * @param event Evento de entrada.
     */
    private handleSearchInput = (event: Event): void => {
        const input = event.target as HTMLElement | null;
        if (!input || input.id !== 'search-categories') {
            return;
        }
        this.searchTerm = (input as HTMLInputElement).value;
        this.activePage = 1;
        this.applyFilter();
    };

    /**
     * Maneja los clics sobre la grilla (editar/eliminar).
     *
     * @param event Evento de clic.
     */
    private handleClick = (event: MouseEvent): void => {
        const button = (event.target as HTMLElement).closest<HTMLButtonElement>('[data-action]');
        if (!button) {
            return;
        }

        const id = Number(button.dataset.id);
        if (button.dataset.action === 'edit') {
            this.navigate(`/admin_home/categories/edit/${id}`);
        } else if (button.dataset.action === 'delete') {
            void this.deleteCategory(id);
        }
    };

    /**
     * Maneja el evento `page-change` del componente de paginación.
     *
     * @param event Evento con el detalle de la página seleccionada.
     */
    private handlePageChange = (event: Event): void => {
        const page = Number((event as CustomEvent).detail?.page);
        if (Number.isNaN(page) || page < 1) {
            return;
        }
        this.activePage = page;
        this.renderTable();
    };

    /**
     * Carga el listado completo de categorías y actualiza la grilla.
     */
    private async loadCategories(): Promise<void> {
        const response = await categoriesService.getAll();

        if (response.ok && response.data) {
            this.categories = response.data.data;
        } else {
            this.categories = [];
        }

        this.activePage = 1;
        this.searchTerm = '';
        this.applyFilter();
    }

    /**
     * Aplica el filtro de búsqueda, recalcula la paginación y renderiza.
     *
     * En la primera renderización se construye toda la grilla; en las
     * siguientes actualiza únicamente las filas y la paginación.
     */
    private applyFilter(): void {
        const term = this.searchTerm.trim().toLowerCase();
        this.filtered = term
            ? this.categories.filter((category) => category.name.toLowerCase().includes(term))
            : this.categories;

        this.totalPages = Math.max(1, Math.ceil(this.filtered.length / PAGE_SIZE));
        if (this.activePage > this.totalPages) {
            this.activePage = this.totalPages;
        }

        const template = new Template(this);
        if (this.querySelector('#categories-tbody')) {
            this.renderTable();
        } else {
            template.render(this.currentPageCategories(), this.totalPages, this.activePage);
            this.restoreSearchValue();
        }
    }

    /**
     * Actualiza únicamente las filas de la tabla y la paginación vigente.
     */
    private renderTable(): void {
        const tbody = this.querySelector<HTMLElement>('#categories-tbody');
        if (tbody) {
            tbody.innerHTML = (new Template(this)).rowsHtml(this.currentPageCategories());
        }

        const pagination = this.querySelector('pagination-nav');
        if (pagination) {
            pagination.setAttribute('total-pages', String(this.totalPages));
            pagination.setAttribute('active-page', String(this.activePage));
        }
    }

    /**
     * Restaura el valor del campo de búsqueda tras una renderización completa.
     */
    private restoreSearchValue(): void {
        const searchInput = this.querySelector<HTMLInputElement>('#search-categories');
        if (searchInput) {
            searchInput.value = this.searchTerm;
        }
    }

    /**
     * Obtiene las categorías correspondientes a la página activa.
     *
     * @returns Categorías de la página vigente.
     */
    private currentPageCategories(): CategoriesInterface[] {
        const start = (this.activePage - 1) * PAGE_SIZE;
        return this.filtered.slice(start, start + PAGE_SIZE);
    }

    /**
     * Navega hacia la ruta entregada.
     *
     * @param path Ruta de destino.
     */
    private navigate(path: string): void {
        window.history.pushState({}, '', path);
        window.dispatchEvent(new PopStateEvent('popstate'));
    }

    /**
     * Elimina una categoría tras confirmación del usuario.
     *
     * @param id Identificador de la categoría a eliminar.
     */
    private async deleteCategory(id: number): Promise<void> {
        const category = this.categories.find((c) => c.id === id);
        const confirmMessage = category
            ? `¿Estás seguro de eliminar la categoría "${category.name}"?`
            : '¿Estás seguro de eliminar esta categoría?';

        if (!(await confirm(confirmMessage))) {
            return;
        }

        const response = await categoriesService.delete(id);

        if (response.ok) {
            await this.loadCategories();
        } else {
            await alertMessage(response.data || 'No se pudo eliminar la categoría.');
        }
    }
}

if (!customElements.get('categories-list-page')) {
    customElements.define('categories-list-page', CategoriesList);
}
