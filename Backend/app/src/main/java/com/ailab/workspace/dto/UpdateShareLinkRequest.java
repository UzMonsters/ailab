package com.ailab.workspace.dto;

import java.time.Instant;

public record UpdateShareLinkRequest(
        String role,
        Instant expiresAt,
        Integer maxUses,
        Boolean allowChat,
        Boolean allowComments
) {
}
