package com.ailab.chemistry.laboratory;

import com.ailab.chemistry.domain.container.ContainerErrorCode;
import com.ailab.chemistry.domain.equipment.EquipmentErrorCode;
import com.ailab.chemistry.domain.labenvironment.EnvironmentErrorCode;
import com.ailab.chemistry.domain.laboratory.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LaboratoryOperationSuitabilityCalculatorTest {
    private final LaboratoryOperationSuitabilityCalculator calculator = new LaboratoryOperationSuitabilityCalculator();

    @Test
    void combinedOperationAggregatesSelectedProfilesWarningsAssumptionsAndProvenance() {
        LaboratoryOperationSuitabilityResult result = calculator.evaluate(new LaboratoryOperationSuitabilityRequest(
                "aqueous room-temperature measurement",
                SupportFixtures.suitableEquipmentResult(),
                SupportFixtures.suitableContainerResult(),
                SupportFixtures.suitableEnvironmentResult(),
                List.of("hazard data descriptive only; no automatic safety decision"),
                List.of("laboratory-equipment-reference-v1.0.0", "laboratory-container-reference-v1.0.0")));

        assertThat(result.status()).isEqualTo(LaboratoryOperationStatus.SUITABLE);
        assertThat(result.selectedEquipmentProfileIds()).contains("BAL-ANALYTICAL-120G");
        assertThat(result.selectedContainerProfileId()).isEqualTo("CON-BORO-BEAKER-100ML");
        assertThat(result.assumptions()).contains("hazard data descriptive only; no automatic safety decision");
        assertThat(result.provenance()).contains("laboratory-equipment-reference-v1.0.0");
    }

    @Test
    void combinedOperationIsBlockedByEquipmentContainerAndEnvironmentViolations() {
        LaboratoryOperationSuitabilityResult result = calculator.evaluate(new LaboratoryOperationSuitabilityRequest(
                "sealed heated incompatible operation",
                SupportFixtures.unsuitableEquipmentResult(EquipmentErrorCode.EXPIRED_CALIBRATION),
                SupportFixtures.unsuitableContainerResult(ContainerErrorCode.INCOMPATIBLE_MATERIAL),
                SupportFixtures.unsuitableEnvironmentResult(EnvironmentErrorCode.VENTILATION_MODE_MISMATCH),
                List.of(),
                List.of()));

        assertThat(result.status()).isEqualTo(LaboratoryOperationStatus.UNSUITABLE);
        assertThat(result.violations()).extracting(LaboratoryOperationViolation::code)
                .contains("EQUIPMENT:EXPIRED_CALIBRATION", "CONTAINER:INCOMPATIBLE_MATERIAL", "ENVIRONMENT:VENTILATION_MODE_MISMATCH");
    }
}
