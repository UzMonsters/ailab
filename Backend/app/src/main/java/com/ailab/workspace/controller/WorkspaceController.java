package com.ailab.workspace.controller;

import com.ailab.workspace.dto.*;
import com.ailab.workspace.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workspaces")
@Tag(name = "Laboratory Workspaces", description = "Workspace lifecycle, infinite canvas state, persistent events, sharing, chat, comments, previews, and measurements")
@SecurityRequirement(name = "bearerAuth")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMemberService memberService;
    private final WorkspaceShareService shareService;
    private final WorkspacePreviewService previewService;
    private final WorkspaceChatService chatService;
    private final WorkspaceCommentService commentService;
    private final MeasurementService measurementService;

    public WorkspaceController(
            WorkspaceService workspaceService,
            WorkspaceMemberService memberService,
            WorkspaceShareService shareService,
            WorkspacePreviewService previewService,
            WorkspaceChatService chatService,
            WorkspaceCommentService commentService,
            MeasurementService measurementService
    ) {
        this.workspaceService = workspaceService;
        this.memberService = memberService;
        this.shareService = shareService;
        this.previewService = previewService;
        this.chatService = chatService;
        this.commentService = commentService;
        this.measurementService = measurementService;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            throw new org.springframework.security.authentication.InsufficientAuthenticationException("User must be authenticated");
        }
        return auth.getName();
    }

    @GetMapping
    @Operation(summary = "List workspaces", description = "Retrieve list of workspaces accessible to the user.")
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
    @Operation(summary = "Get workspace details", description = "Retrieve metadata, permissions, and preview reference of a workspace.")
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
    @Operation(summary = "Update workspace", description = "Update workspace name, favorite status, trash state, or thumbnail.")
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
        return workspaceService.duplicateWorkspace(id, getCurrentUserId(), request != null ? request : new DuplicateWorkspaceRequest(null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Permanently delete workspace", description = "Permanently delete workspace and associated data.")
    public Map<String, String> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id, getCurrentUserId());
        return Map.of("message", "Workspace permanently deleted");
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore workspace from trash", description = "Restore soft-deleted workspace.")
    public WorkspaceDetails restoreWorkspace(@PathVariable String id) {
        return workspaceService.restoreWorkspace(id, getCurrentUserId());
    }

    @PostMapping("/{id}/thumbnail")
    @Operation(summary = "Update workspace thumbnail", description = "Set preview thumbnail string for backward compatibility.")
    public Map<String, Object> updateThumbnail(
            @PathVariable String id,
            @RequestBody ThumbnailRequest request) {
        return workspaceService.updateThumbnail(id, getCurrentUserId(), request);
    }

    // === SANDBOX / CANVAS STATE & EVENTS ===

    @GetMapping("/{id}/state")
    @Operation(summary = "Get canonical workspace state", description = "Load full canonical Sandbox state for canvas rendering.")
    public WorkspaceStateDto getWorkspaceState(@PathVariable String id) {
        return workspaceService.getState(id, getCurrentUserId());
    }

    @PutMapping("/{id}/state")
    @Operation(summary = "Save workspace canvas snapshot", description = "Snapshot save workspace canvas state with optimistic version locking.")
    public WorkspaceStateDto saveWorkspaceState(
            @PathVariable String id,
            @RequestParam(required = false) Long expectedVersion,
            @RequestBody WorkspaceStateDto stateDto) {
        return workspaceService.saveState(id, getCurrentUserId(), expectedVersion, stateDto);
    }

    @PostMapping("/{id}/events")
    @Operation(summary = "Append persistent workspace event", description = "Atomically apply and persist a discrete canvas event.")
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
    @Operation(summary = "Publish workspace", description = "Publish workspace for public viewing.")
    public Map<String, Object> publishWorkspace(
            @PathVariable String id,
            @RequestBody(required = false) PublishWorkspaceRequest request) {
        return workspaceService.publishWorkspace(id, getCurrentUserId(), request);
    }

    @PostMapping("/{id}/autosave")
    @Operation(summary = "Autosave workspace state", description = "Fast autosave endpoint triggered before navigation.")
    public Map<String, Object> autosave(
            @PathVariable String id,
            @RequestBody AutosaveRequest request) {
        return workspaceService.autosave(id, getCurrentUserId(), request);
    }

    // === PERMISSIONS & MEMBERSHIP ===

    @GetMapping("/{id}/permissions")
    @Operation(summary = "Get workspace permissions", description = "Retrieve current user's role and capabilities in this workspace.")
    public WorkspacePermissionsDto getPermissions(@PathVariable String id) {
        return memberService.getPermissions(id, getCurrentUserId());
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "List workspace members", description = "List all members with roles and masked emails.")
    public List<WorkspaceMemberDto> listMembers(@PathVariable String id) {
        return memberService.listMembers(id, getCurrentUserId());
    }

    @PatchMapping("/{id}/members/{memberUserId}")
    @Operation(summary = "Update member role", description = "Change member role (enforces LAST_OWNER protection).")
    public WorkspaceMemberDto updateMemberRole(
            @PathVariable String id,
            @PathVariable String memberUserId,
            @RequestBody Map<String, String> body) {
        String role = body.getOrDefault("role", "VIEWER");
        return memberService.updateMemberRole(id, getCurrentUserId(), memberUserId, role);
    }

    @DeleteMapping("/{id}/members/{memberUserId}")
    @Operation(summary = "Remove workspace member", description = "Remove user from workspace membership.")
    public Map<String, String> removeMember(
            @PathVariable String id,
            @PathVariable String memberUserId) {
        memberService.removeMember(id, getCurrentUserId(), memberUserId);
        return Map.of("message", "Member removed");
    }

    // === INVITATIONS ===

    @PostMapping("/{id}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create invitation", description = "Invite a user or email to collaborate on this workspace.")
    public Map<String, Object> createInvitation(
            @PathVariable String id,
            @Valid @RequestBody CreateInvitationRequest request) {
        return memberService.createInvitation(id, getCurrentUserId(), request);
    }

    @GetMapping("/{id}/invitations")
    @Operation(summary = "List pending invitations", description = "List active workspace invitations.")
    public List<WorkspaceInvitationDto> listInvitations(@PathVariable String id) {
        return memberService.listInvitations(id, getCurrentUserId());
    }

    @DeleteMapping("/{id}/invitations/{invitationId}")
    @Operation(summary = "Revoke invitation", description = "Cancel pending workspace invitation.")
    public Map<String, String> revokeInvitation(
            @PathVariable String id,
            @PathVariable String invitationId) {
        memberService.revokeInvitation(id, getCurrentUserId(), invitationId);
        return Map.of("message", "Invitation revoked");
    }

    // === SHARE LINKS ===

    @PostMapping("/{id}/share-links")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create share link", description = "Create opaque high-entropy share link.")
    public Map<String, Object> createShareLink(
            @PathVariable String id,
            @RequestBody(required = false) CreateShareLinkRequest request) {
        return shareService.createShareLink(id, getCurrentUserId(), request != null ? request : new CreateShareLinkRequest("VIEWER", null, null, null, true, true));
    }

    @GetMapping("/{id}/share-links")
    @Operation(summary = "List share links", description = "List active share links (raw secrets excluded).")
    public List<WorkspaceShareLinkDto> listShareLinks(@PathVariable String id) {
        return shareService.listShareLinks(id, getCurrentUserId());
    }

    @PatchMapping("/{id}/share-links/{linkId}")
    @Operation(summary = "Update share link", description = "Update role, expiry, max uses, or capabilities.")
    public WorkspaceShareLinkDto updateShareLink(
            @PathVariable String id,
            @PathVariable String linkId,
            @RequestBody UpdateShareLinkRequest request) {
        return shareService.updateShareLink(id, getCurrentUserId(), linkId, request);
    }

    @DeleteMapping("/{id}/share-links/{linkId}")
    @Operation(summary = "Revoke share link", description = "Immediately revoke a share link.")
    public Map<String, String> revokeShareLink(
            @PathVariable String id,
            @PathVariable String linkId) {
        shareService.revokeShareLink(id, getCurrentUserId(), linkId);
        return Map.of("message", "Share link revoked");
    }

    // === PREVIEWS ===

    @PostMapping("/{id}/preview-upload-urls")
    @Operation(summary = "Request preview upload URLs", description = "Generate signed upload targets for DARK and LIGHT WebP preview binaries.")
    public PreviewUploadUrlsResponse createPreviewUploadUrls(
            @PathVariable String id,
            @RequestBody PreviewUploadUrlsRequest request) {
        return previewService.createUploadUrls(id, getCurrentUserId(), request);
    }

    @PostMapping("/{id}/previews/{previewId}/complete")
    @Operation(summary = "Complete preview upload", description = "Atomically activate newly uploaded preview with 409 STALE_PREVIEW check.")
    public WorkspacePreviewDto completePreview(
            @PathVariable String id,
            @PathVariable String previewId,
            @RequestBody CompletePreviewRequest request) {
        return previewService.completePreview(id, getCurrentUserId(), previewId, request);
    }

    @GetMapping("/{id}/preview")
    @Operation(summary = "Get current workspace preview", description = "Retrieve current dark/light preview URLs and fallback keys.")
    public WorkspacePreviewDto getPreview(@PathVariable String id) {
        return previewService.getPreview(id, getCurrentUserId());
    }

    // === MEASUREMENTS ===

    @GetMapping("/{id}/measurements")
    @Operation(summary = "Query workspace measurements", description = "Query sensor time-series records for temperature, pH, mass.")
    public List<MeasurementPointDto> getWorkspaceMeasurements(
            @PathVariable String id,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "100") int limit) {
        memberService.requirePermission(id, getCurrentUserId(), "USE_MEASUREMENTS");
        return measurementService.getMeasurements(null, id, kind, from, to, limit);
    }

    // === TEAM CHAT ===

    @GetMapping("/{id}/chat/messages")
    @Operation(summary = "List chat messages", description = "Retrieve persistent team chat messages with cursor pagination and unread counts.")
    public ChatPageResponse listChatMessages(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
            @RequestParam(defaultValue = "50") int limit) {
        return chatService.listMessages(id, getCurrentUserId(), before, limit);
    }

    @PostMapping("/{id}/chat/messages")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send chat message", description = "Send a persistent message with clientMessageId idempotency.")
    public WorkspaceChatMessageDto sendChatMessage(
            @PathVariable String id,
            @Valid @RequestBody SendChatMessageRequest request) {
        return chatService.sendMessage(id, getCurrentUserId(), request);
    }

    @PatchMapping("/{id}/chat/messages/{messageId}")
    @Operation(summary = "Edit chat message", description = "Edit an existing chat message.")
    public WorkspaceChatMessageDto updateChatMessage(
            @PathVariable String id,
            @PathVariable String messageId,
            @Valid @RequestBody UpdateChatMessageRequest request) {
        return chatService.updateMessage(id, getCurrentUserId(), messageId, request);
    }

    @DeleteMapping("/{id}/chat/messages/{messageId}")
    @Operation(summary = "Delete chat message", description = "Soft delete a chat message.")
    public Map<String, String> deleteChatMessage(
            @PathVariable String id,
            @PathVariable String messageId) {
        chatService.deleteMessage(id, getCurrentUserId(), messageId);
        return Map.of("message", "Message deleted");
    }

    @PostMapping("/{id}/chat/read")
    @Operation(summary = "Mark chat as read", description = "Update read receipt and last read message ID.")
    public Map<String, String> markChatRead(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String messageId = body.get("messageId");
        chatService.markRead(id, getCurrentUserId(), messageId);
        return Map.of("status", "ok");
    }

    // === ANCHORED COMMENTS ===

    @GetMapping("/{id}/comments")
    @Operation(summary = "List comment threads", description = "List all anchored comment threads and replies.")
    public List<WorkspaceCommentThreadDto> listComments(@PathVariable String id) {
        return commentService.listThreads(id, getCurrentUserId());
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create comment thread", description = "Create a comment anchored to an item or canvas position.")
    public WorkspaceCommentThreadDto createCommentThread(
            @PathVariable String id,
            @Valid @RequestBody CreateCommentThreadRequest request) {
        return commentService.createThread(id, getCurrentUserId(), request);
    }

    @PostMapping("/{id}/comments/{threadId}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add comment reply", description = "Add a reply to an existing comment thread.")
    public WorkspaceCommentThreadDto addCommentReply(
            @PathVariable String id,
            @PathVariable String threadId,
            @Valid @RequestBody AddCommentReplyRequest request) {
        return commentService.addReply(id, threadId, getCurrentUserId(), request);
    }

    @PatchMapping("/{id}/comments/{threadId}")
    @Operation(summary = "Update comment status", description = "Resolve or reopen a comment thread.")
    public WorkspaceCommentThreadDto updateCommentStatus(
            @PathVariable String id,
            @PathVariable String threadId,
            @Valid @RequestBody UpdateCommentThreadStatusRequest request) {
        return commentService.updateStatus(id, threadId, getCurrentUserId(), request);
    }
}
