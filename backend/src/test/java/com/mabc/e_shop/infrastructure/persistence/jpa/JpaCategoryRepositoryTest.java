package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.infrastructure.persistence.entity.CategoryEntity;
import com.mabc.e_shop.infrastructure.persistence.repositories.CategoryJpaRepository;
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

class JpaCategoryRepositoryTest {

    private final CategoryJpaRepository jpaRepository = mock(CategoryJpaRepository.class);
    private final JpaCategoryRepository repository = new JpaCategoryRepository(jpaRepository);

    private CategoryEntity categoryEntity(long id, String name, boolean active) {
        return new CategoryEntity(id, name, active);
    }

    @Test
    @DisplayName("findById: convierte la entidad en una categoria de dominio")
    void findByIdMapsToDomain() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(categoryEntity(1L, "Computacion", true)));

        Optional<Category> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Computacion", result.get().getName().value());
        assertTrue(result.get().isActive());
    }

    @Test
    @DisplayName("findById: propaga el estado inactivo de la categoria")
    void findByIdMapsInactive() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(categoryEntity(1L, "Gaming", false)));

        assertFalse(repository.findById(1L).orElseThrow().isActive());
    }

    @Test
    @DisplayName("findById: devuelve vacio si no existe la categoria")
    void findByIdReturnsEmptyWhenMissing() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(repository.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findAll: convierte todas las categorias al dominio")
    void findAllMapsToDomain() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                categoryEntity(1L, "Computacion", true),
                categoryEntity(2L, "Gaming", false)));

        List<Category> all = repository.findAll();

        assertEquals(2, all.size());
        assertEquals("Computacion", all.get(0).getName().value());
        assertFalse(all.get(1).isActive());
    }

    @Test
    @DisplayName("findAllByIds: convierte las categorias encontradas al dominio")
    void findAllByIdsMapsToDomain() {
        when(jpaRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(categoryEntity(1L, "Computacion", true)));

        List<Category> found = repository.findAllByIds(List.of(1L, 2L));

        assertEquals(1, found.size());
        assertEquals("Computacion", found.get(0).getName().value());
    }

    @Test
    @DisplayName("save: persiste la entidad y devuelve la categoria convertida")
    void savePersistsAndMapsBack() {
        when(jpaRepository.save(any(CategoryEntity.class)))
                .thenReturn(categoryEntity(1L, "Computacion", true));

        Category saved = repository.save(new Category(1L, new com.mabc.e_shop.domain.valueobject.Name("Computacion")));

        assertEquals(1L, saved.getId());
        assertEquals("Computacion", saved.getName().value());
        assertTrue(saved.isActive());
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
