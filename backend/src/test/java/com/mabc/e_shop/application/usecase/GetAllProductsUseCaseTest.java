package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.repository.ProductRepository.PageResult;
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

    @Test
    @DisplayName("Retorna el resultado paginado delegando en el repositorio")
    void returnsPaginatedProducts() {
        PageResult pageResult = new PageResult(List.of(), 50);
        when(productRepository.findAll(2, 10)).thenReturn(pageResult);

        assertEquals(pageResult, useCase.execute(2, 10));
        verify(productRepository).findAll(2, 10);
    }

    @Test
    @DisplayName("Retorna el resultado paginado filtrado por categoría delegando en el repositorio")
    void returnsPaginatedProductsFilteredByCategory() {
        PageResult pageResult = new PageResult(List.of(), 12);
        when(productRepository.findAll(0, 5, 9L)).thenReturn(pageResult);

        assertEquals(pageResult, useCase.execute(0, 5, 9L));
        verify(productRepository).findAll(0, 5, 9L);
    }
}
