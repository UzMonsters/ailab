package com.ailab.learning;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.LearningTrackEntity;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.repository.*;
import com.ailab.learning.service.AdminLearningService;
import com.ailab.learning.service.AdminLearningValidationService;
import com.ailab.learning.service.LearningLevelService;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminLearningServiceTest {

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LearningLevelService levelService;
    private AdminLearningService adminService;
    private AdminLearningValidationService validationService;

    private LearningLevelEntity level1;

    @BeforeEach
    void setUp() {
        levelService = new LearningLevelService(levelRepository, snapshotRepository, objectMapper);
        adminService = new AdminLearningService(
                trackRepository, levelRepository, snapshotRepository, chapterRepository,
                taskRepository, rewardRepository, progressRepository, attemptRepository,
                resetAuditRepository, levelService, workspaceRepository, workspaceStateRepository, objectMapper
        );
        validationService = new AdminLearningValidationService(levelRepository, objectMapper);

        level1 = new LearningLevelEntity();
        level1.setId("level-chemistry-1");
        level1.setTrackId("track-chemistry");
        level1.setLevelNumber(1);
        level1.setStatus(LearningStatus.DRAFT);
        level1.setDraftVersion(1);
        level1.setStepsJson("[{\"id\":\"step-1\",\"order\":1,\"type\":\"CONTAINER_SETUP\"}]");
        level1.setTranslationsJson("{\"ru\":{\"title\":\"Уровень 1\",\"summary\":\"Описание\"},\"en\":{\"title\":\"Level 1\"},\"uz\":{\"title\":\"1-bosqich\"}}");
    }

    @Test
    void testCreateTrack_Success() {
        CreateTrackRequest request = new CreateTrackRequest("organic-chem", 2, "ru", Map.of("ru", Map.of("title", "Органическая химия")));

        TrackDraftResponse response = adminService.createTrack(request);

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("organic-chem");
        verify(trackRepository).save(any());
    }

    @Test
    void testSaveSteps_AtomicallyUpdatesDraftVersion() {
        when(levelRepository.findById("level-chemistry-1")).thenReturn(Optional.of(level1));

        StepDefinitionDto newStep = new StepDefinitionDto("step-add-flask", 1, "CONTAINER_SETUP", null, null, null);
        SaveStepsRequest req = new SaveStepsRequest(1, List.of(newStep));

        SaveStepsResponse response = adminService.saveSteps("level-chemistry-1", req);

        assertThat(response).isNotNull();
        assertThat(response.version()).isEqualTo(2);
        assertThat(response.steps()).hasSize(1);
        verify(levelRepository).save(level1);
    }

    @Test
    void testPublishLevel_CreatesSnapshotAndTransitionsStatus() {
        when(levelRepository.findById("level-chemistry-1")).thenReturn(Optional.of(level1));

        PublishResultDto result = adminService.publishLevel("level-chemistry-1", new PublishLevelRequest(1, "key-pub-1", "First release"), "admin-1", "Admin");

        assertThat(result).isNotNull();
        assertThat(result.publishedVersion()).isEqualTo(1L);
        assertThat(level1.getStatus()).isEqualTo(LearningStatus.PUBLISHED);
        verify(snapshotRepository).save(any());
    }

    @Test
    void testValidateLevel_DetectsCycleInPrerequisites() {
        LearningLevelEntity cyclicLevel = new LearningLevelEntity();
        cyclicLevel.setId("level-cycl-1");
        cyclicLevel.setPrerequisitesJson("[\"level-cycl-1\"]");
        cyclicLevel.setStepsJson("[]");

        when(levelRepository.findById("level-cycl-1")).thenReturn(Optional.of(cyclicLevel));

        ValidationReportDto report = validationService.validateLevel("level-cycl-1", 1L);

        assertThat(report.valid()).isFalse();
        assertThat(report.errors()).isNotEmpty();
    }
}
