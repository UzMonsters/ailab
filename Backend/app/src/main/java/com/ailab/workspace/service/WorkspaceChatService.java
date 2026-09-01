package com.ailab.workspace.service;

import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.domain.WorkspaceChatMessageEntity;
import com.ailab.workspace.domain.WorkspaceChatReadEntity;
import com.ailab.workspace.dto.ChatPageResponse;
import com.ailab.workspace.dto.SendChatMessageRequest;
import com.ailab.workspace.dto.UpdateChatMessageRequest;
import com.ailab.workspace.dto.WorkspaceChatMessageDto;
import com.ailab.workspace.repository.WorkspaceChatMessageRepository;
import com.ailab.workspace.repository.WorkspaceChatReadRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
public class WorkspaceChatService {

    private final WorkspaceChatMessageRepository chatMessageRepository;
    private final WorkspaceChatReadRepository chatReadRepository;
    private final WorkspaceMemberService memberService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WorkspaceChatService(
            WorkspaceChatMessageRepository chatMessageRepository,
            WorkspaceChatReadRepository chatReadRepository,
            WorkspaceMemberService memberService,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatReadRepository = chatReadRepository;
        this.memberService = memberService;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public ChatPageResponse listMessages(String workspaceId, String actorUserId, Instant beforeTimestamp, int limit) {
        memberService.requirePermission(workspaceId, actorUserId, "CHAT");
        int safeLimit = Math.max(1, Math.min(limit <= 0 ? 50 : limit, 100));
        PageRequest page = PageRequest.of(0, safeLimit);

        List<WorkspaceChatMessageEntity> list;
        if (beforeTimestamp != null) {
            list = chatMessageRepository.findMessagesBefore(workspaceId, beforeTimestamp, page);
        } else {
            list = chatMessageRepository.findLatestMessages(workspaceId, page);
        }

        // Unread calculation
        Instant lastRead = chatReadRepository.findByWorkspaceIdAndUserId(workspaceId, actorUserId)
                .map(WorkspaceChatReadEntity::getLastReadAt)
                .orElse(Instant.EPOCH);
        long unread = chatMessageRepository.countUnreadMessages(workspaceId, lastRead);

        List<WorkspaceChatMessageDto> dtos = list.stream().map(this::toDto).toList();
        String nextCursor = list.size() == safeLimit ? list.get(list.size() - 1).getCreatedAt().toString() : null;

        return new ChatPageResponse(dtos, nextCursor, unread);
    }

    @Transactional
    public WorkspaceChatMessageDto sendMessage(String workspaceId, String actorUserId, SendChatMessageRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "CHAT");

        String clientMessageId = request.clientMessageId() != null ? request.clientMessageId() : UUID.randomUUID().toString();

        // Idempotency check
        Optional<WorkspaceChatMessageEntity> existing = chatMessageRepository.findByWorkspaceIdAndClientMessageId(workspaceId, clientMessageId);
        if (existing.isPresent()) {
            return toDto(existing.get());
        }

        String authorName = "User " + actorUserId;
        String authorAvatar = null;
        Optional<User> uOpt = userRepository.findById(actorUserId);
        if (uOpt.isPresent()) {
            User u = uOpt.get();
            authorName = u.getUsername();
            authorAvatar = u.getAvatarUrl();
        }

        String anchorItemId = null;
        Long anchorVersion = null;
        if (request.anchor() != null) {
            Object itemIdObj = request.anchor().get("itemId");
            if (itemIdObj != null) anchorItemId = itemIdObj.toString();
            Object verObj = request.anchor().get("stateVersion");
            if (verObj instanceof Number n) anchorVersion = n.longValue();
        }

        String id = "msg_" + UUID.randomUUID().toString().substring(0, 12);
        WorkspaceChatMessageEntity msg = new WorkspaceChatMessageEntity(
                id, clientMessageId, workspaceId, actorUserId, authorName, authorAvatar,
                request.body(), request.replyToMessageId(), anchorItemId, anchorVersion
        );
        chatMessageRepository.save(msg);

        WorkspaceChatMessageDto dto = toDto(msg);
        broadcastChatMessage(workspaceId, "CREATED", dto);

        return dto;
    }

    @Transactional
    public WorkspaceChatMessageDto updateMessage(String workspaceId, String actorUserId, String messageId, UpdateChatMessageRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "CHAT");
        WorkspaceChatMessageEntity msg = chatMessageRepository.findByIdAndWorkspaceId(messageId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found: " + messageId));

        if (!msg.getAuthorId().equals(actorUserId)) {
            memberService.requirePermission(workspaceId, actorUserId, "MANAGE_WORKSPACE");
        }

        msg.setBody(request.body());
        msg.setEditedAt(Instant.now());
        chatMessageRepository.save(msg);

        WorkspaceChatMessageDto dto = toDto(msg);
        broadcastChatMessage(workspaceId, "UPDATED", dto);
        return dto;
    }

    @Transactional
    public void deleteMessage(String workspaceId, String actorUserId, String messageId) {
        memberService.requirePermission(workspaceId, actorUserId, "CHAT");
        WorkspaceChatMessageEntity msg = chatMessageRepository.findByIdAndWorkspaceId(messageId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found: " + messageId));

        if (!msg.getAuthorId().equals(actorUserId)) {
            memberService.requirePermission(workspaceId, actorUserId, "MANAGE_WORKSPACE");
        }

        msg.setDeleted(true);
        msg.setBody("[Message deleted]");
        chatMessageRepository.save(msg);

        WorkspaceChatMessageDto dto = toDto(msg);
        broadcastChatMessage(workspaceId, "DELETED", dto);
    }

    @Transactional
    public void markRead(String workspaceId, String actorUserId, String messageId) {
        memberService.requirePermission(workspaceId, actorUserId, "CHAT");
        WorkspaceChatReadEntity read = chatReadRepository.findByWorkspaceIdAndUserId(workspaceId, actorUserId)
                .orElseGet(() -> new WorkspaceChatReadEntity(workspaceId, actorUserId, messageId));
        read.setLastReadMessageId(messageId);
        read.setLastReadAt(Instant.now());
        chatReadRepository.save(read);
    }

    private void broadcastChatMessage(String workspaceId, String eventType, WorkspaceChatMessageDto msg) {
        try {
            messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId + "/chat", Map.of(
                    "type", eventType,
                    "message", msg
            ));
        } catch (Exception ignored) {}
    }

    private WorkspaceChatMessageDto toDto(WorkspaceChatMessageEntity m) {
        Map<String, Object> anchor = null;
        if (m.getAnchorItemId() != null || m.getAnchorVersion() != null) {
            anchor = new LinkedHashMap<>();
            if (m.getAnchorItemId() != null) anchor.put("itemId", m.getAnchorItemId());
            if (m.getAnchorVersion() != null) anchor.put("stateVersion", m.getAnchorVersion());
        }
        return new WorkspaceChatMessageDto(
                m.getId(),
                m.getClientMessageId(),
                m.getWorkspaceId(),
                new WorkspaceChatMessageDto.Author(m.getAuthorId(), m.getAuthorName(), m.getAuthorAvatar()),
                m.getBody(),
                m.getReplyToId(),
                anchor,
                m.getCreatedAt(),
                m.getEditedAt(),
                m.isDeleted() ? m.getCreatedAt() : null
        );
    }
}
