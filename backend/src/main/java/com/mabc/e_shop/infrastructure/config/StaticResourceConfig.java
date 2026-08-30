package com.mabc.e_shop.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Expone el directorio de imágenes subidas como recursos estáticos bajo la
 * ruta {@code /uploads/}.
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StaticResourceConfig implements WebMvcConfigurer {

    private final StorageProperties properties;

    public StaticResourceConfig(StorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(properties.dir()).toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}