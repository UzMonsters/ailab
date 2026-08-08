package com.ailab.chemistry.infrastructure.persistence.electrochemistry;

import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalDatasetVersion;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalEvidenceStatus;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalProvenance;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalReferenceConditions;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalReferenceRepository;
import com.ailab.chemistry.domain.electrochemistry.ElectrodePotential;
import com.ailab.chemistry.domain.electrochemistry.ElectronCount;
import com.ailab.chemistry.domain.electrochemistry.HalfReaction;
import com.ailab.chemistry.domain.electrochemistry.HalfReactionDirection;
import com.ailab.chemistry.domain.electrochemistry.HalfReactionParticipant;
import com.ailab.chemistry.domain.electrochemistry.HalfReactionParticipantSide;
import com.ailab.chemistry.domain.electrochemistry.StandardReductionPotential;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Profile("local | prod | migration-test")
public class JdbcElectrochemicalReferenceRepositoryAdapter implements ElectrochemicalReferenceRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcElectrochemicalReferenceRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<StandardReductionPotential> findByRecordId(String recordId) {
        return find("WHERE srp.record_id = ? AND srp.is_active = TRUE", recordId).stream().findFirst();
    }

    @Override
    public List<StandardReductionPotential> findActive() {
        return find("WHERE srp.is_active = TRUE");
    }

    private List<StandardReductionPotential> find(String where, Object... args) {
        String sql = """
                SELECT srp.record_id, srp.dataset_id, dv.version, dv.immutable_snapshot, srp.equation,
                       srp.standard_potential_v, srp.electron_count, c.temperature_k, c.solvent_code, c.standard_state_convention,
                       srp.source_code, sd.citation, sd.reuse_terms, srp.evidence_status
                  FROM chemistry.standard_reduction_potentials srp
                  JOIN chemistry.electrochemical_dataset_versions dv ON dv.dataset_id = srp.dataset_id
                  JOIN chemistry.electrochemical_reference_conditions c ON c.condition_id = srp.condition_id
                  JOIN chemistry.electrochemical_source_documents sd ON sd.source_code = srp.source_code
                """ + where + " ORDER BY srp.record_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String recordId = rs.getString("record_id");
            List<HalfReactionParticipant> participants = participants(recordId);
            return new StandardReductionPotential(
                    recordId,
                    new ElectrochemicalDatasetVersion(rs.getString("dataset_id"), rs.getString("version"), rs.getBoolean("immutable_snapshot")),
                    new HalfReaction(rs.getString("equation"), participants, HalfReactionDirection.REDUCTION, new ElectronCount(rs.getBigDecimal("electron_count"))),
                    new ElectrodePotential(rs.getBigDecimal("standard_potential_v")),
                    new ElectrochemicalReferenceConditions(
                            Temperature.of(rs.getBigDecimal("temperature_k"), TemperatureUnit.KELVIN),
                            rs.getString("solvent_code"),
                            rs.getString("standard_state_convention")),
                    new ElectrochemicalProvenance(
                            rs.getString("source_code"),
                            rs.getString("citation"),
                            rs.getString("reuse_terms"),
                            ElectrochemicalEvidenceStatus.valueOf(rs.getString("evidence_status"))),
                    true
            );
        }, args);
    }

    private List<HalfReactionParticipant> participants(String recordId) {
        String sql = """
                SELECT species_code, display_formula, element_counts, coefficient, phase, charge, side
                  FROM chemistry.half_reaction_participants
                 WHERE record_id = ?
                 ORDER BY display_order
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new HalfReactionParticipant(
                rs.getString("species_code"),
                rs.getString("display_formula"),
                elementCounts(rs.getString("element_counts")),
                rs.getBigDecimal("coefficient"),
                rs.getString("phase"),
                rs.getInt("charge"),
                HalfReactionParticipantSide.valueOf(rs.getString("side"))
        ), recordId);
    }

    private Map<String, BigDecimal> elementCounts(String value) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (String pair : value.split(",")) {
            String[] parts = pair.split(":");
            result.put(parts[0], new BigDecimal(parts[1]));
        }
        return result;
    }
}
