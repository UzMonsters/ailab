package com.ailab.workspace.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWorkspaceRequest(
        @NotBlank(message = "Workspace name must not be blank")
        String name,
        String science
) {}
