package com.ailab.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record CreateCommentThreadRequest(
        @NotBlank(message = "body must not be blank")
        String body,
        Map<String, Object> anchor
) {
}
