package com.ailab.workspace.dto;

import java.util.List;
import java.util.Map;

public record WorkspaceEventAck(
        String clientEventId,
        String eventId,
        String eventType,
        String workspaceId,
        String sessionId,
        long stateVersion,
        Map<String, Object> stateDelta,
        List<String> safetyWarnings,
        String occurredAt
) {}
