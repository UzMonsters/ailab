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

    public record LevelProgressSummary(
            int percent,
            boolean completed
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LevelSummary(
            String id,
            String code,
            String title,
            Integer position,
            String trackId,
            int levelNumber,
            int order,
            String difficulty,
            int estimatedMinutes,
            String summary,
            String status,
            LevelProgressSummary progress,
            boolean isComingSoon,
            boolean isLocked,
            Long publishedVersion
    ) {
        public LevelSummary(
                String id,
                String code,
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
        ) {
            this(
                    id,
                    code != null ? code : id,
                    title,
                    levelNumber,
                    trackId,
                    levelNumber,
                    order,
                    difficulty,
                    estimatedMinutes,
                    summary,
                    status != null ? (status == LearningStatus.PUBLISHED ? (isLocked ? "LOCKED" : "AVAILABLE") : status.name()) : "AVAILABLE",
                    new LevelProgressSummary(0, false),
                    isComingSoon,
                    isLocked,
                    publishedVersion
            );
        }

        public LevelSummary(
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
        ) {
            this(id, id, trackId, levelNumber, order, difficulty, estimatedMinutes, title, summary, status, isComingSoon, isLocked, publishedVersion);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TrackMapResponse(
            String id,
            String code,
            String title,
            String locale,
            List<LevelSummary> levels,
            TrackSummary track
    ) {
        public TrackMapResponse(TrackSummary track, List<LevelSummary> levels) {
            this(
                    track != null ? track.id() : null,
                    track != null ? track.code() : null,
                    track != null ? track.title() : null,
                    track != null ? track.defaultLocale() : "ru",
                    levels,
                    track
            );
        }
    }

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LevelDefinitionDto(
            String id,
            String code,
            String title,
            String description,
            String summary,
            String goal,
            String trackId,
            int levelNumber,
            int order,
            String difficulty,
            int estimatedMinutes,
            LearningStatus status,
            long version,
            List<String> prerequisites,
            LevelRequirementsDto requirements,
            List<String> availableEquipment,
            List<String> availableMaterials,
            ScenarioBindingDto scenario,
            List<StepDefinitionDto> steps,
            LevelRewardsDto rewards,
            Map<String, Object> translations
    ) {
        public LevelDefinitionDto(
                String id, String trackId, int levelNumber, int order, String difficulty, int estimatedMinutes,
                LearningStatus status, long version, String title, String summary, String goal, List<String> prerequisites,
                LevelRequirementsDto requirements, List<String> availableEquipment, List<String> availableMaterials,
                ScenarioBindingDto scenario, List<StepDefinitionDto> steps, LevelRewardsDto rewards, Map<String, Object> translations
        ) {
            this(id, id, title, summary, summary, goal, trackId, levelNumber, order, difficulty, estimatedMinutes,
                    status, version, prerequisites, requirements, availableEquipment, availableMaterials, scenario, steps, rewards, translations);
        }
    }

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AttemptStateDto(
            String id,
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
            Integer score,
            Instant startedAt,
            Instant completedAt,
            Instant updatedAt
    ) {
        public AttemptStateDto(
                String attemptId, String levelId, long levelVersion, String experimentId, String workspaceId,
                long stateVersion, AttemptStatus status, int currentStepIndex, String currentStep,
                List<CompletedStepDto> completedSteps, List<HintUsageDto> hintUsage, Instant startedAt,
                Instant completedAt, Instant updatedAt
        ) {
            this(attemptId, attemptId, levelId, levelVersion, experimentId, workspaceId, stateVersion, status,
                    currentStepIndex, currentStep, completedSteps, hintUsage, 100, startedAt, completedAt, updatedAt);
        }
    }

    public record SemanticEventRequest(
            String eventId,
            String type,
            String stepId,
            Map<String, Object> payload,
            Long expectedVersion,
            long experimentStateVersion,
            Instant occurredAt
    ) {
        public SemanticEventRequest(String eventId, String type, Map<String, Object> payload, long experimentStateVersion) {
            this(eventId, type, null, payload, null, experimentStateVersion, Instant.now());
        }
    }

    public record SemanticEventResponse(
            boolean accepted,
            List<String> evaluatedCheckpointIds
    ) {}

    public record EvaluateCheckpointRequest(
            String idempotencyKey,
            long stateVersion,
            Map<String, Object> evidence
    ) {
        public EvaluateCheckpointRequest(String idempotencyKey, long stateVersion) {
            this(idempotencyKey, stateVersion, Map.of());
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EvaluateCheckpointResponse(
            boolean passed,
            boolean accepted,
            Integer score,
            String feedback,
            String reason,
            String nextStep,
            Long attemptVersion
    ) {
        public EvaluateCheckpointResponse(boolean accepted, String reason, String nextStep) {
            this(accepted, accepted, accepted ? 100 : 0, reason, reason, nextStep, null);
        }

        public EvaluateCheckpointResponse(boolean passed, Integer score, String feedback, String nextStep, Long attemptVersion) {
            this(passed, passed, score, feedback, feedback, nextStep, attemptVersion);
        }
    }

    public record TargetDescriptor(
            String kind,
            String itemId,
            String portId,
            String catalogCode,
            String sourcePortType,
            String targetPortType,
            String actionType
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GuidePayload(
            String title,
            String body,
            List<Map<String, Object>> actions,
            String currentStep,
            TargetDescriptor target,
            String text,
            String placement,
            Integer sequence
    ) {
        public GuidePayload(TargetDescriptor target, String text, String placement, int sequence) {
            this(text, text, List.of(), null, target, text, placement, sequence);
        }

        public GuidePayload(String title, String body, List<Map<String, Object>> actions, String currentStep) {
            this(title, body, actions != null ? actions : List.of(), currentStep, null, body, "top", 1);
        }
    }

    public record HintRequest(
            int level,
            String currentStepId,
            String stepId,
            String reason
    ) {
        public HintRequest(int level, String currentStepId) {
            this(level, currentStepId, currentStepId, null);
        }
    }

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
            String status,
            Integer score,
            Object rewards,
            Instant completedAt,
            NextLevelInfo nextLevel,
            UnlockedRewardDto reward
    ) {
        public CompleteAttemptResponse(Instant completedAt, NextLevelInfo nextLevel, UnlockedRewardDto reward) {
            this("COMPLETED", 100, reward, completedAt, nextLevel, reward);
        }

        public CompleteAttemptResponse(String status, Integer score, Object rewards, Instant completedAt, NextLevelInfo nextLevel, UnlockedRewardDto reward) {
            this.status = status != null ? status : "COMPLETED";
            this.score = score != null ? score : 100;
            this.rewards = rewards != null ? rewards : reward;
            this.completedAt = completedAt;
            this.nextLevel = nextLevel;
            this.reward = reward;
        }
    }

    public record UserLevelProgressItemDto(
            String levelId,
            String status,
            Integer bestScore,
            String lastAttemptId,
            Instant updatedAt
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UserLearningProgressDto(
            String trackId,
            Integer completedLevels,
            Integer totalLevels,
            Integer percent,
            List<UserLevelProgressItemDto> levels,
            String userId,
            List<String> completedLevelIds,
            String currentLevelId,
            List<String> badges,
            List<String> unlockedEquipment,
            List<String> unlockedMaterials,
            List<String> unlockedBookChapters,
            Map<String, Object> stats
    ) {
        public UserLearningProgressDto(
                String userId,
                String trackId,
                List<String> completedLevelIds,
                String currentLevelId,
                List<String> badges,
                List<String> unlockedEquipment,
                List<String> unlockedMaterials,
                List<String> unlockedBookChapters,
                Map<String, Object> stats
        ) {
            this(
                    trackId,
                    completedLevelIds != null ? completedLevelIds.size() : 0,
                    completedLevelIds != null && !completedLevelIds.isEmpty() ? completedLevelIds.size() : 1,
                    (completedLevelIds != null && !completedLevelIds.isEmpty()) ? 100 : 0,
                    List.of(),
                    userId,
                    completedLevelIds,
                    currentLevelId,
                    badges,
                    unlockedEquipment,
                    unlockedMaterials,
                    unlockedBookChapters,
                    stats
            );
        }

        public UserLearningProgressDto(
                String userId,
                String trackId,
                List<String> completedLevelIds,
                String currentLevelId,
                List<String> badges,
                List<String> unlockedEquipment,
                List<String> unlockedMaterials,
                List<String> unlockedBookChapters,
                Map<String, Object> stats,
                int totalLevels,
                List<UserLevelProgressItemDto> levels
        ) {
            this(
                    trackId,
                    completedLevelIds != null ? completedLevelIds.size() : 0,
                    totalLevels,
                    totalLevels > 0 ? (int) Math.round(((double) (completedLevelIds != null ? completedLevelIds.size() : 0) / totalLevels) * 100) : 0,
                    levels != null ? levels : List.of(),
                    userId,
                    completedLevelIds,
                    currentLevelId,
                    badges,
                    unlockedEquipment,
                    unlockedMaterials,
                    unlockedBookChapters,
                    stats
            );
        }
    }

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PreviewAttemptResponse(
            String attemptId,
            String experimentId,
            String currentStep,
            long stateVersion,
            boolean preview,
            String previewAttemptId,
            String sandboxUrl,
            Instant expiresAt
    ) {
        public PreviewAttemptResponse(String previewAttemptId, String sandboxUrl, Instant expiresAt) {
            this(previewAttemptId, "exp-" + previewAttemptId, "step-1", 1L, true, previewAttemptId, sandboxUrl, expiresAt);
        }
    }

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
