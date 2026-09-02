package com.ailab.learning.config;

import com.ailab.learning.domain.*;
import com.ailab.learning.dto.LearningDtos.LevelDefinitionDto;
import com.ailab.learning.repository.*;
import com.ailab.learning.service.LearningLevelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class LearningDataInitializer implements CommandLineRunner {

    private final LearningTrackRepository trackRepository;
    private final LearningLevelRepository levelRepository;
    private final LearningLevelPublishedSnapshotRepository snapshotRepository;
    private final LearningChapterRepository chapterRepository;
    private final LearningTaskRepository taskRepository;
    private final LearningRewardRepository rewardRepository;
    private final LearningLevelService levelService;
    private final ObjectMapper objectMapper;

    public LearningDataInitializer(
            LearningTrackRepository trackRepository,
            LearningLevelRepository levelRepository,
            LearningLevelPublishedSnapshotRepository snapshotRepository,
            LearningChapterRepository chapterRepository,
            LearningTaskRepository taskRepository,
            LearningRewardRepository rewardRepository,
            LearningLevelService levelService,
            ObjectMapper objectMapper
    ) {
        this.trackRepository = trackRepository;
        this.levelRepository = levelRepository;
        this.snapshotRepository = snapshotRepository;
        this.chapterRepository = chapterRepository;
        this.taskRepository = taskRepository;
        this.rewardRepository = rewardRepository;
        this.levelService = levelService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        seedChemistryTrack();
    }

    private void seedChemistryTrack() {
        String trackId = "track-chemistry";
        if (trackRepository.existsById(trackId)) {
            return;
        }

        LearningTrackEntity track = new LearningTrackEntity(
                trackId,
                "chemistry",
                1,
                "ru",
                "{\"ru\":{\"title\":\"Основы химии\",\"description\":\"Интерактивный курс виртуальной химической лаборатории\"},\"en\":{\"title\":\"Chemistry Foundations\",\"description\":\"Interactive virtual laboratory learning track\"},\"uz\":{\"title\":\"Kimyo asoslari\",\"description\":\"Interaktiv virtual laboratoriya o'quv kursi\"}}"
        );
        track.setStatus(LearningStatus.PUBLISHED);
        track.setDraftVersion(1);
        trackRepository.save(track);

        seedPublishedLevel1(trackId);
        seedPublishedLevel2(trackId);
        seedPublishedLevel3(trackId);
        seedPublishedLevel4(trackId);
        seedPublishedLevel5(trackId);
        seedComingSoonLevels(trackId);
        seedChaptersAndRewards(trackId);
    }

    private void seedPublishedLevel1(String trackId) {
        LearningLevelEntity level = new LearningLevelEntity();
        level.setId("level-chemistry-1");
        level.setTrackId(trackId);
        level.setLevelNumber(1);
        level.setSortOrder(1);
        level.setDifficulty("BEGINNER");
        level.setEstimatedMinutes(5);
        level.setStatus(LearningStatus.PUBLISHED);
        level.setDraftVersion(1);
        level.setPublishedVersion(1L);
        level.setPrerequisitesJson("[]");
        level.setRequirementsJson("{\"prerequisiteLevelIds\":[],\"requiredBadgeIds\":[],\"allowReplay\":true}");
        level.setAvailableEquipmentJson("[\"beaker_250ml\"]");
        level.setAvailableMaterialsJson("[]");
        level.setScenarioJson("{\"catalogVersion\":1,\"availableEquipmentIds\":[\"beaker_250ml\"],\"availableMaterialIds\":[]}");
        level.setStepsJson("[{\"id\":\"add-beaker\",\"order\":1,\"type\":\"CONTAINER_SETUP\",\"translations\":{\"ru\":{\"title\":\"Добавьте стакан\",\"instruction\":\"Перетащите химический стакан на рабочий стол лаборатории\"},\"en\":{\"title\":\"Add a beaker\",\"instruction\":\"Drag a beaker onto the laboratory workspace\"},\"uz\":{\"title\":\"Stakanni qo'shing\",\"instruction\":\"Kimyoviy stakanni ish stoliga qo'ying\"}},\"checkpoint\":{\"factType\":\"CONTAINER_PRESENT\",\"target\":{\"equipmentCode\":\"beaker\"}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"equipment\",\"placement\":\"top\",\"text\":\"Выберите вкладку оборудования\",\"sequence\":1},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"beaker_250ml\",\"placement\":\"top\",\"text\":\"Перетащите стакан на холст\",\"sequence\":2}]}]");
        level.setRewardsJson("{\"badgeId\":\"badge-first-step\",\"unlockLevelIds\":[\"level-chemistry-2\"],\"unlockEquipmentIds\":[\"beaker_250ml\"],\"unlockMaterialIds\":[],\"unlockBookChapterIds\":[]}");
        level.setTranslationsJson("{\"ru\":{\"title\":\"Знакомство с лабораторией\",\"summary\":\"Научитесь размещать базовую лабораторную посуду\",\"goal\":\"Разместите химический стакан на рабочей поверхности\"},\"en\":{\"title\":\"Introduction to Laboratory\",\"summary\":\"Learn how to place basic glassware\",\"goal\":\"Place a beaker on the workspace\"},\"uz\":{\"title\":\"Laboratoriya bilan tanishuv\",\"summary\":\"Asosiy idishlarni joylashtirishni o'rganing\",\"goal\":\"Ish stoliga stakanni joylashtiring\"}}");

        levelRepository.save(level);
        saveSnapshot(level, 1L);
    }

    private void seedPublishedLevel2(String trackId) {
        LearningLevelEntity level = new LearningLevelEntity();
        level.setId("level-chemistry-2");
        level.setTrackId(trackId);
        level.setLevelNumber(2);
        level.setSortOrder(2);
        level.setDifficulty("BEGINNER");
        level.setEstimatedMinutes(7);
        level.setStatus(LearningStatus.PUBLISHED);
        level.setDraftVersion(1);
        level.setPublishedVersion(1L);
        level.setPrerequisitesJson("[\"level-chemistry-1\"]");
        level.setRequirementsJson("{\"prerequisiteLevelIds\":[\"level-chemistry-1\"],\"requiredBadgeIds\":[],\"allowReplay\":true}");
        level.setAvailableEquipmentJson("[\"beaker_250ml\"]");
        level.setAvailableMaterialsJson("[\"H2O\"]");
        level.setScenarioJson("{\"catalogVersion\":1,\"availableEquipmentIds\":[\"beaker_250ml\"],\"availableMaterialIds\":[\"H2O\"]}");
        level.setStepsJson("[{\"id\":\"add-beaker\",\"order\":1,\"type\":\"CONTAINER_SETUP\",\"translations\":{\"ru\":{\"title\":\"Добавьте стакан\",\"instruction\":\"Разместите стакан на рабочем столе\"},\"en\":{\"title\":\"Add a beaker\",\"instruction\":\"Place a beaker on the workbench\"},\"uz\":{\"title\":\"Stakan qo'shing\",\"instruction\":\"Stakanni ish stoliga qo'ying\"}},\"checkpoint\":{\"factType\":\"CONTAINER_PRESENT\",\"target\":{\"equipmentCode\":\"beaker\"}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"equipment\"},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"beaker_250ml\"}]},{\"id\":\"add-water\",\"order\":2,\"type\":\"REAGENT_ADDITION\",\"translations\":{\"ru\":{\"title\":\"Налейте воду\",\"instruction\":\"Добавьте не менее 50 мл дистиллированной воды в стакан\"},\"en\":{\"title\":\"Pour water\",\"instruction\":\"Add at least 50 ml of water into the beaker\"},\"uz\":{\"title\":\"Suv quying\",\"instruction\":\"Stakanga kamida 50 ml suv quying\"}},\"checkpoint\":{\"factType\":\"MATERIAL_ADDED\",\"target\":{\"materialCode\":\"H2O\"},\"parameters\":{\"minVolumeMl\":50.0}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"materials\"},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"H2O\"}]}]");
        level.setRewardsJson("{\"badgeId\":\"badge-hydrologist\",\"unlockLevelIds\":[\"level-chemistry-3\"],\"unlockEquipmentIds\":[\"beaker_250ml\"],\"unlockMaterialIds\":[\"H2O\"],\"unlockBookChapterIds\":[]}");
        level.setTranslationsJson("{\"ru\":{\"title\":\"Работа с жидкостями\",\"summary\":\"Освойте дозирование и переливание воды\",\"goal\":\"Налейте 50 мл воды в химический стакан\"},\"en\":{\"title\":\"Working with Liquids\",\"summary\":\"Master liquid dispensing and pouring\",\"goal\":\"Pour 50 ml of water into a beaker\"},\"uz\":{\"title\":\"Suyuqliklar bilan ishlash\",\"summary\":\"Suv quyishni o'rganing\",\"goal\":\"Stakanga 50 ml suv quying\"}}");

        levelRepository.save(level);
        saveSnapshot(level, 1L);
    }

    private void seedPublishedLevel3(String trackId) {
        LearningLevelEntity level = new LearningLevelEntity();
        level.setId("level-chemistry-3");
        level.setTrackId(trackId);
        level.setLevelNumber(3);
        level.setSortOrder(3);
        level.setDifficulty("INTERMEDIATE");
        level.setEstimatedMinutes(10);
        level.setStatus(LearningStatus.PUBLISHED);
        level.setDraftVersion(1);
        level.setPublishedVersion(1L);
        level.setPrerequisitesJson("[\"level-chemistry-2\"]");
        level.setRequirementsJson("{\"prerequisiteLevelIds\":[\"level-chemistry-2\"],\"requiredBadgeIds\":[],\"allowReplay\":true}");
        level.setAvailableEquipmentJson("[\"beaker_250ml\",\"thermometer\"]");
        level.setAvailableMaterialsJson("[\"H2O\"]");
        level.setScenarioJson("{\"catalogVersion\":1,\"availableEquipmentIds\":[\"beaker_250ml\",\"thermometer\"],\"availableMaterialIds\":[\"H2O\"]}");
        level.setStepsJson("[{\"id\":\"add-beaker\",\"order\":1,\"type\":\"CONTAINER_SETUP\",\"translations\":{\"ru\":{\"title\":\"Добавьте стакан\",\"instruction\":\"Разместите стакан на рабочем столе\"},\"en\":{\"title\":\"Add a beaker\",\"instruction\":\"Place a beaker on the workbench\"},\"uz\":{\"title\":\"Stakan qo'shing\",\"instruction\":\"Stakanni ish stoliga qo'ying\"}},\"checkpoint\":{\"factType\":\"CONTAINER_PRESENT\",\"target\":{\"equipmentCode\":\"beaker\"}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"equipment\"},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"beaker_250ml\"}]},{\"id\":\"connect-thermometer\",\"order\":2,\"type\":\"PORT_CONNECTION\",\"translations\":{\"ru\":{\"title\":\"Подключите термометр\",\"instruction\":\"Соедините sensor port термометра с сосудом\"},\"en\":{\"title\":\"Connect the thermometer\",\"instruction\":\"Connect the thermometer sensor port to the vessel\"},\"uz\":{\"title\":\"Termometrni ulang\",\"instruction\":\"Termometr sensor portini idishga ulang\"}},\"checkpoint\":{\"factType\":\"SENSOR_CONNECTED\",\"source\":{\"equipmentCode\":\"thermometer\",\"portType\":\"SENSOR\"},\"target\":{\"capability\":\"CONTAINER\",\"portType\":\"SENSOR\"}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"equipment\"},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"thermometer\"},{\"level\":3,\"kind\":\"PORT_PAIR\",\"sourcePortType\":\"SENSOR\",\"targetPortType\":\"SENSOR\"}]}]");
        level.setRewardsJson("{\"badgeId\":\"badge-sensor-master\",\"unlockLevelIds\":[\"level-chemistry-4\"],\"unlockEquipmentIds\":[\"thermometer\"],\"unlockMaterialIds\":[],\"unlockBookChapterIds\":[]}");
        level.setTranslationsJson("{\"ru\":{\"title\":\"Измерения и датчики\",\"summary\":\"Подключите датчик температуры к сосуду\",\"goal\":\"Установите термометр и соедините порты с сосудом\"},\"en\":{\"title\":\"Sensors and Measurements\",\"summary\":\"Connect a temperature sensor to the vessel\",\"goal\":\"Set up a thermometer and link sensor ports\"},\"uz\":{\"title\":\"O'lchovlar va datchiklar\",\"summary\":\"Harorat datchigini idishga ulang\",\"goal\":\"Termometrni o'rnating va portlarni ulang\"}}");

        levelRepository.save(level);
        saveSnapshot(level, 1L);
    }

    private void seedPublishedLevel4(String trackId) {
        LearningLevelEntity level = new LearningLevelEntity();
        level.setId("level-chemistry-4");
        level.setTrackId(trackId);
        level.setLevelNumber(4);
        level.setSortOrder(4);
        level.setDifficulty("INTERMEDIATE");
        level.setEstimatedMinutes(12);
        level.setStatus(LearningStatus.PUBLISHED);
        level.setDraftVersion(1);
        level.setPublishedVersion(1L);
        level.setPrerequisitesJson("[\"level-chemistry-3\"]");
        level.setRequirementsJson("{\"prerequisiteLevelIds\":[\"level-chemistry-3\"],\"requiredBadgeIds\":[],\"allowReplay\":true}");
        level.setAvailableEquipmentJson("[\"beaker_250ml\",\"burner\"]");
        level.setAvailableMaterialsJson("[\"H2O\"]");
        level.setScenarioJson("{\"catalogVersion\":1,\"availableEquipmentIds\":[\"beaker_250ml\",\"burner\"],\"availableMaterialIds\":[\"H2O\"]}");
        level.setStepsJson("[{\"id\":\"setup-container\",\"order\":1,\"type\":\"CONTAINER_SETUP\",\"translations\":{\"ru\":{\"title\":\"Подготовьте сосуд с водой\",\"instruction\":\"Разместите стакан и добавьте воду\"},\"en\":{\"title\":\"Prepare water vessel\",\"instruction\":\"Place beaker and pour water\"},\"uz\":{\"title\":\"Suvli idishni tayyorlang\",\"instruction\":\"Stakanni qo'yib suv quying\"}},\"checkpoint\":{\"factType\":\"CONTAINER_PRESENT\",\"target\":{\"equipmentCode\":\"beaker\"}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"equipment\"},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"beaker_250ml\"}]},{\"id\":\"heat-vessel\",\"order\":2,\"type\":\"HEATING\",\"translations\":{\"ru\":{\"title\":\"Нагрейте воду\",\"instruction\":\"Подключите нагреватель и поднимите температуру выше 50°C\"},\"en\":{\"title\":\"Heat water\",\"instruction\":\"Connect heater and raise temperature above 50°C\"},\"uz\":{\"title\":\"Suvni qizdiring\",\"instruction\":\"Qizdirgichni ulab haroratni 50°C dan oshiring\"}},\"checkpoint\":{\"factType\":\"MEASUREMENT_RECORDED\",\"parameters\":{\"sensorType\":\"TEMPERATURE\",\"minValue\":50.0}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"equipment\"},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"burner\"}]}]");
        level.setRewardsJson("{\"badgeId\":\"badge-thermal-chemist\",\"unlockLevelIds\":[\"level-chemistry-5\"],\"unlockEquipmentIds\":[\"burner\"],\"unlockMaterialIds\":[],\"unlockBookChapterIds\":[]}");
        level.setTranslationsJson("{\"ru\":{\"title\":\"Тепловые процессы\",\"summary\":\"Изучите теплопередачу и нагревание растворов\",\"goal\":\"Нагрейте воду в стакане выше 50 градусов\"},\"en\":{\"title\":\"Thermal Processes\",\"summary\":\"Explore heat transfer and solution heating\",\"goal\":\"Heat water in the beaker above 50 degrees\"},\"uz\":{\"title\":\"Issiqlik jarayonlari\",\"summary\":\"Eritmalarni qizdirishni o'rganing\",\"goal\":\"Stakandagi suvni 50 darajadan yuqori qizdiring\"}}");

        levelRepository.save(level);
        saveSnapshot(level, 1L);
    }

    private void seedPublishedLevel5(String trackId) {
        LearningLevelEntity level = new LearningLevelEntity();
        level.setId("level-chemistry-5");
        level.setTrackId(trackId);
        level.setLevelNumber(5);
        level.setSortOrder(5);
        level.setDifficulty("ADVANCED");
        level.setEstimatedMinutes(15);
        level.setStatus(LearningStatus.PUBLISHED);
        level.setDraftVersion(1);
        level.setPublishedVersion(1L);
        level.setPrerequisitesJson("[\"level-chemistry-4\"]");
        level.setRequirementsJson("{\"prerequisiteLevelIds\":[\"level-chemistry-4\"],\"requiredBadgeIds\":[],\"allowReplay\":true}");
        level.setAvailableEquipmentJson("[\"beaker_250ml\",\"flask_250ml\"]");
        level.setAvailableMaterialsJson("[\"HCl\",\"NaOH\"]");
        level.setScenarioJson("{\"catalogVersion\":1,\"availableEquipmentIds\":[\"beaker_250ml\",\"flask_250ml\"],\"availableMaterialIds\":[\"HCl\",\"NaOH\"]}");
        level.setStepsJson("[{\"id\":\"add-acid\",\"order\":1,\"type\":\"REAGENT_ADDITION\",\"translations\":{\"ru\":{\"title\":\"Добавьте кислоту\",\"instruction\":\"Налейте 50 мл раствора соляной кислоты HCl\"},\"en\":{\"title\":\"Add acid\",\"instruction\":\"Pour 50 ml of HCl hydrochloric acid\"},\"uz\":{\"title\":\"Kislota quying\",\"instruction\":\"50 ml HCl kislotasini quying\"}},\"checkpoint\":{\"factType\":\"MATERIAL_ADDED\",\"target\":{\"materialCode\":\"HCl\"},\"parameters\":{\"minVolumeMl\":50.0}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"materials\"},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"HCl\"}]},{\"id\":\"neutralize-base\",\"order\":2,\"type\":\"REACTION_RUN\",\"translations\":{\"ru\":{\"title\":\"Проведите нейтрализацию\",\"instruction\":\"Добавьте гидроксид натрия NaOH для реакции нейтрализации\"},\"en\":{\"title\":\"Perform neutralization\",\"instruction\":\"Add NaOH sodium hydroxide to conduct neutralization\"},\"uz\":{\"title\":\"Neytrallash reaksiyasini bajaring\",\"instruction\":\"Neytrallash uchun NaOH qo'shing\"}},\"checkpoint\":{\"factType\":\"MATERIAL_ADDED\",\"target\":{\"materialCode\":\"NaOH\"},\"parameters\":{\"minVolumeMl\":50.0}},\"guideTargets\":[{\"level\":1,\"kind\":\"TAB\",\"id\":\"materials\"},{\"level\":2,\"kind\":\"ITEM\",\"catalogCode\":\"NaOH\"}]}]");
        level.setRewardsJson("{\"badgeId\":\"badge-reaction-init\",\"unlockLevelIds\":[],\"unlockEquipmentIds\":[\"flask_250ml\"],\"unlockMaterialIds\":[\"HCl\",\"NaOH\"],\"unlockBookChapterIds\":[]}");
        level.setTranslationsJson("{\"ru\":{\"title\":\"Реакция нейтрализации\",\"summary\":\"Смешайте кислоту и щелочь для получения соли и воды\",\"goal\":\"Проведите реакцию HCl + NaOH\"},\"en\":{\"title\":\"Neutralization Reaction\",\"summary\":\"Mix acid and alkali to produce salt and water\",\"goal\":\"Perform HCl + NaOH reaction\"},\"uz\":{\"title\":\"Neytrallanish reaksiyasi\",\"summary\":\"Kislota va ishqorni aralashtiring\",\"goal\":\"HCl + NaOH reaksiyasini bajaring\"}}");

        levelRepository.save(level);
        saveSnapshot(level, 1L);
    }

    private void seedComingSoonLevels(String trackId) {
        for (int i = 6; i <= 10; i++) {
            LearningLevelEntity level = new LearningLevelEntity();
            level.setId("level-chemistry-" + i);
            level.setTrackId(trackId);
            level.setLevelNumber(i);
            level.setSortOrder(i);
            level.setDifficulty(i > 8 ? "ADVANCED" : "INTERMEDIATE");
            level.setEstimatedMinutes(15);
            level.setStatus(LearningStatus.DRAFT);
            level.setDraftVersion(1);
            level.setPrerequisitesJson("[\"level-chemistry-" + (i - 1) + "\"]");
            level.setTranslationsJson("{\"ru\":{\"title\":\"Уровень " + i + "\",\"summary\":\"Скоро будет опубликован\"},\"en\":{\"title\":\"Level " + i + "\",\"summary\":\"Coming soon\"},\"uz\":{\"title\":\"" + i + "-bosqich\",\"summary\":\"Tez kunda\"}}");
            levelRepository.save(level);
        }
    }

    private void seedChaptersAndRewards(String trackId) {
        LearningChapterEntity ch1 = new LearningChapterEntity();
        ch1.setId("ch-foundations");
        ch1.setTrackId(trackId);
        ch1.setSortOrder(1);
        ch1.setLevelIdsJson("[\"level-chemistry-1\",\"level-chemistry-2\",\"level-chemistry-3\"]");
        ch1.setStatus(LearningStatus.PUBLISHED);
        ch1.setTranslationsJson("{\"ru\":{\"title\":\"Глава 1: Введение и основы посуды\"},\"en\":{\"title\":\"Chapter 1: Intro and Glassware\"},\"uz\":{\"title\":\"1-bob: Kirish va asosiy idishlar\"}}");
        chapterRepository.save(ch1);

        LearningRewardEntity r1 = new LearningRewardEntity();
        r1.setId("rew-first-step");
        r1.setCode("badge-first-step");
        r1.setRewardType(RewardType.BADGE);
        r1.setTranslationsJson("{\"ru\":{\"title\":\"Первый шаг в науку\",\"description\":\"Разместите первый предмет посуды\"},\"en\":{\"title\":\"First Step in Science\",\"description\":\"Place your first glassware\"},\"uz\":{\"title\":\"Ilmdagi ilk qadam\",\"description\":\"Birinchi idishni joylashtiring\"}}");
        rewardRepository.save(r1);

        LearningTaskEntity t1 = new LearningTaskEntity();
        t1.setId("task-port-conn");
        t1.setCode("PORT_CONNECTION_SENSOR");
        t1.setTaskType("PORT_CONNECTION");
        t1.setValidationRuleJson("{\"factType\":\"SENSOR_CONNECTED\"}");
        t1.setTranslationsJson("{\"ru\":{\"title\":\"Подключение сенсора\"},\"en\":{\"title\":\"Connect Sensor\"},\"uz\":{\"title\":\"Sensorni ulash\"}}");
        taskRepository.save(t1);
    }

    private void saveSnapshot(LearningLevelEntity level, long version) {
        LevelDefinitionDto def = levelService.mapEntityToDefinitionDto(level, "ru");
        try {
            String json = objectMapper.writeValueAsString(def);
            LearningLevelPublishedSnapshotEntity snap = new LearningLevelPublishedSnapshotEntity();
            snap.setId("snap-" + level.getId() + "-v" + version);
            snap.setLevelId(level.getId());
            snap.setVersion(version);
            snap.setReleaseNote("Initial canonical release");
            snap.setPublishedById("system");
            snap.setPublishedByName("System Bootstrapper");
            snap.setSnapshotDataJson(json);
            snap.setPublishedAt(Instant.now());
            snapshotRepository.save(snap);
        } catch (Exception ignored) {}
    }
}
