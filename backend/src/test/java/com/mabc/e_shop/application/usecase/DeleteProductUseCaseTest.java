package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;
import com.mabc.e_shop.infrastructure.storage.ImageStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteProductUseCaseTest {

    private ProductRepository productRepository;
    private ImageStorage imageStorage;
    private DeleteProductUseCase useCase;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        imageStorage = mock(ImageStorage.class);
        useCase = new DeleteProductUseCase(productRepository, imageStorage);
    }

    private Product buildProduct(Long id) {
        return new Product(id, new Mark(1L, new Name("Lenovo")), List.of(),
                new Name("Notebook"), new Description("Equipo portátil"), new Stock(10),
                new Weight(2.5), new Price(500.0), new Price(700.0),
                new ImagePath("https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("Elimina un producto existente con imagen remota sin borrar el archivo")
    void deletesProductWithRemoteImage() {
        Product product = buildProduct(5L);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        assertTrue(useCase.execute(5L));
        verify(productRepository).deleteById(5L);
        verify(imageStorage, never()).delete(any(Path.class));
    }

    @Test
    @DisplayName("Elimina la imagen local al borrar un producto que la posee")
    void deletesLocalImageWhenProductHasOne() {
        Path local = Path.of("C:/uploads/uuid.png");
        Product product = new Product(5L, new Mark(1L, new Name("Lenovo")), List.of(),
                new Name("Notebook"), new Description("Equipo portátil"), new Stock(10),
                new Weight(2.5), new Price(500.0), new Price(700.0), new ImagePath(local.toString()));
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        useCase.execute(5L);
        verify(imageStorage).delete(local);
        verify(productRepository).deleteById(5L);
    }

    @Test
    @DisplayName("No elimina imagen cuando el producto no la posee")
    void doesNotDeleteImageWhenProductHasNone() {
        Product product = new Product(5L, new Mark(1L, new Name("Lenovo")), List.of(),
                new Name("Notebook"), new Description("Equipo portátil"), new Stock(10),
                new Weight(2.5), new Price(500.0), new Price(700.0), null);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        useCase.execute(5L);
        verify(imageStorage, never()).delete(any(Path.class));
        verify(productRepository).deleteById(5L);
    }

    @Test
    @DisplayName("Lanza IllegalArgumentException cuando el id es nulo")
    void rejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el producto no existe")
    void rejectsWhenProductNotExists() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
        verify(productRepository, never()).deleteById(99L);
    }
}
