import styles from './style.css?inline';

/**
 * Genera el HTML y aplica los estilos del cuadro de diálogo dentro de su
 * shadow DOM. Según el {@code type} se muestran dos botones (modo
 * confirmación) o únicamente el de "Aceptar" (modo mensaje).
 */
export class Render {
    private root: ShadowRoot;
    private message: string;
    private type: 'confirm' | 'alert';

    /**
     * @param root    Shadow root donde se renderiza el diálogo.
     * @param message Texto a mostrar en el diálogo.
     * @param type    Modo del diálogo: confirmación o simple mensaje.
     */
    constructor(root: ShadowRoot, message: string, type: 'confirm' | 'alert') {
        this.root = root;
        this.message = message;
        this.type = type;
    }

    /**
     * Renderiza el contenido (overlay + panel) dentro del shadow root.
     *
     * @returns El shadow root con el diálogo renderizado.
     */
    render() {
        const buttons = this.type === 'confirm'
            ? `
                <button class="dialog-btn dialog-btn--primary" type="button" data-action="confirm">Aceptar</button>
                <button class="dialog-btn dialog-btn--secondary" type="button" data-action="cancel">Cancelar</button>
            `
            : `
                <button class="dialog-btn dialog-btn--primary" type="button" data-action="confirm">Aceptar</button>
            `;

        const htmlString = `
            <div class="dialog-overlay" data-overlay></div>
            <div class="dialog" role="dialog" aria-modal="true" aria-labelledby="dialog-title">
                <div class="dialog-title" id="dialog-title"></div>
                <p class="dialog-message">${this.escapeHtml(this.message)}</p>
                <div class="dialog-actions">
                    ${buttons}
                </div>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        this.root.replaceChildren(fragment);

        const style = document.createElement('style');
        style.textContent = styles;
        this.root.appendChild(style);

        // Se presiona el estado de partida y se aplica la clase en el frame
        // siguiente para animar la entrada (fade + leve elevación).
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                this.root.querySelector('.dialog')?.classList.add('open');
                this.root.querySelector('.dialog-overlay')?.classList.add('open');
            });
        });

        // Foco inicial en el primer botón para poder cerrar con teclado.
        this.root.querySelector<HTMLButtonElement>('button[data-action]')?.focus();

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