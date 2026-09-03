// Importación por efecto lateral: registra el custom element `toast-component`.
import '../components/toast';

/**
 * Nombre del contenedor fijo donde se apilan las notificaciones Toast.
 */
const CONTAINER_ID = 'toast-container';

/**
 * Devuelve el contenedor fijo de notificaciones, creándolo si no existe
 * (arriba a la derecha). Los toasts se apilan en orden de aparición.
 *
 * @returns El contenedor de notificaciones.
 */
function ensureContainer(): HTMLElement {
    let container = document.getElementById(CONTAINER_ID);
    if (!container) {
        container = document.createElement('div');
        container.id = CONTAINER_ID;
        const style = document.createElement('style');
        style.textContent = `
            #${CONTAINER_ID} {
                position: fixed;
                top: 1rem;
                right: 1rem;
                z-index: 3000;
                display: flex;
                flex-direction: column;
                gap: 0.75rem;
                pointer-events: none;
            }
            #${CONTAINER_ID} > * {
                pointer-events: auto;
            }
        `;
        document.head.appendChild(style);
        document.body.appendChild(container);
    }
    return container;
}

/**
 * Muestra una notificación Toast no modal que se auto-desvanece.
 *
 * Crea un `<toast-component>`, lo agrega al contenedor fijo superior derecho
 * y lo gestiona según su tipo (`success` o `error`). Los toasts se apilan:
 * cada nueva notificación aparece por debajo de las anteriores.
 *
 * @param message Texto a mostrar.
 * @param type    Tipo de notificación: éxito o error.
 */
export function toast(message: string, type: 'success' | 'error' = 'success'): void {
    const element = document.createElement('toast-component');
    element.setAttribute('message', message);
    element.setAttribute('type', type);
    ensureContainer().appendChild(element);
}