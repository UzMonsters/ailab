package com.ailab.workspace.dto;

import com.ailab.workspace.domain.WorkspaceEntity;
import java.time.Instant;

public record WorkspaceDetails(
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
    public static WorkspaceDetails fromEntity(WorkspaceEntity entity) {
        return new WorkspaceDetails(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getScience(),
                entity.getThumbnail(),
                entity.getStateVersion(),
                entity.isFavorite(),
                entity.isDeleted(),
                entity.getExperimentSessionId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
