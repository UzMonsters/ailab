package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceCommentThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceCommentThreadRepository extends JpaRepository<WorkspaceCommentThreadEntity, String> {

    @Query("SELECT t FROM WorkspaceCommentThreadEntity t LEFT JOIN FETCH t.replies WHERE t.workspaceId = :workspaceId ORDER BY t.createdAt ASC")
    List<WorkspaceCommentThreadEntity> findByWorkspaceIdWithReplies(String workspaceId);

    Optional<WorkspaceCommentThreadEntity> findByIdAndWorkspaceId(String id, String workspaceId);
}
