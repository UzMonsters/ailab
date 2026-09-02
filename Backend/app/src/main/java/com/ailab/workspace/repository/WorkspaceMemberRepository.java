package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceMemberEntity;
import com.ailab.workspace.domain.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMemberEntity, WorkspaceMemberId> {

    List<WorkspaceMemberEntity> findByWorkspaceId(String workspaceId);

    List<WorkspaceMemberEntity> findByUserId(String userId);

    Optional<WorkspaceMemberEntity> findByWorkspaceIdAndUserId(String workspaceId, String userId);

    @Query("SELECT COUNT(m) FROM WorkspaceMemberEntity m WHERE m.workspaceId = :workspaceId AND m.role = 'OWNER'")
    long countOwnersByWorkspaceId(String workspaceId);

    void deleteByWorkspaceIdAndUserId(String workspaceId, String userId);
}
