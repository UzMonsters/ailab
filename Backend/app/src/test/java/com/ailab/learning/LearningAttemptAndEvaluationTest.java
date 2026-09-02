package com.ailab.learning;

import com.ailab.learning.domain.*;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.evaluator.SemanticCheckpointEvaluator;
import com.ailab.learning.exception.LearningStateVersionConflictException;
import com.ailab.learning.exception.PrerequisiteNotMetException;
import com.ailab.learning.guide.SemanticGuideService;
import com.ailab.learning.repository.*;
import com.ailab.learning.service.LearningAttemptService;
import com.ailab.learning.service.LearningCompletionService;
import com.ailab.learning.service.LearningEvaluationService;
import com.ailab.learning.service.LearningLevelService;
import com.ailab.workspace.domain.MeasurementEntity;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.repository.MeasurementRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningAttemptAndEvaluationTest {

    @Mock
    private LearningUserAttemptRepository attemptRepository;

    @Mock
    private LearningLevelRepository levelRepository;

    @Mock
    private LearningLevelPublishedSnapshotRepository snapshotRepository;

    @Mock
    private LearningUserProgressRepository progressRepository;

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceStateRepository workspaceStateRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LearningLevelService levelService;
    private SemanticGuideService guideService;
    private SemanticCheckpointEvaluator checkpointEvaluator;
    private LearningAttemptService attemptService;
    private LearningEvaluationService evaluationService;
    private LearningCompletionService completionService;

    private LearningLevelEntity level1;
    private LearningLevelEntity level2;

    @BeforeEach
    void setUp() {
        levelService = new LearningLevelService(levelRepository, snapshotRepository, objectMapper);
        guideService = new SemanticGuideService(workspaceStateRepository, objectMapper);
        checkpointEvaluator = new SemanticCheckpointEvaluator(workspaceStateRepository, measurementRepository, objectMapper);

        attemptService = new LearningAttemptService(
                attemptRepository, levelRepository, progressRepository,
                levelService, guideService, workspaceRepository, workspaceStateRepository, objectMapper
        );

        evaluationService = new LearningEvaluationService(
                attemptRepository, levelService, checkpointEvaluator, workspaceStateRepository, objectMapper
        );

        completionService = new LearningCompletionService(
                attemptRepository, levelRepository, progressRepository, levelService, objectMapper
        );

        level1 = new LearningLevelEntity();
        level1.setId("level-chemistry-1");
        level1.setTrackId("track-chemistry");
        level1.setLevelNumber(1);
        level1.setStatus(LearningStatus.PUBLISHED);
        level1.setPublishedVersion(1L);
        level1.setTranslationsJson("{\"ru\":{\"title\":\"Уровень 1\",\"summary\":\"Введение\",\"goalDescription\":\"Цель\"}}");
        level1.setStepsJson("[{\"id\":\"step-1\",\"order\":1,\"type\":\"CONTAINER_SETUP\",\"checkpoint\":{\"type\":\"CONTAINER_PRESENT\",\"allowedEquipment\":[\"beaker_250ml\"]}}]");
        level1.setRewardsJson("{\"badgeId\":\"badge-first-step\",\"unlockLevelIds\":[\"level-chemistry-2\"],\"unlockEquipmentIds\":[\"beaker_250ml\"],\"unlockMaterialIds\":[],\"unlockBookChapterIds\":[]}");

        level2 = new LearningLevelEntity();
        level2.setId("level-chemistry-2");
        level2.setTrackId("track-chemistry");
        level2.setLevelNumber(2);
        level2.setStatus(LearningStatus.PUBLISHED);
        level2.setPublishedVersion(1L);
        level2.setPrerequisitesJson("[\"level-chemistry-1\"]");
        level2.setTranslationsJson("{\"ru\":{\"title\":\"Уровень 2\",\"summary\":\"Второй\"}}");
    }

    @Test
    void testStartAttempt_Success() {
        when(levelRepository.findById("level-chemistry-1")).thenReturn(Optional.of(level1));
        when(snapshotRepository.findByLevelIdAndVersion("level-chemistry-1", 1L)).thenReturn(Optional.empty());
        when(workspaceRepository.existsById(any())).thenReturn(false);
        when(workspaceStateRepository.existsById(any())).thenReturn(false);

        StartAttemptResponse response = attemptService.startOrResumeAttempt("level-chemistry-1", new StartAttemptRequest("client-1", "ru", null), "user-1", false);

        assertThat(response).isNotNull();
        assertThat(response.attemptId()).startsWith("att-");
        assertThat(response.currentStep()).isEqualTo("step-1");
        verify(attemptRepository).save(any());
    }

    @Test
    void testStartAttempt_ThrowsWhenPrerequisiteNotMet() {
        when(levelRepository.findById("level-chemistry-2")).thenReturn(Optional.of(level2));
        when(progressRepository.findByUserIdAndTrackId("user-1", "track-chemistry")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attemptService.startOrResumeAttempt("level-chemistry-2", null, "user-1", false))
                .isInstanceOf(PrerequisiteNotMetException.class);
    }

    @Test
    void testReceiveSemanticEvent_ThrowsOnStaleVersion() {
        LearningUserAttemptEntity att = new LearningUserAttemptEntity();
        att.setId("att-1");
        att.setStateVersion(5);
        att.setStatus(AttemptStatus.ACTIVE);
        when(attemptRepository.findById("att-1")).thenReturn(Optional.of(att));

        SemanticEventRequest req = new SemanticEventRequest("evt-1", "ITEM_ADD", null, 2);

        assertThatThrownBy(() -> evaluationService.receiveSemanticEvent("att-1", req))
                .isInstanceOf(LearningStateVersionConflictException.class);
    }

    @Test
    void testEvaluateCheckpoint_AdvancesStepWhenAccepted() {
        LearningUserAttemptEntity att = new LearningUserAttemptEntity();
        att.setId("att-1");
        att.setLevelId("level-chemistry-1");
        att.setLevelVersion(1L);
        att.setWorkspaceId("ws-1");
        att.setCurrentStepId("step-1");
        att.setCurrentStepIndex(0);
        att.setStateVersion(1);
        att.setStatus(AttemptStatus.ACTIVE);

        WorkspaceStateEntity wsState = new WorkspaceStateEntity("ws-1", 1L);
        wsState.setItemsJson("[{\"id\":\"item-1\",\"catalogCode\":\"beaker_250ml\",\"capabilities\":[\"CONTAINER\"]}]");

        when(attemptRepository.findById("att-1")).thenReturn(Optional.of(att));
        when(levelRepository.findById("level-chemistry-1")).thenReturn(Optional.of(level1));
        when(snapshotRepository.findByLevelIdAndVersion("level-chemistry-1", 1L)).thenReturn(Optional.empty());
        when(workspaceStateRepository.findById("ws-1")).thenReturn(Optional.of(wsState));

        EvaluateCheckpointResponse response = evaluationService.evaluateCheckpoint("att-1", "step-1", new EvaluateCheckpointRequest("idemp-1", 1));

        assertThat(response.accepted()).isTrue();
        verify(attemptRepository).save(att);
    }

    @Test
    void testCompleteAttempt_IdempotentAndUnlocksNextLevel() {
        LearningUserAttemptEntity att = new LearningUserAttemptEntity();
        att.setId("att-1");
        att.setUserId("user-1");
        att.setLevelId("level-chemistry-1");
        att.setLevelVersion(1L);
        att.setStateVersion(1);
        att.setStatus(AttemptStatus.ACTIVE);

        when(attemptRepository.findById("att-1")).thenReturn(Optional.of(att));
        when(levelRepository.findById("level-chemistry-1")).thenReturn(Optional.of(level1));
        when(levelRepository.findByTrackIdAndLevelNumber("track-chemistry", 2)).thenReturn(Optional.of(level2));
        when(progressRepository.findByUserIdAndTrackId("user-1", "track-chemistry")).thenReturn(Optional.empty());
        when(snapshotRepository.findByLevelIdAndVersion("level-chemistry-1", 1L)).thenReturn(Optional.empty());

        CompleteAttemptResponse resp1 = completionService.completeAttempt("att-1", new CompleteAttemptRequest("key-1", 1), "ru");

        assertThat(resp1).isNotNull();
        assertThat(resp1.completedAt()).isNotNull();
        assertThat(resp1.nextLevel()).isNotNull();
        assertThat(resp1.nextLevel().id()).isEqualTo("level-chemistry-2");
        assertThat(resp1.reward().badgeId()).isEqualTo("badge-first-step");

        CompleteAttemptResponse resp2 = completionService.completeAttempt("att-1", new CompleteAttemptRequest("key-1", 1), "ru");
        assertThat(resp2.completedAt()).isEqualTo(resp1.completedAt());
    }
}
