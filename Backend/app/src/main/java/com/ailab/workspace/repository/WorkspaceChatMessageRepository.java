package com.ailab.workspace.repository;

import com.ailab.workspace.domain.WorkspaceChatMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceChatMessageRepository extends JpaRepository<WorkspaceChatMessageEntity, String> {

    Optional<WorkspaceChatMessageEntity> findByWorkspaceIdAndClientMessageId(String workspaceId, String clientMessageId);

    @Query("SELECT m FROM WorkspaceChatMessageEntity m WHERE m.workspaceId = :workspaceId ORDER BY m.createdAt DESC")
    List<WorkspaceChatMessageEntity> findLatestMessages(String workspaceId, Pageable pageable);

    @Query("SELECT m FROM WorkspaceChatMessageEntity m WHERE m.workspaceId = :workspaceId AND m.createdAt < :beforeTimestamp ORDER BY m.createdAt DESC")
    List<WorkspaceChatMessageEntity> findMessagesBefore(String workspaceId, Instant beforeTimestamp, Pageable pageable);

    @Query("SELECT COUNT(m) FROM WorkspaceChatMessageEntity m WHERE m.workspaceId = :workspaceId AND m.createdAt > :afterTimestamp AND m.isDeleted = false")
    long countUnreadMessages(String workspaceId, Instant afterTimestamp);

    Optional<WorkspaceChatMessageEntity> findByIdAndWorkspaceId(String id, String workspaceId);
}
