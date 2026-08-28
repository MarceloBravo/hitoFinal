package com.mabc.hitoFinal.application.usecase;

import com.mabc.hitoFinal.domain.entity.Cart;
import com.mabc.hitoFinal.domain.entity.Category;
import com.mabc.hitoFinal.domain.entity.Mark;
import com.mabc.hitoFinal.domain.entity.Product;
import com.mabc.hitoFinal.domain.exception.ResourceNotFoundException;
import com.mabc.hitoFinal.domain.repository.CartRepository;
import com.mabc.hitoFinal.domain.valueobject.Description;
import com.mabc.hitoFinal.domain.valueobject.Name;
import com.mabc.hitoFinal.domain.valueobject.Price;
import com.mabc.hitoFinal.domain.valueobject.Quantity;
import com.mabc.hitoFinal.domain.valueobject.Stock;
import com.mabc.hitoFinal.domain.valueobject.Weight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetCartByIdUseCaseTest {

    private CartRepository cartRepository;
    private GetCartByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        useCase = new GetCartByIdUseCase(cartRepository);
    }

    @Test
    @DisplayName("Retorna el carrito correspondiente al identificador")
    void returnsCartById() {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        Product product = new Product(3L, mark, List.of(new Category(2L, new Name("Gaming"))),
                new Name("Notebook"), new Description("Equipo portátil"),
                new Stock(10), new Weight(2.5), new Price(500.0), new Price(50.0));
        Cart cart = new Cart(7L);
        cart.addItem(product, new Quantity(2));
        when(cartRepository.findById(7L)).thenReturn(Optional.of(cart));

        assertEquals(cart, useCase.execute(7L));
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el carrito no existe")
    void rejectsWhenCartNotExists() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(99L));
    }
}
