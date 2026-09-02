import './pages';

import { Router } from './router';
import { AuthStore } from './store/authStore';
import { CartStore } from './store/cartStore';

// Mapa de rutas URL 
const routes = {
  '/': 'home-page',
  '/home': 'home-page',
  '/contact': 'contact-page',
  '/admin_login': 'admin-login-page',
  '/admin_home': 'admin-home-page',
  '/admin_home/marks': 'marks-list-page',
  '/admin_home/marks/new': 'marks-form-page',
  '/admin_home/marks/edit/:id': 'marks-form-page',
  '/404': 'page-404' 
};

// Inicializar el router 
//Obs.: router-outlet es el id del div donde se cargarán las todas páginas, está definico en ./pages/appLayout/appLayout.ts
const router = new Router(routes, 'router-outlet'); 

// Interceptar los clics en enlaces con el atributo [data-link] para evitar el refresco
document.addEventListener('click', (e: MouseEvent) => {
  // Se usa composedPath() porque los enlaces del menú viven en el shadow DOM
  // del <nav-bar>: ahí el event.target se retargetea al host y closest() no
  // alcanza el <a data-link> interior.
  const link = e.composedPath().find(
    (entry): entry is HTMLAnchorElement =>
      entry instanceof HTMLAnchorElement && entry.hasAttribute('data-link'),
  );
  if (link) {
    e.preventDefault(); // Evita que el navegador haga un GET al servidor
    router.navigate(link.pathname);
  }
});

// Renderizar la ruta inicial al cargar la aplicación
router.handleRoute();

// Inicializar el carrito del invitado (leer cookie → cargar/crear carrito)
CartStore.init();

// Intentar restaurar la sesión desde el refresh token de la cookie HttpOnly
void AuthStore.tryRestoreSession();

