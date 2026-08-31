package com.mabc.e_shop.infrastructure.storage;

import com.mabc.e_shop.domain.exception.InvalidImageException;
import com.mabc.e_shop.infrastructure.config.StorageProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalImageStorageTest {

    @TempDir
    Path tempDir;

    private LocalImageStorage storage() {
        return new LocalImageStorage(new StorageProperties(tempDir.toString()));
    }

    private MockMultipartFile image(String name, String contentType) {
        return new MockMultipartFile("image", name, contentType, new byte[]{1, 2, 3});
    }

    @Test
    @DisplayName("store: guarda la imagen y devuelve una ruta absoluta dentro del almacenamiento")
    void storeSavesImageWithUniqueName() throws Exception {
        Path stored = storage().store(image("notebook.png", "image/png"));

        assertTrue(Files.exists(stored));
        assertTrue(stored.isAbsolute());
        assertTrue(stored.startsWith(tempDir));
        assertTrue(stored.getFileName().toString().endsWith(".png"));
    }

    @Test
    @DisplayName("store: rechaza archivos vacios")
    void storeRejectsEmptyFile() {
        LocalImageStorage storage = storage();

        assertThrows(InvalidImageException.class, () -> storage.store(
                new MockMultipartFile("image", "foto.png", "image/png", new byte[0])));
    }

    @Test
    @DisplayName("store: rechaza formatos no permitidos")
    void storeRejectsUnsupportedFormat() {
        LocalImageStorage storage = storage();

        assertThrows(InvalidImageException.class, () -> storage.store(image("foto.gif", "image/gif")));
    }

    @Test
    @DisplayName("delete: elimina el archivo guardado")
    void deleteRemovesStoredFile() throws Exception {
        LocalImageStorage storage = storage();
        Path stored = storage.store(image("notebook.png", "image/png"));

        storage.delete(stored);

        assertFalse(Files.exists(stored));
    }

    @Test
    @DisplayName("delete: ignora rutas fuera del almacenamiento")
    void deleteIgnoresExternalPaths() throws Exception {
        LocalImageStorage storage = storage();
        Path external = tempDir.resolveSibling("external.png");
        Files.write(external, new byte[]{1});

        storage.delete(external);

        assertTrue(Files.exists(external));
        Files.deleteIfExists(external);
    }

    @Test
    @DisplayName("toPublicPath: devuelve la ruta publica /uploads/<archivo>")
    void toPublicPathBuildsWebPath() throws Exception {
        LocalImageStorage storage = storage();
        Path stored = storage.store(image("notebook.png", "image/png"));

        String publicPath = storage.toPublicPath(stored);

        assertEquals("/uploads/" + stored.getFileName().toString(), publicPath);
    }

    @Test
    @DisplayName("toPhysicalPath: resuelve la ruta publica a la fisica local")
    void toPhysicalPathResolvesPublicPath() throws Exception {
        LocalImageStorage storage = storage();
        Path stored = storage.store(image("notebook.png", "image/png"));

        Path physical = storage.toPhysicalPath("/uploads/" + stored.getFileName().toString());

        assertEquals(stored, physical);
        assertTrue(Files.exists(physical));
    }

    @Test
    @DisplayName("toPhysicalPath: devuelve null para rutas http o ajenas al almacenamiento")
    void toPhysicalPathRejectsExternalOrHttp() {
        LocalImageStorage storage = storage();

        assertEquals(null, storage.toPhysicalPath("http://localhost:8080/uploads/a.png"));
        assertEquals(null, storage.toPhysicalPath("https://cdn.example.com/a.png"));
        assertEquals(null, storage.toPhysicalPath("C:/outside/a.png"));
        assertEquals(null, storage.toPhysicalPath("/uploads/../a.png"));
    }
}