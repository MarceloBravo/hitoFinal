import '../../../components';
import { Template } from './template';
import { usersService } from '../../../services/usersService';
import type { UserInterface } from '../../../interfaces/UserInterface';

const PAGE_SIZE = 8;

/**
 * Web Component de la página del mantenedor de usuarios (listado).
 *
 * Muestra una grilla paginada de usuarios con búsqueda por nombre o correo y
 * botones de acción (editar/eliminar) por fila. Usa Light DOM y la paginación
 * client-side a partir del listado completo entregado por la API.
 */
export class UsersList extends HTMLElement {

    /** Todos los usuarios cargados desde la API. */
    private users: UserInterface[] = [];
    /** Usuarios tras aplicar el filtro de búsqueda. */
    private filtered: UserInterface[] = [];
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
        this.classList.add('users-list-page');
        this.configurePersistentListeners();
        void this.loadUsers();
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
        if (!input || input.id !== 'search-users') {
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
            this.navigate(`/admin_home/users/edit/${id}`);
        } else if (button.dataset.action === 'delete') {
            void this.deleteUser(id);
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
     * Carga el listado completo de usuarios y actualiza la grilla.
     */
    private async loadUsers(): Promise<void> {
        const response = await usersService.getAll();

        if (response.ok && response.data) {
            this.users = response.data.data;
        } else {
            this.users = [];
            if (!response.ok) {
                window.alert(response.data || 'No se pudieron cargar los usuarios.');
            }
        }

        this.activePage = 1;
        this.searchTerm = '';
        this.applyFilter();
    }

    /**
     * Aplica el filtro de búsqueda, recalcula la paginación y renderiza.
     */
    private applyFilter(): void {
        const term = this.searchTerm.trim().toLowerCase();
        this.filtered = term
            ? this.users.filter((user) =>
                user.name.toLowerCase().includes(term) || user.email.toLowerCase().includes(term))
            : this.users;

        this.totalPages = Math.max(1, Math.ceil(this.filtered.length / PAGE_SIZE));
        if (this.activePage > this.totalPages) {
            this.activePage = this.totalPages;
        }

        const template = new Template(this);
        if (this.querySelector('#users-tbody')) {
            this.renderTable();
        } else {
            template.render(this.currentPageUsers(), this.totalPages, this.activePage);
            this.restoreSearchValue();
        }
    }

    /**
     * Actualiza únicamente las filas de la tabla y la paginación vigente.
     */
    private renderTable(): void {
        const tbody = this.querySelector<HTMLElement>('#users-tbody');
        if (tbody) {
            tbody.innerHTML = (new Template(this)).rowsHtml(this.currentPageUsers());
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
        const searchInput = this.querySelector<HTMLInputElement>('#search-users');
        if (searchInput) {
            searchInput.value = this.searchTerm;
        }
    }

    /**
     * Obtiene los usuarios correspondientes a la página activa.
     *
     * @returns Usuarios de la página vigente.
     */
    private currentPageUsers(): UserInterface[] {
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
     * Elimina (desactiva) un usuario tras confirmación del usuario.
     *
     * @param id Identificador del usuario a eliminar.
     */
    private async deleteUser(id: number): Promise<void> {
        const user = this.users.find((u) => u.id === id);
        const confirmMessage = user
            ? `¿Estás seguro de eliminar al usuario "${user.name}"?`
            : '¿Estás seguro de eliminar a este usuario?';

        if (!window.confirm(confirmMessage)) {
            return;
        }

        const response = await usersService.delete(id);

        if (response.ok) {
            await this.loadUsers();
        } else {
            window.alert(response.data || 'No se pudo eliminar el usuario.');
        }
    }
}

if (!customElements.get('users-list-page')) {
    customElements.define('users-list-page', UsersList);
}
