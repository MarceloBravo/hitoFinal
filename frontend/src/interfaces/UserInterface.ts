/**
 * Usuario obtenido desde el backend (UserResponseDto).
 */
export interface UserInterface {
    /** Identificador del usuario. */
    id: number;
    /** Nombre completo del usuario. */
    name: string;
    /** Correo electrónico del usuario. */
    email: string;
    /** Rol asignado al usuario (USER o ADMIN). */
    role: string;
    /** Estado de actividad de la cuenta. */
    active: boolean;
}
