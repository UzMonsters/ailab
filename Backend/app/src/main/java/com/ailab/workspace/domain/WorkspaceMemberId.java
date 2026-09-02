package com.ailab.workspace.domain;

import java.io.Serializable;
import java.util.Objects;

public class WorkspaceMemberId implements Serializable {
    private String workspaceId;
    private String userId;

    public WorkspaceMemberId() {}

    public WorkspaceMemberId(String workspaceId, String userId) {
        this.workspaceId = workspaceId;
        this.userId = userId;
    }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceMemberId that)) return false;
        return Objects.equals(workspaceId, that.workspaceId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workspaceId, userId);
    }
}
