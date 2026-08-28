/**
 * Valida el formato de una dirección de correo electrónico.
 *
 * @param email Correo electrónico a validar.
 * @returns `true` si el formato es válido, `false` en caso contrario.
 */
export const validateEmail = (email: string): boolean => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
}

/**
 * Valida el formato de un número de teléfono internacional.
 *
 * La expresión regular acepta: prefijo "+" opcional, código de país
 * (1 a 3 dígitos), código de área (1 a 2 dígitos) y número principal
 * (6 a 10 dígitos), con espacios opcionales entre cada bloque.
 *
 * @param phone Número de teléfono a validar.
 * @returns `true` si el formato es válido, `false` en caso contrario.
 */
export const validatePhone = (phone: string): boolean => {
    const regex = /^\+?[0-9]{1,3}\s?[0-9]{1,2}\s?[0-9]{6,10}$/;
    return regex.test(phone);
}