import '../../../components';
import { Template } from './template';
import { marksService } from '../../../services/marksService';
import type { MarkInterface } from '../../../interfaces/MarkInterface';

const PAGE_SIZE = 8;

/**
 * Web Component de la página del mantenedor de marcas (listado).
 *
 * Muestra una grilla paginada de marcas con búsqueda por nombre y botones
 * de acción (editar/eliminar) por fila. Usa Light DOM y la paginación
 * client-side a partir del listado completo entregado por la API.
 *
 * Los listeners de la grilla, la búsqueda y la paginación se registran una
 * sola vez sobre el host; los cambios de página y de término de búsqueda
 * actualizan únicamente el cuerpo de la tabla y el componente de paginación,
 * preservando el foco del campo de búsqueda.
 */
export class MarksList extends HTMLElement {

    /** Todas las marcas cargadas desde la API. */
    private marks: MarkInterface[] = [];
    /** Marcas tras aplicar el filtro de búsqueda. */
    private filtered: MarkInterface[] = [];
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
        this.classList.add('marks-list-page');
        this.configurePersistentListeners();
        void this.loadMarks();
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
        if (!input || input.id !== 'search-marks') {
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
            this.navigate(`/admin_home/marks/edit/${id}`);
        } else if (button.dataset.action === 'delete') {
            void this.deleteMark(id);
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
     * Carga el listado completo de marcas y actualiza la grilla.
     */
    private async loadMarks(): Promise<void> {
        const response = await marksService.getAll();

        if (response.ok && response.data) {
            this.marks = response.data.data;
        } else {
            this.marks = [];
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
            ? this.marks.filter((mark) => mark.name.toLowerCase().includes(term))
            : this.marks;

        this.totalPages = Math.max(1, Math.ceil(this.filtered.length / PAGE_SIZE));
        if (this.activePage > this.totalPages) {
            this.activePage = this.totalPages;
        }

        const template = new Template(this);
        if (this.querySelector('#marks-tbody')) {
            this.renderTable();
        } else {
            template.render(this.currentPageMarks(), this.totalPages, this.activePage);
            this.restoreSearchValue();
        }
    }

    /**
     * Actualiza únicamente las filas de la tabla y la paginación vigente.
     */
    private renderTable(): void {
        const tbody = this.querySelector<HTMLElement>('#marks-tbody');
        if (tbody) {
            tbody.innerHTML = (new Template(this)).rowsHtml(this.currentPageMarks());
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
        const searchInput = this.querySelector<HTMLInputElement>('#search-marks');
        if (searchInput) {
            searchInput.value = this.searchTerm;
        }
    }

    /**
     * Obtiene las marcas correspondientes a la página activa.
     *
     * @returns Marcas de la página vigente.
     */
    private currentPageMarks(): MarkInterface[] {
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
     * Elimina una marca tras confirmación del usuario.
     *
     * @param id Identificador de la marca a eliminar.
     */
    private async deleteMark(id: number): Promise<void> {
        const mark = this.marks.find((m) => m.id === id);
        const confirmMessage = mark
            ? `¿Estás seguro de eliminar la marca "${mark.name}"?`
            : '¿Estás seguro de eliminar esta marca?';

        if (!window.confirm(confirmMessage)) {
            return;
        }

        const response = await marksService.delete(id);

        if (response.ok) {
            await this.loadMarks();
        } else {
            window.alert(response.data || 'No se pudo eliminar la marca.');
        }
    }
}

if (!customElements.get('marks-list-page')) {
    customElements.define('marks-list-page', MarksList);
}