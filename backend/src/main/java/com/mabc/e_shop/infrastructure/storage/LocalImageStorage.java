package com.mabc.e_shop.infrastructure.storage;

import com.mabc.e_shop.domain.exception.InvalidImageException;
import com.mabc.e_shop.infrastructure.config.StorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

/**
 * Implementación local de {@link ImageStorage} que guarda los archivos en el
 * directorio configurado en el sistema de archivos.
 *
 * <p>Genera nombres únicos con {@link UUID} para evitar colisiones y
 * sanitiza la entrada validando el formato antes de escribir el archivo.
 */
@Service
public class LocalImageStorage implements ImageStorage {

    private static final Set<String> EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final String PUBLIC_PREFIX = "/uploads/";

    private final Path baseDir;

    public LocalImageStorage(StorageProperties properties) {
        this.baseDir = Path.of(properties.dir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo inicializar el directorio de imágenes.", e);
        }
    }

    @Override
    public Path store(MultipartFile file) {
        String extension = validateImage(file);
        Path target = baseDir.resolve(UUID.randomUUID() + "." + extension);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo almacenar la imagen.", e);
        }
        return target;
    }

    @Override
    public String toPublicPath(Path stored) {
        Path absolute = stored.toAbsolutePath().normalize();
        if (!absolute.startsWith(baseDir)) {
            throw new IllegalStateException("La imagen está fuera del directorio de almacenamiento.");
        }
        return PUBLIC_PREFIX + absolute.getFileName();
    }

    @Override
    public Path toPhysicalPath(String publicPath) {
        if (publicPath == null || publicPath.isBlank()
                || publicPath.startsWith("http://")
                || publicPath.startsWith("https://")) {
            return null;
        }
        String fileName = publicPath.startsWith(PUBLIC_PREFIX)
                ? publicPath.substring(PUBLIC_PREFIX.length())
                : publicPath;
        if (fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) {
            return null;
        }
        return baseDir.resolve(fileName).normalize();
    }

    @Override
    public void delete(Path path) {
        try {
            if (path != null && path.toAbsolutePath().normalize().startsWith(baseDir)) {
                Files.deleteIfExists(path);
            }
        } catch (IOException ignored) {
        }
    }

    private String validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("La imagen del producto es obligatoria.");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        if (!CONTENT_TYPES.contains(contentType)) {
            throw new InvalidImageException("Formato de imagen no permitido. Use JPG, PNG o WebP.");
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String extension = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1) : "";
        if (!EXTENSIONS.contains(extension)) {
            throw new InvalidImageException("La imagen debe tener extensión jpg, jpeg, png o webp.");
        }
        return extension;
    }
}