package com.ailab.admin.workspace;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/workspaces")
@Tag(name = "Admin Workspaces", description = "Global workspace administration, sharing overview, and purge operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminWorkspaceController {

    private final AdminWorkspaceService service;

    public AdminWorkspaceController(AdminWorkspaceService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all workspaces for admins", description = "Return a global admin workspace list with owner and sharing aggregate counts.")
    public AdminWorkspaceDtos.PageDto list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String science,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ownerId,
            @RequestParam(required = false) Boolean hasActiveLinks,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String sort) {
        return service.list(q, science, status, ownerId, hasActiveLinks, page, size, sort);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get admin workspace detail", description = "Return members, invitations, share links, and state metadata for a workspace.")
    public AdminWorkspaceDtos.DetailDto detail(@PathVariable String id) {
        return service.detail(id);
    }

    @DeleteMapping("/{id}/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Purge workspace", description = "Irreversibly delete a workspace after admin confirmation.")
    public void purge(@PathVariable String id, @RequestParam String confirmation) {
        if (!"PURGE".equals(confirmation)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "CONFIRMATION_REQUIRED: confirmation=PURGE is required");
        }
        service.purge(id);
    }
}
