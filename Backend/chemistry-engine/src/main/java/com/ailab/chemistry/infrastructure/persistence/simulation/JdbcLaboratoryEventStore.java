package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.laboratoryevent.CausationId;
import com.ailab.chemistry.domain.laboratoryevent.CorrelationId;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEvent;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventSequence;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventSource;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventStore;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventType;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationStateVersion;
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
public class JdbcLaboratoryEventStore implements LaboratoryEventStore {
    private final JdbcTemplate jdbcTemplate;
    private final LaboratoryEventCodec codec = new LaboratoryEventCodec();

    public JdbcLaboratoryEventStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<LaboratoryEvent> findByIdempotencyKey(SimulationSessionId sessionId, IdempotencyKey idempotencyKey) {
        return jdbcTemplate.query("""
                SELECT event_id, session_id, sequence_number, state_version, occurred_at, recorded_at, event_type,
                       schema_version, source_type, source_actor, correlation_id, causation_id, idempotency_key,
                       payload_json::text AS payload_json
                FROM chemistry.simulation_events
                WHERE session_id = ? AND idempotency_key = ?
                """, (rs, rowNum) -> map(rs), sessionId.value(), idempotencyKey.value()).stream().findFirst();
    }

    @Override
    public LaboratoryEvent append(LaboratoryEvent event) {
        jdbcTemplate.update("""
                INSERT INTO chemistry.simulation_events
                (event_id, session_id, sequence_number, state_version, occurred_at, recorded_at, event_type,
                 schema_version, source_type, source_actor, correlation_id, causation_id, idempotency_key,
                 payload_json, payload_fingerprint)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb), ?)
                """, event.eventId().value(), event.sessionId().value(), event.sequence().value(),
                event.stateVersion().value(), Timestamp.from(event.occurredAt()), Timestamp.from(event.recordedAt()), event.type().name(),
                event.schemaVersion(), event.source().sourceType(), event.source().actor(),
                event.correlationId().value(), event.causationId() == null ? null : event.causationId().value(),
                event.idempotencyKey().value(), codec.payloadJson(event.payload()), codec.fingerprint(event.payload()));
        return event;
    }

    @Override
    public List<LaboratoryEvent> eventsForSession(SimulationSessionId sessionId) {
        return jdbcTemplate.query("""
                SELECT event_id, session_id, sequence_number, state_version, occurred_at, recorded_at, event_type,
                       schema_version, source_type, source_actor, correlation_id, causation_id, idempotency_key,
                       payload_json::text AS payload_json
                FROM chemistry.simulation_events
                WHERE session_id = ?
                ORDER BY sequence_number
                """, (rs, rowNum) -> map(rs), sessionId.value());
    }

    private LaboratoryEvent map(ResultSet rs) throws SQLException {
        LaboratoryEventType type = LaboratoryEventType.valueOf(rs.getString("event_type"));
        String causation = rs.getString("causation_id");
        return new LaboratoryEvent(
                new LaboratoryEventId(rs.getString("event_id")),
                new SimulationSessionId(rs.getString("session_id")),
                new LaboratoryEventSequence(rs.getLong("sequence_number")),
                new SimulationStateVersion(rs.getLong("state_version")),
                rs.getTimestamp("occurred_at").toInstant(),
                rs.getTimestamp("recorded_at").toInstant(),
                type,
                rs.getInt("schema_version"),
                new LaboratoryEventSource(rs.getString("source_type"), rs.getString("source_actor")),
                new CorrelationId(rs.getString("correlation_id")),
                causation == null ? null : new CausationId(causation),
                new IdempotencyKey(rs.getString("idempotency_key")),
                codec.payloadFromJson(type, rs.getString("payload_json")));
    }
}
