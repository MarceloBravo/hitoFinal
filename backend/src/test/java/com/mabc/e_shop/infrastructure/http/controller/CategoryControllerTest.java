package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.DeleteCategoryUseCase;
import com.mabc.e_shop.application.usecase.GetAllCategoriesUseCase;
import com.mabc.e_shop.application.usecase.GetCategoryByIdUseCase;
import com.mabc.e_shop.application.usecase.SaveCategoryUseCase;
import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.valueobject.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaveCategoryUseCase saveCategoryUseCase;

    @MockitoBean
    private GetAllCategoriesUseCase getAllCategoriesUseCase;

    @MockitoBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockitoBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @Test
    @DisplayName("GET lista todas las categorías y responde 200 con el formato estándar")
    void findsAllCategories() throws Exception {
        when(getAllCategoriesUseCase.execute()).thenReturn(java.util.List.of(new Category(1L, new Name("Gaming"))));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Gaming"));
    }

    @Test
    @DisplayName("GET busca una categoría por id y responde 200")
    void findsCategoryById() throws Exception {
        when(getCategoryByIdUseCase.execute(1L)).thenReturn(new Category(1L, new Name("Gaming")));

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Gaming"));
    }

    @Test
    @DisplayName("GET de una categoría inexistente responde 404")
    void rejectsMissingCategoryAs404() throws Exception {
        when(getCategoryByIdUseCase.execute(99L))
                .thenThrow(new com.mabc.e_shop.domain.exception.ResourceNotFoundException("La categoría no existe."));

        mockMvc.perform(get("/api/v1/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("La categoría no existe."));
    }

    @Test
    @DisplayName("POST registra una categoría y responde 201 con el formato estándar")
    void createsCategory() throws Exception {
        when(saveCategoryUseCase.execute(isNull(), eq("Gaming"), eq(true)))
                .thenReturn(new Category(1L, new Name("Gaming")));

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gaming\",\"active\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Categoría registrada correctamente."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Gaming"))
                .andExpect(jsonPath("$.data.active").value(true));

        verify(saveCategoryUseCase).execute(null, "Gaming", true);
    }

    @Test
    @DisplayName("PUT actualiza una categoría existente y responde 200")
    void updatesCategory() throws Exception {
        Category updated = new Category(2L, new Name("Oficina"));
        updated.deactivate();
        when(saveCategoryUseCase.execute(eq(2L), eq("Oficina"), eq(false))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/categories/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Oficina\",\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Categoría actualizada correctamente."))
                .andExpect(jsonPath("$.data.name").value("Oficina"))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    @DisplayName("Propaga la categoría inexistente como 404 en el formato estándar")
    void propagatesMissingResourceAs404() throws Exception {
        when(saveCategoryUseCase.execute(eq(9L), eq("Oficina"), eq(true)))
                .thenThrow(new IllegalArgumentException("La categoría no existe."));

        mockMvc.perform(put("/api/v1/categories/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Oficina\",\"active\":true}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("La categoría no existe."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("DELETE elimina una categoría y responde 200 con el formato estándar")
    void deletesCategory() throws Exception {
        when(deleteCategoryUseCase.execute(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Categoría eliminada correctamente."));
    }

    @Test
    @DisplayName("DELETE de una categoría inexistente responde 404")
    void rejectsMissingCategoryOnDeleteAs404() throws Exception {
        when(deleteCategoryUseCase.execute(99L))
                .thenThrow(new com.mabc.e_shop.domain.exception.ResourceNotFoundException("La categoría no existe."));

        mockMvc.perform(delete("/api/v1/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("La categoría no existe."));
    }

    @Test
    @DisplayName("DELETE de una categoría en uso responde 409")
    void rejectsInUseCategoryOnDeleteAs409() throws Exception {
        when(deleteCategoryUseCase.execute(1L))
                .thenThrow(new IllegalStateException("La categoría no se puede eliminar porque está asociada a productos."));

        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409))
                .andExpect(jsonPath("$.message").value("La categoría no se puede eliminar porque está asociada a productos."));
    }
}
