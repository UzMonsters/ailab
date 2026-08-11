package com.ailab.chemistry.grpc;

import com.ailab.chemistry.api.ChemistryInternalCalculationService;
import com.ailab.chemistry.api.grpc.v1.*;
import com.ailab.chemistry.domain.compound.CompoundErrorCode;
import com.ailab.chemistry.domain.compound.CompoundException;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionErrorCode;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ChemistryEngineGrpcServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ChemistryInternalCalculationService calculationService;
    private Server server;
    private ManagedChannel channel;
    private ChemistryEngineServiceGrpc.ChemistryEngineServiceBlockingStub stub;

    @BeforeEach
    void setUp() throws Exception {
        calculationService = mock(ChemistryInternalCalculationService.class);
        String serverName = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new ChemistryEngineGrpcService(calculationService, objectMapper))
                .build()
                .start();
        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        stub = ChemistryEngineServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        if (channel != null) {
            channel.shutdownNow();
        }
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void calculateConcentrationDelegatesAndSerializesResult() throws Exception {
        when(calculationService.calculateConcentration(new BigDecimal("0.25"), new BigDecimal("0.5")))
                .thenReturn(Map.of("molarity", 0.5, "unit", "M"));

        JsonResultResponse response = stub.calculateConcentration(CalculateConcentrationRequest.newBuilder()
                .setContext(context())
                .setSoluteMoles("0.25")
                .setVolumeLiters("0.5")
                .build());

        JsonNode json = objectMapper.readTree(response.getResultJson());
        assertThat(response.getRequestId()).isEqualTo("rpc-1");
        assertThat(response.getStatus()).isEqualTo("OK");
        assertThat(json.get("molarity").asDouble()).isEqualTo(0.5);
        verify(calculationService).calculateConcentration(new BigDecimal("0.25"), new BigDecimal("0.5"));
    }

    @Test
    void missingRequestContextIsInvalidArgument() {
        assertThatThrownBy(() -> stub.calculateConcentration(CalculateConcentrationRequest.newBuilder()
                .setSoluteMoles("0.25")
                .setVolumeLiters("0.5")
                .build()))
                .isInstanceOfSatisfying(StatusRuntimeException.class, ex ->
                        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT));
    }

    @Test
    void compoundCatalogMissMapsToNotFound() {
        when(calculationService.getCompound("COMP-MISSING"))
                .thenThrow(new CompoundException(CompoundErrorCode.COMPOUND_NOT_FOUND, "compound missing"));

        assertThatThrownBy(() -> stub.getCompound(GetCompoundRequest.newBuilder()
                .setContext(context())
                .setCompoundIdentifier("COMP-MISSING")
                .build()))
                .isInstanceOfSatisfying(StatusRuntimeException.class, ex ->
                        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND));
    }

    @Test
    void simulationPreconditionFailureMapsToFailedPrecondition() {
        when(calculationService.runSimulation(eq("workspace-1"), anyMap()))
                .thenThrow(new SimulationExecutionException(SimulationExecutionErrorCode.SUITABILITY_REJECTED, "unsafe setup"));

        assertThatThrownBy(() -> stub.runSimulation(RunSimulationRequest.newBuilder()
                .setContext(context())
                .setWorkspaceId("workspace-1")
                .setParametersJson("{}")
                .build()))
                .isInstanceOfSatisfying(StatusRuntimeException.class, ex ->
                        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.FAILED_PRECONDITION));
    }

    private RequestContext context() {
        return RequestContext.newBuilder()
                .setRequestId("rpc-1")
                .setActorId("user-1")
                .setSchemaVersion("v1")
                .build();
    }
}
