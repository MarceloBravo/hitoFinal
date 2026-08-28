import styles from './style.css?inline';

/**
 * Genera el HTML y aplica los estilos del componente `product-card`
 * dentro de su shadow DOM.
 */
export class Render {
    private root: ShadowRoot;
    private img: string;
    private title: string;
    private description: string;
    private price: string;

    /**
     * @param root        Shadow root del componente donde se renderiza.
     * @param img         URL de la imagen del producto.
     * @param title       Título del producto.
     * @param description Descripción del producto.
     * @param price       Precio del producto.
     */
    constructor(root: ShadowRoot, img: string, title: string, description: string, price: string) {
        this.root = root;
        this.img = img;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    /**
     * Construye el HTML de la tarjeta, lo inserta en el shadow root
     * y adjunta los estilos del componente.
     *
     * @returns El shadow root con el contenido renderizado.
     */
    render(){
         const htmlString: string = `
            <article class="product-card">
                <img src="${this.img}" alt="${this.title}">
                <h3>${this.title}</h3>
                <p>${this.description}</p>
                <span>$${this.price}</span>
            </article>
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