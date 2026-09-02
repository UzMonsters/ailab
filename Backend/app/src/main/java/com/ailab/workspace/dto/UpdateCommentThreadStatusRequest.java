package com.ailab.workspace.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentThreadStatusRequest(
        @NotBlank(message = "status must not be blank")
        String status // OPEN, RESOLVED
) {
}
