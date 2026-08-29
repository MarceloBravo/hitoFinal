package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.infrastructure.persistence.entity.MarkEntity;
import com.mabc.e_shop.infrastructure.persistence.repositories.MarkJpaRepository;
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

class JpaMarkRepositoryTest {

    private final MarkJpaRepository jpaRepository = mock(MarkJpaRepository.class);
    private final JpaMarkRepository repository = new JpaMarkRepository(jpaRepository);

    private MarkEntity markEntity(long id, String name, boolean active) {
        return new MarkEntity(id, name, active);
    }

    @Test
    @DisplayName("findById: convierte la entidad en una marca de dominio")
    void findByIdMapsToDomain() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(markEntity(1L, "Lenovo", true)));

        Optional<Mark> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Lenovo", result.get().getName().value());
        assertTrue(result.get().isActive());
    }

    @Test
    @DisplayName("findById: propaga el estado inactivo de la marca")
    void findByIdMapsInactive() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(markEntity(1L, "Asus", false)));

        assertFalse(repository.findById(1L).orElseThrow().isActive());
    }

    @Test
    @DisplayName("findById: devuelve vacio si no existe la marca")
    void findByIdReturnsEmptyWhenMissing() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(repository.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findAll: convierte todas las marcas al dominio")
    void findAllMapsToDomain() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                markEntity(1L, "Lenovo", true),
                markEntity(2L, "Asus", false)));

        List<Mark> all = repository.findAll();

        assertEquals(2, all.size());
        assertEquals("Lenovo", all.get(0).getName().value());
        assertFalse(all.get(1).isActive());
    }

    @Test
    @DisplayName("save: persiste la entidad y devuelve la marca convertida")
    void savePersistsAndMapsBack() {
        when(jpaRepository.save(any(MarkEntity.class)))
                .thenReturn(markEntity(1L, "Lenovo", true));

        Mark saved = repository.save(new Mark(1L, new com.mabc.e_shop.domain.valueobject.Name("Lenovo")));

        assertEquals(1L, saved.getId());
        assertEquals("Lenovo", saved.getName().value());
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
