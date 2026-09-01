import { Template } from './template';
import { AuthService } from '../../services/authService';
import { AuthStore } from '../../store/authStore';
import { validateEmail } from '../../utils/validations';

/**
 * Web Component de la página de acceso al backoffice.
 *
 * Renderiza el formulario de autenticación de usuarios usando Light DOM
 * (sin Shadow DOM). Al validar las credenciales guarda la sesión en el
 * {@link AuthStore} y navega al home del administrador (`/admin_home`).
 */
export class AdminLogin extends HTMLElement {

    constructor() {
        super();
    }

    /**
     * Atributos observados para reaccionar a cambios en el DOM.
     */
    static get observedAttributes() {
        return [
            'title' // Atributo para el título de la página
        ];
    }

    /**
     * Se ejecuta cuando el componente se inserta en el DOM.
     */
    connectedCallback() {
        this.classList.add('admin-login');
        this.render();
    }

    /**
     * Configura los listeners del formulario de autenticación.
     *
     * Al enviar el formulario se validan los campos y se intenta iniciar
     * sesión; los errores de los campos y del servidor se limpian cuando
     * el usuario vuelve a modificar sus valores.
     */
    configListeners() {
        const form = document.getElementById('login-form');
        if (form) {
            form.addEventListener('submit', (event) => {
                event.preventDefault();
                void this.submit();
            });
        }

        const inputEmail = document.querySelector<HTMLInputElement>('#login-email');
        const divEmailError: HTMLElement | null = document.getElementById('login-email_error');
        if (inputEmail && divEmailError) {
            inputEmail.addEventListener('change', () => {
                divEmailError.innerText = '';
                this.clearServerError();
            });
        }

        const inputPassword = document.querySelector<HTMLInputElement>('#login-password');
        const divPasswordError: HTMLElement | null = document.getElementById('login-password_error');
        if (inputPassword && divPasswordError) {
            inputPassword.addEventListener('change', () => {
                divPasswordError.innerText = '';
                this.clearServerError();
            });
        }
    }

    /**
     * Limpia el mensaje de error devuelto por el servidor.
     */
    private clearServerError(): void {
        const divError: HTMLElement | null = document.getElementById('login-error');
        if (divError) {
            divError.innerText = '';
        }
    }

    /**
     * Valida los campos del formulario de autenticación.
     *
     * Muestra mensajes de error por campo y enfoca el primer campo con error.
     *
     * @returns `true` si todos los campos son válidos, `false` en caso contrario.
     */
    validateData(): boolean {
        let errors: number = 0;

        const inputEmail = document.querySelector<HTMLInputElement>('#login-email');
        const divEmailError: HTMLElement | null = document.getElementById('login-email_error');
        if (inputEmail && divEmailError) {
            const msgError = validateEmail(inputEmail.value) ? '' : 'Ingresa una dirección de email válida.';
            divEmailError.innerText = msgError;
            if (msgError) inputEmail.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }

        const inputPassword = document.querySelector<HTMLInputElement>('#login-password');
        const divPasswordError: HTMLElement | null = document.getElementById('login-password_error');
        if (inputPassword && divPasswordError) {
            const msgError = inputPassword.value.trim().length < 1 ? 'La contraseña es obligatoria.' : '';
            divPasswordError.innerText = msgError;
            if (msgError) inputPassword.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }

        return errors === 0;
    }

    /**
     * Envía las credenciales al backend y, si son válidas, guarda la sesión
     * y navega al home del administrador.
     *
     * Si el servidor rechaza las credenciales se muestra su mensaje en el
     * bloque de error general del formulario.
     */
    private async submit(): Promise<void> {
        if (!this.validateData()) {
            return;
        }

        const inputEmail = document.querySelector<HTMLInputElement>('#login-email');
        const inputPassword = document.querySelector<HTMLInputElement>('#login-password');
        const divError: HTMLElement | null = document.getElementById('login-error');
        if (!inputEmail || !inputPassword) {
            return;
        }

        const response = await AuthService.login(inputEmail.value, inputPassword.value);

        if (response.ok && response.data) {
            AuthStore.saveSession(response.data);
            window.history.pushState({}, '', '/admin_home');
            window.dispatchEvent(new PopStateEvent('popstate'));
            return;
        }

        if (!response.ok && divError) {
            divError.innerText = response.data || 'No se pudo iniciar sesión.';
        }
    }

    /**
     * Genera el contenido de la página y configura sus listeners.
     */
    render() {
        (new Template(this)).render();
        this.configListeners();
    }
}

if (!customElements.get('admin-login-page')) {
    customElements.define('admin-login-page', AdminLogin);
}