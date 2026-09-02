package com.ailab.workspace.service;

import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.dto.WorkspacePermissionsDto;
import com.ailab.workspace.exception.WorkspaceNotFoundException;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LaboratoryAccessService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberService memberService;

    public LaboratoryAccessService(WorkspaceRepository workspaceRepository, WorkspaceMemberService memberService) {
        this.workspaceRepository = workspaceRepository;
        this.memberService = memberService;
    }

    @Transactional(readOnly = true)
    public void verifyWorkspaceAccess(String workspaceId, String userId) {
        if (userId != null && userId.startsWith("guest_")) return; // guest session
        WorkspaceEntity ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        WorkspacePermissionsDto perms = memberService.getPermissions(workspaceId, userId);
        if ("NONE".equals(perms.role())) {
            throw new AccessDeniedException("Access denied to workspace: " + workspaceId);
        }
    }

    @Transactional(readOnly = true)
    public void verifyExperimentAccess(String sessionId, String userId) {
        if (userId != null && userId.startsWith("guest_")) return;
        workspaceRepository.findByExperimentSessionId(sessionId).ifPresent(workspace -> {
            WorkspacePermissionsDto perms = memberService.getPermissions(workspace.getId(), userId);
            if ("NONE".equals(perms.role())) {
                throw new AccessDeniedException("Access denied to experiment session: " + sessionId);
            }
        });
    }
}
