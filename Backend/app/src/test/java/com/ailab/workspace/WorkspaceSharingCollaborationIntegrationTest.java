package com.ailab.workspace;

import com.ailab.auth.security.JwtService;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.dto.*;
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

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
class WorkspaceSharingCollaborationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User owner;
    private User collaborator;
    private String ownerToken;
    private String collabToken;
    private String wsId;

    @BeforeEach
    void setUp() throws Exception {
        owner = new User("share_owner_" + System.currentTimeMillis(), "owner" + System.currentTimeMillis() + "@jasscience.dev", "hashed_pwd", Role.USER);
        userRepository.save(owner);
        collaborator = new User("share_collab_" + System.currentTimeMillis(), "collab" + System.currentTimeMillis() + "@jasscience.dev", "hashed_pwd", Role.USER);
        userRepository.save(collaborator);

        ownerToken = "Bearer " + jwtService.issue(owner);
        collabToken = "Bearer " + jwtService.issue(collaborator);

        CreateWorkspaceRequest req = new CreateWorkspaceRequest("Shared Lab Collaboration", "chemistry");
        MvcResult res = mockMvc.perform(post("/api/v1/workspaces")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> map = objectMapper.readValue(res.getResponse().getContentAsString(), Map.class);
        wsId = (String) map.get("id");
    }

    @Test
    void testMembershipRolesAndLastOwnerProtection() throws Exception {
        // Owner has full capabilities
        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/permissions")
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.capabilities", hasItem("MANAGE_ACCESS")));

        // Invite collaborator
        CreateInvitationRequest invReq = new CreateInvitationRequest("collab@jasscience.dev", "EDITOR", null, "Join my lab");
        MvcResult invRes = mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/invitations")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rawToken").isNotEmpty())
                .andReturn();

        Map<?, ?> invMap = objectMapper.readValue(invRes.getResponse().getContentAsString(), Map.class);
        String rawToken = (String) invMap.get("rawToken");

        // Collaborator accepts invitation
        mockMvc.perform(post("/api/v1/workspace-invitations/" + rawToken + "/accept")
                        .header("Authorization", collabToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AcceptInvitationRequest("Collab User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(true));

        // List members shows both
        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/members")
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Attempt to demote/remove sole owner -> 422 LAST_OWNER
        mockMvc.perform(patch("/api/v1/workspaces/" + wsId + "/members/" + owner.getId())
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("role", "VIEWER"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LAST_OWNER"));
    }

    @Test
    void testOpaqueShareLinksAndGuestResolution() throws Exception {
        // Create password-protected share link
        CreateShareLinkRequest linkReq = new CreateShareLinkRequest("VIEWER", Instant.now().plusSeconds(3600), "secret123", 5, true, true);
        MvcResult linkRes = mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/share-links")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.hasPassword").value(true))
                .andReturn();

        Map<?, ?> linkMap = objectMapper.readValue(linkRes.getResponse().getContentAsString(), Map.class);
        String rawToken = (String) linkMap.get("token");

        // 1. Resolve without password -> 401 SHARE_PASSWORD_REQUIRED
        mockMvc.perform(post("/api/v1/shared-workspaces/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResolveShareLinkRequest(rawToken, null))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("SHARE_PASSWORD_REQUIRED"));

        // 2. Resolve with wrong password -> 403 SHARE_PASSWORD_INVALID
        mockMvc.perform(post("/api/v1/shared-workspaces/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResolveShareLinkRequest(rawToken, "wrong_pwd"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("SHARE_PASSWORD_INVALID"));

        // 3. Resolve with correct password -> Success and returns temporary share session token
        mockMvc.perform(post("/api/v1/shared-workspaces/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResolveShareLinkRequest(rawToken, "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workspaceId").value(wsId))
                .andExpect(jsonPath("$.role").value("VIEWER"))
                .andExpect(jsonPath("$.shareSessionToken").isNotEmpty());
    }

    @Test
    void testPersistentTeamChat() throws Exception {
        // Send message with clientMessageId
        SendChatMessageRequest chatReq = new SendChatMessageRequest("client-msg-01", "Hello team! Starting titration.", null, Map.of("stateVersion", 1));
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/chat/messages")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.body").value("Hello team! Starting titration."));

        // Duplicate clientMessageId -> Idempotent response
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/chat/messages")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientMessageId").value("client-msg-01"));

        // Query chat messages
        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/chat/messages")
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].body").value("Hello team! Starting titration."));
    }

    @Test
    void testAnchoredCommentThreads() throws Exception {
        // Create comment thread anchored to beaker-1
        CreateCommentThreadRequest threadReq = new CreateCommentThreadRequest(
                "Check this meniscus level carefully",
                Map.of("itemId", "beaker-1", "x", 0.5, "y", 0.3, "stateVersion", 1)
        );
        MvcResult threadRes = mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/comments")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(threadReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.replies", hasSize(1)))
                .andReturn();

        Map<?, ?> threadMap = objectMapper.readValue(threadRes.getResponse().getContentAsString(), Map.class);
        String threadId = (String) threadMap.get("id");

        // Add reply
        AddCommentReplyRequest replyReq = new AddCommentReplyRequest("reply-01", "Level confirmed at 50 mL.");
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/comments/" + threadId + "/replies")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.replies", hasSize(2)));

        // Resolve thread
        mockMvc.perform(patch("/api/v1/workspaces/" + wsId + "/comments/" + threadId)
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateCommentThreadStatusRequest("RESOLVED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }
}
