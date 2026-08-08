package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventPayload;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventType;
import com.ailab.chemistry.domain.laboratoryevent.MaterialDispensedPayload;
import com.ailab.chemistry.domain.laboratoryevent.MaterialMixedPayload;
import com.ailab.chemistry.domain.laboratoryevent.MaterialTransferredPayload;
import com.ailab.chemistry.domain.laboratoryevent.SampleTakenPayload;
import com.ailab.chemistry.domain.laboratoryevent.ScientificOperationAppliedPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionCreatedPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionLifecyclePayload;
import com.ailab.chemistry.domain.laboratoryevent.StepCompletedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepFailedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepSkippedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepStartedPayload;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class LaboratoryEventCodec {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public String payloadJson(LaboratoryEventPayload payload) {
        return write(payload);
    }

    public LaboratoryEventPayload payloadFromJson(LaboratoryEventType type, String json) {
        try {
            return switch (type) {
                case SESSION_CREATED -> mapper.readValue(json, SessionCreatedPayload.class);
                case SESSION_STARTED, SESSION_PAUSED, SESSION_RESUMED, SESSION_COMPLETED, SESSION_CANCELLED, SESSION_FAILED ->
                        mapper.readValue(json, SessionLifecyclePayload.class);
                case STEP_STARTED -> mapper.readValue(json, StepStartedPayload.class);
                case STEP_COMPLETED -> mapper.readValue(json, StepCompletedPayload.class);
                case STEP_FAILED -> mapper.readValue(json, StepFailedPayload.class);
                case STEP_SKIPPED -> mapper.readValue(json, StepSkippedPayload.class);
                case MATERIAL_DISPENSED -> mapper.readValue(json, MaterialDispensedPayload.class);
                case MATERIAL_TRANSFERRED, MATERIAL_ADDED -> mapper.readValue(json, MaterialTransferredPayload.class);
                case SAMPLE_TAKEN -> mapper.readValue(json, SampleTakenPayload.class);
                case MATERIAL_MIXED -> mapper.readValue(json, MaterialMixedPayload.class);
                case STOICHIOMETRIC_REACTION_APPLIED, EQUILIBRIUM_REACTION_APPLIED, KINETIC_PROGRESS_APPLIED,
                     THERMAL_OPERATION_APPLIED, GAS_STATE_CHANGED, PHASE_TRANSITION_APPLIED,
                     ELECTROLYSIS_APPLIED, BOOKKEEPING_MIX_APPLIED ->
                        mapper.readValue(json, ScientificOperationAppliedPayload.class);
                case PROCESS_ASSIGNED, EQUIPMENT_ALLOCATED, EQUIPMENT_RELEASED, ENVIRONMENT_UPDATED ->
                        mapper.readValue(json, SessionLifecyclePayload.class);
            };
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not read event payload JSON", ex);
        }
    }

    public String stateJson(SimulationState state) {
        return write(state);
    }

    public SimulationState stateFromJson(String json) {
        try {
            return mapper.readValue(json, SimulationState.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not read simulation state JSON", ex);
        }
    }

    public String fingerprint(LaboratoryEventPayload payload) {
        return sha256(payloadJson(payload));
    }

    public String checksum(SimulationState state) {
        return sha256(stateJson(state));
    }

    private String write(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not write JSON", ex);
        }
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
}
