package com.ailab.learning.service;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningLevelPublishedSnapshotEntity;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.exception.LevelNotFoundException;
import com.ailab.learning.repository.LearningLevelPublishedSnapshotRepository;
import com.ailab.learning.repository.LearningLevelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LearningLevelService {

    private final LearningLevelRepository levelRepository;
    private final LearningLevelPublishedSnapshotRepository snapshotRepository;
    private final ObjectMapper objectMapper;

    public LearningLevelService(
            LearningLevelRepository levelRepository,
            LearningLevelPublishedSnapshotRepository snapshotRepository,
            ObjectMapper objectMapper
    ) {
        this.levelRepository = levelRepository;
        this.snapshotRepository = snapshotRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public LevelDefinitionDto getPublishedLevel(String levelId, String locale) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        if (level.getStatus() != LearningStatus.PUBLISHED && level.getPublishedVersion() == null) {
            throw new LevelNotFoundException("Level is not published yet: " + levelId);
        }

        long versionToLoad = level.getPublishedVersion() != null ? level.getPublishedVersion() : level.getDraftVersion();
        Optional<LearningLevelPublishedSnapshotEntity> snapshotOpt = snapshotRepository.findByLevelIdAndVersion(levelId, versionToLoad);

        if (snapshotOpt.isPresent()) {
            return parseLevelFromSnapshot(snapshotOpt.get().getSnapshotDataJson(), locale);
        }

        return mapEntityToDefinitionDto(level, locale);
    }

    @Transactional(readOnly = true)
    public LevelDefinitionDto getLevelSnapshotOrDraft(String levelId, Long version, String locale) {
        if (version != null) {
            Optional<LearningLevelPublishedSnapshotEntity> snap = snapshotRepository.findByLevelIdAndVersion(levelId, version);
            if (snap.isPresent()) {
                return parseLevelFromSnapshot(snap.get().getSnapshotDataJson(), locale);
            }
        }

        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));
        return mapEntityToDefinitionDto(level, locale);
    }

    private LevelDefinitionDto parseLevelFromSnapshot(String snapshotJson, String locale) {
        try {
            LevelDefinitionDto dto = objectMapper.readValue(snapshotJson, LevelDefinitionDto.class);
            return localizeDefinition(dto, locale);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse published snapshot", e);
        }
    }

    public LevelDefinitionDto mapEntityToDefinitionDto(LearningLevelEntity level, String locale) {
        String loc = locale != null && !locale.isBlank() ? locale.toLowerCase() : "ru";
        Map<String, Object> translations = parseJsonMap(level.getTranslationsJson());
        Map<String, Object> locMap = extractLocaleMap(translations, loc);

        String title = extractString(locMap, "title", "name");
        if (title == null) title = "Level " + level.getLevelNumber();
        String summary = extractString(locMap, "summary", "description");
        String goal = extractString(locMap, "goal", "instruction");

        List<String> prereqs = parseJsonListStrings(level.getPrerequisitesJson());
        LevelRequirementsDto requirements = parseObject(level.getRequirementsJson(), LevelRequirementsDto.class,
                new LevelRequirementsDto(prereqs, List.of(), true, null));
        List<String> equipment = parseJsonListStrings(level.getAvailableEquipmentJson());
        List<String> materials = parseJsonListStrings(level.getAvailableMaterialsJson());
        ScenarioBindingDto scenario = parseObject(level.getScenarioJson(), ScenarioBindingDto.class,
                new ScenarioBindingDto(null, 1L, equipment, materials, Map.of()));
        List<StepDefinitionDto> steps = parseSteps(level.getStepsJson(), loc);
        LevelRewardsDto rewards = parseObject(level.getRewardsJson(), LevelRewardsDto.class,
                new LevelRewardsDto(null, List.of(), List.of(), List.of(), List.of()));

        return new LevelDefinitionDto(
                level.getId(),
                level.getTrackId(),
                level.getLevelNumber(),
                level.getSortOrder(),
                level.getDifficulty(),
                level.getEstimatedMinutes(),
                level.getStatus(),
                level.getPublishedVersion() != null ? level.getPublishedVersion() : level.getDraftVersion(),
                title,
                summary,
                goal,
                prereqs,
                requirements,
                equipment,
                materials,
                scenario,
                steps,
                rewards,
                translations
        );
    }

    private LevelDefinitionDto localizeDefinition(LevelDefinitionDto def, String locale) {
        String loc = locale != null && !locale.isBlank() ? locale.toLowerCase() : "ru";
        Map<String, Object> translations = def.translations();
        Map<String, Object> locMap = extractLocaleMap(translations, loc);

        String title = extractString(locMap, "title", "name");
        if (title == null) title = def.title();
        String summary = extractString(locMap, "summary", "description");
        if (summary == null) summary = def.summary();
        String goal = extractString(locMap, "goal");
        if (goal == null) goal = def.goal();

        List<StepDefinitionDto> localizedSteps = new ArrayList<>();
        if (def.steps() != null) {
            for (StepDefinitionDto s : def.steps()) {
                localizedSteps.add(localizeStep(s, loc));
            }
        }

        return new LevelDefinitionDto(
                def.id(),
                def.trackId(),
                def.levelNumber(),
                def.order(),
                def.difficulty(),
                def.estimatedMinutes(),
                def.status(),
                def.version(),
                title,
                summary,
                goal,
                def.prerequisites(),
                def.requirements(),
                def.availableEquipment(),
                def.availableMaterials(),
                def.scenario(),
                localizedSteps,
                def.rewards(),
                translations
        );
    }

    @SuppressWarnings("unchecked")
    private StepDefinitionDto localizeStep(StepDefinitionDto step, String locale) {
        if (step.translations() == null) return step;
        Map<String, Object> stepLocMap = extractLocaleMap(step.translations(), locale);
        String instruction = extractString(stepLocMap, "instruction", "title");

        List<GuideTargetDto> localizedGuideTargets = new ArrayList<>();
        if (step.guideTargets() != null) {
            for (GuideTargetDto gt : step.guideTargets()) {
                String targetText = gt.text();
                if (instruction != null && (targetText == null || targetText.isBlank())) {
                    targetText = instruction;
                }
                localizedGuideTargets.add(new GuideTargetDto(
                        gt.level(),
                        gt.kind(),
                        gt.id(),
                        gt.catalogCode(),
                        gt.itemId(),
                        gt.portId(),
                        gt.sourcePortType(),
                        gt.targetPortType(),
                        gt.placement(),
                        targetText,
                        gt.sequence()
                ));
            }
        }

        return new StepDefinitionDto(
                step.id(),
                step.order(),
                step.type(),
                step.translations(),
                step.checkpoint(),
                localizedGuideTargets
        );
    }

    private List<StepDefinitionDto> parseSteps(String json, String locale) {
        if (json == null || json.isBlank() || json.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            List<StepDefinitionDto> rawList = objectMapper.readValue(json, new TypeReference<List<StepDefinitionDto>>() {});
            List<StepDefinitionDto> localized = new ArrayList<>();
            for (StepDefinitionDto s : rawList) {
                localized.add(localizeStep(s, locale));
            }
            return localized;
        } catch (Exception e) {
            return new ArrayList<>();
        }
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

    private <T> T parseObject(String json, Class<T> clazz, T fallback) {
        if (json == null || json.isBlank() || json.equals("{}") || json.equals("[]")) {
            return fallback;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return fallback;
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
