package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.exception.InvalidNameException;
import com.mabc.e_shop.domain.repository.MarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class SaveMarkUseCaseTest {

    private List<Mark> storedMarks;
    private SaveMarkUseCase useCase;

    @BeforeEach
    void setUp() {
        storedMarks = new ArrayList<>();
        MarkRepository markRepository = mock(MarkRepository.class);
        when(markRepository.findAll()).thenAnswer(invocation -> List.copyOf(storedMarks));
        when(markRepository.findById(any())).thenAnswer(invocation -> storedMarks.stream()
                .filter(mark -> mark.getId().equals(invocation.getArgument(0)))
                .findFirst());
        AtomicLong seq = new AtomicLong(0);
        when(markRepository.save(any(Mark.class))).thenAnswer(invocation -> {
            Mark mark = invocation.getArgument(0);
            if(mark.getId() == null){
                mark = new Mark(seq.incrementAndGet(), mark.getName());
            }
            storedMarks.add(mark);
            return mark;
        });
        useCase = new SaveMarkUseCase(markRepository);
    }

    @Test
    @DisplayName("Crea una marca nueva activa")
    void createsMark() {
        Mark mark = useCase.execute(null, "Lenovo", true);

        //assertEquals(1L, created.getId());
        assertEquals("Lenovo", mark.getName().value());
        assertTrue(mark.isActive());
    }

    @Test
    @DisplayName("Actualiza una marca existente")
    void updatesMark() {
        Mark created = useCase.execute(null, "Lenovo", true);

        Mark updated = useCase.execute(created.getId(), "Asus", false);

        assertEquals(created.getId(), updated.getId());
        assertEquals("Asus", updated.getName().value());
        assertFalse(updated.isActive());
    }

    @Test
    @DisplayName("Rechaza nombre nulo o vacio")
    void rejectsBlankName() {
        assertThrows(InvalidNameException.class, () -> useCase.execute(null, "", true));
    }

    @Test
    @DisplayName("Lanza excepcion si se actualiza una marca inexistente")
    void rejectsUpdatingMissingMark() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L, "Asus", true));
    }

    @Test
    @DisplayName("Activa y desactiva la marca segun el estado recibido")
    void togglesActiveState() {
        Mark created = useCase.execute(null, "Lenovo", false);
        assertTrue(created.isActive());

        Mark reactivated = useCase.execute(created.getId(), "Lenovo", false);
        assertFalse(reactivated.isActive());
    }
}
