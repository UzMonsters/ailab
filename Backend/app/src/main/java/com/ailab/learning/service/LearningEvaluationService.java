package com.ailab.learning.service;

import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningUserAttemptEntity;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.evaluator.SemanticCheckpointEvaluator;
import com.ailab.learning.exception.LearningStateVersionConflictException;
import com.ailab.learning.exception.LevelNotFoundException;
import com.ailab.learning.exception.StepRequirementNotMetException;
import com.ailab.learning.repository.LearningUserAttemptRepository;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class LearningEvaluationService {

    private final LearningUserAttemptRepository attemptRepository;
    private final LearningLevelService levelService;
    private final SemanticCheckpointEvaluator checkpointEvaluator;
    private final WorkspaceStateRepository workspaceStateRepository;
    private final ObjectMapper objectMapper;

    public LearningEvaluationService(
            LearningUserAttemptRepository attemptRepository,
            LearningLevelService levelService,
            SemanticCheckpointEvaluator checkpointEvaluator,
            WorkspaceStateRepository workspaceStateRepository,
            ObjectMapper objectMapper
    ) {
        this.attemptRepository = attemptRepository;
        this.levelService = levelService;
        this.checkpointEvaluator = checkpointEvaluator;
        this.workspaceStateRepository = workspaceStateRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SemanticEventResponse receiveSemanticEvent(String attemptId, SemanticEventRequest request) {
        LearningUserAttemptEntity att = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new LevelNotFoundException("Attempt not found: " + attemptId));

        if (att.getStatus() != AttemptStatus.ACTIVE && att.getStatus() != AttemptStatus.EVALUATING) {
            return new SemanticEventResponse(false, List.of());
        }

        if (request != null && request.experimentStateVersion() > 0 && request.experimentStateVersion() < att.getStateVersion()) {
            throw new LearningStateVersionConflictException(
                    "Stale experiment state version",
                    att.getStateVersion(),
                    request.experimentStateVersion()
            );
        }

        if (request != null && request.experimentStateVersion() > att.getStateVersion()) {
            att.setStateVersion(request.experimentStateVersion());
            att.setUpdatedAt(Instant.now());
            attemptRepository.save(att);
        }

        LevelDefinitionDto levelDef = levelService.getLevelSnapshotOrDraft(att.getLevelId(), att.getLevelVersion(), "ru");
        StepDefinitionDto currentStep = findStep(levelDef, att.getCurrentStepId(), att.getCurrentStepIndex());

        List<String> evaluatedCheckpoints = new ArrayList<>();
        if (currentStep != null && currentStep.checkpoint() != null) {
            evaluatedCheckpoints.add(currentStep.id());
        }

        return new SemanticEventResponse(true, evaluatedCheckpoints);
    }

    @Transactional
    public EvaluateCheckpointResponse evaluateCheckpoint(
            String attemptId,
            String checkpointId,
            EvaluateCheckpointRequest request
    ) {
        LearningUserAttemptEntity att = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new LevelNotFoundException("Attempt not found: " + attemptId));

        if (att.getStatus() == AttemptStatus.COMPLETED) {
            return new EvaluateCheckpointResponse(true, null, null);
        }

        if (request != null && request.stateVersion() > 0 && request.stateVersion() < att.getStateVersion()) {
            throw new LearningStateVersionConflictException(
                    "State version conflict during evaluation",
                    att.getStateVersion(),
                    request.stateVersion()
            );
        }

        if (request != null && request.stateVersion() > att.getStateVersion()) {
            att.setStateVersion(request.stateVersion());
        }

        Optional<WorkspaceStateEntity> wsStateOpt = workspaceStateRepository.findById(att.getWorkspaceId());
        if (wsStateOpt.isPresent()) {
            long wsVer = wsStateOpt.get().getStateVersion();
            if (wsVer > att.getStateVersion()) {
                att.setStateVersion(wsVer);
            }
        }

        LevelDefinitionDto levelDef = levelService.getLevelSnapshotOrDraft(att.getLevelId(), att.getLevelVersion(), "ru");
        List<StepDefinitionDto> steps = levelDef.steps() != null ? levelDef.steps() : List.of();

        StepDefinitionDto targetStep = null;
        int stepIdx = 0;
        for (int i = 0; i < steps.size(); i++) {
            StepDefinitionDto s = steps.get(i);
            if (s.id().equalsIgnoreCase(checkpointId) || s.id().equalsIgnoreCase(att.getCurrentStepId())) {
                targetStep = s;
                stepIdx = i;
                break;
            }
        }

        if (targetStep == null) {
            targetStep = steps.isEmpty() ? null : steps.get(0);
        }

        String nextStepId = (stepIdx + 1 < steps.size()) ? steps.get(stepIdx + 1).id() : null;
        CheckpointDefinitionDto checkpoint = targetStep != null ? targetStep.checkpoint() : null;

        EvaluateCheckpointResponse evalResult = checkpointEvaluator.evaluate(att.getWorkspaceId(), checkpoint, nextStepId);

        if (evalResult.accepted()) {
            List<CompletedStepDto> completedSteps = parseCompletedSteps(att.getCompletedStepsJson());
            boolean alreadyCompleted = completedSteps.stream().anyMatch(cs -> cs.stepId().equalsIgnoreCase(checkpointId));
            if (!alreadyCompleted) {
                completedSteps.add(new CompletedStepDto(checkpointId, Instant.now(), Map.of("factType", checkpoint != null ? checkpoint.factType() : "AUTO")));
                try {
                    att.setCompletedStepsJson(objectMapper.writeValueAsString(completedSteps));
                } catch (Exception ignored) {}
            }

            if (nextStepId != null) {
                att.setCurrentStepIndex(stepIdx + 1);
                att.setCurrentStepId(nextStepId);
                att.setStatus(AttemptStatus.ACTIVE);
            } else {
                att.setCurrentStepIndex(steps.size());
            }

            att.setUpdatedAt(Instant.now());
            attemptRepository.save(att);

            return new EvaluateCheckpointResponse(true, null, nextStepId);
        } else {
            return new EvaluateCheckpointResponse(false, evalResult.reason(), null);
        }
    }

    private StepDefinitionDto findStep(LevelDefinitionDto def, String stepId, int stepIndex) {
        if (def.steps() == null || def.steps().isEmpty()) return null;
        if (stepId != null) {
            for (StepDefinitionDto s : def.steps()) {
                if (stepId.equalsIgnoreCase(s.id())) return s;
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
}
