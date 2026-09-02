package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspacePreviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspacePreviewRepository extends JpaRepository<WorkspacePreviewEntity, String> {

    Optional<WorkspacePreviewEntity> findTopByWorkspaceIdOrderBySourceStateVersionDesc(String workspaceId);

    Optional<WorkspacePreviewEntity> findByIdAndWorkspaceId(String id, String workspaceId);

    List<WorkspacePreviewEntity> findByWorkspaceId(String workspaceId);
}
