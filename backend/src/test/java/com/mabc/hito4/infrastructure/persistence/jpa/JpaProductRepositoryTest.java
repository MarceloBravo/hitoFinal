package com.mabc.hitoFinal.infrastructure.persistence.jpa;

import com.mabc.hitoFinal.domain.entity.Category;
import com.mabc.hitoFinal.domain.entity.Mark;
import com.mabc.hitoFinal.domain.entity.Product;
import com.mabc.hitoFinal.domain.valueobject.Description;
import com.mabc.hitoFinal.domain.valueobject.Name;
import com.mabc.hitoFinal.domain.valueobject.Price;
import com.mabc.hitoFinal.domain.valueobject.Stock;
import com.mabc.hitoFinal.domain.valueobject.Weight;
import com.mabc.hitoFinal.infrastructure.persistence.entity.CategoryEntity;
import com.mabc.hitoFinal.infrastructure.persistence.entity.MarkEntity;
import com.mabc.hitoFinal.infrastructure.persistence.entity.ProductEntity;
import com.mabc.hitoFinal.infrastructure.persistence.repositories.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        return entity;
    }

    private Product domainProduct() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        Category category = new Category(1L, new Name("Computacion"));
        return new Product(1L, mark, List.of(category),
                new Name("Notebook Lenovo"), new Description("Notebook Lenovo IdeaPad 310"),
                new Stock(12), new Weight(1500), new Price(650000), new Price(800000));
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
}
