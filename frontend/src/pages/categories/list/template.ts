import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del listado de categorías en el light DOM.
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
     * Construye el HTML de la grilla de categorías (toolbar, tabla y paginación),
     * lo inserta en el anfitrión y adjunta los estilos correspondientes.
     *
     * @param categories  Categorías a mostrar en la página activa.
     * @param totalPages  Total de páginas.
     * @param activePage  Página activa.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(categories: { id: number; name: string; active: boolean }[], totalPages: number, activePage: number) {
        const htmlString: string = `
            <div class="categories-list">
                <div class="categories-list__header">
                    <h2 class="categories-list__title">Categorías</h2>
                    <a href="/admin_home/categories/new" data-link>
                        <button type="button" class="categories-list__new-btn" id="btn-new">+ Nueva Categoría</button>
                    </a>
                    <div class="categories-list__search">
                        <input type="text" id="search-categories" class="categories-list__search-input" placeholder="Buscar por nombre..." aria-label="Buscar categorías" />
                    </div>
                </div>
                <div class="categories-list__table-wrap">
                    <table class="categories-list__table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="categories-tbody">${this.rowsHtml(categories)}</tbody>
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
     * @param categories Categorías a mostrar.
     * @returns HTML de las filas o de la fila vacía si no hay registros.
     */
    rowsHtml(categories: { id: number; name: string; active: boolean }[]): string {
        if (!categories.length) {
            return `<tr><td colspan="4" class="categories-list__empty">No se encontraron categorías.</td></tr>`;
        }

        return categories
            .map((category) => `
                <tr>
                    <td>${category.id}</td>
                    <td>${category.name}</td>
                    <td>
                        <span class="categories-list__badge ${category.active ? 'categories-list__badge--active' : 'categories-list__badge--inactive'}">
                            ${category.active ? 'Activa' : 'Inactiva'}
                        </span>
                    </td>
                    <td class="categories-list__actions">
                        <button class="categories-list__icon-btn" type="button" data-action="edit" data-id="${category.id}" title="Editar" aria-label="Editar ${this.escapeHtml(category.name)}">✎</button>
                        <button class="categories-list__icon-btn categories-list__icon-btn--danger" type="button" data-action="delete" data-id="${category.id}" title="Eliminar" aria-label="Eliminar ${this.escapeHtml(category.name)}">🗑</button>
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
