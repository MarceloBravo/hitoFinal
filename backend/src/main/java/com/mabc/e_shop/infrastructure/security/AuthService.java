package com.mabc.e_shop.infrastructure.security;

import com.mabc.e_shop.infrastructure.http.dto.UserResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.AuthResponseDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.LoginRequestDto;
import com.mabc.e_shop.infrastructure.http.dto.auth.RegisterRequestDto;
import com.mabc.e_shop.infrastructure.persistence.entity.User;
import com.mabc.e_shop.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de autenticación que orquesta el registro, el inicio de sesión
 * y la renovación de tokens.
 *
 * <p>Coordina el repositorio de usuarios, el codificador de contraseñas y el
 * {@link JwtService}, manteniendo el controlador HTTP delgado. No contiene
 * reglas de negocio propias del catálogo de la tienda.
 */
@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "USER";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    /**
     * Crea el servicio con todas sus dependencias.
     *
     * @param userRepository        repositorio Spring Data de usuarios.
     * @param passwordEncoder       codificador de contraseñas.
     * @param jwtService            servicio de tokens JWT.
     * @param userDetailsService    cargador de usuarios de Spring Security.
     * @param authenticationManager administrador de autenticación (login).
     */
    public AuthService(
        UserJpaRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        UserDetailsService userDetailsService,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registra un usuario nuevo con rol {@code USER} y devuelve sus tokens.
     *
     * @param request datos del usuario a registrar.
     * @return tokens de sesión del usuario creado.
     * @throws IllegalStateException si el correo ya está registrado (409).
     */
    public AuthResponseDto register(RegisterRequestDto request) {
        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Ya existe un usuario con el correo " + email);
        }
        User user = new User(
            null,
            request.name(),
            email,
            passwordEncoder.encode(request.password()),
            DEFAULT_ROLE,
            true
        );
        return buildAuthResponse(userRepository.save(user));
    }

    /**
     * Inicia sesión validando las credenciales con {@link AuthenticationManager}.
     *
     * @param request credenciales del usuario.
     * @return tokens de sesión del usuario autenticado.
     * @throws org.springframework.security.core.AuthenticationException
     *         si las credenciales son inválidas o el usuario está inactivo (401).
     */
    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        return buildAuthResponse(findByEmail(request.email()));
    }

    /**
     * Renueva la sesión validando el refresh token (entregado en la cookie
     * {@code HttpOnly}) y emitiendo un par nuevo de tokens (flujo stateless).
     *
     * @param refreshToken refresh token vigente, o {@code null} si no hay cookie.
     * @return nuevos tokens de sesión del usuario.
     * @throws BadCredentialsException si el token no es de refresco o no es válido (401).
     */
    public AuthResponseDto refresh(String refreshToken) {
        if (refreshToken == null || !REFRESH_TOKEN_TYPE.equals(jwtService.extractTokenType(refreshToken))) {
            throw new BadCredentialsException("Refresh token inválido.");
        }
        String email = jwtService.extractEmail(refreshToken);
        if (email == null) {
            throw new BadCredentialsException("Refresh token inválido.");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException("Refresh token inválido o expirado.");
        }
        return buildAuthResponse(findByEmail(email));
    }

    /**
     * Devuelve todos los usuarios registrados, sin exponer su contraseña.
     *
     * @return lista de usuarios con sus datos básicos; vacía si no hay ninguno.
     */
    public List<UserResponseDto> listUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getActive()))
            .toList();
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + email));
    }

    private AuthResponseDto buildAuthResponse(User user) {
        return new AuthResponseDto(
            jwtService.generateAccessToken(user.getEmail()),
            jwtService.generateRefreshToken(user.getEmail()),
            "Bearer",
            jwtService.getAccessTokenExpirationSeconds(),
            user.getEmail(),
            user.getRole()
        );
    }
}