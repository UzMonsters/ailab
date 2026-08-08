package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionStatus;
import com.ailab.chemistry.domain.simulationstate.SimulationSnapshot;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.chemistry.domain.simulationstate.SimulationStateRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("local | prod | migration-test")
public class JdbcSimulationStateRepositoryAdapter implements SimulationStateRepository {
    private final JdbcTemplate jdbcTemplate;
    private final LaboratoryEventCodec codec = new LaboratoryEventCodec();

    public JdbcSimulationStateRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SimulationState create(SimulationState state, String processCode, int processVersion) {
        jdbcTemplate.update("""
                INSERT INTO chemistry.simulation_sessions
                (session_id, process_code, process_version, status, current_version, current_state)
                VALUES (?, ?, ?, ?, ?, CAST(? AS jsonb))
                """, state.sessionId().value(), processCode, processVersion, state.status().name(),
                state.version().value(), codec.stateJson(state));
        return state;
    }

    @Override
    public Optional<SimulationState> findCurrent(SimulationSessionId sessionId) {
        return jdbcTemplate.query("""
                SELECT current_state::text AS current_state
                FROM chemistry.simulation_sessions
                WHERE session_id = ?
                """, (rs, rowNum) -> codec.stateFromJson(rs.getString("current_state")), sessionId.value())
                .stream().findFirst();
    }

    @Override
    public SimulationState lockCurrent(SimulationSessionId sessionId) {
        return jdbcTemplate.query("""
                SELECT current_state::text AS current_state
                FROM chemistry.simulation_sessions
                WHERE session_id = ?
                FOR UPDATE
                """, (rs, rowNum) -> codec.stateFromJson(rs.getString("current_state")), sessionId.value())
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Simulation session not found: " + sessionId.value()));
    }

    @Override
    public void updateCurrent(SimulationState state) {
        jdbcTemplate.update("""
                UPDATE chemistry.simulation_sessions
                SET status = ?, current_version = ?, current_state = CAST(? AS jsonb), updated_at = NOW()
                WHERE session_id = ?
                """, state.status().name(), state.version().value(), codec.stateJson(state), state.sessionId().value());
    }

    @Override
    public void saveSnapshot(SimulationState state, long eventSequence, String checksum) {
        jdbcTemplate.update("""
                INSERT INTO chemistry.simulation_snapshots
                (session_id, state_version, event_sequence, snapshot_payload, checksum)
                VALUES (?, ?, ?, CAST(? AS jsonb), ?)
                ON CONFLICT (session_id, event_sequence) DO NOTHING
                """, state.sessionId().value(), state.version().value(), eventSequence, codec.stateJson(state), checksum);
    }

    @Override
    public Optional<SimulationSnapshot> latestSnapshot(SimulationSessionId sessionId) {
        return jdbcTemplate.query("""
                SELECT event_sequence, checksum, snapshot_payload::text AS snapshot_payload
                FROM chemistry.simulation_snapshots
                WHERE session_id = ?
                ORDER BY event_sequence DESC
                LIMIT 1
                """, (rs, rowNum) -> new SimulationSnapshot(
                codec.stateFromJson(rs.getString("snapshot_payload")),
                rs.getLong("event_sequence"),
                rs.getString("checksum")), sessionId.value()).stream().findFirst();
    }
}
