import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del formulario de categorías en el light DOM.
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
     * Construye el HTML del formulario (nombre y estado activo), lo inserta
     * en el anfitrión y adjunta los estilos correspondientes.
     *
     * @param heading Texto del encabezado ("Nueva Categoría" o "Editar Categoría").
     * @param name    Valor actual del campo nombre.
     * @param active  Valor actual del estado activo.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(heading: string, name: string, active: boolean) {
        const htmlString: string = `
            <div class="categories-form__container">
                <h2 class="categories-form__title">${heading}</h2>
                <form id="category-form" novalidate>
                    <div class="categories-form__field">
                        <label for="category-name">Nombre</label>
                        <input type="text" id="category-name" name="name" maxlength="100" value="${this.escapeHtml(name)}" class="categories-form__input"/>
                        <div class="categories-form__error" id="category-name_error"></div>
                    </div>
                    <div class="categories-form__field">
                        <label class="categories-form__checkbox">
                            <input type="checkbox" id="category-active" name="active" ${active ? 'checked' : ''}/>
                            <span>Categoría activa</span>
                        </label>
                    </div>
                    <div class="categories-form__error categories-form__server" id="category-form_error" role="alert"></div>
                    <div class="categories-form__actions">
                        <button type="submit" class="categories-form__btn categories-form__btn--primary" id="btn-save">Guardar</button>
                        <a href="/admin_home/categories" data-link>
                            <button type="button" class="categories-form__btn categories-form__btn--ghost" id="btn-cancel">Cancelar</button>
                        </a>
                    </div>
                </form>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        const style = document.createElement('style');
        style.textContent = styles;

        this.host.replaceChildren(style, fragment);

        return this.host;
    }

    /**
     * Escapa caracteres especiales del HTML para evitar inyección.
     *
     * @param value Texto a escapar.
     * @returns Texto seguro para insertar en un atributo/valor HTML.
     */
    private escapeHtml(value: string): string {
        return value
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }
}
