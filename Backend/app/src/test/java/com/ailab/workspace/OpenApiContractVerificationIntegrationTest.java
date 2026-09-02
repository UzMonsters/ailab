package com.ailab.workspace;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiContractVerificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Verify generated OpenAPI v3 documentation exposes all chemistry workspace and collaboration endpoints")
    void testOpenApiContractExposesAllEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/v1/workspaces']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workspaces/{id}/events']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workspaces/{id}/members']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workspaces/{id}/invitations']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workspaces/{id}/share-links']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/shared-workspaces/resolve']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workspaces/{id}/chat/messages']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workspaces/{id}/comments']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workspaces/{id}/measurements']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workspaces/{id}/preview-upload-urls']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/chemistry/equipment/catalog']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/chemistry/experiments/{sessionId}/measurements']").exists());
    }
}
