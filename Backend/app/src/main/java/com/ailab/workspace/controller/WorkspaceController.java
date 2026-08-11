package com.ailab.workspace.controller;

import com.ailab.workspace.dto.*;
import com.ailab.workspace.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workspaces")
@Tag(name = "Laboratory Workspaces", description = "Workspace lifecycle, infinite canvas state, persistent events, autosave, thumbnail, duplicate, undo/redo")
@SecurityRequirement(name = "bearerAuth")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            throw new org.springframework.security.authentication.InsufficientAuthenticationException("User must be authenticated");
        }
        return auth.getName();
    }

    @GetMapping
    @Operation(summary = "List workspaces", description = "Retrieve list of workspaces owned by authenticated user with filtering, search, sorting, and pagination.")
    public WorkspacePageResponse<WorkspaceDetails> listWorkspaces(
            @RequestParam(required = false) String science,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean includeDeleted) {
        return workspaceService.listWorkspaces(getCurrentUserId(), science, search, sort, page, size, includeDeleted);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workspace details", description = "Retrieve metadata and state reference of a workspace owned by authenticated user.")
    public WorkspaceDetails getWorkspace(@PathVariable String id) {
        return workspaceService.getWorkspace(id, getCurrentUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create workspace", description = "Create a new owned laboratory workspace with linked experiment session.")
    public WorkspaceDetails createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        return workspaceService.createWorkspace(getCurrentUserId(), request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update workspace", description = "Update workspace name, favorite status, trash state, or thumbnail with optimistic version check.")
    public WorkspaceDetails updateWorkspace(
            @PathVariable String id,
            @RequestBody UpdateWorkspaceRequest request) {
        return workspaceService.updateWorkspace(id, getCurrentUserId(), request);
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate workspace", description = "Deep copy workspace metadata and canvas state.")
    public WorkspaceDetails duplicateWorkspace(
            @PathVariable String id,
            @RequestBody(required = false) DuplicateWorkspaceRequest request) {
        return workspaceService.duplicateWorkspace(id, getCurrentUserId(), request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Permanently delete workspace", description = "Permanently delete workspace and its associated state.")
    public Map<String, String> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspacePermanently(id, getCurrentUserId());
        return Map.of("message", "Workspace permanently deleted");
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore workspace from trash", description = "Restore soft-deleted workspace.")
    public WorkspaceDetails restoreWorkspace(@PathVariable String id) {
        return workspaceService.restoreWorkspace(id, getCurrentUserId());
    }

    @PostMapping("/{id}/thumbnail")
    @Operation(summary = "Update workspace thumbnail", description = "Upload or set preview thumbnail for workspace dashboard card.")
    public Map<String, Object> updateThumbnail(
            @PathVariable String id,
            @RequestBody ThumbnailRequest request) {
        return workspaceService.updateThumbnail(id, getCurrentUserId(), request);
    }

    // === SANDBOX / CANVAS STATE & EVENTS REST APIs ===

    @GetMapping("/{id}/state")
    @Operation(summary = "Get canonical workspace state", description = "Load full canonical Sandbox state for canvas rendering.")
    public WorkspaceStateDto getWorkspaceState(@PathVariable String id) {
        return workspaceService.getWorkspaceState(id, getCurrentUserId());
    }

    @PutMapping("/{id}/state")
    @Operation(summary = "Save workspace canvas snapshot", description = "Snapshot save workspace canvas state with optimistic version locking.")
    public WorkspaceStateDto saveWorkspaceState(
            @PathVariable String id,
            @RequestParam(required = false) Long expectedVersion,
            @RequestBody WorkspaceStateDto stateDto) {
        return workspaceService.saveWorkspaceState(id, getCurrentUserId(), expectedVersion, stateDto);
    }

    @PostMapping("/{id}/events")
    @Operation(summary = "Append persistent workspace event", description = "Atomically apply and persist a discrete canvas event with idempotency and optimistic versioning.")
    public WorkspaceEventAck appendEvent(
            @PathVariable String id,
            @Valid @RequestBody SandboxEventCommand cmd) {
        return workspaceService.appendEvent(id, getCurrentUserId(), cmd);
    }

    @GetMapping("/{id}/events")
    @Operation(summary = "List persistent workspace events", description = "Fetch event history for replay or missed message recovery.")
    public List<Map<String, Object>> getEvents(
            @PathVariable String id,
            @RequestParam(required = false) Long afterVersion,
            @RequestParam(required = false) Integer limit) {
        return workspaceService.getEvents(id, getCurrentUserId(), afterVersion, limit);
    }

    @PostMapping("/{id}/undo")
    @Operation(summary = "Undo last canvas operation", description = "Revert last canvas event.")
    public WorkspaceStateDto undo(
            @PathVariable String id,
            @RequestParam(required = false) Long expectedVersion) {
        return workspaceService.undo(id, getCurrentUserId(), expectedVersion);
    }

    @PostMapping("/{id}/redo")
    @Operation(summary = "Redo canvas operation", description = "Redo previously reverted event.")
    public WorkspaceStateDto redo(
            @PathVariable String id,
            @RequestParam(required = false) Long expectedVersion) {
        return workspaceService.redo(id, getCurrentUserId(), expectedVersion);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish workspace", description = "Publish workspace experiment for public viewing or link sharing.")
    public Map<String, Object> publishWorkspace(
            @PathVariable String id,
            @RequestBody(required = false) PublishWorkspaceRequest request) {
        return workspaceService.publishWorkspace(id, getCurrentUserId(), request);
    }

    @PostMapping("/{id}/autosave")
    @Operation(summary = "Autosave workspace state", description = "Fast autosave endpoint triggered before tab close or navigation.")
    public Map<String, Object> autosave(
            @PathVariable String id,
            @RequestBody AutosaveRequest request) {
        return workspaceService.autosave(id, getCurrentUserId(), request);
    }
}
