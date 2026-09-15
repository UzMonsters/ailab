package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceShareLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceShareLinkRepository extends JpaRepository<WorkspaceShareLinkEntity, String> {

    List<WorkspaceShareLinkEntity> findByWorkspaceId(String workspaceId);

    @Query("SELECT l.workspaceId, COUNT(l) FROM WorkspaceShareLinkEntity l WHERE l.workspaceId IN :workspaceIds AND l.revokedAt IS NULL AND (l.expiresAt IS NULL OR l.expiresAt > :now) AND (l.maxUses IS NULL OR l.useCount < l.maxUses) GROUP BY l.workspaceId")
    List<Object[]> countActiveByWorkspaceIds(
            @Param("workspaceIds") List<String> workspaceIds,
            @Param("now") Instant now
    );

    Optional<WorkspaceShareLinkEntity> findByTokenHash(String tokenHash);

    Optional<WorkspaceShareLinkEntity> findByIdAndWorkspaceId(String id, String workspaceId);

    void deleteByWorkspaceId(String workspaceId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE WorkspaceShareLinkEntity l SET l.useCount = l.useCount + 1, l.lastUsedAt = :now WHERE l.id = :id AND (l.maxUses IS NULL OR l.useCount < l.maxUses)")
    int incrementUseCountAtomic(@org.springframework.data.repository.query.Param("id") String id, @org.springframework.data.repository.query.Param("now") java.time.Instant now);
}
