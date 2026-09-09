package com.ailab.workspace.controller;

import com.ailab.workspace.dto.AcceptInvitationRequest;
import com.ailab.workspace.dto.ResolveShareLinkRequest;
import com.ailab.workspace.dto.ResolveShareLinkResponse;
import com.ailab.workspace.security.WorkspaceAccessResolver;
import com.ailab.workspace.service.WorkspaceMemberService;
import com.ailab.workspace.service.WorkspaceShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Workspace Sharing & Invitations", description = "Endpoints for resolving public/password-protected share links and accepting invitations")
public class WorkspaceShareController {

    private final WorkspaceShareService shareService;
    private final WorkspaceMemberService memberService;
    private final WorkspaceAccessResolver accessResolver;

    public WorkspaceShareController(WorkspaceShareService shareService, WorkspaceMemberService memberService, WorkspaceAccessResolver accessResolver) {
        this.shareService = shareService;
        this.memberService = memberService;
        this.accessResolver = accessResolver;
    }

    private String getCurrentUserId() {
        return accessResolver.requireUser();
    }

    @PostMapping("/shared-workspaces/resolve")
    @Operation(summary = "Resolve shared workspace link", description = "Validate opaque share token, optional password, expiry and max uses, issuing temporary access.")
    public ResponseEntity<ResolveShareLinkResponse> resolveShareLink(@Valid @RequestBody ResolveShareLinkRequest request) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(shareService.resolveShareLink(request));
    }

    @PostMapping("/workspace-invitations/{token}/accept")
    @Operation(summary = "Accept workspace invitation", description = "Accept pending invitation and establish membership.")
    public Map<String, Object> acceptInvitation(
            @PathVariable String token,
            @RequestBody(required = false) AcceptInvitationRequest request) {
        return memberService.acceptInvitation(token, getCurrentUserId());
    }
}
