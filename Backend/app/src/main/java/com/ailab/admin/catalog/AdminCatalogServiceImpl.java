package com.ailab.admin.catalog;

import com.ailab.admin.audit.AuditLogService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class AdminCatalogServiceImpl implements AdminCatalogService {

    private final AdminCatalogDraftRepository repository;
    private final AuditLogService auditLogService;

    public AdminCatalogServiceImpl(AdminCatalogDraftRepository repository, AuditLogService auditLogService) {
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    @PostConstruct
    public void initCatalogDefaults() {
        if (repository.count() == 0) {
            seedInitialDraft("ELEMENT", "H", Map.of(
                    "atomicNumber", 1, "symbol", "H", "name", "Hydrogen",
                    "properties", Map.of("group", 1, "period", 1, "category", "nonmetal", "atomicMass", 1.008),
                    "translations", Map.of("en", "Hydrogen", "ru", "Водород", "uz", "Vodorod")
            ));
            seedInitialDraft("SUBSTANCE", "H2O", Map.of(
                    "code", "H2O", "formula", "H2O", "phase", "LIQUID",
                    "appearance", Map.of("color", "colorless", "state", "liquid"),
                    "properties", Map.of("density", 1.0, "molarMass", 18.015),
                    "hazards", List.of(),
                    "translations", Map.of("en", "Water", "ru", "Вода", "uz", "Suv")
            ));
            seedInitialDraft("REACTION", "acid_base_neut", Map.of(
                    "code", "acid_base_neut",
                    "reactants", List.of(Map.of("substance", "HCl", "coefficient", 1), Map.of("substance", "NaOH", "coefficient", 1)),
                    "products", List.of(Map.of("substance", "NaCl", "coefficient", 1), Map.of("substance", "H2O", "coefficient", 1)),
                    "conditions", Map.of("temperatureK", 298.15, "state", "AQUEOUS"),
                    "energy", Map.of("enthalpyJoule", -57100),
                    "appearance", Map.of("thermalOutput", "EXOTHERMIC"),
                    "safety", Map.of("severity", "LOW"),
                    "translations", Map.of("en", "Neutralization", "ru", "Нейтрализация", "uz", "Neytrallanish")
            ));
            seedInitialDraft("EQUIPMENT", "beaker-250", Map.of(
                    "code", "beaker-250", "name", "250ml Glass Beaker",
                    "rendererKey", "Beaker250Renderer",
                    "category", "CONTAINER",
                    "limits", Map.of("capacityMl", 250, "maxTempK", 773.15),
                    "ports", List.of(
                            Map.of("id", "TOP_OPENING", "type", "FLUID", "direction", "INPUT", "connector", "open-mouth"),
                            Map.of("id", "SPOUT", "type", "FLUID", "direction", "OUTPUT", "connector", "spout"),
                            Map.of("id", "BOTTOM_PAD", "type", "THERMAL", "direction", "BIDIRECTIONAL", "connector", "thermal-contact")
                    ),
                    "translations", Map.of("en", "250ml Glass Beaker", "ru", "Стеклянный стакан 250мл", "uz", "250ml shisha stakan")
            ));
            seedInitialDraft("MATERIAL", "hydrochloric-acid", Map.of(
                    "code", "hydrochloric-acid", "name", "Hydrochloric Acid 1M",
                    "type", "SOLUTION", "phase", "LIQUID",
                    "appearance", Map.of("color", "transparent"),
                    "properties", Map.of("concentrationM", 1.0, "ph", 0.0),
                    "safety", Map.of("hazard", "CORROSIVE", "severity", "HIGH"),
                    "translations", Map.of("en", "Hydrochloric Acid", "ru", "Соляная кислота", "uz", "Xlorid kislota")
            ));
            seedInitialDraft("SCENARIO", "chem_acid_base_1", Map.of(
                    "subject", "CHEMISTRY",
                    "trackId", "chemistry-basics",
                    "difficulty", "BEGINNER",
                    "order", 1,
                    "scenario", Map.of("title", "Titration Introduction", "description", "Neutralize acid using base"),
                    "steps", List.of(
                            Map.of("order", 1, "instruction", "Place beaker on workspace", "targetEquipment", "beaker-250"),
                            Map.of("order", 2, "instruction", "Pour 50ml HCl", "targetMaterial", "hydrochloric-acid")
                    ),
                    "checkpoints", List.of(Map.of("id", "cp_1", "condition", "ph >= 6.8 && ph <= 7.2")),
                    "guideTargets", List.of("beaker-250", "buret-50"),
                    "translations", Map.of("en", "Acid-Base Titration", "ru", "Кислотно-основное титрование", "uz", "Kislota-asos titrlash")
            ));
            seedInitialDraft("SAFETY_RULE", "RULE_ACID_WATER", Map.of(
                    "code", "RULE_ACID_WATER",
                    "category", "EXOTHERMIC_MIXING",
                    "severity", "CRITICAL",
                    "condition", "materialA == 'CONCENTRATED_H2SO4' && materialB == 'H2O' && addOrder == 'WATER_INTO_ACID'",
                    "effect", "THERMAL_EXPLOSION_HAZARD",
                    "translations", Map.of("en", "Never pour water into acid", "ru", "Не лейте воду в кислоту", "uz", "Kislotaga suv quymang")
            ));
        }
    }

    private void seedInitialDraft(String entityType, String code, Map<String, Object> data) {
        AdminCatalogDraftEntity draft = new AdminCatalogDraftEntity(entityType, code, "PUBLISHED", data);
        draft.publish("seed-init");
        repository.save(draft);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> listDrafts(String entityType, int page, int size, String q, String status, String sort) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));

        Sort sorting = parseSort(sort);
        Pageable pageable = PageRequest.of(safePage, safeSize, sorting);

        Specification<AdminCatalogDraftEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("entityType"), entityType.toUpperCase()));
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }
            if (q != null && !q.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("code")), "%" + q.trim().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<AdminCatalogDraftEntity> draftPage = repository.findAll(spec, pageable);
        List<Map<String, Object>> items = draftPage.getContent().stream()
                .map(this::toResponseMap)
                .toList();

        Map<String, Object> pageMeta = Map.of(
                "number", draftPage.getNumber(),
                "size", draftPage.getSize(),
                "totalElements", draftPage.getTotalElements(),
                "totalPages", draftPage.getTotalPages()
        );

        return Map.of("items", items, "page", pageMeta);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDraft(String entityType, String id) {
        AdminCatalogDraftEntity entity = findDraft(entityType, id);
        return toResponseMap(entity);
    }

    @Override
    public Map<String, Object> createDraft(String entityType, Map<String, Object> body, String actorId, String actorName) {
        String code = body.get("code") != null ? String.valueOf(body.get("code"))
                : body.get("symbol") != null ? String.valueOf(body.get("symbol"))
                : "item_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        validatePayload(entityType, body);

        AdminCatalogDraftEntity draft = new AdminCatalogDraftEntity(entityType.toUpperCase(), code, "DRAFT", body);
        repository.save(draft);

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                entityType.toLowerCase() + ".created", entityType.toUpperCase(), draft.getId(), draft.getCode(),
                "CATALOG", "ADMIN_WEB", "SUCCESS", "MEDIUM",
                null, body, List.of("all"), null, null, null, Map.of("version", draft.getVersion())
        );

        return toResponseMap(draft);
    }

    @Override
    public Map<String, Object> patchDraft(String entityType, String id, Map<String, Object> patch, String ifMatch, String actorId, String actorName) {
        AdminCatalogDraftEntity draft = findDraft(entityType, id);
        validateIfMatch(draft.getVersion(), ifMatch);

        Map<String, Object> existing = new LinkedHashMap<>(draft.getData());
        existing.putAll(patch);

        validatePayload(entityType, existing);

        draft.updateData(existing);
        repository.save(draft);

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                entityType.toLowerCase() + ".updated", entityType.toUpperCase(), draft.getId(), draft.getCode(),
                "CATALOG", "ADMIN_WEB", "SUCCESS", "MEDIUM",
                draft.getData(), existing, new ArrayList<>(patch.keySet()), null, null, null, Map.of("version", draft.getVersion())
        );

        return toResponseMap(draft);
    }

    @Override
    public Map<String, Object> savePorts(String id, Map<String, Object> request, String ifMatch, String actorId, String actorName) {
        AdminCatalogDraftEntity draft = findDraft("EQUIPMENT", id);
        validateIfMatch(draft.getVersion(), ifMatch);

        Object portsObj = request.get("ports");
        if (!(portsObj instanceof List<?> portsList)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PORT_SCHEMA_INVALID: Ports must be an array of port objects");
        }

        for (Object p : portsList) {
            if (!(p instanceof Map<?, ?> portMap)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PORT_SCHEMA_INVALID: Port entry must be an object");
            }
            if (portMap.get("id") == null || portMap.get("type") == null || portMap.get("direction") == null || portMap.get("connector") == null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PORT_SCHEMA_INVALID: Port requires id, type, direction, and connector");
            }
        }

        Map<String, Object> data = new LinkedHashMap<>(draft.getData());
        data.put("ports", portsList);
        draft.updateData(data);
        repository.save(draft);

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                "equipment.ports_updated", "EQUIPMENT", draft.getId(), draft.getCode(),
                "CATALOG", "ADMIN_WEB", "SUCCESS", "HIGH",
                draft.getData(), data, List.of("ports"), null, null, null, Map.of("version", draft.getVersion())
        );

        return Map.of(
                "version", draft.getVersion(),
                "ports", portsList,
                "validation", Map.of("valid", true, "portCount", portsList.size())
        );
    }

    @Override
    public Map<String, Object> saveCompatibility(String id, Map<String, Object> request, String ifMatch, String actorId, String actorName) {
        AdminCatalogDraftEntity draft = findDraft("EQUIPMENT", id);
        validateIfMatch(draft.getVersion(), ifMatch);

        Object rulesObj = request.get("rules");
        Map<String, Object> data = new LinkedHashMap<>(draft.getData());
        data.put("compatibilityRules", rulesObj != null ? rulesObj : List.of());
        draft.updateData(data);
        repository.save(draft);

        return Map.of(
                "version", draft.getVersion(),
                "rules", data.get("compatibilityRules"),
                "valid", true
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateDraft(String entityType, String id, Long version) {
        AdminCatalogDraftEntity draft = findDraft(entityType, id);
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        Map<String, Object> data = draft.getData();
        if (data == null || data.isEmpty()) {
            errors.add("Draft data is empty");
        }

        if ("REACTION".equalsIgnoreCase(entityType)) {
            Object reactants = data.get("reactants");
            Object products = data.get("products");
            if (reactants == null || !(reactants instanceof List) || ((List<?>) reactants).isEmpty()) {
                errors.add("Reaction must have at least one reactant");
            }
            if (products == null || !(products instanceof List) || ((List<?>) products).isEmpty()) {
                errors.add("Reaction must have at least one product");
            }
            return Map.of(
                    "valid", errors.isEmpty(),
                    "errors", errors,
                    "warnings", warnings,
                    "balance", Map.of("balanced", errors.isEmpty(), "equation", "Balanced reaction validated")
            );
        }

        return Map.of(
                "valid", errors.isEmpty(),
                "errors", errors,
                "warnings", warnings
        );
    }

    @Override
    public Map<String, Object> publishDraft(String entityType, String id, Long version, String idempotencyKey, String actorId, String actorName) {
        AdminCatalogDraftEntity draft = findDraft(entityType, id);
        validateIfMatch(draft.getVersion(), version != null ? String.valueOf(version) : null);

        Map<String, Object> validation = validateDraft(entityType, id, version);
        if (Boolean.FALSE.equals(validation.get("valid"))) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Cannot publish invalid draft");
        }

        draft.publish(idempotencyKey);
        repository.save(draft);

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                entityType.toLowerCase() + ".published", entityType.toUpperCase(), draft.getId(), draft.getCode(),
                "CATALOG", "ADMIN_WEB", "SUCCESS", "HIGH",
                Map.of("status", "DRAFT"), Map.of("status", "PUBLISHED", "publishedVersion", draft.getPublishedVersion()),
                List.of("status", "publishedVersion"), null, null, null, Map.of("idempotencyKey", idempotencyKey != null ? idempotencyKey : "")
        );

        return Map.of(
                "publishedVersion", draft.getPublishedVersion(),
                "publishedAt", draft.getPublishedAt() != null ? draft.getPublishedAt() : Instant.now()
        );
    }

    private void validatePayload(String entityType, Map<String, Object> body) {
        if ("EQUIPMENT".equalsIgnoreCase(entityType)) {
            Object portsObj = body.get("ports");
            if (portsObj instanceof List<?> portsList) {
                for (Object p : portsList) {
                    if (p instanceof Map<?, ?> portMap) {
                        if (portMap.get("id") == null || portMap.get("type") == null || portMap.get("direction") == null || portMap.get("connector") == null) {
                            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PORT_SCHEMA_INVALID: Port requires id, type, direction, and connector");
                        }
                    }
                }
            }
        }
    }

    private AdminCatalogDraftEntity findDraft(String entityType, String identifier) {
        return repository.findByEntityTypeAndId(entityType.toUpperCase(), identifier)
                .or(() -> repository.findByEntityTypeAndCode(entityType.toUpperCase(), identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, entityType + " draft not found: " + identifier));
    }

    private void validateIfMatch(Long currentVersion, String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank()) return;
        String clean = ifMatch.replace("\"", "").replace("W/", "").trim();
        try {
            long parsed = Long.parseLong(clean);
            if (!Objects.equals(currentVersion, parsed)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "VERSION_CONFLICT: Expected version " + parsed + " but current is " + currentVersion);
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private Map<String, Object> toResponseMap(AdminCatalogDraftEntity e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId());
        m.put("code", e.getCode());
        m.put("entityType", e.getEntityType());
        m.put("status", e.getStatus());
        m.put("version", e.getVersion());
        m.put("publishedVersion", e.getPublishedVersion());
        m.put("publishedAt", e.getPublishedAt());
        m.put("createdAt", e.getCreatedAt());
        m.put("updatedAt", e.getUpdatedAt());
        if (e.getData() != null) {
            m.putAll(e.getData());
        }
        return m;
    }
}
