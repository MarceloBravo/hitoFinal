package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.infrastructure.persistence.entity.CategoryEntity;
import com.mabc.e_shop.infrastructure.persistence.repositories.CategoryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de {@link CategoryRepository} basada en JPA/Spring Data.
 *
 * <p>Convierte entre las entidades de dominio {@link Category} y las
 * entidades de persistencia {@link CategoryEntity} delegando el acceso a la
 * base de datos en {@link CategoryJpaRepository}.
 */
@Repository
public class JpaCategoryRepository implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    /**
     * Crea el repositorio JPA de categorías.
     *
     * @param categoryJpaRepository repositorio Spring Data de entidades de categoría.
     */
    public JpaCategoryRepository(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id).map(this::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Category> findAllByIds(List<Long> ids) {
        return categoryJpaRepository.findAllById(ids).stream().map(this::toDomain).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Category save(Category category) {
        CategoryEntity saved = categoryJpaRepository.save(toEntity(category));
        return toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        categoryJpaRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Long id) {
        return categoryJpaRepository.existsById(id);
    }

    private Category toDomain(CategoryEntity entity) {
        Category category = new Category(entity.getId(), new Name(entity.getName()));
        if (Boolean.TRUE.equals(entity.getActive())) {
            category.activate();
        } else {
            category.deactivate();
        }
        return category;
    }

    private CategoryEntity toEntity(Category category) {
        return new CategoryEntity(category.getId(), category.getName().value(), category.isActive());
    }
}
