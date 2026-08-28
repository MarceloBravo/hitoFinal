import { Template } from './template';
import { validateEmail, validatePhone } from '../../utils/validations';

/**
 * Web Component de la página de contacto.
 *
 * Implementa un formulario validado usando Light DOM (sin Shadow DOM).
 */
export class ContactPage extends HTMLElement{

    constructor(){
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
        this.classList.add('contact-page');
        this.render();
    }

    /**
     * Configura los listeners del formulario y de los campos de entrada.
     *
     * Al enviar el formulario se valida la información; los errores de cada
     * campo se limpian cuando el usuario modifica su valor.
     */
    configListeners(){
        const form = document.getElementById('contact-form');
        if(form){
            form.addEventListener('submit', (event) => {
                event.preventDefault();
                if(this.validateData()){
                    console.log('Formulario enviado');
                    alert('Formulario enviado');
                }
            });
        }

        const inputName = document.querySelector<HTMLInputElement>("#input-name");
        const divNameError: HTMLElement | null = document.getElementById("input-name_error");
            if(inputName && divNameError){
                inputName.addEventListener('change', () => {
                divNameError.innerText = '';
            })
        } 
        const inputEmail = document.querySelector<HTMLInputElement>("#input-email");
        const divEmailError: HTMLElement | null = document.getElementById("input-email_error");
            if(inputEmail && divEmailError){
                inputEmail.addEventListener('change', () => {
                divEmailError.innerText = '';
            })
        } 
        const inputPhone = document.querySelector<HTMLInputElement>("#input-phone");
        const divPhoneError: HTMLElement | null = document.getElementById("input-phone_error");
            if(inputPhone && divPhoneError){
                inputPhone.addEventListener('change', () => {
                divPhoneError.innerText = '';
            })
        } 
        const inputMessage = document.querySelector<HTMLInputElement>("#input-message");
        const divMessageError: HTMLElement | null = document.getElementById("input-message_error");
            if(inputMessage && divMessageError){
                inputMessage.addEventListener('change', () => {
                divMessageError.innerText = '';
            })
        } 
    }

    /**
     * Valida los campos del formulario de contacto.
     *
     * Muestra mensajes de error por campo y enfoca el primer campo con error.
     *
     * @returns `true` si todos los campos son válidos, `false` en caso contrario.
     */
    validateData(): boolean{
        let errors: number = 0;

        
        
        const inputMessage = document.querySelector<HTMLInputElement>("#input-message");
        const divMessageError: HTMLElement | null = document.getElementById("input-message_error");
        if(inputMessage && divMessageError){
            let msgError = inputMessage.value.trim().length < 3 ? "El mensaje es obligatorio" : '';
            divMessageError.innerText = msgError;
            if(msgError)inputMessage.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }
        
        const inputPone = document.querySelector<HTMLInputElement>("#input-phone");
        const divPhoneError: HTMLElement | null = document.getElementById("input-phone_error");
        if(inputPone && divPhoneError){
            let msgError = !validatePhone(inputPone.value) ? "Ingresa un número de teléfono válido" : '';
            divPhoneError.innerText = msgError;
            if(msgError)inputPone.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }

        const inputEmail = document.querySelector<HTMLInputElement>("#input-email");
        const divEmailError: HTMLElement | null = document.getElementById("input-email_error");
        if(inputEmail && divEmailError){
            let msgError = !validateEmail(inputEmail.value) ? "Ingresa una dirección de email válida." : '';
            divEmailError.innerText = msgError;
            if(msgError)inputEmail.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }
        
        const inputName = document.querySelector<HTMLInputElement>("#input-name");
        const divNameError: HTMLElement | null = document.getElementById("input-name_error");
        if(inputName && divNameError){
            let msgError = inputName.value.trim().length < 3 ? "El nombre debe tener almenos 3 carácteres" : '';
            divNameError.innerText = msgError;
            if(msgError)inputName.focus();
            errors += msgError.length > 0 ? 1 : 0;
        }
        
        return errors === 0;
    }

    /**
     * Genera el contenido de la página y configura sus listeners.
     */
    render(){
        (new Template(this)).render();
        this.configListeners();
    }

}

if (!customElements.get('contact-page')) {
    customElements.define('contact-page', ContactPage);
}