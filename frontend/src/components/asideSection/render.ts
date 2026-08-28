import type { AsideOptions } from '../../interfaces/asideOptions';
import { InputTypeEnum } from '../../enum/inputTypeEnum';
import styles from './style.css?inline';

/**
 * Genera el HTML y aplica los estilos del componente `aside-section`
 * dentro de su shadow DOM.
 */
export class Render {
    private root: ShadowRoot;
    private type: InputTypeEnum;
    private title: string
    private options: AsideOptions[];

    /**
     * @param root    Shadow root del componente donde se renderiza.
     * @param title   Título de la sección de filtros.
     * @param type    Tipo de control (checkbox o radio).
     * @param options Opciones del filtro.
     */
    constructor(root: ShadowRoot, title: string, type: InputTypeEnum, options: AsideOptions[]) {
        this.root = root;
        this.type = type;
        this.title = title;
        this.options = options;
    }

    /**
     * Construye el HTML de la sección, lo inserta en el shadow root
     * y adjunta los estilos del componente.
     *
     * @returns El shadow root con el contenido renderizado.
     */
    render(){
        
        const htmlString: string = `
            <section>
                <h3>${this.title}</h3>
                <div class="aside-options">
                ${this.options.map(({ label, checked }) => `
                    <label>
                        <input 
                            name="${this.type === InputTypeEnum.CHECKBOX ? label : this.title}"
                            type="${this.type}" ${checked ? 'checked' : ''} /> 
                            ${label}
                        </label>`).join('')}
                </div>
            </section>
        `;
        // Parseamos la cadena a un DocumentFragment (conjunto de nodos DOM)
        const fragmento: DocumentFragment = document.createRange().createContextualFragment(htmlString);

        // Reemplazamos todo el contenido del contenedor por los nodos del fragmento
        this.root.replaceChildren(fragmento);

        const style: HTMLStyleElement = document.createElement('style');
        style.textContent = styles;
        this.root.appendChild(style);

        return this.root;
    }
}