/**
 * Cuerpo de petición para registrar o actualizar un usuario (UserRequestDto).
 *
 * La contraseña es obligatoria al crear, pero opcional al actualizar: si no se
 * entrega (o viene vacía), el backend la conserva sin cambios.
 */
export interface UserRequestInterface {
    /** Nombre completo del usuario. */
    name: string;
    /** Correo electrónico del usuario. */
    email: string;
    /** Contraseña del usuario; obligatoria al crear, opcional al actualizar. */
    password?: string;
    /** Rol asignado al usuario (USER o ADMIN). */
    role: string;
    /** Estado de actividad de la cuenta. */
    active: boolean;
}
