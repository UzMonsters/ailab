package com.ailab.workspace.dto;

import java.util.List;

public record ChatPageResponse(
        List<WorkspaceChatMessageDto> items,
        String nextCursor,
        long unreadCount
) {
}
