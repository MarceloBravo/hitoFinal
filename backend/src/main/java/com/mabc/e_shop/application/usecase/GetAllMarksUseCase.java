package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.repository.MarkRepository;

import java.util.List;

/**
 * Caso de uso que consulta todas las marcas registradas.
 */
public class GetAllMarksUseCase {

    private final MarkRepository markRepository;

    /**
     * Crea el caso de uso con el repositorio de marcas.
     *
     * @param markRepository repositorio de marcas.
     */
    public GetAllMarksUseCase(MarkRepository markRepository) {
        this.markRepository = markRepository;
    }

    /**
     * Obtiene todas las marcas registradas.
     *
     * @return lista con todas las marcas; vacía si no hay registros.
     */
    public List<Mark> execute() {
        return markRepository.findAll();
    }
}
