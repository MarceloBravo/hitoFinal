package com.mabc.e_shop.infrastructure.http.mapper;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.infrastructure.http.dto.MarkResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkHttpMapperTest {

    @Test
    @DisplayName("Convierte una marca activa a su DTO de respuesta")
    void mapsActiveMark() {
        Mark mark = new Mark(1L, new Name("Lenovo"));

        MarkResponseDto response = MarkHttpMapper.toResponse(mark);

        assertEquals(1L, response.id());
        assertEquals("Lenovo", response.name());
        assertTrue(response.active());
    }

    @Test
    @DisplayName("Conserva el estado de desactivación de la marca")
    void mapsInactiveMark() {
        Mark mark = new Mark(2L, new Name("Asus"));
        mark.deactivate();

        assertFalse(MarkHttpMapper.toResponse(mark).active());
    }
}
