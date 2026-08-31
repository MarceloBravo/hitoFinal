package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.repository.CartRepository;
import com.mabc.e_shop.domain.valueobject.Quantity;
import com.mabc.e_shop.infrastructure.persistence.entity.CartEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.CartItemEntity;
import com.mabc.e_shop.infrastructure.persistence.repositories.CartJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Implementación de {@link CartRepository} basada en JPA/Spring Data.
 *
 * <p>
 * Convierte entre las entidades de dominio {@link Cart} y las entidades
 * de persistencia {@link CartEntity} delegando el acceso a la base de datos
 * en {@link CartJpaRepository}.
 */
@Repository
public class JpaCartRepository implements CartRepository {

    private final CartJpaRepository cartJpaRepository;

    /**
     * Crea el repositorio JPA de carritos.
     *
     * @param cartJpaRepository repositorio Spring Data de entidades de carrito.
     */
    public JpaCartRepository(CartJpaRepository cartJpaRepository) {
        this.cartJpaRepository = cartJpaRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findById(Long id) {
        return cartJpaRepository.findById(id).map(this::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cart> findLast() {
        return cartJpaRepository.findTopByOrderByIdDesc().map(this::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Cart save(Cart cart) {
        CartEntity saved = cartJpaRepository.save(toEntity(cart));
        return toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        cartJpaRepository.deleteById(id);
    }

    private Cart toDomain(CartEntity entity) {
        Cart cart = new Cart(entity.getId());
        if (entity.getItems() != null) {
            for (CartItemEntity itemEntity : entity.getItems()) {
                // Se pasa el ID real de la BD para no confundir a Hibernate en el merge()
                cart.addItemWithId(
                        itemEntity.getId(),
                        ProductEntityMapper.toDomain(itemEntity.getProduct()),
                        new Quantity(itemEntity.getCant()));
            }
        }
        return cart;
    }

    private CartEntity toEntity(Cart cart) {
        CartEntity entity = (cart.getId() == null) ? new CartEntity()
                : cartJpaRepository.findById(cart.getId()).orElseGet(CartEntity::new);
        entity.setId(cart.getId());
        entity.setCreationDate(cart.getCreationDate());
        entity.setSubTotal(cart.getSubTotal());

        // Construir la lista nueva reutilizando los CartItemEntity ya rastreados por Hibernate
        List<CartItemEntity> newItems = cart.getItems().stream()
                .map(item -> {
                    CartItemEntity itemEntity = entity.getItems().stream()
                            .filter(itemE -> Objects.equals(itemE.getId(), item.getId()))
                            .findFirst()
                            .orElse(new CartItemEntity()); // id==null → INSERT; id real → UPDATE
                    itemEntity.setId(item.getId());
                    itemEntity.setCart(entity);
                    itemEntity.setProduct(ProductEntityMapper.toEntity(item.getProduct()));
                    itemEntity.setCant(item.getQuantity().value());
                    itemEntity.setSubTotal(item.getSubTotal());
                    return itemEntity;
                })
                .toList();
        // Con orphanRemoval=true, Hibernate rastrea la referencia original de la colección.
        // NO se puede reemplazar con setItems() (nueva instancia), hay que modificar in-place.
        entity.getItems().clear();
        entity.getItems().addAll(newItems);
        return entity;
    }
}
