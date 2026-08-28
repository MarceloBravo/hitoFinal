package com.mabc.hitoFinal.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Mi Proyecto hitoFinal")
                        .version("1.0.0")
                        .description("Documentación interactiva de la API REST del proyecto hitoFinal")
                        .contact(new Contact().name("Marcelo Bravo").email("mabc@live.cl")));
    }
}