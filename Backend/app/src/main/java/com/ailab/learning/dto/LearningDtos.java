package com.ailab.learning.dto;

import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.RewardType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class LearningDtos {

    private LearningDtos() {}

    public record TrackSummary(
            String id,
            String code,
            int order,
            String defaultLocale,
            String title,
            String description,
            LearningStatus status,
            long version
    ) {}

    public record LevelSummary(
            String id,
            String trackId,
            int levelNumber,
            int order,
            String difficulty,
            int estimatedMinutes,
            String title,
            String summary,
            LearningStatus status,
            boolean isComingSoon,
            boolean isLocked,
            Long publishedVersion
    ) {}

    public record TrackMapResponse(
            TrackSummary track,
            List<LevelSummary> levels
    ) {}

    public record ScenarioBindingDto(
            String scenarioId,
            Long catalogVersion,
            List<String> availableEquipmentIds,
            List<String> availableMaterialIds,
            Map<String, Object> initialState
    ) {}

    public record CheckpointDefinitionDto(
            String factType,
            Map<String, Object> source,
            Map<String, Object> target,
            Map<String, Object> parameters
    ) {}

    public record GuideTargetDto(
            Integer level,
            String kind,
            String id,
            String catalogCode,
            String itemId,
            String portId,
            String sourcePortType,
            String targetPortType,
            String placement,
            String text,
            Integer sequence
    ) {}

    public record StepDefinitionDto(
            String id,
            int order,
            String type,
            Map<String, Object> translations,
            CheckpointDefinitionDto checkpoint,
            List<GuideTargetDto> guideTargets
    ) {}

    public record LevelRequirementsDto(
            List<String> prerequisiteLevelIds,
            List<String> requiredBadgeIds,
            Boolean allowReplay,
            Integer maxAttempts
    ) {}

    public record LevelRewardsDto(
            String badgeId,
            List<String> unlockLevelIds,
            List<String> unlockEquipmentIds,
            List<String> unlockMaterialIds,
            List<String> unlockBookChapterIds
    ) {}

    public record LevelDefinitionDto(
            String id,
            String trackId,
            int levelNumber,
            int order,
            String difficulty,
            int estimatedMinutes,
            LearningStatus status,
            long version,
            String title,
            String summary,
            String goal,
            List<String> prerequisites,
            LevelRequirementsDto requirements,
            List<String> availableEquipment,
            List<String> availableMaterials,
            ScenarioBindingDto scenario,
            List<StepDefinitionDto> steps,
            LevelRewardsDto rewards,
            Map<String, Object> translations
    ) {}

    public record StartAttemptRequest(
            String clientAttemptId,
            String locale,
            String workspaceId
    ) {}

    public record StartAttemptResponse(
            String attemptId,
            String experimentId,
            String currentStep,
            long stateVersion
    ) {}

    public record CompletedStepDto(
            String stepId,
            Instant completedAt,
            Map<String, Object> evaluationDetails
    ) {}

    public record HintUsageDto(
            String stepId,
            int level,
            Instant requestedAt
    ) {}

    public record AttemptStateDto(
            String attemptId,
            String levelId,
            long levelVersion,
            String experimentId,
            String workspaceId,
            long stateVersion,
            AttemptStatus status,
            int currentStepIndex,
            String currentStep,
            List<CompletedStepDto> completedSteps,
            List<HintUsageDto> hintUsage,
            Instant startedAt,
            Instant completedAt,
            Instant updatedAt
    ) {}

    public record SemanticEventRequest(
            String eventId,
            String type,
            Map<String, Object> payload,
            long experimentStateVersion
    ) {}

    public record SemanticEventResponse(
            boolean accepted,
            List<String> evaluatedCheckpointIds
    ) {}

    public record EvaluateCheckpointRequest(
            String idempotencyKey,
            long stateVersion
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EvaluateCheckpointResponse(
            boolean accepted,
            String reason,
            String nextStep
    ) {}

    public record TargetDescriptor(
            String kind,
            String itemId,
            String portId,
            String catalogCode,
            String sourcePortType,
            String targetPortType,
            String actionType
    ) {}

    public record GuidePayload(
            TargetDescriptor target,
            String text,
            String placement,
            int sequence
    ) {}

    public record HintRequest(
            int level,
            String currentStepId
    ) {}

    public record NextLevelInfo(
            String id,
            int levelNumber,
            String title
    ) {}

    public record UnlockedRewardDto(
            String badgeId,
            List<String> unlockLevelIds,
            List<String> unlockEquipmentIds,
            List<String> unlockMaterialIds,
            List<String> unlockBookChapterIds
    ) {}

    public record CompleteAttemptRequest(
            String idempotencyKey,
            long stateVersion
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CompleteAttemptResponse(
            Instant completedAt,
            NextLevelInfo nextLevel,
            UnlockedRewardDto reward
    ) {}

    public record UserLearningProgressDto(
            String userId,
            String trackId,
            List<String> completedLevelIds,
            String currentLevelId,
            List<String> badges,
            List<String> unlockedEquipment,
            List<String> unlockedMaterials,
            List<String> unlockedBookChapters,
            Map<String, Object> stats
    ) {}

    public record LearningOverviewResponse(
            Map<String, Long> levels,
            long attempts,
            double completionRate,
            double averageDurationSeconds,
            long hintUsage
    ) {}

    public record CreateTrackRequest(
            String code,
            Integer order,
            String defaultLocale,
            Map<String, Object> translations
    ) {}

    public record TrackDraftResponse(
            String id,
            String code,
            int order,
            String defaultLocale,
            LearningStatus status,
            long version,
            Map<String, Object> translations
    ) {}

    public record CreateLevelRequest(
            String trackId,
            Integer levelNumber,
            Integer order,
            String difficulty,
            Integer estimatedMinutes,
            Map<String, Object> translations
    ) {}

    public record LevelDraftResponse(
            String id,
            String trackId,
            int levelNumber,
            int order,
            String difficulty,
            int estimatedMinutes,
            LearningStatus status,
            long version,
            Map<String, Object> translations
    ) {}

    public record LevelEditorDocument(
            String id,
            String trackId,
            int levelNumber,
            int order,
            String difficulty,
            int estimatedMinutes,
            LearningStatus status,
            long version,
            Long publishedVersion,
            List<String> prerequisites,
            LevelRequirementsDto requirements,
            List<String> availableEquipment,
            List<String> availableMaterials,
            ScenarioBindingDto scenario,
            List<StepDefinitionDto> steps,
            LevelRewardsDto rewards,
            Map<String, Object> translations
    ) {}

    public record PatchLevelRequest(
            String difficulty,
            Integer estimatedMinutes,
            Map<String, Object> content,
            LevelRequirementsDto requirements,
            Map<String, Object> translations
    ) {}

    public record SaveStepsRequest(
            long version,
            List<StepDefinitionDto> steps
    ) {}

    public record SaveStepsResponse(
            long version,
            List<StepDefinitionDto> steps,
            List<String> validationWarnings
    ) {}

    public record SaveScenarioRequest(
            long version,
            String scenarioId,
            Long catalogVersion,
            List<String> availableEquipmentIds,
            List<String> availableMaterialIds,
            Map<String, Object> initialState
    ) {}

    public record SaveScenarioResponse(
            long version,
            ScenarioBindingDto scenarioBinding
    ) {}

    public record SaveRequirementsRequest(
            long version,
            List<String> prerequisiteLevelIds,
            List<String> requiredBadgeIds,
            Boolean allowReplay,
            Integer maxAttempts
    ) {}

    public record SaveRewardsRequest(
            long version,
            String badgeId,
            List<String> unlockLevelIds,
            List<String> unlockEquipmentIds,
            List<String> unlockMaterialIds,
            List<String> unlockBookChapterIds
    ) {}

    public record SaveTranslationsRequest(
            String title,
            String summary,
            String goal,
            Map<String, Object> steps,
            Map<String, Object> reward
    ) {}

    public record SaveTranslationsResponse(
            String locale,
            double completeness,
            List<String> missingKeys,
            long version
    ) {}

    public record ValidationErrorDto(
            String path,
            String code,
            String message
    ) {}

    public record ValidationReportDto(
            boolean valid,
            List<ValidationErrorDto> errors,
            List<String> warnings
    ) {}

    public record PreviewAttemptRequest(
            long version,
            String locale
    ) {}

    public record PreviewAttemptResponse(
            String previewAttemptId,
            String sandboxUrl,
            Instant expiresAt
    ) {}

    public record PublishLevelRequest(
            long version,
            String idempotencyKey,
            String releaseNote
    ) {}

    public record PublishResultDto(
            long publishedVersion,
            Instant publishedAt
    ) {}

    public record ArchiveLevelRequest(
            String reason
    ) {}

    public record ArchiveResultDto(
            String status
    ) {}

    public record LevelAnalyticsResponse(
            long starts,
            long completions,
            double completionRate,
            double medianDurationSeconds,
            Map<String, Long> dropOffByStep,
            Map<String, Long> hintsByStep,
            Map<String, Long> failures
    ) {}

    public record ChapterDto(
            String id,
            String trackId,
            int order,
            List<String> levelIds,
            LearningStatus status,
            Map<String, Object> translations
    ) {}

    public record CreateChapterRequest(
            String trackId,
            Integer order,
            List<String> levelIds,
            Map<String, Object> translations
    ) {}

    public record TaskDto(
            String id,
            String code,
            String type,
            Map<String, Object> validationRule,
            Map<String, Object> guideTemplate,
            Map<String, Object> translations
    ) {}

    public record CreateTaskRequest(
            String code,
            String type,
            Map<String, Object> validationRule,
            Map<String, Object> guideTemplate,
            Map<String, Object> translations
    ) {}

    public record RewardAdminDto(
            String id,
            String code,
            RewardType type,
            String assetId,
            Map<String, Object> criteria,
            Map<String, Object> translations
    ) {}

    public record CreateRewardRequest(
            String code,
            RewardType type,
            String assetId,
            Map<String, Object> criteria,
            Map<String, Object> translations
    ) {}

    public record AdminProgressItemDto(
            String user,
            String level,
            String status,
            int attempts,
            double duration,
            Instant lastActivityAt
    ) {}

    public record AdminProgressPageResponse(
            List<AdminProgressItemDto> items,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record ResetProgressRequest(
            String trackId,
            String levelId,
            String reason
    ) {}

    public record ResetProgressResponse(
            String resetJobId,
            String status
    ) {}

    public record LocalizationItemDto(
            String entityId,
            String locale,
            double completeness,
            List<String> missingKeys
    ) {}

    public record LocalizationPageResponse(
            List<LocalizationItemDto> items,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record TrackPageResponse(
            List<TrackSummary> items,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record LevelPageResponse(
            List<LevelSummary> items,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}
}
