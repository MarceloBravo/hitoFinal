package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.infrastructure.persistence.entity.ProductEntity;
import com.mabc.e_shop.infrastructure.persistence.repositories.ProductJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de {@link ProductRepository} basada en JPA/Spring Data.
 *
 * <p>Convierte entre las entidades de dominio {@link Product} y las
 * entidades de persistencia {@link ProductEntity} delegando el acceso a la
 * base de datos en {@link ProductJpaRepository}.
 */
@Repository
public class JpaProductRepository implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    /**
     * Crea el repositorio JPA de productos.
     *
     * @param productJpaRepository repositorio Spring Data de entidades de producto.
     */
    public JpaProductRepository(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(ProductEntityMapper::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream().map(ProductEntityMapper::toDomain).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> pageResult = productJpaRepository.findAll(pageable);
        List<Product> content = pageResult.getContent().stream()
                .map(ProductEntityMapper::toDomain)
                .toList();
        return new PageResult(content, pageResult.getTotalElements());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult findAll(int page, int size, Long categoryId, Long markId, Double minPrice, Double maxPrice) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> pageResult = productJpaRepository.findFiltered(categoryId, markId, minPrice, maxPrice, pageable);
        List<Product> content = pageResult.getContent().stream()
                .map(ProductEntityMapper::toDomain)
                .toList();
        return new PageResult(content, pageResult.getTotalElements());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult findAll(
            int page, int size, Long categoryId, Long markId, Double minPrice, Double maxPrice, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> pageResult =
                productJpaRepository.findFilteredWithSearch(categoryId, markId, minPrice, maxPrice, search, pageable);
        List<Product> content = pageResult.getContent().stream()
                .map(ProductEntityMapper::toDomain)
                .toList();
        return new PageResult(content, pageResult.getTotalElements());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Product save(Product product) {
        ProductEntity saved = productJpaRepository.save(ProductEntityMapper.toEntity(product));
        return ProductEntityMapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(Long id) {
        return productJpaRepository.existsById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsProductWithCategory(Long categoryId) {
        return productJpaRepository.countProductsByCategoryId(categoryId) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsProductWithMark(Long markId) {
        return productJpaRepository.countProductsByMarkId(markId) > 0;
    }
}
