import styles from './style.css?inline';

/**
 * Genera el HTML y aplica los estilos del componente toast dentro de su
 * shadow DOM. Según el {@code type} se aplica una clase con la paleta de
 * color correspondiente (éxito o error).
 */
export class Render {
    private root: ShadowRoot;
    private message: string;
    private type: 'success' | 'error';

    /**
     * @param root    Shadow root donde se renderiza el toast.
     * @param message Texto a mostrar en la notificación.
     * @param type    Tipo de notificación: éxito o error.
     */
    constructor(root: ShadowRoot, message: string, type: 'success' | 'error') {
        this.root = root;
        this.message = message;
        this.type = type;
    }

    /**
     * Renderiza el contenido del toast dentro del shadow root.
     *
     * @returns El shadow root con el toast renderizado.
     */
    render() {
        const role = this.type === 'error' ? 'alert' : 'status';
        const htmlString = `
            <div class="toast toast--${this.type}" role="${role}" aria-live="${role === 'alert' ? 'assertive' : 'polite'}">
                ${this.type === 'error'
                    ? '<span class="toast__icon" aria-hidden="true">⚠</span>'
                    : '<span class="toast__icon" aria-hidden="true">✓</span>'}
                <span class="toast__message">${this.escapeHtml(this.message)}</span>
                <button class="toast__close" type="button" data-close aria-label="Cerrar notificación">✕</button>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        this.root.replaceChildren(fragment);

        const style = document.createElement('style');
        style.textContent = styles;
        this.root.appendChild(style);

        this.root.querySelector<HTMLElement>('[data-close]')?.addEventListener('click', () => {
            this.root.host.dispatchEvent(new CustomEvent('toast-dismiss', { bubbles: true, composed: true }));
        });

        return this.root;
    }

    /**
     * Escapa caracteres especiales del HTML para evitar inyección.
     *
     * @param value Texto a escapar.
     * @returns Texto seguro para insertar en el HTML.
     */
    private escapeHtml(value: string): string {
        return value
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }
}