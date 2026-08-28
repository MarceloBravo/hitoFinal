import type { AsideOptions } from '../../interfaces/asideOptions';
import { InputTypeEnum } from '../../enum/inputTypeEnum';
import { Render } from './render';

/**
 * Web Component que renderiza una sección de filtros laterales.
 *
 * Acepta los atributos `title`, `type` (checkbox o radio) y `options`
 * (JSON con las opciones del filtro) dentro de su shadow DOM.
 */
export class AsideSection extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    /**
     * Atributos observados para reaccionar a cambios en el DOM.
     */
    static get observedAttributes() {
        return [
            'title', // Atributo para el título de la sección
            'type', // Atributo para el tipo de sección (checkbox o radio) Ej.: checkbox, radio
            'options' // Atributo para las opciones de la sección Ej.: [{"label":"Samsung","checked":true}, {"label":"Apple","checked":false}, {"label":"Sony","checked":false}]
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
        
        const type: InputTypeEnum = (this.getAttribute('type') as InputTypeEnum) || InputTypeEnum.CHECKBOX;
        const optionsAttr: string | null = this.getAttribute('options');
        const options: AsideOptions[] = optionsAttr ? JSON.parse(optionsAttr) as AsideOptions[] : [
            { 'label': "Opción 1", 'type': 'checkbox', 'checked': true },
            { 'label': "Opción 2", 'type': 'checkbox', 'checked': false },
            { 'label': "Opción 3", 'type': 'checkbox', 'checked': false }
        ];
        
        const title: string = this.getAttribute('title') || 'Sección';
        const render = new Render(root, title, type, options);
        render.render();
    }
}

if (!customElements.get('aside-section')) {
    customElements.define('aside-section', AsideSection);
}