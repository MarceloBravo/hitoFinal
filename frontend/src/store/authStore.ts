import { AuthService } from '../services/authService.js';
import type { AuthResponse } from '../interfaces/authResponseInterface.js';

/**
 * Nombre del evento que se despacha en `window` cuando la sesión cambia.
 */
export const AUTH_UPDATED_EVENT = 'auth-updated';

/**
 * Estado de la sesión de usuario (singleton).
 *
 * Mantiene en memoria únicamente el access token y el rol del usuario
 * autenticado. El refresh token nunca se persiste en el frontend: viaja en
 * una cookie `HttpOnly` que el navegador envía automáticamente y rota en
 * cada renovación. Por eso una recarga de página no restaura la sesión por
 * sí sola; `tryRestoreSession()` la valida contra el backend usando la cookie.
 */
class AuthStoreImpl {
  private session: AuthResponse | null = null;

  /**
   * Indica si hay una sesión activa.
   *
   * @returns `true` si existe un usuario autenticado.
   */
  isAuthenticated(): boolean {
    return this.session !== null;
  }

  /**
   * Obtiene el token de acceso para autorizar peticiones del backoffice.
   *
   * @returns token JWT o `null` si no hay sesión.
   */
  getAccessToken(): string | null {
    return this.session?.accessToken ?? null;
  }

  /**
   * Obtiene el rol del usuario autenticado.
   *
   * @returns rol (`ADMIN`, `USER`...) o `null` si no hay sesión.
   */
  getRole(): string | null {
    return this.session?.role ?? null;
  }

  /**
   * Persiste una sesión nueva en memoria y notifica a los componentes.
   *
   * @param auth datos de la sesión iniciada.
   */
  saveSession(auth: AuthResponse): void {
    this.session = auth;
    window.dispatchEvent(new CustomEvent(AUTH_UPDATED_EVENT, {
      detail: { auth },
      bubbles: true,
      composed: true,
    }));
  }

  /**
   * Elimina la sesión actual y notifica a los componentes interesados.
   */
  clearSession(): void {
    this.session = null;
    window.dispatchEvent(new CustomEvent(AUTH_UPDATED_EVENT, {
      detail: { auth: null },
      bubbles: true,
      composed: true,
    }));
  }

  /**
   * Intenta restaurar la sesión al recargar la página.
   *
   * Pide al backend un access token nuevo validando el refresh token de la
   * cookie `HttpOnly`. Si la cookie no existe o caducó (401), descarta
   * cualquier estado local.
   */
  async tryRestoreSession(): Promise<void> {
    const response = await AuthService.refresh();
    if (response.ok && response.data) {
      this.saveSession(response.data);
    } else {
      this.clearSession();
    }
  }
}

/**
 * Instancia única de la sesión del usuario.
 */
export const AuthStore = new AuthStoreImpl();