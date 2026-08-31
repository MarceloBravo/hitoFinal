package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.infrastructure.storage.ImageStorage;

import java.nio.file.Path;

/**
 * Caso de uso que elimina un producto por su identificador.
 *
 * <p>Si el producto posee una imagen local asociada, se libera el archivo del
 * almacenamiento antes de persistir la eliminación.
 */
public class DeleteProductUseCase {

    private final ProductRepository productRepository;
    private final ImageStorage imageStorage;

    /**
     * Crea el caso de uso con el repositorio de productos y el almacenamiento
     * de imágenes.
     *
     * @param productRepository repositorio de productos.
     * @param imageStorage       almacenamiento de las imágenes de los productos.
     */
    public DeleteProductUseCase(
        ProductRepository productRepository,
        ImageStorage imageStorage
    ) {
        this.productRepository = productRepository;
        this.imageStorage = imageStorage;
    }

    /**
     * Elimina el producto correspondiente al identificador entregado junto con
     * su imagen local, si la posee.
     *
     * @param id identificador del producto a eliminar.
     * @return {@code true} si el producto fue eliminado.
     * @throws IllegalArgumentException si el identificador es {@code null}.
     * @throws ResourceNotFoundException si el producto no existe.
     */
    public boolean execute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del producto a eliminar es obligatorio.");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no existe."));

        deleteImageIfLocal(product.getImagePath());
        productRepository.deleteById(id);
        return true;
    }

    private void deleteImageIfLocal(ImagePath imagePath) {
        if (imagePath == null) {
            return;
        }
        String value = imagePath.value();
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return;
        }
        try {
            imageStorage.delete(Path.of(value));
        } catch (RuntimeException ignored) {
            // No bloquea la eliminación del producto si la imagen no puede liberarse.
        }
    }
}
