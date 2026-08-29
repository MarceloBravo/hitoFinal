package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.CreateProductUseCase;
import com.mabc.e_shop.application.usecase.GetAllProductsUseCase;
import com.mabc.e_shop.application.usecase.GetProductByIdUseCase;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.infrastructure.http.dto.ProductRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.ProductResponseDto;
import com.mabc.e_shop.infrastructure.http.mapper.ProductHttpMapper;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de productos.
 *
 * <p>Expone los endpoints de consulta, creación y actualización de productos
 * delegando la lógica en los casos de uso de la capa de aplicación.
 */
@Tag(name = "Productos", description = "Consulta, registro y actualización de productos.")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;

    /**
     * Crea el controlador con los casos de uso de productos.
     *
     * @param createProductUseCase  caso de uso que crea o actualiza productos.
     * @param getAllProductsUseCase caso de uso que consulta todos los productos.
     * @param getProductByIdUseCase caso de uso que consulta un producto por id.
     */
    public ProductController(
        CreateProductUseCase createProductUseCase,
        GetAllProductsUseCase getAllProductsUseCase,
        GetProductByIdUseCase getProductByIdUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    /**
     * Obtiene todos los productos registrados.
     *
     * @return la respuesta estándar con la lista de productos y estado HTTP 200.
     */
    @Operation(summary = "Lista todos los productos",
            description = "Retorna el catálogo completo de productos registrados.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Consulta exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> findAll() {
        List<ProductResponseDto> products = getAllProductsUseCase.execute().stream()
                .map(ProductHttpMapper::toResponse)
                .toList();
        return ResponseEntity.ok().body(ApiResponseFactory.queried(products));
    }

    /**
     * Obtiene un producto por su identificador.
     *
     * @param id identificador del producto.
     * @return la respuesta estándar con el producto encontrado y estado HTTP 200.
     */
    @Operation(summary = "Busca un producto por su identificador",
            description = "Retorna el producto correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Producto encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Producto inexistente.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> findById(@PathVariable Long id) {
        Product product = getProductByIdUseCase.execute(id);
        return ResponseEntity.ok().body(ApiResponseFactory.queried(ProductHttpMapper.toResponse(product)));
    }

    /**
     * Crea un producto nuevo.
     *
     * @param request datos del producto a crear.
     * @return la respuesta estándar con el producto creado y estado HTTP 201.
     */
    @Operation(summary = "Crea un producto nuevo",
            description = "Registra un producto validando su marca, categorías y datos de negocio.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201", description = "Producto creado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido o datos de negocio inválidos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Marca o categoría inexistente.")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> create(
        @Valid @RequestBody ProductRequestDto request
    ) {
        Product product = createProductUseCase.execute(
                null,
                request.markId(),
                request.categoryIds(),
                request.name(),
                request.description(),
                request.stock(),
                request.weight(),
                request.priceCost(),
                request.priceSale(),
                request.imagePath()
                );
        return ResponseEntity.status(201).body(ApiResponseFactory.created(
                "Producto creado correctamente.", ProductHttpMapper.toResponse(product)));
    }

    /**
     * Actualiza un producto existente.
     *
     * @param id      identificador del producto a actualizar.
     * @param request nuevos datos del producto.
     * @return la respuesta estándar con el producto actualizado y estado HTTP 200.
     */
    @Operation(summary = "Actualiza un producto existente",
            description = "Reemplaza los datos del producto correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Producto actualizado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido o datos de negocio inválidos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Producto, marca o categoría inexistente.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> update(
        @PathVariable Long id,
        @Valid @RequestBody ProductRequestDto request
    ) {
        Product product = createProductUseCase.execute(
                id,
                request.markId(),
                request.categoryIds(),
                request.name(),
                request.description(),
                request.stock(),
                request.weight(),
                request.priceCost(),
                request.priceSale(),
                request.imagePath()
                );
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Producto actualizado correctamente.", ProductHttpMapper.toResponse(product)));
    }
}
