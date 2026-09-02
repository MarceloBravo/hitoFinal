package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;
import com.mabc.e_shop.infrastructure.persistence.entity.CategoryEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.MarkEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.ProductEntity;
import com.mabc.e_shop.infrastructure.persistence.repositories.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaProductRepositoryTest {

    private final ProductJpaRepository jpaRepository = mock(ProductJpaRepository.class);
    private final JpaProductRepository repository = new JpaProductRepository(jpaRepository);

    private ProductEntity productEntity() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setMark(new MarkEntity(1L, "Lenovo", true));
        entity.setCategories(List.of(new CategoryEntity(1L, "Computacion", true)));
        entity.setName("Notebook Lenovo");
        entity.setDescription("Notebook Lenovo IdeaPad 310");
        entity.setStock(12);
        entity.setWeight(1500);
        entity.setPriceCost(650000);
        entity.setPriceSale(800000);
        entity.setImagePath("https://images.example.com/products/notebook.png");
        return entity;
    }

    private Product domainProduct() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        Category category = new Category(1L, new Name("Computacion"));
        return new Product(1L, mark, List.of(category),
                new Name("Notebook Lenovo"), new Description("Notebook Lenovo IdeaPad 310"),
                new Stock(12), new Weight(1500), new Price(650000), new Price(800000),
                new ImagePath("https://images.example.com/products/notebook.png"));
    }

    @Test
    @DisplayName("findById: convierte la entidad en un producto de dominio")
    void findByIdMapsToDomain() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(productEntity()));

        Optional<Product> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Notebook Lenovo", result.get().getName().value());
        assertEquals("Lenovo", result.get().getMark().getName().value());
        assertEquals(1, result.get().getCategories().size());
    }

    @Test
    @DisplayName("findById: devuelve vacio si no existe el producto")
    void findByIdReturnsEmptyWhenMissing() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(repository.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findAll: convierte todos los productos al dominio")
    void findAllMapsToDomain() {
        when(jpaRepository.findAll()).thenReturn(List.of(productEntity()));

        List<Product> all = repository.findAll();

        assertEquals(1, all.size());
        assertEquals("Notebook Lenovo", all.get(0).getName().value());
    }

    @Test
    @DisplayName("findAll(page,size): delega la paginacion y devuelve contenido y total")
    void findAllPaginatedMapsToDomain() {
        when(jpaRepository.findAll(PageRequest.of(2, 10)))
                .thenReturn(new PageImpl<>(List.of(productEntity()), PageRequest.of(2, 10), 50));

        ProductRepository.PageResult result = repository.findAll(2, 10);

        assertEquals(1, result.content().size());
        assertEquals("Notebook Lenovo", result.content().get(0).getName().value());
        assertEquals(50, result.total());
        verify(jpaRepository).findAll((Pageable) PageRequest.of(2, 10));
    }

    @Test
    @DisplayName("findAll(page,size,filtros,search): delega en findFilteredWithSearch y convierte al dominio")
    void findAllFilteredWithSearchMapsToDomain() {
        when(jpaRepository.findFilteredWithSearch(1L, 3L, 500.0, 800000.0, "lenovo", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(productEntity()), PageRequest.of(0, 10), 1));

        ProductRepository.PageResult result = repository.findAll(0, 10, 1L, 3L, 500.0, 800000.0, "lenovo");

        assertEquals(1, result.content().size());
        assertEquals("Notebook Lenovo", result.content().get(0).getName().value());
        assertEquals(1, result.total());
        verify(jpaRepository).findFilteredWithSearch(1L, 3L, 500.0, 800000.0, "lenovo", PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("save: persiste la entidad y devuelve el producto convertido")
    void savePersistsAndMapsBack() {
        when(jpaRepository.save(any(ProductEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product saved = repository.save(domainProduct());

        assertEquals(1L, saved.getId());
        assertEquals("Notebook Lenovo", saved.getName().value());
        assertEquals(800000, saved.getPriceSale().value());
    }

    @Test
    @DisplayName("deleteById: delega la eliminacion en el repositorio Spring Data")
    void deleteByIdDelegates() {
        repository.deleteById(1L);

        verify(jpaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("existsById: delega la consulta de existencia")
    void existsByIdDelegates() {
        when(jpaRepository.existsById(1L)).thenReturn(true);
        when(jpaRepository.existsById(2L)).thenReturn(false);

        assertTrue(repository.existsById(1L));
        assertFalse(repository.existsById(2L));
    }

    @Test
    @DisplayName("existsProductWithCategory: delega el conteo de productos por categoría")
    void existsProductWithCategoryDelegates() {
        when(jpaRepository.countProductsByCategoryId(1L)).thenReturn(2L);
        when(jpaRepository.countProductsByCategoryId(2L)).thenReturn(0L);

        assertTrue(repository.existsProductWithCategory(1L));
        assertFalse(repository.existsProductWithCategory(2L));
    }
}
