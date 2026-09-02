package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.DeleteCategoryUseCase;
import com.mabc.e_shop.application.usecase.GetAllCategoriesUseCase;
import com.mabc.e_shop.application.usecase.GetCategoryByIdUseCase;
import com.mabc.e_shop.application.usecase.SaveCategoryUseCase;
import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.infrastructure.http.dto.CategoryRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.CategoryResponseDto;
import com.mabc.e_shop.infrastructure.http.mapper.CategoryHttpMapper;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import com.mabc.e_shop.infrastructure.http.response.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de categorías.
 *
 * <p>Expone los endpoints de consulta, registro y actualización de categorías
 * delegando la lógica en los casos de uso de la capa de aplicación.
 */
@Tag(name = "Categorías", description = "Consulta, registro y actualización de categorías.")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final SaveCategoryUseCase saveCategoryUseCase;
    private final GetAllCategoriesUseCase getAllCategoriesUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    /**
     * Crea el controlador con los casos de uso de categorías.
     *
     * @param saveCategoryUseCase    caso de uso que registra o actualiza categorías.
     * @param getAllCategoriesUseCase caso de uso que consulta todas las categorías.
     * @param getCategoryByIdUseCase  caso de uso que consulta una categoría por id.
     * @param deleteCategoryUseCase   caso de uso que elimina una categoría por id.
     */
    public CategoryController(
        SaveCategoryUseCase saveCategoryUseCase,
        GetAllCategoriesUseCase getAllCategoriesUseCase,
        GetCategoryByIdUseCase getCategoryByIdUseCase,
        DeleteCategoryUseCase deleteCategoryUseCase
    ) {
        this.saveCategoryUseCase = saveCategoryUseCase;
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
        this.getCategoryByIdUseCase = getCategoryByIdUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    /**
     * Obtiene todas las categorías registradas.
     *
     * @return la respuesta estándar con la lista de categorías y estado HTTP 200.
     */
    @Operation(summary = "Lista todas las categorías",
            description = "Retorna el listado completo de categorías registradas.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Consulta exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> findAll() {
        List<CategoryResponseDto> categories = getAllCategoriesUseCase.execute().stream()
                .map(CategoryHttpMapper::toResponse)
                .toList();
        return ResponseEntity.ok().body(ApiResponseFactory.queried(categories));
    }

    /**
     * Obtiene una categoría por su identificador.
     *
     * @param id identificador de la categoría.
     * @return la respuesta estándar con la categoría encontrada y estado HTTP 200.
     */
    @Operation(summary = "Busca una categoría por su identificador",
            description = "Retorna la categoría correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Categoría encontrada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Categoría inexistente.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> findById(@PathVariable Long id) {
        Category category = getCategoryByIdUseCase.execute(id);
        return ResponseEntity.ok().body(ApiResponseFactory.queried(CategoryHttpMapper.toResponse(category)));
    }

    /**
     * Registra una categoría nueva.
     *
     * @param request datos de la categoría a registrar.
     * @return la respuesta estándar con la categoría creada y estado HTTP 201.
     */
    @Operation(summary = "Registra una categoría nueva",
            description = "Crea una categoría validando las reglas de negocio del nombre.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201", description = "Categoría registrada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido o reglas de negocio violadas.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(
        @Valid @RequestBody CategoryRequestDto request
    ) {
        Category category = saveCategoryUseCase.execute(null, request.name(), request.active());
        return ResponseEntity.status(201).body(ApiResponseFactory.created(
                "Categoría registrada correctamente.", CategoryHttpMapper.toResponse(category)));
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param id      identificador de la categoría a actualizar.
     * @param request nuevos datos de la categoría.
     * @return la respuesta estándar con la categoría actualizada y estado HTTP 200.
     */
    @Operation(summary = "Actualiza una categoría existente",
            description = "Reemplaza los datos de la categoría correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Categoría actualizada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido o reglas de negocio violadas."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Categoría inexistente.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> update(
        @PathVariable Long id,
        @Valid @RequestBody CategoryRequestDto request
    ) {
        Category category = saveCategoryUseCase.execute(id, request.name(), request.active());
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Categoría actualizada correctamente.", CategoryHttpMapper.toResponse(category)));
    }

    /**
     * Elimina una categoría existente.
     *
     * @param id identificador de la categoría a eliminar.
     * @return la respuesta estándar con estado HTTP 200.
     */
    @Operation(summary = "Elimina una categoría existente",
            description = "Elimina los datos de la categoría correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Categoría eliminada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Categoría inexistente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409", description = "Categoría en uso por algún producto.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> delete(
        @PathVariable Long id
    ) {
        deleteCategoryUseCase.execute(id);
        return ResponseEntity.ok().body(ApiResponseFactory.deleted("Categoría eliminada correctamente.", null));
    }
}
