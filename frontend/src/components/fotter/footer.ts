import { Render } from './render';

/**
 * Web Component que renderiza el pie de página del e-commerce.
 *
 * Acepta los atributos `label`, `created`, `phone` y `email` para
 * personalizar el contenido dentro de su shadow DOM.
 */
export class Footer extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    /**
     * Atributos observados para reaccionar a cambios en el DOM.
     */
    static get observedAttributes() {
        return [
            'label', // Atributo para el título del footer
            'created', // Atributo para la fecha de creación de la página
            'phone', // Atributo para el número de teléfono de contacto
            'email' // Atributo para la dirección de correo electrónico de contacto
        ];
    }

    /**
     * Se ejecuta cuando el componente se inserta en el DOM.
     */
    connectedCallback() {
        this.render();
    }

    /**
     * Re-renderiza el componente cuando cambian sus atributos observados.
     */
    attributeChangedCallback(_attrName: string, oldValue: string | null, newValue: string | null) {
        if (oldValue !== newValue) {
            this.render();
        }
    }

    /**
     * Genera el HTML y aplica el CSS del componente dentro de su shadow DOM.
     */
    render() {
        const root: ShadowRoot | null = this.shadowRoot;

        if (!root) {
            return;
        }
        
        const label: string = this.getAttribute('label') || 'E-Commerce';
        const created: string = this.getAttribute('created') || '03/08/2026';
        const phone: string = this.getAttribute('phone') || '+56 9 1234 5678';
        const email: string = this.getAttribute('email') || 'contacto@tiendaonline.cl';
        const render = new Render(root, label, created, phone, email);
        render.render();
    }
}
    
if (!customElements.get('footer-section')) {
    customElements.define('footer-section', Footer);
}