package com.mabc.e_shop.domain.repository;

import com.mabc.e_shop.domain.entity.Mark;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de marcas.
 *
 * <p>Define el contrato de persistencia para la entidad {@link Mark},
 * permitiendo consultar, guardar, eliminar y verificar la existencia de
 * marcas.
 */
public interface MarkRepository {

    /**
     * Busca una marca por su identificador.
     *
     * @param id identificador de la marca.
     * @return un {@link Optional} con la marca encontrada o vacío si no existe.
     */
    Optional<Mark> findById(Long id);

    /**
     * Obtiene todas las marcas registradas.
     *
     * @return lista de todas las marcas.
     */
    List<Mark> findAll();

    /**
     * Guarda o actualiza una marca.
     *
     * @param mark marca a persistir.
     * @return la marca persistida.
     */
    Mark save(Mark mark);

    /**
     * Elimina una marca por su identificador.
     *
     * @param id identificador de la marca a eliminar.
     */
    void deleteById(Long id);

    /**
     * Indica si existe una marca con el identificador entregado.
     *
     * @param id identificador de la marca.
     * @return {@code true} si la marca existe, {@code false} en caso contrario.
     */
    boolean existsById(Long id);
}
