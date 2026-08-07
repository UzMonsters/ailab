package com.ailab.chemistry.domain.simulationengine;

import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventType;
import com.ailab.chemistry.domain.laboratoryevent.ScientificOperationAppliedPayload;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulationEngine {
    public SimulationEngineDomainResult execute(SimulationExecutionPlan plan, SimulationCommand command) {
        validateCommand(command);
        SimulationOperationType operationType = command.operation().operationType();
        ConservationLedger ledger = operationType == SimulationOperationType.BOOKKEEPING_MIX
                ? ConservationLedger.notApplicable()
                : ConservationLedger.satisfiedFor(operationType);
        VesselStateDelta vesselDelta = new VesselStateDelta(
                command.targetVesselId(),
                command.materialDeltas(),
                command.inputs().getOrDefault("mixingNote", ""),
                decimalInput(command.inputs(), "targetTemperatureK"),
                decimalInput(command.inputs(), "finalPressureKPa"),
                decimalInput(command.inputs(), "finalVolumeMl"));
        SimulationStateDelta stateDelta = new SimulationStateDelta(List.of(vesselDelta), ledger);
        validate(stateDelta);

        String eventType = eventTypeFor(operationType).name();
        Map<String, String> resultValues = new LinkedHashMap<>(command.inputs());
        resultValues.put("eventType", eventType);
        SimulationCalculationTrace trace = new SimulationCalculationTrace(
                operationType.name(),
                new SimulationCalculationInput(command.inputs()),
                new SimulationCalculationResult(resultValues),
                command.inputs().getOrDefault("solverStatus", "SUCCESS"),
                command.inputs().containsKey("iterationCount") ? Integer.parseInt(command.inputs().get("iterationCount")) : 1,
                operationType == SimulationOperationType.KINETIC_PROGRESS ? List.of("t=1.0s", "endpoint") : List.of(),
                Map.of("defaultTolerance", "1E-12"));

        String inputHash = sha256(canonical(command));
        String resultHash = sha256(canonical(stateDelta) + canonical(trace));
        ScientificOperationAppliedPayload payload = new ScientificOperationAppliedPayload(
                eventTypeFor(operationType),
                operationType.name(),
                command.commandId().value(),
                commandFingerprint(command),
                plan.processCode(),
                plan.processVersion(),
                command.stepId(),
                command.operation().modelSelection().calculationMethod(),
                command.operation().modelSelection().reactionOrProfileIdentifier(),
                command.operation().modelSelection().model(),
                command.operation().modelSelection().datasets(),
                command.operation().modelSelection().assumptions(),
                trace,
                stateDelta,
                ledger,
                inputHash,
                resultHash,
                1,
                1);
        return new SimulationEngineDomainResult(SimulationExecutionStatus.APPLIED, payload);
    }

    public void validate(SimulationStateDelta delta) {
        for (ConservationResidual residual : delta.conservationLedger().residuals().values()) {
            if (residual.status() == ConservationStatus.FAILED) {
                throw invariant("Conservation ledger contains failed residual");
            }
        }
        for (VesselStateDelta vesselDelta : delta.vesselDeltas()) {
            if (vesselDelta.finalTemperatureKelvin() != null
                    && vesselDelta.finalTemperatureKelvin().compareTo(BigDecimal.ZERO) <= 0) {
                throw invariant("Temperature must remain above absolute zero");
            }
            if (vesselDelta.finalPressureKpa() != null
                    && vesselDelta.finalPressureKpa().compareTo(BigDecimal.ZERO) < 0) {
                throw invariant("Pressure must not become negative");
            }
            if (vesselDelta.finalVolumeMl() != null
                    && vesselDelta.finalVolumeMl().compareTo(BigDecimal.ZERO) < 0) {
                throw invariant("Volume must not become negative");
            }
        }
    }

    public String commandFingerprint(SimulationCommand command) {
        validateCommand(command);
        return sha256(canonical(command));
    }

    public LaboratoryEventType eventTypeFor(SimulationOperationType operationType) {
        return switch (operationType) {
            case STOICHIOMETRIC_REACTION -> LaboratoryEventType.STOICHIOMETRIC_REACTION_APPLIED;
            case EQUILIBRIUM_REACTION -> LaboratoryEventType.EQUILIBRIUM_REACTION_APPLIED;
            case KINETIC_PROGRESS -> LaboratoryEventType.KINETIC_PROGRESS_APPLIED;
            case THERMAL_OPERATION -> LaboratoryEventType.THERMAL_OPERATION_APPLIED;
            case GAS_STATE_CHANGE -> LaboratoryEventType.GAS_STATE_CHANGED;
            case PHASE_TRANSITION -> LaboratoryEventType.PHASE_TRANSITION_APPLIED;
            case ELECTROLYSIS -> LaboratoryEventType.ELECTROLYSIS_APPLIED;
            case BOOKKEEPING_MIX -> LaboratoryEventType.BOOKKEEPING_MIX_APPLIED;
        };
    }

    private void validateCommand(SimulationCommand command) {
        if (command == null || command.operation() == null || command.operation().operationType() == null) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.EXPLICIT_OPERATION_REQUIRED,
                    "Each command must specify exactly one explicit operation type");
        }
        if (command.operation().modelSelection().calculationMethod().startsWith("unsupported")) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.UNSUPPORTED_MODEL_SELECTION,
                    "Unsupported scientific model selection: "
                            + command.operation().modelSelection().calculationMethod());
        }
        if (!"SUCCESS".equals(command.inputs().getOrDefault("solverStatus", "SUCCESS"))) {
            throw new SimulationExecutionException(SimulationExecutionErrorCode.STATE_DELTA_INVARIANT_FAILED,
                    "Only successful solver results may become state deltas");
        }
    }

    private BigDecimal decimalInput(Map<String, String> inputs, String key) {
        String value = inputs.get(key);
        return value == null || value.isBlank() ? null : new BigDecimal(value);
    }

    private SimulationExecutionException invariant(String message) {
        return new SimulationExecutionException(SimulationExecutionErrorCode.STATE_DELTA_INVARIANT_FAILED, message);
    }

    private String canonical(Object value) {
        if (value instanceof SimulationCommand command) {
            return "command:%s:%s:%s:%s:%s:%s".formatted(
                    command.commandId().value(),
                    command.stepId(),
                    command.targetVesselId(),
                    canonical(command.operation()),
                    canonicalMap(command.inputs()),
                    command.materialDeltas().stream().map(this::canonical).collect(Collectors.joining("|")));
        }
        if (value instanceof ScientificOperationSpecification spec) {
            return "spec:%s:%s".formatted(spec.operationType(), canonical(spec.modelSelection()));
        }
        if (value instanceof ScientificModelSelection selection) {
            return "model:%s:%s:%s:%s:%s".formatted(
                    selection.calculationMethod(),
                    selection.reactionOrProfileIdentifier(),
                    selection.model().identifier() + "@" + selection.model().version(),
                    selection.datasets().stream()
                            .sorted(Comparator.comparing(ScientificDatasetReference::name))
                            .map(dataset -> dataset.name() + "@" + dataset.version())
                            .collect(Collectors.joining(",")),
                    canonicalMap(selection.assumptions()));
        }
        if (value instanceof MaterialStateDelta delta) {
            return "%s:%s:%s:%s:%s".formatted(delta.vesselId(), delta.compoundCode(),
                    delta.quantityDelta().stripTrailingZeros().toPlainString(), delta.unit(), delta.physicalState());
        }
        if (value instanceof SimulationStateDelta delta) {
            return delta.vesselDeltas().stream().map(this::canonical).collect(Collectors.joining("|"))
                    + canonical(delta.conservationLedger());
        }
        if (value instanceof VesselStateDelta delta) {
            return "%s:%s:%s:%s:%s:%s".formatted(delta.vesselId(),
                    delta.materialDeltas().stream().map(this::canonical).collect(Collectors.joining(",")),
                    delta.mixingNote(),
                    decimal(delta.finalTemperatureKelvin()),
                    decimal(delta.finalPressureKpa()),
                    decimal(delta.finalVolumeMl()));
        }
        if (value instanceof ConservationLedger ledger) {
            return ledger.residuals().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + ":" + entry.getValue().status())
                    .collect(Collectors.joining(","));
        }
        if (value instanceof SimulationCalculationTrace trace) {
            return "%s:%s:%s:%s:%d:%s".formatted(trace.selectedHandler(),
                    canonicalMap(trace.input().values()),
                    canonicalMap(trace.result().values()),
                    trace.solverStatus(),
                    trace.iterationCount(),
                    String.join(",", trace.intermediatePoints()));
        }
        return String.valueOf(value);
    }

    private String canonicalMap(Map<String, String> values) {
        return values.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));
    }

    private String decimal(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
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
