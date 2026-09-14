package com.ailab.chemistry.controller;

import com.ailab.auth.security.JwtService;
import com.ailab.admin.catalog.AdminCatalogDraftEntity;
import com.ailab.admin.catalog.AdminCatalogDraftRepository;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
public class ChemistryCatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminCatalogDraftRepository adminCatalogDraftRepository;

    @Autowired
    private JwtService jwtService;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        adminCatalogDraftRepository.deleteAll();

        user = new User("Catalog Tester", "catalogtester@example.com", "hash", Role.USER);
        userRepository.save(user);
        token = "Bearer " + jwtService.issue(user);
    }

    @Test
    void publicEquipmentCatalogReturnsPublishedAdminCatalogRowsOnly() throws Exception {
        AdminCatalogDraftEntity published = new AdminCatalogDraftEntity("EQUIPMENT", "microreactor-public", "DRAFT", Map.of(
                "code", "microreactor-public",
                "name", "Microreactor Public",
                "category", "APPARATUS",
                "rendererKey", "microreactor",
                "ports", List.of(Map.of("id", "INLET", "type", "FLUID", "direction", "INPUT", "connector", "microfluidic")),
                "limits", Map.of("maxVolumeMl", 5.0)
        ));
        published.publish("test-publish-equipment");

        AdminCatalogDraftEntity draft = new AdminCatalogDraftEntity("EQUIPMENT", "microreactor-draft", "DRAFT", Map.of(
                "code", "microreactor-draft",
                "name", "Microreactor Draft",
                "category", "APPARATUS"
        ));
        adminCatalogDraftRepository.saveAll(List.of(published, draft));

        mockMvc.perform(get("/api/v1/chemistry/equipment")
                        .header("Authorization", token)
                        .param("query", "microreactor")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("microreactor-public"))
                .andExpect(jsonPath("$[0].displayName").value("Microreactor Public"));

        mockMvc.perform(get("/api/v1/chemistry/equipment/microreactor-public")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("microreactor-public"))
                .andExpect(jsonPath("$.ports.length()").value(1));
    }

    @Test
    void publicMaterialsCatalogReturnsPublishedAdminCatalogRowsOnly() throws Exception {
        AdminCatalogDraftEntity published = new AdminCatalogDraftEntity("MATERIAL", "sodium-hydroxide-1m", "DRAFT", Map.of(
                "code", "sodium-hydroxide-1m",
                "name", "Sodium Hydroxide 1M",
                "formula", "NaOH",
                "type", "SOLUTION",
                "phase", "LIQUID"
        ));
        published.publish("test-publish-material");

        AdminCatalogDraftEntity draft = new AdminCatalogDraftEntity("MATERIAL", "sodium-hydroxide-draft", "DRAFT", Map.of(
                "code", "sodium-hydroxide-draft",
                "name", "Sodium Hydroxide Draft",
                "formula", "NaOH",
                "phase", "LIQUID"
        ));
        adminCatalogDraftRepository.saveAll(List.of(published, draft));

        mockMvc.perform(get("/api/v1/chemistry/materials")
                        .header("Authorization", token)
                        .param("query", "sodium")
                        .param("phase", "liquid")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].materialId").value("sodium-hydroxide-1m"))
                .andExpect(jsonPath("$[0].name").value("Sodium Hydroxide 1M"));
    }

    @Test
    void testEquipmentCatalogEndpoints() throws Exception {
        savePublished("EQUIPMENT", "volumetric-flask-100", Map.of(
                "code", "volumetric-flask-100",
                "name", "Volumetric Flask 100 mL",
                "category", "FLASK",
                "ports", List.of(
                        Map.of("id", "INLET", "type", "FLUID", "direction", "INPUT", "connector", "standard-neck"),
                        Map.of("id", "OUTLET", "type", "FLUID", "direction", "OUTPUT", "connector", "standard-neck"),
                        Map.of("id", "THERMAL", "type", "THERMAL", "direction", "BIDIRECTIONAL", "connector", "glass-body")
                )
        ));

        mockMvc.perform(get("/api/v1/chemistry/equipment")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/chemistry/equipment")
                        .header("Authorization", token)
                        .param("category", "flask")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].profileId").value("volumetric-flask-100"));

        mockMvc.perform(get("/api/v1/chemistry/equipment/volumetric-flask-100")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ports.length()").value(3));
    }

    @Test
    void testMaterialsCatalogEndpoints() throws Exception {
        savePublished("SUBSTANCE", "H2O", Map.of(
                "code", "H2O",
                "name", "Water",
                "formula", "H2O",
                "phase", "LIQUID",
                "type", "COMPOUND"
        ));

        mockMvc.perform(get("/api/v1/chemistry/materials")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/chemistry/materials")
                        .header("Authorization", token)
                        .param("query", "water")
                        .param("phase", "liquid")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].materialId").value("H2O"));
    }

    @Test
    void testExistingChemistryEndpointsPreserved() throws Exception {
        mockMvc.perform(get("/api/v1/chemistry/elements")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/chemistry/compounds")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    private void savePublished(String entityType, String code, Map<String, Object> data) {
        AdminCatalogDraftEntity entity = new AdminCatalogDraftEntity(entityType, code, "DRAFT", data);
        entity.publish("test-publish-" + code);
        adminCatalogDraftRepository.save(entity);
    }
}
