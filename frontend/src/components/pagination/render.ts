import styles from './style.css?inline';

/**
 * Genera el HTML y aplica los estilos del componente `pagination-nav`
 * dentro de su shadow DOM.
 */
export class Render {
    private root: ShadowRoot;
    private pageNumbers: number[];
    private active: number;
    private firstDisabled: boolean;
    private lastDisabled: boolean;
    private lastPage: number;

    /**
     * @param root          Shadow root del componente donde se renderiza.
     * @param pageNumbers   Números de página a mostrar como botones.
     * @param active        Página activa.
     * @param firstDisabled Indica si el botón de primera página debe estar deshabilitado.
     * @param lastDisabled  Indica si el botón de última página debe estar deshabilitado.
     * @param lastPage      Número de la última página.
     */
    constructor(root: ShadowRoot, pageNumbers: number[], active: number, firstDisabled: boolean, lastDisabled: boolean, lastPage: number) {
        this.root = root;
        this.pageNumbers = pageNumbers;
        this.active = active;
        this.firstDisabled = firstDisabled;
        this.lastDisabled = lastDisabled;
        this.lastPage = lastPage;
    }

    /**
     * Construye el HTML de la paginación, lo inserta en el shadow root
     * y adjunta los estilos del componente.
     *
     * @returns El shadow root con el contenido renderizado.
     */
    render() {
        const pageButtons = this.pageNumbers
            .map((page) => {
                const isActive = page === this.active;
                return `<button class="page-button${isActive ? ' active' : ''}" type="button" data-page="${page}" aria-current="${isActive ? 'page' : 'false'}">${page}</button>`;
            })
            .join('');

        const htmlString: string = `
            <div class="pagination">
                <button class="nav-button" type="button" data-page="1" ${this.firstDisabled ? 'disabled' : ''} aria-label="Ir a la primera página">«</button>
                <div class="page-list">
                    ${pageButtons}
                </div>
                <button class="nav-button" type="button" data-page="${this.lastPage}" ${this.lastDisabled ? 'disabled' : ''} aria-label="Ir a la última página">»</button>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        this.root.replaceChildren(fragment);

        const style = document.createElement('style');
        style.textContent = styles;
        this.root.appendChild(style);

        return this.root;
    }
}
