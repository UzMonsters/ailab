package com.ailab.workspace.dto;

import java.time.Instant;

public record CreateShareLinkRequest(
        String role, // VIEWER, EDITOR
        Instant expiresAt,
        String password,
        Integer maxUses,
        Boolean allowChat,
        Boolean allowComments
) {
}
