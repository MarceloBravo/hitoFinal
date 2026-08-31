import '../../components';

import styles from './style.css?inline';
import type { ProductResponseApi } from '../../interfaces/productResponseApi';
import { LoadStatus } from '../../enum/loadStatusEnum';

/**
 * Genera el HTML y los estilos de la página de inicio en el light DOM.
 *
 * Según el estado de carga, muestra un error, la grilla de productos
 * o la paginación correspondiente.
 */
export class Template{
    private root: HTMLElement; 
    private title: string = 'Tienda on-line';
    private optionsCategories: string;
    private optionsMarks: string;
    private optionsPrices: string;
    private productsData: ProductResponseApi | string;
    private activaPage: number = 1;
    private loadStatus: LoadStatus;
    
    /**
     * @param root              Elemento anfitrión donde se renderiza la plantilla.
     * @param title             Título de la página.
     * @param optionsCategories Opciones de categorías (JSON) para los filtros.
     * @param optionsMarks      Opciones de marcas (JSON) para los filtros.
     * @param optionsPrices     Opciones de precios (JSON) para los filtros.
     * @param productsData      Datos de productos o mensaje de error.
     * @param activaPage        Página activa actual.
     * @param loadStatus        Estado de carga de la interfaz.
     */
    constructor(root: HTMLElement, title: string, optionsCategories: string, optionsMarks: string, optionsPrices: string, productsData: ProductResponseApi | string, activaPage: number, loadStatus: LoadStatus){
        this.root = root;
        this.title = title;
        this.optionsCategories = optionsCategories;
        this.optionsMarks = optionsMarks;
        this.optionsPrices = optionsPrices;
        this.productsData = productsData;
        this.activaPage = activaPage;
        this.loadStatus = loadStatus;
    }

    /**
     * Genera el contenido de la grilla de productos según el estado de carga.
     *
     * @returns HTML con el mensaje de error, la grilla de productos o un mensaje de carga.
     */
    private renderContent(): string {
        switch (this.loadStatus) {
            case LoadStatus.ERROR:
                return `
                    <div class="alerta-error">
                        <p>No fue posible obtener los datos de productos.</p>
                    </div>
                `;
            case LoadStatus.SUCCESS:
                if (typeof this.productsData === 'string') {
                    return '';
                }
                if (this.productsData.products.length === 0) {
                    return '<p>No hay productos disponibles en este momento.</p>';
                }
                return this.productsData.products.map((product) => `
                    <product-card
                        img="${import.meta.env.VITE_IMAGE_URL}${product.imagePath}"
                        title="${product.name}"
                        description="${product.description}"
                        price="${product.priceSale}"
                    ></product-card>
                `).join('');
            case LoadStatus.LOADING:
            default:
                return "<p class='loading'>Cargando productos desde el servidor ...</p>";
        }
    }

    /**
     * Calcula el total de páginas de la paginación.
     *
     * @returns Total de páginas, o 1 si no hay datos o el estado no es de éxito.
     */
    private getTotalPages(): number {
        if (this.loadStatus !== LoadStatus.SUCCESS || typeof this.productsData === 'string') {
            return 1;
        }
        const total: number = this.productsData.total;
        const limit: number = this.productsData.limit;
        return total > 0 && limit > 0 ? Math.ceil(total / limit) : 1;
    }

    /**
     * Construye el HTML completo de la página de inicio, lo inserta en el
     * anfitrión y adjunta los estilos correspondientes.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(){
        const productsGrid: string = this.renderContent();
        const totalPages: number = this.getTotalPages();
        const pagination: string = this.loadStatus === LoadStatus.SUCCESS
            ? `<pagination-nav total-pages="${totalPages}" active-page="${this.activaPage}"></pagination-nav>`
            : '';

        const htmlString: string = `
            <div class="home-page-shell">

                <main class="home-content-layout">
                    <aside class="home-filters-panel">
                        <h2>Filtros</h2>
                        <aside-section 
                            title="Categorias"
                            type="radio" 
                            options='[${this.optionsCategories}]'>
                        </aside-section>

                        <aside-section 
                            title="marcas"
                            type="radio" 
                            options='[${this.optionsMarks}]'>
                        </aside-section>

                        <aside-section 
                            title="precios"
                            type="radio" 
                            options='[${this.optionsPrices}]'>
                        </aside-section>
                    </aside>
                    

                    <section class="home-products-section">
                        <div class="home-section-title">
                            <h2>Productos destacados</h2>
                            <p>Descubre las mejores opciones del día</p>
                        </div>
                        
                        <div class="home-products-grid">
                        ${productsGrid}
                        </div>

                        ${pagination}
                    </section>
                </main>

                <footer-section 
                    title="${this.title}"
                    created="03/08/2026"
                    phone="+56 9 1234 5678"
                    email="contacto@tiendaonline.cl"/>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        this.root.replaceChildren(fragment);

        const style = document.createElement('style');
        style.textContent = styles;
        this.root.appendChild(style);

        return this.root;
    }
}
