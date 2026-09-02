package com.ailab.learning;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.LearningTrackEntity;
import com.ailab.learning.domain.LearningUserProgressEntity;
import com.ailab.learning.dto.LearningDtos.LevelDefinitionDto;
import com.ailab.learning.dto.LearningDtos.TrackMapResponse;
import com.ailab.learning.exception.LevelNotFoundException;
import com.ailab.learning.repository.LearningLevelPublishedSnapshotRepository;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningTrackRepository;
import com.ailab.learning.repository.LearningUserProgressRepository;
import com.ailab.learning.service.LearningLevelService;
import com.ailab.learning.service.LearningTrackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningTrackAndLevelServiceTest {

    @Mock
    private LearningTrackRepository trackRepository;

    @Mock
    private LearningLevelRepository levelRepository;

    @Mock
    private LearningLevelPublishedSnapshotRepository snapshotRepository;

    @Mock
    private LearningUserProgressRepository progressRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LearningTrackService trackService;
    private LearningLevelService levelService;

    private LearningTrackEntity track;
    private LearningLevelEntity level1;
    private LearningLevelEntity level2;

    @BeforeEach
    void setUp() {
        trackService = new LearningTrackService(trackRepository, levelRepository, progressRepository, objectMapper);
        levelService = new LearningLevelService(levelRepository, snapshotRepository, objectMapper);

        track = new LearningTrackEntity(
                "track-chemistry",
                "chemistry",
                1,
                "ru",
                "{\"ru\":{\"title\":\"Основы химии\",\"description\":\"Курс химии\"},\"en\":{\"title\":\"Chemistry Foundations\"}}"
        );
        track.setStatus(LearningStatus.PUBLISHED);

        level1 = new LearningLevelEntity();
        level1.setId("level-chemistry-1");
        level1.setTrackId("track-chemistry");
        level1.setLevelNumber(1);
        level1.setStatus(LearningStatus.PUBLISHED);
        level1.setPublishedVersion(1L);
        level1.setTranslationsJson("{\"ru\":{\"title\":\"Уровень 1\",\"summary\":\"Введение\"},\"en\":{\"title\":\"Level 1\",\"summary\":\"Intro\"},\"uz\":{\"title\":\"1-bosqich\"}}");
        level1.setStepsJson("[{\"id\":\"step-1\",\"order\":1,\"type\":\"CONTAINER_SETUP\",\"translations\":{\"ru\":{\"title\":\"Добавьте стакан\",\"instruction\":\"Поставьте стакан\"}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"equipment\"}]}]");

        level2 = new LearningLevelEntity();
        level2.setId("level-chemistry-2");
        level2.setTrackId("track-chemistry");
        level2.setLevelNumber(2);
        level2.setStatus(LearningStatus.PUBLISHED);
        level2.setPublishedVersion(1L);
        level2.setPrerequisitesJson("[\"level-chemistry-1\"]");
        level2.setTranslationsJson("{\"ru\":{\"title\":\"Уровень 2\",\"summary\":\"Вода\"}}");
    }

    @Test
    void testGetTrackMap_ReturnsTrackAndLockedLevels() {
        when(trackRepository.findByCode("chemistry")).thenReturn(Optional.of(track));
        when(levelRepository.findAllByTrackIdOrderBySortOrderAsc("track-chemistry")).thenReturn(List.of(level1, level2));

        TrackMapResponse response = trackService.getTrackMap("chemistry", "ru", "user-123");

        assertThat(response).isNotNull();
        assertThat(response.track().code()).isEqualTo("chemistry");
        assertThat(response.track().title()).isEqualTo("Основы химии");
        assertThat(response.levels()).hasSize(2);
        assertThat(response.levels().get(0).isLocked()).isFalse();
        assertThat(response.levels().get(1).isLocked()).isTrue();
    }

    @Test
    void testGetTrackMap_UnlocksLevelWhenPrerequisiteCompleted() {
        when(trackRepository.findByCode("chemistry")).thenReturn(Optional.of(track));
        when(levelRepository.findAllByTrackIdOrderBySortOrderAsc("track-chemistry")).thenReturn(List.of(level1, level2));

        LearningUserProgressEntity progress = new LearningUserProgressEntity("p1", "user-123", "track-chemistry");
        progress.setCompletedLevelIdsJson("[\"level-chemistry-1\"]");
        when(progressRepository.findByUserIdAndTrackId("user-123", "track-chemistry")).thenReturn(Optional.of(progress));

        TrackMapResponse response = trackService.getTrackMap("chemistry", "ru", "user-123");

        assertThat(response.levels().get(1).isLocked()).isFalse();
    }

    @Test
    void testGetPublishedLevel_Success() {
        when(levelRepository.findById("level-chemistry-1")).thenReturn(Optional.of(level1));
        when(snapshotRepository.findByLevelIdAndVersion("level-chemistry-1", 1L)).thenReturn(Optional.empty());

        LevelDefinitionDto dto = levelService.getPublishedLevel("level-chemistry-1", "ru");

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo("level-chemistry-1");
        assertThat(dto.title()).isEqualTo("Уровень 1");
        assertThat(dto.steps()).hasSize(1);
    }

    @Test
    void testGetPublishedLevel_ThrowsWhenNotFound() {
        when(levelRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> levelService.getPublishedLevel("non-existent", "ru"))
                .isInstanceOf(LevelNotFoundException.class);
    }
}
