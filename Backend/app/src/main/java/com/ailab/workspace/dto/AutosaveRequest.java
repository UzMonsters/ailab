package com.ailab.workspace.dto;

import java.util.Map;

public record AutosaveRequest(
        Long expectedVersion,
        String stateHash,
        Map<String, Object> state
) {}
