package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.infrastructure.persistence.entity.MarkEntity;
import com.mabc.e_shop.infrastructure.persistence.repositories.MarkJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de {@link MarkRepository} basada en JPA/Spring Data.
 *
 * <p>Convierte entre las entidades de dominio {@link Mark} y las entidades
 * de persistencia {@link MarkEntity} delegando el acceso a la base de datos
 * en {@link MarkJpaRepository}.
 */
@Repository
public class JpaMarkRepository implements MarkRepository {

    private final MarkJpaRepository markJpaRepository;

    /**
     * Crea el repositorio JPA de marcas.
     *
     * @param markJpaRepository repositorio Spring Data de entidades de marca.
     */
    public JpaMarkRepository(MarkJpaRepository markJpaRepository) {
        this.markJpaRepository = markJpaRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Mark> findById(Long id) {
        return markJpaRepository.findById(id).map(this::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Mark> findAll() {
        return markJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Mark save(Mark mark) {
        MarkEntity saved = markJpaRepository.save(toEntity(mark));
        return toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        markJpaRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Long id) {
        return markJpaRepository.existsById(id);
    }

    private Mark toDomain(MarkEntity entity) {
        Mark mark = new Mark(entity.getId(), new Name(entity.getName()));
        if (Boolean.TRUE.equals(entity.getActive())) {
            mark.activate();
        } else {
            mark.deactivate();
        }
        return mark;
    }

    private MarkEntity toEntity(Mark mark) {
        return new MarkEntity(mark.getId(), mark.getName().value(), mark.isActive());
    }
}
