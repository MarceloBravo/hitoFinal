package com.mabc.e_shop.infrastructure.security;

import com.jayway.jsonpath.JsonPath;
import com.mabc.e_shop.infrastructure.persistence.entity.CategoryEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.MarkEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.ProductEntity;
import com.mabc.e_shop.infrastructure.persistence.entity.User;
import com.mabc.e_shop.infrastructure.persistence.repositories.CategoryJpaRepository;
import com.mabc.e_shop.infrastructure.persistence.repositories.MarkJpaRepository;
import com.mabc.e_shop.infrastructure.persistence.repositories.ProductJpaRepository;
import com.mabc.e_shop.infrastructure.persistence.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductUpdateSecurityIntegrationTest {

    private static final String CLIENT_EMAIL = "cliente@tienda.cl";
    private static final String PASSWORD = "secreta123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private MarkJpaRepository markRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long markId;
    private Long categoryId;
    private Long productId;

    @BeforeEach
    void seed() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        markRepository.deleteAll();

        userRepository.save(new User(null, "Cliente", CLIENT_EMAIL, passwordEncoder.encode(PASSWORD), "USER", true));
        MarkEntity mark = markRepository.save(new MarkEntity(null, "Lenovo", true));
        CategoryEntity category = categoryRepository.save(new CategoryEntity(null, "Computacion", true));
        ProductEntity product = new ProductEntity();
        product.setMark(mark);
        product.setCategories(new java.util.ArrayList<>(List.of(category)));
        product.setName("Notebook");
        product.setDescription("Portatil");
        product.setStock(10);
        product.setWeight(2.5);
        product.setPriceCost(500.0);
        product.setPriceSale(700.0);
        product.setImagePath("https://images.example.com/products/notebook.png");
        product = productRepository.save(product);

        markId = mark.getId();
        categoryId = category.getId();
        productId = product.getId();
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} sin token responde 401")
    void updateWithoutTokenReturns401() throws Exception {
        mockMvc.perform(multipart(org.springframework.http.HttpMethod.PUT, "/api/v1/products/" + productId)
                .param("markId", String.valueOf(markId))
                .param("categoryIds", String.valueOf(categoryId))
                .param("name", "Notebook")
                .param("description", "Actualizado")
                .param("stock", "8")
                .param("weight", "2.4")
                .param("priceCost", "550.0")
                .param("priceSale", "790.0")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} con access token válido responde 200")
    void updateWithValidTokenReturns200() throws Exception {
        String token = loginAccessToken();

        mockMvc.perform(multipart(org.springframework.http.HttpMethod.PUT, "/api/v1/products/" + productId)
                .header("Authorization", "Bearer " + token)
                .param("markId", String.valueOf(markId))
                .param("categoryIds", String.valueOf(categoryId))
                .param("name", "Notebook")
                .param("description", "Actualizado")
                .param("stock", "8")
                .param("weight", "2.4")
                .param("priceCost", "550.0")
                .param("priceSale", "790.0")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    private String loginAccessToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s"}
                                """.formatted(CLIENT_EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.data.accessToken");
    }
}
