package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceEventRepository extends JpaRepository<WorkspaceEventEntity, String> {

    Optional<WorkspaceEventEntity> findByWorkspaceIdAndUserIdAndClientEventId(String workspaceId, String userId, String clientEventId);

    List<WorkspaceEventEntity> findByWorkspaceIdAndVersionGreaterThanOrderByVersionAsc(String workspaceId, long version);

    List<WorkspaceEventEntity> findByWorkspaceIdOrderByVersionAsc(String workspaceId);
}
