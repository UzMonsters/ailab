package com.ailab.workspace.dto;

import java.time.Instant;
import java.util.Map;

public record WorkspaceInvitationDto(
        String invitationId,
        String workspaceId,
        Map<String, String> invitee,
        String role,
        String status,
        Instant expiresAt,
        Instant createdAt
) {
}
