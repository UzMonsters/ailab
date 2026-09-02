package com.ailab.learning.service;

import com.ailab.learning.domain.*;
import com.ailab.learning.dto.LearningDtos.*;
import com.ailab.learning.exception.LevelNotFoundException;
import com.ailab.learning.repository.*;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.exception.VersionConflictException;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AdminLearningService {

    private final LearningTrackRepository trackRepository;
    private final LearningLevelRepository levelRepository;
    private final LearningLevelPublishedSnapshotRepository snapshotRepository;
    private final LearningChapterRepository chapterRepository;
    private final LearningTaskRepository taskRepository;
    private final LearningRewardRepository rewardRepository;
    private final LearningUserProgressRepository progressRepository;
    private final LearningUserAttemptRepository attemptRepository;
    private final LearningProgressResetAuditRepository resetAuditRepository;
    private final LearningLevelService levelService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceStateRepository workspaceStateRepository;
    private final ObjectMapper objectMapper;

    public AdminLearningService(
            LearningTrackRepository trackRepository,
            LearningLevelRepository levelRepository,
            LearningLevelPublishedSnapshotRepository snapshotRepository,
            LearningChapterRepository chapterRepository,
            LearningTaskRepository taskRepository,
            LearningRewardRepository rewardRepository,
            LearningUserProgressRepository progressRepository,
            LearningUserAttemptRepository attemptRepository,
            LearningProgressResetAuditRepository resetAuditRepository,
            LearningLevelService levelService,
            WorkspaceRepository workspaceRepository,
            WorkspaceStateRepository workspaceStateRepository,
            ObjectMapper objectMapper
    ) {
        this.trackRepository = trackRepository;
        this.levelRepository = levelRepository;
        this.snapshotRepository = snapshotRepository;
        this.chapterRepository = chapterRepository;
        this.taskRepository = taskRepository;
        this.rewardRepository = rewardRepository;
        this.progressRepository = progressRepository;
        this.attemptRepository = attemptRepository;
        this.resetAuditRepository = resetAuditRepository;
        this.levelService = levelService;
        this.workspaceRepository = workspaceRepository;
        this.workspaceStateRepository = workspaceStateRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public TrackPageResponse listTracks(int page, int size, LearningStatus status) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.ASC, "sortOrder"));
        Page<LearningTrackEntity> p = status != null
                ? trackRepository.findAllByStatusOrderBySortOrderAsc(status, pageable)
                : trackRepository.findAll(pageable);

        List<TrackSummary> items = p.getContent().stream().map(t -> {
            Map<String, Object> translations = parseJsonMap(t.getTranslationsJson());
            Map<String, Object> locMap = extractLocaleMap(translations, t.getDefaultLocale());
            String title = extractString(locMap, "title", "name");
            if (title == null) title = t.getCode();
            String desc = extractString(locMap, "description", "summary");
            return new TrackSummary(t.getId(), t.getCode(), t.getSortOrder(), t.getDefaultLocale(), title, desc, t.getStatus(), t.getDraftVersion());
        }).toList();

        return new TrackPageResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Transactional
    public TrackDraftResponse createTrack(CreateTrackRequest request) {
        String id = "track-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String code = (request.code() != null && !request.code().isBlank())
                ? request.code()
                : "track-" + System.currentTimeMillis();
        int order = request.order() != null ? request.order() : 1;
        String defaultLocale = request.defaultLocale() != null ? request.defaultLocale() : "ru";
        String translationsJson = writeJson(request.translations() != null ? request.translations() : Map.of());

        LearningTrackEntity track = new LearningTrackEntity(id, code, order, defaultLocale, translationsJson);
        track.setStatus(LearningStatus.DRAFT);
        track.setDraftVersion(1);
        trackRepository.save(track);

        return new TrackDraftResponse(
                track.getId(),
                track.getCode(),
                track.getSortOrder(),
                track.getDefaultLocale(),
                track.getStatus(),
                track.getDraftVersion(),
                parseJsonMap(track.getTranslationsJson())
        );
    }

    @Transactional(readOnly = true)
    public LevelPageResponse listLevels(String trackId, LearningStatus status, String q, int page, int size, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.ASC, "sortOrder");
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            Sort.Direction dir = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            sortObj = Sort.by(dir, parts[0].trim());
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sortObj);
        Page<LearningLevelEntity> p = levelRepository.findLevelsFiltered(trackId, status, q, pageable);

        List<LevelSummary> items = p.getContent().stream().map(l -> {
            Map<String, Object> translations = parseJsonMap(l.getTranslationsJson());
            Map<String, Object> locMap = extractLocaleMap(translations, "ru");
            String title = extractString(locMap, "title", "name");
            if (title == null) title = "Level " + l.getLevelNumber();
            String summary = extractString(locMap, "summary", "description");
            boolean isComingSoon = l.getStatus() != LearningStatus.PUBLISHED;
            return new LevelSummary(
                    l.getId(),
                    l.getTrackId(),
                    l.getLevelNumber(),
                    l.getSortOrder(),
                    l.getDifficulty(),
                    l.getEstimatedMinutes(),
                    title,
                    summary,
                    l.getStatus(),
                    isComingSoon,
                    false,
                    l.getPublishedVersion()
            );
        }).toList();

        return new LevelPageResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Transactional
    public LevelDraftResponse createLevel(CreateLevelRequest request) {
        String trackId = request.trackId() != null && !request.trackId().isBlank() ? request.trackId() : "track-chemistry";
        int levelNum = request.levelNumber() != null ? request.levelNumber() : (int) levelRepository.countByTrackId(trackId) + 1;
        int order = request.order() != null ? request.order() : levelNum;
        String difficulty = request.difficulty() != null ? request.difficulty() : "BEGINNER";
        int estMin = request.estimatedMinutes() != null ? request.estimatedMinutes() : 10;
        String id = "level-" + trackId.replace("track-", "") + "-" + levelNum;

        LearningLevelEntity level = new LearningLevelEntity();
        level.setId(id);
        level.setTrackId(trackId);
        level.setLevelNumber(levelNum);
        level.setSortOrder(order);
        level.setDifficulty(difficulty);
        level.setEstimatedMinutes(estMin);
        level.setStatus(LearningStatus.DRAFT);
        level.setDraftVersion(1);
        level.setTranslationsJson(writeJson(request.translations() != null ? request.translations() : Map.of()));
        level.setPrerequisitesJson("[]");
        level.setRequirementsJson("{}");
        level.setAvailableEquipmentJson("[]");
        level.setAvailableMaterialsJson("[]");
        level.setScenarioJson("{}");
        level.setStepsJson("[]");
        level.setRewardsJson("{}");

        levelRepository.save(level);

        return new LevelDraftResponse(
                level.getId(),
                level.getTrackId(),
                level.getLevelNumber(),
                level.getSortOrder(),
                level.getDifficulty(),
                level.getEstimatedMinutes(),
                level.getStatus(),
                level.getDraftVersion(),
                parseJsonMap(level.getTranslationsJson())
        );
    }

    @Transactional(readOnly = true)
    public LevelEditorDocument getLevelEditorDocument(String levelId, String include) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        List<String> prereqs = parseJsonListStrings(level.getPrerequisitesJson());
        LevelRequirementsDto requirements = parseObject(level.getRequirementsJson(), LevelRequirementsDto.class,
                new LevelRequirementsDto(prereqs, List.of(), true, null));
        List<String> equipment = parseJsonListStrings(level.getAvailableEquipmentJson());
        List<String> materials = parseJsonListStrings(level.getAvailableMaterialsJson());
        ScenarioBindingDto scenario = parseObject(level.getScenarioJson(), ScenarioBindingDto.class,
                new ScenarioBindingDto(null, 1L, equipment, materials, Map.of()));
        List<StepDefinitionDto> steps = parseSteps(level.getStepsJson());
        LevelRewardsDto rewards = parseObject(level.getRewardsJson(), LevelRewardsDto.class,
                new LevelRewardsDto(null, List.of(), List.of(), List.of(), List.of()));
        Map<String, Object> translations = parseJsonMap(level.getTranslationsJson());

        return new LevelEditorDocument(
                level.getId(),
                level.getTrackId(),
                level.getLevelNumber(),
                level.getSortOrder(),
                level.getDifficulty(),
                level.getEstimatedMinutes(),
                level.getStatus(),
                level.getDraftVersion(),
                level.getPublishedVersion(),
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

    @Transactional
    public LevelDraftResponse patchLevel(String levelId, PatchLevelRequest request, String ifMatch) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        checkIfMatch(level.getDraftVersion(), ifMatch);

        if (request.difficulty() != null) level.setDifficulty(request.difficulty());
        if (request.estimatedMinutes() != null) level.setEstimatedMinutes(request.estimatedMinutes());
        if (request.requirements() != null) {
            level.setRequirementsJson(writeJson(request.requirements()));
            if (request.requirements().prerequisiteLevelIds() != null) {
                level.setPrerequisitesJson(writeJson(request.requirements().prerequisiteLevelIds()));
            }
        }
        if (request.translations() != null) {
            Map<String, Object> current = parseJsonMap(level.getTranslationsJson());
            current.putAll(request.translations());
            level.setTranslationsJson(writeJson(current));
        }

        level.setDraftVersion(level.getDraftVersion() + 1);
        level.setUpdatedAt(Instant.now());
        levelRepository.save(level);

        return new LevelDraftResponse(
                level.getId(),
                level.getTrackId(),
                level.getLevelNumber(),
                level.getSortOrder(),
                level.getDifficulty(),
                level.getEstimatedMinutes(),
                level.getStatus(),
                level.getDraftVersion(),
                parseJsonMap(level.getTranslationsJson())
        );
    }

    @Transactional
    public SaveStepsResponse saveSteps(String levelId, SaveStepsRequest request) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        if (request.version() > 0 && request.version() != level.getDraftVersion()) {
            throw new VersionConflictException(level.getDraftVersion(), request.version());
        }

        List<StepDefinitionDto> steps = request.steps() != null ? request.steps() : List.of();
        level.setStepsJson(writeJson(steps));
        level.setDraftVersion(level.getDraftVersion() + 1);
        level.setUpdatedAt(Instant.now());
        levelRepository.save(level);

        List<String> warnings = new ArrayList<>();
        if (steps.isEmpty()) {
            warnings.add("Level currently has 0 steps defined");
        }

        return new SaveStepsResponse(level.getDraftVersion(), steps, warnings);
    }

    @Transactional
    public SaveScenarioResponse saveScenario(String levelId, SaveScenarioRequest request) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        if (request.version() > 0 && request.version() != level.getDraftVersion()) {
            throw new VersionConflictException(level.getDraftVersion(), request.version());
        }

        ScenarioBindingDto binding = new ScenarioBindingDto(
                request.scenarioId(),
                request.catalogVersion(),
                request.availableEquipmentIds() != null ? request.availableEquipmentIds() : List.of(),
                request.availableMaterialIds() != null ? request.availableMaterialIds() : List.of(),
                request.initialState() != null ? request.initialState() : Map.of()
        );

        level.setScenarioJson(writeJson(binding));
        level.setAvailableEquipmentJson(writeJson(binding.availableEquipmentIds()));
        level.setAvailableMaterialsJson(writeJson(binding.availableMaterialIds()));
        level.setDraftVersion(level.getDraftVersion() + 1);
        level.setUpdatedAt(Instant.now());
        levelRepository.save(level);

        return new SaveScenarioResponse(level.getDraftVersion(), binding);
    }

    @Transactional
    public LevelRequirementsDto saveRequirements(String levelId, SaveRequirementsRequest request) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        if (request.version() > 0 && request.version() != level.getDraftVersion()) {
            throw new VersionConflictException(level.getDraftVersion(), request.version());
        }

        List<String> prereqs = request.prerequisiteLevelIds() != null ? request.prerequisiteLevelIds() : List.of();
        LevelRequirementsDto reqDto = new LevelRequirementsDto(
                prereqs,
                request.requiredBadgeIds() != null ? request.requiredBadgeIds() : List.of(),
                request.allowReplay() != null ? request.allowReplay() : true,
                request.maxAttempts()
        );

        level.setRequirementsJson(writeJson(reqDto));
        level.setPrerequisitesJson(writeJson(prereqs));
        level.setDraftVersion(level.getDraftVersion() + 1);
        level.setUpdatedAt(Instant.now());
        levelRepository.save(level);

        return reqDto;
    }

    @Transactional
    public LevelRewardsDto saveRewards(String levelId, SaveRewardsRequest request) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        if (request.version() > 0 && request.version() != level.getDraftVersion()) {
            throw new VersionConflictException(level.getDraftVersion(), request.version());
        }

        LevelRewardsDto rewardsDto = new LevelRewardsDto(
                request.badgeId(),
                request.unlockLevelIds() != null ? request.unlockLevelIds() : List.of(),
                request.unlockEquipmentIds() != null ? request.unlockEquipmentIds() : List.of(),
                request.unlockMaterialIds() != null ? request.unlockMaterialIds() : List.of(),
                request.unlockBookChapterIds() != null ? request.unlockBookChapterIds() : List.of()
        );

        level.setRewardsJson(writeJson(rewardsDto));
        level.setDraftVersion(level.getDraftVersion() + 1);
        level.setUpdatedAt(Instant.now());
        levelRepository.save(level);

        return rewardsDto;
    }

    @Transactional
    public SaveTranslationsResponse saveTranslations(String levelId, String locale, SaveTranslationsRequest request) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        String loc = locale != null && !locale.isBlank() ? locale.toLowerCase() : "ru";
        Map<String, Object> rootTranslations = parseJsonMap(level.getTranslationsJson());

        Map<String, Object> localeData = new HashMap<>();
        if (request.title() != null) localeData.put("title", request.title());
        if (request.summary() != null) localeData.put("summary", request.summary());
        if (request.goal() != null) localeData.put("goal", request.goal());
        if (request.steps() != null) localeData.put("steps", request.steps());
        if (request.reward() != null) localeData.put("reward", request.reward());

        rootTranslations.put(loc, localeData);
        level.setTranslationsJson(writeJson(rootTranslations));
        level.setDraftVersion(level.getDraftVersion() + 1);
        level.setUpdatedAt(Instant.now());
        levelRepository.save(level);

        List<String> missingKeys = new ArrayList<>();
        if (!localeData.containsKey("title")) missingKeys.add("title");
        if (!localeData.containsKey("summary")) missingKeys.add("summary");
        if (!localeData.containsKey("goal")) missingKeys.add("goal");

        double completeness = Math.max(0.0, (3.0 - missingKeys.size()) / 3.0 * 100.0);

        return new SaveTranslationsResponse(loc, completeness, missingKeys, level.getDraftVersion());
    }

    @Transactional
    public PreviewAttemptResponse createPreviewAttempt(String levelId, PreviewAttemptRequest request, String adminId) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        String previewId = "prev-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String sandboxUrl = "/sandbox?previewAttemptId=" + previewId + "&levelId=" + levelId;
        Instant expiresAt = Instant.now().plus(java.time.Duration.ofHours(2));

        String wsId = "ws-preview-" + previewId;
        LevelDefinitionDto levelDef = levelService.mapEntityToDefinitionDto(level, request != null ? request.locale() : "ru");

        WorkspaceEntity ws = new WorkspaceEntity(wsId, adminId != null ? adminId : "admin", "Preview: " + levelDef.title(), "CHEMISTRY", "preview");
        workspaceRepository.save(ws);
        WorkspaceStateEntity state = new WorkspaceStateEntity(wsId, 1);
        workspaceStateRepository.save(state);

        LearningUserAttemptEntity att = new LearningUserAttemptEntity();
        att.setId(previewId);
        att.setUserId(adminId);
        att.setPreview(true);
        att.setLevelId(levelId);
        att.setLevelVersion(level.getDraftVersion());
        att.setWorkspaceId(wsId);
        att.setExperimentId("exp-" + previewId);
        att.setStateVersion(1);
        att.setStatus(AttemptStatus.ACTIVE);
        att.setCurrentStepIndex(0);
        att.setCurrentStepId((levelDef.steps() != null && !levelDef.steps().isEmpty()) ? levelDef.steps().get(0).id() : "step-1");
        att.setStartedAt(Instant.now());
        att.setUpdatedAt(Instant.now());
        attemptRepository.save(att);

        return new PreviewAttemptResponse(previewId, sandboxUrl, expiresAt);
    }

    @Transactional
    public PublishResultDto publishLevel(String levelId, PublishLevelRequest request, String actorId, String actorName) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        String idempotencyKey = request != null ? request.idempotencyKey() : null;
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<LearningLevelPublishedSnapshotEntity> existingSnap = snapshotRepository.findByIdempotencyKey(idempotencyKey);
            if (existingSnap.isPresent()) {
                return new PublishResultDto(existingSnap.get().getVersion(), existingSnap.get().getPublishedAt());
            }
        }

        long nextVersion = (level.getPublishedVersion() != null ? level.getPublishedVersion() : 0) + 1;
        level.setPublishedVersion(nextVersion);
        level.setStatus(LearningStatus.PUBLISHED);
        level.setUpdatedAt(Instant.now());

        LevelDefinitionDto def = levelService.mapEntityToDefinitionDto(level, "ru");
        String snapshotJson = writeJson(def);

        LearningLevelPublishedSnapshotEntity snapshot = new LearningLevelPublishedSnapshotEntity();
        snapshot.setId("snap-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        snapshot.setLevelId(levelId);
        snapshot.setVersion(nextVersion);
        snapshot.setReleaseNote(request != null ? request.releaseNote() : "Version " + nextVersion);
        snapshot.setPublishedById(actorId != null ? actorId : "admin");
        snapshot.setPublishedByName(actorName != null ? actorName : "Administrator");
        snapshot.setSnapshotDataJson(snapshotJson);
        snapshot.setIdempotencyKey(idempotencyKey);
        snapshot.setPublishedAt(Instant.now());

        snapshotRepository.save(snapshot);
        levelRepository.save(level);

        return new PublishResultDto(nextVersion, snapshot.getPublishedAt());
    }

    @Transactional
    public ArchiveResultDto archiveLevel(String levelId, ArchiveLevelRequest request) {
        LearningLevelEntity level = levelRepository.findById(levelId)
                .orElseThrow(() -> new LevelNotFoundException("Level not found: " + levelId));

        level.setStatus(LearningStatus.ARCHIVED);
        level.setUpdatedAt(Instant.now());
        levelRepository.save(level);

        return new ArchiveResultDto(LearningStatus.ARCHIVED.name());
    }

    @Transactional(readOnly = true)
    public List<ChapterDto> listChapters(String trackId) {
        String effectiveTrack = (trackId != null && !trackId.isBlank()) ? trackId : "track-chemistry";
        return chapterRepository.findAllByTrackIdOrderBySortOrderAsc(effectiveTrack).stream()
                .map(c -> new ChapterDto(c.getId(), c.getTrackId(), c.getSortOrder(), parseJsonListStrings(c.getLevelIdsJson()), c.getStatus(), parseJsonMap(c.getTranslationsJson())))
                .toList();
    }

    @Transactional
    public ChapterDto createChapter(CreateChapterRequest request) {
        String id = "ch-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String trackId = request.trackId() != null ? request.trackId() : "track-chemistry";
        int order = request.order() != null ? request.order() : 1;
        String levelIdsJson = writeJson(request.levelIds() != null ? request.levelIds() : List.of());
        String translationsJson = writeJson(request.translations() != null ? request.translations() : Map.of());

        LearningChapterEntity chapter = new LearningChapterEntity();
        chapter.setId(id);
        chapter.setTrackId(trackId);
        chapter.setSortOrder(order);
        chapter.setLevelIdsJson(levelIdsJson);
        chapter.setTranslationsJson(translationsJson);
        chapter.setStatus(LearningStatus.DRAFT);
        chapterRepository.save(chapter);

        return new ChapterDto(chapter.getId(), chapter.getTrackId(), chapter.getSortOrder(), parseJsonListStrings(chapter.getLevelIdsJson()), chapter.getStatus(), parseJsonMap(chapter.getTranslationsJson()));
    }

    @Transactional(readOnly = true)
    public List<TaskDto> listTasks() {
        return taskRepository.findAll().stream()
                .map(t -> new TaskDto(t.getId(), t.getCode(), t.getTaskType(), parseJsonMap(t.getValidationRuleJson()), parseJsonMap(t.getGuideTemplateJson()), parseJsonMap(t.getTranslationsJson())))
                .toList();
    }

    @Transactional
    public TaskDto createTask(CreateTaskRequest request) {
        String id = "task-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        LearningTaskEntity task = new LearningTaskEntity();
        task.setId(id);
        task.setCode(request.code() != null ? request.code() : id);
        task.setTaskType(request.type() != null ? request.type() : "CUSTOM");
        task.setValidationRuleJson(writeJson(request.validationRule() != null ? request.validationRule() : Map.of()));
        task.setGuideTemplateJson(writeJson(request.guideTemplate() != null ? request.guideTemplate() : Map.of()));
        task.setTranslationsJson(writeJson(request.translations() != null ? request.translations() : Map.of()));
        taskRepository.save(task);

        return new TaskDto(task.getId(), task.getCode(), task.getTaskType(), parseJsonMap(task.getValidationRuleJson()), parseJsonMap(task.getGuideTemplateJson()), parseJsonMap(task.getTranslationsJson()));
    }

    @Transactional(readOnly = true)
    public List<RewardAdminDto> listRewards() {
        return rewardRepository.findAll().stream()
                .map(r -> new RewardAdminDto(r.getId(), r.getCode(), r.getRewardType(), r.getAssetId(), parseJsonMap(r.getCriteriaJson()), parseJsonMap(r.getTranslationsJson())))
                .toList();
    }

    @Transactional
    public RewardAdminDto createReward(CreateRewardRequest request) {
        String id = "rew-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        LearningRewardEntity rew = new LearningRewardEntity();
        rew.setId(id);
        rew.setCode(request.code() != null ? request.code() : id);
        rew.setRewardType(request.type() != null ? request.type() : RewardType.BADGE);
        rew.setAssetId(request.assetId());
        rew.setCriteriaJson(writeJson(request.criteria() != null ? request.criteria() : Map.of()));
        rew.setTranslationsJson(writeJson(request.translations() != null ? request.translations() : Map.of()));
        rewardRepository.save(rew);

        return new RewardAdminDto(rew.getId(), rew.getCode(), rew.getRewardType(), rew.getAssetId(), parseJsonMap(rew.getCriteriaJson()), parseJsonMap(rew.getTranslationsJson()));
    }

    @Transactional(readOnly = true)
    public AdminProgressPageResponse listProgress(String trackId, String levelId, String status, String q, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<LearningUserProgressEntity> p = progressRepository.findProgressFiltered(trackId, q, pageable);

        List<AdminProgressItemDto> items = p.getContent().stream().map(pr -> {
            List<LearningUserAttemptEntity> attempts = attemptRepository.findAllByUserIdAndLevelIdOrderByStartedAtDesc(
                    pr.getUserId(), pr.getCurrentLevelId() != null ? pr.getCurrentLevelId() : "level-1");
            double totalDur = 0;
            for (LearningUserAttemptEntity a : attempts) {
                if (a.getStartedAt() != null && a.getCompletedAt() != null) {
                    totalDur += java.time.Duration.between(a.getStartedAt(), a.getCompletedAt()).toSeconds();
                }
            }
            return new AdminProgressItemDto(
                    pr.getUserId(),
                    pr.getCurrentLevelId() != null ? pr.getCurrentLevelId() : "level-1",
                    "ACTIVE",
                    attempts.size(),
                    totalDur,
                    pr.getUpdatedAt()
            );
        }).toList();

        return new AdminProgressPageResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Transactional
    public ResetProgressResponse resetUserProgress(String userId, ResetProgressRequest request, String adminId) {
        String resetJobId = "job-reset-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String reason = (request != null && request.reason() != null) ? request.reason() : "Admin manual reset";

        LearningProgressResetAuditEntity audit = new LearningProgressResetAuditEntity(
                resetJobId,
                userId,
                request != null ? request.trackId() : "track-chemistry",
                request != null ? request.levelId() : null,
                reason,
                adminId != null ? adminId : "admin"
        );
        audit.setStatus("COMPLETED");
        resetAuditRepository.save(audit);

        String trackId = request != null && request.trackId() != null ? request.trackId() : "track-chemistry";
        Optional<LearningUserProgressEntity> progOpt = progressRepository.findByUserIdAndTrackId(userId, trackId);
        if (progOpt.isPresent()) {
            LearningUserProgressEntity prog = progOpt.get();
            if (request != null && request.levelId() != null) {
                List<String> comp = parseJsonListStrings(prog.getCompletedLevelIdsJson());
                comp.remove(request.levelId());
                prog.setCompletedLevelIdsJson(writeJson(comp));
            } else {
                prog.setCompletedLevelIdsJson("[]");
                prog.setBadgesJson("[]");
                prog.setUnlockedEquipmentJson("[]");
                prog.setUnlockedMaterialsJson("[]");
                prog.setUnlockedBookChaptersJson("[]");
            }
            prog.setUpdatedAt(Instant.now());
            progressRepository.save(prog);
        }

        return new ResetProgressResponse(resetJobId, "COMPLETED");
    }

    @Transactional(readOnly = true)
    public LocalizationPageResponse getLocalizationSummary(String entityType, String locale, String status, int page, int size) {
        List<LearningLevelEntity> levels = levelRepository.findAll();
        String loc = locale != null && !locale.isBlank() ? locale.toLowerCase() : "ru";

        List<LocalizationItemDto> items = new ArrayList<>();
        for (LearningLevelEntity l : levels) {
            Map<String, Object> translations = parseJsonMap(l.getTranslationsJson());
            Map<String, Object> locMap = extractLocaleMap(translations, loc);

            List<String> missing = new ArrayList<>();
            if (!locMap.containsKey("title")) missing.add("title");
            if (!locMap.containsKey("summary")) missing.add("summary");
            if (!locMap.containsKey("goal")) missing.add("goal");

            double completeness = Math.max(0.0, (3.0 - missing.size()) / 3.0 * 100.0);
            items.add(new LocalizationItemDto(l.getId(), loc, completeness, missing));
        }

        return new LocalizationPageResponse(items, 0, items.size(), items.size(), 1);
    }

    private void checkIfMatch(long currentVersion, String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank() || ifMatch.equals("*")) {
            return;
        }
        String clean = ifMatch.replace("\"", "").trim();
        try {
            long clientVersion = Long.parseLong(clean);
            if (clientVersion != currentVersion) {
                throw new VersionConflictException(currentVersion, clientVersion);
            }
        } catch (NumberFormatException ignored) {}
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

    private String writeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
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
