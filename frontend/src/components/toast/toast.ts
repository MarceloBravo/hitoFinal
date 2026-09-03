import { Render } from './render';

/**
 * Web Component de notificación Toast (no modal, auto-desvanecible).
 *
 * Acepta los atributos `message` (texto) y `type` (`'success'` o
 * `'error'`). Se agrega a un contenedor fijo gestionado por el helper
 * `toast()` y se elimina del DOM al completar su animación de salida.
 *
 * Uso:
 * @example
 * const t = document.createElement('toast-component');
 * t.setAttribute('message', 'Producto agregado');
 * t.setAttribute('type', 'success');
 * document.querySelector('.toast-container')?.appendChild(t);
 */
export class Toast extends HTMLElement {

    /** Duración (ms) que permanece visible antes de desvanecerse. */
    static readonly DURATION = 5000;

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
     * toast y programa su auto-dismiss, así como la escucha de media query
     * para pausar/reanudar cuando el usuario lo enfoca con el puntero/hover.
     */
    connectedCallback() {
        this.render();
        this.shadowRoot?.addEventListener('pointerenter', this.pause, { once: true });
        this.shadowRoot?.addEventListener('pointerleave', this.resume);
        this.addEventListener('toast-dismiss', this.handleDismiss);
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                this.shadowRoot?.querySelector('.toast')?.classList.add('show');
            });
        });
        this.scheduleDismiss();
    }

    /**
     * Se ejecuta cuando el componente se retira del DOM.
     */
    disconnectedCallback() {
        this.cancelDismiss();
        this.shadowRoot?.removeEventListener('pointerenter', this.pause);
        this.shadowRoot?.removeEventListener('pointerleave', this.resume);
        this.removeEventListener('toast-dismiss', this.handleDismiss);
    }

    /**
     * Mensaje a mostrar.
     */
    private get message(): string {
        return this.getAttribute('message') || '';
    }

    /**
     * Tipo de notificación: éxito o error.
     */
    private get type(): 'success' | 'error' {
        return this.getAttribute('type') === 'error' ? 'error' : 'success';
    }

    /** Identificador del temporizador de ocultado programado. */
    private dismissTimer: number | null = null;

    /** Tiempo (ms) sobrante tras una pausa. */
    private remaining = Toast.DURATION;

    /** Marca de tiempo del último inicio de cuenta atrás. */
    private lastStart = 0;

    /** Indica si el descuento está pausado por hover. */
    private paused = false;

    /**
     * Programa el auto-desvanecimiento del toast. Si está pausado (hover), se
     * reanuda la cuenta restante.
     */
    private scheduleDismiss(): void {
        if (this.paused) {
            return;
        }
        this.lastStart = Date.now();
        this.dismissTimer = window.setTimeout(() => void this.hide(), this.remaining);
    }

    /**
     * Cancela el temporizador de ocultado actual.
     */
    private cancelDismiss(): void {
        if (this.dismissTimer !== null) {
            window.clearTimeout(this.dismissTimer);
            this.dismissTimer = null;
        }
    }

    /**
     * Oculta manualmente el toast al pulsar el botón de cerrar.
     */
    private handleDismiss = (): void => {
        void this.hide();
    };

    /**
     * Pausa la cuenta atrás mientras el puntero está sobre el toast.
     */
    private pause = (): void => {
        this.cancelDismiss();
        this.remaining -= Date.now() - this.lastStart;
        this.paused = true;
    };

    /**
     * Reanuda la cuenta atrás al retirar el puntero del toast.
     */
    private resume = (): void => {
        if (!this.paused) {
            return;
        }
        this.paused = false;
        this.scheduleDismiss();
    };

    /**
     * Desvanece el toast y lo retira del DOM al finalizar la animación.
     */
    private async hide(): Promise<void> {
        this.cancelDismiss();
        const toast = this.shadowRoot?.querySelector('.toast');
        toast?.classList.remove('show');
        const delay = (toast && window.getComputedStyle(toast).transitionDuration)
            ? 400
            : 0;
        await new Promise((resolve) => setTimeout(resolve, delay));
        this.remove();
    }

    /**
     * Genera el HTML y aplica los estilos del toast en su shadow DOM.
     */
    render() {
        const root: ShadowRoot | null = this.shadowRoot;
        if (!root) {
            return;
        }
        new Render(root, this.message, this.type).render();
    }
}

if (!customElements.get('toast-component')) {
    customElements.define('toast-component', Toast);
}