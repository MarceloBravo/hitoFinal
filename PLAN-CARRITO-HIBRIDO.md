# Plan: Carrito híbrido con guest checkout (BD + cookie)

> Estado: **COMPLETADO** — todas las fases terminadas (A–E + decrement/checkout).
> Última actualización: 31/08/2026.
> Progreso: Fases A, B, C, D, E ✅ ✅ ✅ ✅ ✅.

## Objetivo general

Implementar un carrito de compras **híbrido** adecuado para una compra como
invitado (sin autenticación):

- Carrito persistido en **base de datos** (backend ya existente), identificado por
  un `cartId` que el frontend guarda en una **cookie**.
- Al agregar un producto: validar stock en el dominio y, **si el producto ya está
  en el carrito, sumar +1 a ese ítem** (no duplicarlo).
- **NO descontar stock al agregar**. El descuento real se hará recién al
  *concretar la compra* (checkout, fuera de esta iteración).
- Frontend: drawer lateral (1/3 ancho en desktop / 100% en móvil), botón en
  `product-card`, totales en tiempo real, aumentar/eliminar/vaciar. Sin checkout.

---

## Contexto técnico ya verificado (no volver a investigar)

- `Cart.addItem()` **crea un ítem nuevo** por cada agregado (NO suma). Hay que
  agregar la suma/actualización de un ítem existente.
- `CartItem.changeQuantity(Quantity)` ya existe y recalcula el subtotal del ítem.
- `JpaCartRepository.toEntity()` **reutiliza el `CartItemEntity` por `id`**
  (UPDATE, no INSERT) mientras el `CartItem` conserve su `id` real → la suma
  persistirá bien.
- `Cart.addItemWithId(id, product, quantity)` existe para reconstruir ítems
  persistidos; es el que se usa al cargar desde BD (el ítem ya tiene id).
- `Product.reduceStock()` existe pero **NO se llama** al agregar; hay que dejarlo
  así y usarlo solo en el checkout.
- Endpoints actuales de carrito en `CartController` (base `/api/v1/carts`):
  - `GET /{id}` — consultar carrito.
  - `POST` — crear carrito vacío (201).
  - `POST /{id}/items` — agregar ítem (body: `productId`, `quantity`).
  - `DELETE /{id}` — eliminar carrito.
  - `DELETE /{cartId}/items/{itemId}` — eliminar ítem.
- Suites de tests: backend **236 tests / 0 fallos** (referencia). Frontend buildea
  con `npm run build` (tsc + vite).

---

## FASE A — Backend: "sumar +1 al ítem existente"  ✅ (implementada)

- [x] **A1. `Cart.java` (dominio)**: agregar método para sumar cantidad a un ítem
      existente, o unificar en `addItem` la lógica "existe → suma, no existe → crea".
      Validar stock contra la cantidad acumulada (NO descontar stock).
      Recalcular subtotal del carrito.
- [x] **A2. `AddItemToCartUseCase`**: usar la lógica de suma/agregado.
- [x] **A3. Persistencia**: verificado — `JpaCartRepository.toEntity` ya hace UPDATE
      por id al conservarse el `id` del ítem existente.
- [x] **A4. Tests backend**:
      - `CartTest` (dominio): agregar producto ya presente → 1 ítem con qty
        acumulada (no duplica); stock insuficiente acumulado → lanza excepción.
      - `AddItemToCartUseCaseTest`: mismo producto 2 veces → 1 ítem qty 2;
        verificar que `save` conserva el ítem único.
      - Verificado: suite completa **240 tests, 0 fallos, BUILD SUCCESS**.

## FASE B — Decrementar ítem + "concretar la compra" (checkout)  ✅ (implementada)

### B1. Decrementar cantidad de un ítem (bajar 1)
- [x] `Cart.decrementItemQuantity(itemId)` (dominio): baja 1 la cantidad y
      recalcula el subtotal; si llegaba a 1, elimina el ítem.
- [x] `DecrementItemQuantityFromCartUseCase`: delega en el dominio y guarda.
- [x] `PATCH /api/v1/carts/{cartId}/items/{itemId}` en `CartController`.
- [x] Frontend: botón "−" en el drawer (`data-action="decrease"`) → `CartStore.decrementItem`
      → `CartService.decrementItem` (`PATCH`). (Reemplaza la "×" de eliminar.)

### B2. Checkout ficticio con descuento de stock (transaccional)
- [x] `POST /api/v1/carts/{id}/checkout` → `CheckoutCartUseCase`:
      - valida el stock de **todos** los ítems,
      - descuenta el stock llamando `Product.reduceStock(Quantity)` en una
        transacción (`@Transactional`),
      - elimina el carrito al finalizar,
      - devuelve `CheckoutResult(cartId, total, itemCount, products)`.
- [x] `CheckoutResponseDto` + `ApiResponseFactory.created` (HTTP 201).
- [x] Frontend: botón "Finalizar compra" (debajo de "Vaciar carrito")
      → `CartStore.checkout` → `CartService.checkout`; al éxito limpia estado,
      borra la cookie y crea un carrito nuevo vacío.
- [x] Se mantiene la regla: **NO se descuenta stock al agregar**, solo al comprar.

### B3. Tests
- [x] `CartTest`: `decrementItemQuantity` (bajar 1, eliminar última unidad,
      ítem inexistente, id nulo).
- [x] `DecrementItemQuantityFromCartUseCaseTest` (6 tests).
- [x] `CheckoutCartUseCaseTest` (5 tests): descuenta stock y guarda, elimina el
      carrito, errores (carrito no existe, producto no existe, stock insuficiente,
      id nulo).
- [x] `CartControllerTest`: PATCH decrement (200/404), POST checkout (201/404/409).
- Verificado: suite backend completa **260 tests, 0 fallos, BUILD SUCCESS**;
      frontend `npm run build` (tsc + vite) OK.

> Nota: esta fase NO descuenta stock al agregar; el descuento ocurre aquí,
> al concretar la compra. El checkout es ficticio (sin entidad de orden).

## FASE C — Frontend: infraestructura del carrito  ✅ (implementada)

- [x] **C1. `services/cartService.ts`**: wrappers sobre `apiClient`:
      `createCart`, `getCart(id)`, `addItem(cartId, productId, quantity=1)`,
      `removeItem(cartId, itemId)`, `deleteCart(id)`.
- [x] **C2. `CartStore` / util de estado**:
      - `utils/cookie.ts`: helpers (`getCookie`/`setCookie`/`removeCookie` y
        `getCartId`/`setCartId`/`clearCartId`, clave `cart_id`, expiración ~30 días).
      - `store/cartStore.ts`: singleton reactivo (items + subTotal + count) que
        despacha el evento `cart-updated` en `window` (bubbles+composed).
      - `main.ts` llama a `CartStore.init()`: lee cookie → `getCart(id)` → si
        404/inexistente, crea carrito y guarda su id en la cookie.
      - `interfaces/cartResponseApi.ts`: tipado de la respuesta de carrito.
- [x] **C3. `product-card`: botón "Agregar al carrito"**:
      - `template.ts`: el `product-card` recibe ahora `product-id` y `stock`.
      - `Render.ts`: `<button class="add-to-cart">Agregar al carrito</button>`.
      - `productCard.ts`: emite el evento `add-to-cart` (bubbles+composed) con
        `detail: { productId, stock }`.
      - `homePage.ts`: al recibir `add-to-cart` → `CartStore.addItem(productId)`
        → valida stock en backend → actualiza carrito + totales + evento
        `cart-updated` (badge/drawer se conectarán en Fase D).
- Verificado: `npm run build` (tsc + vite, 53 módulos) OK.

## FASE D — Frontend: drawer del carrito  ✅ (implementada)

- [x] **D1. Componente `cart-drawer`** (`components/cartDrawer/`):
      `index.ts`, `cartDrawer.ts`, `render.ts`, `style.css`.
      - overlay/sombreado + panel fijo al borde derecho.
      - responsive: `width: 33.33vw` desktop (`max-width: 420px`); `width: 100%`
        en móvil (`@media max-width: 768px`).
      - secciones: encabezado (título + contador + cerrar), lista de ítems
        (nombre, subtotal, cantidad, botones + / ×), total y acción de "Vaciar
        carrito". Sin checkout.
      - se abre con el evento `cart-open` y se re-renderiza con `cart-updated`
        (ambos en `window`); acciones delegan en `CartStore`.
- [x] **D2. NavBar badge + apertura del drawer**:
      - `navBar/render.ts`: botón 🛒 (único).
      - `navBar/navbar.ts`: escucha `cart-updated` y actualiza el conteo del
        badge (`🛒 N`); al hacer clic emite `cart-open` (abre el drawer).
      - `appLayout.ts`: agregado `<cart-drawer></cart-drawer>` junto al nav-bar.
      - `components/index.ts`: registrado `CartDrawer`.
- Verificado: `npm run build` (tsc + vite, 57 módulos) OK.

## FASE E — Pruebas / verificación  ✅ (realizada)

- [x] Backend: `mvnw test` → **240 tests, 0 fallos, BUILD SUCCESS**.
- [x] Frontend: `npm run build` (tsc + vite).
- [x] Verificación en vivo (backend dev corriendo en `:8080` con PostgreSQL vía
      docker, frontend Vite en `:5173` con CORS a `localhost:5173`):
      - Fase A: agregar producto 1 dos veces → **1 ítem con qty 2**, subtotal
        1400 (no duplica).
      - `GET /carts/999999` → **404** (CartStore crea uno nuevo).
      - crear → get → carrito vacío OK.
      - agregar 2 productos distintos → 2 ítems, subtotal 2800.
      - eliminar ítem → 1 ítem, subtotal 2100.
      - eliminar carrito (vaciar) OK.
      - Vite sirve los módulos nuevos: `cartStore.ts`, `cartService.ts`,
        `cookie.ts`, `cartDrawer.ts`, `render.ts`, `navbar.ts`, `appLayout.ts` (200).

---

## Orden / dependencias

1. Fase A (backend) — terminada.
2. Fase C1/C2 (servicio + cookie) — terminada.
3. Fase C3 (botón en product-card) — terminada.
4. Fase D1/D2 (drawer + badge) — terminada.
5. Fase E (verificación) — terminada.
6. Fase B (decrementar ítem + checkout con descuento de stock) — terminada.

## Impacto / dificultad

| Área | Dificultad | Esfuerzo |
|------|-----------|----------|
| Backend: sumar +1 (dominio + use case + persistencia) | Media | ~0.5-1 día + tests |
| Backend: endpoint checkout con descuento transaccional | Media-alta | Futuro |
| Frontend: CartService + cookie + estado | Media | ~0.5-1 día |
| Frontend: botón en product-card + flujo addItem | Baja-media | ~0.5 día |
| Frontend: drawer + responsive + acciones | Media-alta | ~1-1.5 días |
| Verificación | Baja | ~0.5 día |
