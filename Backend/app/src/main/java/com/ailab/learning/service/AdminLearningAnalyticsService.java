package com.ailab.learning.service;

import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.LearningUserAttemptEntity;
import com.ailab.learning.dto.LearningDtos.HintUsageDto;
import com.ailab.learning.dto.LearningDtos.LearningOverviewResponse;
import com.ailab.learning.dto.LearningDtos.LevelAnalyticsResponse;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningUserAttemptRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class AdminLearningAnalyticsService {

    private final LearningLevelRepository levelRepository;
    private final LearningUserAttemptRepository attemptRepository;
    private final ObjectMapper objectMapper;

    public AdminLearningAnalyticsService(
            LearningLevelRepository levelRepository,
            LearningUserAttemptRepository attemptRepository,
            ObjectMapper objectMapper
    ) {
        this.levelRepository = levelRepository;
        this.attemptRepository = attemptRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public LearningOverviewResponse getOverview(String trackId, Instant from, Instant to) {
        List<LearningLevelEntity> levels = (trackId != null && !trackId.isBlank())
                ? levelRepository.findAllByTrackIdOrderBySortOrderAsc(trackId)
                : levelRepository.findAll();

        long total = levels.size();
        long published = levels.stream().filter(l -> l.getStatus() == LearningStatus.PUBLISHED).count();
        long draft = levels.stream().filter(l -> l.getStatus() == LearningStatus.DRAFT).count();
        long archived = levels.stream().filter(l -> l.getStatus() == LearningStatus.ARCHIVED).count();

        Map<String, Long> levelCounts = Map.of(
                "total", total,
                "published", published,
                "draft", draft,
                "archived", archived
        );

        List<LearningUserAttemptEntity> attempts = attemptRepository.findAll();
        long totalAttempts = attempts.size();
        long completedAttempts = attempts.stream().filter(a -> a.getStatus() == AttemptStatus.COMPLETED).count();

        double completionRate = totalAttempts > 0 ? ((double) completedAttempts / totalAttempts) * 100.0 : 0.0;

        List<Double> durations = new ArrayList<>();
        long totalHintUsage = 0;

        for (LearningUserAttemptEntity a : attempts) {
            if (a.getCompletedAt() != null && a.getStartedAt() != null) {
                double sec = Duration.between(a.getStartedAt(), a.getCompletedAt()).toSeconds();
                if (sec > 0) {
                    durations.add(sec);
                }
            }
            List<HintUsageDto> hints = parseHintUsage(a.getHintUsageJson());
            totalHintUsage += hints.size();
        }

        double avgDuration = durations.isEmpty() ? 0.0 : durations.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        return new LearningOverviewResponse(
                levelCounts,
                totalAttempts,
                completionRate,
                avgDuration,
                totalHintUsage
        );
    }

    @Transactional(readOnly = true)
    public LevelAnalyticsResponse getLevelAnalytics(String levelId, Instant from, Instant to) {
        List<LearningUserAttemptEntity> attempts = attemptRepository.findForAnalytics(levelId, from, to);

        long starts = attempts.size();
        long completions = 0;
        List<Double> durations = new ArrayList<>();
        Map<String, Long> dropOffByStep = new HashMap<>();
        Map<String, Long> hintsByStep = new HashMap<>();
        Map<String, Long> failures = new HashMap<>();

        for (LearningUserAttemptEntity a : attempts) {
            if (a.getStatus() == AttemptStatus.COMPLETED) {
                completions++;
                if (a.getStartedAt() != null && a.getCompletedAt() != null) {
                    double dur = Duration.between(a.getStartedAt(), a.getCompletedAt()).toSeconds();
                    if (dur > 0) durations.add(dur);
                }
            } else if (a.getStatus() == AttemptStatus.ABANDONED || a.getStatus() == AttemptStatus.ACTIVE) {
                String step = a.getCurrentStepId() != null ? a.getCurrentStepId() : "step-" + a.getCurrentStepIndex();
                dropOffByStep.put(step, dropOffByStep.getOrDefault(step, 0L) + 1);
            }

            List<HintUsageDto> hints = parseHintUsage(a.getHintUsageJson());
            for (HintUsageDto h : hints) {
                String step = h.stepId() != null ? h.stepId() : "step-1";
                hintsByStep.put(step, hintsByStep.getOrDefault(step, 0L) + 1);
            }
        }

        double completionRate = starts > 0 ? ((double) completions / starts) * 100.0 : 0.0;
        double medianDuration = calculateMedian(durations);

        return new LevelAnalyticsResponse(
                starts,
                completions,
                completionRate,
                medianDuration,
                dropOffByStep,
                hintsByStep,
                failures
        );
    }

    private double calculateMedian(List<Double> list) {
        if (list.isEmpty()) return 0.0;
        Collections.sort(list);
        int middle = list.size() / 2;
        if (list.size() % 2 == 1) {
            return list.get(middle);
        } else {
            return (list.get(middle - 1) + list.get(middle)) / 2.0;
        }
    }

    private List<HintUsageDto> parseHintUsage(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<HintUsageDto>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
