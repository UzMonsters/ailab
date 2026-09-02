package com.ailab.workspace.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record WorkspaceCommentThreadDto(
        String id,
        String workspaceId,
        Author author,
        Map<String, Object> anchor,
        String status, // OPEN, RESOLVED
        List<WorkspaceCommentReplyDto> replies,
        Instant createdAt,
        Instant updatedAt
) {
    public record Author(
            String id,
            String displayName,
            String avatarUrl
    ) {}
}
