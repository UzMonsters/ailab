package com.ailab.workspace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record WorkspaceEventAck(
        @JsonProperty("clientEventId") String clientEventId,
        @JsonProperty("operationId") String operationId,
        @JsonProperty("eventId") String eventId,
        @JsonProperty("eventType") String eventType,
        @JsonProperty("workspaceId") String workspaceId,
        @JsonProperty("sessionId") String sessionId,
        @JsonProperty("accepted") boolean accepted,
        @JsonProperty("stateVersion") long stateVersion,
        @JsonProperty("newVersion") long newVersion,
        @JsonProperty("stateDelta") Map<String, Object> stateDelta,
        @JsonProperty("measurements") List<MeasurementPointDto> measurements,
        @JsonProperty("safetyWarnings") List<String> safetyWarnings,
        @JsonProperty("checkpointFacts") List<CheckpointFactDto> checkpointFacts,
        @JsonProperty("occurredAt") String occurredAt
) {
    public WorkspaceEventAck(
            String clientEventId,
            String eventId,
            String eventType,
            String workspaceId,
            String sessionId,
            long stateVersion,
            Map<String, Object> stateDelta,
            List<String> safetyWarnings,
            String occurredAt
    ) {
        this(
                clientEventId,
                clientEventId,
                eventId,
                eventType,
                workspaceId,
                sessionId,
                true,
                stateVersion,
                stateVersion,
                stateDelta,
                List.of(),
                safetyWarnings != null ? safetyWarnings : List.of(),
                List.of(),
                occurredAt
        );
    }
}
