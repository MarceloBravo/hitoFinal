package com.mabc.e_shop.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI (Swagger).
 *
 * <p>Declara el esquema de seguridad {@code bearerAuth} (JWT) usado por los
 * endpoints que exigen sesión. Solo se aplica a las operaciones que lo
 * requieren mediante {@code @SecurityRequirement}; los endpoints públicos
 * quedan documentados sin candado.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Registra la especificación OpenAPI con el esquema de autenticación.
     *
     * <p>Con el esquema declarado, Swagger-UI muestra el botón «Authorize»
     * donde se pega el access token devuelto por {@code /api/v1/auth/login}.
     *
     * @return la especificación OpenAPI de la aplicación.
     */
    @Bean
    public OpenAPI hitoFinalOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}