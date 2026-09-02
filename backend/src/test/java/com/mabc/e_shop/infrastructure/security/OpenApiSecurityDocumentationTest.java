package com.mabc.e_shop.infrastructure.security;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class OpenApiSecurityDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("La operacion PUT /api/v1/products/{id} declara bearerAuth como security requirement")
    void productPutDeclaresBearerAuth() throws Exception {
        MvcResult result = mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andReturn();
        String spec = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Map<String, Object> putSecurity = JsonPath.read(spec, "$.paths['/api/v1/products/{id}'].put.security[0]");
        assertNotNull(putSecurity);
        assertTrue(putSecurity.containsKey("bearerAuth"));

        String schemeType = JsonPath.read(spec, "$.components.securitySchemes.bearerAuth.type");
        assertEquals("http", schemeType);
    }
}
