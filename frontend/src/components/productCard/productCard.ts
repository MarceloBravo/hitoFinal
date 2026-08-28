import { Render } from './Render';

const PLACEHOLDER_IMAGE = import.meta.env.VITE_PLACEHOLDER_IMAGE;

/**
 * Web Component que renderiza la tarjeta de un producto.
 *
 * Acepta los atributos `img`, `title`, `description` y `price` dentro
 * de su shadow DOM. Si no se entrega imagen, usa `VITE_PLACEHOLDER_IMAGE`.
 */
export class ProductCard extends HTMLElement {
  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
  }

  /**
   * Atributos observados para reaccionar a cambios en el DOM.
   */
  static get observedAttributes() {
      return [
          'img', // Atributo para la imagen del producto
          'title', // Atributo para el título del producto
          'description', // Atributo para la descripción del producto
          'price' // Atributo para el precio del producto
      ];
  }

  /**
   * Se ejecuta cuando el componente se inserta en el DOM.
   */
  connectedCallback() {
      this.render();
  }

  /**
   * Re-renderiza el componente cuando cambian sus atributos observados.
   */
  attributeChangedCallback(_attrName: string, oldValue: string | null, newValue: string | null) {
      if (oldValue !== newValue) {
          this.render();
      }
  }

  /**
   * Genera el HTML y aplica el CSS del componente dentro de su shadow DOM.
   */
  render() {
      const root: ShadowRoot | null = this.shadowRoot;

      if (!root) {
          return;
      }
      
      const img: string = this.getAttribute('img') || PLACEHOLDER_IMAGE;
      const title: string = this.getAttribute('title') || 'Producto desconocido';
      const description: string = this.getAttribute('description') || 'Descripción no disponible';
      const price: string = this.getAttribute('price') || '$0.00';
      const render = new Render(root, img, title, description, price);
      render.render();
  }
}
    
if (!customElements.get('product-card')) {
    customElements.define('product-card', ProductCard);
}

