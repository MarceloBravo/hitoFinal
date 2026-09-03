# Hito 4 — Microservicio de E-commerce (Carritos de Compras)

Microservicio REST para la gestión de un catálogo de productos con carritos de compras, construido con **Java 17**, **Spring Boot**, **PostgreSQL** y **Docker**, documentado con **OpenAPI 3 (Swagger UI)** y siguiendo una arquitectura hexagonal (Domain-Driven Design) con separación clara entre dominio, aplicación e infraestructura.

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot (Web MVC, Validation) |
| Persistencia | Spring Data JPA + Hibernate + PostgreSQL 16 |
| Virtualización | Docker / Docker Compose |
| Documentación | Springdoc OpenAPI (Swagger UI) |
| Pruebas | JUnit 5, Mockito, MockMvc + JaCoCo (cobertura) |

## Arquitectura

```
src/main/java/com/mabc/hitoFinal/
├── domain/            # Entidades, value objects, excepciones y puertos de repositorio
├── application/       # Casos de uso (sin dependencias del framework)
└── infrastructure/    # Controladores REST, DTOs, mappers, entidades JPA y adaptadores
```

## Requisitos Previos

- JDK 17+
- Docker Desktop (o motor Docker con Compose v2)
- Node.js 18+ (y npm) para el frontend

## Estructura del Repositorio

```
hitoFinal/
├── backend/     # Microservicio Spring Boot (API REST)
├── frontend/    # SPA en TypeScript + Vite (Web Components)
└── README.md
```

## 1. Levantar la Base de Datos

Configura las credenciales en el archivo `.env` (usa `.env.example` como plantilla):

```env
DB_NAME=hitoFinal
DB_URL=jdbc:postgresql://localhost:5432/hitoFinal
DB_USERNAME=postgres
DB_PASSWORD=tu_password
```

Luego levanta PostgreSQL contenedorizado:

```bash
docker compose up -d
```

El servicio `postgres` queda disponible en `localhost:5432` con volumen persistente (`postgres_data`), por lo que los datos sobreviven a reinicios del contenedor.

## 2. Ejecutar el Backend (API REST)

Configura las credenciales en `backend/.env` (usa `backend/.env.example` como plantilla; incluye la base de datos y el secreto JWT):

Instala las dependencias con Maven Wrapper (solo la primera vez):

```bash
cd backend
./mvnw dependency:go-offline
```

Luego inicia la aplicación:

```bash
./mvnw spring-boot:run
```

> En Windows: `.\mvnw spring-boot:run`

La aplicación arranca con el perfil `dev` por defecto (`spring.profiles.default: dev`) y queda escuchando en `http://localhost:8080`. Para producción se debe iniciar explícitamente con `-Dspring-boot.run.profiles=prod`.

> **Nota:** el frontend está codificado por defecto contra `http://localhost:8080/api/v1` (ver `frontend/.env`). Asegúrate de tener el backend corriendo antes de levantar el frontend.

## 3. Ejecutar el Frontend (SPA)

Configura el archivo `frontend/.env` (usa `frontend/.env.example` como plantilla). Las variables relevantes para apuntar a la API local son:

```env
VITE_API_URL=http://localhost:8080/api/v1
VITE_IMAGE_URL=http://localhost:8080
VITE_PLACEHOLDER_IMAGE=https://via.placeholder.com/150
VITE_PRODUCTS_PER_PAGE=12
```

Instala las dependencias (solo la primera vez) y levanta el servidor de desarrollo:

```bash
cd frontend
npm install
npm run dev
```

Vite inicia un servidor de desarrollo (por defecto en `http://localhost:5173`) con hot-reload. Abre esa URL en el navegador.

Para generar el bundle de producción:

```bash
npm run build
npm run preview   # sirve el build de producción para inspección local
```

## 4. Documentación y Pruebas de Contratos

Con la aplicación corriendo bajo el perfil `dev`:

| Recurso | Ruta |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html/index.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| Wiki | https://deepwiki.com/MarceloBravo/hitoFinal/3-backend-infrastructure-layer |

> 🔒 **Aislamiento hermético:** en el perfil `prod` (y por defecto en la configuración base), `springdoc.api-docs.enabled=false` y `springdoc.swagger-ui.enabled=false`, eliminando la superficie de ataque fuera del entorno local.

## Endpoints Principales

| Método | Ruta | Descripción | Códigos |
|---|---|---|---|
| GET | `/api/v1/products` | Lista todos los productos | 200 |
| GET | `/api/v1/products/{id}` | Busca un producto por id | 200, 404 |
| POST | `/api/v1/products` | Crea un producto | 201, 400, 404 |
| PUT | `/api/v1/products/{id}` | Actualiza un producto | 200, 400, 404 |
| GET | `/api/v1/categories` | Lista todas las categorías | 200 |
| GET | `/api/v1/categories/{id}` | Busca una categoría por id | 200, 404 |
| POST | `/api/v1/categories` | Registra una categoría | 201, 400 |
| PUT | `/api/v1/categories/{id}` | Actualiza una categoría | 200, 400, 404 |
| GET | `/api/v1/marks` | Lista todas las marcas | 200 |
| GET | `/api/v1/marks/{id}` | Busca una marca por id | 200, 404 |
| POST | `/api/v1/marks` | Registra una marca | 201, 400 |
| PUT | `/api/v1/marks/{id}` | Actualiza una marca | 200, 400, 404 |
| GET | `/api/v1/carts/{id}` | Consulta un carrito con sus ítems | 200, 404 |
| POST | `/api/v1/carts` | Crea un carrito vacío | 201 |
| POST | `/api/v1/carts/{id}/items` | Agrega un producto al carrito | 200, 400, 404, 409 |

Todas las respuestas usan un DTO unificado `ApiResponse { statusCode, message, data }`. Los errores de negocio son interceptados centralizadamente por un `@RestControllerAdvice` (`GlobalExceptionHandler`) que traduce las excepciones a códigos semánticos (400, 404, 409) sin exponer stacktraces nativos del servidor.

## Colección de Pruebas

La auditoría de los endpoints se realizó con **Postman** ejecutando los contratos documentados en Swagger contra el entorno local. Cualquier cliente HTTP (Postman, Insomnia, curl) puede importar la especificación desde `http://localhost:8080/api-docs`.

También se incluye el archivo hitoFinal.postman_collection.json, ubicado en el directorio raíz de la aplicación,  para ser importados en Postman.

## Pruebas y Cobertura

```bash
./mvnw test
```

El reporte JaCoCo se genera en `target/site/jacoco/`.
