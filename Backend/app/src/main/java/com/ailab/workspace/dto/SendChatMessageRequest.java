package com.ailab.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record SendChatMessageRequest(
        String clientMessageId,
        @NotBlank(message = "body must not be blank")
        String body,
        String replyToMessageId,
        Map<String, Object> anchor
) {
}
