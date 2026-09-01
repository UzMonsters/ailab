package com.ailab.workspace.websocket;

import com.ailab.chemistry.api.SimulationEngineService;
import com.ailab.chemistry.domain.simulationengine.*;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.exception.VersionConflictException;
import com.ailab.workspace.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.*;

@Controller
public class WorkspaceRealtimeController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMemberService memberService;
    private final WorkspaceChatService chatService;
    private final WorkspaceCommentService commentService;
    private final SimulationEngineService engineService;
    private final LaboratoryAccessService accessService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private static final List<String> USER_COLORS = List.of(
            "#3B82F6", "#10B981", "#F59E0B", "#EF4444", "#8B5CF6", "#EC4899", "#06B6D4", "#F97316"
    );

    public WorkspaceRealtimeController(
            WorkspaceService workspaceService,
            WorkspaceMemberService memberService,
            WorkspaceChatService chatService,
            WorkspaceCommentService commentService,
            SimulationEngineService engineService,
            LaboratoryAccessService accessService,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate,
            ObjectMapper objectMapper) {
        this.workspaceService = workspaceService;
        this.memberService = memberService;
        this.chatService = chatService;
        this.commentService = commentService;
        this.engineService = engineService;
        this.accessService = accessService;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/workspaces/{workspaceId}/events")
    public void handleWorkspaceEvent(
            @DestinationVariable String workspaceId,
            @Payload SandboxEventCommand command,
            Principal principal) {
        String userId = requireUser(principal);
        try {
            WorkspaceEventAck ack = workspaceService.appendEvent(workspaceId, userId, command);

            // Ack to originating client
            messagingTemplate.convertAndSendToUser(userId, "/queue/acks", ack);

            // Broadcast to workspace subscribers (both paths for backward compatibility)
            messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId + "/events", ack);
            messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId, ack);
        } catch (VersionConflictException vce) {
            RealtimeError err = new RealtimeError(
                    "VERSION_CONFLICT",
                    vce.getMessage(),
                    command.clientEventId(),
                    vce.getExpectedVersion(),
                    vce.getActualVersion()
            );
            messagingTemplate.convertAndSendToUser(userId, "/queue/workspace-errors", err);
            messagingTemplate.convertAndSendToUser(userId, "/queue/errors", err);
        } catch (Exception e) {
            RealtimeError err = new RealtimeError(
                    "EVENT_ERROR",
                    e.getMessage(),
                    command.clientEventId(),
                    command.expectedVersion(),
                    null
            );
            messagingTemplate.convertAndSendToUser(userId, "/queue/workspace-errors", err);
            messagingTemplate.convertAndSendToUser(userId, "/queue/errors", err);
        }
    }

    @MessageMapping("/workspaces/{workspaceId}/presence")
    public void handlePresence(
            @DestinationVariable String workspaceId,
            @Payload Map<String, Object> presenceData,
            Principal principal) {
        String userId = requireUser(principal);

        String displayName = "User " + userId.substring(0, Math.min(6, userId.length()));
        String avatar = null;
        Optional<User> uOpt = userRepository.findById(userId);
        if (uOpt.isPresent()) {
            User u = uOpt.get();
            displayName = u.getUsername();
            avatar = u.getAvatarUrl();
        }

        int colorIdx = Math.abs(userId.hashCode()) % USER_COLORS.size();
        String assignedColor = USER_COLORS.get(colorIdx);

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("userId", userId);
        event.put("displayName", displayName);
        event.put("color", assignedColor);
        event.put("type", presenceData.getOrDefault("type", "HEARTBEAT"));
        event.put("status", presenceData.getOrDefault("status", "ONLINE"));
        event.put("cursor", presenceData.get("cursor"));
        event.put("selectedItemIds", presenceData.get("selectedItemIds"));
        event.put("at", Instant.now().toString());

        messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId + "/presence", event);
    }

    @MessageMapping("/workspaces/{workspaceId}/chat")
    public void handleChat(
            @DestinationVariable String workspaceId,
            @Payload SendChatMessageRequest request,
            Principal principal) {
        String userId = requireUser(principal);
        try {
            chatService.sendMessage(workspaceId, userId, request);
        } catch (Exception e) {
            RealtimeError err = new RealtimeError("CHAT_ERROR", e.getMessage(), request.clientMessageId(), null, null);
            messagingTemplate.convertAndSendToUser(userId, "/queue/workspace-errors", err);
            messagingTemplate.convertAndSendToUser(userId, "/queue/errors", err);
        }
    }

    @MessageMapping("/workspaces/{workspaceId}/comments")
    public void handleComment(
            @DestinationVariable String workspaceId,
            @Payload CreateCommentThreadRequest request,
            Principal principal) {
        String userId = requireUser(principal);
        try {
            commentService.createThread(workspaceId, userId, request);
        } catch (Exception e) {
            RealtimeError err = new RealtimeError("COMMENT_ERROR", e.getMessage(), null, null, null);
            messagingTemplate.convertAndSendToUser(userId, "/queue/workspace-errors", err);
            messagingTemplate.convertAndSendToUser(userId, "/queue/errors", err);
        }
    }

    @MessageMapping("/experiments/{sessionId}/commands")
    public void handleExperimentCommand(
            @DestinationVariable String sessionId,
            @Payload Map<String, Object> commandRequest,
            Principal principal) {
        String userId = requireUser(principal);
        try {
            accessService.verifyExperimentAccess(sessionId, userId);
            Long expectedVersion = ((Number) commandRequest.getOrDefault("expectedStateVersion", 0)).longValue();
            String commandIdStr = (String) commandRequest.getOrDefault("commandId", "cmd-" + System.currentTimeMillis());
            String idempotency = (String) commandRequest.getOrDefault("idempotencyKey", commandIdStr);

            SimulationCommand command = commandFrom(commandRequest, commandIdStr);

            SimulationExecutionResult result = engineService.execute(
                    new SimulationSessionId(sessionId),
                    expectedVersion,
                    new com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey(idempotency),
                    command
            );

            Map<String, Object> ack = Map.of(
                    "commandId", commandIdStr,
                    "sessionId", sessionId,
                    "status", result.status().name(),
                    "stateVersion", result.state().version().value(),
                    "occurredAt", Instant.now().toString()
            );

            messagingTemplate.convertAndSendToUser(userId, "/queue/acks", ack);
            messagingTemplate.convertAndSend("/topic/experiments/" + sessionId, result.payload());
        } catch (Exception e) {
            RealtimeError err = new RealtimeError(
                    "SIMULATION_ERROR",
                    e.getMessage(),
                    null,
                    null,
                    null
            );
            messagingTemplate.convertAndSendToUser(userId, "/queue/workspace-errors", err);
            messagingTemplate.convertAndSendToUser(userId, "/queue/errors", err);
        }
    }

    private String requireUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException("Authenticated STOMP principal required");
        }
        return principal.getName();
    }

    @SuppressWarnings("unchecked")
    private SimulationCommand commandFrom(Map<String, Object> request, String commandId) {
        Object rawCommand = request.get("command");
        if (rawCommand instanceof Map<?, ?> commandMap && commandMap.containsKey("operation")) {
            return objectMapper.convertValue(rawCommand, SimulationCommand.class);
        }

        Map<String, Object> commandMap = rawCommand instanceof Map<?, ?> map ? (Map<String, Object>) map : request;
        String commandType = stringValue(commandMap.getOrDefault("commandType", commandMap.getOrDefault("type", commandMap.get("command"))));
        SimulationOperationType operationType = mapOperation(commandType);
        String stepId = stringValue(commandMap.getOrDefault("stepId", "step-1"));
        String targetVesselId = stringValue(commandMap.getOrDefault("targetVesselId", commandMap.getOrDefault("vesselId", "vessel-1")));

        Map<String, String> inputs = objectMapper.convertValue(
                commandMap.getOrDefault("inputs", Map.of()),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));

        return new SimulationCommand(
                new SimulationCommandId(commandId),
                stepId,
                targetVesselId,
                new ScientificOperationSpecification(
                        operationType,
                        new ScientificModelSelection(
                                stringValue(commandMap.getOrDefault("calculationMethod", "DEFAULT")),
                                stringValue(commandMap.getOrDefault("reactionOrProfileIdentifier", "workspace-command")),
                                new ScientificModelReference("workspace-ws", "1.0"),
                                List.of(),
                                Map.of("source", "websocket")
                        )
                ),
                inputs,
                List.of()
        );
    }

    private SimulationOperationType mapOperation(String commandType) {
        if (commandType == null || commandType.isBlank()) {
            throw new IllegalArgumentException("Experiment command type is required");
        }
        return switch (commandType.trim().toUpperCase()) {
            case "HEAT", "COOL", "EVAPORATE" -> SimulationOperationType.THERMAL_OPERATION;
            case "FREEZE", "BOIL" -> SimulationOperationType.PHASE_TRANSITION;
            case "POUR", "TRANSFER", "MIX", "STIR", "WASH", "DRY", "STOP" -> SimulationOperationType.BOOKKEEPING_MIX;
            default -> SimulationOperationType.valueOf(commandType.trim().toUpperCase());
        };
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString();
    }
}
