package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.DeleteMarkUseCase;
import com.mabc.e_shop.application.usecase.GetAllMarksUseCase;
import com.mabc.e_shop.application.usecase.GetMarkByIdUseCase;
import com.mabc.e_shop.application.usecase.SaveMarkUseCase;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.infrastructure.http.dto.MarkRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.MarkResponseDto;
import com.mabc.e_shop.infrastructure.http.mapper.MarkHttpMapper;
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
 * Controlador REST de marcas.
 *
 * <p>Expone los endpoints de consulta, registro y actualización de marcas
 * delegando la lógica en los casos de uso de la capa de aplicación.
 */
@Tag(name = "Marcas", description = "Consulta, registro y actualización de marcas.")
@RestController
@RequestMapping("/api/v1/marks")
public class MarkController {

    private final SaveMarkUseCase saveMarkUseCase;
    private final GetAllMarksUseCase getAllMarksUseCase;
    private final GetMarkByIdUseCase getMarkByIdUseCase;
    private final DeleteMarkUseCase deleteMarkUseCase;

    /**
     * Crea el controlador con los casos de uso de marcas.
     *
     * @param saveMarkUseCase    caso de uso que registra o actualiza marcas.
     * @param getAllMarksUseCase caso de uso que consulta todas las marcas.
     * @param getMarkByIdUseCase caso de uso que consulta una marca por id.
     * @param deleteMarkUseCase  caso de uso que elimina una marca por id.
     */
    public MarkController(
        SaveMarkUseCase saveMarkUseCase,
        GetAllMarksUseCase getAllMarksUseCase,
        GetMarkByIdUseCase getMarkByIdUseCase,
        DeleteMarkUseCase deleteMarkUseCase
    ) {
        this.saveMarkUseCase = saveMarkUseCase;
        this.getAllMarksUseCase = getAllMarksUseCase;
        this.getMarkByIdUseCase = getMarkByIdUseCase;
        this.deleteMarkUseCase = deleteMarkUseCase;
    }

    /**
     * Obtiene todas las marcas registradas.
     *
     * @return la respuesta estándar con la lista de marcas y estado HTTP 200.
     */
    @Operation(summary = "Lista todas las marcas",
            description = "Retorna el listado completo de marcas registradas.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Consulta exitosa."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> findAll() {
        List<MarkResponseDto> marks = getAllMarksUseCase.execute().stream()
                .map(MarkHttpMapper::toResponse)
                .toList();
        return ResponseEntity.ok().body(ApiResponseFactory.queried(marks));
    }

    /**
     * Obtiene una marca por su identificador.
     *
     * @param id identificador de la marca.
     * @return la respuesta estándar con la marca encontrada y estado HTTP 200.
     */
    @Operation(summary = "Busca una marca por su identificador",
            description = "Retorna la marca correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Marca encontrada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Marca inexistente.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarkResponseDto>> findById(@PathVariable Long id) {
        Mark mark = getMarkByIdUseCase.execute(id);
        return ResponseEntity.ok().body(ApiResponseFactory.queried(MarkHttpMapper.toResponse(mark)));
    }

    /**
     * Registra una marca nueva.
     *
     * @param request datos de la marca a registrar.
     * @return la respuesta estándar con la marca creada y estado HTTP 201.
     */
    @Operation(summary = "Registra una marca nueva",
            description = "Crea una marca validando las reglas de negocio del nombre.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201", description = "Marca registrada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido o reglas de negocio violadas.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ApiResponse<MarkResponseDto>> create(@Valid @RequestBody MarkRequestDto request) {
        Mark mark = saveMarkUseCase.execute(null, request.name(), request.active());
        return ResponseEntity.status(201)
                .body(ApiResponseFactory.created("Marca registrada correctamente.", MarkHttpMapper.toResponse(mark)));
    }

    /**
     * Actualiza una marca existente.
     *
     * @param id      identificador de la marca a actualizar.
     * @param request nuevos datos de la marca.
     * @return la respuesta estándar con la marca actualizada y estado HTTP 200.
     */
    @Operation(summary = "Actualiza una marca existente",
            description = "Reemplaza los datos de la marca correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Marca actualizada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido o reglas de negocio violadas."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Marca inexistente.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MarkResponseDto>> update(
        @PathVariable Long id,
        @Valid @RequestBody MarkRequestDto request
    ) {
        Mark mark = saveMarkUseCase.execute(id, request.name(), request.active());
        return ResponseEntity.ok()
                .body(ApiResponseFactory.updated("Marca actualizada correctamente.", MarkHttpMapper.toResponse(mark)));
    }

    /**
     * Elimina una marca existente.
     *
     * @param id identificador de la marca a eliminar.
     * @return la respuesta estándar con estado HTTP 200.
     */
    @Operation(summary = "Elimina una marca existente",
            description = "Elimina los datos de la marca correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Marca eliminada correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Marca inexistente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409", description = "Marca en uso por algún producto.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<MarkResponseDto>> delete(
        @PathVariable Long id
    ) {
        deleteMarkUseCase.execute(id);
        return ResponseEntity.ok().body(ApiResponseFactory.deleted("Marca eliminada correctamente.", null));
    }
}
