package com.ailab.chemistry.laboratory;

import com.ailab.chemistry.domain.container.*;
import com.ailab.chemistry.domain.measurement.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContainerSuitabilityCalculatorTest {
    private final ContainerSuitabilityCalculator calculator = new ContainerSuitabilityCalculator();

    @Test
    void capacityFillFractionAndHeadspaceAreValidated() {
        ContainerProfile beaker = beaker();

        ContainerSuitabilityResult accepted = calculator.evaluate(new ContainerSuitabilityRequest(
                beaker, Volume.of("80", VolumeUnit.MILLILITER), false, null, null, Volume.of("10", VolumeUnit.MILLILITER),
                "COMP-H2O", "AQUEOUS", null, null));
        assertThat(accepted.status()).isEqualTo(ContainerSuitabilityStatus.SUITABLE);
        assertThat(accepted.fillFraction().value()).isEqualByComparingTo("0.8");
        assertThat(accepted.headspace().volume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo("20");

        ContainerSuitabilityResult boundary = calculator.evaluate(new ContainerSuitabilityRequest(
                beaker, Volume.of("90", VolumeUnit.MILLILITER), false, null, null, null,
                "COMP-H2O", "AQUEOUS", null, null));
        assertThat(boundary.status()).isEqualTo(ContainerSuitabilityStatus.SUITABLE);

        ContainerSuitabilityResult overfill = calculator.evaluate(new ContainerSuitabilityRequest(
                beaker, Volume.of("95", VolumeUnit.MILLILITER), false, null, null, null,
                "COMP-H2O", "AQUEOUS", null, null));
        assertThat(overfill.errorCodes()).contains(ContainerErrorCode.CONTENT_VOLUME_EXCEEDS_WORKING_LIMIT);

        ContainerSuitabilityResult sealedHeadspace = calculator.evaluate(new ContainerSuitabilityRequest(
                beaker, Volume.of("80", VolumeUnit.MILLILITER), true, null, null, Volume.of("25", VolumeUnit.MILLILITER),
                "COMP-H2O", "AQUEOUS", null, null));
        assertThat(sealedHeadspace.errorCodes()).contains(ContainerErrorCode.INSUFFICIENT_HEADSPACE);
    }

    @Test
    void pressureTemperatureAndOpenContainerSemanticsAreExplicit() {
        ContainerSuitabilityResult openPressure = calculator.evaluate(new ContainerSuitabilityRequest(
                beaker(), Volume.of("20", VolumeUnit.MILLILITER), false,
                null, Pressure.of("1.5", PressureUnit.BAR), null, "COMP-H2O", "AQUEOUS", null, null));
        assertThat(openPressure.errorCodes()).contains(ContainerErrorCode.OPEN_CONTAINER_NOT_PRESSURE_RATED);

        ContainerSuitabilityResult pressureAccepted = calculator.evaluate(new ContainerSuitabilityRequest(
                sealedBottle(), Volume.of("200", VolumeUnit.MILLILITER), true,
                null, Pressure.of("1.5", PressureUnit.BAR), Volume.of("50", VolumeUnit.MILLILITER),
                "COMP-H2O", "AQUEOUS", null, null));
        assertThat(pressureAccepted.status()).isEqualTo(ContainerSuitabilityStatus.SUITABLE);

        ContainerSuitabilityResult pressureRejected = calculator.evaluate(new ContainerSuitabilityRequest(
                sealedBottle(), Volume.of("200", VolumeUnit.MILLILITER), true,
                null, Pressure.of("3", PressureUnit.BAR), Volume.of("50", VolumeUnit.MILLILITER),
                "COMP-H2O", "AQUEOUS", null, null));
        assertThat(pressureRejected.errorCodes()).contains(ContainerErrorCode.PRESSURE_LIMIT_EXCEEDED);

        ContainerSuitabilityResult temperatureRejected = calculator.evaluate(new ContainerSuitabilityRequest(
                beaker(), Volume.of("20", VolumeUnit.MILLILITER), false,
                Temperature.of("180", TemperatureUnit.CELSIUS), null, null, "COMP-H2O", "AQUEOUS", null, null));
        assertThat(temperatureRejected.errorCodes()).contains(ContainerErrorCode.TEMPERATURE_LIMIT_EXCEEDED);
    }

    @Test
    void compatibilityUnknownAndIncompatibleRecordsBlockSuitability() {
        ContainerSuitabilityResult compatible = calculator.evaluate(new ContainerSuitabilityRequest(
                beaker(), Volume.of("20", VolumeUnit.MILLILITER), false, null, null, null,
                "COMP-H2O", "AQUEOUS", null, null));
        assertThat(compatible.status()).isEqualTo(ContainerSuitabilityStatus.SUITABLE);

        ContainerSuitabilityResult limitedOutsideTemperature = calculator.evaluate(new ContainerSuitabilityRequest(
                hdpeBottle(), Volume.of("100", VolumeUnit.MILLILITER), false,
                Temperature.of("70", TemperatureUnit.CELSIUS), null, null, "FAMILY-DILUTE-ACID", "AQUEOUS", null, null));
        assertThat(limitedOutsideTemperature.errorCodes()).contains(ContainerErrorCode.COMPATIBILITY_LIMIT_VIOLATED);

        ContainerSuitabilityResult incompatible = calculator.evaluate(new ContainerSuitabilityRequest(
                hdpeBottle(), Volume.of("100", VolumeUnit.MILLILITER), false,
                Temperature.of("20", TemperatureUnit.CELSIUS), null, null, "FAMILY-AROMATIC-SOLVENT", "LIQUID", null, null));
        assertThat(incompatible.errorCodes()).contains(ContainerErrorCode.INCOMPATIBLE_MATERIAL);

        ContainerSuitabilityResult unknown = calculator.evaluate(new ContainerSuitabilityRequest(
                hdpeBottle(), Volume.of("100", VolumeUnit.MILLILITER), false,
                Temperature.of("20", TemperatureUnit.CELSIUS), null, null, "COMP-UNKNOWN", "LIQUID", null, null));
        assertThat(unknown.errorCodes()).contains(ContainerErrorCode.UNKNOWN_COMPATIBILITY);
    }

    private static ContainerProfile beaker() {
        return new ContainerProfile("CON-BORO-BEAKER-100ML", "laboratory-container-reference-v1.0.0",
                ContainerType.BEAKER, ContainerMaterial.BOROSILICATE_GLASS, ContainerClosureType.OPEN, null,
                new ContainerGeometry("cylindrical", false), new NominalCapacity(Volume.of("100", VolumeUnit.MILLILITER)),
                new MaximumWorkingVolume(Volume.of("90", VolumeUnit.MILLILITER)),
                new ContainerTemperatureLimit(Temperature.of("0", TemperatureUnit.CELSIUS), Temperature.of("150", TemperatureUnit.CELSIUS)),
                null,
                List.of(new ContainerCompatibilityRecord("COMP-H2O", "AQUEOUS", ContainerMaterial.BOROSILICATE_GLASS, null,
                        CompatibilityStatus.COMPATIBLE, null, null, null, "public borosilicate-glass guidance", "SOURCED_REFERENCE_VALUE")),
                "type taxonomy; no pressure rating");
    }

    private static ContainerProfile sealedBottle() {
        return new ContainerProfile("CON-SEALED-BORO-BOTTLE-250ML", "laboratory-container-reference-v1.0.0",
                ContainerType.REAGENT_BOTTLE, ContainerMaterial.BOROSILICATE_GLASS, ContainerClosureType.SCREW_CAP, ContainerMaterial.POLYPROPYLENE,
                new ContainerGeometry("bottle", true), new NominalCapacity(Volume.of("250", VolumeUnit.MILLILITER)),
                new MaximumWorkingVolume(Volume.of("200", VolumeUnit.MILLILITER)),
                new ContainerTemperatureLimit(Temperature.of("0", TemperatureUnit.CELSIUS), Temperature.of("120", TemperatureUnit.CELSIUS)),
                new ContainerPressureLimit(Pressure.of("2", PressureUnit.BAR)),
                List.of(new ContainerCompatibilityRecord("COMP-H2O", "AQUEOUS", ContainerMaterial.BOROSILICATE_GLASS, ContainerMaterial.POLYPROPYLENE,
                        CompatibilityStatus.COMPATIBLE, null, null, null, "manufacturer bottle profile", "SOURCED_REFERENCE_VALUE")),
                "model-specific bottle documentation");
    }

    private static ContainerProfile hdpeBottle() {
        return new ContainerProfile("CON-HDPE-BOTTLE-500ML", "laboratory-container-reference-v1.0.0",
                ContainerType.BOTTLE, ContainerMaterial.HDPE, ContainerClosureType.SCREW_CAP, ContainerMaterial.HDPE,
                new ContainerGeometry("bottle", true), new NominalCapacity(Volume.of("500", VolumeUnit.MILLILITER)),
                new MaximumWorkingVolume(Volume.of("450", VolumeUnit.MILLILITER)),
                new ContainerTemperatureLimit(Temperature.of("0", TemperatureUnit.CELSIUS), Temperature.of("60", TemperatureUnit.CELSIUS)),
                null,
                List.of(
                        new ContainerCompatibilityRecord("FAMILY-DILUTE-ACID", "AQUEOUS", ContainerMaterial.HDPE, ContainerMaterial.HDPE,
                                CompatibilityStatus.COMPATIBLE_WITH_LIMITS, null,
                                new ContainerTemperatureLimit(Temperature.of("0", TemperatureUnit.CELSIUS), Temperature.of("40", TemperatureUnit.CELSIUS)),
                                null, "supplier chemical compatibility chart record HDPE-acid", "SOURCED_REFERENCE_VALUE"),
                        new ContainerCompatibilityRecord("FAMILY-AROMATIC-SOLVENT", "LIQUID", ContainerMaterial.HDPE, ContainerMaterial.HDPE,
                                CompatibilityStatus.INCOMPATIBLE, null, null, null, "supplier chemical compatibility chart record HDPE-aromatic", "SOURCED_REFERENCE_VALUE")
                ),
                "type taxonomy");
    }
}
