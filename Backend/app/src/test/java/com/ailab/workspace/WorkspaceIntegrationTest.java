package com.ailab.workspace;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.dto.CreateWorkspaceRequest;
import com.ailab.workspace.dto.UpdateWorkspaceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
class WorkspaceIntegrationTest {

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

    private User user1;
    private User user2;
    private String token1;
    private String token2;

    @BeforeEach
    void setUp() {
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

        userRepository.deleteAll();

        user1 = new User("User One", "user1@example.com", "hash", Role.USER);
        userRepository.save(user1);
        token1 = "Bearer " + jwtService.issue(user1);

        user2 = new User("User Two", "user2@example.com", "hash", Role.USER);
        userRepository.save(user2);
        token2 = "Bearer " + jwtService.issue(user2);
    }

    @Test
    void testWorkspaceLifecycleAndOwnershipIsolation() throws Exception {
        // 1. Create workspace for User 1
        CreateWorkspaceRequest req = new CreateWorkspaceRequest("My Organic Chemistry Lab", "chemistry");
        String responseContent = mockMvc.perform(post("/api/v1/workspaces")
                        .header("Authorization", token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("My Organic Chemistry Lab"))
                .andExpect(jsonPath("$.science").value("chemistry"))
                .andExpect(jsonPath("$.stateVersion").value(1))
                .andExpect(jsonPath("$.ownerId").value(user1.getId()))
                .andReturn().getResponse().getContentAsString();

        String wsId = objectMapper.readTree(responseContent).get("id").asText();

        // 2. User 1 can get their workspace
        mockMvc.perform(get("/api/v1/workspaces/" + wsId)
                        .header("Authorization", token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wsId));

        // 3. User 2 cannot access User 1's workspace (404/Forbidden)
        mockMvc.perform(get("/api/v1/workspaces/" + wsId)
                        .header("Authorization", token2))
                .andExpect(status().isNotFound());

        // 4. Update workspace (rename & favorite) for User 1
        UpdateWorkspaceRequest updateReq = new UpdateWorkspaceRequest("Renamed Lab", true, null, null, 1L);
        mockMvc.perform(put("/api/v1/workspaces/" + wsId)
                        .header("Authorization", token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed Lab"))
                .andExpect(jsonPath("$.isFavorite").value(true))
                .andExpect(jsonPath("$.stateVersion").value(2));

        // 5. Stale optimistic lock version conflict returns 409
        UpdateWorkspaceRequest staleUpdate = new UpdateWorkspaceRequest("Stale Update", null, null, null, 1L);
        mockMvc.perform(put("/api/v1/workspaces/" + wsId)
                        .header("Authorization", token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staleUpdate)))
                .andExpect(status().isConflict());

        // 6. List workspaces for User 1
        mockMvc.perform(get("/api/v1/workspaces")
                        .header("Authorization", token1)
                        .param("science", "chemistry")
                        .param("search", "Renamed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(wsId))
                .andExpect(jsonPath("$.total").value(1));

        // 7. Duplicate workspace
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/duplicate")
                        .header("Authorization", token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed Lab (Copy)"));

        // 8. Delete and restore
        mockMvc.perform(delete("/api/v1/workspaces/" + wsId)
                        .header("Authorization", token1))
                .andExpect(status().isOk());
    }
}
