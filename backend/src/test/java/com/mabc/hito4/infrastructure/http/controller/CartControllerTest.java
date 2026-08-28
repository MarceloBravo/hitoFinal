package com.mabc.hitoFinal.infrastructure.http.controller;

import com.mabc.hitoFinal.application.usecase.AddItemToCartUseCase;
import com.mabc.hitoFinal.application.usecase.CreateCartUseCase;
import com.mabc.hitoFinal.application.usecase.GetCartByIdUseCase;
import com.mabc.hitoFinal.domain.entity.Cart;
import com.mabc.hitoFinal.domain.entity.Category;
import com.mabc.hitoFinal.domain.entity.Mark;
import com.mabc.hitoFinal.domain.entity.Product;
import com.mabc.hitoFinal.domain.valueobject.Description;
import com.mabc.hitoFinal.domain.valueobject.Name;
import com.mabc.hitoFinal.domain.valueobject.Price;
import com.mabc.hitoFinal.domain.valueobject.Quantity;
import com.mabc.hitoFinal.domain.valueobject.Stock;
import com.mabc.hitoFinal.domain.valueobject.Weight;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCartUseCase createCartUseCase;

    @MockitoBean
    private AddItemToCartUseCase addItemToCartUseCase;

    @MockitoBean
    private GetCartByIdUseCase getCartByIdUseCase;

    @Test
    @DisplayName("GET busca un carrito por id y responde 200 con el formato estándar")
    void findsCartById() throws Exception {
        Cart cart = new Cart(7L);
        cart.addItem(buildProduct(), new Quantity(2));
        when(getCartByIdUseCase.execute(7L)).thenReturn(cart);

        mockMvc.perform(get("/api/v1/carts/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.subTotal").value(100.0));
    }

    @Test
    @DisplayName("GET de un carrito inexistente responde 404")
    void rejectsMissingCartAs404OnGet() throws Exception {
        when(getCartByIdUseCase.execute(99L))
                .thenThrow(new com.mabc.hitoFinal.domain.exception.ResourceNotFoundException("El carrito no existe o no es válido."));

        mockMvc.perform(get("/api/v1/carts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("El carrito no existe o no es válido."));
    }

    private Product buildProduct() {
        return new Product(
                3L, new Mark(1L, new Name("Lenovo")), java.util.List.of(new Category(2L, new Name("Gaming"))),
                new Name("Notebook"), new Description("Equipo portátil"),
                new Stock(10), new Weight(2.5), new Price(500.0), new Price(50.0));
    }

    @Test
    @DisplayName("POST crea un carrito vacío y responde 201 con el formato estándar")
    void createsCart() throws Exception {
        when(createCartUseCase.execute()).thenReturn(new Cart(7L));

        mockMvc.perform(post("/api/v1/carts"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Carrito creado correctamente."))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.items.length()").value(0))
                .andExpect(jsonPath("$.data.subTotal").value(0.0));
    }

    @Test
    @DisplayName("POST agrega un producto al carrito y responde 200 con subtotal actualizado")
    void addsItemToCart() throws Exception {
        Cart cart = new Cart(7L);
        cart.addItem(buildProduct(), new Quantity(2));
        when(addItemToCartUseCase.execute(7L, 3L, 2)).thenReturn(cart);

        mockMvc.perform(post("/api/v1/carts/7/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":3,\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Producto agregado al carrito correctamente."))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].productId").value(3))
                .andExpect(jsonPath("$.data.items[0].productName").value("Notebook"))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[0].subTotal").value(100.0))
                .andExpect(jsonPath("$.data.subTotal").value(100.0));
    }

    @Test
    @DisplayName("Responde 400 sin invocar el caso de uso cuando la cantidad es inválida")
    void rejectsInvalidQuantity() throws Exception {
        mockMvc.perform(post("/api/v1/carts/7/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":3,\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message", containsString("quantity")));

        verifyNoInteractions(addItemToCartUseCase, createCartUseCase);
    }

    @Test
    @DisplayName("Propaga el stock insuficiente como 409 en el formato estándar")
    void propagatesInsufficientStockAs409() throws Exception {
        when(addItemToCartUseCase.execute(7L, 3L, 99))
                .thenThrow(new IllegalStateException("Stock insuficiente para el producto Notebook"));

        mockMvc.perform(post("/api/v1/carts/7/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":3,\"quantity\":99}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409))
                .andExpect(jsonPath("$.message", containsString("Stock insuficiente")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Propaga el carrito inexistente como 404 en el formato estándar")
    void propagatesMissingCartAs404() throws Exception {
        when(addItemToCartUseCase.execute(99L, 3L, 1))
                .thenThrow(new IllegalArgumentException("El carrito no existe o no es válido."));

        mockMvc.perform(post("/api/v1/carts/99/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":3,\"quantity\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("El carrito no existe o no es válido."));
    }
}
