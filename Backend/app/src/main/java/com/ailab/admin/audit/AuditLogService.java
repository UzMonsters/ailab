package com.ailab.admin.audit;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface AuditLogService {

    AdminAuditEventEntity logEvent(String actorId, String actorName, String actorRole,
                                   String action, String entityType, String entityId, String entityLabel,
                                   String subject, String source, String result, String severity,
                                   Object beforeState, Object afterState, List<String> changedKeys,
                                   String requestId, String ipAddress, String userAgent, Map<String, Object> metadata);

    Map<String, Object> getAuditEvents(int page, int size, String q, String actorId, String action,
                                       String entityType, String entityId, String subject, String source,
                                       String result, String severity, Instant from, Instant to, String sort);

    Map<String, Object> getAuditEventById(String eventId);

    Map<String, Object> createExportJob(String format, Map<String, Object> filters);

    Map<String, Object> getExportJob(String jobId);

    Map<String, Object> getRetentionPolicy();
}
