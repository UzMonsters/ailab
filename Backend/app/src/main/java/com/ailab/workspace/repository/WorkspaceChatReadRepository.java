package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceChatReadEntity;
import com.ailab.workspace.domain.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceChatReadRepository extends JpaRepository<WorkspaceChatReadEntity, WorkspaceMemberId> {

    Optional<WorkspaceChatReadEntity> findByWorkspaceIdAndUserId(String workspaceId, String userId);
}
