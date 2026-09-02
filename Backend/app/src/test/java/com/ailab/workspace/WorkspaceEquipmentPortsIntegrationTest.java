package com.ailab.workspace;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.dto.CreateWorkspaceRequest;
import com.ailab.workspace.dto.SandboxEventCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
class WorkspaceEquipmentPortsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private String token;
    private String wsId;

    @BeforeEach
    void setUp() throws Exception {
        user = new User("ports_tester_" + System.currentTimeMillis(), "ports_tester" + System.currentTimeMillis() + "@jasscience.dev", "hashed_pwd", Role.USER);
        userRepository.save(user);
        token = "Bearer " + jwtService.issue(user);

        CreateWorkspaceRequest req = new CreateWorkspaceRequest("Equipment & Ports Workspace", "chemistry");
        MvcResult res = mockMvc.perform(post("/api/v1/workspaces")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> map = objectMapper.readValue(res.getResponse().getContentAsString(), Map.class);
        wsId = (String) map.get("id");
    }

    @Test
    void testEquipmentCatalogListAndDetails() throws Exception {
        // Query catalog
        mockMvc.perform(get("/api/v1/chemistry/equipment")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(10))))
                .andExpect(jsonPath("$[?(@.id == 'beaker-250ml')].ports[0]").isNotEmpty());

        // Get single details
        mockMvc.perform(get("/api/v1/chemistry/equipment/bunsen-burner")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("bunsen-burner"))
                .andExpect(jsonPath("$.capabilities", hasItem("HEAT_SOURCE")))
                .andExpect(jsonPath("$.ports[?(@.id == 'THERMAL')].connector").value(hasItem("thermal-pad")));
    }

    @Test
    void testPortValidation_RejectSelfConnection() throws Exception {
        // Add beaker
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-b", 1L, "ITEM_ADDED",
                        Map.of("id", "beaker-self", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        // Try to connect beaker-self to beaker-self -> 422 INVALID_CONNECTION
        SandboxEventCommand selfConn = new SandboxEventCommand(
                "self-conn", 2L, "CONNECT",
                Map.of("sourceItemId", "beaker-self", "sourcePort", "OUTLET", "targetItemId", "beaker-self", "targetPort", "INLET")
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(selfConn)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INVALID_CONNECTION"));
    }

    @Test
    void testPortValidation_RejectIncompatiblePortTypes() throws Exception {
        // Add beaker and Bunsen burner
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-b1", 1L, "ITEM_ADDED",
                        Map.of("id", "beaker-p1", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-burner", 2L, "ITEM_ADDED",
                        Map.of("id", "burner-p1", "equipmentType", "HEATER", "profileId", "bunsen-burner")
                )))).andExpect(status().isOk());

        // Try to connect burner THERMAL port directly into beaker FLUID INLET port -> 422 PORT_TYPE_MISMATCH
        SandboxEventCommand invalidTypeConn = new SandboxEventCommand(
                "invalid-type", 3L, "CONNECT",
                Map.of("sourceItemId", "burner-p1", "sourcePort", "THERMAL", "targetItemId", "beaker-p1", "targetPort", "INLET")
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTypeConn)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("PORT_TYPE_MISMATCH"));
    }

    @Test
    void testPortValidation_ValidThermalConnection() throws Exception {
        // Add beaker and burner
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-b-th", 1L, "ITEM_ADDED",
                        Map.of("id", "beaker-th", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-burner-th", 2L, "ITEM_ADDED",
                        Map.of("id", "burner-th", "equipmentType", "HEATER", "profileId", "bunsen-burner")
                )))).andExpect(status().isOk());

        // Valid THERMAL to THERMAL connection
        SandboxEventCommand validThermalConn = new SandboxEventCommand(
                "valid-th", 3L, "CONNECT",
                Map.of("sourceItemId", "burner-th", "sourcePort", "THERMAL", "targetItemId", "beaker-th", "targetPort", "THERMAL")
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validThermalConn)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.connectionsChanged[0].type").value("THERMAL"))
                .andExpect(jsonPath("$.checkpointFacts[?(@.type == 'CONNECTION_COMPLETED')]").isNotEmpty());
    }
}
