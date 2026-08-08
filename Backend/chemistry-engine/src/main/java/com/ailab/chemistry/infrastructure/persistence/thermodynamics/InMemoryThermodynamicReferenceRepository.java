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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemoryThermodynamicReferenceRepository implements ThermodynamicReferenceRepository {
    public static final String DATASET_VERSION = "thermodynamic-reference-v1.0.0";
    private static final ThermodynamicDatasetVersion VERSION = new ThermodynamicDatasetVersion(DATASET_VERSION);
    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final Pressure P1BAR = Pressure.of("1.000", PressureUnit.BAR);
    private static final ThermodynamicProvenance NIST = new ThermodynamicProvenance("NIST-WEBBOOK",
            "NIST Chemistry WebBook thermochemistry tables and CODATA reference-state conventions",
            "NIST WebBook values are cited as a minimal educational subset; verify licensing before redistributing as a data table.");

    private final List<ThermodynamicProfile> profiles;

    public InMemoryThermodynamicReferenceRepository() {
        profiles = List.of(
                profile("COMP-H2", gasRef("0", "0", "130.68", "28.84")),
                profile("COMP-O2", gasRef("0", "0", "205.15", "29.36")),
                profile("COMP-H2O", records(
                        rec(MatterState.LIQUID, StandardStateConvention.PURE_SUBSTANCE_STANDARD_STATE, "-285.830", "-237.129", "69.91", "75.38", ThermodynamicEvidenceStatus.EVALUATED),
                        rec(MatterState.GAS, StandardStateConvention.IDEAL_GAS_STANDARD_STATE, "-241.826", "-228.572", "188.83", "33.58", ThermodynamicEvidenceStatus.EVALUATED))),
                profile("COMP-CO2", gasRef("-393.509", "-394.359", "213.79", "37.135")),
                profile("COMP-CO", gasRef("-110.525", "-137.168", "197.66", "29.14")),
                profile("COMP-CH4", gasRef("-74.873", "-50.80", "186.25", "35.69")),
                profile("COMP-HCL", gasRef("-92.307", "-95.30", "186.69", "29.10")),
                profile("COMP-NH3", gasRef("-46.11", "-16.45", "192.77", "35.06")),
                profile("COMP-H2O2", liquidRef("-187.78", "-120.35", "109.6", "89.1")),
                profile("COMP-ETHANOL", liquidRef("-277.69", "-174.78", "160.7", "112.4")),
                profile("COMP-CH3COOH", liquidRef("-484.5", "-389.9", "159.8", "123.1")),
                profile("COMP-NAOH", solidRef("-425.6", "-379.5", "64.5", "59.5")),
                profile("COMP-NACL", solidRef("-411.12", "-384.14", "72.11", "50.5")),
                profile("COMP-CACO3", solidRef("-1206.9", "-1128.8", "92.9", "81.9")),
                profile("COMP-NAHCO3", solidRef("-947.7", "-851.0", "102.1", "87.6")),
                profile("COMP-NA2CO3", solidRef("-1130.7", "-1044.4", "135.0", "112.3"))
        );
        profiles.forEach(ThermodynamicProfile::validateNoDuplicateRecords);
    }

    @Override
    public Optional<ThermodynamicProfile> findProfile(String compoundCode) {
        return profiles.stream()
                .filter(profile -> profile.compoundCode().equalsIgnoreCase(compoundCode))
                .findFirst();
    }

    @Override
    public List<ThermodynamicProfile> findAllProfiles() {
        return profiles;
    }

    private static ThermodynamicProfile profile(String compoundCode, List<ThermodynamicPropertyRecord> records) {
        return new ThermodynamicProfile(compoundCode, VERSION, records);
    }

    private static List<ThermodynamicPropertyRecord> gasRef(String enthalpy, String gibbs, String entropy, String cp) {
        return records(rec(MatterState.GAS, StandardStateConvention.IDEAL_GAS_STANDARD_STATE, enthalpy, gibbs, entropy, cp,
                "0".equals(enthalpy) && "0".equals(gibbs) ? ThermodynamicEvidenceStatus.REFERENCE_STATE_DEFINED : ThermodynamicEvidenceStatus.EVALUATED));
    }

    private static List<ThermodynamicPropertyRecord> liquidRef(String enthalpy, String gibbs, String entropy, String cp) {
        return records(rec(MatterState.LIQUID, StandardStateConvention.PURE_SUBSTANCE_STANDARD_STATE, enthalpy, gibbs, entropy, cp, ThermodynamicEvidenceStatus.EVALUATED));
    }

    private static List<ThermodynamicPropertyRecord> solidRef(String enthalpy, String gibbs, String entropy, String cp) {
        return records(rec(MatterState.SOLID, StandardStateConvention.SOLID_REFERENCE_STATE, enthalpy, gibbs, entropy, cp, ThermodynamicEvidenceStatus.EVALUATED));
    }

    @SafeVarargs
    private static List<ThermodynamicPropertyRecord> records(List<ThermodynamicPropertyRecord>... groups) {
        List<ThermodynamicPropertyRecord> all = new ArrayList<>();
        for (List<ThermodynamicPropertyRecord> group : groups) {
            all.addAll(group);
        }
        return List.copyOf(all);
    }

    private static List<ThermodynamicPropertyRecord> rec(MatterState state, StandardStateConvention convention, String enthalpy,
                                                         String gibbs, String entropy, String cp, ThermodynamicEvidenceStatus formationEvidence) {
        var conditions = new ThermodynamicReferenceConditions(T25, P1BAR, state, convention);
        return List.of(
                new ThermodynamicPropertyRecord(ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                        MolarEnergy.of(enthalpy, MolarEnergyUnit.KILOJOULE_PER_MOLE), null, null, conditions, formationEvidence, NIST),
                new ThermodynamicPropertyRecord(ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION,
                        MolarEnergy.of(gibbs, MolarEnergyUnit.KILOJOULE_PER_MOLE), null, null, conditions, formationEvidence, NIST),
                new ThermodynamicPropertyRecord(ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY,
                        null, MolarEntropy.of(entropy, MolarEntropyUnit.JOULE_PER_MOLE_KELVIN), null, conditions, ThermodynamicEvidenceStatus.EVALUATED, NIST),
                new ThermodynamicPropertyRecord(ThermodynamicPropertyType.MOLAR_HEAT_CAPACITY,
                        null, null, MolarHeatCapacity.of(cp, MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN), conditions, ThermodynamicEvidenceStatus.EVALUATED, NIST)
        );
    }
}
