package com.mabc.e_shop.infrastructure.http.controller;

import com.mabc.e_shop.application.usecase.DeleteMarkUseCase;
import com.mabc.e_shop.application.usecase.GetAllMarksUseCase;
import com.mabc.e_shop.application.usecase.GetMarkByIdUseCase;
import com.mabc.e_shop.application.usecase.SaveMarkUseCase;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.valueobject.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarkController.class)
@AutoConfigureMockMvc(addFilters = false)
class MarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaveMarkUseCase saveMarkUseCase;

    @MockitoBean
    private GetAllMarksUseCase getAllMarksUseCase;

    @MockitoBean
    private GetMarkByIdUseCase getMarkByIdUseCase;

    @MockitoBean
    private DeleteMarkUseCase deleteMarkUseCase;

    @Test
    @DisplayName("GET lista todas las marcas y responde 200 con el formato estándar")
    void findsAllMarks() throws Exception {
        when(getAllMarksUseCase.execute()).thenReturn(java.util.List.of(new Mark(1L, new Name("Lenovo"))));

        mockMvc.perform(get("/api/v1/marks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Lenovo"));
    }

    @Test
    @DisplayName("GET busca una marca por id y responde 200")
    void findsMarkById() throws Exception {
        when(getMarkByIdUseCase.execute(1L)).thenReturn(new Mark(1L, new Name("Lenovo")));

        mockMvc.perform(get("/api/v1/marks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Lenovo"));
    }

    @Test
    @DisplayName("GET de una marca inexistente responde 404")
    void rejectsMissingMarkAs404() throws Exception {
        when(getMarkByIdUseCase.execute(99L))
                .thenThrow(new com.mabc.e_shop.domain.exception.ResourceNotFoundException("La marca no existe."));

        mockMvc.perform(get("/api/v1/marks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("La marca no existe."));
    }

    @Test
    @DisplayName("POST registra una marca y responde 201 con el formato estándar")
    void createsMark() throws Exception {
        when(saveMarkUseCase.execute(isNull(), eq("Lenovo"), eq(true))).thenReturn(new Mark(1L, new Name("Lenovo")));

        mockMvc.perform(post("/api/v1/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Lenovo\",\"active\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Marca registrada correctamente."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Lenovo"))
                .andExpect(jsonPath("$.data.active").value(true));

        verify(saveMarkUseCase).execute(null, "Lenovo", true);
    }

    @Test
    @DisplayName("PUT actualiza una marca existente y responde 200")
    void updatesMark() throws Exception {
        Mark updated = new Mark(1L, new Name("Asus"));
        updated.deactivate();
        when(saveMarkUseCase.execute(eq(1L), eq("Asus"), eq(false))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/marks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Asus\",\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Marca actualizada correctamente."))
                .andExpect(jsonPath("$.data.name").value("Asus"))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    @DisplayName("Responde 400 sin invocar el caso de uso cuando el DTO es inválido")
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/marks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message", containsString("name")));

        verifyNoInteractions(saveMarkUseCase);
    }

    @Test
    @DisplayName("Propaga el recurso inexistente como 404 en el formato estándar")
    void propagatesMissingResourceAs404() throws Exception {
        when(saveMarkUseCase.execute(eq(9L), eq("Asus"), eq(true)))
                .thenThrow(new IllegalArgumentException("La marca no existe."));

        mockMvc.perform(put("/api/v1/marks/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Asus\",\"active\":true}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("La marca no existe."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("DELETE elimina una marca existente y responde 200")
    void deletesMark() throws Exception {
        mockMvc.perform(delete("/api/v1/marks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Marca eliminada correctamente."));

        verify(deleteMarkUseCase).execute(1L);
    }

    @Test
    @DisplayName("DELETE de una marca inexistente responde 404")
    void rejectsDeletingMissingMarkAs404() throws Exception {
        doThrow(new com.mabc.e_shop.domain.exception.ResourceNotFoundException("La marca no existe."))
                .when(deleteMarkUseCase).execute(99L);

        mockMvc.perform(delete("/api/v1/marks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("La marca no existe."));
    }

    @Test
    @DisplayName("DELETE de una marca asociada a productos responde 409")
    void rejectsDeletingMarkInUseAs409() throws Exception {
        doThrow(new IllegalStateException("La marca no se puede eliminar porque está asociada a productos."))
                .when(deleteMarkUseCase).execute(2L);

        mockMvc.perform(delete("/api/v1/marks/2"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409))
                .andExpect(jsonPath("$.message").value(
                        "La marca no se puede eliminar porque está asociada a productos."));
    }
}
