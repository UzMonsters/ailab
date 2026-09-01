package com.ailab.workspace.dto;

import java.time.Instant;
import java.util.List;

public record WorkspaceShareLinkDto(
        String id,
        String linkId,
        String url,
        String role, // VIEWER, EDITOR
        Instant expiresAt,
        Integer maxUses,
        Integer useCount,
        Boolean allowChat,
        Boolean allowComments,
        List<String> capabilities,
        Instant lastUsedAt,
        Instant createdAt
) {
    public WorkspaceShareLinkDto(
            String id,
            String url,
            String role,
            Instant expiresAt,
            Integer maxUses,
            Integer useCount,
            Boolean allowChat,
            Boolean allowComments,
            List<String> capabilities,
            Instant lastUsedAt,
            Instant createdAt
    ) {
        this(id, id, url, role, expiresAt, maxUses, useCount, allowChat, allowComments, capabilities, lastUsedAt, createdAt);
    }
}
