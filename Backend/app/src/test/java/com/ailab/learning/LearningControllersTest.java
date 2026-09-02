package com.ailab.learning;

import com.ailab.learning.controller.AdminLearningController;
import com.ailab.learning.controller.PublicLearningController;
import com.ailab.learning.controller.UserLearningProgressController;
import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.LearningTrackEntity;
import com.ailab.learning.domain.LearningUserProgressEntity;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.evaluator.SemanticCheckpointEvaluator;
import com.ailab.learning.guide.SemanticGuideService;
import com.ailab.learning.repository.*;
import com.ailab.learning.service.*;
import com.ailab.workspace.repository.MeasurementRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningControllersTest {

    @Mock
    private LearningTrackRepository trackRepository;

    @Mock
    private LearningLevelRepository levelRepository;

    @Mock
    private LearningLevelPublishedSnapshotRepository snapshotRepository;

    @Mock
    private LearningChapterRepository chapterRepository;

    @Mock
    private LearningTaskRepository taskRepository;

    @Mock
    private LearningRewardRepository rewardRepository;

    @Mock
    private LearningUserProgressRepository progressRepository;

    @Mock
    private LearningUserAttemptRepository attemptRepository;

    @Mock
    private LearningProgressResetAuditRepository resetAuditRepository;

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceStateRepository workspaceStateRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PublicLearningController publicController;
    private UserLearningProgressController userProgressController;
    private AdminLearningController adminController;

    private LearningTrackEntity track;
    private LearningLevelEntity level1;
    private LearningLevelEntity level2;

    @BeforeEach
    void setUp() {
        LearningTrackService trackService = new LearningTrackService(trackRepository, levelRepository, progressRepository, objectMapper);
        LearningLevelService levelService = new LearningLevelService(levelRepository, snapshotRepository, objectMapper);
        SemanticGuideService guideService = new SemanticGuideService(workspaceStateRepository, objectMapper);
        SemanticCheckpointEvaluator evaluator = new SemanticCheckpointEvaluator(workspaceStateRepository, measurementRepository, objectMapper);
        LearningAttemptService attemptService = new LearningAttemptService(
                attemptRepository, levelRepository, progressRepository,
                levelService, guideService, workspaceRepository, workspaceStateRepository, objectMapper
        );
        LearningEvaluationService evaluationService = new LearningEvaluationService(
                attemptRepository, levelService, evaluator, workspaceStateRepository, objectMapper
        );
        LearningCompletionService completionService = new LearningCompletionService(
                attemptRepository, levelRepository, progressRepository, levelService, objectMapper
        );
        LearningProgressService progressService = new LearningProgressService(
                progressRepository, trackRepository, objectMapper
        );
        AdminLearningService adminLearningService = new AdminLearningService(
                trackRepository, levelRepository, snapshotRepository, chapterRepository,
                taskRepository, rewardRepository, progressRepository, attemptRepository,
                resetAuditRepository, levelService, workspaceRepository, workspaceStateRepository, objectMapper
        );
        AdminLearningValidationService validationService = new AdminLearningValidationService(levelRepository, objectMapper);
        AdminLearningAnalyticsService analyticsService = new AdminLearningAnalyticsService(
                levelRepository, attemptRepository, objectMapper
        );

        publicController = new PublicLearningController(
                trackService, levelService, attemptService, evaluationService, completionService
        );
        userProgressController = new UserLearningProgressController(progressService);
        adminController = new AdminLearningController(adminLearningService, validationService, analyticsService);

        track = new LearningTrackEntity("track-chem", "chemistry", 1, "ru", "{\"ru\":{\"title\":\"Химия\",\"description\":\"Курс\"}}");
        track.setStatus(LearningStatus.PUBLISHED);

        level1 = new LearningLevelEntity();
        level1.setId("level-1");
        level1.setTrackId("track-chem");
        level1.setLevelNumber(1);
        level1.setStatus(LearningStatus.PUBLISHED);
        level1.setPublishedVersion(1L);
        level1.setTranslationsJson("{\"ru\":{\"title\":\"Уровень 1\",\"summary\":\"Введение\"}}");
        level1.setStepsJson("[{\"id\":\"step-1\",\"order\":1,\"type\":\"CONTAINER_SETUP\"}]");
        level1.setRewardsJson("{\"badgeId\":\"badge-1\",\"unlockedLevelIds\":[\"level-2\"],\"unlockedEquipment\":[],\"unlockedMaterials\":[],\"unlockedReactions\":[]}");

        level2 = new LearningLevelEntity();
        level2.setId("level-2");
        level2.setTrackId("track-chem");
        level2.setLevelNumber(2);
        level2.setStatus(LearningStatus.DRAFT);
        level2.setPublishedVersion(null);
        level2.setTranslationsJson("{\"ru\":{\"title\":\"Уровень 2\",\"summary\":\"Второй\"}}");
    }

    @Test
    void testPublicGetChemistryTrackMap() {
        when(trackRepository.findByCode("chemistry")).thenReturn(Optional.of(track));
        when(levelRepository.findAllByTrackIdOrderBySortOrderAsc("track-chem")).thenReturn(List.of(level1));

        TrackMapResponse res = publicController.getChemistryTrackMap("ru");

        assertThat(res).isNotNull();
        assertThat(res.track().code()).isEqualTo("chemistry");
    }

    @Test
    void testPublicStartAttempt() {
        when(levelRepository.findById("level-1")).thenReturn(Optional.of(level1));
        when(snapshotRepository.findByLevelIdAndVersion("level-1", 1L)).thenReturn(Optional.empty());
        when(workspaceRepository.existsById(any())).thenReturn(false);
        when(workspaceStateRepository.existsById(any())).thenReturn(false);

        StartAttemptResponse res = publicController.startOrResumeAttempt("level-1", new StartAttemptRequest("client-1", "ru", null));

        assertThat(res.attemptId()).startsWith("att-");
        assertThat(res.currentStep()).isEqualTo("step-1");
    }

    @Test
    void testAdminOverview() {
        when(levelRepository.findAll()).thenReturn(List.of(level1, level2));
        when(attemptRepository.findAll()).thenReturn(List.of());

        LearningOverviewResponse res = adminController.getOverview(null, null, null);

        assertThat(res.attempts()).isEqualTo(0L);
    }

    @Test
    void testAdminPublishLevel() {
        when(levelRepository.findById("level-2")).thenReturn(Optional.of(level2));

        PublishResultDto res = adminController.publishLevel("level-2", new PublishLevelRequest(1, "key-1", "Release"));

        assertThat(res.publishedVersion()).isEqualTo(1L);
    }

    @Test
    void testUserLearningProgress() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user-1", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        LearningUserProgressEntity progress = new LearningUserProgressEntity("p1", "user-1", "track-chem");
        progress.setCompletedLevelIdsJson("[\"level-1\"]");
        when(trackRepository.findByCode("chemistry")).thenReturn(Optional.of(track));
        when(progressRepository.findByUserIdAndTrackId("user-1", "track-chem")).thenReturn(Optional.of(progress));

        UserLearningProgressDto res = userProgressController.getUserProgress("chemistry");

        assertThat(res).isNotNull();
        assertThat(res.trackId()).isEqualTo("track-chem");
        assertThat(res.completedLevelIds()).contains("level-1");
    }
}
