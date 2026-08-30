package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;
import com.mabc.e_shop.infrastructure.persistence.entity.CategoryEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.MarkEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.ProductEntity;

import java.util.List;

/**
 * Mapper que convierte entre la entidad de dominio {@link Product} y la
 * entidad de persistencia {@link ProductEntity}.
 *
 * <p>Clase utilitaria con métodos estáticos, no instanciable.
 */
public final class ProductEntityMapper {

    private ProductEntityMapper() {
    }

    /**
     * Convierte una entidad de persistencia en una entidad de dominio.
     *
     * @param entity entidad JPA de producto a convertir.
     * @return el producto de dominio resultante.
     */
    public static Product toDomain(ProductEntity entity) {
        MarkEntity markEntity = entity.getMark();
        Mark mark = new Mark(markEntity.getId(), new Name(markEntity.getName()));
        if (Boolean.TRUE.equals(markEntity.getActive())) {
            mark.activate();
        } else {
            mark.deactivate();
        }

        List<Category> categories = entity.getCategories() == null ? List.of()
                : entity.getCategories().stream()
                        .map(ProductEntityMapper::toDomainCategory)
                        .toList();

        return new Product(
                entity.getId(),
                mark,
                categories,
                new Name(entity.getName()),
                new Description(entity.getDescription()),
                new Stock(entity.getStock()),
                new Weight(entity.getWeight()),
                new Price(entity.getPriceCost()),
                new Price(entity.getPriceSale()),
                new ImagePath(entity.getImagePath()));
    }

    /**
     * Convierte una entidad de dominio en una entidad de persistencia.
     *
     * @param product producto de dominio a convertir.
     * @return la entidad JPA de producto resultante.
     */
    public static ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName().value());
        entity.setDescription(product.getDescription().value());
        entity.setStock(product.getStock().value());
        entity.setWeight(product.getWeight().value());
        entity.setPriceCost(product.getPriceCost().value());
        entity.setPriceSale(product.getPriceSale().value());
        entity.setImagePath(product.getImagePath() == null ? null : product.getImagePath().value());

        entity.setMark(new MarkEntity(
                product.getMark().getId(),
                product.getMark().getName().value(),
                product.getMark().isActive()));

        List<CategoryEntity> categories = product.getCategories().stream()
                .map(category -> new CategoryEntity(
                        category.getId(),
                        category.getName().value(),
                        category.isActive()))
                .toList();
        entity.setCategories(categories);
        return entity;
    }

    /**
     * Convierte una entidad JPA de categoría en una categoría de dominio.
     *
     * @param entity entidad JPA de categoría a convertir.
     * @return la categoría de dominio resultante.
     */
    private static Category toDomainCategory(CategoryEntity entity) {
        Category category = new Category(entity.getId(), new Name(entity.getName()));
        if (Boolean.TRUE.equals(entity.getActive())) {
            category.activate();
        } else {
            category.deactivate();
        }
        return category;
    }
}
