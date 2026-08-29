package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.repository.MarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetAllMarksUseCaseTest {

    private MarkRepository markRepository;
    private GetAllMarksUseCase useCase;

    @BeforeEach
    void setUp() {
        markRepository = mock(MarkRepository.class);
        useCase = new GetAllMarksUseCase(markRepository);
    }

    @Test
    @DisplayName("Retorna todas las marcas del repositorio")
    void returnsAllMarks() {
        List<Mark> marks = List.of();
        when(markRepository.findAll()).thenReturn(marks);

        assertEquals(marks, useCase.execute());
        verify(markRepository).findAll();
    }
}
