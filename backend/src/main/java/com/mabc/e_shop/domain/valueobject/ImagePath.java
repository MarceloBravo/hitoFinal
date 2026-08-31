package com.mabc.e_shop.domain.valueobject;

import com.mabc.e_shop.domain.exception.InvalidImageException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * Value object que representa la ruta de la imagen de un producto.
 *
 * <p>Acepta URLs absolutas (http/https/file), rutas de sistema de archivos
 * locales absolutas y rutas web relativas al servidor (que comienzan con
 * {@code /}, como {@code /uploads/...}); rechaza valores vacíos o relativos.
 * Un valor {@code null} se permite (producto sin imagen).
 *
 * @param value ruta de la imagen; puede ser {@code null}.
 */
public record ImagePath(String value) {

    /**
     * Constructor compacto que valida la ruta cuando se entrega un valor.
     *
     * @throws InvalidImageException si la ruta está en blanco o no es válida.
     */
    public ImagePath {
        if (value != null && !isValidPath(value)) {
            throw new InvalidImageException("La ubicación de la imagen no existe o no es válida.");
        }
    }

    private boolean isValidPath(String ruta) {
        // 1. Soporte para URLs web (http / https) o URLs de archivo locales (file://)
        try {
            URI uri = new URI(ruta);
            if (uri.isAbsolute() && uri.getScheme() != null) {
                String scheme = uri.getScheme().toLowerCase();
                if (scheme.equals("http") || scheme.equals("https") || scheme.equals("file")) {
                    return true;
                }
            }
        } catch (URISyntaxException e) {
            // No es una URI válida; se valida como ruta a continuación.
        }

        // 2. Soporte para rutas web relativas al servidor (p. ej. "/uploads/imagen.jpg")
        if (ruta.startsWith("/")) {
            return true;
        }

        // 3. Soporte para rutas de sistema de archivos local (Windows/Linux) o rutas UNC
        try {
            Path path = Path.of(ruta);
            return path.isAbsolute();
        } catch (InvalidPathException e) {
            return false;
        }
    }
}