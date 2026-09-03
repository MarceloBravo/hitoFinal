import { ProductService } from '../../services/productService';
import { categoriesService } from '../../services/categoriesService';
import { marksService } from '../../services/marksService';
import { CartStore } from '../../store/cartStore';
import { toast } from '../../utils/toast';
import type { ResponseInterface } from '../../interfaces/responseInterface';
import type { ProductResponseApi } from '../../interfaces/productResponseApi';
import type { CategoriesResponseApi } from '../../interfaces/categoriesResponseApi';
import type { MarksResponseApi } from '../../interfaces/marksResponseApi';
import type { ProductFilters } from '../../interfaces/productFilters';
import { Template } from './template';
import { LoadStatus } from '../../enum/loadStatusEnum';

const LIMIT = import.meta.env.VITE_PRODUCTS_PER_PAGE;
const FILTERS_STORAGE_KEY = 'productFilters';

interface FilterEventDetail {
    group: string;
    id?: number;
    value?: string;
    priceMin?: number;
    priceMax?: number;
}

interface AddToCartDetail {
    productId?: number;
    stock?: number;
}


/**
 * Web Component de la página de inicio (catálogo de productos).
 *
 * Carga los productos, categorías y marcas desde la API, muestra un spinner
 * mientras carga, gestiona la paginación del catálogo y aplica los filtros
 * de categoría, marca y precio que el usuario selecciona en el panel lateral.
 */
export class HomePage extends HTMLElement {

    constructor() {
        super();
        this.filters = this.readFilters();
    }

    private filters: ProductFilters;

    /** Temporizador del debounce de la búsqueda por texto (3 segundos). */
    private searchTimer: number | null = null;

    /**
     * Atributos observados para reaccionar a cambios en el DOM.
     */
    static get observedAttributes() {
        return [
            'title' // Atributo para el título de la página
        ];
    }

    /**
     * Lee los filtros guardados en el localStorage.
     *
     * @returns filtros persistidos o un objeto vacío si no existen.
     */
    private readFilters(): ProductFilters {
        try {
            const raw = localStorage.getItem(FILTERS_STORAGE_KEY);
            return raw ? JSON.parse(raw) as ProductFilters : {};
        } catch {
            return {};
        }
    }

    /**
     * Guarda los filtros actuales en el localStorage.
     */
    private persistFilters(): void {
        try {
            localStorage.setItem(FILTERS_STORAGE_KEY, JSON.stringify(this.filters));
        } catch {
            // Si el almacenamiento no está disponible se ignora silenciosamente.
        }
    }

    /**
     * Construye las opciones JSON de un filtro de selección única (radio),
     * añadiendo una opción "Todos/as" que permite limpiar el filtro e
     * incluyendo el id de cada elemento (categoría o marca).
     *
     * @param items       elementos a mostrar (categorías o marcas) con su id y nombre.
     * @param selectedId  id actualmente seleccionado (para marcarlo como activo).
     * @param allLabel    etiqueta de la opción que limpia el filtro.
     * @returns JSON string con las opciones del filtro.
     */
    private buildIdOptions(items: { id: number; name: string }[], selectedId: number | undefined, allLabel: string): string {
        const all: string = `{"label":"${allLabel}","type":"radio","checked":${selectedId === undefined}}`;
        const rest: string = items
            .map((item) => `{"label":"${item.name}","id":${item.id},"type":"radio","checked":${item.id === selectedId}}`)
            .join(',');
        return [all, rest].filter(Boolean).join(',');
    }

    /**
     * Construye las opciones JSON del filtro de precios (radio) a partir de
     * los rangos definidos y el rango actualmente activo.
     *
     * @param selectedMin límite inferior del rango activo, o indefinido.
     * @param selectedMax límite superior del rango activo, o indefinido.
     * @returns JSON string con las opciones del filtro de precios.
     */
    private buildPriceOptions(selectedMin: number | undefined, selectedMax: number | undefined): string {
        const ranges: { label: string; value: string; priceMin?: number; priceMax?: number }[] = [
            { label: 'Todas las precios', value: 'all' },
            { label: 'Menor a $50.000', value: '0-50000', priceMin: 0, priceMax: 50000 },
            { label: '$50.000 - $100.000', value: '50000-100000', priceMin: 50000, priceMax: 100000 },
            { label: 'Más de $100.000', value: '100000-', priceMin: 100000 }
        ];
        return ranges
            .map((range) => {
                const isActive = range.priceMin === selectedMin && range.priceMax === selectedMax;
                return `{"label":"${range.label}","value":"${range.value}"`
                    + `${range.priceMin !== undefined ? `,"priceMin":${range.priceMin}` : ''}`
                    + `${range.priceMax !== undefined ? `,"priceMax":${range.priceMax}` : ''}`
                    + `,"type":"radio","checked":${isActive}}`;
            })
            .join(',');
    }

    /**
     * Obtiene los productos, categorías y marcas desde la API aplicando los
     * filtros activos.
     *
     * @param limit   Cantidad de productos a solicitar.
     * @param page    Página a solicitar (1-indexado).
     * @param filters Filtros de categoría, marca y precio a aplicar.
     * @returns Opciones de categorías, marcas y precios (JSON) y datos de productos o error.
     */
    loadData = async (limit: number = 12, page: number = 1, filters: ProductFilters = {}) => {
        const products: ResponseInterface<ProductResponseApi> = await ProductService.getAll(
            limit, page, filters.categoryId, filters.markId, filters.minPrice, filters.maxPrice, filters.search
        );
        const categories: ResponseInterface<CategoriesResponseApi> = await categoriesService.getAll();
        const marks: ResponseInterface<MarksResponseApi> = await marksService.getAll();

        const optionsCategories: string = categories.ok
            ? this.buildIdOptions(categories.data.data, filters.categoryId, 'Todas las categorías')
            : `{"label": "No fue posible cargar las categorías", "type": "radio", "checked": false}`;

        const optionsMarks: string = marks.ok
            ? this.buildIdOptions(marks.data.data, filters.markId, 'Todas las marcas')
            : `{"label": "No fue posible cargar las marcas", "type": "radio", "checked": false}`;

        const optionsPrices: string = this.buildPriceOptions(filters.minPrice, filters.maxPrice);

        const productsData: ProductResponseApi | string = products.ok ? products.data : products.data;

        return { optionsCategories, optionsMarks, optionsPrices, productsData };
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
     * Actualiza los filtros según el evento de un aside-section y re-renderiza
     * el catálogo desde la primera página.
     *
     * @param detail detalle del evento de filtro seleccionado.
     */
    private applyFilter(detail: FilterEventDetail): void {
        if (detail.group === 'Categorias') {
            this.filters.categoryId = detail.id;
        } else if (detail.group === 'marcas') {
            this.filters.markId = detail.id;
        } else if (detail.group === 'precios') {
            this.filters.minPrice = detail.priceMin;
            this.filters.maxPrice = detail.priceMax;
        }
        this.persistFilters();
        this.render(1);
    }

    /**
     * Programa la búsqueda por texto con un retraso de 3 segundos tras el
     * último carácter ingresado. Si ya hay un temporizador pendiente se
     * reinicia para no ejecutar la búsqueda mientras el usuario sigue
     * escribiendo.
     */
    private scheduleSearch(): void {
        if (this.searchTimer !== null) {
            window.clearTimeout(this.searchTimer);
        }
        this.searchTimer = window.setTimeout(() => {
            this.searchTimer = null;
            const input = this.querySelector<HTMLInputElement>('#product-search');
            if (input) {
                this.runSearch(input);
            }
        }, 3000);
    }

    /**
     * Aplica el término de búsqueda actual al filtro y re-renderiza el
     * catálogo desde la primera página. Se ejecuta al presionar Enter, al
     * salir del campo o tras 3 segundos sin escribir.
     *
     * @param input Campo de búsqueda del cual se lee el valor.
     */
    private runSearch(input: HTMLInputElement): void {
        if (this.searchTimer !== null) {
            window.clearTimeout(this.searchTimer);
            this.searchTimer = null;
        }
        const term = input.value.trim();
        this.filters.search = term === '' ? undefined : term;
        this.persistFilters();
        this.render(1);
    }

    /**
     * Genera la página de inicio de forma asíncrona.
     *
     * Muestra un spinner mientras carga los datos, renderiza la plantilla,
     * escucha el evento `page-change` de la paginación para recargar y el
     * evento `filter-change` de los filtros laterales para aplicar filtros.
     *
     * @param page Página a mostrar.
     */
    render = async (page: number = 1) => {
        const title: string = this.getAttribute('title') || 'Home';

        this.replaceChildren();
        const spinner = document.createElement('spinner-component');
        this.appendChild(spinner);

        const { optionsCategories, optionsMarks, optionsPrices, productsData } = await this.loadData(LIMIT, page, this.filters);
        const loadStatus = typeof productsData === 'string' ? LoadStatus.ERROR : LoadStatus.SUCCESS;

        const template = new Template(this, title, optionsCategories, optionsMarks, optionsPrices, productsData, page, loadStatus, this.filters.search ?? '');
        template.render();

        const pagination = this.querySelector('pagination-nav');
        pagination?.addEventListener('page-change', (event) => {
            const { page: nextPage } = (event as CustomEvent<{ page: number }>).detail;
            this.render(nextPage);
        });

        const asides = this.querySelectorAll('aside-section');
        asides.forEach((aside) => {
            aside.addEventListener('filter-change', (event) => {
                const detail = (event as CustomEvent<FilterEventDetail>).detail;
                this.applyFilter(detail);
            });
        });

        const searchInput = this.querySelector<HTMLInputElement>('#product-search');
        if (searchInput) {
            searchInput.addEventListener('keyup', (event) => {
                if (event.key === 'Enter') {
                    this.runSearch(searchInput);
                } else {
                    this.scheduleSearch();
                }
            });
            searchInput.addEventListener('blur', () => {
                this.runSearch(searchInput);
            });
        }

        const cards = this.querySelectorAll('product-card');
        cards.forEach((card) => {
            card.addEventListener('add-to-cart', async (event) => {
                const detail = (event as CustomEvent<AddToCartDetail>).detail;
                if (detail.productId === undefined) {
                    return;
                }
                if (await CartStore.addItem(detail.productId)) {
                    toast('El producto ha sido agregado al carrito', 'success');
                } else {
                    toast('No se pudo agregar el producto al carrito.', 'error');
                }
            });
            card.addEventListener('product-view', (event) => {
                const detail = (event as CustomEvent<{ productId?: number }>).detail;
                if (detail.productId !== undefined) {
                    this.navigate(`/product/${detail.productId}`);
                }
            });
        });
    }

    /**
     * Navega hacia la ruta entregada sin recargar la página.
     *
     * @param path Ruta de destino.
     */
    private navigate(path: string): void {
        window.history.pushState({}, '', path);
        window.dispatchEvent(new PopStateEvent('popstate'));
    }
}

if (!customElements.get('home-page')) {
    customElements.define('home-page', HomePage);
}
