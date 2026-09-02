package com.ailab.learning.controller;

import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.service.AdminLearningAnalyticsService;
import com.ailab.learning.service.AdminLearningService;
import com.ailab.learning.service.AdminLearningValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/learning")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Learning", description = "Learning tracks, levels, scenarios, validation, publishing, and analytics management")
public class AdminLearningController {

    private final AdminLearningService adminService;
    private final AdminLearningValidationService validationService;
    private final AdminLearningAnalyticsService analyticsService;

    public AdminLearningController(
            AdminLearningService adminService,
            AdminLearningValidationService validationService,
            AdminLearningAnalyticsService analyticsService
    ) {
        this.adminService = adminService;
        this.validationService = validationService;
        this.analyticsService = analyticsService;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            return "admin";
        }
        return auth.getName();
    }

    @GetMapping("/overview")
    @Operation(summary = "Get Learning Admin Overview")
    public LearningOverviewResponse getOverview(
            @RequestParam(required = false) String track,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        return analyticsService.getOverview(track, from, to);
    }

    @GetMapping("/tracks")
    @Operation(summary = "List Learning Tracks")
    public TrackPageResponse listTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) LearningStatus status
    ) {
        return adminService.listTracks(page, size, status);
    }

    @PostMapping("/tracks")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Learning Track")
    public TrackDraftResponse createTrack(@RequestBody CreateTrackRequest request) {
        return adminService.createTrack(request);
    }

    @GetMapping("/levels")
    @Operation(summary = "List Learning Levels")
    public LevelPageResponse listLevels(
            @RequestParam(required = false) String trackId,
            @RequestParam(required = false) LearningStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sortOrder,asc") String sort
    ) {
        return adminService.listLevels(trackId, status, q, page, size, sort);
    }

    @PostMapping("/levels")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Level Draft")
    public LevelDraftResponse createLevel(@RequestBody CreateLevelRequest request) {
        return adminService.createLevel(request);
    }

    @GetMapping("/levels/{id}")
    @Operation(summary = "Get Level Editor Document")
    public LevelEditorDocument getLevel(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "steps,scenario,requirements,rewards,translations") String include
    ) {
        return adminService.getLevelEditorDocument(id, include);
    }

    @PatchMapping("/levels/{id}")
    @Operation(summary = "Patch Level Draft")
    public LevelDraftResponse patchLevel(
            @PathVariable String id,
            @RequestBody PatchLevelRequest request,
            @RequestHeader(value = "If-Match", required = false) String ifMatch
    ) {
        return adminService.patchLevel(id, request, ifMatch);
    }

    @PutMapping("/levels/{id}/steps")
    @Operation(summary = "Save Level Steps Atomically")
    public SaveStepsResponse saveSteps(
            @PathVariable String id,
            @RequestBody SaveStepsRequest request
    ) {
        return adminService.saveSteps(id, request);
    }

    @PutMapping("/levels/{id}/scenario")
    @Operation(summary = "Save Level Scenario Binding")
    public SaveScenarioResponse saveScenario(
            @PathVariable String id,
            @RequestBody SaveScenarioRequest request
    ) {
        return adminService.saveScenario(id, request);
    }

    @PutMapping("/levels/{id}/requirements")
    @Operation(summary = "Save Level Requirements and Prerequisites")
    public LevelRequirementsDto saveRequirements(
            @PathVariable String id,
            @RequestBody SaveRequirementsRequest request
    ) {
        return adminService.saveRequirements(id, request);
    }

    @PutMapping("/levels/{id}/rewards")
    @Operation(summary = "Save Level Rewards (XP Excluded)")
    public LevelRewardsDto saveRewards(
            @PathVariable String id,
            @RequestBody SaveRewardsRequest request
    ) {
        return adminService.saveRewards(id, request);
    }

    @PutMapping("/levels/{id}/translations/{locale}")
    @Operation(summary = "Save Level Translations")
    public SaveTranslationsResponse saveTranslations(
            @PathVariable String id,
            @PathVariable String locale,
            @RequestBody SaveTranslationsRequest request
    ) {
        return adminService.saveTranslations(id, locale, request);
    }

    @PostMapping("/levels/{id}/validate")
    @Operation(summary = "Validate Level Structure, Cycles and Translations")
    public ValidationReportDto validateLevel(
            @PathVariable String id,
            @RequestBody(required = false) ValidateRequest request
    ) {
        Long version = request != null ? request.version() : null;
        return validationService.validateLevel(id, version);
    }

    public record ValidateRequest(Long version) {}

    @PostMapping("/levels/{id}/preview-attempts")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Isolated Admin Preview Attempt")
    public PreviewAttemptResponse createPreviewAttempt(
            @PathVariable String id,
            @RequestBody(required = false) PreviewAttemptRequest request
    ) {
        return adminService.createPreviewAttempt(id, request, getCurrentUserId());
    }

    @PostMapping("/levels/{id}/publish")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Publish Immutable Level Version")
    public PublishResultDto publishLevel(
            @PathVariable String id,
            @RequestBody(required = false) PublishLevelRequest request
    ) {
        return adminService.publishLevel(id, request, getCurrentUserId(), "Administrator");
    }

    @PostMapping("/levels/{id}/archive")
    @Operation(summary = "Archive Level")
    public ArchiveResultDto archiveLevel(
            @PathVariable String id,
            @RequestBody(required = false) ArchiveLevelRequest request
    ) {
        return adminService.archiveLevel(id, request);
    }

    @GetMapping("/levels/{id}/analytics")
    @Operation(summary = "Get Level Analytics")
    public LevelAnalyticsResponse getLevelAnalytics(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        return analyticsService.getLevelAnalytics(id, from, to);
    }

    @GetMapping("/chapters")
    @Operation(summary = "List Learning Chapters")
    public List<ChapterDto> listChapters(@RequestParam(required = false) String trackId) {
        return adminService.listChapters(trackId);
    }

    @PostMapping("/chapters")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Learning Chapter")
    public ChapterDto createChapter(@RequestBody CreateChapterRequest request) {
        return adminService.createChapter(request);
    }

    @GetMapping("/tasks")
    @Operation(summary = "List Reusable Tasks")
    public List<TaskDto> listTasks() {
        return adminService.listTasks();
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Reusable Task")
    public TaskDto createTask(@RequestBody CreateTaskRequest request) {
        return adminService.createTask(request);
    }

    @GetMapping("/rewards")
    @Operation(summary = "List Learning Rewards (XP Excluded)")
    public List<RewardAdminDto> listRewards() {
        return adminService.listRewards();
    }

    @PostMapping("/rewards")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Learning Reward (XP Excluded)")
    public RewardAdminDto createReward(@RequestBody CreateRewardRequest request) {
        return adminService.createReward(request);
    }

    @GetMapping("/progress")
    @Operation(summary = "List User Progress across Tracks and Levels")
    public AdminProgressPageResponse listProgress(
            @RequestParam(required = false) String trackId,
            @RequestParam(required = false) String levelId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return adminService.listProgress(trackId, levelId, status, q, page, size);
    }

    @PostMapping("/progress/{userId}/reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Reset User Progress with Audit Trail")
    public ResetProgressResponse resetUserProgress(
            @PathVariable String userId,
            @RequestBody(required = false) ResetProgressRequest request
    ) {
        return adminService.resetUserProgress(userId, request, getCurrentUserId());
    }

    @GetMapping("/localization")
    @Operation(summary = "Get Localization Completeness Summary")
    public LocalizationPageResponse getLocalization(
            @RequestParam(required = false, defaultValue = "level") String entityType,
            @RequestParam(required = false, defaultValue = "ru") String locale,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return adminService.getLocalizationSummary(entityType, locale, status, page, size);
    }
}
