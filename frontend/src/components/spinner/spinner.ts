import styles from './style.css?inline';

/**
 * Web Component que muestra un indicador de carga animado.
 *
 * Renderiza un spinner accesible dentro de su shadow DOM.
 */
export class Spinner extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    /**
     * Se ejecuta cuando el componente se inserta en el DOM.
     */
    connectedCallback() {
        this.render();
    }

    /**
     * Genera el HTML y aplica los estilos del spinner dentro de su shadow DOM.
     */
    render() {
        const shadow = this.shadowRoot;

        if (!shadow) {
            return;
        }

        shadow.innerHTML = `
            <style>${styles}</style>
            <div class="spinner-card" role="status" aria-live="polite">
                <div class="spinner-ring"></div>
                <span class="spinner-label">Cargando...</span>
            </div>
        `;
    }
}

if (!customElements.get('spinner-component')) {
    customElements.define('spinner-component', Spinner);
}
