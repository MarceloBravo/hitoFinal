import { Template } from './template';
import { marksService } from '../../../services/marksService';
import type { MarkInterface } from '../../../interfaces/MarkInterface';

/**
 * Web Component de la página del mantenedor de marcas (formulario).
 *
 * Sirve tanto para registrar una marca nueva (`/admin_home/marks/new`) como para editar
 * una existente (`/admin_home/marks/edit/{id}`). Usa Light DOM y valida los campos
 * antes de enviarlos a la API.
 */
export class MarksForm extends HTMLElement {

    /** Identificador de la marca en modo edición, o `null` en modo creación. */
    private markId: number | null = null;
    /** Datos de la marca cargada en modo edición. */
    private mark: MarkInterface | null = null;

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
        this.classList.add('marks-form-page');
        this.markId = this.resolveIdFromPath();

        if (this.markId === null) {
            this.renderNew();
            return;
        }

        void this.loadMark(this.markId);
    }

    /**
     * Extrae el identificador de la marca desde la ruta `/admin_home/marks/edit/{id}`.
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
        (new Template(this)).render('Nueva Marca', '', true);
        this.configListeners();
    }

    /**
     * Carga la marca a editar y renderiza el formulario con sus datos.
     *
     * @param id Identificador de la marca.
     */
    private async loadMark(id: number): Promise<void> {
        const response = await marksService.getById(id);

        if (response.ok && response.data?.data) {
            this.mark = response.data.data;
            (new Template(this)).render('Editar Marca', this.mark.name, this.mark.active);
            this.configListeners();
            return;
        }

        (new Template(this)).render('Editar Marca', '', true);
        this.configListeners();
        const message = response.ok
            ? 'No se pudo cargar la marca.'
            : (response.data || 'No se pudo cargar la marca.');
        this.showServerError(message);
    }

    /**
     * Configura los listeners del formulario y de los campos de entrada.
     *
     * Al enviar se valida la información y se guarda la marca; los errores de
     * cada campo se limpian cuando el usuario modifica su valor.
     */
    configListeners(): void {
        const form = document.getElementById('mark-form');
        if (form) {
            form.addEventListener('submit', (event) => {
                event.preventDefault();
                void this.submit();
            });
        }

        const inputName = document.querySelector<HTMLInputElement>('#mark-name');
        const divNameError: HTMLElement | null = document.getElementById('mark-name_error');
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
        const divError: HTMLElement | null = document.getElementById('mark-form_error');
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
        const divError: HTMLElement | null = document.getElementById('mark-form_error');
        if (divError) {
            divError.innerText = message;
        }
    }

    /**
     * Valida los campos del formulario de marcas.
     *
     * @returns `true` si el formulario es válido, `false` en caso contrario.
     */
    validateData(): boolean {
        let errors = 0;

        const inputName = document.querySelector<HTMLInputElement>('#mark-name');
        const divNameError: HTMLElement | null = document.getElementById('mark-name_error');
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
     * @returns Datos de la marca listos para enviar a la API.
     */
    private readForm(): { name: string; active: boolean } {
        const inputName = document.querySelector<HTMLInputElement>('#mark-name');
        const inputActive = document.querySelector<HTMLInputElement>('#mark-active');
        return {
            name: inputName?.value.trim() ?? '',
            active: inputActive?.checked ?? false,
        };
    }

    /**
     * Valida y guarda la marca (crea o actualiza según el modo).
     */
    private async submit(): Promise<void> {
        if (!this.validateData()) {
            return;
        }

        const { name, active } = this.readForm();
        this.clearServerError();

        const response = this.markId === null
            ? await marksService.create({ name, active })
            : await marksService.update(this.markId, { name, active });

        if (response.ok) {
            window.alert(this.markId === null ? 'Marca registrada correctamente.' : 'Marca actualizada correctamente.');
            this.navigate('/admin_home/marks');
            return;
        }

        this.showServerError(response.data || 'No se pudo guardar la marca.');
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

if (!customElements.get('marks-form-page')) {
    customElements.define('marks-form-page', MarksForm);
}
