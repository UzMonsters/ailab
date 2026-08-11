package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, String> {

    Optional<WorkspaceEntity> findByIdAndOwnerId(String id, String ownerId);

    Optional<WorkspaceEntity> findByExperimentSessionId(String experimentSessionId);

    Optional<WorkspaceEntity> findByExperimentSessionIdAndOwnerId(String experimentSessionId, String ownerId);

    @Query("SELECT w FROM WorkspaceEntity w WHERE w.ownerId = :ownerId " +
           "AND (:includeDeleted = true OR w.isDeleted = false) " +
           "AND (:science IS NULL OR LTRIM(RTRIM(:science)) = '' OR LOWER(w.science) = LOWER(:science)) " +
           "AND (:search IS NULL OR LTRIM(RTRIM(:search)) = '' OR LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<WorkspaceEntity> findAllByOwner(
            @Param("ownerId") String ownerId,
            @Param("science") String science,
            @Param("search") String search,
            @Param("includeDeleted") boolean includeDeleted,
            Pageable pageable
    );
}
