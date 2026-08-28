// src/components/AppLayout.ts
import '../../components/navBar';

/**
 * Layout principal encargado de cargar dinámicamente las páginas de la
 * aplicación en el elemento `router-outlet`.
 *
 * Contiene el header con la barra de navegación superior estática, única
 * para toda la aplicación.
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
            links='[{"title":"Inicio","href":"home"},{"title":"Ofertas","href":"404"},{"title":"Contacto","href":"contact"}]'
        ></nav-bar>
        <div id="router-outlet"></div>
    `;
  }
}
customElements.define('app-layout', AppLayout);