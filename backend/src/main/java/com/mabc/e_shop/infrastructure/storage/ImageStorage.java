package com.mabc.e_shop.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Puerto de almacenamiento de imágenes de productos.
 *
 * <p>Permite persistir un archivo de imagen en el sistema de almacenamiento
 * y liberar recursos previamente guardados. También expone la conversión
 * entre la ruta física en disco y la ruta pública servida por HTTP.
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
     * Convierte la ruta física de una imagen en su ruta pública accesible
     * por HTTP (por ejemplo {@code /uploads/<archivo>}).
     *
     * @param stored ruta física de la imagen almacenada.
     * @return la ruta pública de la imagen.
     */
    String toPublicPath(Path stored);

    /**
     * Convierte la ruta pública de una imagen en su ruta física en disco.
     *
     * <p>Devuelve {@code null} si la ruta no corresponde a una imagen del
     * almacenamiento (por ejemplo rutas absolutas externas o HTTP).
     *
     * @param publicPath ruta pública de la imagen.
     * @return la ruta física de la imagen o {@code null} si no es local.
     */
    Path toPhysicalPath(String publicPath);

    /**
     * Elimina una imagen guardada; si la ruta está fuera del almacenamiento
     * la operación se ignora.
     *
     * @param path ruta de la imagen a eliminar.
     */
    void delete(Path path);
}