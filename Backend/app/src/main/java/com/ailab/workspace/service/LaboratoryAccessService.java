package com.ailab.workspace.service;

import com.ailab.workspace.exception.WorkspaceNotFoundException;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LaboratoryAccessService {
    private final WorkspaceRepository workspaceRepository;

    public LaboratoryAccessService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional(readOnly = true)
    public void verifyWorkspaceAccess(String workspaceId, String userId) {
        workspaceRepository.findByIdAndOwnerId(workspaceId, userId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
    }

    @Transactional(readOnly = true)
    public void verifyExperimentAccess(String sessionId, String userId) {
        workspaceRepository.findByExperimentSessionId(sessionId).ifPresent(workspace -> {
            if (!workspace.getOwnerId().equals(userId)) {
                throw new WorkspaceNotFoundException(workspace.getId());
            }
        });
    }
}
