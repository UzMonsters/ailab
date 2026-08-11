package com.ailab.workspace.websocket;

import com.ailab.chemistry.api.SimulationEngineService;
import com.ailab.chemistry.domain.simulationengine.SimulationCommand;
import com.ailab.chemistry.domain.simulationengine.SimulationCommandId;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionResult;
import com.ailab.chemistry.domain.simulationengine.SimulationOperationType;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.workspace.dto.RealtimeError;
import com.ailab.workspace.dto.SandboxEventCommand;
import com.ailab.workspace.dto.WorkspaceEventAck;
import com.ailab.workspace.exception.VersionConflictException;
import com.ailab.workspace.service.LaboratoryAccessService;
import com.ailab.workspace.service.WorkspaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Controller
public class WorkspaceRealtimeController {

    private final WorkspaceService workspaceService;
    private final SimulationEngineService engineService;
    private final LaboratoryAccessService accessService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public WorkspaceRealtimeController(
            WorkspaceService workspaceService,
            SimulationEngineService engineService,
            LaboratoryAccessService accessService,
            SimpMessagingTemplate messagingTemplate,
            ObjectMapper objectMapper) {
        this.workspaceService = workspaceService;
        this.engineService = engineService;
        this.accessService = accessService;
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

            // Broadcast to workspace subscribers
            messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId, ack);
        } catch (VersionConflictException vce) {
            RealtimeError err = new RealtimeError(
                    "VERSION_CONFLICT",
                    vce.getMessage(),
                    command.clientEventId(),
                    vce.getExpectedVersion(),
                    vce.getActualVersion()
            );
            messagingTemplate.convertAndSendToUser(userId, "/queue/errors", err);
        } catch (Exception e) {
            RealtimeError err = new RealtimeError(
                    "EVENT_ERROR",
                    e.getMessage(),
                    command.clientEventId(),
                    command.expectedVersion(),
                    null
            );
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
            messagingTemplate.convertAndSendToUser(userId, "/queue/errors", err);
        }
    }

    @MessageMapping("/workspaces/{workspaceId}/presence")
    public void handlePresence(
            @DestinationVariable String workspaceId,
            @Payload Map<String, Object> presenceData,
            Principal principal) {
        String userId = requireUser(principal);
        accessService.verifyWorkspaceAccess(workspaceId, userId);
        Map<String, Object> event = Map.of(
                "userId", userId,
                "status", presenceData.getOrDefault("status", "ONLINE"),
                "at", Instant.now().toString()
        );
        messagingTemplate.convertAndSend("/topic/workspaces/" + workspaceId + "/presence", event);
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
                new com.ailab.chemistry.domain.simulationengine.ScientificOperationSpecification(
                        operationType,
                        new com.ailab.chemistry.domain.simulationengine.ScientificModelSelection(
                                stringValue(commandMap.getOrDefault("calculationMethod", "DEFAULT")),
                                stringValue(commandMap.getOrDefault("reactionOrProfileIdentifier", "workspace-command")),
                                new com.ailab.chemistry.domain.simulationengine.ScientificModelReference("workspace-ws", "1.0"),
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
