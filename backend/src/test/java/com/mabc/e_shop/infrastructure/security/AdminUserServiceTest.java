package com.mabc.e_shop.infrastructure.security;

import com.mabc.e_shop.infrastructure.http.dto.UserRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.UserResponseDto;
import com.mabc.e_shop.infrastructure.persistence.entity.User;
import com.mabc.e_shop.infrastructure.persistence.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminUserServiceTest {

    private UserJpaRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        adminUserService = new AdminUserService(userRepository, passwordEncoder);
        when(passwordEncoder.encode("secreta123")).thenReturn("$2a$10$hash");
        when(passwordEncoder.encode("nueva12345")).thenReturn("$2a$10$newhash");
    }

    private User existing(Long id, String email) {
        return new User(id, "Ana Rivera", email, "hash", "USER", true);
    }

    private User savedUser(User unsaved) {
        return new User(1L, unsaved.getName(), unsaved.getEmail(), unsaved.getPassword(),
                unsaved.getRole(), unsaved.getActive());
    }

    @Test
    @DisplayName("create: encripta la contraseña, guarda con el rol y el estado indicados y no expone password")
    void createHashesPasswordAndSetsRoleAndActive() {
        UserRequestDto request = new UserRequestDto("Ana Rivera", "ana@tienda.cl", "secreta123", "ADMIN", false);
        when(userRepository.existsByEmail("ana@tienda.cl")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> savedUser(invocation.getArgument(0)));

        UserResponseDto response = adminUserService.create(request);

        assertEquals(1L, response.id());
        assertEquals("ana@tienda.cl", response.email());
        assertEquals("ADMIN", response.role());
        assertFalse(response.active());
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                "$2a$10$hash".equals(user.getPassword())));
    }

    @Test
    @DisplayName("create: exige contraseña al crear")
    void createRejectsBlankPassword() {
        UserRequestDto request = new UserRequestDto("Ana Rivera", "ana@tienda.cl", "", "USER", true);
        assertThrows(IllegalArgumentException.class, () -> adminUserService.create(request));
    }

    @Test
    @DisplayName("create: rechaza correo duplicado")
    void createRejectsDuplicateEmail() {
        UserRequestDto request = new UserRequestDto("Ana Rivera", "ana@tienda.cl", "secreta123", "USER", true);
        when(userRepository.existsByEmail("ana@tienda.cl")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> adminUserService.create(request));
    }

    @Test
    @DisplayName("update: cambia datos y normaliza el rol a mayúsculas; password no cambia si no se entrega")
    void updateChangesFieldsAndKeepsPasswordWhenBlank() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(existing(id, "ana@tienda.cl")));
        when(userRepository.save(any(User.class))).thenReturn(new User(id, "Ana R.", "ana@tienda.cl", "hash", "ADMIN", false));

        UserRequestDto request = new UserRequestDto("Ana R.", "ana@tienda.cl", null, "admin", false);
        UserResponseDto response = adminUserService.update(id, request);

        assertEquals("Ana R.", response.name());
        assertEquals("ADMIN", response.role());
        assertFalse(response.active());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("update: cambia la contraseña solo si se entrega una nueva")
    void updateRehashesPasswordWhenProvided() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(existing(id, "ana@tienda.cl")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> savedUser(invocation.getArgument(0)));

        UserRequestDto request = new UserRequestDto("Ana Rivera", "ana@tienda.cl", "nueva12345", "USER", true);
        adminUserService.update(id, request);

        verify(passwordEncoder).encode("nueva12345");
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                "$2a$10$newhash".equals(user.getPassword())));
    }

    @Test
    @DisplayName("update: rechaza que el nuevo correo ya esté en uso por otro usuario")
    void updateRejectsEmailInUseElsewhere() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(existing(id, "otro@tienda.cl")));
        when(userRepository.existsByEmail("ana@tienda.cl")).thenReturn(true);

        UserRequestDto request = new UserRequestDto("Ana Rivera", "ana@tienda.cl", null, "USER", true);
        assertThrows(IllegalStateException.class, () -> adminUserService.update(id, request));
    }

    @Test
    @DisplayName("delete: desactiva el usuario en lugar de borrarlo físicamente")
    void deleteDeactivatesUser() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(existing(id, "ana@tienda.cl")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminUserService.delete(id);

        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                !Boolean.TRUE.equals(user.getActive())));
    }

    @Test
    @DisplayName("findById devuelve el usuario sin exponer la contraseña")
    void findByIdReturnsUser() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(existing(id, "ana@tienda.cl")));

        UserResponseDto response = adminUserService.findById(id);

        assertEquals(id, response.id());
        assertEquals("ana@tienda.cl", response.email());
        assertEquals("USER", response.role());
        assertTrue(response.active());
    }

    @Test
    @DisplayName("findById lanza excepción si el usuario no existe")
    void findByIdThrowsWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> adminUserService.findById(99L));
    }

    @Test
    @DisplayName("list devuelve todos los usuarios sin contraseña")
    void listReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(existing(1L, "a@tienda.cl"), existing(2L, "b@tienda.cl")));

        List<UserResponseDto> users = adminUserService.list();

        assertEquals(2, users.size());
    }
}
