package com.ailab.learning.service;

import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningUserAttemptEntity;
import com.ailab.learning.domain.LearningUserProgressEntity;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.exception.LearningStateVersionConflictException;
import com.ailab.learning.exception.LevelNotFoundException;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningUserAttemptRepository;
import com.ailab.learning.repository.LearningUserProgressRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class LearningCompletionService {

    private final LearningUserAttemptRepository attemptRepository;
    private final LearningLevelRepository levelRepository;
    private final LearningUserProgressRepository progressRepository;
    private final LearningLevelService levelService;
    private final ObjectMapper objectMapper;

    public LearningCompletionService(
            LearningUserAttemptRepository attemptRepository,
            LearningLevelRepository levelRepository,
            LearningUserProgressRepository progressRepository,
            LearningLevelService levelService,
            ObjectMapper objectMapper
    ) {
        this.attemptRepository = attemptRepository;
        this.levelRepository = levelRepository;
        this.progressRepository = progressRepository;
        this.levelService = levelService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CompleteAttemptResponse completeAttempt(
            String attemptId,
            CompleteAttemptRequest request,
            String locale
    ) {
        LearningUserAttemptEntity att = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new LevelNotFoundException("Attempt not found: " + attemptId));

        String loc = locale != null && !locale.isBlank() ? locale.toLowerCase() : "ru";

        if (att.getStatus() == AttemptStatus.COMPLETED) {
            return buildCompletionResponse(att, loc);
        }

        if (request != null && request.stateVersion() > 0 && request.stateVersion() < att.getStateVersion()) {
            throw new LearningStateVersionConflictException(
                    "State version conflict during completion",
                    att.getStateVersion(),
                    request.stateVersion()
            );
        }

        if (request != null && request.idempotencyKey() != null && !request.idempotencyKey().isBlank()) {
            att.setIdempotencyKey(request.idempotencyKey());
        }

        att.setStatus(AttemptStatus.COMPLETED);
        att.setCompletedAt(Instant.now());
        att.setUpdatedAt(Instant.now());
        attemptRepository.save(att);

        if (att.getUserId() != null && !att.isPreview()) {
            updateUserProgress(att);
        }

        return buildCompletionResponse(att, loc);
    }

    private void updateUserProgress(LearningUserAttemptEntity att) {
        LearningLevelEntity level = levelRepository.findById(att.getLevelId()).orElse(null);
        if (level == null) return;

        String trackId = level.getTrackId();
        LearningUserProgressEntity progress = progressRepository.findByUserIdAndTrackId(att.getUserId(), trackId)
                .orElseGet(() -> new LearningUserProgressEntity(
                        "prog-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16),
                        att.getUserId(),
                        trackId
                ));

        List<String> completedLevels = parseJsonListStrings(progress.getCompletedLevelIdsJson());
        if (!completedLevels.contains(att.getLevelId())) {
            completedLevels.add(att.getLevelId());
            try {
                progress.setCompletedLevelIdsJson(objectMapper.writeValueAsString(completedLevels));
            } catch (Exception ignored) {}
        }

        LevelDefinitionDto levelDef = levelService.getLevelSnapshotOrDraft(att.getLevelId(), att.getLevelVersion(), "ru");
        LevelRewardsDto rewards = levelDef.rewards();
        if (rewards != null) {
            if (rewards.badgeId() != null && !rewards.badgeId().isBlank()) {
                List<String> badges = parseJsonListStrings(progress.getBadgesJson());
                if (!badges.contains(rewards.badgeId())) {
                    badges.add(rewards.badgeId());
                    try {
                        progress.setBadgesJson(objectMapper.writeValueAsString(badges));
                    } catch (Exception ignored) {}
                }
            }

            if (rewards.unlockEquipmentIds() != null && !rewards.unlockEquipmentIds().isEmpty()) {
                List<String> eq = parseJsonListStrings(progress.getUnlockedEquipmentJson());
                for (String eId : rewards.unlockEquipmentIds()) {
                    if (!eq.contains(eId)) eq.add(eId);
                }
                try {
                    progress.setUnlockedEquipmentJson(objectMapper.writeValueAsString(eq));
                } catch (Exception ignored) {}
            }

            if (rewards.unlockMaterialIds() != null && !rewards.unlockMaterialIds().isEmpty()) {
                List<String> mat = parseJsonListStrings(progress.getUnlockedMaterialsJson());
                for (String mId : rewards.unlockMaterialIds()) {
                    if (!mat.contains(mId)) mat.add(mId);
                }
                try {
                    progress.setUnlockedMaterialsJson(objectMapper.writeValueAsString(mat));
                } catch (Exception ignored) {}
            }

            if (rewards.unlockBookChapterIds() != null && !rewards.unlockBookChapterIds().isEmpty()) {
                List<String> bch = parseJsonListStrings(progress.getUnlockedBookChaptersJson());
                for (String bId : rewards.unlockBookChapterIds()) {
                    if (!bch.contains(bId)) bch.add(bId);
                }
                try {
                    progress.setUnlockedBookChaptersJson(objectMapper.writeValueAsString(bch));
                } catch (Exception ignored) {}
            }
        }

        Optional<LearningLevelEntity> nextLevelOpt = levelRepository.findByTrackIdAndLevelNumber(trackId, level.getLevelNumber() + 1);
        if (nextLevelOpt.isPresent()) {
            progress.setCurrentLevelId(nextLevelOpt.get().getId());
        } else {
            progress.setCurrentLevelId(level.getId());
        }

        progress.setUpdatedAt(Instant.now());
        progressRepository.save(progress);
    }

    private CompleteAttemptResponse buildCompletionResponse(LearningUserAttemptEntity att, String locale) {
        LearningLevelEntity currentLevel = levelRepository.findById(att.getLevelId()).orElse(null);
        NextLevelInfo nextLevel = null;

        if (currentLevel != null) {
            Optional<LearningLevelEntity> nextOpt = levelRepository.findByTrackIdAndLevelNumber(
                    currentLevel.getTrackId(), currentLevel.getLevelNumber() + 1);
            if (nextOpt.isPresent()) {
                LearningLevelEntity next = nextOpt.get();
                Map<String, Object> translations = parseJsonMap(next.getTranslationsJson());
                Map<String, Object> locMap = extractLocaleMap(translations, locale);
                String title = extractString(locMap, "title", "name");
                if (title == null) title = "Level " + next.getLevelNumber();
                nextLevel = new NextLevelInfo(next.getId(), next.getLevelNumber(), title);
            }
        }

        LevelDefinitionDto levelDef = levelService.getLevelSnapshotOrDraft(att.getLevelId(), att.getLevelVersion(), locale);
        LevelRewardsDto defRewards = levelDef.rewards();

        UnlockedRewardDto rewardDto = new UnlockedRewardDto(
                defRewards != null ? defRewards.badgeId() : null,
                defRewards != null && defRewards.unlockLevelIds() != null ? defRewards.unlockLevelIds() : List.of(),
                defRewards != null && defRewards.unlockEquipmentIds() != null ? defRewards.unlockEquipmentIds() : List.of(),
                defRewards != null && defRewards.unlockMaterialIds() != null ? defRewards.unlockMaterialIds() : List.of(),
                defRewards != null && defRewards.unlockBookChapterIds() != null ? defRewards.unlockBookChapterIds() : List.of()
        );

        Instant completedAt = att.getCompletedAt() != null ? att.getCompletedAt() : Instant.now();
        return new CompleteAttemptResponse(completedAt, nextLevel, rewardDto);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractLocaleMap(Map<String, Object> translations, String locale) {
        if (translations == null) return Map.of();
        Object obj = translations.get(locale);
        if (obj == null) obj = translations.get("ru");
        if (obj == null) obj = translations.get("en");
        if (obj instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return Map.of();
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

    private String extractString(Map<String, Object> map, String... keys) {
        if (map == null) return null;
        for (String key : keys) {
            Object val = map.get(key);
            if (val != null && !val.toString().isBlank()) {
                return val.toString();
            }
        }
        return null;
    }
}
