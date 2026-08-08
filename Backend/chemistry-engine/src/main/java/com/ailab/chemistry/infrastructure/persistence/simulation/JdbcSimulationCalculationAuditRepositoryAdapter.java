package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.simulationengine.ConservationLedger;
import com.ailab.chemistry.domain.simulationengine.ScientificDatasetReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelReference;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAudit;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAuditRepository;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationTrace;
import com.ailab.chemistry.domain.simulationengine.SimulationOperationType;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("local | prod | migration-test")
public class JdbcSimulationCalculationAuditRepositoryAdapter implements SimulationCalculationAuditRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public JdbcSimulationCalculationAuditRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SimulationCalculationAudit save(SimulationCalculationAudit audit) {
        jdbcTemplate.update("""
                INSERT INTO chemistry.simulation_calculation_audits
                (event_id, session_id, command_id, operation_type, model_identifier, model_version,
                 dataset_versions, input_hash, result_hash, calculation_trace, conservation_ledger, created_at)
                VALUES (?, ?, ?, ?, ?, ?, CAST(? AS jsonb), ?, ?, CAST(? AS jsonb), CAST(? AS jsonb), ?)
                """,
                audit.eventId().value(),
                audit.sessionId().value(),
                audit.commandId(),
                audit.operationType().name(),
                audit.model().identifier(),
                audit.model().version(),
                write(audit.datasetVersions()),
                audit.inputHash(),
                audit.resultHash(),
                write(audit.calculationTrace()),
                write(audit.conservationLedger()),
                Timestamp.from(audit.createdAt()));
        return audit;
    }

    @Override
    public Optional<SimulationCalculationAudit> find(SimulationSessionId sessionId, LaboratoryEventId eventId) {
        return jdbcTemplate.query("""
                SELECT event_id, session_id, command_id, operation_type, model_identifier, model_version,
                       dataset_versions::text AS dataset_versions, input_hash, result_hash,
                       calculation_trace::text AS calculation_trace, conservation_ledger::text AS conservation_ledger,
                       created_at
                FROM chemistry.simulation_calculation_audits
                WHERE session_id = ? AND event_id = ?
                """, (rs, rowNum) -> map(rs), sessionId.value(), eventId.value()).stream().findFirst();
    }

    private SimulationCalculationAudit map(ResultSet rs) throws SQLException {
        return new SimulationCalculationAudit(
                new LaboratoryEventId(rs.getString("event_id")),
                new SimulationSessionId(rs.getString("session_id")),
                rs.getString("command_id"),
                SimulationOperationType.valueOf(rs.getString("operation_type")),
                new ScientificModelReference(rs.getString("model_identifier"), rs.getString("model_version")),
                read(rs.getString("dataset_versions"), new TypeReference<List<ScientificDatasetReference>>() {
                }),
                rs.getString("input_hash"),
                rs.getString("result_hash"),
                read(rs.getString("calculation_trace"), SimulationCalculationTrace.class),
                read(rs.getString("conservation_ledger"), ConservationLedger.class),
                rs.getTimestamp("created_at").toInstant());
    }

    private String write(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not write audit JSON", ex);
        }
    }

    private <T> T read(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not read audit JSON", ex);
        }
    }

    private <T> T read(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not read audit JSON", ex);
        }
    }
}
