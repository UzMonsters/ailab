package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceCommentReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceCommentReplyRepository extends JpaRepository<WorkspaceCommentReplyEntity, String> {

    List<WorkspaceCommentReplyEntity> findByThreadId(String threadId);
}
