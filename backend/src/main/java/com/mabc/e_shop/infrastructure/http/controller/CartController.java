package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.AddItemToCartUseCase;
import com.mabc.e_shop.application.usecase.CreateCartUseCase;
import com.mabc.e_shop.application.usecase.GetCartByIdUseCase;
import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.infrastructure.http.dto.CartItemRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.CartResponseDto;
import com.mabc.e_shop.infrastructure.http.mapper.CartHttpMapper;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import com.mabc.e_shop.infrastructure.http.response.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * Crea el controlador con los casos de uso de carritos.
     *
     * @param createCartUseCase    caso de uso que crea carritos nuevos.
     * @param addItemToCartUseCase caso de uso que agrega ítems al carrito.
     * @param getCartByIdUseCase   caso de uso que consulta un carrito por id.
     */
    public CartController(
        CreateCartUseCase createCartUseCase,
        AddItemToCartUseCase addItemToCartUseCase,
        GetCartByIdUseCase getCartByIdUseCase
    ) {
        this.createCartUseCase = createCartUseCase;
        this.addItemToCartUseCase = addItemToCartUseCase;
        this.getCartByIdUseCase = getCartByIdUseCase;
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
    @PostMapping("/{id}/items")
    public ResponseEntity<ApiResponse<CartResponseDto>> addItem(
        @PathVariable Long id,
        @Valid @RequestBody CartItemRequestDto request
    ) {
        Cart cart = addItemToCartUseCase.execute(id, request.productId(), request.quantity());
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Producto agregado al carrito correctamente.", CartHttpMapper.toResponse(cart)));
    }
}
