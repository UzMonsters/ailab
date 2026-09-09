package com.ailab.workspace.repository;

import com.ailab.admin.workspace.AdminWorkspaceSummaryRow;
import com.ailab.workspace.domain.WorkspaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, String> {

    Optional<WorkspaceEntity> findByIdAndOwnerId(String id, String ownerId);

    Optional<WorkspaceEntity> findByExperimentSessionId(String experimentSessionId);

    Optional<WorkspaceEntity> findByExperimentSessionIdAndOwnerId(String experimentSessionId, String ownerId);

    @Query("SELECT w FROM WorkspaceEntity w WHERE w.ownerId = :ownerId " +
           "AND (:includeDeleted = true OR w.isDeleted = false) " +
           "AND (:hasScience = false OR LOWER(w.science) = :science) " +
           "AND (:hasSearch = false OR LOWER(w.name) LIKE CONCAT('%', :search, '%'))")
    Page<WorkspaceEntity> findAllByOwner(
            @Param("ownerId") String ownerId,
            @Param("science") String science,
            @Param("search") String search,
            @Param("hasScience") boolean hasScience,
            @Param("hasSearch") boolean hasSearch,
            @Param("includeDeleted") boolean includeDeleted,
            Pageable pageable
    );

    @Query("""
           SELECT new com.ailab.admin.workspace.AdminWorkspaceSummaryRow(
               w.id,
               w.name,
               w.science,
               CASE WHEN w.isDeleted = true THEN 'DELETED' ELSE 'ACTIVE' END,
               w.ownerId,
               w.stateVersion,
               w.updatedAt,
               (SELECT COUNT(m) FROM WorkspaceMemberEntity m WHERE m.workspaceId = w.id),
               (SELECT COUNT(l) FROM WorkspaceShareLinkEntity l WHERE l.workspaceId = w.id AND l.revokedAt IS NULL AND (l.expiresAt IS NULL OR l.expiresAt > :now) AND (l.maxUses IS NULL OR l.useCount < l.maxUses)),
               (SELECT COUNT(i) FROM WorkspaceInvitationEntity i WHERE i.workspaceId = w.id AND i.status = 'PENDING')
           )
           FROM WorkspaceEntity w
           WHERE (:hasScience = false OR LOWER(w.science) = :science)
             AND (:hasSearch = false OR LOWER(w.name) LIKE CONCAT('%', :search, '%'))
             AND (:status = '' OR (:status = 'ACTIVE' AND w.isDeleted = false) OR (:status = 'DELETED' AND w.isDeleted = true))
             AND (:hasOwner = false OR w.ownerId = :ownerId)
             AND (:hasActiveLinksFilter = false
                  OR (:hasActiveLinks = true AND EXISTS (
                      SELECT 1 FROM WorkspaceShareLinkEntity l
                      WHERE l.workspaceId = w.id AND l.revokedAt IS NULL AND (l.expiresAt IS NULL OR l.expiresAt > :now) AND (l.maxUses IS NULL OR l.useCount < l.maxUses)
                  ))
                  OR (:hasActiveLinks = false AND NOT EXISTS (
                      SELECT 1 FROM WorkspaceShareLinkEntity l
                      WHERE l.workspaceId = w.id AND l.revokedAt IS NULL AND (l.expiresAt IS NULL OR l.expiresAt > :now) AND (l.maxUses IS NULL OR l.useCount < l.maxUses)
                  )))
           """)
    Page<AdminWorkspaceSummaryRow> findAdminWorkspaceSummaries(
            @Param("search") String search,
            @Param("hasSearch") boolean hasSearch,
            @Param("science") String science,
            @Param("hasScience") boolean hasScience,
            @Param("status") String status,
            @Param("ownerId") String ownerId,
            @Param("hasOwner") boolean hasOwner,
            @Param("hasActiveLinks") Boolean hasActiveLinks,
            @Param("hasActiveLinksFilter") boolean hasActiveLinksFilter,
            @Param("now") Instant now,
            Pageable pageable
    );
}
