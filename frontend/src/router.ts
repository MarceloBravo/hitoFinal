// src/router.ts
type RouteMap = Record<string, string>;

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
    window.addEventListener('popstate', () => this.handleRoute());
  }

  /**
   * Navega a una nueva ruta sin recargar la página.
   *
   * @param path Ruta de destino, p. ej. `/contact`.
   */
  public navigate(path: string): void {
    window.history.pushState({}, '', path);
    this.handleRoute();
  }

  /**
   * Renderiza el Web Component correspondiente al path actual de la URL.
   *
   * Si la ruta no existe en el mapa, se muestra la página 404.
   */
  public handleRoute(): void {
    const currentPath = window.location.pathname;
    const tagName = this.routes[currentPath] || this.routes['404'];

    // Limpia el contenedor e inyecta la etiqueta HTML del nuevo Web Component
    this.appOutlet.innerHTML = `<${tagName}></${tagName}>`;
  }
}