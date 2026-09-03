import { ConfirmDialog } from '../components/confirmDialog';

/**
 * Función de diálogo reutilizable para confirmar una acción del usuario.
 *
 * Crea un elemento `<confirm-dialog>` en modo confirmación, lo agrega al
 * documento y devuelve una promesa que se resuelve con `true` al pulsar
 * "Aceptar" o `false` al pulsar "Cancelar" (o cerrar con Escape / clic
 * fuera). El diálogo se elimina del DOM al resolverse.
 *
 * @param message Texto a mostrar en el diálogo.
 * @returns Promesa que se resuelve con la opción elegida por el usuario.
 */
export function confirm(message: string): Promise<boolean> {
    return openDialog(message, 'confirm');
}

/**
 * Función de diálogo reutilizable para informar un mensaje al usuario.
 *
 * Crea un elemento `<confirm-dialog>` en modo alert, lo agrega al documento
 * y devuelve una promesa que se resuelve con `true` al pulsar "Aceptar".
 * El diálogo se elimina del DOM al resolverse.
 *
 * @param message Texto a mostrar en el diálogo.
 * @returns Promesa que se resuelve con `true` al pulsar "Aceptar".
 */
export function alertMessage(message: string): Promise<boolean> {
    return openDialog(message, 'alert');
}

/**
 * Crea y muestra un diálogo, resolviendo una promesa según la opción elegida.
 *
 * @param message Texto a mostrar en el diálogo.
 * @param type    Modo de presentación: confirmación o simple mensaje.
 * @returns Promesa con el resultado de la elección del usuario.
 */
function openDialog(message: string, type: 'confirm' | 'alert'): Promise<boolean> {
    const dialog = document.createElement('confirm-dialog');
    dialog.setAttribute('message', message);
    if (type === 'alert') {
        dialog.setAttribute('type', 'alert');
    }
    document.body.appendChild(dialog);

    return new Promise((resolve) => {
        dialog.addEventListener(ConfirmDialog.RESULT_EVENT, (event) => {
            const confirmed = (event as CustomEvent<{ confirmed: boolean }>).detail.confirmed;
            resolve(confirmed);
        }, { once: true });
    });
}