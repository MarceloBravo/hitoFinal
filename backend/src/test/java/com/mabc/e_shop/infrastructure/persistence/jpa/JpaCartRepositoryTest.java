package com.mabc.e_shop.infrastructure.persistence.jpa;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.infrastructure.persistence.entity.CartEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.CartItemEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.CategoryEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.MarkEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.ProductEntity;
import com.mabc.e_shop.infrastructure.persistence.repositories.CartJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaCartRepositoryTest {

    private final CartJpaRepository jpaRepository = mock(CartJpaRepository.class);
    private final JpaCartRepository repository = new JpaCartRepository(jpaRepository);

    private ProductEntity productEntity() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setMark(new MarkEntity(1L, "Lenovo", true));
        product.setCategories(List.of(new CategoryEntity(1L, "Computacion", true)));
        product.setName("Notebook Lenovo");
        product.setDescription("Notebook Lenovo IdeaPad 310");
        product.setStock(12);
        product.setWeight(1500);
        product.setPriceCost(650000);
        product.setPriceSale(800000);
        product.setImagePath("https://images.example.com/products/notebook.png");
        return product;
    }

    private CartEntity cartEntity() {
        CartEntity cart = new CartEntity();
        cart.setId(1L);
        cart.setCreationDate(LocalDateTime.of(2026, 8, 11, 10, 30));
        cart.setSubTotal(1600000.0);

        CartItemEntity item = new CartItemEntity();
        item.setId(1L);
        item.setCart(cart);
        item.setProduct(productEntity());
        item.setCant(2);
        item.setSubTotal(1600000.0);

        cart.setItems(List.of(item));
        return cart;
    }

    @Test
    @DisplayName("findById: convierte la entidad persistida en un carrito de dominio")
    void findByIdMapsToDomain() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(cartEntity()));

        Optional<Cart> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1, result.get().getItems().size());
        assertEquals("Notebook Lenovo", result.get().getItems().get(0).getProduct().getName().value());
        assertEquals(2, result.get().getItems().get(0).getQuantity().value());
        assertEquals(1600000.0, result.get().getSubTotal());
    }

    @Test
    @DisplayName("findById: devuelve vacio si no existe el carrito")
    void findByIdReturnsEmptyWhenMissing() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(repository.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findLast: consulta el ultimo carrito por id descendente")
    void findLastMapsToDomain() {
        when(jpaRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(cartEntity()));

        Optional<Cart> result = repository.findLast();

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("findLast: devuelve vacio cuando no hay carritos")
    void findLastReturnsEmptyWhenMissing() {
        when(jpaRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());

        assertTrue(repository.findLast().isEmpty());
    }

    @Test
    @DisplayName("save: persiste y devuelve el carrito convertido a dominio")
    void savePersistsAndMapsBack() {
        when(jpaRepository.save(any(CartEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = new Cart(1L);
        cart.addItem(ProductEntityMapper.toDomain(productEntity()),
                new com.mabc.e_shop.domain.valueobject.Quantity(2));

        Cart saved = repository.save(cart);

        assertEquals(1L, saved.getId());
        assertEquals(1, saved.getItems().size());
        assertEquals(1600000.0, saved.getSubTotal());
    }

    @Test
    @DisplayName("deleteById: delega la eliminacion en el repositorio Spring Data")
    void deleteByIdDelegates() {
        repository.deleteById(1L);

        verify(jpaRepository).deleteById(1L);
    }
}
