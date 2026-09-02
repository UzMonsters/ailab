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

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "local"})
class WorkspaceMeasurementsAndPreviewsIntegrationTest {

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
        user = new User("measure_tester_" + System.currentTimeMillis(), "measure_tester" + System.currentTimeMillis() + "@jasscience.dev", "hashed_pwd", Role.USER);
        userRepository.save(user);
        token = "Bearer " + jwtService.issue(user);

        CreateWorkspaceRequest req = new CreateWorkspaceRequest("Measurements & Previews Workspace", "chemistry");
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
    void testSensorMeasurementAndQueryAPI() throws Exception {
        // 1. Add beaker and pH meter
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "init-b-ph", 1L, "ITEM_ADDED",
                        Map.of("id", "beaker-ph", "equipmentType", "CONTAINER", "profileId", "beaker-250ml", "capacityMl", 250.0)
                )))).andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "init-ph", 2L, "ITEM_ADDED",
                        Map.of("id", "sensor-ph", "equipmentType", "SENSOR", "profileId", "ph-meter")
                )))).andExpect(status().isOk());

        // 2. Add HCl (acidic)
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "add-acid", 3L, "MATERIAL_ADDED",
                        Map.of("itemId", "beaker-ph", "materialId", "HCl", "amountMl", 50.0, "phase", "LIQUID")
                )))).andExpect(status().isOk());

        // 3. Take pH MEASURE
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                                "measure-ph", 4L, "MEASURE",
                                Map.of("sensorItemId", "sensor-ph", "targetItemId", "beaker-ph", "kind", "PH")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements", hasSize(1)))
                .andExpect(jsonPath("$.measurements[0].kind").value("PH"))
                .andExpect(jsonPath("$.measurements[0].value").value(1.2))
                .andExpect(jsonPath("$.checkpointFacts[?(@.type == 'MEASUREMENT_RECORDED')]").isNotEmpty());

        // 4. Query measurements time series endpoint
        mockMvc.perform(get("/api/v1/workspaces/" + wsId + "/measurements?kind=PH")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].kind").value("PH"))
                .andExpect(jsonPath("$[0].value").value(1.2));
    }

    @Test
    void testVersionedPreviewPipelineAndStaleCheck() throws Exception {
        // 1. Request Upload URLs at version 1
        PreviewUploadUrlsRequest req = new PreviewUploadUrlsRequest(1L, List.of(
                new PreviewUploadUrlsRequest.VariantRequest("DARK", "image/webp", 960, 540, "sha256_dark"),
                new PreviewUploadUrlsRequest.VariantRequest("LIGHT", "image/webp", 960, 540, "sha256_light")
        ));
        MvcResult res = mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/preview-upload-urls")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previewId").isNotEmpty())
                .andExpect(jsonPath("$.uploads", hasSize(2)))
                .andReturn();

        PreviewUploadUrlsResponse uploadResp = objectMapper.readValue(res.getResponse().getContentAsString(), PreviewUploadUrlsResponse.class);
        String prevId = uploadResp.previewId();

        // 2. Complete preview successfully
        CompletePreviewRequest completeReq = new CompletePreviewRequest(
                1L,
                List.of(
                        new CompletePreviewRequest.AssetResult("DARK", "asset-dark", "/storage/previews/" + wsId + "_dark.webp", "sha256_dark"),
                        new CompletePreviewRequest.AssetResult("LIGHT", "asset-light", "/storage/previews/" + wsId + "_light.webp", "sha256_light")
                ),
                "chemistry-default-01"
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/previews/" + prevId + "/complete")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.variants.dark.url").value("/storage/previews/" + wsId + "_dark.webp"))
                .andExpect(jsonPath("$.variants.light.url").value("/storage/previews/" + wsId + "_light.webp"));

        // 3. Mutate workspace state to advance version to 2
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/events")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SandboxEventCommand(
                        "mut-b", 1L, "ITEM_ADDED",
                        Map.of("id", "beaker-v2", "equipmentType", "CONTAINER", "profileId", "beaker-250ml")
                )))).andExpect(status().isOk());

        // 4. Stale completion (completing with old version 1 when workspace is at version 2) -> 409 STALE_PREVIEW
        CompletePreviewRequest staleReq = new CompletePreviewRequest(
                1L,
                List.of(new CompletePreviewRequest.AssetResult("DARK", "asset-dark", "/storage/previews/stale.webp", "sha")),
                "chemistry-default-01"
        );
        mockMvc.perform(post("/api/v1/workspaces/" + wsId + "/previews/prev_stale/complete")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staleReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STALE_PREVIEW"));
    }
}
