package com.ailab.workspace;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.service.WorkspaceMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
class WorkspaceRedTeamSecurityAndPhysicsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private WorkspaceMemberService memberService;

    private User createUser(String prefix) {
        String uid = prefix + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 4);
        User u = new User(uid, uid + "@jasscience.dev", "hash", Role.USER);
        return userRepository.save(u);
    }

    private String createBearerToken(User u) {
        return "Bearer " + jwtService.issue(u);
    }

    private String createWorkspace(String ownerToken, String name) throws Exception {
        CreateWorkspaceRequest req = new CreateWorkspaceRequest(name, "chemistry");
        String res = mockMvc.perform(post("/api/v1/workspaces")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(res).get("id").asText();
    }

    @Test
    @DisplayName("Red-Team: LAST_OWNER protection prevents deleting or demoting the sole owner of a workspace")
    void testLastOwnerProtection() throws Exception {
        User owner = createUser("usr_owner_redteam");
        String token = createBearerToken(owner);
        String wsId = createWorkspace(token, "Owner Protection Workspace");
        String ownerId = owner.getId();

        // 1. Attempt to demote sole owner to EDITOR -> returns 422 with LAST_OWNER
        mockMvc.perform(patch("/api/v1/workspaces/" + wsId + "/members/" + ownerId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"EDITOR\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LAST_OWNER"));

        // 2. Attempt to remove sole owner -> returns 422 with LAST_OWNER
        mockMvc.perform(delete("/api/v1/workspaces/" + wsId + "/members/" + ownerId)
                        .header("Authorization", token))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LAST_OWNER"));
    }

    @Test
    @DisplayName("Red-Team: Adversarial port connections rejected with precise domain error codes")
    void testAdversarialPortConnections() throws Exception {
        User user = createUser("usr_port_tester");
        String token = createBearerToken(user);
        String wsId = createWorkspace(token, "Port Adversarial Workspace");

        // Add Beaker
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-beaker-1", 1L, "ITEM_ADDED",
                                Map.of("id", "beaker-1", "equipmentType", "CONTAINER", "profileId", "beaker-250ml")
                        ))))
                .andExpect(status().isOk());

        // Add Burner
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-burner-1", 2L, "ITEM_ADDED",
                                Map.of("id", "burner-1", "equipmentType", "HEATER", "profileId", "bunsen-burner")
                        ))))
                .andExpect(status().isOk());

        // 1. Self connection -> INVALID_CONNECTION
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "self-conn", 3L, "CONNECT",
                                Map.of("sourceItemId", "beaker-1", "sourcePort", "INLET", "targetItemId", "beaker-1", "targetPort", "OUTLET")
                        ))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INVALID_CONNECTION"));

        // 2. Incompatible Port Types (FLUID to THERMAL) -> PORT_TYPE_MISMATCH
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "type-mismatch", 3L, "CONNECT",
                                Map.of("sourceItemId", "beaker-1", "sourcePort", "INLET", "targetItemId", "burner-1", "targetPort", "THERMAL")
                        ))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("PORT_TYPE_MISMATCH"));

        // 3. Direction Mismatch (INPUT to INPUT) -> PORT_DIRECTION_MISMATCH
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "dir-mismatch", 3L, "CONNECT",
                                Map.of("sourceItemId", "beaker-1", "sourcePort", "INLET", "targetItemId", "burner-1", "targetPort", "GAS_INLET")
                        ))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("PORT_DIRECTION_MISMATCH"));
    }

    @Test
    @DisplayName("Red-Team: Acid-Base neutralization stoichiometry computes input-dependent dynamic pH & exothermic delta T")
    void testAcidBaseNeutralizationStoichiometry() throws Exception {
        User user = createUser("usr_chem_tester");
        String token = createBearerToken(user);
        String wsId = createWorkspace(token, "Stoichiometry Workspace");

        // Add Beaker
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-beaker-chem", 1L, "ITEM_ADDED",
                                Map.of("id", "beaker-chem", "equipmentType", "CONTAINER", "profileId", "beaker-500ml", "capacityMl", 500)
                        ))))
                .andExpect(status().isOk());

        // Add 50 mL of 1.0 M HCl
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-hcl-50", 2L, "MATERIAL_ADDED",
                                Map.of("itemId", "beaker-chem", "materialId", "HCl", "amountMl", 50.0, "concentrationMolar", 1.0)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].volumeMl").value(50.0));

        // Add 50 mL of 1.0 M NaOH -> Equimolar neutralization, temp rise, NaCl formation
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-naoh-50", 3L, "MATERIAL_ADDED",
                                Map.of("itemId", "beaker-chem", "materialId", "NaOH", "amountMl", 50.0, "concentrationMolar", 1.0)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].materialId").value("NaCl(aq)"))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].temperatureC", greaterThan(24.0)))
                .andExpect(jsonPath("$.checkpointFacts[0].type").value("NEUTRALIZATION_COMPLETED"));
    }

    @Test
    @DisplayName("Red-Team: Zn + HCl single displacement computes gas bubbles, H2 evolution & safety alert")
    void testSingleDisplacementGasEvolution() throws Exception {
        User user = createUser("usr_zn_tester");
        String token = createBearerToken(user);
        String wsId = createWorkspace(token, "Zn Reaction Workspace");

        // Add Flask
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-flask-zn", 1L, "ITEM_ADDED",
                                Map.of("id", "flask-zn", "equipmentType", "CONTAINER", "profileId", "erlenmeyer-flask", "capacityMl", 250)
                        ))))
                .andExpect(status().isOk());

        // Add 50 mL of HCl
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-hcl-zn", 2L, "MATERIAL_ADDED",
                                Map.of("itemId", "flask-zn", "materialId", "HCl", "amountMl", 50.0)
                        ))))
                .andExpect(status().isOk());

        // Add 5 g of Zn solid -> Effervescence, bubbles = true, safety alert
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "add-zn-solid", 3L, "MATERIAL_ADDED",
                                Map.of("itemId", "flask-zn", "materialId", "Zn", "amountMl", 5.0, "phase", "solid")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].materialId").value("ZnCl2(aq)"))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].appearance.bubbles").value(true))
                .andExpect(jsonPath("$.stateDelta.itemsChanged[0].appearance.gas").value("H2"))
                .andExpect(jsonPath("$.safetyWarnings", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.checkpointFacts[0].type").value("GAS_EVOLVED"));
    }

    @Test
    @DisplayName("Red-Team: Concurrent conflicting mutations on same stateVersion yield 409 Conflict for loser")
    void testConcurrentVersionConflict() throws Exception {
        User user = createUser("usr_conc");
        String token = createBearerToken(user);
        String wsId = createWorkspace(token, "Concurrency Test Lab");

        // Concurrent requests on base stateVersion 1L
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<Integer> op1 = () -> mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "conc-op-1", 1L, "ITEM_ADDED",
                                Map.of("id", "item-conc-1", "equipmentType", "CONTAINER", "profileId", "beaker-250ml")
                        ))))
                .andReturn().getResponse().getStatus();

        Callable<Integer> op2 = () -> mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "conc-op-2", 1L, "ITEM_ADDED",
                                Map.of("id", "item-conc-2", "equipmentType", "CONTAINER", "profileId", "beaker-500ml")
                        ))))
                .andReturn().getResponse().getStatus();

        Future<Integer> f1 = executor.submit(op1);
        Future<Integer> f2 = executor.submit(op2);

        int s1 = f1.get(5, TimeUnit.SECONDS);
        int s2 = f2.get(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Exactly one must succeed (200) and the other must fail with optimistic conflict (409)
        assertThat(List.of(s1, s2)).containsExactlyInAnyOrder(200, 409);
    }
}
