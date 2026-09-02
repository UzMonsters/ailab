package com.ailab.learning.service;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.LearningTrackEntity;
import com.ailab.learning.domain.LearningUserProgressEntity;
import com.ailab.learning.dto.LearningDtos.LevelSummary;
import com.ailab.learning.dto.LearningDtos.TrackMapResponse;
import com.ailab.learning.dto.LearningDtos.TrackSummary;
import com.ailab.learning.exception.LevelNotFoundException;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningTrackRepository;
import com.ailab.learning.repository.LearningUserProgressRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LearningTrackService {

    private final LearningTrackRepository trackRepository;
    private final LearningLevelRepository levelRepository;
    private final LearningUserProgressRepository progressRepository;
    private final ObjectMapper objectMapper;

    public LearningTrackService(
            LearningTrackRepository trackRepository,
            LearningLevelRepository levelRepository,
            LearningUserProgressRepository progressRepository,
            ObjectMapper objectMapper
    ) {
        this.trackRepository = trackRepository;
        this.levelRepository = levelRepository;
        this.progressRepository = progressRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public TrackMapResponse getTrackMap(String codeOrId, String locale, String userId) {
        LearningTrackEntity track = trackRepository.findByCode(codeOrId)
                .or(() -> trackRepository.findById(codeOrId))
                .orElseGet(() -> trackRepository.findAllByOrderBySortOrderAsc().stream().findFirst().orElse(null));

        if (track == null) {
            track = createDefaultChemistryTrack();
        }

        String loc = locale != null && !locale.isBlank() ? locale.toLowerCase() : track.getDefaultLocale();
        Map<String, Object> trackTranslations = parseJsonMap(track.getTranslationsJson());
        Map<String, Object> locMap = extractLocaleMap(trackTranslations, loc);

        String trackTitle = extractString(locMap, "title", "name");
        if (trackTitle == null) trackTitle = "Chemistry Foundations";
        String trackDesc = extractString(locMap, "description", "summary");
        if (trackDesc == null) trackDesc = "Interactive chemistry simulation learning track";

        TrackSummary trackSummary = new TrackSummary(
                track.getId(),
                track.getCode(),
                track.getSortOrder(),
                track.getDefaultLocale(),
                trackTitle,
                trackDesc,
                track.getStatus(),
                track.getDraftVersion()
        );

        Set<String> completedLevelIds = new HashSet<>();
        if (userId != null && !userId.isBlank() && !"anonymousUser".equalsIgnoreCase(userId)) {
            Optional<LearningUserProgressEntity> progressOpt = progressRepository.findByUserIdAndTrackId(userId, track.getId());
            if (progressOpt.isPresent()) {
                List<String> comp = parseJsonListStrings(progressOpt.get().getCompletedLevelIdsJson());
                completedLevelIds.addAll(comp);
            }
        }

        List<LearningLevelEntity> levelEntities = levelRepository.findAllByTrackIdOrderBySortOrderAsc(track.getId());
        List<LevelSummary> levels = new ArrayList<>();

        for (LearningLevelEntity level : levelEntities) {
            Map<String, Object> levelTranslations = parseJsonMap(level.getTranslationsJson());
            Map<String, Object> levelLocMap = extractLocaleMap(levelTranslations, loc);

            String levelTitle = extractString(levelLocMap, "title", "name");
            if (levelTitle == null) levelTitle = "Level " + level.getLevelNumber();
            String levelSummaryText = extractString(levelLocMap, "summary", "description", "goal");

            boolean isComingSoon = level.getStatus() != LearningStatus.PUBLISHED;
            boolean isLocked = false;

            if (!isComingSoon && level.getLevelNumber() > 1) {
                List<String> prereqs = parseJsonListStrings(level.getPrerequisitesJson());
                if (!prereqs.isEmpty()) {
                    for (String prereqId : prereqs) {
                        if (!completedLevelIds.contains(prereqId)) {
                            isLocked = true;
                            break;
                        }
                    }
                } else {
                    int prevLevelNum = level.getLevelNumber() - 1;
                    Optional<LearningLevelEntity> prevLevelOpt = levelRepository.findByTrackIdAndLevelNumber(track.getId(), prevLevelNum);
                    if (prevLevelOpt.isPresent() && !completedLevelIds.contains(prevLevelOpt.get().getId())) {
                        isLocked = true;
                    }
                }
            }

            levels.add(new LevelSummary(
                    level.getId(),
                    level.getTrackId(),
                    level.getLevelNumber(),
                    level.getSortOrder(),
                    level.getDifficulty(),
                    level.getEstimatedMinutes(),
                    levelTitle,
                    levelSummaryText,
                    level.getStatus(),
                    isComingSoon,
                    isLocked,
                    level.getPublishedVersion()
            ));
        }

        return new TrackMapResponse(trackSummary, levels);
    }

    private LearningTrackEntity createDefaultChemistryTrack() {
        LearningTrackEntity track = new LearningTrackEntity(
                "track-chemistry",
                "chemistry",
                1,
                "ru",
                "{\"ru\":{\"title\":\"Основы химии\",\"description\":\"Интерактивный курс виртуальной химической лаборатории\"},\"en\":{\"title\":\"Chemistry Foundations\",\"description\":\"Interactive virtual laboratory learning track\"},\"uz\":{\"title\":\"Kimyo asoslari\",\"description\":\"Interaktiv virtual laboratoriya o'quv kursi\"}}"
        );
        track.setStatus(LearningStatus.PUBLISHED);
        return trackRepository.save(track);
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
