import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del home del administrador en el light DOM.
 */
export class Template{
    private host: HTMLElement;

    /**
     * @param host Elemento anfitrión donde se renderiza la plantilla.
     */
    constructor(host: HTMLElement){
        this.host = host;
    }

    /**
     * Construye el HTML base del backoffice, lo inserta en el anfitrión
     * y adjunta los estilos correspondientes.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(){
        const htmlString: string = `
            <div class="admin-home__container">
                <h2>Backoffice</h2>
                <p>Sección administrativa de la tienda.</p>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        const style = document.createElement('style');
        style.textContent = styles;

        this.host.replaceChildren(style, fragment);

        return this.host;
    }
}