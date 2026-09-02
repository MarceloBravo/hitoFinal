package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.AddItemToCartUseCase;
import com.mabc.e_shop.application.usecase.CheckoutCartUseCase;
import com.mabc.e_shop.application.usecase.CreateCartUseCase;
import com.mabc.e_shop.application.usecase.DecrementItemQuantityFromCartUseCase;
import com.mabc.e_shop.application.usecase.DeleteCartUseCase;
import com.mabc.e_shop.application.usecase.GetCartByIdUseCase;
import com.mabc.e_shop.application.usecase.RemoveItemFromCartUseCase;
import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.infrastructure.http.dto.CartItemRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.CartResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.CheckoutResponseDto;
import com.mabc.e_shop.infrastructure.http.mapper.CartHttpMapper;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import com.mabc.e_shop.infrastructure.http.response.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de carritos de compras.
 *
 * <p>Expone los endpoints de consulta, creación de carritos y agregado de
 * ítems delegando la lógica en los casos de uso de la capa de aplicación.
 */
@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Carritos", description = "Consulta, creación y gestión de carritos de compras.")
public class CartController {

    private final CreateCartUseCase createCartUseCase;
    private final AddItemToCartUseCase addItemToCartUseCase;
    private final GetCartByIdUseCase getCartByIdUseCase;
    private final DeleteCartUseCase deleteCartUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;
    private final DecrementItemQuantityFromCartUseCase decrementItemQuantityFromCartUseCase;
    private final CheckoutCartUseCase checkoutCartUseCase;

    /**
     * Crea el controlador con los casos de uso de carritos.
     *
     * @param createCartUseCase         caso de uso que crea carritos nuevos.
     * @param addItemToCartUseCase      caso de uso que agrega ítems al carrito.
     * @param getCartByIdUseCase        caso de uso que consulta un carrito por id.
     * @param deleteCartUseCase         caso de uso que elimina un carrito por id.
     * @param removeItemFromCartUseCase caso de uso que elimina un ítem del carrito.
     * @param decrementItemQuantityFromCartUseCase caso de uso que disminuye la cantidad de un ítem.
     * @param checkoutCartUseCase       caso de uso que concreta una compra y rebaja el stock.
     */
    public CartController(
        CreateCartUseCase createCartUseCase,
        AddItemToCartUseCase addItemToCartUseCase,
        GetCartByIdUseCase getCartByIdUseCase,
        DeleteCartUseCase deleteCartUseCase,
        RemoveItemFromCartUseCase removeItemFromCartUseCase,
        DecrementItemQuantityFromCartUseCase decrementItemQuantityFromCartUseCase,
        CheckoutCartUseCase checkoutCartUseCase
    ) {
        this.createCartUseCase = createCartUseCase;
        this.addItemToCartUseCase = addItemToCartUseCase;
        this.getCartByIdUseCase = getCartByIdUseCase;
        this.deleteCartUseCase = deleteCartUseCase;
        this.removeItemFromCartUseCase = removeItemFromCartUseCase;
        this.decrementItemQuantityFromCartUseCase = decrementItemQuantityFromCartUseCase;
        this.checkoutCartUseCase = checkoutCartUseCase;
    }

    /**
     * Obtiene un carrito por su identificador, incluyendo sus ítems y subtotal.
     *
     * @param id identificador del carrito.
     * @return la respuesta estándar con el carrito encontrado y estado HTTP 200.
     */
    @Operation(summary = "Busca un carrito por su identificador",
            description = "Retorna el carrito correspondiente al id entregado con sus ítems y subtotal.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Carrito encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Carrito inexistente.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CartResponseDto>> findById(@PathVariable Long id) {
        Cart cart = getCartByIdUseCase.execute(id);
        return ResponseEntity.ok().body(ApiResponseFactory.queried(CartHttpMapper.toResponse(cart)));
    }

    /**
     * Crea un carrito de compras vacío.
     *
     * @return la respuesta estándar con el carrito creado y estado HTTP 201.
     */
    @Operation(summary = "Crea un carrito de compras vacío",
            description = "Genera un carrito nuevo sin ítems y retorna su identificador.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201", description = "Carrito creado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = "Error interno del servidor.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ApiResponse<CartResponseDto>> create() {
        Cart cart = createCartUseCase.execute();
        return ResponseEntity.status(201)
                .body(ApiResponseFactory.created("Carrito creado correctamente.", CartHttpMapper.toResponse(cart)));
    }

    /**
     * Agrega un producto a un carrito existente.
     *
     * @param id      identificador del carrito.
     * @param request producto y cantidad a agregar.
     * @return la respuesta estándar con el carrito actualizado y estado HTTP 200.
     */
    @Operation(summary = "Agrega un producto al carrito",
            description = "Valida el stock disponible del producto y agrega la cantidad solicitada al carrito.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Producto agregado al carrito correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Carrito o producto inexistente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409", description = "Stock insuficiente para la cantidad solicitada.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/items")
    public ResponseEntity<ApiResponse<CartResponseDto>> addItem(
        @PathVariable Long id,
        @Valid @RequestBody CartItemRequestDto request
    ) {
        Cart cart = addItemToCartUseCase.execute(id, request.productId(), request.quantity());
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Producto agregado al carrito correctamente.", CartHttpMapper.toResponse(cart)));
    }

    /**
     * Elimina un carrito existente.
     *
     * @param id identificador del carrito a eliminar.
     * @return la respuesta estándar con estado HTTP 200.
     */
    @Operation(summary = "Elimina un carrito existente",
            description = "Elimina los datos del carrito correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Carrito eliminado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Carrito inexistente.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<CartResponseDto>> delete(
        @PathVariable Long id
    ) {
        deleteCartUseCase.execute(id);
        return ResponseEntity.ok().body(ApiResponseFactory.deleted("Carrito eliminado correctamente.", null));
    }

    /**
     * Elimina un producto específico (ítem) de un carrito existente.
     *
     * @param cartId identificador del carrito.
     * @param itemId identificador del ítem a eliminar.
     * @return la respuesta estándar con el carrito actualizado y estado HTTP 200.
     */
    @Operation(summary = "Elimina un producto de un carrito",
            description = "Elimina el ítem correspondiente al id entregado del carrito dado y recalcula su subtotal.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Producto eliminado del carrito correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Carrito o ítem inexistente.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping(value = "/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponseDto>> removeItem(
        @PathVariable Long cartId,
        @PathVariable Long itemId
    ) {
        Cart cart = removeItemFromCartUseCase.execute(cartId, itemId);
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Producto eliminado del carrito correctamente.", CartHttpMapper.toResponse(cart)));
    }

    /**
     * Disminuye en una unidad la cantidad de un ítem de un carrito.
     *
     * @param cartId identificador del carrito.
     * @param itemId identificador del ítem a disminuir.
     * @return la respuesta estándar con el carrito actualizado y estado HTTP 200.
     */
    @Operation(summary = "Disminuye la cantidad de un producto del carrito",
            description = "Resta una unidad a la cantidad del ítem indicado; si llegaba a una sola unidad, lo elimina.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Cantidad disminuida correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Carrito o ítem inexistente.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping(value = "/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponseDto>> decrementItem(
        @PathVariable Long cartId,
        @PathVariable Long itemId
    ) {
        Cart cart = decrementItemQuantityFromCartUseCase.execute(cartId, itemId);
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Cantidad del producto disminuida correctamente.", CartHttpMapper.toResponse(cart)));
    }

    /**
     * Concreta una compra (checkout ficticio) de un carrito rebajando el stock.
     *
     * @param id identificador del carrito a concretar.
     * @return la respuesta estándar con el resumen de la compra y estado HTTP 200.
     */
    @Operation(summary = "Concreta la compra de un carrito",
            description = "Rebaja el stock de los productos del carrito en las cantidades compradas y elimina el carrito.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Compra concretada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Carrito o producto inexistente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409", description = "Stock insuficiente para algún producto.")
    })
    @PostMapping("/{id}/checkout")
    @Transactional
    public ResponseEntity<ApiResponse<CheckoutResponseDto>> checkout(@PathVariable Long id) {
        CheckoutCartUseCase.CheckoutResult result = checkoutCartUseCase.execute(id);
        CheckoutResponseDto dto = new CheckoutResponseDto(
                result.cartId(), result.total(), result.itemCount(), result.products());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseFactory.created(
                "Compra concretada correctamente.", dto));
    }
}
