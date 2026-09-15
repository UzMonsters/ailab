package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, String>, JpaSpecificationExecutor<WorkspaceEntity> {

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

    long countByIsDeletedFalse();

    @Query("SELECT w.science, COUNT(w) FROM WorkspaceEntity w WHERE w.isDeleted = false GROUP BY w.science")
    java.util.List<Object[]> countWorkspacesByScience();
}
