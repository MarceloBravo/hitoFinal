// src/router.ts
type RouteMap = Record<string, string>;

/**
 * Evento que se despacha en `window` tras cada navegación (pushState,
 * botones atrás/adelante o navegación programática).
 *
 * Permite que componentes globales como el layout reaccionen al cambio de
 * ruta sin recargar la página.
 */
export const ROUTE_CHANGED_EVENT = 'route-changed';

/**
 * Enrutador SPA basado en la History API del navegador.
 *
 * Asocia cada ruta URL con la etiqueta de un Web Component y la inyecta
 * en el contenedor `router-outlet` sin recargar la página.
 */
export class Router {
  /** Mapa de rutas: pathname → etiqueta del Web Component. */
  private routes: RouteMap;
  /** Contenedor donde se monta el Web Component de la ruta activa. */
  private appOutlet: HTMLElement;

  /**
   * Crea una instancia del router y escucha el evento `popstate`.
   *
   * @param routes   Mapa de rutas URL con su Web Component asociado.
   * @param outletId Identificador del elemento que actuará como contenedor de las páginas.
   */
  constructor(routes: RouteMap, outletId: string) {
    this.routes = routes;
    this.appOutlet = document.getElementById(outletId)!;

    // Escuchar los botones de "Atrás / Adelante" del navegador
    window.addEventListener('popstate', () => {
      this.handleRoute();
      this.notifyRouteChanged();
    });
  }

  /**
   * Navega a una nueva ruta sin recargar la página.
   *
   * @param path Ruta de destino, p. ej. `/contact`.
   */
  public navigate(path: string): void {
    window.history.pushState({}, '', path);
    this.handleRoute();
    this.notifyRouteChanged();
  }

  /**
   * Renderiza el Web Component correspondiente al path actual de la URL.
   *
   * Si la ruta no existe en el mapa, se muestra la página 404.
   */
  public handleRoute(): void {
    const currentPath = window.location.pathname;
    const tagName = this.resolveRoute(currentPath);

    // Limpia el contenedor e inyecta la etiqueta HTML del nuevo Web Component
    this.appOutlet.innerHTML = `<${tagName}></${tagName}>`;
  }

  /**
   * Resuelve la etiqueta del Web Component para una ruta.
   *
   * Primero intenta coincidencia exacta y, si no existe, busca una regla con
   * parámetros de ruta (segmentos `:param`, p. ej. `/admin_home/marks/edit/:id`) que
   * coincida con el path actual.
   *
   * @param path Path actual sin el host.
   * @returns Etiqueta del Web Component o la de la página 404.
   */
  private resolveRoute(path: string): string {
    if (this.routes[path]) {
      return this.routes[path];
    }

    const paramKey = Object.keys(this.routes).find((key) => {
      if (!key.includes(':')) {
        return false;
      }
      return this.matchPathParams(key, path) !== null;
    });

    return paramKey ? this.routes[paramKey] : this.routes['404'];
  }

  /**
   * Compara una regla con parámetros (p. ej. `/admin_home/marks/edit/:id`) contra un path
   * real (p. ej. `/admin_home/marks/edit/5`) segmento a segmento.
   *
   * @param rule Regla del mapa de rutas.
   * @param path Path real a comparar.
   * @returns Un objeto con los parámetros extraídos o `null` si no coincide.
   */
  private matchPathParams(rule: string, path: string): Record<string, string> | null {
    const ruleSegments = rule.split('/').filter(Boolean);
    const pathSegments = path.split('/').filter(Boolean);

    if (ruleSegments.length !== pathSegments.length) {
      return null;
    }

    const params: Record<string, string> = {};
    for (let i = 0; i < ruleSegments.length; i += 1) {
      if (ruleSegments[i].startsWith(':')) {
        params[ruleSegments[i].slice(1)] = decodeURIComponent(pathSegments[i]);
      } else if (ruleSegments[i] !== pathSegments[i]) {
        return null;
      }
    }
    return params;
  }

  /**
   * Notifica a los interesados (p. ej. el layout) que la ruta cambió.
   */
  private notifyRouteChanged(): void {
    window.dispatchEvent(new CustomEvent(ROUTE_CHANGED_EVENT, {
      detail: { path: window.location.pathname },
    }));
  }
}