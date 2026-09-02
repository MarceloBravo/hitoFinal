import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del formulario de usuarios en el light DOM.
 */
export class Template {
    private host: HTMLElement;

    /**
     * @param host Elemento anfitrión donde se renderiza la plantilla.
     */
    constructor(host: HTMLElement) {
        this.host = host;
    }

    /**
     * Construye el HTML del formulario (nombre, correo, contraseña, rol y
     * estado activo), lo inserta en el anfitrión y adjunta los estilos.
     *
     * @param heading  Texto del encabezado ("Nuevo Usuario" o "Editar Usuario").
     * @param name     Valor actual del campo nombre.
     * @param email    Valor actual del campo correo.
     * @param role     Valor actual del rol (USER o ADMIN).
     * @param active   Valor actual del estado activo.
     * @param isEdit   `true` si se está editando (la contraseña es opcional).
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(heading: string, name: string, email: string, role: string, active: boolean, isEdit: boolean) {
        const htmlString: string = `
            <div class="users-form__container">
                <h2 class="users-form__title">${heading}</h2>
                <form id="user-form" novalidate>
                    <div class="users-form__field">
                        <label for="user-name">Nombre</label>
                        <input type="text" id="user-name" name="name" maxlength="100" value="${this.escapeHtml(name)}" class="users-form__input"/>
                        <div class="users-form__error" id="user-name_error"></div>
                    </div>
                    <div class="users-form__field">
                        <label for="user-email">Correo electrónico</label>
                        <input type="email" id="user-email" name="email" maxlength="150" value="${this.escapeHtml(email)}" class="users-form__input"/>
                        <div class="users-form__error" id="user-email_error"></div>
                    </div>
                    <div class="users-form__field">
                        <label for="user-password">Contraseña ${isEdit ? '(dejar en blanco para no cambiar)' : ''}</label>
                        <input type="password" id="user-password" name="password" maxlength="100" class="users-form__input"/>
                        <div class="users-form__error" id="user-password_error"></div>
                    </div>
                    <div class="users-form__field">
                        <label for="user-role">Rol</label>
                        <select id="user-role" name="role" class="users-form__input users-form__select">
                            <option value="USER" ${role === 'USER' ? 'selected' : ''}>Usuario (USER)</option>
                            <option value="ADMIN" ${role === 'ADMIN' ? 'selected' : ''}>Administrador (ADMIN)</option>
                        </select>
                        <div class="users-form__error" id="user-role_error"></div>
                    </div>
                    <div class="users-form__field">
                        <label class="users-form__checkbox">
                            <input type="checkbox" id="user-active" name="active" ${active ? 'checked' : ''}/>
                            <span>Usuario activo</span>
                        </label>
                    </div>
                    <div class="users-form__error users-form__server" id="user-form_error" role="alert"></div>
                    <div class="users-form__actions">
                        <button type="submit" class="users-form__btn users-form__btn--primary" id="btn-save">Guardar</button>
                        <a href="/admin_home/users" data-link>
                            <button type="button" class="users-form__btn users-form__btn--ghost" id="btn-cancel">Cancelar</button>
                        </a>
                    </div>
                </form>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        const style = document.createElement('style');
        style.textContent = styles;

        this.host.replaceChildren(style, fragment);

        return this.host;
    }

    /**
     * Escapa caracteres especiales del HTML para evitar inyección.
     *
     * @param value Texto a escapar.
     * @returns Texto seguro para insertar en un atributo/valor HTML.
     */
    private escapeHtml(value: string): string {
        return value
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }
}
