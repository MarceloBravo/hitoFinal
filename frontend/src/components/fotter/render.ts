import styles from './style.css?inline';

/**
 * Genera el HTML y aplica los estilos del componente `footer-section`
 * dentro de su shadow DOM.
 */
export class Render {
    private root: ShadowRoot;
    private label: string;
    private created: string;
    private phone: string;
    private email: string;

    /**
     * @param root    Shadow root del componente donde se renderiza.
     * @param label   Título del footer.
     * @param created Fecha de creación mostrada en el footer.
     * @param phone   Teléfono de contacto.
     * @param email   Correo electrónico de contacto.
     */
    constructor(root: ShadowRoot, label: string, created: string, phone: string, email: string) {
        this.root = root;
        this.label = label;
        this.created = created;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Construye el HTML del footer, lo inserta en el shadow root
     * y adjunta los estilos del componente.
     *
     * @returns El shadow root con el contenido renderizado.
     */
    render(){
        const htmlString: string = `
            <footer class="site-footer">
                <div>
                    <h2>${this.label}</h2>
                    <p>Creado el ${this.created}</p>
                </div>
                <div>
                    <p>📞 ${this.phone}</p>
                    <p>✉️ ${this.email}</p>
                </div>
            </footer>
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