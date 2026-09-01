// src/components/AppLayout.ts
import '../../components/navBar';
import '../../components/cartDrawer';

/**
 * Layout principal encargado de cargar dinámicamente las páginas de la
 * aplicación en el elemento `router-outlet`.
 *
 * Contiene el header con la barra de navegación superior estática, única
 * para toda la aplicación, y el drawer del carrito.
 */
export class AppLayout extends HTMLElement {    
  /**
   * Se ejecuta cuando el componente se inserta en el DOM.
   */
  connectedCallback() {
    this.innerHTML = `
        <nav-bar 
            ShopName="${this.title}" 
            slogan="Encuentra lo mejor para tu hogar"
            links='[{"title":"Inicio","href":"home"},{"title":"Ofertas","href":"404"},{"title":"Contacto","href":"contact"},{"title":"Administración","href":"admin_login"}]'
        ></nav-bar>
        <div id="router-outlet"></div>
        <cart-drawer></cart-drawer>
    `;
  }
}
customElements.define('app-layout', AppLayout);