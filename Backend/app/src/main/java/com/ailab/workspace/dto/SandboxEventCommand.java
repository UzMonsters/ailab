package com.ailab.workspace.dto;

import java.util.Map;

public record SandboxEventCommand(
        String clientEventId,
        Long expectedVersion,
        String eventType,
        Map<String, Object> payload
) {}
