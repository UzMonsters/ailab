package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitationEntity, String> {

    List<WorkspaceInvitationEntity> findByWorkspaceId(String workspaceId);

    List<WorkspaceInvitationEntity> findByEmail(String email);

    Optional<WorkspaceInvitationEntity> findByTokenHash(String tokenHash);

    Optional<WorkspaceInvitationEntity> findByIdAndWorkspaceId(String id, String workspaceId);

    void deleteByWorkspaceId(String workspaceId);

    @org.springframework.data.jpa.repository.Query("SELECT i.workspaceId, COUNT(i) FROM WorkspaceInvitationEntity i WHERE i.workspaceId IN :workspaceIds AND i.status = 'PENDING' GROUP BY i.workspaceId")
    List<Object[]> countPendingInvitationsByWorkspaceIds(@org.springframework.data.repository.query.Param("workspaceIds") java.util.Collection<String> workspaceIds);
}

