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
class WorkspaceCanonicalScienceIntegrationTest {

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
        user = new User("science_tester_" + System.currentTimeMillis(), "science_tester" + System.currentTimeMillis() + "@jasscience.dev", "hashed_pwd", Role.USER);
        userRepository.save(user);
        token = "Bearer " + jwtService.issue(user);

        CreateWorkspaceRequest req = new CreateWorkspaceRequest("Science Suite Workspace", "chemistry");
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
    void testSingleVersionAuthorityAndOptimisticLocking() throws Exception {
        // Version starts at 1
        // 1. ADD_ITEM beaker
        SandboxEventCommand cmd1 = new SandboxEventCommand(
                "evt-1", 1L, "ITEM_ADDED",
                Map.of("id", "beaker-1", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cmd1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(true))
                .andExpect(jsonPath("$.stateVersion").value(2))
                .andExpect(jsonPath("$.newVersion").value(2));

        // 2. Stale version (expectedVersion = 1 instead of 2) -> 409 Conflict
        SandboxEventCommand staleCmd = new SandboxEventCommand(
                "evt-2-stale", 1L, "ITEM_MOVED",
                Map.of("itemId", "beaker-1", "x", 100, "y", 200)
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staleCmd)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STATE_VERSION_CONFLICT"));

        // 3. Duplicate clientEventId -> Idempotent replay
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cmd1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.idempotencyHit").value(true));
    }

    @Test
    void testScenario1_CuSO4AqueousDilution() throws Exception {
        // Setup beaker-1
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "init-b1", 1L, "ITEM_ADDED",
                        Map.of("id", "beaker-1", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        // Add CuSO4(aq) (50 mL)
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-cuso4", 2L, "MATERIAL_ADDED",
                        Map.of("itemId", "beaker-1", "materialId", "CuSO4(aq)", "amountMl", 50.0, "phase", "LIQUID")
                )))).andExpect(status().isOk());

        // Add Water H2O (100 mL) -> Dilution
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-water", 3L, "MATERIAL_ADDED",
                                Map.of("itemId", "beaker-1", "materialId", "H2O", "amountMl", 100.0, "phase", "LIQUID")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newVersion").value(4))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].volumeMl").value(150.0))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].contents[0].mixtureState").value("HOMOGENEOUS"))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].appearance.color").value(startsWith("#")))
                .andExpect(jsonPath("$.checkpointFacts[?(@.type == 'DILUTION_COMPLETED')]").isNotEmpty());

        // Verify Hydration/Reload returns the exact same diluted state
        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/state")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(4))
                .andExpect(jsonPath("$.items[0].volumeMl").value(150.0))
                .andExpect(jsonPath("$.items[0].contents[0].materialCode").value("CuSO4(aq)"));
    }

    @Test
    void testScenario2_CuSO4SolidDissolution() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "init-b2", 1L, "ITEM_ADDED",
                        Map.of("id", "beaker-2", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        // Add Solid CuSO4(s)
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-solid-cuso4", 2L, "MATERIAL_ADDED",
                        Map.of("itemId", "beaker-2", "materialId", "CuSO4(s)", "amountMl", 10.0, "phase", "SOLID")
                )))).andExpect(status().isOk());

        // Add H2O -> Dissolves into blue aqueous solution
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-h2o", 3L, "MATERIAL_ADDED",
                                Map.of("itemId", "beaker-2", "materialId", "H2O", "amountMl", 90.0, "phase", "LIQUID")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].contents[0].materialCode").value("CuSO4(aq)"))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].appearance.color").value("#2563EB"))
                .andExpect(jsonPath("$.checkpointFacts[?(@.type == 'DISSOLUTION_COMPLETED')]").isNotEmpty());
    }

    @Test
    void testScenario3_KMnO4PurpleSolution() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "init-flask", 1L, "ITEM_ADDED",
                        Map.of("id", "flask-1", "equipmentType", "CONTAINER", "profileId", "erlenmeyer-flask", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        // Add KMnO4
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-kmno4", 2L, "MATERIAL_ADDED",
                        Map.of("itemId", "flask-1", "materialId", "KMnO4", "amountMl", 5.0, "phase", "SOLID")
                )))).andExpect(status().isOk());

        // Add H2O -> Deep purple solution (#7E22CE)
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-h2o-k", 3L, "MATERIAL_ADDED",
                                Map.of("itemId", "flask-1", "materialId", "H2O", "amountMl", 100.0, "phase", "LIQUID")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].appearance.color").value("#7E22CE"))
                .andExpect(jsonPath("$.checkpointFacts[?(@.type == 'DISSOLUTION_COMPLETED')]").isNotEmpty());
    }

    @Test
    void testScenario4_HClAndNaOHNeutralizationAndHeat() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "init-neut-beaker", 1L, "ITEM_ADDED",
                        Map.of("id", "neut-beaker", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        // Add HCl
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-hcl", 2L, "MATERIAL_ADDED",
                        Map.of("itemId", "neut-beaker", "materialId", "HCl", "amountMl", 50.0, "phase", "LIQUID")
                )))).andExpect(status().isOk());

        // Add NaOH -> Neutralization: NaCl(aq) + H2O and exothermic heat (+15 C)
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-naoh", 3L, "MATERIAL_ADDED",
                                Map.of("itemId", "neut-beaker", "materialId", "NaOH", "amountMl", 50.0, "phase", "LIQUID")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].contents[0].materialCode").value("NaCl(aq)"))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].temperatureC").value(greaterThan(30.0)))
                .andExpect(jsonPath("$.checkpointFacts[?(@.type == 'NEUTRALIZATION_COMPLETED')]").isNotEmpty());
    }

    @Test
    void testScenario5_ZnAndHClGasAndSafetyAlert() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "init-tube", 1L, "ITEM_ADDED",
                        Map.of("id", "tube-1", "equipmentType", "CONTAINER", "profileId", "test-tube", "capacityMl", 50.0)
                )))).andExpect(status().isOk());

        // Add Zinc metal Zn
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-zn", 2L, "MATERIAL_ADDED",
                        Map.of("itemId", "tube-1", "materialId", "Zn", "amountMl", 5.0, "phase", "SOLID")
                )))).andExpect(status().isOk());

        // Add HCl -> Single displacement: ZnCl2(aq) + H2(g) bubbles + safety alert
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-hcl-zn", 3L, "MATERIAL_ADDED",
                                Map.of("itemId", "tube-1", "materialId", "HCl", "amountMl", 20.0, "phase", "LIQUID")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].appearance.bubbles").value(true))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].appearance.gas").value("H2"))
                .andExpect(jsonPath("$.safetyWarnings").isNotEmpty())
                .andExpect(jsonPath("$.checkpointFacts[?(@.type == 'GAS_EVOLVED')]").isNotEmpty());
    }

    @Test
    void testScienceAwareUndoRedo() throws Exception {
        // 1. ADD beaker
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "undo-b1", 1L, "ITEM_ADDED",
                        Map.of("id", "beaker-u", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        // 2. ADD KMnO4
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "undo-k", 2L, "MATERIAL_ADDED",
                        Map.of("itemId", "beaker-u", "materialId", "KMnO4", "amountMl", 10.0, "phase", "SOLID")
                )))).andExpect(status().isOk());

        // 3. Undo KMnO4 addition
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/undo?expectedVersion=3")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(4))
                .andExpect(jsonPath("$.items[0].volumeMl").doesNotExist());

        // 4. Redo KMnO4 addition
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/redo?expectedVersion=4")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(5))
                .andExpect(jsonPath("$.items[0].volumeMl").value(10.0));
    }
}
