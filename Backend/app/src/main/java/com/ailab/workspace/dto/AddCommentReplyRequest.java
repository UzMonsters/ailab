package com.ailab.workspace.dto;

import jakarta.validation.constraints.NotBlank;

public record AddCommentReplyRequest(
        String clientMessageId,
        @NotBlank(message = "body must not be blank")
        String body
) {
}
