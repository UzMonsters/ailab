package com.ailab.common.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
class OpenApiContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void exportsOpenApiAndContainsLaboratoryContracts() throws Exception {
        String spec = mockMvc.perform(get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Path export = Path.of("target", "openapi", "ailab-openapi.json");
        Files.createDirectories(export.getParent());
        Files.writeString(export, spec);

        JsonNode paths = objectMapper.readTree(spec).path("paths");
        assertThat(paths.has("/api/v1/workspaces")).isTrue();
        assertThat(paths.has("/api/v1/workspaces/{id}/state")).isTrue();
        assertThat(paths.has("/api/v1/workspaces/{id}/events")).isTrue();
        assertThat(paths.has("/api/v1/chemistry/equipment")).isTrue();
        assertThat(paths.has("/api/v1/chemistry/materials")).isTrue();
        assertThat(paths.has("/api/v1/chemistry/experiments/{sessionId}")).isTrue();
    }
}
