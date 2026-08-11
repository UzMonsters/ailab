package com.ailab.chemistry.controller;

import com.ailab.auth.security.JwtService;
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
    private JwtService jwtService;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User("Catalog Tester", "catalogtester@example.com", "hash", Role.USER);
        userRepository.save(user);
        token = "Bearer " + jwtService.issue(user);
    }

    @Test
    void testEquipmentCatalogEndpoints() throws Exception {
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
                .andExpect(jsonPath("$[0].profileId").value("EQ-DWK-KIMAX-28014B-100-VOLUMETRIC"));

        mockMvc.perform(get("/api/v1/chemistry/equipment/EQ-DWK-KIMAX-28014B-100-VOLUMETRIC")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ports.length()").value(3));
    }

    @Test
    void testMaterialsCatalogEndpoints() throws Exception {
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
                .andExpect(jsonPath("$[0].materialId").value("COMP-H2O"));
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
}
