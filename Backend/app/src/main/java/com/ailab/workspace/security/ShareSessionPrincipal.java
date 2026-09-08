package com.ailab.workspace.security;

import java.time.Instant;
import java.util.List;

public record ShareSessionPrincipal(
        String guestId,
        String linkId,
        String workspaceId,
        String role,
        List<String> capabilities,
        Instant expiresAt
) {
    public boolean grants(String requestedWorkspaceId, String capability) {
        return workspaceId.equals(requestedWorkspaceId) && capabilities.contains(capability);
    }
}
