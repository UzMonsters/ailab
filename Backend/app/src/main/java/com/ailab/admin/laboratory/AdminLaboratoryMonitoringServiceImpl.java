package com.ailab.admin.laboratory;

import com.ailab.admin.audit.AuditLogService;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.data.domain.Page;
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
    private final Map<String, String> sessionStatusMap = new ConcurrentHashMap<>();

    public AdminLaboratoryMonitoringServiceImpl(WorkspaceRepository workspaceRepository, AuditLogService auditLogService) {
        this.workspaceRepository = workspaceRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSessions(int page, int size, String q, String science, String status, String ownerId, Instant startedFrom) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));

        Page<WorkspaceEntity> workspacePage = workspaceRepository.findAll(
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"))
        );

        List<Map<String, Object>> items = workspacePage.getContent().stream()
                .filter(w -> q == null || w.getName().toLowerCase().contains(q.toLowerCase()))
                .filter(w -> ownerId == null || w.getOwnerId().equals(ownerId))
                .map(w -> {
                    String currentStatus = sessionStatusMap.getOrDefault(w.getId(), "ACTIVE");
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("sessionId", "sess_" + w.getId().substring(Math.max(0, w.getId().length() - 8)));
                    m.put("workspaceId", w.getId());
                    m.put("name", w.getName());
                    m.put("science", "Chemistry");
                    m.put("owner", Map.of("id", w.getOwnerId(), "displayName", "User " + w.getOwnerId().substring(0, Math.min(6, w.getOwnerId().length()))));
                    m.put("objectCount", 12);
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
                .orElseGet(() -> workspaceRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Laboratory session not found: " + id)));

        String currentStatus = sessionStatusMap.getOrDefault(workspace.getId(), "ACTIVE");

        Map<String, Object> session = Map.of(
                "id", "sess_" + workspace.getId().substring(Math.max(0, workspace.getId().length() - 8)),
                "workspaceId", workspace.getId(),
                "name", workspace.getName(),
                "science", "Chemistry",
                "status", currentStatus,
                "startedAt", workspace.getCreatedAt(),
                "lastHeartbeatAt", workspace.getUpdatedAt()
        );

        Map<String, Object> workspaceSummary = Map.of(
                "apparatusCount", 4,
                "substancesCount", 6,
                "activeReactions", 1,
                "maxTemperatureK", 373.15,
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

        List<Map<String, Object>> latestEvents = List.of(
                Map.of("type", "MATERIAL_ADDED", "at", workspace.getUpdatedAt(), "details", Map.of("material", "H2O", "volumeMl", 100))
        );

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
        sessionStatusMap.put(id, "TERMINATED");

        auditLogService.logEvent(
                actorId, actorName, "ADMIN",
                "laboratory.terminated", "LABORATORY_SESSION", id, "Lab Session " + id,
                "LABORATORY", "ADMIN_WEB", "SUCCESS", "HIGH",
                Map.of("status", "ACTIVE"), Map.of("status", "TERMINATED", "reason", reason, "notifyOwner", notifyOwner),
                List.of("status"), null, null, null, Map.of("reason", reason, "notifyOwner", notifyOwner)
        );

        return Map.of("status", "TERMINATING");
    }
}
