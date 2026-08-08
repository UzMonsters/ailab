package com.ailab.chemistry.infrastructure.persistence.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.HeatCapacityCorrelation;
import com.ailab.chemistry.domain.thermodynamics.HeatCapacityCorrelationType;
import com.ailab.chemistry.domain.thermodynamics.PolynomialCoefficientSet;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrelationRepository;
import com.ailab.chemistry.domain.thermodynamics.TemperatureValidityRange;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicProvenance;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@Profile("!(test | standalone-engine)")
@Transactional(readOnly = true)
public class JdbcTemperatureCorrelationRepositoryAdapter implements TemperatureCorrelationRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemperatureCorrelationRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<HeatCapacityCorrelation> find(String compoundCode, MatterState state, Temperature targetTemperature) {
        return jdbcTemplate.query("""
                        SELECT compound_code,
                               physical_state,
                               correlation_type,
                               coefficient_a,
                               coefficient_b,
                               coefficient_c,
                               coefficient_d,
                               coefficient_e,
                               coefficient_f,
                               coefficient_g,
                               coefficient_h,
                               temperature_min_kelvin,
                               temperature_max_kelvin,
                               heat_capacity_unit,
                               scaling_convention,
                               source_identifier,
                               citation,
                               reuse_limitations
                        FROM chemistry.thermodynamic_temperature_correlations
                        WHERE lower(compound_code) = lower(?)
                          AND physical_state = ?
                          AND temperature_min_kelvin <= ?
                          AND temperature_max_kelvin >= ?
                        ORDER BY temperature_min_kelvin, temperature_max_kelvin
                        LIMIT 1
                        """,
                this::mapRow,
                compoundCode,
                state.name(),
                targetTemperature.in(TemperatureUnit.KELVIN),
                targetTemperature.in(TemperatureUnit.KELVIN)).stream().findFirst();
    }

    @Override
    public List<HeatCapacityCorrelation> findAll() {
        return jdbcTemplate.query("""
                SELECT compound_code,
                       physical_state,
                       correlation_type,
                       coefficient_a,
                       coefficient_b,
                       coefficient_c,
                       coefficient_d,
                       coefficient_e,
                       coefficient_f,
                       coefficient_g,
                       coefficient_h,
                       temperature_min_kelvin,
                       temperature_max_kelvin,
                       heat_capacity_unit,
                       scaling_convention,
                       source_identifier,
                       citation,
                       reuse_limitations
                FROM chemistry.thermodynamic_temperature_correlations
                ORDER BY compound_code, physical_state, temperature_min_kelvin
                """, this::mapRow);
    }

    private HeatCapacityCorrelation mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new HeatCapacityCorrelation(
                rs.getString("compound_code"),
                MatterState.valueOf(rs.getString("physical_state")),
                HeatCapacityCorrelationType.valueOf(rs.getString("correlation_type")),
                new PolynomialCoefficientSet(
                        rs.getBigDecimal("coefficient_a"),
                        rs.getBigDecimal("coefficient_b"),
                        rs.getBigDecimal("coefficient_c"),
                        rs.getBigDecimal("coefficient_d"),
                        rs.getBigDecimal("coefficient_e"),
                        rs.getBigDecimal("coefficient_f"),
                        rs.getBigDecimal("coefficient_g"),
                        rs.getBigDecimal("coefficient_h")),
                new TemperatureValidityRange(
                        Temperature.of(rs.getBigDecimal("temperature_min_kelvin"), TemperatureUnit.KELVIN),
                        Temperature.of(rs.getBigDecimal("temperature_max_kelvin"), TemperatureUnit.KELVIN)),
                rs.getString("heat_capacity_unit"),
                rs.getString("scaling_convention"),
                new ThermodynamicProvenance(rs.getString("source_identifier"), rs.getString("citation"),
                        rs.getString("reuse_limitations")));
    }
}
