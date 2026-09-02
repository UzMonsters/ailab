package com.ailab.admin.catalog;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/v1/admin/materials", "/api/v1/admin/catalog/materials"})
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminMaterialsCatalogController {

    private final AdminCatalogService catalogService;

    public AdminMaterialsCatalogController(AdminCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public Map<String, Object> listMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {
        return catalogService.listDrafts("MATERIAL", page, size, q, status, sort);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createMaterial(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> res = catalogService.createDraft("MATERIAL", body, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getMaterial(@PathVariable String id) {
        return catalogService.getDraft("MATERIAL", id);
    }

    @PatchMapping("/{id}")
    public Map<String, Object> patchMaterial(
            @PathVariable String id,
            @RequestBody Map<String, Object> patch,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            Authentication authentication) {
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        return catalogService.patchDraft("MATERIAL", id, patch, ifMatch, actorId, "Admin User");
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishMaterial(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> request,
            Authentication authentication) {
        Long version = request != null && request.get("version") != null ? ((Number) request.get("version")).longValue() : null;
        String idemp = request != null && request.get("idempotencyKey") != null ? String.valueOf(request.get("idempotencyKey")) : null;
        String actorId = authentication != null ? authentication.getName() : "usr_admin";
        Map<String, Object> res = catalogService.publishDraft("MATERIAL", id, version, idemp, actorId, "Admin User");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
