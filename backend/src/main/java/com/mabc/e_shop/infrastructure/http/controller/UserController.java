package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.infrastructure.http.dto.UserRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.UserResponseDto;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import com.mabc.e_shop.infrastructure.http.response.ApiResponseFactory;
import com.mabc.e_shop.infrastructure.security.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * Controlador REST de administración de usuarios.
 *
 * <p>Implementa el CRUD completo de usuarios. Todos los endpoints exigen
 * autenticación y quedan restringidos al rol {@code ADMIN}, tanto por las
 * reglas de {@code SecurityConfig} como por {@code @PreAuthorize}.
 */
@Tag(name = "Usuarios", description = "Administración de usuarios (acceso exclusivo para ADMIN).")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final AdminUserService adminUserService;

    /**
     * Crea el controlador con el servicio de administración de usuarios.
     *
     * @param adminUserService servicio que gestiona el CRUD de usuarios.
     */
    public UserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * Lista todos los usuarios registrados.
     *
     * @return la respuesta estándar con el listado de usuarios y estado HTTP 200.
     */
    @Operation(summary = "Lista los usuarios registrados",
            description = "Devuelve todos los usuarios sin su contraseña. Requiere sesión con rol ADMIN.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Listado de usuarios obtenido correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Se requiere una sesión iniciada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403", description = "Se requiere rol ADMIN.")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> findAll() {
        List<UserResponseDto> users = adminUserService.list();
        return ResponseEntity.ok().body(ApiResponseFactory.queried(users));
    }

    /**
     * Obtiene un usuario por su identificador.
     *
     * @param id identificador del usuario.
     * @return la respuesta estándar con el usuario encontrado y estado HTTP 200.
     */
    @Operation(summary = "Busca un usuario por su identificador",
            description = "Retorna el usuario correspondiente al id entregado sin su contraseña.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Usuario encontrado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Se requiere una sesión iniciada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403", description = "Se requiere rol ADMIN."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Usuario inexistente.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(ApiResponseFactory.queried(adminUserService.findById(id)));
    }

    /**
     * Crea un usuario nuevo con rol configurable.
     *
     * @param request datos del usuario a crear.
     * @return la respuesta estándar con el usuario creado y estado HTTP 201.
     */
    @Operation(summary = "Crea un usuario nuevo",
            description = "Registra un usuario con el nombre, correo, contraseña, rol y estado indicados.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201", description = "Usuario creado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Se requiere una sesión iniciada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403", description = "Se requiere rol ADMIN."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409", description = "El correo ya está registrado.")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> create(@Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.status(201).body(ApiResponseFactory.created(
                "Usuario creado correctamente.", adminUserService.create(request)));
    }

    /**
     * Actualiza un usuario existente.
     *
     * <p>La contraseña solo cambia si se entrega una nueva.
     *
     * @param id      identificador del usuario a actualizar.
     * @param request nuevos datos del usuario.
     * @return la respuesta estándar con el usuario actualizado y estado HTTP 200.
     */
    @Operation(summary = "Actualiza un usuario existente",
            description = "Reemplaza los datos del usuario correspondiente al id entregado; la contraseña solo cambia si se entrega.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Usuario actualizado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400", description = "Payload inválido."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Se requiere una sesión iniciada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403", description = "Se requiere rol ADMIN."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Usuario inexistente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409", description = "El nuevo correo ya está en uso.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> update(
        @PathVariable Long id,
        @Valid @RequestBody UserRequestDto request
    ) {
        return ResponseEntity.ok().body(ApiResponseFactory.updated(
                "Usuario actualizado correctamente.", adminUserService.update(id, request)));
    }

    /**
     * Elimina lógicamente (desactiva) un usuario existente.
     *
     * @param id identificador del usuario a eliminar.
     * @return la respuesta estándar con estado HTTP 200.
     */
    @Operation(summary = "Elimina un usuario existente",
            description = "Desactiva la cuenta del usuario correspondiente al id entregado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Usuario eliminado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Se requiere una sesión iniciada."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403", description = "Se requiere rol ADMIN."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404", description = "Usuario inexistente.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        adminUserService.delete(id);
        return ResponseEntity.ok().body(ApiResponseFactory.deleted("Usuario eliminado correctamente.", null));
    }
}
