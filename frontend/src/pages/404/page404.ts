import { Template } from "./template";

/**
 * Web Component de la página 404 (recurso no encontrado).
 *
 * Renderiza un mensaje de error con un enlace de regreso al inicio.
 */
export class Page404 extends HTMLElement {

    constructor(){
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
        this.classList.add('page-404');
        this.render();
    }

    /**
     * Configura los listeners de la página, como el botón de regreso al inicio.
     */
    configListeners(){
        const button = this.querySelector('#btn-back');
        if (button instanceof HTMLElement) {
            button.addEventListener('click', (event) => {
                event.preventDefault();
                window.history.pushState({}, '', '/home');
                window.dispatchEvent(new PopStateEvent('popstate'));
            });
        }
    }

    /**
     * Genera el contenido de la página y configura sus listeners.
     */
    render(){
        (new Template(this)).render();
        this.configListeners();
    }

}

if (!customElements.get('page-404')) {
    customElements.define('page-404', Page404);
}