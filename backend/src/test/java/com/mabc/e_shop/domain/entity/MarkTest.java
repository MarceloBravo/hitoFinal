package com.mabc.e_shop.domain.entity;

import com.mabc.e_shop.domain.valueobject.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkTest {

    @Test
    @DisplayName("Marca nueva inicia activa")
    void newMarkStartsActive() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        assertTrue(mark.isActive());
    }

    @Test
    @DisplayName("deactivate y activate cambian el estado")
    void deactivateAndActivate() {
        Mark mark = new Mark(1L, new Name("Lenovo"));

        mark.deactivate();
        assertFalse(mark.isActive());

        mark.activate();
        assertTrue(mark.isActive());
    }

    @Test
    @DisplayName("rename actualiza el nombre manteniendo el id")
    void renameKeepsId() {
        Mark mark = new Mark(1L, new Name("Lenovo"));

        mark.rename(new Name("Asus"));

        assertEquals(1L, mark.getId());
        assertEquals("Asus", mark.getName().value());
    }
}
