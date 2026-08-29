package com.mabc.e_shop.domain.valueobject;

import com.mabc.e_shop.domain.exception.InvalidImageException;
import java.net.URI;
import java.nio.file.Path;

/**
 * Value object que representa la ruta de la imagen de un producto.
 *
 * <p>Acepta URLs absolutas (http/https/file) o rutas de sistema de archivos
 * locales absolutas; rechaza referencias relativas o valores vacíos.
 *
 * @param value ruta de la imagen.
 */
public record ImagePath(String value) {

    /**
     * Constructor compacto que valida la ruta de la imagen.
     *
     * @throws InvalidImageException si la ruta es nula, está en blanco o no es válida.
     */
    public ImagePath {
        if (value == null || value.isBlank()) {
            throw new InvalidImageException("La ruta de la imagen no puede estar vacia o en blanco.");
        }
        if (!isValidPath(value)) {
            throw new InvalidImageException("La ubicación de la imagen no existe o no es válida.");
        }
    }

    private boolean isValidPath(String ruta) {
        try {
            // 1. Soporte para URLs web (http / https) o URLs de archivo locales (file://)
            URI uri = new URI(ruta);
            if (uri.isAbsolute() && uri.getScheme() != null) {
                String scheme = uri.getScheme().toLowerCase();
                if (scheme.equals("http") || scheme.equals("https") || scheme.equals("file")) {
                    return true;
                }
            }

            // 2. Soporte para rutas de sistema de archivos local (Windows/Linux) o rutas UNC de red (\\servidor\carpeta)
            Path path = Path.of(ruta);
            return path.isAbsolute();

        } catch (Exception e) {
            return false;
        }
    }
}