package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.MarkRepository;

/**
 * Caso de uso que consulta una marca por su identificador.
 */
public class GetMarkByIdUseCase {

    private final MarkRepository markRepository;

    /**
     * Crea el caso de uso con el repositorio de marcas.
     *
     * @param markRepository repositorio de marcas.
     */
    public GetMarkByIdUseCase(MarkRepository markRepository) {
        this.markRepository = markRepository;
    }

    /**
     * Busca la marca correspondiente al identificador entregado.
     *
     * @param id identificador de la marca.
     * @return la marca encontrada.
     * @throws ResourceNotFoundException si la marca no existe.
     */
    public Mark execute(Long id) {
        return markRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La marca no existe."));
    }
}
