package com.ailab.workspace.dto;

import java.time.Instant;
import java.util.List;

public record ResolveShareLinkResponse(
        String workspaceId,
        String name,
        String science,
        WorkspacePreviewDto preview,
        String role, // VIEWER, EDITOR
        List<String> capabilities,
        boolean requiresAuth,
        Instant expiresAt,
        String shareSessionToken
) {
}
