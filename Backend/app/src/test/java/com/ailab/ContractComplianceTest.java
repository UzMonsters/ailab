package com.ailab;

import com.ailab.admin.assets.AdminAssetService;
import com.ailab.admin.dashboard.AdminDashboardService;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.service.BookReaderService;
import com.ailab.common.api.ApiError;
import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.service.*;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.exception.VersionConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ContractComplianceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookReaderService bookReaderService;

    @Autowired
    private LearningTrackService trackService;

    @Autowired
    private LearningLevelService levelService;

    @Autowired
    private LearningAttemptService attemptService;

    @Autowired
    private LearningEvaluationService evaluationService;

    @Autowired
    private LearningCompletionService completionService;

    @Autowired
    private LearningProgressService progressService;

    @Autowired
    private AdminAssetService assetService;

    @Autowired
    private AdminDashboardService dashboardService;

    @Autowired
    private AdminLearningService adminLearningService;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private User testUser;

    @BeforeEach
    void setupUser() {
        testUser = userRepository.findByEmailIgnoreCase("compliance@ailab.local").orElseGet(() -> {
            User u = new User("compliance_user", "compliance@ailab.local", "hash123", Role.USER);
            return userRepository.save(u);
        });
    }

    @Test
    void testBookSeederAndReaderContract() {
        // Section 7.1: Book Reader Manifest
        BookDtos.PublicBookManifest manifest = bookReaderService.getManifest("chemistry-lab", "ru", null);
        assertThat(manifest).isNotNull();
        assertThat(manifest.book()).isNotNull();
        assertThat(manifest.chapters()).isNotEmpty();

        String chapterId = manifest.chapters().get(0).id();
        BookDtos.PublicChapterDetail chapter = bookReaderService.getChapter("chemistry-lab", chapterId, "ru", null);
        assertThat(chapter).isNotNull();
        assertThat(chapter.chapter().id()).isEqualTo(chapterId);
        // Section 7.2: Chapter must contain pages array
        assertThat(chapter.pages()).isNotNull();
        assertThat(chapter.pages()).isNotEmpty();
        assertThat(chapter.pages().get(0).blocks()).isNotNull();
        assertThat(chapter.pages().get(0).blocks()).isNotEmpty();
    }

    @Test
    void testLearningTrackAndLearnerFlowContract() {
        // Section 8.1: Public track map
        TrackMapResponse trackMap = trackService.getTrackMap("chemistry", "ru", testUser.getId());
        assertThat(trackMap).isNotNull();
        assertThat(trackMap.code()).isEqualTo("chemistry");
        assertThat(trackMap.title()).isEqualTo("Химия");
        assertThat(trackMap.levels()).isNotEmpty();

        LevelSummary lvlSummary = trackMap.levels().get(0);
        assertThat(lvlSummary.id()).isEqualTo("lvl_1");
        assertThat(lvlSummary.code()).isEqualTo("mixtures");
        assertThat(lvlSummary.title()).isEqualTo("Смеси");
        assertThat(lvlSummary.status()).isEqualTo("AVAILABLE");
        assertThat(lvlSummary.progress()).isNotNull();

        // Section 8.2: Level runtime definition
        LevelDefinitionDto levelDef = levelService.getPublishedLevel("lvl_1", "ru");
        assertThat(levelDef).isNotNull();
        assertThat(levelDef.id()).isEqualTo("lvl_1");
        assertThat(levelDef.title()).isEqualTo("Смеси");
        assertThat(levelDef.steps()).isNotNull();
        assertThat(levelDef.steps()).isNotEmpty();

        // Learner flow: Start Attempt
        StartAttemptResponse start = attemptService.startOrResumeAttempt(
                "lvl_1", new StartAttemptRequest(null, "ru", null), testUser.getId(), false
        );
        assertThat(start).isNotNull();
        assertThat(start.attemptId()).isNotNull();

        AttemptStateDto attempt = attemptService.getAttemptState(start.attemptId());
        assertThat(attempt.status()).isEqualTo(AttemptStatus.ACTIVE);
        assertThat(attempt.score()).isNotNull();

        // Evaluate Checkpoint
        EvaluateCheckpointRequest evalReq = new EvaluateCheckpointRequest(
                "key-1",
                attempt.stateVersion(),
                Map.of("beaker_1", Map.of("temperature", 25.0, "color", "clear"))
        );
        EvaluateCheckpointResponse evalRes = evaluationService.evaluateCheckpoint(
                attempt.attemptId(), "step_mix", evalReq
        );
        assertThat(evalRes).isNotNull();
        assertThat(evalRes.passed()).isTrue();
        assertThat(evalRes.attemptVersion()).isNotNull();

        // Complete Attempt
        CompleteAttemptResponse compRes = completionService.completeAttempt(
                attempt.attemptId(), new CompleteAttemptRequest("key-2", evalRes.attemptVersion()), "ru"
        );
        assertThat(compRes).isNotNull();
        assertThat(compRes.status()).isEqualTo("COMPLETED");
        assertThat(compRes.score()).isNotNull();
        assertThat(compRes.rewards()).isNotNull();

        // Progress check
        UserLearningProgressDto progress = progressService.getUserProgress(testUser.getId(), "chemistry");
        assertThat(progress).isNotNull();
        assertThat(progress.totalLevels()).isGreaterThanOrEqualTo(1);
        assertThat(progress.completedLevels()).isGreaterThanOrEqualTo(1);
        assertThat(progress.percent()).isEqualTo(100);
    }

    @Test
    void testAdminAssetStorageLifecycle() throws Exception {
        // Valid PNG header bytes for magic-byte inspection
        byte[] content = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52};
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String checksum = "sha256:" + HexFormat.of().formatHex(digest.digest(content));

        Map<String, Object> fileDesc = Map.of(
                "name", "flask",
                "filename", "flask.png",
                "contentType", "image/png",
                "sizeBytes", content.length,
                "checksum", checksum,
                "kind", "IMAGE"
        );

        Map<String, Object> uploadResult = assetService.generateUploadUrls(List.of(fileDesc));
        assertThat(uploadResult.get("uploads")).isInstanceOf(List.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> uploads = (List<Map<String, Object>>) uploadResult.get("uploads");
        assertThat(uploads).isNotEmpty();

        String assetId = String.valueOf(uploads.get(0).get("assetId"));
        String uploadUrl = String.valueOf(uploads.get(0).get("uploadUrl"));
        String downloadUrl = String.valueOf(uploads.get(0).get("downloadUrl"));

        // Binary upload via PUT
        HttpHeaders putHeaders = new HttpHeaders();
        putHeaders.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<byte[]> putReq = new HttpEntity<>(content, putHeaders);
        ResponseEntity<Void> putRes = restTemplate.exchange(baseUrl() + uploadUrl, HttpMethod.PUT, putReq, Void.class);
        assertThat(putRes.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Section 7.6: Complete asset upload
        Map<String, Object> completeReq = Map.of(
                "checksum", checksum,
                "width", 400,
                "height", 300,
                "alt", Map.of("ru", "Колба")
        );
        Map<String, Object> completeRes = assetService.completeAsset(assetId, completeReq);
        assertThat(completeRes.get("status")).isEqualTo("READY");
        assertThat(completeRes.get("assetId")).isEqualTo(assetId);

        // Download verified asset
        ResponseEntity<byte[]> getRes = restTemplate.getForEntity(baseUrl() + downloadUrl, byte[].class);
        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRes.getBody()).isEqualTo(content);
    }

    @Test
    void testDashboardAndReportsContract() {
        // Section 5.1: Summary
        Map<String, Object> summary = dashboardService.getSummary(null, null, "UTC", "chemistry");
        assertThat(summary).isNotNull();
        assertThat(summary.get("kpis")).isNotNull();

        // Section 5.2: Activity series
        Map<String, Object> series = dashboardService.getActivitySeries("experiments", null, null, "day", "UTC");
        assertThat(series.get("points")).isInstanceOf(List.class);

        // Section 5.3: Reports job creation
        Map<String, Object> createJob = dashboardService.createReport(Map.of("format", "CSV"));
        assertThat(createJob.get("jobId")).isNotNull();
        assertThat(createJob.get("status")).isEqualTo("QUEUED");

        String jobId = String.valueOf(createJob.get("jobId"));
        Map<String, Object> job = dashboardService.getReportJob(jobId);
        assertThat(job.get("status")).isEqualTo("READY");
        assertThat(job.get("downloadUrl")).isNotNull();

        byte[] csv = dashboardService.downloadReport(jobId);
        assertThat(csv).isNotEmpty();
        assertThat(new String(csv, StandardCharsets.UTF_8)).contains("Date,Metric,Value");
    }

    @Test
    void testOptimisticLockingAndSortValidation() {
        // Optimistic locking on learning level patch (If-Match: "W/\"999\"")
        PatchLevelRequest patchReq = new PatchLevelRequest(
                "MEDIUM", 15, null, null, Map.of("ru", Map.of("title", "Updated"))
        );

        assertThatThrownBy(() -> adminLearningService.patchLevel("lvl_1", patchReq, "\"999\""))
                .isInstanceOf(VersionConflictException.class);

        // Invalid sort parameter rejected
        assertThatThrownBy(() -> adminLearningService.listLevels(null, null, null, 0, 10, "malicious_sql_column,desc"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).contains("INVALID_QUERY");
                });
    }

    @Autowired
    private com.ailab.auth.security.JwtService jwtService;

    @Test
    void testAdminLearningEndpointsAndEquipmentCreation() {
        User adminUser = userRepository.findByEmailIgnoreCase("admin_compliance@ailab.local").orElseGet(() -> {
            User u = new User("admin_compliance", "admin_compliance@ailab.local", "hash123", Role.ADMIN);
            return userRepository.save(u);
        });
        String adminToken = "Bearer " + jwtService.issue(adminUser);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", adminToken);

        // 1. GET /api/v1/admin/learning/progress?size=10&page=0
        HttpEntity<Void> progressReq = new HttpEntity<>(headers);
        ResponseEntity<String> progressRes = restTemplate.exchange(
                baseUrl() + "/api/v1/admin/learning/progress?size=10&page=0",
                HttpMethod.GET, progressReq, String.class
        );
        assertThat(progressRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(progressRes.getBody()).contains("items");

        // 2. GET /api/v1/admin/learning/levels?size=100&sort=sortOrder%2Casc
        ResponseEntity<String> levelsRes = restTemplate.exchange(
                baseUrl() + "/api/v1/admin/learning/levels?size=100&sort=sortOrder%2Casc",
                HttpMethod.GET, progressReq, String.class
        );
        assertThat(levelsRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(levelsRes.getBody()).contains("items");

        // 3. POST /api/v1/admin/equipment
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> eqBody = Map.of(
                "code", "eq_beaker_test_" + java.util.UUID.randomUUID().toString().substring(0, 6),
                "name", "Beaker 250ml Test",
                "category", "CONTAINER",
                "ports", List.of(
                        Map.of("id", "INLET", "type", "FLUID", "direction", "INPUT")
                )
        );
        HttpEntity<Map<String, Object>> eqReq = new HttpEntity<>(eqBody, headers);
        ResponseEntity<Map> eqRes = restTemplate.postForEntity(
                baseUrl() + "/api/v1/admin/equipment",
                eqReq, Map.class
        );
        assertThat(eqRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(eqRes.getBody()).isNotNull();
        assertThat(eqRes.getBody().get("id")).isNotNull();
        assertThat(eqRes.getBody().get("createdAt")).isNotNull();
    }

    @Test
    void testUnifiedApiErrorSchema() {
        ApiError err = ApiError.ofProblem(404, "RESOURCE_NOT_FOUND", "Not Found", "Item missing", "/api/v1/test", "corr-123", Map.of());
        assertThat(err.status()).isEqualTo(404);
        assertThat(err.code()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(err.title()).isEqualTo("Not Found");
        assertThat(err.detail()).isEqualTo("Item missing");
        assertThat(err.path()).isEqualTo("/api/v1/test");
        assertThat(err.correlationId()).isEqualTo("corr-123");
        assertThat(err.fieldErrors()).isNotNull();
    }
}
