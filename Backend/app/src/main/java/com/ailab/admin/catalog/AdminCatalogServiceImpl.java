package com.ailab.admin.catalog;

import com.ailab.admin.audit.AuditLogService;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.FormulaParser;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "updatedAt", "createdAt", "code", "status", "version", "publishedVersion", "entityType"
    );

    private final AdminCatalogDraftRepository repository;
    private final AuditLogService auditLogService;
    private final FormulaParser formulaParser;

    @Autowired
    public AdminCatalogServiceImpl(AdminCatalogDraftRepository repository, AuditLogService auditLogService) {
        this.repository = repository;
        this.auditLogService = auditLogService;
        this.formulaParser = new DefaultFormulaParser();
    }

    public AdminCatalogServiceImpl(AdminCatalogDraftRepository repository, AuditLogService auditLogService, FormulaParser formulaParser) {
        this.repository = repository;
        this.auditLogService = auditLogService;
        this.formulaParser = formulaParser != null ? formulaParser : new DefaultFormulaParser();
    }

    @PostConstruct
    public void initCatalogDefaults() {
        if (repository.count() == 0) {
            seedInitialDraft("ELEMENT", "H", Map.of(
                    "atomicNumber", 1, "symbol", "H", "name", "Hydrogen",
                    "properties", Map.of("group", 1, "period", 1, "category", "nonmetal", "atomicMass", 1.008),
                    "translations", Map.of("en", Map.of("name", "Hydrogen"), "ru", Map.of("name", "Водород"), "uz", Map.of("name", "Vodorod"))
            ));
            seedInitialDraft("SUBSTANCE", "H2O", Map.of(
                    "code", "H2O", "formula", "H2O", "phase", "LIQUID",
                    "appearance", Map.of("color", "colorless", "state", "liquid"),
                    "properties", Map.of("density", 1.0, "molarMass", 18.015),
                    "hazards", List.of(),
                    "translations", Map.of("en", Map.of("name", "Water"), "ru", Map.of("name", "Вода"), "uz", Map.of("name", "Suv"))
            ));
            seedInitialDraft("REACTION", "acid_base_neut", Map.of(
                    "code", "acid_base_neut",
                    "reactants", List.of(Map.of("substance", "HCl", "coefficient", 1), Map.of("substance", "NaOH", "coefficient", 1)),
                    "products", List.of(Map.of("substance", "NaCl", "coefficient", 1), Map.of("substance", "H2O", "coefficient", 1)),
                    "conditions", Map.of("temperatureK", 298.15, "state", "AQUEOUS"),
                    "energy", Map.of("enthalpyJoule", -57100),
                    "appearance", Map.of("thermalOutput", "EXOTHERMIC"),
                    "safety", Map.of("severity", "LOW"),
                    "translations", Map.of("en", Map.of("name", "Neutralization"), "ru", Map.of("name", "Нейтрализация"), "uz", Map.of("name", "Neytrallanish"))
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
                    "translations", Map.of("en", Map.of("name", "250ml Glass Beaker"), "ru", Map.of("name", "Стеклянный стакан 250мл"), "uz", Map.of("name", "250ml shisha stakan"))
            ));
            seedInitialDraft("MATERIAL", "hydrochloric-acid", Map.of(
                    "code", "hydrochloric-acid", "name", "Hydrochloric Acid 1M",
                    "formula", "HCl",
                    "type", "SOLUTION", "phase", "LIQUID",
                    "appearance", Map.of("color", "transparent"),
                    "properties", Map.of("concentrationM", 1.0, "ph", 0.0),
                    "safety", Map.of("hazard", "CORROSIVE", "severity", "HIGH"),
                    "translations", Map.of("en", Map.of("name", "Hydrochloric Acid"), "ru", Map.of("name", "Соляная кислота"), "uz", Map.of("name", "Xlorid kislota"))
            ));
            seedInitialDraft("SCENARIO", "chem_acid_base_1", Map.of(
                    "subject", "CHEMISTRY",
                    "trackId", "chemistry-basics",
                    "difficulty", "BEGINNER",
                    "order", 1,
                    "initialScene", Map.of("alias", "beaker-250", "equipmentCode", "beaker-250"),
                    "scenario", Map.of("title", "Titration Introduction", "description", "Neutralize acid using base"),
                    "steps", List.of(
                            Map.of("order", 1, "instruction", "Place beaker on workspace", "targetEquipment", "beaker-250"),
                            Map.of("order", 2, "instruction", "Pour 50ml HCl", "targetMaterial", "hydrochloric-acid")
                    ),
                    "checkpoints", List.of(Map.of("id", "cp_1", "condition", "ph >= 6.8 && ph <= 7.2")),
                    "guideTargets", List.of("beaker-250"),
                    "translations", Map.of("en", Map.of("name", "Acid-Base Titration"), "ru", Map.of("name", "Кислотно-основное титрование"), "uz", Map.of("name", "Kislota-asos titrlash"))
            ));
            seedInitialDraft("SAFETY_RULE", "RULE_ACID_WATER", Map.of(
                    "code", "RULE_ACID_WATER",
                    "category", "EXOTHERMIC_MIXING",
                    "severity", "CRITICAL",
                    "condition", "materialA == 'CONCENTRATED_H2SO4' && materialB == 'H2O' && addOrder == 'WATER_INTO_ACID'",
                    "effect", "THERMAL_EXPLOSION_HAZARD",
                    "translations", Map.of("en", Map.of("name", "Never pour water into acid"), "ru", Map.of("name", "Не лейте воду в кислоту"), "uz", Map.of("name", "Kislotaga suv quymang"))
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

        Map<String, Object> pageMeta = new LinkedHashMap<>();
        pageMeta.put("page", draftPage.getNumber());
        pageMeta.put("number", draftPage.getNumber());
        pageMeta.put("size", draftPage.getSize());
        pageMeta.put("totalElements", draftPage.getTotalElements());
        pageMeta.put("totalPages", draftPage.getTotalPages());

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
        if (body == null) {
            body = new LinkedHashMap<>();
        }
        String code = body.get("code") != null ? String.valueOf(body.get("code"))
                : body.get("symbol") != null ? String.valueOf(body.get("symbol"))
                : "item_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        validatePayload(entityType, body);

        AdminCatalogDraftEntity draft = new AdminCatalogDraftEntity(entityType.toUpperCase(), code, "DRAFT", body);
        draft = repository.save(draft);

        String safeActorId = (actorId != null && !actorId.isBlank()) ? actorId : "usr_admin";
        String safeActorName = (actorName != null && !actorName.isBlank()) ? actorName : "Admin User";

        auditLogService.logEvent(
                safeActorId, safeActorName, "ADMIN",
                entityType.toLowerCase() + ".created", entityType.toUpperCase(), draft.getId(), draft.getCode(),
                "CATALOG", "ADMIN_WEB", "SUCCESS", "MEDIUM",
                null, new LinkedHashMap<>(body), List.of("all"), null, null, null, Map.of("version", draft.getVersion())
        );

        return toResponseMap(draft);
    }

    @Override
    public Map<String, Object> patchDraft(String entityType, String id, Map<String, Object> patch, String ifMatch, String actorId, String actorName) {
        AdminCatalogDraftEntity draft = findDraft(entityType, id);

        Long expectedVersion = null;
        if (patch.get("expectedVersion") instanceof Number n) {
            expectedVersion = n.longValue();
        } else if (patch.get("version") instanceof Number n) {
            expectedVersion = n.longValue();
        }

        if (expectedVersion != null) {
            validateIfMatch(draft.getVersion(), String.valueOf(expectedVersion));
        } else {
            validateIfMatch(draft.getVersion(), ifMatch);
        }

        Map<String, Object> beforeState = draft.getData() != null ? new LinkedHashMap<>(draft.getData()) : Map.of();
        Map<String, Object> existing = new LinkedHashMap<>(beforeState);
        existing.putAll(patch);

        validatePayload(entityType, existing);

        draft.updateData(existing);
        repository.save(draft);

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                entityType.toLowerCase() + ".updated", entityType.toUpperCase(), draft.getId(), draft.getCode(),
                "CATALOG", "ADMIN_WEB", "SUCCESS", "MEDIUM",
                beforeState, existing, new ArrayList<>(patch.keySet()), null, null, null, Map.of("version", draft.getVersion())
        );

        return toResponseMap(draft);
    }

    @Override
    public Map<String, Object> savePorts(String id, Map<String, Object> request, String ifMatch, String actorId, String actorName) {
        AdminCatalogDraftEntity draft = findDraft("EQUIPMENT", id);

        Long expectedVersion = null;
        if (request.get("expectedVersion") instanceof Number n) {
            expectedVersion = n.longValue();
        } else if (request.get("version") instanceof Number n) {
            expectedVersion = n.longValue();
        }

        if (expectedVersion != null) {
            validateIfMatch(draft.getVersion(), String.valueOf(expectedVersion));
        } else {
            validateIfMatch(draft.getVersion(), ifMatch);
        }

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

        Map<String, Object> beforeState = draft.getData() != null ? new LinkedHashMap<>(draft.getData()) : Map.of();
        Map<String, Object> data = new LinkedHashMap<>(beforeState);
        data.put("ports", portsList);
        draft.updateData(data);
        repository.save(draft);

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                "equipment.ports_updated", "EQUIPMENT", draft.getId(), draft.getCode(),
                "CATALOG", "ADMIN_WEB", "SUCCESS", "HIGH",
                beforeState, data, List.of("ports"), null, null, null, Map.of("version", draft.getVersion())
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

        Long expectedVersion = request.get("expectedVersion") != null
                ? ((Number) request.get("expectedVersion")).longValue()
                : (request.get("version") != null ? ((Number) request.get("version")).longValue() : null);

        if (expectedVersion != null) {
            validateIfMatch(draft.getVersion(), String.valueOf(expectedVersion));
        } else {
            validateIfMatch(draft.getVersion(), ifMatch);
        }

        Object rulesObj = request.get("rules");
        Map<String, Object> beforeState = draft.getData() != null ? new LinkedHashMap<>(draft.getData()) : Map.of();
        Map<String, Object> data = new LinkedHashMap<>(beforeState);
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
        if (version != null && !Objects.equals(draft.getVersion(), version)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "VERSION_CONFLICT: Version mismatch. Expected " + version + " but current is " + draft.getVersion());
        }

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        Map<String, Object> data = draft.getData();
        if (data == null || data.isEmpty()) {
            errors.add("Draft data is empty");
        }

        if ("MATERIAL".equalsIgnoreCase(entityType)) {
            if (data != null && data.get("formula") != null) {
                String formula = String.valueOf(data.get("formula")).trim();
                try {
                    formulaParser.parse(formula);
                } catch (Exception e) {
                    errors.add("Invalid chemical formula: " + formula);
                }
            }
        } else if ("REACTION".equalsIgnoreCase(entityType)) {
            Object reactants = data != null ? data.get("reactants") : null;
            Object products = data != null ? data.get("products") : null;
            if (reactants == null || !(reactants instanceof List) || ((List<?>) reactants).isEmpty()) {
                errors.add("Reaction must have at least one reactant");
            }
            if (products == null || !(products instanceof List) || ((List<?>) products).isEmpty()) {
                errors.add("Reaction must have at least one product");
            }
        } else if ("SCENARIO".equalsIgnoreCase(entityType)) {
            if (data != null) {
                Object initialSceneObj = data.get("initialScene");
                if (initialSceneObj instanceof Map<?, ?> initialScene) {
                    Object eqCode = initialScene.get("equipmentCode") != null ? initialScene.get("equipmentCode") : initialScene.get("alias");
                    if (eqCode != null) {
                        String eqCodeStr = String.valueOf(eqCode);
                        boolean eqExists = repository.findByEntityTypeAndCode("EQUIPMENT", eqCodeStr).isPresent()
                                || repository.findByEntityTypeAndId("EQUIPMENT", eqCodeStr).isPresent();
                        if (!eqExists) {
                            warnings.add("Referenced initialScene equipment not found in catalog: " + eqCodeStr);
                        }
                    }
                }
                Object stepsObj = data.get("steps");
                if (stepsObj instanceof List<?> stepsList) {
                    for (Object s : stepsList) {
                        if (s instanceof Map<?, ?> stepMap) {
                            Object tMat = stepMap.get("targetMaterial");
                            if (tMat != null) {
                                String matStr = String.valueOf(tMat);
                                boolean matExists = repository.findByEntityTypeAndCode("MATERIAL", matStr).isPresent()
                                        || repository.findByEntityTypeAndId("MATERIAL", matStr).isPresent();
                                if (!matExists) {
                                    warnings.add("Step references material not found in catalog: " + matStr);
                                }
                            }
                        }
                    }
                }
            }
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
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PUBLISH_VALIDATION_FAILED: Cannot publish invalid draft: " + validation.get("errors"));
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
        if ("MATERIAL".equalsIgnoreCase(entityType) || "SUBSTANCE".equalsIgnoreCase(entityType)) {
            Object formulaObj = body.get("formula");
            if (formulaObj != null) {
                String formula = String.valueOf(formulaObj).trim();
                if (!formula.isBlank()) {
                    try {
                        formulaParser.parse(formula);
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: Invalid chemical formula '" + formula + "': " + e.getMessage());
                    }
                }
            }
        } else if ("EQUIPMENT".equalsIgnoreCase(entityType)) {
            Object portsObj = body.get("ports");
            if (portsObj instanceof List<?> portsList) {
                for (Object p : portsList) {
                    if (p instanceof Map<?, ?> portMap) {
                        if (portMap.get("id") == null || (portMap.get("type") == null && portMap.get("kind") == null) || portMap.get("direction") == null) {
                            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PORT_SCHEMA_INVALID: Port requires id, direction, and type/kind");
                        }
                    }
                }
            }
        }
    }

    private AdminCatalogDraftEntity findDraft(String entityType, String identifier) {
        return repository.findByEntityTypeAndId(entityType.toUpperCase(), identifier)
                .or(() -> repository.findByEntityTypeAndCode(entityType.toUpperCase(), identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND: " + entityType + " draft not found: " + identifier));
    }

    private void validateIfMatch(Long currentVersion, String ifMatch) {
        if (ifMatch == null || ifMatch.isBlank() || "*".equals(ifMatch.trim())) return;
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
        try {
            if (sort.contains("%")) {
                sort = java.net.URLDecoder.decode(sort, java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {}
        String[] parts = sort.split("[,:]");
        String field = parts[0].trim();
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_QUERY: Invalid sort field: " + field);
        }
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
        m.put("createdAt", e.getCreatedAt() != null ? e.getCreatedAt() : Instant.now());
        m.put("updatedAt", e.getUpdatedAt() != null ? e.getUpdatedAt() : Instant.now());

        Map<String, Object> data = e.getData();
        if (data != null) {
            m.putAll(data);
            Object trObj = data.get("translations");
            if (trObj instanceof Map<?, ?> trMap) {
                Map<String, Object> normalizedTranslations = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : trMap.entrySet()) {
                    String locale = String.valueOf(entry.getKey());
                    Object val = entry.getValue();
                    if (val instanceof Map<?, ?>) {
                        normalizedTranslations.put(locale, val);
                    } else if (val != null) {
                        normalizedTranslations.put(locale, Map.of("name", String.valueOf(val)));
                    }
                }
                m.put("translations", normalizedTranslations);
            }
        }
        return m;
    }
}
