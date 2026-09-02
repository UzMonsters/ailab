package com.ailab.workspace.dto;

import java.time.Instant;
import java.util.Map;

public record WorkspaceChatMessageDto(
        String id,
        String clientMessageId,
        String workspaceId,
        Author author,
        String body,
        String replyToMessageId,
        Map<String, Object> anchor,
        Instant createdAt,
        Instant editedAt,
        Instant deletedAt
) {
    public record Author(
            String id,
            String displayName,
            String avatarUrl
    ) {}
}
