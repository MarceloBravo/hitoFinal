package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetAllProductsUseCaseTest {

    private ProductRepository productRepository;
    private GetAllProductsUseCase useCase;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        useCase = new GetAllProductsUseCase(productRepository);
    }

    @Test
    @DisplayName("Retorna todos los productos del repositorio")
    void returnsAllProducts() {
        List<Product> products = List.of();
        when(productRepository.findAll()).thenReturn(products);

        assertEquals(products, useCase.execute());
        verify(productRepository).findAll();
    }
}
