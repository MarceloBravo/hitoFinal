package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.infrastructure.http.dto.UserResponseDto;
import com.mabc.e_shop.infrastructure.http.response.ApiResponse;
import com.mabc.e_shop.infrastructure.http.response.ApiResponseFactory;
import com.mabc.e_shop.infrastructure.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de consulta de usuarios registrados.
 *
 * <p>Expone el listado de usuarios para sesiones autenticadas, delegando en
 * el {@link AuthService}. Solo permite lectura: la creación y la mantención
 * se gestionan a través del flujo propio de autenticación.
 */
@Tag(name = "Usuarios", description = "Consulta de usuarios registrados.")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final AuthService authService;

    /**
     * Crea el controlador con el servicio que gestiona los usuarios.
     *
     * @param authService servicio que expone el listado de usuarios.
     */
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Lista todos los usuarios registrados.
     *
     * <p>Requiere autenticación; las reglas de acceso devuelven 401 sin sesión.
     *
     * @return la respuesta estándar con el listado de usuarios y estado HTTP 200.
     */
    @Operation(summary = "Lista los usuarios registrados",
            description = "Devuelve todos los usuarios sin su contraseña. Requiere sesión iniciada.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200", description = "Listado de usuarios obtenido correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401", description = "Se requiere una sesión iniciada.")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> findAll() {
        List<UserResponseDto> users = authService.listUsers();
        return ResponseEntity.ok().body(ApiResponseFactory.queried(users));
    }
}