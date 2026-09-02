import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del listado de marcas en el light DOM.
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
     * Construye el HTML de la grilla de marcas (toolbar, tabla y paginación),
     * lo inserta en el anfitrión y adjunta los estilos correspondientes.
     *
     * @param marks       Marcas a mostrar en la página activa.
     * @param totalPages  Total de páginas.
     * @param activePage  Página activa.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(marks: { id: number; name: string; active: boolean }[], totalPages: number, activePage: number) {
        const htmlString: string = `
            <div class="marks-list">
                <div class="marks-list__header">
                    <h2 class="marks-list__title">Marcas</h2>
                    <a href="/admin_home/marks/new" data-link>
                        <button type="button" class="marks-list__new-btn" id="btn-new">+ Nueva Marca</button>
                    </a>
                    <div class="marks-list__search">
                        <input type="text" id="search-marks" class="marks-list__search-input" placeholder="Buscar por nombre..." aria-label="Buscar marcas" />
                    </div>
                </div>
                <div class="marks-list__table-wrap">
                    <table class="marks-list__table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody id="marks-tbody">${this.rowsHtml(marks)}</tbody>
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
     * @param marks Marcas a mostrar.
     * @returns HTML de las filas o de la fila vacía si no hay registros.
     */
    rowsHtml(marks: { id: number; name: string; active: boolean }[]): string {
        if (!marks.length) {
            return `<tr><td colspan="4" class="marks-list__empty">No se encontraron marcas.</td></tr>`;
        }

        return marks
            .map((mark) => `
                <tr>
                    <td>${mark.id}</td>
                    <td>${mark.name}</td>
                    <td>
                        <span class="marks-list__badge ${mark.active ? 'marks-list__badge--active' : 'marks-list__badge--inactive'}">
                            ${mark.active ? 'Activa' : 'Inactiva'}
                        </span>
                    </td>
                    <td class="marks-list__actions">
                        <button class="marks-list__icon-btn" type="button" data-action="edit" data-id="${mark.id}" title="Editar" aria-label="Editar ${this.escapeHtml(mark.name)}">✎</button>
                        <button class="marks-list__icon-btn marks-list__icon-btn--danger" type="button" data-action="delete" data-id="${mark.id}" title="Eliminar" aria-label="Eliminar ${this.escapeHtml(mark.name)}">🗑</button>
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