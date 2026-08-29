package com.mabc.e_shop.infrastructure.http.mapper;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.infrastructure.http.dto.MarkResponseDto;

/**
 * Mapper que convierte la entidad de dominio {@link Mark} en su DTO de
 * respuesta HTTP {@link MarkResponseDto}.
 *
 * <p>Clase utilitaria con métodos estáticos, no instanciable.
 */
public final class MarkHttpMapper {

    private MarkHttpMapper() {
    }

    /**
     * Convierte una marca de dominio en su DTO de respuesta HTTP.
     *
     * @param mark marca de dominio a convertir.
     * @return el DTO de respuesta resultante.
     */
    public static MarkResponseDto toResponse(Mark mark) {
        return new MarkResponseDto(mark.getId(), mark.getName().value(), mark.isActive());
    }
}
