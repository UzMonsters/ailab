package com.ailab.learning.service;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningLevelPublishedSnapshotEntity;
import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.LearningTrackEntity;
import com.ailab.learning.dto.LearningDtos.LevelDefinitionDto;
import com.ailab.learning.repository.LearningLevelPublishedSnapshotRepository;
import com.ailab.learning.repository.LearningLevelRepository;
import com.ailab.learning.repository.LearningTrackRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
@Order(20)
public class LearningDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LearningDataSeeder.class);

    private final LearningTrackRepository trackRepository;
    private final LearningLevelRepository levelRepository;
    private final LearningLevelPublishedSnapshotRepository snapshotRepository;
    private final LearningLevelService levelService;
    private final ObjectMapper objectMapper;

    public LearningDataSeeder(
            LearningTrackRepository trackRepository,
            LearningLevelRepository levelRepository,
            LearningLevelPublishedSnapshotRepository snapshotRepository,
            LearningLevelService levelService,
            ObjectMapper objectMapper
    ) {
        this.trackRepository = trackRepository;
        this.levelRepository = levelRepository;
        this.snapshotRepository = snapshotRepository;
        this.levelService = levelService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedChemistryTrackAndLevelIfMissing();
    }

    public void seedChemistryTrackAndLevelIfMissing() {
        String trackCode = "chemistry";
        String trackId = "track-chemistry";

        Optional<LearningTrackEntity> existingTrack = trackRepository.findByCode(trackCode);
        LearningTrackEntity track;
        if (existingTrack.isEmpty()) {
            log.info("Seeding learning track '{}'...", trackCode);
            String trackTranslations = """
                    {
                      "ru": { "title": "Химия", "description": "Интерактивный курс общей и экспериментальной химии" },
                      "en": { "title": "Chemistry", "description": "Interactive general and experimental chemistry course" },
                      "uz": { "title": "Kimyo", "description": "Umumiy va amaliy kimyo interaktiv kursi" }
                    }
                    """;
            track = new LearningTrackEntity(trackId, trackCode, 1, "ru", trackTranslations);
            track.setStatus(LearningStatus.PUBLISHED);
            track = trackRepository.save(track);
            log.info("Learning track '{}' created", trackCode);
        } else {
            track = existingTrack.get();
        }

        String levelId = "lvl_1";
        Optional<LearningLevelEntity> existingLevel = levelRepository.findById(levelId);
        Optional<LearningLevelEntity> existingByTrack = levelRepository.findByTrackIdAndLevelNumber(track.getId(), 1);

        if (existingLevel.isPresent() || existingByTrack.isPresent()) {
            log.info("Learning level 1 already exists for track '{}' (id: '{}'), skipping seeding",
                    track.getId(), existingLevel.map(LearningLevelEntity::getId).orElseGet(() -> existingByTrack.get().getId()));
            return;
        }

        log.info("Seeding learning level '{}' (mixtures)...", levelId);
        LearningLevelEntity level = new LearningLevelEntity();
            level.setId(levelId);
            level.setTrackId(track.getId());
            level.setLevelNumber(1);
            level.setSortOrder(1);
            level.setDifficulty("BEGINNER");
            level.setEstimatedMinutes(15);
            level.setStatus(LearningStatus.PUBLISHED);
            level.setDraftVersion(1L);
            level.setPublishedVersion(1L);
            level.setPrerequisitesJson("[]");
            level.setRequirementsJson("{\"prerequisiteLevelIds\":[],\"requiredBadgeIds\":[],\"allowReplay\":true,\"maxAttempts\":null}");
            level.setAvailableEquipmentJson("[\"beaker_250ml\", \"graduated_cylinder_100ml\"]");
            level.setAvailableMaterialsJson("[\"water_distilled\", \"nacl\"]");
            level.setScenarioJson("{\"scenarioId\":\"scen_mixtures\",\"catalogVersion\":1,\"availableEquipmentIds\":[\"beaker_250ml\",\"graduated_cylinder_100ml\"],\"availableMaterialIds\":[\"water_distilled\",\"nacl\"],\"initialState\":{}}");
            level.setStepsJson("""
                    [
                      {
                        "id": "step-1",
                        "order": 1,
                        "type": "INTERACTION",
                        "translations": {
                          "ru": { "title": "Подготовка оборудования", "instruction": "Разместите стакан на рабочем столе" },
                          "en": { "title": "Equipment Setup", "instruction": "Place beaker on the workbench" },
                          "uz": { "title": "Uskunani o'rnatish", "instruction": "Stakanni ish stoliga qo'ying" }
                        },
                        "checkpoint": {
                          "factType": "AUTO",
                          "source": {},
                          "target": {},
                          "parameters": {}
                        },
                        "guideTargets": []
                      }
                    ]
                    """);
            level.setRewardsJson("{\"badgeId\":\"badge_chemistry_starter\",\"unlockLevelIds\":[],\"unlockEquipmentIds\":[],\"unlockMaterialIds\":[],\"unlockBookChapterIds\":[\"chapter-basics\"]}");
            level.setTranslationsJson("""
                    {
                      "ru": { "title": "Смеси", "code": "mixtures", "summary": "Изучение смесей и методов их разделения", "goal": "Научиться создавать и разделять растворы" },
                      "en": { "title": "Mixtures", "code": "mixtures", "summary": "Study of mixtures and separation methods", "goal": "Learn to create and separate solutions" },
                      "uz": { "title": "Aralashmalar", "code": "mixtures", "summary": "Aralashmalar va ularni ajratish usullarini o'rganish", "goal": "Eritmalar tayyorlash va ajratishni o'rganish" }
                    }
                    """);

            level = levelRepository.save(level);

            try {
                LevelDefinitionDto def = levelService.mapEntityToDefinitionDto(level, "ru");
                String snapshotJson = objectMapper.writeValueAsString(def);

                LearningLevelPublishedSnapshotEntity snapshot = new LearningLevelPublishedSnapshotEntity();
                snapshot.setId("snap-lvl-1");
                snapshot.setLevelId(levelId);
                snapshot.setVersion(1L);
                snapshot.setReleaseNote("Initial seed published level");
                snapshot.setPublishedById("system");
                snapshot.setPublishedByName("System Seeder");
                snapshot.setSnapshotDataJson(snapshotJson);
                snapshot.setPublishedAt(Instant.now());
                snapshotRepository.save(snapshot);
                log.info("Learning level '{}' snapshot published", levelId);
            } catch (Exception e) {
                log.error("Failed to serialize learning level '{}' snapshot: {}", levelId, e.getMessage());
            }
    }
}
