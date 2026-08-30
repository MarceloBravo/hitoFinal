package com.mabc.e_shop.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración del almacenamiento local de imágenes.
 *
 * <p>Define el directorio donde se guardan los archivos subidos; si no se
 * configura, se usa {@code uploads} relativo al directorio de trabajo.
 *
 * @param dir directorio base para almacenar las imágenes.
 */
@ConfigurationProperties(prefix = "app.upload")
public record StorageProperties(String dir) {

    private static final String DEFAULT_DIR = "uploads";

    public StorageProperties {
        if (dir == null || dir.isBlank()) {
            dir = DEFAULT_DIR;
        }
    }
}