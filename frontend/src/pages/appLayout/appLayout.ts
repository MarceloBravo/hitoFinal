// src/pages/appLayout/appLayout.ts
import '../../components/navBar';
import '../../components/cartDrawer';
import { ROUTE_CHANGED_EVENT } from '../../router';
import type { Links } from '../../interfaces/links';

/**
 * Layout principal encargado de cargar dinámicamente las páginas de la
 * aplicación en el elemento `router-outlet`.
 *
 * Contiene el header con la barra de navegación superior y el drawer del
 * carrito. El menú de la barra cambia según la sección activa (FrontOffice
 * o BackOffice) y se re-renderiza cuando cambia la ruta de la URL, por lo
 * que escucha el evento {@link ROUTE_CHANGED_EVENT} que emite el router.
 */
export class AppLayout extends HTMLElement {
  frontOfficeLinks: Links[] = [
    { title: 'Inicio', href: 'home' },
    { title: 'Ofertas', href: '404' },
    { title: 'Contacto', href: 'contact' },
    { title: 'Administración', href: 'admin_login' },
  ];
  backOfficeLinks: Links[] = [
    { title: 'Marcas', href: '404' },
    { title: 'Categorías', href: '404' },
    { title: 'Productos', href: '404' },
    { title: 'Usuarios', href: '404' },
  ];

  /** Referencia al `<nav-bar>` para actualizarlo sin recrear el layout. */
  private navBar: HTMLElement | null = null;

  /**
   * Se ejecuta cuando el componente se inserta en el DOM.
   */
  connectedCallback() {
    this.renderChrome();
    window.addEventListener(ROUTE_CHANGED_EVENT, this.updateNavBar);
  }

  /**
   * Se ejecuta cuando el componente se retira del DOM.
   */
  disconnectedCallback() {
    window.removeEventListener(ROUTE_CHANGED_EVENT, this.updateNavBar);
  }

  /**
   * Indica si la ruta actual pertenece al BackOffice.
   *
   * Se evalúa sobre `pathname` en el momento en que se invoca, de modo que
   * refleja siempre la URL vigente de la barra del navegador.
   *
   * @returns `true` si la URL apunta a la sección de administración.
   */
  private get isAdmin(): boolean {
    return window.location.pathname.startsWith('/admin_home');
  }

  /**
   * Construye la estructura del layout: barra de navegación, contenedor de
   * páginas y drawer del carrito.
   */
  private renderChrome(): void {
    const navBar = document.createElement('nav-bar');
    this.applySection(navBar);
    this.navBar = navBar;

    const outlet = document.createElement('div');
    outlet.id = 'router-outlet';

    const drawer = document.createElement('cart-drawer');

    this.replaceChildren(navBar, outlet, drawer);
  }

  /**
   * Aplica a la barra de navegación el título y el menú de la sección actual.
   *
   * Los menús se entregan como JSON; se fijan con `setAttribute` (y no
   * interpolados en innerHTML) para que las comillas del JSON no rompan
   * el atributo `links` del componente.
   *
   * @param navBar elemento de la barra de navegación a configurar.
   */
  private applySection(navBar: HTMLElement): void {
    const links: Links[] = this.isAdmin ? this.backOfficeLinks : this.frontOfficeLinks;
    const title: string = this.isAdmin ? 'Panel de Administración' : 'Tienda Online';

    navBar.setAttribute('ShopName', title);
    navBar.setAttribute('slogan', 'Encuentra lo mejor para tu hogar');
    navBar.setAttribute('links', JSON.stringify(links));
  }

  /**
   * Re-configura la barra de navegación al cambiar la ruta de la URL.
   */
  private updateNavBar = (): void => {
    if (this.navBar) {
      this.applySection(this.navBar);
    }
  };
}
customElements.define('app-layout', AppLayout);