package com.mabc.e_shop.infrastructure.config;

import com.mabc.e_shop.application.usecase.AddItemToCartUseCase;
import com.mabc.e_shop.application.usecase.CreateCartUseCase;
import com.mabc.e_shop.application.usecase.CreateProductUseCase;
import com.mabc.e_shop.application.usecase.SaveCategoryUseCase;
import com.mabc.e_shop.application.usecase.SaveMarkUseCase;
import com.mabc.e_shop.domain.repository.CartRepository;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ApplicationConfigTest {

    private final ApplicationConfig config = new ApplicationConfig();

    @Test
    @DisplayName("Construye el caso de uso de marcas con su repositorio")
    void buildsSaveMarkUseCase() {
        SaveMarkUseCase useCase = config.saveMarkUseCase(mock(MarkRepository.class));

        assertNotNull(useCase);
        assertInstanceOf(SaveMarkUseCase.class, useCase);
    }

    @Test
    @DisplayName("Construye el caso de uso de categorías con su repositorio")
    void buildsSaveCategoryUseCase() {
        SaveCategoryUseCase useCase = config.saveCategoryUseCase(mock(CategoryRepository.class));

        assertNotNull(useCase);
        assertInstanceOf(SaveCategoryUseCase.class, useCase);
    }

    @Test
    @DisplayName("Construye el caso de uso de productos con sus tres repositorios")
    void buildsCreateProductUseCase() {
        CreateProductUseCase useCase = config.createProductUseCase(
                mock(ProductRepository.class), mock(CategoryRepository.class), mock(MarkRepository.class));

        assertNotNull(useCase);
        assertInstanceOf(CreateProductUseCase.class, useCase);
    }

    @Test
    @DisplayName("Construye el caso de uso de creación de carritos con su repositorio")
    void buildsCreateCartUseCase() {
        CreateCartUseCase useCase = config.createCartUseCase(mock(CartRepository.class));

        assertNotNull(useCase);
        assertInstanceOf(CreateCartUseCase.class, useCase);
    }

    @Test
    @DisplayName("Construye el caso de uso de agregado de ítems con sus dos repositorios")
    void buildsAddItemToCartUseCase() {
        AddItemToCartUseCase useCase = config.addItemToCartUseCase(
                mock(CartRepository.class), mock(ProductRepository.class));

        assertNotNull(useCase);
        assertInstanceOf(AddItemToCartUseCase.class, useCase);
    }
}
