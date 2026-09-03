import { Template } from './template';
import { usersService } from '../../../services/usersService';
import { alertMessage } from '../../../utils/dialog';
import type { UserInterface } from '../../../interfaces/UserInterface';
import type { UserRequestInterface } from '../../../interfaces/UserRequestInterface';

/**
 * Web Component de la página del mantenedor de usuarios (formulario).
 *
 * Sirve tanto para registrar un usuario nuevo (`/admin_home/users/new`) como
 * para editar una existente (`/admin_home/users/edit/{id}`). Usa Light DOM y
 * valida los campos antes de enviarlos a la API. El rol se selecciona mediante
 * un `<select>` nativo con las opciones USER y ADMIN.
 */
export class UsersForm extends HTMLElement {

    /** Identificador del usuario en modo edición, o `null` en modo creación. */
    private userId: number | null = null;
    /** Datos del usuario cargado en modo edición. */
    private user: UserInterface | null = null;

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
        this.classList.add('users-form-page');
        this.userId = this.resolveIdFromPath();

        if (this.userId === null) {
            this.renderNew();
            return;
        }

        void this.loadUser(this.userId);
    }

    /**
     * Extrae el identificador del usuario desde la ruta `/admin_home/users/edit/{id}`.
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
        (new Template(this)).render('Nuevo Usuario', '', '', 'USER', true, false);
        this.configListeners();
    }

    /**
     * Carga el usuario a editar y renderiza el formulario con sus datos.
     *
     * @param id Identificador del usuario.
     */
    private async loadUser(id: number): Promise<void> {
        const response = await usersService.getById(id);

        if (response.ok && response.data?.data) {
            this.user = response.data.data;
            (new Template(this)).render('Editar Usuario',
                this.user.name, this.user.email, this.user.role, this.user.active, true);
            this.configListeners();
            return;
        }

        (new Template(this)).render('Editar Usuario', '', '', 'USER', true, true);
        this.configListeners();
        const message = response.ok
            ? 'No se pudo cargar el usuario.'
            : (response.data || 'No se pudo cargar el usuario.');
        this.showServerError(message);
    }

    /**
     * Configura los listeners del formulario y de los campos de entrada.
     *
     * Al enviar se valida la información y se guarda el usuario; los errores de
     * cada campo se limpian cuando el usuario modifica su valor.
     */
    configListeners(): void {
        const form = document.getElementById('user-form');
        if (form) {
            form.addEventListener('submit', (event) => {
                event.preventDefault();
                void this.submit();
            });
        }

        const clearOnChange = (inputId: string, errorId: string) => {
            const input = document.querySelector<HTMLInputElement>(`#${inputId}`);
            const divError: HTMLElement | null = document.getElementById(errorId);
            if (input && divError) {
                input.addEventListener('change', () => {
                    divError.innerText = '';
                    this.clearServerError();
                });
            }
        };

        clearOnChange('user-name', 'user-name_error');
        clearOnChange('user-email', 'user-email_error');
        clearOnChange('user-password', 'user-password_error');
        clearOnChange('user-role', 'user-role_error');
    }

    /**
     * Limpia el mensaje de error general del formulario.
     */
    private clearServerError(): void {
        const divError: HTMLElement | null = document.getElementById('user-form_error');
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
        const divError: HTMLElement | null = document.getElementById('user-form_error');
        if (divError) {
            divError.innerText = message;
        }
    }

    /**
     * Valida los campos del formulario de usuarios.
     *
     * @returns `true` si el formulario es válido, `false` en caso contrario.
     */
    validateData(): boolean {
        let errors = 0;

        const inputName = document.querySelector<HTMLInputElement>('#user-name');
        const divNameError: HTMLElement | null = document.getElementById('user-name_error');
        if (inputName && divNameError) {
            const msgError = inputName.value.trim().length < 3
                ? 'El nombre debe tener al menos 3 caracteres.'
                : '';
            divNameError.innerText = msgError;
            if (msgError) inputName.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }

        const inputEmail = document.querySelector<HTMLInputElement>('#user-email');
        const divEmailError: HTMLElement | null = document.getElementById('user-email_error');
        if (inputEmail && divEmailError) {
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            const msgError = !emailPattern.test(inputEmail.value.trim())
                ? 'El correo no tiene un formato válido.'
                : '';
            divEmailError.innerText = msgError;
            if (msgError && errors === 0) inputEmail.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }

        const inputPassword = document.querySelector<HTMLInputElement>('#user-password');
        const divPasswordError: HTMLElement | null = document.getElementById('user-password_error');
        if (inputPassword && divPasswordError) {
            const password = inputPassword.value;
            const msgError = this.userId === null && password.length < 8
                ? 'La contraseña debe tener al menos 8 caracteres.'
                : '';
            divPasswordError.innerText = msgError;
            if (msgError && errors === 0) inputPassword.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }

        const selectRole = document.querySelector<HTMLSelectElement>('#user-role');
        const divRoleError: HTMLElement | null = document.getElementById('user-role_error');
        if (selectRole && divRoleError) {
            const msgError = !selectRole.value ? 'Debe seleccionar un rol.' : '';
            divRoleError.innerText = msgError;
            if (msgError && errors === 0) selectRole.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }

        return errors === 0;
    }

    /**
     * Lee el estado actual del formulario.
     *
     * @returns Datos del usuario listos para enviar a la API.
     */
    private readForm(): UserRequestInterface {
        const inputName = document.querySelector<HTMLInputElement>('#user-name');
        const inputEmail = document.querySelector<HTMLInputElement>('#user-email');
        const inputPassword = document.querySelector<HTMLInputElement>('#user-password');
        const selectRole = document.querySelector<HTMLSelectElement>('#user-role');
        const inputActive = document.querySelector<HTMLInputElement>('#user-active');

        const payload: UserRequestInterface = {
            name: inputName?.value.trim() ?? '',
            email: inputEmail?.value.trim() ?? '',
            role: selectRole?.value ?? 'USER',
            active: inputActive?.checked ?? false,
        };

        const password = inputPassword?.value ?? '';
        if (password) {
            payload.password = password;
        }

        return payload;
    }

    /**
     * Valida y guarda el usuario (crea o actualiza según el modo).
     */
    private async submit(): Promise<void> {
        if (!this.validateData()) {
            return;
        }

        const payload = this.readForm();
        this.clearServerError();

        const response = this.userId === null
            ? await usersService.create(payload)
            : await usersService.update(this.userId, payload);

        if (response.ok) {
            await alertMessage(this.userId === null
                ? 'Usuario registrado correctamente.'
                : 'Usuario actualizado correctamente.');
            this.navigate('/admin_home/users');
            return;
        }

        this.showServerError(response.data || 'No se pudo guardar el usuario.');
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

if (!customElements.get('users-form-page')) {
    customElements.define('users-form-page', UsersForm);
}
