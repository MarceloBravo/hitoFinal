import './pages';

import { Router } from './router';

// Mapa de rutas URL 
const routes = {
  '/': 'home-page',
  '/home': 'home-page',
  '/contact': 'contact-page',
  '/404': 'page-404' 
};

// Inicializar el router 
//Obs.: router-outlet es el id del div donde se cargarán las todas páginas, está definico en ./pages/appLayout/appLayout.ts
const router = new Router(routes, 'router-outlet'); 

// Interceptar los clics en enlaces con el atributo [data-link] para evitar el refresco
document.addEventListener('click', (e: MouseEvent) => {
  const target = (e.target as HTMLElement).closest('[data-link]');
  if (target && target instanceof HTMLAnchorElement) {
    e.preventDefault(); // Evita que el navegador haga un GET al servidor
    router.navigate(target.pathname);
  }
});

// Renderizar la ruta inicial al cargar la aplicación
router.handleRoute();

