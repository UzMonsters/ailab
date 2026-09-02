package com.ailab.learning.controller;

import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning")
@Tag(name = "Learning Engine", description = "Public Learning Tracks, Levels, Attempts, Semantic Checkpoints, Guide, and Completion APIs")
public class PublicLearningController {

    private final LearningTrackService trackService;
    private final LearningLevelService levelService;
    private final LearningAttemptService attemptService;
    private final LearningEvaluationService evaluationService;
    private final LearningCompletionService completionService;

    public PublicLearningController(
            LearningTrackService trackService,
            LearningLevelService levelService,
            LearningAttemptService attemptService,
            LearningEvaluationService evaluationService,
            LearningCompletionService completionService
    ) {
        this.trackService = trackService;
        this.levelService = levelService;
        this.attemptService = attemptService;
        this.evaluationService = evaluationService;
        this.completionService = completionService;
    }

    private String getOptionalUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            return null;
        }
        return auth.getName();
    }

    @GetMapping("/tracks/chemistry")
    @Operation(summary = "Get Chemistry Track Map")
    public TrackMapResponse getChemistryTrackMap(@RequestParam(required = false, defaultValue = "ru") String locale) {
        return trackService.getTrackMap("chemistry", locale, getOptionalUserId());
    }

    @GetMapping("/tracks/{codeOrId}")
    @Operation(summary = "Get Track Map by Code or ID")
    public TrackMapResponse getTrackMap(
            @PathVariable String codeOrId,
            @RequestParam(required = false, defaultValue = "ru") String locale
    ) {
        return trackService.getTrackMap(codeOrId, locale, getOptionalUserId());
    }

    @GetMapping("/levels/{id}")
    @Operation(summary = "Get Published Level Definition")
    public LevelDefinitionDto getPublishedLevel(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "ru") String locale
    ) {
        return levelService.getPublishedLevel(id, locale);
    }

    @PostMapping("/levels/{id}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start or Resume Level Attempt")
    public StartAttemptResponse startOrResumeAttempt(
            @PathVariable String id,
            @RequestBody(required = false) StartAttemptRequest request
    ) {
        return attemptService.startOrResumeAttempt(id, request, getOptionalUserId(), false);
    }

    @GetMapping("/attempts/{id}")
    @Operation(summary = "Get Attempt State")
    public AttemptStateDto getAttemptState(@PathVariable String id) {
        return attemptService.getAttemptState(id);
    }

    @PostMapping("/attempts/{id}/events")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Submit Semantic Laboratory Event for Evaluation")
    public SemanticEventResponse receiveSemanticEvent(
            @PathVariable String id,
            @RequestBody SemanticEventRequest request
    ) {
        return evaluationService.receiveSemanticEvent(id, request);
    }

    @PostMapping("/attempts/{id}/checkpoints/{checkpointId}/evaluate")
    @Operation(summary = "Evaluate Checkpoint Condition")
    public EvaluateCheckpointResponse evaluateCheckpoint(
            @PathVariable String id,
            @PathVariable String checkpointId,
            @RequestBody(required = false) EvaluateCheckpointRequest request
    ) {
        return evaluationService.evaluateCheckpoint(id, checkpointId, request);
    }

    @GetMapping("/attempts/{id}/guide")
    @Operation(summary = "Get Active Guide Target")
    public GuidePayload getGuide(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "hint") String mode,
            @RequestParam(required = false, defaultValue = "ru") String locale
    ) {
        return attemptService.getGuide(id, mode, locale);
    }

    @PostMapping("/attempts/{id}/hint-requests")
    @Operation(summary = "Request Detailed Hint")
    public GuidePayload requestHint(
            @PathVariable String id,
            @RequestBody HintRequest request,
            @RequestParam(required = false, defaultValue = "ru") String locale
    ) {
        return attemptService.requestHint(id, request, locale);
    }

    @PostMapping("/attempts/{id}/complete")
    @Operation(summary = "Complete Attempt Idempotently")
    public CompleteAttemptResponse completeAttempt(
            @PathVariable String id,
            @RequestBody(required = false) CompleteAttemptRequest request,
            @RequestParam(required = false, defaultValue = "ru") String locale
    ) {
        return completionService.completeAttempt(id, request, locale);
    }
}
