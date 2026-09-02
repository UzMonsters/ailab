package com.ailab.workspace.dto;

import java.time.Instant;

public record WorkspaceCommentReplyDto(
        String id,
        String threadId,
        Author author,
        String body,
        Instant createdAt
) {
    public record Author(
            String id,
            String displayName,
            String avatarUrl
    ) {}
}
