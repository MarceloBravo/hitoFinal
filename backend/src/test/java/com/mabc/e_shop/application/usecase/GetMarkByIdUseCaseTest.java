package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.valueobject.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetMarkByIdUseCaseTest {

    private MarkRepository markRepository;
    private GetMarkByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        markRepository = mock(MarkRepository.class);
        useCase = new GetMarkByIdUseCase(markRepository);
    }

    @Test
    @DisplayName("Retorna la marca correspondiente al identificador")
    void returnsMarkById() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        when(markRepository.findById(1L)).thenReturn(Optional.of(mark));

        assertEquals(mark, useCase.execute(1L));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando la marca no existe")
    void rejectsWhenMarkNotExists() {
        when(markRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
    }
}
