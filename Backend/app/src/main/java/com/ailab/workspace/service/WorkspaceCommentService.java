package com.ailab.workspace.service;

import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.domain.WorkspaceCommentReplyEntity;
import com.ailab.workspace.domain.WorkspaceCommentThreadEntity;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.repository.WorkspaceCommentReplyRepository;
import com.ailab.workspace.repository.WorkspaceCommentThreadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
public class WorkspaceCommentService {

    private final WorkspaceCommentThreadRepository threadRepository;
    private final WorkspaceCommentReplyRepository replyRepository;
    private final WorkspaceMemberService memberService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WorkspaceCommentService(
            WorkspaceCommentThreadRepository threadRepository,
            WorkspaceCommentReplyRepository replyRepository,
            WorkspaceMemberService memberService,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.threadRepository = threadRepository;
        this.replyRepository = replyRepository;
        this.memberService = memberService;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<WorkspaceCommentThreadDto> listThreads(String workspaceId, String actorUserId) {
        memberService.requirePermission(workspaceId, actorUserId, "COMMENT");
        List<WorkspaceCommentThreadEntity> list = threadRepository.findByWorkspaceIdWithReplies(workspaceId);
        return list.stream().map(this::toDto).toList();
    }

    @Transactional
    public WorkspaceCommentThreadDto createThread(String workspaceId, String actorUserId, CreateCommentThreadRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "COMMENT");

        String authorName = "User " + actorUserId;
        String authorAvatar = null;
        Optional<User> uOpt = userRepository.findById(actorUserId);
        if (uOpt.isPresent()) {
            User u = uOpt.get();
            authorName = u.getUsername();
            authorAvatar = u.getAvatarUrl();
        }

        String anchorItemId = null;
        Double anchorX = null;
        Double anchorY = null;
        Long anchorVersion = null;

        if (request.anchor() != null) {
            Object itemObj = request.anchor().get("itemId");
            if (itemObj != null) anchorItemId = itemObj.toString();
            Object xObj = request.anchor().get("x");
            if (xObj instanceof Number n) anchorX = n.doubleValue();
            Object yObj = request.anchor().get("y");
            if (yObj instanceof Number n) anchorY = n.doubleValue();
            Object verObj = request.anchor().get("stateVersion");
            if (verObj instanceof Number n) anchorVersion = n.longValue();
        }

        String threadId = "thread_" + UUID.randomUUID().toString().substring(0, 12);
        WorkspaceCommentThreadEntity thread = new WorkspaceCommentThreadEntity(
                threadId, workspaceId, actorUserId, authorName, authorAvatar,
                anchorItemId, anchorX, anchorY, anchorVersion
        );

        // Add root comment as first reply
        String replyId = "rep_" + UUID.randomUUID().toString().substring(0, 12);
        WorkspaceCommentReplyEntity firstReply = new WorkspaceCommentReplyEntity(
                replyId, thread, actorUserId, authorName, authorAvatar, request.body()
        );
        thread.getReplies().add(firstReply);

        threadRepository.save(thread);

        WorkspaceCommentThreadDto dto = toDto(thread);
        broadcastCommentEvent(workspaceId, "THREAD_CREATED", dto);
        return dto;
    }

    @Transactional
    public WorkspaceCommentThreadDto addReply(String workspaceId, String threadId, String actorUserId, AddCommentReplyRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "COMMENT");
        WorkspaceCommentThreadEntity thread = threadRepository.findByIdAndWorkspaceId(threadId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thread not found: " + threadId));

        String authorName = "User " + actorUserId;
        String authorAvatar = null;
        Optional<User> uOpt = userRepository.findById(actorUserId);
        if (uOpt.isPresent()) {
            User u = uOpt.get();
            authorName = u.getUsername();
            authorAvatar = u.getAvatarUrl();
        }

        String replyId = "rep_" + UUID.randomUUID().toString().substring(0, 12);
        WorkspaceCommentReplyEntity reply = new WorkspaceCommentReplyEntity(
                replyId, thread, actorUserId, authorName, authorAvatar, request.body()
        );
        replyRepository.save(reply);

        thread.setUpdatedAt(Instant.now());
        thread.getReplies().add(reply);
        threadRepository.save(thread);

        WorkspaceCommentThreadDto dto = toDto(thread);
        broadcastCommentEvent(workspaceId, "REPLY_ADDED", dto);
        return dto;
    }

    @Transactional
    public WorkspaceCommentThreadDto updateStatus(String workspaceId, String threadId, String actorUserId, UpdateCommentThreadStatusRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "COMMENT");
        WorkspaceCommentThreadEntity thread = threadRepository.findByIdAndWorkspaceId(threadId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thread not found: " + threadId));

        thread.setStatus(request.status().toUpperCase());
        thread.setUpdatedAt(Instant.now());
        threadRepository.save(thread);

        WorkspaceCommentThreadDto dto = toDto(thread);
        broadcastCommentEvent(workspaceId, "STATUS_UPDATED", dto);
        return dto;
    }

    private void broadcastCommentEvent(String workspaceId, String eventType, WorkspaceCommentThreadDto thread) {
        try {
            messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId + "/comments", Map.of(
                    "type", eventType,
                    "thread", thread
            ));
        } catch (Exception ignored) {}
    }

    private WorkspaceCommentThreadDto toDto(WorkspaceCommentThreadEntity t) {
        Map<String, Object> anchor = new LinkedHashMap<>();
        if (t.getAnchorItemId() != null) anchor.put("itemId", t.getAnchorItemId());
        if (t.getAnchorPointX() != null) anchor.put("x", t.getAnchorPointX());
        if (t.getAnchorPointY() != null) anchor.put("y", t.getAnchorPointY());
        if (t.getAnchorVersion() != null) anchor.put("stateVersion", t.getAnchorVersion());

        List<WorkspaceCommentReplyDto> replies = t.getReplies().stream().map(r -> new WorkspaceCommentReplyDto(
                r.getId(),
                t.getId(),
                new WorkspaceCommentReplyDto.Author(r.getAuthorId(), r.getAuthorName(), r.getAuthorAvatar()),
                r.getBody(),
                r.getCreatedAt()
        )).toList();

        return new WorkspaceCommentThreadDto(
                t.getId(),
                t.getWorkspaceId(),
                new WorkspaceCommentThreadDto.Author(t.getAuthorId(), t.getAuthorName(), t.getAuthorAvatar()),
                anchor,
                t.getStatus(),
                replies,
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
