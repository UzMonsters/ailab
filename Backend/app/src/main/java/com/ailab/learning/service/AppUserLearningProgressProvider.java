package com.ailab.learning.service;

import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningUserAttemptEntity;
import com.ailab.learning.domain.LearningUserProgressEntity;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningUserAttemptRepository;
import com.ailab.learning.repository.LearningUserProgressRepository;
import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.User;
import com.ailab.user.service.UserLearningProgressProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class AppUserLearningProgressProvider implements UserLearningProgressProvider {

    private final LearningUserProgressRepository progressRepository;
    private final LearningUserAttemptRepository attemptRepository;
    private final LearningLevelRepository levelRepository;
    private final ObjectMapper objectMapper;

    public AppUserLearningProgressProvider(
            LearningUserProgressRepository progressRepository,
            LearningUserAttemptRepository attemptRepository,
            LearningLevelRepository levelRepository,
            ObjectMapper objectMapper
    ) {
        this.progressRepository = progressRepository;
        this.attemptRepository = attemptRepository;
        this.levelRepository = levelRepository;
        this.objectMapper = objectMapper;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Optional<UserDtos.LearningProgressResponse> getLearningProgress(User user, String track) {
        if (user == null) {
            return Optional.empty();
        }

        List<LearningUserProgressEntity> progressList;
        if (track != null && !track.isBlank()) {
            progressList = progressRepository.findByUserIdAndTrackId(user.getId(), track)
                    .map(List::of)
                    .orElse(List.of());
        } else {
            progressList = progressRepository.findAllByUserId(user.getId());
        }

        int totalAttempts = (int) attemptRepository.countByUserId(user.getId());
        Set<String> allCompletedLevels = new HashSet<>();
        Set<String> allBadges = new HashSet<>();
        List<Map<String, Object>> trackSummaries = new ArrayList<>();
        Instant lastActivityAt = null;

        for (LearningUserProgressEntity p : progressList) {
            List<String> completed = parseJsonList(p.getCompletedLevelIdsJson());
            allCompletedLevels.addAll(completed);
            List<String> badges = parseJsonList(p.getBadgesJson());
            allBadges.addAll(badges);

            Map<String, Object> trackMap = new LinkedHashMap<>();
            trackMap.put("trackId", p.getTrackId());
            trackMap.put("completedLevels", completed.size());
            trackMap.put("currentLevelId", p.getCurrentLevelId());
            trackMap.put("updatedAt", p.getUpdatedAt());
            trackSummaries.add(trackMap);

            if (lastActivityAt == null || (p.getUpdatedAt() != null && p.getUpdatedAt().isAfter(lastActivityAt))) {
                lastActivityAt = p.getUpdatedAt();
            }
        }

        List<LearningUserAttemptEntity> recentAttempts = attemptRepository.findTop20ByUserIdOrderByStartedAtDesc(user.getId());
        if (!recentAttempts.isEmpty()) {
            Instant latestAttemptTime = recentAttempts.get(0).getStartedAt();
            if (lastActivityAt == null || (latestAttemptTime != null && latestAttemptTime.isAfter(lastActivityAt))) {
                lastActivityAt = latestAttemptTime;
            }
        }

        Object activeAttempt = null;
        for (LearningUserAttemptEntity att : recentAttempts) {
            if (att.getStatus() == AttemptStatus.ACTIVE) {
                Map<String, Object> attMap = new LinkedHashMap<>();
                attMap.put("attemptId", att.getId());
                attMap.put("levelId", att.getLevelId());
                attMap.put("startedAt", att.getStartedAt());
                activeAttempt = attMap;
                break;
            }
        }

        return Optional.of(new UserDtos.LearningProgressResponse(
                allCompletedLevels.size(),
                activeAttempt,
                new ArrayList<>(allBadges),
                trackSummaries,
                totalAttempts,
                lastActivityAt
        ));
    }

    @Override
    public Optional<UserDtos.UserLearningProgressResponse> getUserLearningProgress(User user, String track) {
        return getLearningProgress(user, track).map(p -> new UserDtos.UserLearningProgressResponse(
                p.tracks(),
                p.attempts(),
                p.completedLevels(),
                p.lastActivityAt()
        ));
    }
}
