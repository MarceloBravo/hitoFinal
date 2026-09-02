import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del formulario de marcas en el light DOM.
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
     * @param heading Texto del encabezado ("Nueva Marca" o "Editar Marca").
     * @param name    Valor actual del campo nombre.
     * @param active  Valor actual del estado activo.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(heading: string, name: string, active: boolean) {
        const htmlString: string = `
            <div class="marks-form__container">
                <h2 class="marks-form__title">${heading}</h2>
                <form id="mark-form" novalidate>
                    <div class="marks-form__field">
                        <label for="mark-name">Nombre</label>
                        <input type="text" id="mark-name" name="name" maxlength="100" value="${this.escapeHtml(name)}" class="marks-form__input"/>
                        <div class="marks-form__error" id="mark-name_error"></div>
                    </div>
                    <div class="marks-form__field">
                        <label class="marks-form__checkbox">
                            <input type="checkbox" id="mark-active" name="active" ${active ? 'checked' : ''}/>
                            <span>Marca activa</span>
                        </label>
                    </div>
                    <div class="marks-form__error marks-form__server" id="mark-form_error" role="alert"></div>
                    <div class="marks-form__actions">
                        <button type="submit" class="marks-form__btn marks-form__btn--primary" id="btn-save">Guardar</button>
                        <a href="/admin_home/marks" data-link>
                            <button type="button" class="marks-form__btn marks-form__btn--ghost" id="btn-cancel">Cancelar</button>
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
