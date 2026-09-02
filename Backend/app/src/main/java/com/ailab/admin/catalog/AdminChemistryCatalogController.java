package com.ailab.admin.catalog;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/chemistry")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminChemistryCatalogController {

    private final AdminCatalogService catalogService;

    public AdminChemistryCatalogController(AdminCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/elements")
    public Map<String, Object> listElements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {
        return catalogService.listDrafts("ELEMENT", page, size, q, status, sort);
    }

    @PostMapping("/elements")
    public ResponseEntity<Map<String, Object>> createElement(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> res = catalogService.createDraft("ELEMENT", body, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/elements/{id}")
    public Map<String, Object> getElement(@PathVariable String id) {
        return catalogService.getDraft("ELEMENT", id);
    }

    @PatchMapping("/elements/{id}")
    public Map<String, Object> patchElement(
            @PathVariable String id,
            @RequestBody Map<String, Object> patch,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        return catalogService.patchDraft("ELEMENT", id, patch, ifMatch, actorId, "Admin User");
    }

    @PostMapping("/elements/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishElement(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> request,
            Authentication authentication) {
        Long version = request != null && request.get("version") != null ? ((Number) request.get("version")).longValue() : null;
        String idemp = request != null && request.get("idempotencyKey") != null ? String.valueOf(request.get("idempotencyKey")) : null;
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> res = catalogService.publishDraft("ELEMENT", id, version, idemp, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/substances")
    public Map<String, Object> listSubstances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {
        return catalogService.listDrafts("SUBSTANCE", page, size, q, status, sort);
    }

    @PostMapping("/substances")
    public ResponseEntity<Map<String, Object>> createSubstance(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> res = catalogService.createDraft("SUBSTANCE", body, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/substances/{id}")
    public Map<String, Object> getSubstance(@PathVariable String id) {
        return catalogService.getDraft("SUBSTANCE", id);
    }

    @PatchMapping("/substances/{id}")
    public Map<String, Object> patchSubstance(
            @PathVariable String id,
            @RequestBody Map<String, Object> patch,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        return catalogService.patchDraft("SUBSTANCE", id, patch, ifMatch, actorId, "Admin User");
    }

    @PostMapping("/substances/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishSubstance(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> request,
            Authentication authentication) {
        Long version = request != null && request.get("version") != null ? ((Number) request.get("version")).longValue() : null;
        String idemp = request != null && request.get("idempotencyKey") != null ? String.valueOf(request.get("idempotencyKey")) : null;
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> res = catalogService.publishDraft("SUBSTANCE", id, version, idemp, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/reactions")
    public Map<String, Object> listReactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {
        return catalogService.listDrafts("REACTION", page, size, q, status, sort);
    }

    @PostMapping("/reactions")
    public ResponseEntity<Map<String, Object>> createReaction(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> res = catalogService.createDraft("REACTION", body, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/reactions/{id}")
    public Map<String, Object> getReaction(@PathVariable String id) {
        return catalogService.getDraft("REACTION", id);
    }

    @PatchMapping("/reactions/{id}")
    public Map<String, Object> patchReaction(
            @PathVariable String id,
            @RequestBody Map<String, Object> patch,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        return catalogService.patchDraft("REACTION", id, patch, ifMatch, actorId, "Admin User");
    }

    @PostMapping("/reactions/{id}/validate")
    public Map<String, Object> validateReaction(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> request) {
        Long version = request != null && request.get("version") != null ? ((Number) request.get("version")).longValue() : null;
        return catalogService.validateDraft("REACTION", id, version);
    }

    @PostMapping("/reactions/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishReaction(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> request,
            Authentication authentication) {
        Long version = request != null && request.get("version") != null ? ((Number) request.get("version")).longValue() : null;
        String idemp = request != null && request.get("idempotencyKey") != null ? String.valueOf(request.get("idempotencyKey")) : null;
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> res = catalogService.publishDraft("REACTION", id, version, idemp, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
