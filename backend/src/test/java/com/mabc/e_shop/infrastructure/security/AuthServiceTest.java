package com.mabc.e_shop.infrastructure.security;

import com.mabc.e_shop.infrastructure.http.dto.UserResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.AuthResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.LoginRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.RegisterRequestDto;
import com.mabc.e_shop.infrastructure.persistence.entity.User;
import com.mabc.e_shop.infrastructure.persistence.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private static final String EMAIL = "ana@tienda.cl";

    private UserJpaRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsService.class);
        authenticationManager = mock(AuthenticationManager.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtService,
                userDetailsService, authenticationManager);
    }

    @Test
    @DisplayName("Registra un usuario, encripta su contraseña y devuelve tokens")
    void registersUserWithHashedPasswordAndTokens() {
        RegisterRequestDto request = new RegisterRequestDto("Ana Rivera", EMAIL, "secreta123");
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode("secreta123")).thenReturn("$2a$10$hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new User(1L, user.getName(), user.getEmail(), user.getPassword(), user.getRole(), user.getActive());
        });
        when(jwtService.generateAccessToken(EMAIL)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(EMAIL)).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);

        AuthResponseDto response = authService.register(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(EMAIL, response.email());
        assertEquals("USER", response.role());
        verify(passwordEncoder).encode("secreta123");
    }

    @Test
    @DisplayName("Rechaza el registro si el correo ya existe")
    void rejectsDuplicateEmail() {
        RegisterRequestDto request = new RegisterRequestDto("Ana Rivera", EMAIL, "secreta123");
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("Devuelve tokens al validar credenciales correctas")
    void loginReturnsTokensForValidCredentials() {
        LoginRequestDto request = new LoginRequestDto(EMAIL, "secreta123");
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn(EMAIL);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
        when(jwtService.generateAccessToken(EMAIL)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(EMAIL)).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);

        AuthResponseDto response = authService.login(request);

        assertEquals("access-token", response.accessToken());
        assertEquals(EMAIL, response.email());
    }

    @Test
    @DisplayName("Propaga el error de credenciales inválidas")
    void loginPropagatesBadCredentials() {
        LoginRequestDto request = new LoginRequestDto(EMAIL, "incorrecta");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas."));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Renueva los tokens con un refresh token válido")
    void refreshReturnsNewTokensForValidRefreshToken() {
        when(jwtService.extractTokenType("refresh-token")).thenReturn("refresh");
        when(jwtService.extractEmail("refresh-token")).thenReturn(EMAIL);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtService.isTokenValid("refresh-token", userDetails)).thenReturn(true);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
        when(jwtService.generateAccessToken(EMAIL)).thenReturn("new-access");
        when(jwtService.generateRefreshToken(EMAIL)).thenReturn("new-refresh");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);

        AuthResponseDto response = authService.refresh("refresh-token");

        assertEquals("new-access", response.accessToken());
        assertEquals("new-refresh", response.refreshToken());
    }

    @Test
    @DisplayName("Rechaza un access token usado como refresh token")
    void refreshRejectsAccessToken() {
        when(jwtService.extractTokenType("access-token")).thenReturn("access");

        assertThrows(BadCredentialsException.class, () -> authService.refresh("access-token"));
    }

    @Test
    @DisplayName("Rechaza un refresh token inválido o expirado")
    void refreshRejectsInvalidOrExpiredToken() {
        when(jwtService.extractTokenType("refresh-token")).thenReturn("refresh");
        when(jwtService.extractEmail("refresh-token")).thenReturn(EMAIL);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtService.isTokenValid("refresh-token", userDetails)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.refresh("refresh-token"));
    }

    @Test
    @DisplayName("Lista los usuarios sin exponer su contraseña")
    void listsUsersWithoutPassword() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "Ana Rivera", EMAIL, "$2a$10$hash", "USER", true),
                new User(2L, "Bruno Díaz", "bruno@tienda.cl", "$2a$10$hash2", "ADMIN", false)));

        List<UserResponseDto> users = authService.listUsers();

        assertEquals(2, users.size());
        assertEquals("ana@tienda.cl", users.get(0).email());
        assertEquals("USER", users.get(0).role());
        assertEquals("ADMIN", users.get(1).role());
    }

    @Test
    @DisplayName("Rechaza un refresh token no parseable")
    void refreshRejectsUnparseableToken() {
        when(jwtService.extractTokenType("basura")).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> authService.refresh("basura"));
    }

    private User user() {
        return new User(1L, "Ana Rivera", EMAIL, "$2a$10$hash", "USER", true);
    }
}