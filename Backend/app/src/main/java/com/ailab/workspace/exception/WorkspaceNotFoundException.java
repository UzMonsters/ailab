package com.ailab.workspace.exception;

public class WorkspaceNotFoundException extends RuntimeException {
    public WorkspaceNotFoundException(String workspaceId) {
        super("Workspace not found or access denied: " + workspaceId);
    }
}
