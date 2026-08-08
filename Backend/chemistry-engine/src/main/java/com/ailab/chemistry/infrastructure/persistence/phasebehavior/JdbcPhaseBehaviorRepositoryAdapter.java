package com.ailab.chemistry.infrastructure.persistence.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.phasebehavior.AntoineCoefficientSet;
import com.ailab.chemistry.domain.phasebehavior.PhaseBehaviorRepository;
import com.ailab.chemistry.domain.phasebehavior.PhaseBoundaryPoint;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionConditions;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionEvidenceStatus;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionProvenance;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionRecord;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionType;
import com.ailab.chemistry.domain.phasebehavior.TransitionEnthalpy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("local | prod | migration-test")
public class JdbcPhaseBehaviorRepositoryAdapter implements PhaseBehaviorRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcPhaseBehaviorRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<PhaseTransitionRecord> findTransition(String compoundCode, PhaseTransitionType forwardType) {
        String sql = """
                SELECT record_id, compound_code, transition_type, initial_phase, final_phase,
                       temperature_k, pressure_pa, normalized_enthalpy_j_mol, original_value, original_unit,
                       uncertainty, source_code, citation, reuse_terms, evidence_status
                  FROM chemistry.phase_transition_records
                 WHERE compound_code = ? AND transition_type = ? AND is_active = TRUE
                """;
        List<PhaseTransitionRecord> rows = jdbcTemplate.query(sql, (rs, rowNum) -> new PhaseTransitionRecord(
                rs.getString("record_id"),
                rs.getString("compound_code"),
                PhaseTransitionType.valueOf(rs.getString("transition_type")),
                MatterState.valueOf(rs.getString("initial_phase")),
                MatterState.valueOf(rs.getString("final_phase")),
                new PhaseTransitionConditions(
                        Temperature.of(rs.getBigDecimal("temperature_k"), TemperatureUnit.KELVIN),
                        Pressure.of(rs.getBigDecimal("pressure_pa"), PressureUnit.PASCAL)),
                new TransitionEnthalpy(
                        MolarEnergy.of(rs.getBigDecimal("normalized_enthalpy_j_mol"), MolarEnergyUnit.JOULE_PER_MOLE),
                        rs.getString("original_value"),
                        rs.getString("original_unit"),
                        rs.getString("uncertainty")),
                new PhaseTransitionProvenance(
                        rs.getString("source_code"),
                        rs.getString("citation"),
                        rs.getString("reuse_terms"),
                        PhaseTransitionEvidenceStatus.valueOf(rs.getString("evidence_status")))), compoundCode, forwardType.forwardType().name());
        return rows.stream().findFirst();
    }

    @Override
    public Optional<AntoineCoefficientSet> findAntoine(String compoundCode, MatterState initialPhase, MatterState finalPhase) {
        String sql = """
                SELECT correlation_id, compound_code, initial_phase, final_phase, coefficient_a, coefficient_b, coefficient_c,
                       min_temperature_k, max_temperature_k, temperature_unit, pressure_unit, convention,
                       source_code, citation, reuse_terms, evidence_status
                  FROM chemistry.antoine_correlations
                 WHERE compound_code = ? AND initial_phase = ? AND final_phase = ? AND is_active = TRUE
                """;
        List<AntoineCoefficientSet> rows = jdbcTemplate.query(sql, (rs, rowNum) -> new AntoineCoefficientSet(
                rs.getString("correlation_id"),
                rs.getString("compound_code"),
                MatterState.valueOf(rs.getString("initial_phase")),
                MatterState.valueOf(rs.getString("final_phase")),
                rs.getBigDecimal("coefficient_a"),
                rs.getBigDecimal("coefficient_b"),
                rs.getBigDecimal("coefficient_c"),
                Temperature.of(rs.getBigDecimal("min_temperature_k"), TemperatureUnit.KELVIN),
                Temperature.of(rs.getBigDecimal("max_temperature_k"), TemperatureUnit.KELVIN),
                rs.getString("temperature_unit"),
                rs.getString("pressure_unit"),
                rs.getString("convention"),
                new PhaseTransitionProvenance(
                        rs.getString("source_code"),
                        rs.getString("citation"),
                        rs.getString("reuse_terms"),
                        PhaseTransitionEvidenceStatus.valueOf(rs.getString("evidence_status")))), compoundCode, initialPhase.name(), finalPhase.name());
        return rows.stream().findFirst();
    }

    @Override
    public Optional<PhaseBoundaryPoint> findTriplePoint(String compoundCode) {
        return findBoundary(compoundCode, "TRIPLE_POINT");
    }

    @Override
    public Optional<PhaseBoundaryPoint> findCriticalPoint(String compoundCode) {
        return findBoundary(compoundCode, "CRITICAL_POINT");
    }

    private Optional<PhaseBoundaryPoint> findBoundary(String compoundCode, String boundaryType) {
        String sql = """
                SELECT compound_code, boundary_type, temperature_k, pressure_pa, source_code, citation, reuse_terms, evidence_status
                  FROM chemistry.phase_boundary_points
                 WHERE compound_code = ? AND boundary_type = ? AND is_active = TRUE
                """;
        List<PhaseBoundaryPoint> rows = jdbcTemplate.query(sql, (rs, rowNum) -> new PhaseBoundaryPoint(
                rs.getString("compound_code"),
                Temperature.of(rs.getBigDecimal("temperature_k"), TemperatureUnit.KELVIN),
                Pressure.of(rs.getBigDecimal("pressure_pa"), PressureUnit.PASCAL),
                rs.getString("boundary_type"),
                new PhaseTransitionProvenance(
                        rs.getString("source_code"),
                        rs.getString("citation"),
                        rs.getString("reuse_terms"),
                        PhaseTransitionEvidenceStatus.valueOf(rs.getString("evidence_status")))), compoundCode, boundaryType);
        return rows.stream().findFirst();
    }
}
