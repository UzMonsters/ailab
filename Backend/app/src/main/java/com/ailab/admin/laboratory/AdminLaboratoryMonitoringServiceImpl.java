package com.ailab.admin.laboratory;

import com.ailab.admin.audit.AuditLogService;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AdminLaboratoryMonitoringServiceImpl implements AdminLaboratoryMonitoringService {

    private final WorkspaceRepository workspaceRepository;
    private final AuditLogService auditLogService;
    private final com.ailab.workspace.repository.WorkspaceStateRepository stateRepository;
    private final com.ailab.workspace.repository.WorkspaceEventRepository eventRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final Map<String, String> sessionStatusMap = new ConcurrentHashMap<>();

    public AdminLaboratoryMonitoringServiceImpl(WorkspaceRepository workspaceRepository, AuditLogService auditLogService) {
        this(workspaceRepository, auditLogService, null, null, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AdminLaboratoryMonitoringServiceImpl(
            WorkspaceRepository workspaceRepository,
            AuditLogService auditLogService,
            @org.springframework.beans.factory.annotation.Autowired(required = false) com.ailab.workspace.repository.WorkspaceStateRepository stateRepository,
            @org.springframework.beans.factory.annotation.Autowired(required = false) com.ailab.workspace.repository.WorkspaceEventRepository eventRepository,
            @org.springframework.beans.factory.annotation.Autowired(required = false) com.fasterxml.jackson.databind.ObjectMapper objectMapper
    ) {
        this.workspaceRepository = workspaceRepository;
        this.auditLogService = auditLogService;
        this.stateRepository = stateRepository;
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper != null ? objectMapper : new com.fasterxml.jackson.databind.ObjectMapper();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSessions(int page, int size, String q, String science, String status, String ownerId, Instant startedFrom) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));

        boolean hasSearch = q != null && !q.isBlank();
        String search = hasSearch ? q.trim() : "";
        boolean hasScience = science != null && !science.isBlank();
        String sci = hasScience ? science.trim() : "";
        boolean hasOwner = ownerId != null && !ownerId.isBlank();
        String owner = hasOwner ? ownerId.trim() : "";

        Page<WorkspaceEntity> workspacePage = workspaceRepository.findLaboratories(
                search, hasSearch, sci, hasScience, owner, hasOwner,
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"))
        );
        if (workspacePage == null) {
            workspacePage = workspaceRepository.findAll(PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "updatedAt")));
        }
        if (workspacePage == null) {
            workspacePage = new PageImpl<>(List.of());
        }

        List<Map<String, Object>> items = workspacePage.getContent().stream()
                .map(w -> {
                    String currentStatus = sessionStatusMap.getOrDefault(w.getId(), "ACTIVE");
                    int objectCount = countSceneObjects(w.getId());
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("sessionId", "sess_" + w.getId().substring(Math.max(0, w.getId().length() - 8)));
                    m.put("workspaceId", w.getId());
                    m.put("name", w.getName());
                    m.put("science", w.getScience() != null ? w.getScience() : "Chemistry");
                    m.put("owner", Map.of("id", w.getOwnerId(), "displayName", "User " + w.getOwnerId().substring(0, Math.min(6, w.getOwnerId().length()))));
                    m.put("objectCount", objectCount);
                    m.put("runtimeSeconds", ChronoUnit.SECONDS.between(w.getCreatedAt(), Instant.now()));
                    m.put("status", currentStatus);
                    m.put("lastEventAt", w.getUpdatedAt());
                    return m;
                })
                .filter(m -> status == null || String.valueOf(m.get("status")).equalsIgnoreCase(status))
                .toList();

        Map<String, Object> pageMeta = Map.of(
                "number", workspacePage.getNumber(),
                "size", workspacePage.getSize(),
                "totalElements", workspacePage.getTotalElements(),
                "totalPages", workspacePage.getTotalPages()
        );

        return Map.of("items", items, "page", pageMeta);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSessionDetails(String id) {
        WorkspaceEntity workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Laboratory session not found: " + id));

        String currentStatus = sessionStatusMap.getOrDefault(workspace.getId(), "ACTIVE");

        Map<String, Object> session = Map.of(
                "id", "sess_" + workspace.getId().substring(Math.max(0, workspace.getId().length() - 8)),
                "workspaceId", workspace.getId(),
                "name", workspace.getName(),
                "science", workspace.getScience() != null ? workspace.getScience() : "Chemistry",
                "status", currentStatus,
                "startedAt", workspace.getCreatedAt(),
                "lastHeartbeatAt", workspace.getUpdatedAt()
        );

        Map<String, Integer> counts = calculateWorkspaceItemCounts(workspace.getId());
        int apparatusCount = counts.getOrDefault("apparatus", 0);
        int substancesCount = counts.getOrDefault("substances", 0);

        Map<String, Object> workspaceSummary = Map.of(
                "apparatusCount", apparatusCount,
                "substancesCount", substancesCount,
                "activeReactions", apparatusCount > 0 && substancesCount > 0 ? 1 : 0,
                "maxTemperatureK", 298.15,
                "pressureKpa", 101.325
        );

        Map<String, Object> safetyState = Map.of(
                "level", "NOMINAL",
                "incidentCount", 0,
                "hazardWarnings", List.of()
        );

        List<Map<String, Object>> participants = List.of(
                Map.of("userId", workspace.getOwnerId(), "role", "OWNER", "online", true)
        );

        List<Map<String, Object>> latestEvents = getLatestEventsForWorkspace(workspace.getId(), 10);

        return Map.of(
                "session", session,
                "workspaceSummary", workspaceSummary,
                "safetyState", safetyState,
                "participants", participants,
                "latestEvents", latestEvents
        );
    }

    @Override
    public Map<String, Object> pauseSession(String id, String reason, String actorId, String actorName) {
        if (reason == null || reason.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Reason is required to pause a simulation");
        }
        workspaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Laboratory session not found: " + id));

        sessionStatusMap.put(id, "PAUSED");

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                "laboratory.paused", "LABORATORY_SESSION", id, "Lab Session " + id,
                "LABORATORY", "ADMIN_WEB", "SUCCESS", "HIGH",
                Map.of("status", "ACTIVE"), Map.of("status", "PAUSED", "reason", reason),
                List.of("status"), null, null, null, Map.of("reason", reason)
        );

        return Map.of(
                "status", "PAUSED",
                "pausedAt", Instant.now()
        );
    }

    @Override
    public Map<String, Object> terminateSession(String id, String reason, boolean notifyOwner, String actorId, String actorName) {
        if (reason == null || reason.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Reason is required to terminate a simulation");
        }
        workspaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Laboratory session not found: " + id));

        String currentStatus = sessionStatusMap.getOrDefault(id, "ACTIVE");
        if ("TERMINATING".equalsIgnoreCase(currentStatus) || "TERMINATED".equalsIgnoreCase(currentStatus)) {
            return Map.of("status", "TERMINATING", "idempotent", true);
        }

        sessionStatusMap.put(id, "TERMINATING");

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                "laboratory.terminated", "LABORATORY_SESSION", id, "Lab Session " + id,
                "LABORATORY", "ADMIN_WEB", "SUCCESS", "HIGH",
                Map.of("status", currentStatus), Map.of("status", "TERMINATING", "reason", reason, "notifyOwner", notifyOwner),
                List.of("status"), null, null, null, Map.of("reason", reason, "notifyOwner", notifyOwner)
        );

        return Map.of("status", "TERMINATING");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLaboratoryEvents(String id, Long afterVersion, int limit) {
        WorkspaceEntity workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Laboratory session not found: " + id));

        int safeLimit = Math.max(1, Math.min(limit > 0 ? limit : 50, 100));
        List<Map<String, Object>> events = getLatestEventsForWorkspace(workspace.getId(), safeLimit);

        return Map.of(
                "sessionId", "sess_" + workspace.getId().substring(Math.max(0, workspace.getId().length() - 8)),
                "workspaceId", workspace.getId(),
                "events", events,
                "count", events.size()
        );
    }

    private int countSceneObjects(String workspaceId) {
        if (stateRepository == null) return 0;
        return stateRepository.findById(workspaceId)
                .map(s -> {
                    try {
                        if (s.getItemsJson() == null || s.getItemsJson().isBlank()) return 0;
                        List<?> list = objectMapper.readValue(s.getItemsJson(), List.class);
                        return list.size();
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .orElse(0);
    }

    private Map<String, Integer> calculateWorkspaceItemCounts(String workspaceId) {
        if (stateRepository == null) return Map.of("apparatus", 0, "substances", 0);
        return stateRepository.findById(workspaceId)
                .map(s -> {
                    try {
                        if (s.getItemsJson() == null || s.getItemsJson().isBlank()) return Map.of("apparatus", 0, "substances", 0);
                        List<?> list = objectMapper.readValue(s.getItemsJson(), List.class);
                        int apparatus = 0;
                        int substances = 0;
                        for (Object item : list) {
                            if (item instanceof Map<?, ?> m) {
                                Object typeVal = m.get("type");
                                if (typeVal == null) typeVal = m.get("kind");
                                String type = typeVal != null ? String.valueOf(typeVal).toLowerCase() : "";
                                if (type.contains("substance") || type.contains("chemical") || type.contains("material") || type.contains("liquid")) {
                                    substances++;
                                } else {
                                    apparatus++;
                                }
                            } else {
                                apparatus++;
                            }
                        }
                        return Map.of("apparatus", apparatus, "substances", substances);
                    } catch (Exception e) {
                        return Map.of("apparatus", 0, "substances", 0);
                    }
                })
                .orElse(Map.of("apparatus", 0, "substances", 0));
    }

    private List<Map<String, Object>> getLatestEventsForWorkspace(String workspaceId, int limit) {
        if (eventRepository == null) return List.of();
        try {
            var eventList = eventRepository.findByWorkspaceIdOrderByVersionAsc(workspaceId);
            if (eventList == null || eventList.isEmpty()) return List.of();
            int start = Math.max(0, eventList.size() - limit);
            return eventList.subList(start, eventList.size()).stream()
                    .map(e -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", e.getId());
                        m.put("type", e.getEventType());
                        m.put("version", e.getVersion());
                        m.put("at", e.getCreatedAt());
                        m.put("userId", e.getUserId());
                        return m;
                    })
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
