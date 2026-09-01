package com.mabc.e_shop.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Servicio responsable de la generación y validación de tokens JWT.
 *
 * <p>Maneja únicamente operaciones sobre tokens (crear access/refresh,
 * extraer claims y verificar vigencia), sin conocer la base de datos ni la
 * lógica de negocio del usuario. Cada token lleva un claim {@code type}
 * que distingue si es de acceso o de refresco, de modo que un access token
 * jamás sea aceptado como refresh token.
 */

/**
 * Servicio responsable de la generación y validación de tokens JWT.
 *
 * <p>Maneja únicamente operaciones sobre tokens (crear access/refresh,
 * extraer claims y verificar vigencia), sin conocer la base de datos ni la
 * lógica de negocio del usuario.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    /**
     * Crea el servicio JWT con la clave de firma y las duraciones configuradas.
     *
     * @param secret               secreto en Base64 de al menos 32 bytes (HS256).
     * @param accessExpirationMs   vigencia del access token en milisegundos.
     * @param refreshExpirationMs  vigencia del refresh token en milisegundos.
     */
    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token.expiration}") long accessExpirationMs,
        @Value("${jwt.refresh-token.expiration}") long refreshExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    /**
     * Genera un access token de corta duración para el usuario entregado.
     *
     * @param email correo del usuario autenticado.
     * @return token JWT firmado de tipo acceso.
     */
    public String generateAccessToken(String email) {
        return generateToken(email, TokenType.ACCESS, accessExpirationMs);
    }

    /**
     * Genera un refresh token de larga duración para el usuario entregado.
     *
     * @param email correo del usuario autenticado.
     * @return token JWT firmado de tipo refresco.
     */
    public String generateRefreshToken(String email) {
        return generateToken(email, TokenType.REFRESH, refreshExpirationMs);
    }

    /**
     * Extrae el correo electrónico (subject) del token.
     *
     * @param token token JWT.
     * @return correo del usuario o {@code null} si el token no es parseable.
     */
    public String extractEmail(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Indica el tipo del token: {@code access} o {@code refresh}.
     *
     * @param token token JWT.
     * @return el tipo del token o {@code null} si no es parseable.
     */
    public String extractTokenType(String token) {
        try {
            return extractClaim(token, claims -> claims.get("type", String.class));
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Verifica que el token pertenezca al usuario y no haya expirado.
     *
     * @param token       token JWT.
     * @param userDetails usuario contra el que se valida.
     * @return {@code true} si el token es válido para el usuario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email != null
            && email.equals(userDetails.getUsername())
            && !isTokenExpired(token);
    }

    /**
     * Indica si el token ha expirado.
     *
     * <p>jjwt lanza {@link ExpiredJwtException} al parsear un token cuya
     * vigencia ya pasó, por lo que se captura y se traduce a {@code true}.
     *
     * @param token token JWT.
     * @return {@code true} si la fecha de expiración ya pasó.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration != null && expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Entrega la vigencia del access token en segundos (para el cliente).
     *
     * @return segundos de vida del access token.
     */
    public long getAccessTokenExpirationSeconds() {
        return accessExpirationMs / 1000;
    }

    private String generateToken(String email, TokenType type, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
            .subject(email)
            .claim("type", type.value)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expirationMs))
            .signWith(key, Jwts.SIG.HS256)
            .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseClaims(token));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Tipos de token emitidos por el servicio.
     */
    private enum TokenType {
        ACCESS("access"),
        REFRESH("refresh");

        private final String value;

        TokenType(String value) {
            this.value = value;
        }
    }
}