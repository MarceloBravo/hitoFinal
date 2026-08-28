# E-Commerce Front

Aplicación de tienda en línea construida con **Vite + TypeScript vanilla** como parte del Hito 2 del curso Java Desafío Latam. Implementa interfaces dinámicas mediante Web Components, tipado estricto, manipulación segura del DOM y peticiones asíncronas a la API pública de [DummyJSON](https://dummyjson.com) con feedback continuo para el usuario.

## Descripción

La aplicación muestra un catálogo de productos con filtros por categoría, marca y precio, paginación de resultados y una página de contacto con formulario validado. Los datos se consumen desde un servicio externo con arquitectura `async/await` y control de errores mediante `try/catch`, mostrando estados de carga, éxito y error en la interfaz.

## Tecnologías

Aplicación 100% vanilla: **cero dependencias de runtime** (sin React, Vue, Angular, etc.). Las interfaces dinámicas se construyen con Web Components nativos del navegador — los componentes reutilizables usan **Custom Elements + Shadow DOM** y las páginas usan **Custom Elements + Light DOM** — junto con `fetch` y manipulación directa del DOM.

- [Vite](https://vitejs.dev/) — build tool y servidor de desarrollo
- [TypeScript](https://www.typescriptlang.org/) (modo `strict`) — tipado hermético
- [Web Components](https://developer.mozilla.org/es/docs/Web/API/Web_components) (Custom Elements + Shadow DOM)
- [DummyJSON](https://dummyjson.com/) (API REST)

## Estructura

```
e-commerce-front/
├── index.html                  # HTML raíz (monta <app-layout>)
├── .env.example                # Plantilla de variables de entorno
├── package.json
├── tsconfig.json               # TypeScript en modo strict
├── README.md
└── src/
    ├── main.ts                 # Punto de entrada: router + navegación
    ├── router.ts               # Enrutado SPA sin recarga de página
    ├── style.css               # Estilos globales
    ├── api/
    │   └── apiClient.ts        # Cliente HTTP (fetch + manejo de errores)
    ├── services/               # Consumo de la API asíncrona
    │   ├── productService.ts
    │   └── categoriesService.ts
    ├── models/                 # Modelos de dominio
    │   └── Product.ts
    ├── interfaces/             # Contratos de datos (API y UI)
    │   ├── responseInterface.ts
    │   ├── productResponseApi.ts
    │   ├── categoriesResponseApi.ts
    │   ├── asideOptions.ts
    │   ├── links.ts
    │   ├── metaData.ts
    │   ├── dimensions.ts
    │   └── reviews.ts
    ├── enum/                   # Control de estados de la interfaz
    │   ├── inputTypeEnum.ts
    │   └── loadStatusEnum.ts
    ├── components/             # Web Components reutilizables
    │   ├── index.ts
    │   ├── navBar/             # Barra de navegación superior
    │   ├── asideSection/       # Filtros (checkbox / radio)
    │   ├── productCard/        # Tarjeta de producto
    │   ├── pagination/         # Paginación del catálogo
    │   ├── spinner/            # Indicador de carga
    │   └── fotter/             # Pie de página
    ├── pages/                  # Páginas de la aplicación
    │   ├── index.ts
    │   ├── appLayout/          # Layout principal (navbar + router-outlet)
    │   ├── home/               # Catálogo de productos
    │   ├── 404/                # Página 404
    │   └── contact/            # Formulario de contacto validado
    └── utils/
        ├── validations.ts      # Validaciones (email, teléfono)
        └── errorHandler.ts     # Normalización de errores de red
```

Cada componente de `src/components/` y cada página de `src/pages/` sigue el patrón de tres archivos: el `index.ts` (barrel export), el archivo del Web Component (p. ej. `productCard.ts`) y su `render.ts`/`template.ts` que genera el HTML con su `style.css`.

## Configuración

Copia el archivo `.env.example` a `.env` y ajusta los valores según corresponda:

```bash
cp .env.example .env
```

Variables disponibles:

| Variable                  | Descripción                                              | Valor por defecto          |
| ------------------------- | -------------------------------------------------------- | -------------------------- |
| `VITE_API_URL`            | URL base de la API REST usada para el catálogo           | `https://dummyjson.com`    |
| `VITE_PLACEHOLDER_IMAGE`  | URL de la imagen por defecto para productos sin imagen   | `https://via.placeholder.com/150` |

> El archivo `.env` está incluido en `.gitignore` y no se sube al repositorio.

## Comandos de Instalación y Ejecución

```bash
npm install
npm run dev
npm run build
```

- `npm install` — instala las dependencias del proyecto.
- `npm run dev` — levanta el servidor de desarrollo de Vite.
- `npm run build` — compila el proyecto (TypeScript + build de Vite) para producción.

## Autor
Marcelo Bravo C.

Tienda on-line — Hito 2, Java Desafío Latam.

Se puede ver la aplicación desplegada en: https://e-shop-vanilla.netlify.app/
