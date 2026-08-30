package com.mabc.e_shop.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Puerto de almacenamiento de imágenes de productos.
 *
 * <p>Permite persistir un archivo de imagen en el sistema de almacenamiento
 * seleccionado y liberar recursos previamente guardados.
 */
public interface ImageStorage {

    /**
     * Guarda una imagen y devuelve su ruta absoluta en el almacenamiento.
     *
     * @param file archivo de imagen recibido en la petición.
     * @return la ruta absoluta donde quedó almacenada la imagen.
     */
    Path store(MultipartFile file);

    /**
     * Elimina una imagen guardada; si la ruta está fuera del almacenamiento
     * la operación se ignora.
     *
     * @param path ruta de la imagen a eliminar.
     */
    void delete(Path path);
}