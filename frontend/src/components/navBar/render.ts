import type { Links } from '../../interfaces/links';
import styles from './style.css?inline';

/**
 * Genera el HTML y aplica los estilos de la barra de navegación
 * dentro de su shadow DOM.
 */
export class Render {
    root: ShadowRoot;
    title: string;
    slogan: string;
    items: Links[];

    /**
     * @param root    Shadow root del componente donde se renderiza.
     * @param title   Nombre de la tienda.
     * @param slogan  Eslogan de la tienda.
     * @param items   Enlaces de navegación de la barra superior.
     */
    constructor(root: ShadowRoot, title: string, slogan: string, items: Links[]) {
        this.root = root;
        this.title = title;
        this.slogan = slogan;
        this.items = items;
    }

    /**
     * Construye el HTML de la barra de navegación, lo inserta en el
     * shadow root y adjunta los estilos del componente.
     *
     * @returns El shadow root con el contenido renderizado.
     */
    render(){
        
        const htmlString: string = `
                <header class="topbar">
                    <div class="brand-block">
                    <span class="brand-mark">🛍️</span>
                    <div>
                        <h1>${this.title}</h1>
                        <p>${this.slogan}</p>
                    </div>
                    </div>
                    <nav class="top-nav">
                    ${this.items.map(({ title, href }) => `<a href="${href}" data-link>${title}</a>`).join('')}
                    </nav>
                    <button class="cart-btn" type="button" aria-label="Carrito">🛒 0</button>
                </header>
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