package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateProductUseCaseTest {

    private List<Product> storedProducts;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private MarkRepository markRepository;
    private CreateProductUseCase useCase;
    private Mark mark;
    private Category category;

    @BeforeEach
    void setUp() {
        storedProducts = new ArrayList<>();
        productRepository = mock(ProductRepository.class);
        when(productRepository.findAll()).thenAnswer(invocation -> List.copyOf(storedProducts));
        when(productRepository.findById(any())).thenAnswer(invocation -> storedProducts.stream()
                .filter(product -> product.getId().equals(invocation.getArgument(0)))
                .findFirst());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            if(product.getId() == null){
                product = new Product(1L, product.getMark(), product.getCategories(), product.getName(), product.getDescription(), product.getStock(), product.getWeight(), product.getPriceCost(), product.getPriceSale(), product.getImagePath());
            }
            storedProducts.add(product);
            return product;
        });

        categoryRepository = mock(CategoryRepository.class);
        markRepository = mock(MarkRepository.class);
        mark = new Mark(1L, new Name("Lenovo"));
        category = new Category(1L, new Name("Computacion"));

        Mark mark2 = new Mark(2L, new Name("Apple"));
        Category category2 = new Category(2L, new Name("Gaming"));

        when(markRepository.findById(1L)).thenReturn(Optional.of(mark));
        when(markRepository.findById(2L)).thenReturn(Optional.of(mark2));
        when(categoryRepository.findAllByIds(List.of(1L))).thenReturn(List.of(category));
        when(categoryRepository.findAllByIds(List.of(2L))).thenReturn(List.of(category2));
        when(categoryRepository.findAllByIds(List.of(1L, 2L))).thenReturn(List.of(category, category2));

        useCase = new CreateProductUseCase(productRepository, categoryRepository, markRepository);
    }

    @Test
    @DisplayName("Crea un producto nuevo con sus value objects")
    void createsProduct() {
        Product product = useCase.execute(null, 1L, List.of(1L),
                "Notebook Lenovo", "Notebook Lenovo IdeaPad 310", 12, 1500, 650000, 800000,
                "https://images.example.com/products/notebook.png");

        assertNotNull(product);
        assertEquals("Notebook Lenovo", product.getName().value());
        assertEquals(12, product.getStock().value());
        assertEquals(800000, product.getPriceSale().value());
    }

    @Test
    @DisplayName("Actualiza un producto existente manteniendo el id")
    void updatesProductKeepingId() {
        Product existing = useCase.execute(null, 1L, List.of(1L),
                "Old", "Desc", 10, 1500, 650000, 800000,
                "https://images.example.com/products/notebook.png");

        Product updated = useCase.execute(existing.getId(), 1L, List.of(1L),
                "New", "Desc2", 5, 1500, 700000, 900000,
                "https://images.example.com/products/notebook.png");

        assertEquals(existing.getId(), updated.getId());
        assertEquals("New", updated.getName().value());
        assertEquals(5, updated.getStock().value());
        assertEquals(900000, updated.getPriceSale().value());
    }

    @Test
    @DisplayName("Actualiza la marca y categorias al modificar un producto existente")
    void updatesMarkAndCategories() {
        Product existing = useCase.execute(null, 1L, List.of(1L),
                "Old", "Desc", 10, 1500, 650000, 800000,
                "https://images.example.com/products/notebook.png");

        Product updated = useCase.execute(existing.getId(), 2L, List.of(2L),
                "Updated", "Desc2", 5, 1500, 700000, 900000,
                "https://images.example.com/products/notebook.png");

        assertEquals(existing.getId(), updated.getId());
        assertEquals("Apple", updated.getMark().getName().value());
        assertEquals("Gaming", updated.getCategories().get(0).getName().value());
    }

    @Test
    @DisplayName("Lanza excepcion si la marca no existe")
    void rejectsWhenMarkNotExists() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                null, 999L, List.of(1L), "Name", "Desc", 1, 1500, 1, 2,
                "https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("Lanza excepcion si alguna categoria no existe")
    void rejectsWhenCategoryNotExists() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                null, 1L, List.of(999L), "Name", "Desc", 1, 1500, 1, 2,
                "https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("Lanza excepcion si se actualiza un producto inexistente")
    void rejectsUpdatingMissingProduct() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                99L, 1L, List.of(1L), "Name", "Desc", 1, 1500, 1, 2,
                "https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("Lanza excepcion si la lista de categorias esta vacia")
    void rejectsEmptyCategoryList() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(
                null, 1L, List.of(), "Name", "Desc", 1, 1500, 1, 2,
                "https://images.example.com/products/notebook.png"));
    }

    /*
    @Test
    @DisplayName("Asigna un id incremental cuando se crean varios productos")
    void assignsIncrementalIds() {
        Product first = useCase.execute(null, 1L, List.of(1L),
                "Producto 1", "Desc", 1, 1500, 1, 2,
                "https://images.example.com/products/notebook.png");
        Product second = useCase.execute(null, 1L, List.of(1L),
                "Producto 2", "Desc", 1, 1500, 1, 2,
                "https://images.example.com/products/notebook.png");

        assertEquals(1L, first.getId());
        assertEquals(2L, second.getId());
    }
    */
}
