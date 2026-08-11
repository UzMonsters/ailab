package com.ailab.chemistry.grpc;

import com.ailab.chemistry.api.ChemistryInternalCalculationService;
import com.ailab.chemistry.api.grpc.v1.*;
import com.ailab.chemistry.domain.compound.CompoundErrorCode;
import com.ailab.chemistry.domain.compound.CompoundException;
import com.ailab.chemistry.domain.element.exception.ElementCatalogErrorCode;
import com.ailab.chemistry.domain.element.exception.ElementCatalogException;
import com.ailab.chemistry.domain.laboratorysafety.SafetyException;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionException;
import com.ailab.chemistry.domain.simulationstate.SimulationStateException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Service
public class ChemistryEngineGrpcService extends ChemistryEngineServiceGrpc.ChemistryEngineServiceImplBase {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ChemistryInternalCalculationService calculationService;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Autowired
    public ChemistryEngineGrpcService(ChemistryInternalCalculationService calculationService, ObjectMapper objectMapper) {
        this(calculationService, objectMapper, Clock.systemUTC());
    }

    ChemistryEngineGrpcService(ChemistryInternalCalculationService calculationService, ObjectMapper objectMapper, Clock clock) {
        this.calculationService = calculationService;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    public void runSimulation(RunSimulationRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.runSimulation(required(request.getWorkspaceId(), "workspace_id"),
                        jsonMap(request.getParametersJson())));
    }

    @Override
    public void validateReaction(ValidateReactionRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.validateReaction(request.getChemicalsList(), jsonMap(request.getCurrentStateJson())));
    }

    @Override
    public void calculateReaction(CalculateReactionRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.calculateReaction(request.getReactantsList(), jsonMap(request.getConditionsJson())));
    }

    @Override
    public void calculatePH(CalculatePHRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.calculatePH(required(request.getCompoundCode(), "compound_code"),
                        decimal(request.getConcentrationMolar(), "concentration_molar"),
                        decimal(request.getTemperatureK(), "temperature_k")));
    }

    @Override
    public void calculateTemperature(CalculateTemperatureRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.calculateTemperature(
                        decimal(request.getMassGrams(), "mass_grams"),
                        decimal(request.getHeatJoules(), "heat_joules"),
                        decimal(request.getHeatCapacity(), "heat_capacity")));
    }

    @Override
    public void calculatePressure(CalculatePressureRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.calculatePressure(
                        decimal(request.getMoles(), "moles"),
                        decimal(request.getVolumeLiters(), "volume_liters"),
                        decimal(request.getTemperatureK(), "temperature_k")));
    }

    @Override
    public void calculateEnergy(CalculateEnergyRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.calculateEnergy(request.getMaterialsList(),
                        decimal(request.getDeltaT(), "delta_t")));
    }

    @Override
    public void calculateConcentration(CalculateConcentrationRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.calculateConcentration(
                        decimal(request.getSoluteMoles(), "solute_moles"),
                        decimal(request.getVolumeLiters(), "volume_liters")));
    }

    @Override
    public void getChemicalProperties(GetChemicalPropertiesRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.getChemicalProperties(required(request.getChemicalIdentifier(), "chemical_identifier")));
    }

    @Override
    public void getElement(GetElementRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.getElement(required(request.getElementIdentifier(), "element_identifier")));
    }

    @Override
    public void getCompound(GetCompoundRequest request, StreamObserver<JsonResultResponse> responseObserver) {
        respond(request.getContext(), responseObserver,
                () -> calculationService.getCompound(required(request.getCompoundIdentifier(), "compound_identifier")));
    }

    private void respond(RequestContext context, StreamObserver<JsonResultResponse> observer, Callable<Object> action) {
        try {
            validateContext(context);
            Object result = action.call();
            observer.onNext(JsonResultResponse.newBuilder()
                    .setRequestId(context.getRequestId())
                    .setStatus("OK")
                    .setResultJson(objectMapper.writeValueAsString(result))
                    .build());
            observer.onCompleted();
        } catch (Exception ex) {
            observer.onError(toStatus(ex).asRuntimeException());
        }
    }

    private void validateContext(RequestContext context) {
        if (context == null || context.getRequestId().isBlank() || context.getActorId().isBlank()) {
            throw new IllegalArgumentException("request_id and actor_id are required");
        }
        if (!List.of("", "v1", "1").contains(context.getSchemaVersion())) {
            throw new IllegalArgumentException("Unsupported schema_version: " + context.getSchemaVersion());
        }
        long deadline = context.getDeadlineEpochMillis();
        if (deadline > 0 && deadline < clock.millis()) {
            throw new IllegalArgumentException("request deadline has expired");
        }
    }

    private Status toStatus(Exception ex) {
        if (ex instanceof CompoundException compound
                && compound.getErrorCode() == CompoundErrorCode.COMPOUND_NOT_FOUND) {
            return Status.NOT_FOUND.withDescription(ex.getMessage());
        }
        if (ex instanceof ElementCatalogException element
                && element.getErrorCode() == ElementCatalogErrorCode.ELEMENT_NOT_FOUND) {
            return Status.NOT_FOUND.withDescription(ex.getMessage());
        }
        if (ex instanceof SimulationExecutionException || ex instanceof SimulationStateException || ex instanceof SafetyException) {
            return Status.FAILED_PRECONDITION.withDescription(ex.getMessage());
        }
        if (ex instanceof IllegalArgumentException || ex instanceof NumberFormatException) {
            return Status.INVALID_ARGUMENT.withDescription(ex.getMessage());
        }
        return Status.INTERNAL.withDescription("Chemistry engine RPC failed");
    }

    private Map<String, Object> jsonMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Malformed JSON object", ex);
        }
    }

    private BigDecimal decimal(String value, String field) {
        return new BigDecimal(required(value, field));
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }
}
