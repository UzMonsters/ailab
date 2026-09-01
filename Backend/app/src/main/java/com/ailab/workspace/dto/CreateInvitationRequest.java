package com.ailab.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record CreateInvitationRequest(
        @NotBlank(message = "emailOrUserId is required")
        String emailOrUserId,
        String role, // EDITOR, VIEWER
        Instant expiresAt,
        String message
) {
}
