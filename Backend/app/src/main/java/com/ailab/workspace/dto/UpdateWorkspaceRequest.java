package com.ailab.workspace.dto;

public record UpdateWorkspaceRequest(
        String name,
        Boolean isFavorite,
        Boolean isDeleted,
        String thumbnail,
        Long stateVersion
) {}
