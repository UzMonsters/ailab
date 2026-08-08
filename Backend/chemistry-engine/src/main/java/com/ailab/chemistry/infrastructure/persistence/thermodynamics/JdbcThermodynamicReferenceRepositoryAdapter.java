package com.ailab.chemistry.infrastructure.persistence.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.MolarEntropy;
import com.ailab.chemistry.domain.measurement.MolarEntropyUnit;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.StandardStateConvention;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicDatasetVersion;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicEvidenceStatus;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicProfile;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyRecord;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicProvenance;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceConditions;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Primary
@Profile("!(test | standalone-engine)")
@Transactional(readOnly = true)
public class JdbcThermodynamicReferenceRepositoryAdapter implements ThermodynamicReferenceRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcThermodynamicReferenceRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ThermodynamicProfile> findProfile(String compoundCode) {
        List<ThermodynamicProfile> profiles = query("""
                SELECT p.compound_code,
                       p.dataset_version,
                       r.property_type,
                       r.numeric_value,
                       r.unit_symbol,
                       c.temperature_kelvin,
                       c.pressure_pascal,
                       c.physical_state,
                       c.standard_state_convention,
                       r.evidence_status,
                       r.source_identifier,
                       r.citation,
                       r.reuse_limitations
                FROM chemistry.thermodynamic_profiles p
                JOIN chemistry.thermodynamic_property_records r ON r.profile_id = p.id
                JOIN chemistry.thermodynamic_reference_conditions c ON c.id = r.reference_condition_id
                WHERE lower(p.compound_code) = lower(?)
                ORDER BY p.compound_code, c.physical_state, r.property_type
                """, compoundCode);
        return profiles.stream().findFirst();
    }

    @Override
    public List<ThermodynamicProfile> findAllProfiles() {
        return query("""
                SELECT p.compound_code,
                       p.dataset_version,
                       r.property_type,
                       r.numeric_value,
                       r.unit_symbol,
                       c.temperature_kelvin,
                       c.pressure_pascal,
                       c.physical_state,
                       c.standard_state_convention,
                       r.evidence_status,
                       r.source_identifier,
                       r.citation,
                       r.reuse_limitations
                FROM chemistry.thermodynamic_profiles p
                JOIN chemistry.thermodynamic_property_records r ON r.profile_id = p.id
                JOIN chemistry.thermodynamic_reference_conditions c ON c.id = r.reference_condition_id
                ORDER BY p.compound_code, c.physical_state, r.property_type
                """);
    }

    private List<ThermodynamicProfile> query(String sql, Object... args) {
        List<Row> rows = jdbcTemplate.query(sql, this::mapRow, args);
        Map<String, List<ThermodynamicPropertyRecord>> recordsByCompound = new LinkedHashMap<>();
        Map<String, String> versionByCompound = new LinkedHashMap<>();
        for (Row row : rows) {
            recordsByCompound.computeIfAbsent(row.compoundCode(), ignored -> new java.util.ArrayList<>()).add(row.record());
            versionByCompound.put(row.compoundCode(), row.datasetVersion());
        }
        return recordsByCompound.entrySet().stream()
                .map(entry -> new ThermodynamicProfile(entry.getKey(), new ThermodynamicDatasetVersion(versionByCompound.get(entry.getKey())), entry.getValue()))
                .toList();
    }

    private Row mapRow(ResultSet rs, int rowNum) throws SQLException {
        String propertyType = rs.getString("property_type");
        BigDecimal value = rs.getBigDecimal("numeric_value");
        var type = ThermodynamicPropertyType.valueOf(propertyType);
        var conditions = new ThermodynamicReferenceConditions(
                Temperature.of(rs.getBigDecimal("temperature_kelvin"), TemperatureUnit.KELVIN),
                Pressure.of(rs.getBigDecimal("pressure_pascal"), PressureUnit.PASCAL),
                MatterState.valueOf(rs.getString("physical_state")),
                StandardStateConvention.valueOf(rs.getString("standard_state_convention")));
        var provenance = new ThermodynamicProvenance(rs.getString("source_identifier"), rs.getString("citation"), rs.getString("reuse_limitations"));
        var evidence = ThermodynamicEvidenceStatus.valueOf(rs.getString("evidence_status"));
        ThermodynamicPropertyRecord record = switch (type) {
            case STANDARD_ENTHALPY_OF_FORMATION, STANDARD_GIBBS_ENERGY_OF_FORMATION ->
                    new ThermodynamicPropertyRecord(type, MolarEnergy.of(value, energyUnit(rs.getString("unit_symbol"))),
                            null, null, conditions, evidence, provenance);
            case STANDARD_MOLAR_ENTROPY ->
                    new ThermodynamicPropertyRecord(type, null, MolarEntropy.of(value, entropyUnit(rs.getString("unit_symbol"))),
                            null, conditions, evidence, provenance);
            case MOLAR_HEAT_CAPACITY ->
                    new ThermodynamicPropertyRecord(type, null, null,
                            MolarHeatCapacity.of(value.toPlainString(), MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN),
                            conditions, evidence, provenance);
        };
        return new Row(rs.getString("compound_code"), rs.getString("dataset_version"), record);
    }

    private MolarEnergyUnit energyUnit(String symbol) {
        return "J/mol".equals(symbol) ? MolarEnergyUnit.JOULE_PER_MOLE : MolarEnergyUnit.KILOJOULE_PER_MOLE;
    }

    private MolarEntropyUnit entropyUnit(String symbol) {
        return "kJ/(mol*K)".equals(symbol) ? MolarEntropyUnit.KILOJOULE_PER_MOLE_KELVIN : MolarEntropyUnit.JOULE_PER_MOLE_KELVIN;
    }

    private record Row(String compoundCode, String datasetVersion, ThermodynamicPropertyRecord record) {
    }
}
