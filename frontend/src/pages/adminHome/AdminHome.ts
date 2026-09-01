import { Template } from './template';

/**
 * Web Component del home del administrador (backoffice).
 *
 * Página base de la sección administrativa; por ahora es una página en
 * blanco cuyo diseño se definirá en una iteración siguiente.
 */
export class AdminHome extends HTMLElement {

    constructor() {
        super();
    }

    /**
     * Atributos observados para reaccionar a cambios en el DOM.
     */
    static get observedAttributes() {
        return [
            'title' // Atributo para el título de la página
        ];
    }

    /**
     * Se ejecuta cuando el componente se inserta en el DOM.
     */
    connectedCallback() {
        this.classList.add('admin-home');
        this.render();
    }

    /**
     * Genera el contenido de la página.
     */
    render() {
        (new Template(this)).render();
    }

}

if (!customElements.get('admin-home-page')) {
    customElements.define('admin-home-page', AdminHome);
}