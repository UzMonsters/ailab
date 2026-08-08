package com.ailab.chemistry.infrastructure.persistence.laboratory;

import com.ailab.chemistry.domain.container.*;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("local | prod | migration-test")
public class JdbcContainerReferenceRepositoryAdapter implements ContainerReferenceRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcContainerReferenceRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ContainerProfile> findByProfileId(String profileId) {
        return find("WHERE p.profile_id = ? AND p.is_active = TRUE", profileId).stream().findFirst();
    }

    @Override
    public List<ContainerProfile> findActive() {
        return find("WHERE p.is_active = TRUE");
    }

    private List<ContainerProfile> find(String where, Object... args) {
        String sql = """
                SELECT p.profile_id, p.dataset_id, p.container_type, p.material, p.closure_type, p.closure_material,
                       p.geometry_description, p.nominal_capacity_ml, p.maximum_working_volume_ml,
                       p.min_temperature_c, p.max_temperature_c, p.max_pressure_bar, p.provenance_note,
                       s.source_code, s.citation, s.edition, s.table_or_section, s.page_or_record_identifier
                  FROM chemistry.container_reference_profiles p
                  JOIN chemistry.laboratory_source_documents s ON s.source_code = p.provenance_source_code
                """ + where + " ORDER BY p.profile_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String profileId = rs.getString("profile_id");
            ContainerMaterial closureMaterial = rs.getString("closure_material") == null ? null : ContainerMaterial.valueOf(rs.getString("closure_material"));
            ContainerTemperatureLimit temperatureLimit = rs.getBigDecimal("min_temperature_c") == null
                    ? null
                    : new ContainerTemperatureLimit(
                            Temperature.of(rs.getBigDecimal("min_temperature_c"), TemperatureUnit.CELSIUS),
                            Temperature.of(rs.getBigDecimal("max_temperature_c"), TemperatureUnit.CELSIUS));
            ContainerPressureLimit pressureLimit = rs.getBigDecimal("max_pressure_bar") == null
                    ? null
                    : new ContainerPressureLimit(Pressure.of(rs.getBigDecimal("max_pressure_bar"), PressureUnit.BAR));
            String provenance = rs.getString("source_code") + "; " + rs.getString("citation")
                    + "; edition=" + rs.getString("edition")
                    + "; section=" + rs.getString("table_or_section")
                    + "; record=" + rs.getString("page_or_record_identifier")
                    + "; " + rs.getString("provenance_note");
            return new ContainerProfile(
                    profileId,
                    rs.getString("dataset_id"),
                    ContainerType.valueOf(rs.getString("container_type")),
                    ContainerMaterial.valueOf(rs.getString("material")),
                    ContainerClosureType.valueOf(rs.getString("closure_type")),
                    closureMaterial,
                    new ContainerGeometry(rs.getString("geometry_description"), rs.getString("closure_type") != null && !"OPEN".equals(rs.getString("closure_type"))),
                    new NominalCapacity(Volume.of(rs.getBigDecimal("nominal_capacity_ml"), VolumeUnit.MILLILITER)),
                    new MaximumWorkingVolume(Volume.of(rs.getBigDecimal("maximum_working_volume_ml"), VolumeUnit.MILLILITER)),
                    temperatureLimit,
                    pressureLimit,
                    compatibility(rs.getString("dataset_id"), ContainerMaterial.valueOf(rs.getString("material")), closureMaterial),
                    provenance);
        }, args);
    }

    private List<ContainerCompatibilityRecord> compatibility(String datasetId, ContainerMaterial material, ContainerMaterial closureMaterial) {
        String sql = """
                SELECT r.compound_or_family, r.physical_state, r.container_material, r.closure_material,
                       r.compatibility_status, r.concentration_condition, r.min_temperature_c, r.max_temperature_c,
                       r.contact_duration_limit, r.source_code, r.evidence_status, r.source_record_id
                  FROM chemistry.container_compatibility_records r
                 WHERE r.dataset_id = ? AND r.container_material = ? AND r.is_active = TRUE
                   AND (r.closure_material IS NULL OR r.closure_material = ?)
                 ORDER BY r.compatibility_id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ContainerTemperatureLimit temperatureLimit = rs.getBigDecimal("min_temperature_c") == null
                    ? null
                    : new ContainerTemperatureLimit(
                            Temperature.of(rs.getBigDecimal("min_temperature_c"), TemperatureUnit.CELSIUS),
                            Temperature.of(rs.getBigDecimal("max_temperature_c"), TemperatureUnit.CELSIUS));
            ContainerMaterial recordClosure = rs.getString("closure_material") == null ? null : ContainerMaterial.valueOf(rs.getString("closure_material"));
            return new ContainerCompatibilityRecord(
                    rs.getString("compound_or_family"),
                    rs.getString("physical_state"),
                    ContainerMaterial.valueOf(rs.getString("container_material")),
                    recordClosure,
                    CompatibilityStatus.valueOf(rs.getString("compatibility_status")),
                    rs.getString("concentration_condition") == null ? null : new CompatibilityCondition(rs.getString("concentration_condition")),
                    temperatureLimit,
                    rs.getString("contact_duration_limit") == null ? null : new CompatibilityCondition(rs.getString("contact_duration_limit")),
                    rs.getString("source_code") + ":" + rs.getString("source_record_id"),
                    rs.getString("evidence_status"));
        }, datasetId, material.name(), closureMaterial == null ? null : closureMaterial.name());
    }
}
