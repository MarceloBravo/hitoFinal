package com.mabc.e_shop.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuración de seguridad de la aplicación.
 *
 * <p>Define las reglas de acceso: los endpoints de lectura de productos,
 * categorías, marcas y todo el carrito son públicos para el frontoffice,
 * mientras que la escritura de productos queda restringida a usuarios con
 * rol {@code ADMIN}. Las escrituras de categorías y marcas requieren
 * cualquier usuario autenticado. La administración de usuarios queda
 * restringida al rol {@code ADMIN}. La autenticación es stateless mediante
 * tokens JWT procesados por {@link JwtAuthenticationFilter}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Registra el filtro JWT con sus dependencias.
     *
     * <p>Se declara aquí en lugar de anotarlo como {@code @Component} para
     * mantener el ensamblado de la cadena de seguridad en un único punto y
     * evitar que los slices de test lo instancien sin su {@link JwtService}.
     *
     * @param jwtService          servicio de tokens JWT.
     * @param userDetailsService  cargador de usuarios de Spring Security.
     * @return el filtro de autenticación JWT.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService
    ) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    /**
     * Define la cadena de filtros de seguridad y las reglas de autorización.
     *
     * @param http                  constructor de la cadena de filtros.
     * @param jwtAuthenticationFilter filtro que autentica las peticiones.
     * @return la cadena de filtros configurada.
     * @throws Exception si falla la construcción de la cadena.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/marks/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/carts/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/carts/*/checkout").permitAll()
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Codificador de contraseñas usado al registrar y validar credenciales.
     *
     * @return codificador BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el {@link AuthenticationManager} global para que los puntos de
     * entrada (login) validen las credenciales; se respalda en el
     * {@link org.springframework.security.core.userdetails.UserDetailsService}
     * y en el {@link PasswordEncoder} registrados.
     *
     * @param configuration configuración de autenticación de Spring Security.
     * @return el administrador de autenticación global.
     * @throws Exception si falla la obtención del administrador.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}