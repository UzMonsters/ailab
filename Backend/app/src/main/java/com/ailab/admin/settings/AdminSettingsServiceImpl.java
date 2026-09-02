package com.ailab.admin.settings;

import com.ailab.admin.audit.AuditLogService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class AdminSettingsServiceImpl implements AdminSettingsService {

    private final AdminSettingsRepository settingsRepository;
    private final AdminSettingsHistoryRepository historyRepository;
    private final AdminSubjectRepository subjectRepository;
    private final AuditLogService auditLogService;

    public AdminSettingsServiceImpl(AdminSettingsRepository settingsRepository,
                                    AdminSettingsHistoryRepository historyRepository,
                                    AdminSubjectRepository subjectRepository,
                                    AuditLogService auditLogService) {
        this.settingsRepository = settingsRepository;
        this.historyRepository = historyRepository;
        this.subjectRepository = subjectRepository;
        this.auditLogService = auditLogService;
    }

    @PostConstruct
    public void initDefaults() {
        if (!settingsRepository.existsById("global")) {
            Map<String, Object> defaults = createDefaultSettingsMap();
            AdminSettingsEntity entity = new AdminSettingsEntity(
                    "global", defaults, 1L, "settings-v1", Instant.now(), "system", "System Initialization"
            );
            settingsRepository.save(entity);
            historyRepository.save(new AdminSettingsHistoryEntity(1L, "system", "System Initialization", List.of("all"), defaults));
        }

        if (subjectRepository.count() == 0) {
            subjectRepository.save(new AdminSubjectEntity("chemistry", "Chemistry", true, "#3B82F6", 1));
            subjectRepository.save(new AdminSubjectEntity("physics", "Physics", true, "#10B981", 2));
            subjectRepository.save(new AdminSubjectEntity("biology", "Biology", false, "#8B5CF6", 3));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSettings() {
        AdminSettingsEntity entity = getEntity();
        return toSettingsResponse(entity);
    }

    @Override
    public Map<String, Object> patchSettings(Map<String, Object> patch, String ifMatch, String actorId, String actorName) {
        AdminSettingsEntity entity = getEntity();
        validateIfMatch(entity.getVersion(), ifMatch);

        Map<String, Object> current = new LinkedHashMap<>(entity.getSettingsData());
        List<String> changedKeys = new ArrayList<>();

        for (Map.Entry<String, Object> entry : patch.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if ("appearance".equalsIgnoreCase(key) || "version".equalsIgnoreCase(key) || "etag".equalsIgnoreCase(key)) {
                continue;
            }
            if (val instanceof Map<?, ?> incomingGroup && current.get(key) instanceof Map<?, ?> existingGroup) {
                @SuppressWarnings("unchecked")
                Map<String, Object> mutableExisting = new LinkedHashMap<>((Map<String, Object>) existingGroup);
                @SuppressWarnings("unchecked")
                Map<String, Object> incomingMap = (Map<String, Object>) incomingGroup;

                for (Map.Entry<String, Object> subEntry : incomingMap.entrySet()) {
                    if (!Objects.equals(mutableExisting.get(subEntry.getKey()), subEntry.getValue())) {
                        mutableExisting.put(subEntry.getKey(), subEntry.getValue());
                        changedKeys.add(key + "." + subEntry.getKey());
                    }
                }
                current.put(key, mutableExisting);
            } else if (!Objects.equals(current.get(key), val)) {
                current.put(key, val);
                changedKeys.add(key);
            }
        }

        Long newVersion = entity.getVersion() + 1;
        String newEtag = "settings-v" + newVersion;
        Instant now = Instant.now();
        String safeActorId = actorId != null ? actorId : "usr_admin";
        String safeActorName = actorName != null ? actorName : "Admin";

        entity.update(current, newVersion, newEtag, now, safeActorId, safeActorName);
        settingsRepository.save(entity);

        historyRepository.save(new AdminSettingsHistoryEntity(newVersion, safeActorId, safeActorName, changedKeys, current));

        auditLogService.logEvent(
                safeActorId, safeActorName, "ADMIN",
                "setting.changed", "SYSTEM_SETTINGS", "global", "System settings",
                "SETTINGS", "ADMIN_WEB", "SUCCESS", "MEDIUM",
                entity.getSettingsData(), current, changedKeys,
                null, null, null, Map.of("version", newVersion)
        );

        return toSettingsResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSchema(String locale) {
        List<Map<String, Object>> groups = List.of(
                Map.of(
                        "key", "general",
                        "title", "General Settings",
                        "fields", List.of(
                                Map.of("key", "appName", "type", "STRING", "min", 2, "max", 100, "sensitive", false, "restartRequired", false),
                                Map.of("key", "defaultLocale", "type", "STRING", "allowed", List.of("ru", "en", "uz"), "sensitive", false, "restartRequired", false),
                                Map.of("key", "timezone", "type", "STRING", "sensitive", false, "restartRequired", false),
                                Map.of("key", "dateFormat", "type", "STRING", "sensitive", false, "restartRequired", false),
                                Map.of("key", "supportEmail", "type", "STRING", "sensitive", false, "restartRequired", false)
                        )
                ),
                Map.of(
                        "key", "languages",
                        "title", "Language Configurations",
                        "fields", List.of(
                                Map.of("key", "available", "type", "ARRAY_STRING", "sensitive", false, "restartRequired", false),
                                Map.of("key", "default", "type", "STRING", "allowed", List.of("ru", "en", "uz"), "sensitive", false, "restartRequired", false)
                        )
                ),
                Map.of(
                        "key", "learning",
                        "title", "Learning Configurations",
                        "fields", List.of(
                                Map.of("key", "enableLevels", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "enableBadges", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "minPassingScore", "type", "INTEGER", "min", 0, "max", 100, "sensitive", false, "restartRequired", false),
                                Map.of("key", "maxAttempts", "type", "INTEGER", "min", 1, "max", 100, "sensitive", false, "restartRequired", false)
                        )
                ),
                Map.of(
                        "key", "laboratory",
                        "title", "Laboratory Controls",
                        "fields", List.of(
                                Map.of("key", "workspaceGrid", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "objectsLimit", "type", "INTEGER", "min", 1, "max", 500, "sensitive", false, "restartRequired", false),
                                Map.of("key", "autosave", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "sharing", "type", "BOOLEAN", "sensitive", false, "restartRequired", false)
                        )
                ),
                Map.of(
                        "key", "simulation",
                        "title", "Simulation Engine Parameters",
                        "fields", List.of(
                                Map.of("key", "enableEvaporation", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "fluidTransfer", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "thermalShock", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "particles", "type", "BOOLEAN", "sensitive", false, "restartRequired", false)
                        )
                ),
                Map.of(
                        "key", "safety",
                        "title", "Safety Governance",
                        "fields", List.of(
                                Map.of("key", "enableWarnings", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "pauseOnCriticalFailure", "type", "BOOLEAN", "sensitive", false, "restartRequired", false)
                        )
                ),
                Map.of(
                        "key", "features",
                        "title", "Feature Toggles",
                        "fields", List.of(
                                Map.of("key", "sandboxBeta", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "achievements", "type", "BOOLEAN", "sensitive", false, "restartRequired", false),
                                Map.of("key", "aiAssistant", "type", "BOOLEAN", "sensitive", false, "restartRequired", false)
                        )
                ),
                Map.of(
                        "key", "administration",
                        "title", "Administration Controls",
                        "fields", List.of(
                                Map.of("key", "showEntityIds", "type", "BOOLEAN", "sensitive", false, "restartRequired", false)
                        )
                )
        );

        return Map.of("groups", groups);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getHistory(int page, int size, Instant from, Instant to, String actorId) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));

        Specification<AdminSettingsHistoryEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }
            if (actorId != null && !actorId.isBlank()) {
                predicates.add(cb.equal(root.get("actorId"), actorId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<AdminSettingsHistoryEntity> historyPage = historyRepository.findAll(
                spec, PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<Map<String, Object>> items = historyPage.getContent().stream().map(h -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("version", h.getVersion());
            m.put("actor", Map.of("id", h.getActorId(), "displayName", h.getActorName()));
            m.put("changedKeys", h.getChangedKeys());
            m.put("createdAt", h.getCreatedAt());
            return m;
        }).toList();

        Map<String, Object> pageMeta = Map.of(
                "number", historyPage.getNumber(),
                "size", historyPage.getSize(),
                "totalElements", historyPage.getTotalElements(),
                "totalPages", historyPage.getTotalPages()
        );

        return Map.of("items", items, "page", pageMeta);
    }

    @Override
    public Map<String, Object> restoreVersion(Long version, String reason, String actorId, String actorName) {
        AdminSettingsHistoryEntity history = historyRepository.findByVersion(version)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Settings version not found: " + version));

        AdminSettingsEntity currentEntity = getEntity();
        Long newVersion = currentEntity.getVersion() + 1;
        String newEtag = "settings-v" + newVersion;
        Instant now = Instant.now();
        String safeActorId = actorId != null ? actorId : "usr_admin";
        String safeActorName = actorName != null ? actorName : "Admin";

        Map<String, Object> restoredData = history.getSettingsSnapshot();
        currentEntity.update(restoredData, newVersion, newEtag, now, safeActorId, safeActorName);
        settingsRepository.save(currentEntity);

        historyRepository.save(new AdminSettingsHistoryEntity(newVersion, safeActorId, safeActorName, List.of("restored_from_v" + version), restoredData));

        auditLogService.logEvent(
                safeActorId, safeActorName, "ADMIN",
                "setting.restored", "SYSTEM_SETTINGS", "global", "System settings",
                "SETTINGS", "ADMIN_WEB", "SUCCESS", "HIGH",
                currentEntity.getSettingsData(), restoredData, List.of("version_restore"),
                null, null, null, Map.of("restoredFromVersion", version, "reason", reason != null ? reason : "Version restored")
        );

        return Map.of(
                "version", newVersion,
                "restoredFrom", version,
                "settings", restoredData
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSubjects() {
        return subjectRepository.findAllByOrderBySortOrderAsc().stream()
                .map(s -> Map.<String, Object>of(
                        "id", s.getId(),
                        "name", s.getName(),
                        "enabled", s.isEnabled(),
                        "accent", s.getAccent() != null ? s.getAccent() : "",
                        "order", s.getSortOrder()
                ))
                .toList();
    }

    @Override
    public Map<String, Object> patchSubject(String id, Map<String, Object> patch, String actorId, String actorName) {
        AdminSubjectEntity subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found: " + id));

        Boolean enabled = patch.containsKey("enabled") ? (Boolean) patch.get("enabled") : null;
        String accent = patch.containsKey("accent") ? (String) patch.get("accent") : null;
        Integer order = patch.containsKey("order") ? ((Number) patch.get("order")).intValue() : null;

        subject.update(enabled, accent, order);
        subjectRepository.save(subject);

        return Map.of(
                "id", subject.getId(),
                "name", subject.getName(),
                "enabled", subject.isEnabled(),
                "accent", subject.getAccent() != null ? subject.getAccent() : "",
                "order", subject.getSortOrder()
        );
    }

    private AdminSettingsEntity getEntity() {
        return settingsRepository.findById("global")
                .orElseGet(() -> {
                    Map<String, Object> defaults = createDefaultSettingsMap();
                    AdminSettingsEntity entity = new AdminSettingsEntity(
                            "global", defaults, 1L, "settings-v1", Instant.now(), "system", "System Initialization"
                    );
                    return settingsRepository.save(entity);
                });
    }

    private void validateIfMatch(Long currentVersion, String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) return;
        String clean = ifMatch.replace("\"", "").replace("W/", "").replace("settings-v", "").trim();
        try {
            long parsed = Long.parseLong(clean);
            if (!Objects.equals(currentVersion, parsed)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "VERSION_CONFLICT: Expected version " + parsed + " but found " + currentVersion);
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private Map<String, Object> toSettingsResponse(AdminSettingsEntity entity) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("settings", entity.getSettingsData());
        m.put("version", entity.getVersion());
        m.put("etag", entity.getEtag());
        m.put("updatedAt", entity.getUpdatedAt());
        m.put("updatedBy", Map.of("id", entity.getUpdatedById(), "displayName", entity.getUpdatedByName()));
        m.put("restartRequiredKeys", List.of());
        return m;
    }

    private Map<String, Object> createDefaultSettingsMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("general", Map.of(
                "appName", "jasScience",
                "environment", "PRODUCTION",
                "defaultLocale", "ru",
                "timezone", "Asia/Tashkent",
                "dateFormat", "DD.MM.YYYY",
                "supportEmail", "support@jasscience.com"
        ));
        map.put("languages", Map.of(
                "available", List.of("en", "ru", "uz"),
                "default", "ru"
        ));
        map.put("learning", Map.of(
                "enableLevels", true,
                "enableBadges", true,
                "enablePrerequisites", true,
                "allowReplay", true,
                "showLockedNames", true,
                "minPassingScore", 70,
                "maxAttempts", 5
        ));
        map.put("laboratory", Map.of(
                "workspaceGrid", true,
                "objectsLimit", 50,
                "autosave", true,
                "sharing", true
        ));
        map.put("simulation", Map.of(
                "enableEvaporation", true,
                "fluidTransfer", true,
                "thermalShock", true,
                "particles", false
        ));
        map.put("safety", Map.of(
                "enableWarnings", true,
                "pauseOnCriticalFailure", true
        ));
        map.put("features", Map.of(
                "sandboxBeta", true,
                "achievements", true,
                "aiAssistant", false
        ));
        map.put("administration", Map.of(
                "showEntityIds", false
        ));
        return map;
    }
}
