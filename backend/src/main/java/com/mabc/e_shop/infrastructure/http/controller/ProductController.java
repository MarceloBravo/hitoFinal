package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.CreateProductUseCase;
import com.mabc.e_shop.application.usecase.GetAllProductsUseCase;
import com.mabc.e_shop.application.usecase.GetProductByIdUseCase;
import com.mabc.e_shop.application.usecase.DeleteProductUseCase;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.infrastructure.http.dto.ProductPaginatedResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.ProductRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.ProductResponseDto;
import com.mabc.e_shop.infrastructure.http.mapper.ProductHttpMapper;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import com.mabc.e_shop.infrastructure.http.response.ApiResponseFactory;
import com.mabc.e_shop.infrastructure.storage.ImageStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyEditorSupport;
import java.nio.file.Path;
import java.util.List;

/**
 * Controlador REST de productos.
 *
 * <p>Expone los endpoints de consulta, creación y actualización de productos
 * delegando la lógica en los casos de uso de la capa de aplicación. La
 * creación y actualización reciben multipart/form-data, almacenan la imagen
 * con {@link ImageStorage} y persisten su ruta pública {@code /uploads/...}.
 */
@Tag(name = "Productos", description = "Consulta, registro y actualización de productos.")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final ImageStorage imageStorage;

    /**
     * Crea el controlador con los casos de uso de productos y el almacenamiento de imágenes.
     *
     * @param createProductUseCase  caso de uso que crea o actualiza productos.
     * @param getAllProductsUseCase caso de uso que consulta todos los productos.
     * @param getProductByIdUseCase caso de uso que consulta un producto por id.
     * @param imageStorage          almacenamiento de las imágenes de los productos.
     */
    public ProductController(
        CreateProductUseCase createProductUseCase,
        GetAllProductsUseCase getAllProductsUseCase,
        GetProductByIdUseCase getProductByIdUseCase,
        ImageStorage imageStorage,
        DeleteProductUseCase deleteProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.imageStorage = imageStorage;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    /**
     * Permite que el campo {@code image} de la petición llegue como texto vacío
     * sin fallar el binding: al ser la imagen opcional, una cadena en blanco se
     * interpreta como ausencia de archivo (valor {@code null}).
     *
     * @param binder binder de datos del controlador.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(MultipartFile.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                }
            }
        });
    }

    /**
     * Obtiene una página de productos.
     *
     * <p>La paginación usa convención del frontend: {@code page} es
     * 1-indexado y {@code limit} es la cantidad por página. Internamente se
     * convierte {@code page} a base 0 para Spring Data y se calcula
     * {@code skip} como {@code (page - 1) * limit}.
     *
     * @param limit      cantidad máxima de productos por página.
     * @param page       número de página (1-indexado).
     * @param categoryId identificador de la categoría para filtrar (opcional).
     * @param markId     identificador de la marca para filtrar (opcional).
     * @param minPrice   precio de venta mínimo para filtrar (opcional).
     * @param maxPrice   precio de venta máximo para filtrar (opcional).
     * @param search     término de búsqueda de texto sobre nombre, descripción o
     *                   nombre de la marca (opcional).
     * @return la respuesta paginada de productos y estado HTTP 200.
     */
    @Operation(summary = "Lista los productos de forma paginada",
            description = "Retorna una página de productos con metadatos de paginación (limit, skip, total).")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Consulta exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping
    public ResponseEntity<ProductPaginatedResponseDto> findAll(
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long markId,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) String search
    ) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        int safePage = Math.max(1, page);
        int skip = (safePage - 1) * safeLimit;
        ProductRepository.PageResult result = getAllProductsUseCase.execute(safePage - 1, safeLimit, categoryId, markId, minPrice, maxPrice, search);
        List<ProductResponseDto> products = result.content().stream()
                .map(ProductHttpMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new ProductPaginatedResponseDto(safeLimit, skip, (int) result.total(), products));
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
     * Crea un producto nuevo con su imagen.
     *
     * @param request datos del producto y archivo de imagen a crear.
     * @return la respuesta estándar con el producto creado y estado HTTP 201.
     */
    @Operation(summary = "Crea un producto nuevo con su imagen",
            description = "Registra un producto validando su marca, categorías y datos de negocio.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201", description = "Producto creado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido o datos de negocio inválidos."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Marca o categoría inexistente.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponseDto>> create(
        @Valid @ModelAttribute ProductRequestDto request
    ) {
        MultipartFile image = request.image();
        boolean hasImage = image != null && !image.isEmpty();
        Path stored = hasImage ? imageStorage.store(image) : null;
        try {
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
                    stored == null ? null : imageStorage.toPublicPath(stored)
                    );
            return ResponseEntity.status(201).body(ApiResponseFactory.created(
                    "Producto creado correctamente.", ProductHttpMapper.toResponse(product)));
        } catch (RuntimeException e) {
            if (stored != null) {
                imageStorage.delete(stored);
            }
            throw e;
        }
    }

    /**
     * Actualiza un producto existente.
     *
     * <p>Si se adjunta una nueva imagen se reemplaza la anterior; en caso
     * contrario se conserva la ruta de la imagen actual del producto.
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
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponseDto>> update(
        @PathVariable Long id,
        @Valid @ModelAttribute ProductRequestDto request
    ) {
        ImagePath currentImage = getProductByIdUseCase.execute(id).getImagePath();
        MultipartFile image = request.image();
        boolean replacing = image != null && !image.isEmpty();
        Path stored = replacing ? imageStorage.store(image) : null;

        try {
            String imagePath;
            if (replacing) {
                imagePath = imageStorage.toPublicPath(stored);
            } else if (currentImage != null) {
                imagePath = currentImage.value();
            } else {
                imagePath = null;
            }
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
                    imagePath
                    );
            if (replacing) {
                Path previous = imageStorage.toPhysicalPath(currentImage == null ? null : currentImage.value());
                if (previous != null) {
                    imageStorage.delete(previous);
                }
            }
            return ResponseEntity.ok().body(ApiResponseFactory.updated(
                    "Producto actualizado correctamente.", ProductHttpMapper.toResponse(product)));
        } catch (RuntimeException e) {
            if (stored != null) {
                imageStorage.delete(stored);
            }
            throw e;
        }
    }

    @Operation(summary = "Elimina un producto existente",
            description = "Elimina los datos del producto correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Producto eliminado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Producto inexistente.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> delete(
        @PathVariable Long id
    ){
        try{
            deleteProductUseCase.execute(id);
            return ResponseEntity.ok()
                .body(ApiResponseFactory.deleted("Producto eliminado correctamente.", null));
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
