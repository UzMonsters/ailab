package com.ailab.workspace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateWorkspaceRequest(
        String name,
        Boolean isFavorite,
        Boolean isDeleted,
        String thumbnail,
        @JsonProperty("stateVersion") Long stateVersion,
        @JsonProperty("expectedVersion") Long expectedVersion
) {
    public UpdateWorkspaceRequest(String name, Boolean isFavorite, Boolean isDeleted, String thumbnail, Long stateVersion) {
        this(name, isFavorite, isDeleted, thumbnail, stateVersion, stateVersion);
    }

    public Long resolvedExpectedVersion() {
        return expectedVersion != null ? expectedVersion : stateVersion;
    }
}
