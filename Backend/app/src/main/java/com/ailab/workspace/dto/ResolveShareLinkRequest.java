package com.ailab.workspace.dto;

import jakarta.validation.constraints.NotBlank;

public record ResolveShareLinkRequest(
        @NotBlank(message = "token is required")
        String token,
        String password
) {
}
