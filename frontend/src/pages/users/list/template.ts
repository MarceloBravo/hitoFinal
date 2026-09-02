import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del listado de usuarios en el light DOM.
 */
export class Template {
    private host: HTMLElement;

    /**
     * @param host Elemento anfitrión donde se renderiza la plantilla.
     */
    constructor(host: HTMLElement) {
        this.host = host;
    }

    /**
     * Construye el HTML de la grilla de usuarios (toolbar, tabla y paginación),
     * lo inserta en el anfitrión y adjunta los estilos correspondientes.
     *
     * @param users       Usuarios a mostrar en la página activa.
     * @param totalPages  Total de páginas.
     * @param activePage  Página activa.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(users: { id: number; name: string; email: string; role: string; active: boolean }[], totalPages: number, activePage: number) {
        const htmlString: string = `
            <div class="users-list">
                <div class="users-list__header">
                    <h2 class="users-list__title">Usuarios</h2>
                    <a href="/admin_home/users/new" data-link>
                        <button type="button" class="users-list__new-btn" id="btn-new">+ Nuevo Usuario</button>
                    </a>
                    <div class="users-list__search">
                        <input type="text" id="search-users" class="users-list__search-input" placeholder="Buscar por nombre o correo..." aria-label="Buscar usuarios" />
                    </div>
                </div>
                <div class="users-list__table-wrap">
                    <table class="users-list__table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Correo</th>
                                <th>Rol</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="users-tbody">${this.rowsHtml(users)}</tbody>
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

    /**
     * Genera el HTML de las filas (`<tr>`) de la grilla.
     *
     * @param users Usuarios a mostrar.
     * @returns HTML de las filas o de la fila vacía si no hay registros.
     */
    rowsHtml(users: { id: number; name: string; email: string; role: string; active: boolean }[]): string {
        if (!users.length) {
            return `<tr><td colspan="6" class="users-list__empty">No se encontraron usuarios.</td></tr>`;
        }

        return users
            .map((user) => `
                <tr>
                    <td>${user.id}</td>
                    <td>${this.escapeHtml(user.name)}</td>
                    <td>${this.escapeHtml(user.email)}</td>
                    <td>
                        <span class="users-list__badge ${user.role === 'ADMIN' ? 'users-list__badge--admin' : 'users-list__badge--user'}">
                            ${this.escapeHtml(user.role)}
                        </span>
                    </td>
                    <td>
                        <span class="users-list__badge ${user.active ? 'users-list__badge--active' : 'users-list__badge--inactive'}">
                            ${user.active ? 'Activo' : 'Inactivo'}
                        </span>
                    </td>
                    <td class="users-list__actions">
                        <button class="users-list__icon-btn" type="button" data-action="edit" data-id="${user.id}" title="Editar" aria-label="Editar ${this.escapeHtml(user.name)}">✎</button>
                        <button class="users-list__icon-btn users-list__icon-btn--danger" type="button" data-action="delete" data-id="${user.id}" title="Eliminar" aria-label="Eliminar ${this.escapeHtml(user.name)}">🗑</button>
                    </td>
                </tr>`)
            .join('');
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
