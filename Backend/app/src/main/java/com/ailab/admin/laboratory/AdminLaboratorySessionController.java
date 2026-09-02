package com.ailab.admin.laboratory;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminLaboratorySessionController {

    private final AdminLaboratoryMonitoringService service;

    public AdminLaboratorySessionController(AdminLaboratoryMonitoringService service) {
        this.service = service;
    }

    @GetMapping("/laboratory-sessions")
    public Map<String, Object> listSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String science,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ownerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startedFrom) {
        return service.getSessions(page, size, q, science, status, ownerId, startedFrom);
    }

    @GetMapping("/laboratory-sessions/{id}")
    public Map<String, Object> getSessionDetail(@PathVariable String id) {
        return service.getSessionDetails(id);
    }

    @PostMapping("/laboratory-sessions/{id}/pause")
    public Map<String, Object> pauseSession(
            @PathVariable String id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String reason = request != null ? request.get("reason") : null;
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        return service.pauseSession(id, reason, actorId, "Admin User");
    }

    @PostMapping("/laboratory-sessions/{id}/terminate")
    public ResponseEntity<Map<String, Object>> terminateSession(
            @PathVariable String id,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        String reason = request != null && request.get("reason") != null ? String.valueOf(request.get("reason")) : null;
        boolean notifyOwner = request == null || request.get("notifyOwner") == null || Boolean.parseBoolean(String.valueOf(request.get("notifyOwner")));
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> result = service.terminateSession(id, reason, notifyOwner, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }
}
