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

    @Query("SELECT w FROM WorkspaceEntity w WHERE w.isDeleted = false " +
           "AND (:hasScience = false OR LOWER(w.science) = LOWER(:science)) " +
           "AND (:hasOwner = false OR w.ownerId = :ownerId) " +
           "AND (:hasSearch = false OR LOWER(w.name) LIKE CONCAT('%', LOWER(:search), '%'))")
    Page<WorkspaceEntity> findLaboratories(
            @Param("search") String search,
            @Param("hasSearch") boolean hasSearch,
            @Param("science") String science,
            @Param("hasScience") boolean hasScience,
            @Param("ownerId") String ownerId,
            @Param("hasOwner") boolean hasOwner,
            Pageable pageable
    );

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

    @Query(value = """
           SELECT w FROM WorkspaceEntity w
           WHERE (:hasScience = false OR LOWER(w.science) = :science)
             AND (:hasSearch = false OR LOWER(w.name) LIKE CONCAT('%', :search, '%'))
             AND (:status = '' OR (:status = 'ACTIVE' AND w.isDeleted = false) OR (:status = 'DELETED' AND w.isDeleted = true))
             AND (:hasOwner = false OR w.ownerId = :ownerId)
             AND (:hasActiveLinksFilter = false
                  OR (:hasActiveLinks = true AND w.id IN (
                      SELECT l.workspaceId FROM WorkspaceShareLinkEntity l
                      WHERE l.revokedAt IS NULL AND (l.expiresAt IS NULL OR l.expiresAt > :now) AND (l.maxUses IS NULL OR l.useCount < l.maxUses)
                  ))
                  OR (:hasActiveLinks = false AND w.id NOT IN (
                      SELECT l.workspaceId FROM WorkspaceShareLinkEntity l
                      WHERE l.revokedAt IS NULL AND (l.expiresAt IS NULL OR l.expiresAt > :now) AND (l.maxUses IS NULL OR l.useCount < l.maxUses)
                  )))
           """,
           countQuery = """
           SELECT COUNT(w) FROM WorkspaceEntity w
           WHERE (:hasScience = false OR LOWER(w.science) = :science)
             AND (:hasSearch = false OR LOWER(w.name) LIKE CONCAT('%', :search, '%'))
             AND (:status = '' OR (:status = 'ACTIVE' AND w.isDeleted = false) OR (:status = 'DELETED' AND w.isDeleted = true))
             AND (:hasOwner = false OR w.ownerId = :ownerId)
             AND (:hasActiveLinksFilter = false
                  OR (:hasActiveLinks = true AND w.id IN (
                      SELECT l.workspaceId FROM WorkspaceShareLinkEntity l
                      WHERE l.revokedAt IS NULL AND (l.expiresAt IS NULL OR l.expiresAt > :now) AND (l.maxUses IS NULL OR l.useCount < l.maxUses)
                  ))
                  OR (:hasActiveLinks = false AND w.id NOT IN (
                      SELECT l.workspaceId FROM WorkspaceShareLinkEntity l
                      WHERE l.revokedAt IS NULL AND (l.expiresAt IS NULL OR l.expiresAt > :now) AND (l.maxUses IS NULL OR l.useCount < l.maxUses)
                  )))
           """)
    Page<WorkspaceEntity> findAdminWorkspaces(
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


    long countByIsDeletedFalse();

    @Query("SELECT w.science, COUNT(w) FROM WorkspaceEntity w WHERE w.isDeleted = false GROUP BY w.science")
    java.util.List<Object[]> countWorkspacesByScience();
}
