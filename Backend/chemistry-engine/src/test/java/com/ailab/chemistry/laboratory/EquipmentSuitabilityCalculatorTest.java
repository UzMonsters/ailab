package com.ailab.chemistry.laboratory;

import com.ailab.chemistry.domain.equipment.*;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.measurement.MeasurementResolution;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EquipmentSuitabilityCalculatorTest {
    private final EquipmentSuitabilityCalculator calculator = new EquipmentSuitabilityCalculator();
    private final Instant evaluationTime = Instant.parse("2026-08-06T10:00:00Z");

    @Test
    void balanceCapacityResolutionAccuracyAndCalibrationAreEvaluatedSeparately() {
        EquipmentReferenceProfile balance = analyticalBalance("120", "0.0001", "0.0002");

        EquipmentSuitabilityResult accepted = calculator.evaluate(new EquipmentSuitabilityRequest(
                balance,
                List.of(new EquipmentRequirement("MEASURE", "MASS", new BigDecimal("100"), "g",
                        MeasurementResolution.of("0.001", "g"), true)),
                List.of(new CalibrationRecord("CAL-1", Instant.parse("2026-07-20T00:00:00Z"), "certificate")),
                evaluationTime
        ));
        assertThat(accepted.status()).isEqualTo(EquipmentSuitabilityStatus.SUITABLE);

        EquipmentSuitabilityResult overCapacity = calculator.evaluate(new EquipmentSuitabilityRequest(
                balance,
                List.of(new EquipmentRequirement("MEASURE", "MASS", new BigDecimal("121"), "g",
                        MeasurementResolution.of("0.001", "g"), true)),
                List.of(new CalibrationRecord("CAL-1", Instant.parse("2026-07-20T00:00:00Z"), "certificate")),
                evaluationTime
        ));
        assertThat(overCapacity.errorCodes()).contains(EquipmentErrorCode.VALUE_OUTSIDE_OPERATING_RANGE);

        EquipmentSuitabilityResult resolutionRejected = calculator.evaluate(new EquipmentSuitabilityRequest(
                balance,
                List.of(new EquipmentRequirement("MEASURE", "MASS", new BigDecimal("1"), "g",
                        MeasurementResolution.of("0.00001", "g"), true)),
                List.of(new CalibrationRecord("CAL-1", Instant.parse("2026-07-20T00:00:00Z"), "certificate")),
                evaluationTime
        ));
        assertThat(resolutionRejected.errorCodes()).contains(EquipmentErrorCode.INSUFFICIENT_RESOLUTION);

        EquipmentReferenceProfile noAccuracy = analyticalBalance("120", "0.0001", null);
        EquipmentSuitabilityResult accuracyRejected = calculator.evaluate(new EquipmentSuitabilityRequest(
                noAccuracy,
                List.of(new EquipmentRequirement("MEASURE", "MASS", new BigDecimal("1"), "g",
                        MeasurementResolution.of("0.001", "g"), true)),
                List.of(new CalibrationRecord("CAL-1", Instant.parse("2026-07-20T00:00:00Z"), "certificate")),
                evaluationTime
        ));
        assertThat(accuracyRejected.errorCodes()).contains(EquipmentErrorCode.MISSING_ACCURACY_OR_UNCERTAINTY);
    }

    @Test
    void temperatureAndPhRangesMissingCapabilitiesAndNameInferenceAreBlocked() {
        EquipmentReferenceProfile thermometer = new EquipmentReferenceProfile(
                "THERM-GLASS-10-110", "laboratory-equipment-reference-v1.0.0", EquipmentType.THERMOMETER,
                "Glass thermometer", EquipmentCondition.OPERATIONAL,
                List.of(new EquipmentCapability("MEASURE", "TEMPERATURE",
                        new OperatingRange(new BigDecimal("-10"), new BigDecimal("110"), "degC"),
                        MeasurementResolution.of("1", "degC"), null, null, null,
                        CalibrationRequirement.notRequired(), List.of(), "public type taxonomy")));

        EquipmentSuitabilityResult temperatureRejected = calculator.evaluate(new EquipmentSuitabilityRequest(
                thermometer,
                List.of(new EquipmentRequirement("MEASURE", "TEMPERATURE", new BigDecimal("125"), "degC", null, false)),
                List.of(),
                evaluationTime
        ));
        assertThat(temperatureRejected.errorCodes()).contains(EquipmentErrorCode.VALUE_OUTSIDE_OPERATING_RANGE);

        EquipmentReferenceProfile namedHotPlate = new EquipmentReferenceProfile(
                "HOTPLATE-TAXONOMY", "laboratory-equipment-reference-v1.0.0", EquipmentType.HOT_PLATE,
                "Hot plate with digital temperature display", EquipmentCondition.OPERATIONAL,
                List.of(), "type taxonomy only; no performance claims");
        EquipmentSuitabilityResult nameDoesNotInferCapability = calculator.evaluate(new EquipmentSuitabilityRequest(
                namedHotPlate,
                List.of(new EquipmentRequirement("MEASURE", "TEMPERATURE", new BigDecimal("80"), "degC", null, false)),
                List.of(),
                evaluationTime
        ));
        assertThat(nameDoesNotInferCapability.errorCodes()).contains(EquipmentErrorCode.MISSING_CAPABILITY);

        EquipmentReferenceProfile phMeter = new EquipmentReferenceProfile(
                "PH-METER-0-14", "laboratory-equipment-reference-v1.0.0", EquipmentType.PH_METER,
                "pH meter", EquipmentCondition.OPERATIONAL,
                List.of(new EquipmentCapability("MEASURE", "PH",
                        new OperatingRange(new BigDecimal("0"), new BigDecimal("14"), "pH"),
                        MeasurementResolution.of("0.01", "pH"), new AccuracySpecification(new BigDecimal("0.02"), "pH"),
                        null, null, CalibrationRequirement.required(Duration.of("720", DurationUnit.HOUR), Duration.of("48", DurationUnit.HOUR), "internal calibration policy"),
                        List.of(), "model-specific specification")));
        EquipmentSuitabilityResult phAccepted = calculator.evaluate(new EquipmentSuitabilityRequest(
                phMeter,
                List.of(new EquipmentRequirement("MEASURE", "PH", new BigDecimal("7.2"), "pH", MeasurementResolution.of("0.1", "pH"), true)),
                List.of(new CalibrationRecord("CAL-PH", Instant.parse("2026-08-01T00:00:00Z"), "buffer records")),
                evaluationTime
        ));
        assertThat(phAccepted.status()).isEqualTo(EquipmentSuitabilityStatus.SUITABLE);
    }

    @Test
    void calibrationUsesCallerTimestampAndReportsMissingExpiredAndDueSoon() {
        EquipmentReferenceProfile balance = analyticalBalance("120", "0.0001", "0.0002");
        EquipmentRequirement req = new EquipmentRequirement("MEASURE", "MASS", new BigDecimal("1"), "g",
                MeasurementResolution.of("0.001", "g"), true);

        EquipmentSuitabilityResult missing = calculator.evaluate(new EquipmentSuitabilityRequest(balance, List.of(req), List.of(), evaluationTime));
        assertThat(missing.errorCodes()).contains(EquipmentErrorCode.MISSING_CALIBRATION);

        EquipmentSuitabilityResult expired = calculator.evaluate(new EquipmentSuitabilityRequest(balance, List.of(req),
                List.of(new CalibrationRecord("OLD", Instant.parse("2026-06-01T00:00:00Z"), "certificate")), evaluationTime));
        assertThat(expired.errorCodes()).contains(EquipmentErrorCode.EXPIRED_CALIBRATION);

        EquipmentSuitabilityResult dueSoon = calculator.evaluate(new EquipmentSuitabilityRequest(balance, List.of(req),
                List.of(new CalibrationRecord("SOON", Instant.parse("2026-07-08T00:00:00Z"), "certificate")), evaluationTime));
        assertThat(dueSoon.status()).isEqualTo(EquipmentSuitabilityStatus.SUITABLE_WITH_WARNINGS);
        assertThat(dueSoon.warnings()).extracting(EquipmentWarning::message).anyMatch(m -> m.contains("due soon"));
    }

    private static EquipmentReferenceProfile analyticalBalance(String capacityGrams, String resolutionGrams, String accuracyGrams) {
        return new EquipmentReferenceProfile(
                "BAL-ANALYTICAL-120G", "laboratory-equipment-reference-v1.0.0", EquipmentType.ANALYTICAL_BALANCE,
                "Analytical balance reference profile", EquipmentCondition.OPERATIONAL,
                List.of(new EquipmentCapability("MEASURE", "MASS",
                        new OperatingRange(BigDecimal.ZERO, new BigDecimal(capacityGrams), "g"),
                        MeasurementResolution.of(resolutionGrams, "g"),
                        accuracyGrams == null ? null : new AccuracySpecification(new BigDecimal(accuracyGrams), "g"),
                        null,
                        new CapacityLimit("MASS", new BigDecimal(capacityGrams), "g"),
                        CalibrationRequirement.required(Duration.of("720", DurationUnit.HOUR), Duration.of("72", DurationUnit.HOUR), "internal calibration policy"),
                        List.of("stable bench"), "model-specific specification")));
    }
}
