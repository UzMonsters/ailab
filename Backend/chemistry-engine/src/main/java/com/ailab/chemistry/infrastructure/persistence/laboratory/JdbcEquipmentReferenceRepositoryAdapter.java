package com.ailab.chemistry.infrastructure.persistence.laboratory;

import com.ailab.chemistry.domain.equipment.*;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.measurement.MeasurementResolution;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("local | prod | migration-test")
public class JdbcEquipmentReferenceRepositoryAdapter implements EquipmentReferenceRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcEquipmentReferenceRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<EquipmentReferenceProfile> findByProfileId(String profileId) {
        return find("WHERE p.profile_id = ? AND p.is_active = TRUE", profileId).stream().findFirst();
    }

    @Override
    public List<EquipmentReferenceProfile> findActive() {
        return find("WHERE p.is_active = TRUE");
    }

    private List<EquipmentReferenceProfile> find(String where, Object... args) {
        String sql = """
                SELECT p.profile_id, p.dataset_id, p.equipment_type, p.display_name, p.provenance_note,
                       s.source_code, s.citation, s.edition, s.table_or_section, s.page_or_record_identifier
                  FROM chemistry.equipment_reference_profiles p
                  JOIN chemistry.laboratory_source_documents s ON s.source_code = p.provenance_source_code
                """ + where + " ORDER BY p.profile_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String profileId = rs.getString("profile_id");
            String provenance = rs.getString("source_code") + "; " + rs.getString("citation")
                    + "; edition=" + rs.getString("edition")
                    + "; section=" + rs.getString("table_or_section")
                    + "; record=" + rs.getString("page_or_record_identifier")
                    + "; " + rs.getString("provenance_note");
            return new EquipmentReferenceProfile(
                    profileId,
                    rs.getString("dataset_id"),
                    EquipmentType.valueOf(rs.getString("equipment_type")),
                    rs.getString("display_name"),
                    EquipmentCondition.OPERATIONAL,
                    capabilities(profileId),
                    provenance);
        }, args);
    }

    private List<EquipmentCapability> capabilities(String profileId) {
        String sql = """
                SELECT c.capability_id, c.capability_type, c.quantity, c.unit, c.minimum_value, c.maximum_value,
                       c.resolution_value, c.accuracy_value, c.uncertainty_value, c.capacity_value,
                       COALESCE(c.environmental_restrictions, '') AS environmental_restrictions,
                       c.provenance_note, s.source_code, s.citation, s.edition, s.table_or_section,
                       s.page_or_record_identifier, cr.calibration_required, cr.interval_seconds, cr.due_soon_seconds,
                       COALESCE(cr.provenance_note, '') AS calibration_note
                  FROM chemistry.equipment_capabilities c
                  JOIN chemistry.laboratory_source_documents s ON s.source_code = c.provenance_source_code
             LEFT JOIN chemistry.equipment_calibration_requirements cr ON cr.capability_id = c.capability_id
                 WHERE c.profile_id = ? AND c.is_active = TRUE
                 ORDER BY c.capability_id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String unit = rs.getString("unit");
            AccuracySpecification accuracy = rs.getBigDecimal("accuracy_value") == null
                    ? null
                    : new AccuracySpecification(rs.getBigDecimal("accuracy_value"), unit);
            UncertaintySpecification uncertainty = rs.getBigDecimal("uncertainty_value") == null
                    ? null
                    : new UncertaintySpecification(new com.ailab.chemistry.domain.measurement.MeasurementUncertainty(rs.getBigDecimal("uncertainty_value"), unit));
            CapacityLimit capacity = rs.getBigDecimal("capacity_value") == null
                    ? null
                    : new CapacityLimit(rs.getString("quantity"), rs.getBigDecimal("capacity_value"), unit);
            CalibrationRequirement calibration = calibrationRequirement(rs.getBoolean("calibration_required"), rs.getBigDecimal("interval_seconds"), rs.getBigDecimal("due_soon_seconds"), rs.getString("calibration_note"));
            String provenance = rs.getString("source_code") + "; " + rs.getString("citation")
                    + "; edition=" + rs.getString("edition")
                    + "; section=" + rs.getString("table_or_section")
                    + "; record=" + rs.getString("page_or_record_identifier")
                    + "; " + rs.getString("provenance_note");
            return new EquipmentCapability(
                    rs.getString("capability_type"),
                    rs.getString("quantity"),
                    new OperatingRange(rs.getBigDecimal("minimum_value"), rs.getBigDecimal("maximum_value"), unit),
                    rs.getBigDecimal("resolution_value") == null ? null : new MeasurementResolution(rs.getBigDecimal("resolution_value"), unit),
                    accuracy,
                    uncertainty,
                    capacity,
                    calibration,
                    split(rs.getString("environmental_restrictions")),
                    provenance);
        }, profileId);
    }

    private CalibrationRequirement calibrationRequirement(boolean required, java.math.BigDecimal intervalSeconds, java.math.BigDecimal dueSoonSeconds, String provenance) {
        if (!required) {
            return CalibrationRequirement.notRequired();
        }
        return CalibrationRequirement.required(
                Duration.of(intervalSeconds, DurationUnit.SECOND),
                dueSoonSeconds == null ? null : Duration.of(dueSoonSeconds, DurationUnit.SECOND),
                provenance);
    }

    private List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(";")).map(String::trim).filter(s -> !s.isBlank()).toList();
    }
}
