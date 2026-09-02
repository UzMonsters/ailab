package com.ailab.learning.service;

import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.LearningUserAttemptEntity;
import com.ailab.learning.domain.LearningUserProgressEntity;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.exception.LevelNotFoundException;
import com.ailab.learning.exception.PrerequisiteNotMetException;
import com.ailab.learning.guide.SemanticGuideService;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningUserAttemptRepository;
import com.ailab.learning.repository.LearningUserProgressRepository;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class LearningAttemptService {

    private final LearningUserAttemptRepository attemptRepository;
    private final LearningLevelRepository levelRepository;
    private final LearningUserProgressRepository progressRepository;
    private final LearningLevelService levelService;
    private final SemanticGuideService guideService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceStateRepository workspaceStateRepository;
    private final ObjectMapper objectMapper;

    public LearningAttemptService(
            LearningUserAttemptRepository attemptRepository,
            LearningLevelRepository levelRepository,
            LearningUserProgressRepository progressRepository,
            LearningLevelService levelService,
            SemanticGuideService guideService,
            WorkspaceRepository workspaceRepository,
            WorkspaceStateRepository workspaceStateRepository,
            ObjectMapper objectMapper
    ) {
        this.attemptRepository = attemptRepository;
        this.levelRepository = levelRepository;
        this.progressRepository = progressRepository;
        this.levelService = levelService;
        this.guideService = guideService;
        this.workspaceRepository = workspaceRepository;
        this.workspaceStateRepository = workspaceStateRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public StartAttemptResponse startOrResumeAttempt(
            String levelId,
            StartAttemptRequest request,
            String userId,
            boolean isPreview
    ) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        if (!isPreview && level.getStatus() != LearningStatus.PUBLISHED && level.getPublishedVersion() == null) {
            throw new LevelNotFoundException("Level is not published yet: " + levelId);
        }

        boolean isGuest = userId == null || userId.isBlank() || "anonymousUser".equalsIgnoreCase(userId);
        String effectiveUserId = isGuest ? null : userId;

        if (!isPreview && !isGuest && level.getLevelNumber() > 1) {
            verifyPrerequisites(level, effectiveUserId);
        }

        String clientAttemptId = request != null ? request.clientAttemptId() : null;
        if (clientAttemptId != null && !clientAttemptId.isBlank()) {
            Optional<LearningUserAttemptEntity> existingAttempt = attemptRepository.findByClientAttemptId(clientAttemptId);
            if (existingAttempt.isPresent()) {
                LearningUserAttemptEntity att = existingAttempt.get();
                if (att.getStatus() == AttemptStatus.ACTIVE || att.getStatus() == AttemptStatus.EVALUATING) {
                    return new StartAttemptResponse(
                            att.getId(),
                            att.getExperimentId(),
                            att.getCurrentStepId(),
                            att.getStateVersion()
                    );
                }
            }
        }

        if (effectiveUserId != null && !isPreview) {
            Optional<LearningUserAttemptEntity> activeOpt = attemptRepository.findFirstByUserIdAndLevelIdAndStatusOrderByStartedAtDesc(
                    effectiveUserId, levelId, AttemptStatus.ACTIVE);
            if (activeOpt.isPresent()) {
                LearningUserAttemptEntity att = activeOpt.get();
                return new StartAttemptResponse(
                        att.getId(),
                        att.getExperimentId(),
                        att.getCurrentStepId(),
                        att.getStateVersion()
                );
            }
        }

        LevelDefinitionDto levelDef = levelService.getPublishedLevel(levelId, request != null ? request.locale() : "ru");
        String firstStepId = (levelDef.steps() != null && !levelDef.steps().isEmpty())
                ? levelDef.steps().get(0).id()
                : "step-1";

        String attemptId = "att-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String workspaceId = (request != null && request.workspaceId() != null && !request.workspaceId().isBlank())
                ? request.workspaceId()
                : "ws-learning-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String experimentId = "exp-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        ensureWorkspaceInitialized(workspaceId, effectiveUserId, levelDef);

        LearningUserAttemptEntity newAttempt = new LearningUserAttemptEntity();
        newAttempt.setId(attemptId);
        newAttempt.setClientAttemptId(clientAttemptId);
        newAttempt.setUserId(effectiveUserId);
        newAttempt.setGuest(isGuest);
        newAttempt.setPreview(isPreview);
        newAttempt.setLevelId(levelId);
        newAttempt.setLevelVersion(levelDef.version());
        newAttempt.setWorkspaceId(workspaceId);
        newAttempt.setExperimentId(experimentId);
        newAttempt.setStateVersion(1);
        newAttempt.setStatus(AttemptStatus.ACTIVE);
        newAttempt.setCurrentStepIndex(0);
        newAttempt.setCurrentStepId(firstStepId);
        newAttempt.setCompletedStepsJson("[]");
        newAttempt.setHintUsageJson("[]");
        newAttempt.setStartedAt(Instant.now());
        newAttempt.setUpdatedAt(Instant.now());

        attemptRepository.save(newAttempt);

        return new StartAttemptResponse(attemptId, experimentId, firstStepId, 1);
    }

    private void verifyPrerequisites(LearningLevelEntity level, String userId) {
        Optional<LearningUserProgressEntity> progressOpt = progressRepository.findByUserIdAndTrackId(userId, level.getTrackId());
        Set<String> completedLevels = new HashSet<>();
        if (progressOpt.isPresent()) {
            completedLevels.addAll(parseJsonListStrings(progressOpt.get().getCompletedLevelIdsJson()));
        }

        List<String> prereqs = parseJsonListStrings(level.getPrerequisitesJson());
        if (!prereqs.isEmpty()) {
            for (String prereqId : prereqs) {
                if (!completedLevels.contains(prereqId)) {
                    throw new PrerequisiteNotMetException("Prerequisite level not completed: " + prereqId, prereqId);
                }
            }
        } else {
            int prevLevelNum = level.getLevelNumber() - 1;
            Optional<LearningLevelEntity> prevLevelOpt = levelRepository.findByTrackIdAndLevelNumber(level.getTrackId(), prevLevelNum);
            if (prevLevelOpt.isPresent()) {
                String prevId = prevLevelOpt.get().getId();
                if (!completedLevels.contains(prevId)) {
                    throw new PrerequisiteNotMetException("Prerequisite level not completed: " + prevId, prevId);
                }
            }
        }
    }

    private void ensureWorkspaceInitialized(String workspaceId, String userId, LevelDefinitionDto levelDef) {
        if (!workspaceRepository.existsById(workspaceId)) {
            WorkspaceEntity ws = new WorkspaceEntity(
                    workspaceId,
                    userId != null ? userId : "guest",
                    "Learning: " + levelDef.title(),
                    "CHEMISTRY",
                    "learning"
            );
            workspaceRepository.save(ws);
        }

        if (!workspaceStateRepository.existsById(workspaceId)) {
            WorkspaceStateEntity state = new WorkspaceStateEntity(workspaceId, 1);
            if (levelDef.scenario() != null && levelDef.scenario().initialState() != null) {
                try {
                    Map<String, Object> init = levelDef.scenario().initialState();
                    if (init.containsKey("items")) {
                        state.setItemsJson(objectMapper.writeValueAsString(init.get("items")));
                    }
                    if (init.containsKey("connections")) {
                        state.setConnectionsJson(objectMapper.writeValueAsString(init.get("connections")));
                    }
                } catch (Exception ignored) {}
            }
            workspaceStateRepository.save(state);
        }
    }

    @Transactional(readOnly = true)
    public AttemptStateDto getAttemptState(String attemptId) {
        LearningUserAttemptEntity att = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new LevelNotFoundException("Attempt not found: " + attemptId));

        List<CompletedStepDto> completedSteps = parseCompletedSteps(att.getCompletedStepsJson());
        List<HintUsageDto> hintUsage = parseHintUsage(att.getHintUsageJson());

        return new AttemptStateDto(
                att.getId(),
                att.getLevelId(),
                att.getLevelVersion(),
                att.getExperimentId(),
                att.getWorkspaceId(),
                att.getStateVersion(),
                att.getStatus(),
                att.getCurrentStepIndex(),
                att.getCurrentStepId(),
                completedSteps,
                hintUsage,
                att.getStartedAt(),
                att.getCompletedAt(),
                att.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public GuidePayload getGuide(String attemptId, String mode, String locale) {
        LearningUserAttemptEntity att = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new LevelNotFoundException("Attempt not found: " + attemptId));

        LevelDefinitionDto levelDef = levelService.getLevelSnapshotOrDraft(att.getLevelId(), att.getLevelVersion(), locale);
        StepDefinitionDto currentStep = findStep(levelDef, att.getCurrentStepId(), att.getCurrentStepIndex());

        int hintLevel = 1;
        List<HintUsageDto> hintUsage = parseHintUsage(att.getHintUsageJson());
        for (HintUsageDto hu : hintUsage) {
            if (att.getCurrentStepId().equalsIgnoreCase(hu.stepId())) {
                hintLevel = Math.max(hintLevel, hu.level());
            }
        }

        return guideService.resolveGuide(currentStep, hintLevel, mode, locale, att.getWorkspaceId());
    }

    @Transactional
    public GuidePayload requestHint(String attemptId, HintRequest request, String locale) {
        LearningUserAttemptEntity att = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new LevelNotFoundException("Attempt not found: " + attemptId));

        int requestedLevel = request != null && request.level() > 0 ? request.level() : 1;
        String stepId = (request != null && request.currentStepId() != null && !request.currentStepId().isBlank())
                ? request.currentStepId()
                : att.getCurrentStepId();

        List<HintUsageDto> hintUsage = parseHintUsage(att.getHintUsageJson());
        hintUsage.add(new HintUsageDto(stepId, requestedLevel, Instant.now()));
        try {
            att.setHintUsageJson(objectMapper.writeValueAsString(hintUsage));
        } catch (Exception ignored) {}
        att.setUpdatedAt(Instant.now());
        attemptRepository.save(att);

        LevelDefinitionDto levelDef = levelService.getLevelSnapshotOrDraft(att.getLevelId(), att.getLevelVersion(), locale);
        StepDefinitionDto currentStep = findStep(levelDef, stepId, att.getCurrentStepIndex());

        return guideService.resolveGuide(currentStep, requestedLevel, "hint", locale, att.getWorkspaceId());
    }

    private StepDefinitionDto findStep(LevelDefinitionDto def, String stepId, int stepIndex) {
        if (def.steps() == null || def.steps().isEmpty()) {
            return null;
        }
        if (stepId != null) {
            for (StepDefinitionDto s : def.steps()) {
                if (stepId.equalsIgnoreCase(s.id())) {
                    return s;
                }
            }
        }
        if (stepIndex >= 0 && stepIndex < def.steps().size()) {
            return def.steps().get(stepIndex);
        }
        return def.steps().get(0);
    }

    private List<CompletedStepDto> parseCompletedSteps(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CompletedStepDto>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<HintUsageDto> parseHintUsage(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<HintUsageDto>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<String> parseJsonListStrings(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
