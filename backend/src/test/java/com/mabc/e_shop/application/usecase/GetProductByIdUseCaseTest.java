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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetProductByIdUseCaseTest {

    private ProductRepository productRepository;
    private GetProductByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        useCase = new GetProductByIdUseCase(productRepository);
    }

    @Test
    @DisplayName("Retorna el producto correspondiente al identificador")
    void returnsProductById() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        Product product = new Product(5L, mark, List.of(), new Name("Notebook"),
                new Description("Equipo portátil"), new Stock(10), new Weight(2.5),
                new Price(500.0), new Price(700.0),
                new ImagePath("https://images.example.com/products/notebook.png"));
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        assertEquals(product, useCase.execute(5L));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el producto no existe")
    void rejectsWhenProductNotExists() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
    }
}
