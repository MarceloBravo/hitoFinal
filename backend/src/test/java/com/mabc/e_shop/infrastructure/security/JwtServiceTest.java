package com.mabc.e_shop.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LXdpdGgtYXQtbGVhc3QtMzItYnl0ZXM=";
    private static final String EMAIL = "ana@tienda.cl";
    private static final long ACCESS_MS = 900_000;
    private static final long REFRESH_MS = 604_800_000;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, ACCESS_MS, REFRESH_MS);
    }

    @Test
    @DisplayName("El access token lleva el correo y el tipo access")
    void accessTokenCarriesEmailAndType() {
        String token = jwtService.generateAccessToken(EMAIL);

        assertEquals(EMAIL, jwtService.extractEmail(token));
        assertEquals("access", jwtService.extractTokenType(token));
    }

    @Test
    @DisplayName("El refresh token lleva el tipo refresh")
    void refreshTokenCarriesRefreshType() {
        String token = jwtService.generateRefreshToken(EMAIL);

        assertEquals(EMAIL, jwtService.extractEmail(token));
        assertEquals("refresh", jwtService.extractTokenType(token));
    }

    @Test
    @DisplayName("El access token es válido para su propio usuario")
    void accessTokenIsValidForItsUser() {
        String token = jwtService.generateAccessToken(EMAIL);

        assertTrue(jwtService.isTokenValid(token, userDetails(EMAIL)));
    }

    @Test
    @DisplayName("El token no es válido para un usuario distinto")
    void tokenIsInvalidForAnotherUser() {
        String token = jwtService.generateAccessToken(EMAIL);

        assertFalse(jwtService.isTokenValid(token, userDetails("otro@tienda.cl")));
    }

    @Test
    @DisplayName("Un token recién emitido no está expirado")
    void freshTokenIsNotExpired() {
        String token = jwtService.generateAccessToken(EMAIL);

        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    @DisplayName("Un token con expiración pasada se detecta como expirado")
    void expiredTokenIsDetected() {
        String token = Jwts.builder()
                .subject(EMAIL)
                .issuedAt(new Date(System.currentTimeMillis() - 60_000))
                .expiration(new Date(System.currentTimeMillis() - 30_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();

        assertTrue(jwtService.isTokenExpired(token));
        assertFalse(jwtService.isTokenValid(token, userDetails(EMAIL)));
    }

    @Test
    @DisplayName("Un token malformado devuelve null en correo y tipo")
    void malformedTokenReturnsNullEmailAndType() {
        assertNull(jwtService.extractEmail("no-es-un-jwt"));
        assertNull(jwtService.extractTokenType("no-es-un-jwt"));
    }

    @Test
    @DisplayName("La vigencia del access token se entrega en segundos")
    void accessTokenExpirationIsReturnedInSeconds() {
        assertEquals(900, jwtService.getAccessTokenExpirationSeconds());
    }

    private UserDetails userDetails(String email) {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);
        return userDetails;
    }
}