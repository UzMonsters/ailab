package com.ailab.learning.service;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.exception.LevelNotFoundException;
import com.ailab.learning.repository.LearningLevelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminLearningValidationService {

    private final LearningLevelRepository levelRepository;
    private final ObjectMapper objectMapper;

    public AdminLearningValidationService(
            LearningLevelRepository levelRepository,
            ObjectMapper objectMapper
    ) {
        this.levelRepository = levelRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public ValidationReportDto validateLevel(String levelId, Long version) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        List<ValidationErrorDto> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        validatePrerequisitesCycle(level, errors);
        validateTranslations(level, errors, warnings);
        validateSteps(level, errors, warnings);

        boolean valid = errors.isEmpty();
        return new ValidationReportDto(valid, errors, warnings);
    }

    private void validatePrerequisitesCycle(LearningLevelEntity level, List<ValidationErrorDto> errors) {
        List<String> prereqs = parseJsonListStrings(level.getPrerequisitesJson());
        if (prereqs.contains(level.getId())) {
            errors.add(new ValidationErrorDto("prerequisites", "SELF_PREREQUISITE", "Level cannot depend on itself"));
            return;
        }

        Set<String> visited = new HashSet<>();
        visited.add(level.getId());
        Queue<String> queue = new ArrayDeque<>(prereqs);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            if (currentId.equalsIgnoreCase(level.getId())) {
                errors.add(new ValidationErrorDto("prerequisites", "PREREQUISITE_CYCLE", "Circular prerequisite dependency detected: " + currentId));
                return;
            }
            if (visited.add(currentId)) {
                Optional<LearningLevelEntity> pLevel = levelRepository.findById(currentId);
                if (pLevel.isPresent()) {
                    List<String> nested = parseJsonListStrings(pLevel.get().getPrerequisitesJson());
                    queue.addAll(nested);
                }
            }
        }
    }

    private void validateTranslations(LearningLevelEntity level, List<ValidationErrorDto> errors, List<String> warnings) {
        Map<String, Object> translations = parseJsonMap(level.getTranslationsJson());
        String[] requiredLocales = {"ru", "en", "uz"};

        for (String loc : requiredLocales) {
            Object locObj = translations.get(loc);
            if (locObj == null) {
                warnings.add("Missing complete translations for locale: " + loc);
            } else if (locObj instanceof Map<?, ?> map) {
                if (!map.containsKey("title") || map.get("title").toString().isBlank()) {
                    warnings.add("Missing title for locale: " + loc);
                }
            }
        }
    }

    private void validateSteps(LearningLevelEntity level, List<ValidationErrorDto> errors, List<String> warnings) {
        List<StepDefinitionDto> steps = parseSteps(level.getStepsJson());
        if (steps.isEmpty()) {
            errors.add(new ValidationErrorDto("steps", "NO_STEPS", "Level must contain at least one step"));
            return;
        }

        for (int i = 0; i < steps.size(); i++) {
            StepDefinitionDto s = steps.get(i);
            if (s.id() == null || s.id().isBlank()) {
                errors.add(new ValidationErrorDto("steps[" + i + "].id", "MISSING_STEP_ID", "Step ID is required"));
            }
            if (s.checkpoint() == null) {
                warnings.add("Step " + s.id() + " has no checkpoint condition");
            }
            if (s.guideTargets() == null || s.guideTargets().isEmpty()) {
                warnings.add("Step " + s.id() + " has no guide targets defined");
            }
        }
    }

    private List<StepDefinitionDto> parseSteps(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<StepDefinitionDto>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
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
