package com.ailab.workspace.dto;

import java.util.List;
import java.util.Map;

public record WorkspaceStateDto(
        String workspaceId,
        String sessionId,
        long stateVersion,
        Map<String, Object> viewport,
        Map<String, Object> grid,
        List<Map<String, Object>> items,
        List<Map<String, Object>> connections,
        List<Map<String, Object>> log,
        String updatedAt
) {}
