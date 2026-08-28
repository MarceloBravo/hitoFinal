import styles from './style.css?inline';

/**
 * Genera el HTML y los estilos del formulario de contacto en el light DOM.
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
     * Construye el HTML del formulario, lo inserta en el anfitrión
     * y adjunta los estilos correspondientes.
     *
     * @returns El elemento anfitrión con el contenido renderizado.
     */
    render(){
        const htmlString: string = `<div class="contact-page__container">
            <h2>Contacto</h2>
            <form id="contact-form">
                <div>
                    <label for="input-name">Nombre</label>
                    <input type="text" id="input-name" name="name" maxlength="20" class="contact-page__input"/>
                    <div class="contact-error" id="input-name_error"></div>
                </div>
                <div>
                    <label for="input-email">Email</label>
                    <input type="email" id="input-email" name="email" maxlength="150" class="contact-page__input"/>
                    <div class="contact-error" id="input-email_error"></div>
                </div>
                <div>
                    <label for="input-phone">Teléfono de contacto</label>
                    <input type="text" id="input-phone" name="phone" maxlength="20" class="contact-page__input"/>
                    <div class="contact-error" id="input-phone_error"></div>
                </div>
                <div>
                    <label for="input-message">Mensaje</label>
                    <textarea id="input-message" name="message" class="contact-page__textarea"></textarea>
                    <div class="contact-error" id="input-message_error"></div>
                </div>
                <button type="submit" id="btn-send" class="btn btn-submit contact-page__submit">Enviar</button>
            </form>
        </div>`;

        const fragment = document.createRange().createContextualFragment(htmlString);
        this.host.replaceChildren(fragment);

        const style = document.createElement('style');
        style.textContent = styles;
        this.host.appendChild(style);

        return this.host;
    }
}