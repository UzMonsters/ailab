package com.ailab.workspace.dto;

import java.util.List;

public record WorkspacePermissionsDto(
        String role, // OWNER, EDITOR, VIEWER, GUEST_VIEWER, GUEST_EDITOR
        List<String> capabilities
) {
    public static WorkspacePermissionsDto of(String role, List<String> capabilities) {
        return new WorkspacePermissionsDto(role, capabilities != null ? capabilities : List.of());
    }
}
