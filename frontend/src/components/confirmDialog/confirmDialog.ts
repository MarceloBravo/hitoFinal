import { Render } from './render';

/**
 * Web Component de cuadro de diálogo/confirmación reutilizable.
 *
 * Acepta los atributos `message` (texto a mostrar) y `type`
 * (`'confirm'` por defecto para mostrar "Aceptar" y "Cancelar", o
 * `'alert'` para mostrar únicamente "Aceptar"). Se crea dinámicamente y
 * emite el evento `custom-confirm-dialog` (bubbles+composed) indicando en
 * `detail.confirmed` la opción elegida, para luego eliminarse del DOM.
 *
 * Uso:
 * @example
 * const dlg = document.createElement('confirm-dialog');
 * dlg.setAttribute('message', '¿Continuar?');
 * dlg.setAttribute('type', 'confirm');
 * document.body.appendChild(dlg);
 */
export class ConfirmDialog extends HTMLElement {

    /** Evento emitido al resolver el diálogo. */
    static readonly RESULT_EVENT = 'custom-confirm-dialog';

    /**
     * Atributos observados para reaccionar a cambios en el DOM.
     */
    static get observedAttributes() {
        return ['message', 'type'];
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    /**
     * Se ejecuta cuando el componente se inserta en el DOM. Renderiza el
     * diálogo y registra los listeners de clic y teclado.
     */
    connectedCallback() {
        this.render();
        this.shadowRoot?.addEventListener('click', this.handleClick);
        this.addEventListener('keydown', this.handleKeydown);
    }

    /**
     * Se ejecuta cuando el componente se retira del DOM.
     */
    disconnectedCallback() {
        this.shadowRoot?.removeEventListener('click', this.handleClick);
        this.removeEventListener('keydown', this.handleKeydown);
    }

    /**
     * Texto de mensaje del diálogo o un valor por defecto.
     */
    private get message(): string {
        return this.getAttribute('message') || '¿Desea continuar?';
    }

    /**
     * Modo de presentación: {@code 'confirm'} muestra Aceptar/Cancelar,
     * {@code 'alert'} solo Aceptar.
     */
    private get type(): 'confirm' | 'alert' {
        return this.getAttribute('type') === 'alert' ? 'alert' : 'confirm';
    }

    /**
     * Gestiona los clics sobre los botones y el overlay.
     *
     * @param event evento de clic.
     */
    private handleClick = (event: Event): void => {
        const target = event.target as HTMLElement;

        if (target.closest('[data-overlay]')) {
            this.resolve(false);
            return;
        }

        const action = target.closest<HTMLElement>('[data-action]')?.dataset.action;
        if (action === 'confirm') {
            this.resolve(true);
        } else if (action === 'cancel') {
            this.resolve(false);
        }
    };

    /**
     * Permite cerrar el diálogo con la tecla Escape.
     *
     * @param event evento de teclado.
     */
    private handleKeydown = (event: KeyboardEvent): void => {
        if (event.key === 'Escape') {
            event.preventDefault();
            this.resolve(false);
        }
    };

    /**
     * Emite el evento de resultado con la opción elegida y se elimina del DOM.
     *
     * @param confirmed {@code true} al pulsar "Aceptar", {@code false} si no.
     */
    private resolve(confirmed: boolean): void {
        this.dispatchEvent(new CustomEvent(ConfirmDialog.RESULT_EVENT, {
            detail: { confirmed, message: this.message },
            bubbles: true,
            composed: true,
        }));
        this.remove();
    }

    /**
     * Genera el HTML y aplica los estilos del diálogo en su shadow DOM.
     */
    render() {
        const root: ShadowRoot | null = this.shadowRoot;
        if (!root) {
            return;
        }
        new Render(root, this.message, this.type).render();
    }
}

if (!customElements.get('confirm-dialog')) {
    customElements.define('confirm-dialog', ConfirmDialog);
}