package com.mabc.hitoFinal.infrastructure.http.controller;

import com.mabc.hitoFinal.application.usecase.CreateProductUseCase;
import com.mabc.hitoFinal.application.usecase.GetAllProductsUseCase;
import com.mabc.hitoFinal.application.usecase.GetProductByIdUseCase;
import com.mabc.hitoFinal.domain.entity.Category;
import com.mabc.hitoFinal.domain.entity.Mark;
import com.mabc.hitoFinal.domain.entity.Product;
import com.mabc.hitoFinal.domain.valueobject.Description;
import com.mabc.hitoFinal.domain.valueobject.Name;
import com.mabc.hitoFinal.domain.valueobject.Price;
import com.mabc.hitoFinal.domain.valueobject.Stock;
import com.mabc.hitoFinal.domain.valueobject.Weight;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final String PRODUCT_JSON = """
            {
              "markId": 1,
              "categoryIds": [2, 3],
              "name": "Notebook",
              "description": "Equipo portátil",
              "stock": 10,
              "weight": 2.5,
              "priceCost": 500.0,
              "priceSale": 700.0
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private GetAllProductsUseCase getAllProductsUseCase;

    @MockitoBean
    private GetProductByIdUseCase getProductByIdUseCase;

    private Product buildProduct(Long id) {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        List<Category> categories = List.of(
                new Category(2L, new Name("Gaming")),
                new Category(3L, new Name("Oficina")));
        return new Product(
                id, mark, categories, new Name("Notebook"), new Description("Equipo portátil"),
                new Stock(10), new Weight(2.5), new Price(500.0), new Price(700.0));
    }

    @Test
    @DisplayName("GET lista todos los productos y responde 200 con el formato estándar")
    void findsAllProducts() throws Exception {
        when(getAllProductsUseCase.execute()).thenReturn(List.of(buildProduct(5L)));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(5))
                .andExpect(jsonPath("$.data[0].name").value("Notebook"));
    }

    @Test
    @DisplayName("GET busca un producto por id y responde 200")
    void findsProductById() throws Exception {
        when(getProductByIdUseCase.execute(5L)).thenReturn(buildProduct(5L));

        mockMvc.perform(get("/api/v1/products/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.markName").value("Lenovo"));
    }

    @Test
    @DisplayName("GET de un producto inexistente responde 404")
    void rejectsMissingProductAs404() throws Exception {
        when(getProductByIdUseCase.execute(99L))
                .thenThrow(new com.mabc.hitoFinal.domain.exception.ResourceNotFoundException("El producto no existe."));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("El producto no existe."));
    }

    @Test
    @DisplayName("POST crea un producto y responde 201 con el formato estándar")
    void createsProduct() throws Exception {
        when(createProductUseCase.execute(isNull(), eq(1L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0))).thenReturn(buildProduct(5L));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Producto creado correctamente."))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.markId").value(1))
                .andExpect(jsonPath("$.data.markName").value("Lenovo"))
                .andExpect(jsonPath("$.data.categoryIds.length()").value(2))
                .andExpect(jsonPath("$.data.name").value("Notebook"))
                .andExpect(jsonPath("$.data.priceSale").value(700.0));
    }

    @Test
    @DisplayName("PUT actualiza un producto existente y responde 200")
    void updatesProduct() throws Exception {
        when(createProductUseCase.execute(eq(5L), eq(1L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0))).thenReturn(buildProduct(5L));

        mockMvc.perform(put("/api/v1/products/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Producto actualizado correctamente."))
                .andExpect(jsonPath("$.data.id").value(5));
    }

    @Test
    @DisplayName("Responde 400 sin invocar el caso de uso cuando el DTO es inválido")
    void rejectsInvalidPayload() throws Exception {
        String invalidJson = """
                {
                  "markId": 1,
                  "categoryIds": [],
                  "name": "",
                  "description": "Equipo portátil",
                  "stock": -1,
                  "weight": 2.5,
                  "priceCost": 500.0,
                  "priceSale": 700.0
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message", containsString("categoryIds")));

        verifyNoInteractions(createProductUseCase);
    }

    @Test
    @DisplayName("Propaga la marca inexistente como 404 en el formato estándar")
    void propagatesMissingMarkAs404() throws Exception {
        when(createProductUseCase.execute(isNull(), eq(99L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0)))
                .thenThrow(new IllegalArgumentException("La marca no existe."));

        String jsonWithMissingMark = PRODUCT_JSON.replace("\"markId\": 1", "\"markId\": 99");

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithMissingMark))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("La marca no existe."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
