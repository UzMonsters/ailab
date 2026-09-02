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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
class WorkspaceStateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private org.flywaydb.core.Flyway identityFlyway;

    @Autowired
    private org.flywaydb.core.Flyway workspaceFlyway;

    @Autowired(required = false)
    @org.springframework.beans.factory.annotation.Qualifier("chemistryFlyway")
    private org.flywaydb.core.Flyway chemistryFlyway;

    private User user;
    private String token;
    private String wsId;

    @BeforeEach
    void setUp() throws Exception {
        com.ailab.chemistry.TestPostgresUtils.assumePostgresAvailable();
        try (java.sql.Connection conn = workspaceFlyway.getConfiguration().getDataSource().getConnection()) {
            boolean usersExist = conn.getMetaData().getTables(null, "public", "users", null).next();
            boolean wsExist = conn.getMetaData().getTables(null, "public", "workspaces", null).next();
            if (!usersExist || !wsExist) {
                try { identityFlyway.clean(); } catch (Exception ignored) {}
                try { workspaceFlyway.clean(); } catch (Exception ignored) {}
                identityFlyway.migrate();
                workspaceFlyway.migrate();
            }
        } catch (Exception e) {
            try { identityFlyway.clean(); } catch (Exception ignored) {}
            try { workspaceFlyway.clean(); } catch (Exception ignored) {}
            identityFlyway.migrate();
            workspaceFlyway.migrate();
        }
        if (chemistryFlyway != null) {
            try { chemistryFlyway.migrate(); } catch (Exception ignored) {}
        }

        userRepository.deleteAll();

        user = new User("State Tester", "statetester@example.com", "hash", Role.USER);
        userRepository.save(user);
        token = "Bearer " + jwtService.issue(user);

        CreateWorkspaceRequest req = new CreateWorkspaceRequest("State Test Lab", "chemistry");
        String res = mockMvc.perform(post("/api/v1/workspaces")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        wsId = objectMapper.readTree(res).get("id").asText();
    }

    @Test
    void testGetAndSaveWorkspaceState() throws Exception {
        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/state")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workspaceId").value(wsId))
                .andExpect(jsonPath("$.stateVersion").value(1));
    }

    @Test
    void testAppendEventIdempotencyAndOptimisticLock() throws Exception {
        SandboxEventCommand cmd1 = new SandboxEventCommand(
                "client-evt-100",
                1L,
                "ITEM_ADDED",
                Map.of("id", "flask-1", "equipmentType", "ERLENMEYER_FLASK", "name", "Erlenmeyer Flask")
        );

        // 1. Append ITEM_ADDED event successfully
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cmd1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientEventId").value("client-evt-100"))
                .andExpect(jsonPath("$.stateVersion").value(2));

        // 2. Idempotent re-submission returns same ack without error
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cmd1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientEventId").value("client-evt-100"))
                .andExpect(jsonPath("$.stateVersion").value(2));

        // 3. New event with stale expected version returns 409 conflict
        SandboxEventCommand cmdStale = new SandboxEventCommand(
                "client-evt-101",
                1L, // expected 1, but server version is 2
                "ITEM_MOVED",
                Map.of("itemId", "flask-1", "x", 100, "y", 200)
        );

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cmdStale)))
                .andExpect(status().isConflict());

        // 4. Retrieve event history
        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .param("afterVersion", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientEventId").value("client-evt-100"));
    }

    @Test
    void testScientificEventValidationAndRollback() throws Exception {
        SandboxEventCommand addVessel = new SandboxEventCommand(
                "client-evt-vessel",
                1L,
                "ITEM_ADDED",
                Map.of(
                        "id", "vessel-1",
                        "equipmentType", "VOLUMETRIC_FLASK",
                        "capacityMl", 100
                )
        );

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVessel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(2))
                .andExpect(jsonPath("$.stateDelta.addedItem.profileId").value("EQ-DWK-KIMAX-28014B-100-VOLUMETRIC"));

        SandboxEventCommand unknownMaterial = new SandboxEventCommand(
                "client-evt-bad-material",
                2L,
                "MATERIAL_ADDED",
                Map.of("itemId", "vessel-1", "materialId", "COMP-NOT-A-REAL-COMPOUND", "amountMl", 10)
        );

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unknownMaterial)))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/state")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(2))
                .andExpect(jsonPath("$.items[0].volumeMl").doesNotExist());

        SandboxEventCommand tooMuchWater = new SandboxEventCommand(
                "client-evt-too-much-water",
                2L,
                "MATERIAL_ADDED",
                Map.of("itemId", "vessel-1", "materialId", "COMP-H2O", "amountMl", 150)
        );

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooMuchWater)))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/state")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(2))
                .andExpect(jsonPath("$.items[0].volumeMl").doesNotExist());
    }

    @Test
    void testMaterialAndTransferEventsUseAuthoritativeSimulationState() throws Exception {
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "client-evt-source-vessel",
                                1L,
                                "ITEM_ADDED",
                                Map.of("id", "source-vessel", "equipmentType", "VOLUMETRIC_FLASK", "capacityMl", 100)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(2));

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "client-evt-target-vessel",
                                2L,
                                "ITEM_ADDED",
                                Map.of("id", "target-vessel", "equipmentType", "VOLUMETRIC_FLASK", "capacityMl", 50)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(3));

        SandboxEventCommand addWater = new SandboxEventCommand(
                "client-evt-add-water",
                3L,
                "MATERIAL_ADDED",
                Map.of("itemId", "source-vessel", "materialId", "COMP-H2O", "amountMl", 60, "phase", "liquid")
        );

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addWater)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(4))
                .andExpect(jsonPath("$.stateDelta.updatedItem.volumeMl").value(60.0))
                .andExpect(jsonPath("$.stateDelta.updatedItem.materialId").value("COMP-H2O"));

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addWater)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(4))
                .andExpect(jsonPath("$.stateDelta.idempotencyHit").value(true));

        SandboxEventCommand pour = new SandboxEventCommand(
                "client-evt-pour-water",
                4L,
                "POUR",
                Map.of("sourceId", "source-vessel", "targetId", "target-vessel", "amountMl", 25)
        );

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(5))
                .andExpect(jsonPath("$.stateDelta.sourceItem.volumeMl").value(35.0))
                .andExpect(jsonPath("$.stateDelta.targetItem.volumeMl").value(25.0))
                .andExpect(jsonPath("$.stateDelta.targetItem.materialId").value("COMP-H2O"));

        SandboxEventCommand tooMuchPour = new SandboxEventCommand(
                "client-evt-too-much-pour",
                5L,
                "POUR",
                Map.of("sourceId", "source-vessel", "targetId", "target-vessel", "amountMl", 40)
        );

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooMuchPour)))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/state")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(5))
                .andExpect(jsonPath("$.items[0].volumeMl").value(35.0))
                .andExpect(jsonPath("$.items[1].volumeMl").value(25.0));
    }

    @Test
    void testUndoRedoReplaysAuthoritativeState() throws Exception {
        SandboxEventCommand addVessel = new SandboxEventCommand(
                "client-evt-undo-vessel",
                1L,
                "ITEM_ADDED",
                Map.of("id", "vessel-undo", "equipmentType", "VOLUMETRIC_FLASK", "capacityMl", 100)
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVessel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(2));

        SandboxEventCommand move = new SandboxEventCommand(
                "client-evt-undo-move",
                2L,
                "ITEM_MOVED",
                Map.of("itemId", "vessel-undo", "x", 100, "y", 200)
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(move)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(3));

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/undo")
                        .header("Authorization", token)
                        .param("expectedVersion", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(4))
                .andExpect(jsonPath("$.items[0].id").value("vessel-undo"))
                .andExpect(jsonPath("$.items[0].x").doesNotExist());

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/redo")
                        .header("Authorization", token)
                        .param("expectedVersion", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateVersion").value(5))
                .andExpect(jsonPath("$.items[0].x").value(100))
                .andExpect(jsonPath("$.items[0].y").value(200));

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/undo")
                        .header("Authorization", token)
                        .param("expectedVersion", "4"))
                .andExpect(status().isConflict());
    }
}
