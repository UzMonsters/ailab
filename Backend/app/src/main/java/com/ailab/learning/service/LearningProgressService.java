package com.ailab.learning.service;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningTrackEntity;
import com.ailab.learning.domain.LearningUserAttemptEntity;
import com.ailab.learning.domain.LearningUserProgressEntity;
import com.ailab.learning.dto.LearningDtos;
import com.ailab.learning.dto.LearningDtos.UserLearningProgressDto;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningTrackRepository;
import com.ailab.learning.repository.LearningUserAttemptRepository;
import com.ailab.learning.repository.LearningUserProgressRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class LearningProgressService {

    private final LearningUserProgressRepository progressRepository;
    private final LearningTrackRepository trackRepository;
    private final LearningLevelRepository levelRepository;
    private final LearningUserAttemptRepository attemptRepository;
    private final ObjectMapper objectMapper;

    public LearningProgressService(
            LearningUserProgressRepository progressRepository,
            LearningTrackRepository trackRepository,
            LearningLevelRepository levelRepository,
            LearningUserAttemptRepository attemptRepository,
            ObjectMapper objectMapper
    ) {
        this.progressRepository = progressRepository;
        this.trackRepository = trackRepository;
        this.levelRepository = levelRepository;
        this.attemptRepository = attemptRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UserLearningProgressDto getUserProgress(String userId, String trackCodeOrId) {
        String effectiveTrack = (trackCodeOrId != null && !trackCodeOrId.isBlank()) ? trackCodeOrId : "chemistry";
        LearningTrackEntity track = trackRepository.findByCode(effectiveTrack)
                .or(() -> trackRepository.findById(effectiveTrack))
                .orElseGet(() -> trackRepository.findAllByOrderBySortOrderAsc().stream().findFirst().orElse(null));

        String trackId = track != null ? track.getId() : effectiveTrack;

        LearningUserProgressEntity progress = progressRepository.findByUserIdAndTrackId(userId, trackId)
                .orElseGet(() -> {
                    LearningUserProgressEntity p = new LearningUserProgressEntity(
                            "prog-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16),
                            userId,
                            trackId
                    );
                    return progressRepository.save(p);
                });

        List<String> completedLevelIds = parseJsonListStrings(progress.getCompletedLevelIdsJson());
        int totalLevels = (int) levelRepository.countByTrackId(trackId);
        if (totalLevels == 0) {
            totalLevels = Math.max(1, completedLevelIds.size());
        }

        List<LearningLevelEntity> trackLevels = levelRepository.findAllByTrackIdOrderBySortOrderAsc(trackId);
        List<LearningDtos.UserLevelProgressItemDto> levelProgressItems = new ArrayList<>();

        for (LearningLevelEntity lvl : trackLevels) {
            boolean isCompleted = completedLevelIds.contains(lvl.getId());
            List<LearningUserAttemptEntity> attempts = attemptRepository.findAllByUserIdAndLevelIdOrderByStartedAtDesc(userId, lvl.getId());
            String lastAttemptId = !attempts.isEmpty() ? attempts.get(0).getId() : null;
            Instant updatedAt = !attempts.isEmpty() ? attempts.get(0).getUpdatedAt() : progress.getUpdatedAt();
            int bestScore = attempts.stream().filter(a -> a.getScore() != null).mapToInt(LearningUserAttemptEntity::getScore).max().orElse(isCompleted ? 100 : 0);

            levelProgressItems.add(new LearningDtos.UserLevelProgressItemDto(
                    lvl.getId(),
                    isCompleted ? "COMPLETED" : "AVAILABLE",
                    bestScore,
                    lastAttemptId,
                    updatedAt
            ));
        }

        return new UserLearningProgressDto(
                progress.getUserId(),
                progress.getTrackId(),
                completedLevelIds,
                progress.getCurrentLevelId(),
                parseJsonListStrings(progress.getBadgesJson()),
                parseJsonListStrings(progress.getUnlockedEquipmentJson()),
                parseJsonListStrings(progress.getUnlockedMaterialsJson()),
                parseJsonListStrings(progress.getUnlockedBookChaptersJson()),
                parseJsonMap(progress.getStatsJson()),
                totalLevels,
                levelProgressItems
        );
    }

    @Transactional
    public void migrateGuestProgress(String guestAttemptId, String userId) {
        if (guestAttemptId == null || userId == null || userId.isBlank()) return;
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

    private Map<String, Object> parseJsonMap(String json) {
        if (json == null || json.isBlank() || json.equals("{}")) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
