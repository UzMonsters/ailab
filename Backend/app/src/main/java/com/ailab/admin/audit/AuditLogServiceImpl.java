package com.ailab.admin.audit;

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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AdminAuditRepository repository;
    private final Map<String, Map<String, Object>> exportJobs = new ConcurrentHashMap<>();

    public AuditLogServiceImpl(AdminAuditRepository repository) {
        this.repository = repository;
    }

    @Override
    public AdminAuditEventEntity logEvent(String actorId, String actorName, String actorRole,
                                          String action, String entityType, String entityId, String entityLabel,
                                          String subject, String source, String result, String severity,
                                          Object beforeState, Object afterState, List<String> changedKeys,
                                          String requestId, String ipAddress, String userAgent, Map<String, Object> metadata) {
        AdminAuditEventEntity entity = new AdminAuditEventEntity(
                actorId, actorName, actorRole, action, entityType, entityId, entityLabel,
                subject, source, result, severity, beforeState, afterState, changedKeys,
                requestId, ipAddress, userAgent, metadata
        );
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAuditEvents(int page, int size, String q, String actorId, String action,
                                              String entityType, String entityId, String subject, String source,
                                              String result, String severity, Instant from, Instant to, String sort) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));

        Sort sorting = parseSort(sort);
        Pageable pageable = PageRequest.of(safePage, safeSize, sorting);

        Specification<AdminAuditEventEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                String pattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("action")), pattern),
                        cb.like(cb.lower(root.get("entityType")), pattern),
                        cb.like(cb.lower(root.get("entityId")), pattern),
                        cb.like(cb.lower(root.get("actorName")), pattern)
                ));
            }
            if (actorId != null && !actorId.isBlank()) {
                predicates.add(cb.equal(root.get("actorId"), actorId));
            }
            if (action != null && !action.isBlank()) {
                predicates.add(cb.equal(root.get("action"), action));
            }
            if (entityType != null && !entityType.isBlank()) {
                predicates.add(cb.equal(root.get("entityType"), entityType));
            }
            if (entityId != null && !entityId.isBlank()) {
                predicates.add(cb.equal(root.get("entityId"), entityId));
            }
            if (subject != null && !subject.isBlank()) {
                predicates.add(cb.equal(root.get("subject"), subject));
            }
            if (source != null && !source.isBlank()) {
                predicates.add(cb.equal(root.get("source"), source));
            }
            if (result != null && !result.isBlank()) {
                predicates.add(cb.equal(root.get("result"), result));
            }
            if (severity != null && !severity.isBlank()) {
                predicates.add(cb.equal(root.get("severity"), severity));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("occurredAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("occurredAt"), to));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<AdminAuditEventEntity> eventPage = repository.findAll(spec, pageable);
        List<Map<String, Object>> items = eventPage.getContent().stream()
                .map(this::toSummaryMap)
                .toList();

        Map<String, Object> pageMeta = Map.of(
                "number", eventPage.getNumber(),
                "size", eventPage.getSize(),
                "totalElements", eventPage.getTotalElements(),
                "totalPages", eventPage.getTotalPages()
        );

        Map<String, Object> facets = Map.of(
                "actions", repository.findDistinctActions(),
                "severities", repository.findDistinctSeverities(),
                "sources", repository.findDistinctSources()
        );

        return Map.of(
                "items", items,
                "page", pageMeta,
                "facets", facets
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAuditEventById(String eventId) {
        AdminAuditEventEntity e = repository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Audit event not found: " + eventId));
        return toDetailMap(e);
    }

    @Override
    public Map<String, Object> createExportJob(String format, Map<String, Object> filters) {
        String jobId = "job_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
        String downloadUrl = "/api/v1/admin/audit-exports/" + jobId + "/download";

        Map<String, Object> job = new LinkedHashMap<>();
        job.put("jobId", jobId);
        job.put("status", "READY");
        job.put("format", format != null ? format.toUpperCase() : "CSV");
        job.put("downloadUrl", downloadUrl);
        job.put("expiresAt", expiresAt);
        job.put("createdAt", Instant.now());

        exportJobs.put(jobId, job);

        return Map.of(
                "jobId", jobId,
                "status", "QUEUED"
        );
    }

    @Override
    public Map<String, Object> getExportJob(String jobId) {
        Map<String, Object> job = exportJobs.get(jobId);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Export job not found: " + jobId);
        }
        return job;
    }

    @Override
    public Map<String, Object> getRetentionPolicy() {
        return Map.of(
                "retentionDays", 365,
                "immutable", true,
                "archiveEnabled", true
        );
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "occurredAt");
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private Map<String, Object> toSummaryMap(AdminAuditEventEntity e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId());
        m.put("occurredAt", e.getOccurredAt());
        m.put("actor", Map.of(
                "id", e.getActorId(),
                "displayName", e.getActorName(),
                "role", e.getActorRole()
        ));
        m.put("action", e.getAction());
        m.put("entity", Map.of(
                "type", e.getEntityType(),
                "id", e.getEntityId(),
                "label", e.getEntityLabel() != null ? e.getEntityLabel() : e.getEntityId()
        ));
        m.put("source", e.getSource());
        m.put("result", e.getResult());
        m.put("severity", e.getSeverity());
        m.put("changedKeys", e.getChangedKeys() != null ? e.getChangedKeys() : List.of());
        m.put("requestId", e.getRequestId());
        return m;
    }

    private Map<String, Object> toDetailMap(AdminAuditEventEntity e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId());
        m.put("occurredAt", e.getOccurredAt());
        m.put("actor", Map.of(
                "id", e.getActorId(),
                "displayName", e.getActorName(),
                "role", e.getActorRole()
        ));
        m.put("action", e.getAction());
        m.put("entity", Map.of(
                "type", e.getEntityType(),
                "id", e.getEntityId(),
                "label", e.getEntityLabel() != null ? e.getEntityLabel() : e.getEntityId()
        ));
        m.put("source", e.getSource());
        m.put("result", e.getResult());
        m.put("severity", e.getSeverity());
        m.put("before", e.getBeforeState());
        m.put("after", e.getAfterState());
        m.put("changedKeys", e.getChangedKeys());
        m.put("requestId", e.getRequestId());
        m.put("ip", e.getIpAddress());
        m.put("userAgent", e.getUserAgent());
        m.put("metadata", e.getMetadata() != null ? e.getMetadata() : Map.of());
        return m;
    }
}
