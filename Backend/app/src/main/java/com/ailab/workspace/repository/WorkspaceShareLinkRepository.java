package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceShareLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceShareLinkRepository extends JpaRepository<WorkspaceShareLinkEntity, String> {

    List<WorkspaceShareLinkEntity> findByWorkspaceId(String workspaceId);

    Optional<WorkspaceShareLinkEntity> findByTokenHash(String tokenHash);

    Optional<WorkspaceShareLinkEntity> findByIdAndWorkspaceId(String id, String workspaceId);
}
