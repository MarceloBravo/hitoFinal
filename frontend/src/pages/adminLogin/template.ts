import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del formulario de autenticación en el light DOM.
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
     * Construye el HTML del formulario de acceso, lo inserta en el anfitrión
     * y adjunta los estilos correspondientes.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(){
        const htmlString: string = `<div class="admin-login__container">
            <h2>Acceso Administración</h2>
            <p class="admin-login__intro">Ingresa con tu cuenta para gestionar el backoffice de la tienda.</p>
            <form id="login-form">
                <div>
                    <label for="login-email">Email</label>
                    <input type="email" id="login-email" name="email" maxlength="150" class="admin-login__input" autocomplete="username"/>
                    <div class="admin-login-error" id="login-email_error"></div>
                </div>
                <div>
                    <label for="login-password">Contraseña</label>
                    <input type="password" id="login-password" name="password" maxlength="72" class="admin-login__input" autocomplete="current-password"/>
                    <div class="admin-login-error" id="login-password_error"></div>
                </div>
                <div class="admin-login-error admin-login__server" id="login-error" role="alert"></div>
                <button type="submit" class="btn btn-submit admin-login__submit">Ingresar</button>
            </form>
        </div>`;

        const fragment = document.createRange().createContextualFragment(htmlString);
        const style = document.createElement('style');
        style.textContent = styles;

        this.host.replaceChildren(style, fragment);

        return this.host;
    }
}