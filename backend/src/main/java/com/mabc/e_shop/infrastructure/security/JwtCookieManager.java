package com.mabc.e_shop.infrastructure.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Administra la cookie {@code HttpOnly} que transporta el refresh token.
 *
 * <p>El refresh token se entrega exclusivamente por {@code Set-Cookie}
 * (ilegible por JavaScript), limitado a la ruta de autenticación, con
 * {@code SameSite=Strict} y rotación en cada renovación. Esto permite
 * mantener la sesión sin exponer el refresh token a ataques XSS.
 */
@Service
public class JwtCookieManager {

    /**
     * Nombre de la cookie que transporta el refresh token.
     */
    public static final String REFRESH_COOKIE = "refresh_token";

    /**
     * Ruta a la que se limita la cookie: solo los endpoints de autenticación.
     */
    private static final String COOKIE_PATH = "/api/v1/auth";

    private final boolean secure;
    private final long refreshExpirationSeconds;

    /**
     * Crea el administrador de la cookie de refresco.
     *
     * @param secure               {@code true} si la cookie solo debe viajar por HTTPS.
     * @param refreshExpirationMs  vigencia del refresh token en milisegundos.
     */
    public JwtCookieManager(
        @Value("${jwt.cookie-secure:false}") boolean secure,
        @Value("${jwt.refresh-token.expiration}") long refreshExpirationMs
    ) {
        this.secure = secure;
        this.refreshExpirationSeconds = refreshExpirationMs / 1000;
    }

    /**
     * Construye el {@code Set-Cookie} que dispone (o rota) el refresh token.
     *
     * @param token refresh token a almacenar en la cookie.
     * @return valor listo para el header {@code Set-Cookie}.
     */
    public String createRefreshCookie(String token) {
        return ResponseCookie.from(REFRESH_COOKIE, token)
            .httpOnly(true)
            .secure(secure)
            .path(COOKIE_PATH)
            .maxAge(refreshExpirationSeconds)
            .sameSite("Strict")
            .build()
            .toString();
    }

    /**
     * Construye el {@code Set-Cookie} que expira la cookie de refresco.
     *
     * @return valor listo para el header {@code Set-Cookie}.
     */
    public String clearRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE, "")
            .httpOnly(true)
            .secure(secure)
            .path(COOKIE_PATH)
            .maxAge(0)
            .sameSite("Strict")
            .build()
            .toString();
    }

    /**
     * Lee el refresh token de la cookie de la petición.
     *
     * @param request petición HTTP entrante.
     * @return el refresh token o {@code null} si la cookie no está presente.
     */
    public String readRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}