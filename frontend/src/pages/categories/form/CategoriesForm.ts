import { Template } from './template';
import { categoriesService } from '../../../services/categoriesService';
import type { CategoriesInterface } from '../../../interfaces/CategoriesInterface';

/**
 * Web Component de la página del mantenedor de categorías (formulario).
 *
 * Sirve tanto para registrar una categoría nueva (`/admin_home/categories/new`) como para editar
 * una existente (`/admin_home/categories/edit/{id}`). Usa Light DOM y valida los campos
 * antes de enviarlos a la API.
 */
export class CategoriesForm extends HTMLElement {

    /** Identificador de la categoría en modo edición, o `null` en modo creación. */
    private categoryId: number | null = null;
    /** Datos de la categoría cargada en modo edición. */
    private category: CategoriesInterface | null = null;

    /**
     * Atributos observados para reaccionar a cambios en el DOM.
     */
    static get observedAttributes() {
        return ['title'];
    }

    /**
     * Se ejecuta cuando el componente se inserta en el DOM.
     */
    connectedCallback() {
        this.classList.add('categories-form-page');
        this.categoryId = this.resolveIdFromPath();

        if (this.categoryId === null) {
            this.renderNew();
            return;
        }

        void this.loadCategory(this.categoryId);
    }

    /**
     * Extrae el identificador de la categoría desde la ruta `/admin_home/categories/edit/{id}`.
     *
     * @returns El id parseado o `null` si la ruta no lo contiene.
     */
    private resolveIdFromPath(): number | null {
        const segments = window.location.pathname.split('/').filter(Boolean);
        const editIndex = segments.findIndex((seg) => seg === 'edit');
        if (editIndex === -1) {
            return null;
        }
        const id = Number(segments[editIndex + 1]);
        return Number.isInteger(id) && id > 0 ? id : null;
    }

    /**
     * Renderiza el formulario en modo creación.
     */
    private renderNew(): void {
        (new Template(this)).render('Nueva Categoría', '', true);
        this.configListeners();
    }

    /**
     * Carga la categoría a editar y renderiza el formulario con sus datos.
     *
     * @param id Identificador de la categoría.
     */
    private async loadCategory(id: number): Promise<void> {
        const response = await categoriesService.getById(id);

        if (response.ok && response.data?.data) {
            this.category = response.data.data;
            (new Template(this)).render('Editar Categoría', this.category.name, this.category.active);
            this.configListeners();
            return;
        }

        (new Template(this)).render('Editar Categoría', '', true);
        this.configListeners();
        const message = response.ok
            ? 'No se pudo cargar la categoría.'
            : (response.data || 'No se pudo cargar la categoría.');
        this.showServerError(message);
    }

    /**
     * Configura los listeners del formulario y de los campos de entrada.
     *
     * Al enviar se valida la información y se guarda la categoría; los errores de
     * cada campo se limpian cuando el usuario modifica su valor.
     */
    configListeners(): void {
        const form = document.getElementById('category-form');
        if (form) {
            form.addEventListener('submit', (event) => {
                event.preventDefault();
                void this.submit();
            });
        }

        const inputName = document.querySelector<HTMLInputElement>('#category-name');
        const divNameError: HTMLElement | null = document.getElementById('category-name_error');
        if (inputName && divNameError) {
            inputName.addEventListener('change', () => {
                divNameError.innerText = '';
                this.clearServerError();
            });
        }
    }

    /**
     * Limpia el mensaje de error general del formulario.
     */
    private clearServerError(): void {
        const divError: HTMLElement | null = document.getElementById('category-form_error');
        if (divError) {
            divError.innerText = '';
        }
    }

    /**
     * Muestra un mensaje de error general en el formulario.
     *
     * @param message Mensaje a mostrar.
     */
    private showServerError(message: string): void {
        const divError: HTMLElement | null = document.getElementById('category-form_error');
        if (divError) {
            divError.innerText = message;
        }
    }

    /**
     * Valida los campos del formulario de categorías.
     *
     * @returns `true` si el formulario es válido, `false` en caso contrario.
     */
    validateData(): boolean {
        let errors = 0;

        const inputName = document.querySelector<HTMLInputElement>('#category-name');
        const divNameError: HTMLElement | null = document.getElementById('category-name_error');
        if (inputName && divNameError) {
            const msgError = inputName.value.trim().length < 3
                ? 'El nombre debe tener al menos 3 caracteres.'
                : '';
            divNameError.innerText = msgError;
            if (msgError) inputName.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }

        return errors === 0;
    }

    /**
     * Lee el estado actual del formulario.
     *
     * @returns Datos de la categoría listos para enviar a la API.
     */
    private readForm(): { name: string; active: boolean } {
        const inputName = document.querySelector<HTMLInputElement>('#category-name');
        const inputActive = document.querySelector<HTMLInputElement>('#category-active');
        return {
            name: inputName?.value.trim() ?? '',
            active: inputActive?.checked ?? false,
        };
    }

    /**
     * Valida y guarda la categoría (crea o actualiza según el modo).
     */
    private async submit(): Promise<void> {
        if (!this.validateData()) {
            return;
        }

        const { name, active } = this.readForm();
        this.clearServerError();

        const response = this.categoryId === null
            ? await categoriesService.create({ name, active })
            : await categoriesService.update(this.categoryId, { name, active });

        if (response.ok) {
            window.alert(this.categoryId === null ? 'Categoría registrada correctamente.' : 'Categoría actualizada correctamente.');
            this.navigate('/admin_home/categories');
            return;
        }

        this.showServerError(response.data || 'No se pudo guardar la categoría.');
    }

    /**
     * Navega hacia la ruta entregada.
     *
     * @param path Ruta de destino.
     */
    private navigate(path: string): void {
        window.history.pushState({}, '', path);
        window.dispatchEvent(new PopStateEvent('popstate'));
    }
}

if (!customElements.get('categories-form-page')) {
    customElements.define('categories-form-page', CategoriesForm);
}
