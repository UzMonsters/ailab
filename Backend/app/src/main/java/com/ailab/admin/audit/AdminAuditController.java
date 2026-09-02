package com.ailab.admin.audit;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminAuditController {

    private final AuditLogService auditLogService;

    public AdminAuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/audit-events")
    public Map<String, Object> listAuditEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String sort) {
        return auditLogService.getAuditEvents(page, size, q, actorId, action, entityType, entityId, subject, source, result, severity, from, to, sort);
    }

    @GetMapping("/audit-events/{eventId}")
    public Map<String, Object> getAuditEventDetail(@PathVariable String eventId) {
        return auditLogService.getAuditEventById(eventId);
    }

    @PostMapping("/audit-exports")
    public ResponseEntity<Map<String, Object>> createAuditExport(@RequestBody(required = false) Map<String, Object> request) {
        String format = request != null && request.get("format") != null ? String.valueOf(request.get("format")) : "CSV";
        @SuppressWarnings("unchecked")
        Map<String, Object> filters = request != null && request.get("filters") instanceof Map
                ? (Map<String, Object>) request.get("filters") : Map.of();

        Map<String, Object> job = auditLogService.createExportJob(format, filters);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }

    @GetMapping("/audit-exports/{jobId}")
    public Map<String, Object> getAuditExportStatus(@PathVariable String jobId) {
        return auditLogService.getExportJob(jobId);
    }

    @GetMapping("/audit-retention")
    public Map<String, Object> getAuditRetention() {
        return auditLogService.getRetentionPolicy();
    }
}
