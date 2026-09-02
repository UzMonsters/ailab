package com.ailab.admin.settings;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminSettingsController {

    private final AdminSettingsService service;

    public AdminSettingsController(AdminSettingsService service) {
        this.service = service;
    }

    @GetMapping("/settings")
    public Map<String, Object> getSettings() {
        return service.getSettings();
    }

    @PatchMapping("/settings")
    public Map<String, Object> patchSettings(
            @RequestBody Map<String, Object> patch,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        return service.patchSettings(patch, ifMatch, actorId, "Admin User");
    }

    @GetMapping("/settings/schema")
    public Map<String, Object> getSchema(@RequestParam(required = false) String locale) {
        return service.getSchema(locale);
    }

    @GetMapping("/settings/history")
    public Map<String, Object> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String actorId) {
        return service.getHistory(page, size, from, to, actorId);
    }

    @PostMapping("/settings/{version}/restore")
    public ResponseEntity<Map<String, Object>> restoreVersion(
            @PathVariable Long version,
            @RequestBody(required = false) Map<String, String> request,
            Authentication authentication) {
        String reason = request != null ? request.get("reason") : "Settings rollback";
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> result = service.restoreVersion(version, reason, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/subjects")
    public Map<String, Object> getSubjects() {
        List<Map<String, Object>> items = service.getSubjects();
        return Map.of("items", items);
    }

    @PatchMapping("/subjects/{id}")
    public Map<String, Object> patchSubject(
            @PathVariable String id,
            @RequestBody Map<String, Object> patch,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        return service.patchSubject(id, patch, actorId, "Admin User");
    }
}
