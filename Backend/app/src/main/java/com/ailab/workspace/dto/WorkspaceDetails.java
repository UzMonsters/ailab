package com.ailab.workspace.dto;

import com.ailab.workspace.domain.WorkspaceEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record WorkspaceDetails(
        String id,
        String ownerId,
        String name,
        String science,
        String thumbnail,
        WorkspacePreviewDto preview,
        String accessLevel, // OWNER, EDITOR, VIEWER
        long stateVersion,
        boolean isFavorite,
        boolean isDeleted,
        String experimentSessionId,
        Instant createdAt,
        Instant updatedAt
) {
    public WorkspaceDetails(
            String id,
            String ownerId,
            String name,
            String science,
            String thumbnail,
            long stateVersion,
            boolean isFavorite,
            boolean isDeleted,
            String experimentSessionId,
            Instant createdAt,
            Instant updatedAt
    ) {
        this(
                id,
                ownerId,
                name,
                science,
                thumbnail,
                thumbnail != null ? WorkspacePreviewDto.of(stateVersion, thumbnail, thumbnail, "chemistry-default-01") : WorkspacePreviewDto.fallback("chemistry-default-01"),
                "OWNER",
                stateVersion,
                isFavorite,
                isDeleted,
                experimentSessionId,
                createdAt,
                updatedAt
        );
    }

    public static WorkspaceDetails fromEntity(WorkspaceEntity entity) {
        return fromEntity(entity, "OWNER", null);
    }

    public static WorkspaceDetails fromEntity(WorkspaceEntity entity, String accessLevel, WorkspacePreviewDto preview) {
        WorkspacePreviewDto resolvedPreview = preview != null ? preview
                : (entity.getThumbnail() != null ? WorkspacePreviewDto.of(entity.getStateVersion(), entity.getThumbnail(), entity.getThumbnail(), "chemistry-default-01")
                : WorkspacePreviewDto.fallback("chemistry-default-01"));
        return new WorkspaceDetails(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getScience(),
                entity.getThumbnail(),
                resolvedPreview,
                accessLevel != null ? accessLevel : "OWNER",
                entity.getStateVersion(),
                entity.isFavorite(),
                entity.isDeleted(),
                entity.getExperimentSessionId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
