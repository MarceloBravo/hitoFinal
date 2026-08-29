package com.mabc.e_shop.domain.repository;

import com.mabc.e_shop.domain.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de categorías.
 *
 * <p>Define el contrato de persistencia para la entidad {@link Category},
 * permitiendo consultar, guardar, eliminar y verificar la existencia de
 * categorías.
 */
public interface CategoryRepository {

    /**
     * Busca una categoría por su identificador.
     *
     * @param id identificador de la categoría.
     * @return un {@link Optional} con la categoría encontrada o vacío si no existe.
     */
    Optional<Category> findById(Long id);

    /**
     * Obtiene todas las categorías registradas.
     *
     * @return lista de todas las categorías.
     */
    List<Category> findAll();

    /**
     * Obtiene las categorías cuyos identificadores están en la lista entregada.
     *
     * @param ids lista de identificadores a buscar.
     * @return lista de categorías encontradas.
     */
    List<Category> findAllByIds(List<Long> ids);

    /**
     * Guarda o actualiza una categoría.
     *
     * @param category categoría a persistir.
     * @return la categoría persistida.
     */
    Category save(Category category);

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id identificador de la categoría a eliminar.
     */
    void deleteById(Long id);

    /**
     * Indica si existe una categoría con el identificador entregado.
     *
     * @param id identificador de la categoría.
     * @return {@code true} si la categoría existe, {@code false} en caso contrario.
     */
    boolean existsById(Long id);
}
