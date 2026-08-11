package com.ailab.workspace.dto;

import java.util.List;

public record WorkspacePageResponse<T>(
        List<T> items,
        int page,
        int size,
        long total
) {}
