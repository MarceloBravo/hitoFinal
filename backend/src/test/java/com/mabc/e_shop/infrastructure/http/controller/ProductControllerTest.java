package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.CreateProductUseCase;
import com.mabc.e_shop.application.usecase.DeleteProductUseCase;
import com.mabc.e_shop.application.usecase.GetAllProductsUseCase;
import com.mabc.e_shop.application.usecase.GetProductByIdUseCase;
import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;
import com.mabc.e_shop.infrastructure.storage.ImageStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    private static final Path STORED_IMAGE = Path.of("C:/uploads/uuid.png");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private GetAllProductsUseCase getAllProductsUseCase;

    @MockitoBean
    private GetProductByIdUseCase getProductByIdUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockitoBean
    private ImageStorage imageStorage;

    private Product buildProduct(Long id) {
        Mark mark = new Mark(1L, new Name("Lenovo"));
        List<Category> categories = List.of(
                new Category(2L, new Name("Gaming")),
                new Category(3L, new Name("Oficina")));
        return new Product(
                id, mark, categories, new Name("Notebook"), new Description("Equipo portátil"),
                new Stock(10), new Weight(2.5), new Price(500.0), new Price(700.0),
                new ImagePath("https://images.example.com/products/notebook.png"));
    }

    private Product buildProductWithoutImage(Long id) {
        return new Product(id, new Mark(1L, new Name("Lenovo")),
                List.of(new Category(2L, new Name("Gaming"))),
                new Name("Notebook"), new Description("Equipo portátil"),
                new Stock(10), new Weight(2.5), new Price(500.0), new Price(700.0), null);
    }

    private MockMultipartFile image(String name) {
        return new MockMultipartFile("image", name, "image/png", new byte[]{1, 2, 3});
    }

    private static MockMultipartHttpServletRequestBuilder validMultipartPost() {
        return multipart("/api/v1/products")
                .file(new MockMultipartFile("image", "foto.png", "image/png", new byte[]{1, 2, 3}))
                .param("markId", "1")
                .param("categoryIds", "2", "3")
                .param("name", "Notebook")
                .param("description", "Equipo portátil")
                .param("stock", "10")
                .param("weight", "2.5")
                .param("priceCost", "500.0")
                .param("priceSale", "700.0");
    }

    @Test
    @DisplayName("GET lista productos paginados y responde 200 con el formato esperado")
    void findsAllProducts() throws Exception {
        when(getAllProductsUseCase.execute(0, 10, null, null, null, null, null)).thenReturn(
                new ProductRepository.PageResult(List.of(buildProduct(5L)), 1));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.skip").value(0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].id").value(5))
                .andExpect(jsonPath("$.products[0].name").value("Notebook"));

        verify(getAllProductsUseCase).execute(0, 10, null, null, null, null, null);
    }

    @Test
    @DisplayName("GET convierte page 1-indexado a base 0 y calcula skip")
    void findsAllProductsWithPaginationParams() throws Exception {
        when(getAllProductsUseCase.execute(2, 10, null, null, null, null, null)).thenReturn(
                new ProductRepository.PageResult(List.of(buildProduct(25L)), 50));

        mockMvc.perform(get("/api/v1/products?limit=10&page=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.skip").value(20))
                .andExpect(jsonPath("$.total").value(50))
                .andExpect(jsonPath("$.products[0].id").value(25));

        verify(getAllProductsUseCase).execute(2, 10, null, null, null, null, null);
    }

    @Test
    @DisplayName("GET filtra productos por categoryId y responde 200")
    void findsAllProductsFilteredByCategory() throws Exception {
        when(getAllProductsUseCase.execute(0, 10, 7L, null, null, null, null)).thenReturn(
                new ProductRepository.PageResult(List.of(buildProduct(5L)), 1));

        mockMvc.perform(get("/api/v1/products?categoryId=7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.skip").value(0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].id").value(5));

        verify(getAllProductsUseCase).execute(0, 10, 7L, null, null, null, null);
    }

    @Test
    @DisplayName("GET filtra productos por markId y responde 200")
    void findsAllProductsFilteredByMark() throws Exception {
        when(getAllProductsUseCase.execute(0, 10, null, 3L, null, null, null)).thenReturn(
                new ProductRepository.PageResult(List.of(buildProduct(5L)), 1));

        mockMvc.perform(get("/api/v1/products?markId=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.skip").value(0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].id").value(5));

        verify(getAllProductsUseCase).execute(0, 10, null, 3L, null, null, null);
    }

    @Test
    @DisplayName("GET combina filtros de categoría y marca y responde 200")
    void findsAllProductsFilteredByCategoryAndMark() throws Exception {
        when(getAllProductsUseCase.execute(0, 10, 7L, 3L, null, null, null)).thenReturn(
                new ProductRepository.PageResult(List.of(buildProduct(5L)), 1));

        mockMvc.perform(get("/api/v1/products?categoryId=7&markId=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.skip").value(0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].id").value(5));

        verify(getAllProductsUseCase).execute(0, 10, 7L, 3L, null, null, null);
    }

    @Test
    @DisplayName("GET filtra productos por rango de precio y responde 200")
    void findsAllProductsFilteredByPriceRange() throws Exception {
        when(getAllProductsUseCase.execute(0, 10, null, null, 500.0, 1500.0, null)).thenReturn(
                new ProductRepository.PageResult(List.of(buildProduct(5L)), 1));

        mockMvc.perform(get("/api/v1/products?minPrice=500&maxPrice=1500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.skip").value(0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].id").value(5));

        verify(getAllProductsUseCase).execute(0, 10, null, null, 500.0, 1500.0, null);
    }

    @Test
    @DisplayName("GET filtra por búsqueda de texto y responde 200")
    void findsAllProductsFilteredBySearch() throws Exception {
        when(getAllProductsUseCase.execute(0, 10, null, null, null, null, "lenovo")).thenReturn(
                new ProductRepository.PageResult(List.of(buildProduct(5L)), 1));

        mockMvc.perform(get("/api/v1/products?search=lenovo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.skip").value(0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].id").value(5));

        verify(getAllProductsUseCase).execute(0, 10, null, null, null, null, "lenovo");
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
                .thenThrow(new com.mabc.e_shop.domain.exception.ResourceNotFoundException("El producto no existe."));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("El producto no existe."));
    }

    @Test
    @DisplayName("POST crea un producto y responde 201 con el formato estándar")
    void createsProduct() throws Exception {
        when(imageStorage.store(any(MultipartFile.class))).thenReturn(STORED_IMAGE);
        when(imageStorage.toPublicPath(STORED_IMAGE)).thenReturn("/uploads/uuid.png");
        when(createProductUseCase.execute(isNull(), eq(1L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0), eq("/uploads/uuid.png")))
                .thenReturn(buildProduct(5L));

        mockMvc.perform(validMultipartPost())
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
    @DisplayName("PUT actualiza un producto manteniendo la imagen existente y responde 200")
    void updatesProductKeepingImage() throws Exception {
        when(getProductByIdUseCase.execute(5L)).thenReturn(buildProduct(5L));
        when(createProductUseCase.execute(eq(5L), eq(1L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0), eq("https://images.example.com/products/notebook.png")))
                .thenReturn(buildProduct(5L));

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/5")
                        .param("markId", "1")
                        .param("categoryIds", "2", "3")
                        .param("name", "Notebook")
                        .param("description", "Equipo portátil")
                        .param("stock", "10")
                        .param("weight", "2.5")
                        .param("priceCost", "500.0")
                        .param("priceSale", "700.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Producto actualizado correctamente."))
                .andExpect(jsonPath("$.data.id").value(5));

        verify(imageStorage, never()).store(any(MultipartFile.class));
    }

    @Test
    @DisplayName("PUT reemplaza la imagen del producto y responde 200")
    void updatesProductReplacingImage() throws Exception {
        when(getProductByIdUseCase.execute(5L)).thenReturn(buildProduct(5L));
        when(imageStorage.store(any(MultipartFile.class))).thenReturn(STORED_IMAGE);
        when(imageStorage.toPublicPath(STORED_IMAGE)).thenReturn("/uploads/uuid.png");
        when(createProductUseCase.execute(eq(5L), eq(1L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0), eq("/uploads/uuid.png")))
                .thenReturn(buildProduct(5L));

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/5")
                        .file(image("notebook.png"))
                        .param("markId", "1")
                        .param("categoryIds", "2", "3")
                        .param("name", "Notebook")
                        .param("description", "Equipo portátil")
                        .param("stock", "10")
                        .param("weight", "2.5")
                        .param("priceCost", "500.0")
                        .param("priceSale", "700.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(5));

        verify(imageStorage).store(any(MultipartFile.class));
        verify(imageStorage, never()).delete(any(Path.class));
    }

    @Test
    @DisplayName("Responde 400 sin invocar el caso de uso cuando el DTO es inválido")
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(multipart("/api/v1/products")
                        .file(image("notebook.png"))
                        .param("markId", "1")
                        .param("categoryIds")
                        .param("name", "")
                        .param("description", "Equipo portátil")
                        .param("stock", "-1")
                        .param("weight", "2.5")
                        .param("priceCost", "500.0")
                        .param("priceSale", "700.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message", containsString("categoryIds")));

        verifyNoInteractions(createProductUseCase);
    }

    @Test
    @DisplayName("POST crea un producto sin imagen y responde 201 con imagePath nulo")
    void createsProductWithoutImage() throws Exception {
        when(createProductUseCase.execute(isNull(), eq(1L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0), isNull()))
                .thenReturn(buildProductWithoutImage(5L));

        mockMvc.perform(multipart("/api/v1/products")
                        .param("markId", "1")
                        .param("categoryIds", "2", "3")
                        .param("name", "Notebook")
                        .param("description", "Equipo portátil")
                        .param("stock", "10")
                        .param("weight", "2.5")
                        .param("priceCost", "500.0")
                        .param("priceSale", "700.0"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.imagePath").value(nullValue()));

        verify(imageStorage, never()).store(any(MultipartFile.class));
    }

    @Test
    @DisplayName("POST tolera image enviado como texto vacío y responde 201 sin imagen")
    void createsProductWhenImageSentAsBlankText() throws Exception {
        when(createProductUseCase.execute(isNull(), eq(1L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0), isNull()))
                .thenReturn(buildProductWithoutImage(5L));

        mockMvc.perform(validMultipartPost().param("image", ""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.imagePath").value(nullValue()));

        verify(imageStorage, never()).store(any(MultipartFile.class));
    }

    @Test
    @DisplayName("PUT actualiza un producto sin imagen manteniendo la ruta nula")
    void updatesProductKeepingNullImage() throws Exception {
        when(getProductByIdUseCase.execute(5L)).thenReturn(buildProductWithoutImage(5L));
        when(createProductUseCase.execute(eq(5L), eq(1L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0), isNull()))
                .thenReturn(buildProductWithoutImage(5L));

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/5")
                        .param("markId", "1")
                        .param("categoryIds", "2", "3")
                        .param("name", "Notebook")
                        .param("description", "Equipo portátil")
                        .param("stock", "10")
                        .param("weight", "2.5")
                        .param("priceCost", "500.0")
                        .param("priceSale", "700.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(5));

        verify(imageStorage, never()).store(any(MultipartFile.class));
        verify(imageStorage, never()).delete(any(Path.class));
    }

    @Test
    @DisplayName("Propaga la marca inexistente como 404 y elimina la imagen almacenada")
    void propagatesMissingMarkAs404() throws Exception {
        when(imageStorage.store(any(MultipartFile.class))).thenReturn(STORED_IMAGE);
        when(imageStorage.toPublicPath(STORED_IMAGE)).thenReturn("/uploads/uuid.png");
        when(createProductUseCase.execute(isNull(), eq(99L), anyList(), eq("Notebook"), eq("Equipo portátil"),
                eq(10), eq(2.5), eq(500.0), eq(700.0), eq("/uploads/uuid.png")))
                .thenThrow(new IllegalArgumentException("La marca no existe."));

        mockMvc.perform(multipart("/api/v1/products")
                        .file(image("notebook.png"))
                        .param("markId", "99")
                        .param("categoryIds", "2", "3")
                        .param("name", "Notebook")
                        .param("description", "Equipo portátil")
                        .param("stock", "10")
                        .param("weight", "2.5")
                        .param("priceCost", "500.0")
                        .param("priceSale", "700.0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("La marca no existe."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(imageStorage).delete(eq(STORED_IMAGE));
    }

    @Test
    @DisplayName("DELETE elimina un producto y responde 200 con el formato estándar")
    void deletesProduct() throws Exception {
        when(deleteProductUseCase.execute(5L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/products/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Producto eliminado correctamente."));
    }

    @Test
    @DisplayName("DELETE de un producto inexistente responde 404")
    void rejectsMissingProductOnDeleteAs404() throws Exception {
        when(deleteProductUseCase.execute(99L))
                .thenThrow(new com.mabc.e_shop.domain.exception.ResourceNotFoundException("El producto no existe."));

        mockMvc.perform(delete("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("El producto no existe."));
    }
}