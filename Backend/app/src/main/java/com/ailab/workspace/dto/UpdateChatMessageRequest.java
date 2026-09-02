package com.ailab.workspace.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateChatMessageRequest(
        @NotBlank(message = "body must not be blank")
        String body
) {
}
