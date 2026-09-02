package com.mabc.e_shop.infrastructure.security;

import com.mabc.e_shop.infrastructure.http.dto.UserRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.UserResponseDto;
import com.mabc.e_shop.infrastructure.persistence.entity.User;
import com.mabc.e_shop.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de administración de usuarios, reservado para rol {@code ADMIN}.
 *
 * <p>Implementa el CRUD completo de usuarios: lectura por id, creación con rol
 * configurable, actualización (con contraseña opcional) y eliminación lógica
 * (desactivación). Ninguna respuesta expone la contraseña.
 */
@Service
public class AdminUserService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea el servicio con el repositorio y el codificador de contraseñas.
     *
     * @param userRepository  repositorio Spring Data de usuarios.
     * @param passwordEncoder codificador de contraseñas.
     */
    public AdminUserService(UserJpaRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Devuelve todos los usuarios registrados sin exponer su contraseña.
     *
     * @return lista de usuarios con sus datos básicos.
     */
    public List<UserResponseDto> list() {
        return userRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario.
     * @return el usuario encontrado sin su contraseña.
     * @throws IllegalArgumentException si el usuario no existe (404).
     */
    public UserResponseDto findById(Long id) {
        return toResponse(findUser(id));
    }

    /**
     * Crea un usuario nuevo con el rol y estado indicados.
     *
     * @param request datos del usuario a crear.
     * @return el usuario creado sin su contraseña.
     * @throws IllegalArgumentException si la contraseña no se entrega.
     * @throws IllegalStateException    si el correo ya está registrado (409).
     */
    public UserResponseDto create(UserRequestDto request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria al crear un usuario.");
        }
        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Ya existe un usuario con el correo " + email);
        }
        User user = new User(
            null,
            request.name(),
            email,
            passwordEncoder.encode(request.password()),
            normalizeRole(request.role()),
            Boolean.TRUE.equals(request.active())
        );
        return toResponse(userRepository.save(user));
    }

    /**
     * Actualiza los datos de un usuario existente. La contraseña solo cambia
     * si se entrega una nueva (no nula o en blanco).
     *
     * @param id      identificador del usuario a actualizar.
     * @param request nuevos datos del usuario.
     * @return el usuario actualizado sin su contraseña.
     * @throws IllegalArgumentException si el usuario no existe (404).
     * @throws IllegalStateException    si el nuevo correo ya está en uso por otro usuario (409).
     */
    public UserResponseDto update(Long id, UserRequestDto request) {
        User user = findUser(id);
        boolean emailChanged = !user.getEmail().equals(request.email());
        if (emailChanged && userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Ya existe un usuario con el correo " + request.email());
        }
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(normalizeRole(request.role()));
        user.setActive(Boolean.TRUE.equals(request.active()));
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return toResponse(userRepository.save(user));
    }

    /**
     * Elimina lógicamente (desactiva) un usuario existente.
     *
     * @param id identificador del usuario a desactivar.
     * @throws IllegalArgumentException si el usuario no existe (404).
     */
    public void delete(Long id) {
        User user = findUser(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    private String normalizeRole(String role) {
        return role == null || role.isBlank() ? "USER" : role.trim().toUpperCase();
    }

    private UserResponseDto toResponse(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getActive());
    }
}
