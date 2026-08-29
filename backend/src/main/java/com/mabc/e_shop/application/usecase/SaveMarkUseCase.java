package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.valueobject.Name;

/**
 * Caso de uso que crea o actualiza una marca.
 *
 * <p>Si se entrega un {@code id} nulo se crea una marca nueva; en caso
 * contrario se actualiza la marca existente. También permite activarla o
 * desactivarla según el estado recibido.
 */
public class SaveMarkUseCase {

    private final MarkRepository markRepository;

    /**
     * Crea el caso de uso con el repositorio de marcas.
     *
     * @param markRepository repositorio de marcas.
     */
    public SaveMarkUseCase(MarkRepository markRepository) {
        this.markRepository = markRepository;
    }

    /**
     * Crea o actualiza una marca y la persiste.
     *
     * @param id     identificador de la marca; si es {@code null} se crea una nueva.
     * @param name   nombre de la marca.
     * @param active {@code true} para activar la marca, {@code false} para desactivarla.
     * @return la marca creada o actualizada.
     * @throws IllegalArgumentException si se entrega un {@code id} y la marca no existe.
     */
    public Mark execute(Long id, String name, boolean active) {
        Name markName = new Name(name);

        Mark mark;
        if (id == null) {
            mark = new Mark(null, markName);
        } else {
            mark = markRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("La marca no existe."));
            mark.rename(markName);
        }

        if (active) {
            mark.activate();
        } else {
            mark.deactivate();
        }

        return markRepository.save(mark);
    }
}
