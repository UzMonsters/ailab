package com.ailab.chemistry.infrastructure.persistence.kinetics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.kinetics.ArrheniusParameters;
import com.ailab.chemistry.domain.kinetics.KineticEvidenceStatus;
import com.ailab.chemistry.domain.kinetics.KineticProfile;
import com.ailab.chemistry.domain.kinetics.KineticProfileRepository;
import com.ailab.chemistry.domain.kinetics.KineticProvenance;
import com.ailab.chemistry.domain.kinetics.KineticRateLaw;
import com.ailab.chemistry.domain.kinetics.KineticRateLawTerm;
import com.ailab.chemistry.domain.kinetics.KineticReferenceConditions;
import com.ailab.chemistry.domain.kinetics.RateConstant;
import com.ailab.chemistry.domain.kinetics.RateConstantDimension;
import com.ailab.chemistry.domain.kinetics.ReactionOrder;
import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemoryKineticProfileRepository implements KineticProfileRepository {

    public static final String DATASET_VERSION = "kinetic-reference-v1.0.0";

    private final List<KineticProfile> activeProfiles = List.of(
            new KineticProfile(
                    "KP-ELEM-H-O2-PIRRAGLIA-1989-REC3",
                    "RXN-ELEM-H-O2-PROPAGATION",
                    KineticRateLaw.of(List.of(
                            new KineticRateLawTerm("COMP-RAD-H", MatterState.GAS, ReactionOrder.of(1)),
                            new KineticRateLawTerm("COMP-O2", MatterState.GAS, ReactionOrder.of(1))
                    )),
                    RateConstant.of("73470117.272", RateConstantDimension.SECOND_ORDER),
                    new ArrheniusParameters(
                            new BigDecimal("168017727204.0"),
                            BigDecimal.ZERO,
                            Temperature.of("1050.0", TemperatureUnit.KELVIN),
                            MolarEnergy.of("67.514", MolarEnergyUnit.KILOJOULE_PER_MOLE),
                            Temperature.of("962.0", TemperatureUnit.KELVIN),
                            Temperature.of("1700.0", TemperatureUnit.KELVIN),
                            "STANDARD_ARRHENIUS"
                    ),
                    new KineticReferenceConditions(Temperature.of("1050.0", TemperatureUnit.KELVIN), Pressure.of("0.0413", PressureUnit.BAR), "GAS_PHASE (Ar bath)", null, null, null),
                    KineticEvidenceStatus.EXPERIMENTAL,
                    new KineticProvenance(
                            "NIST-CHEMICAL-KINETICS",
                            "NIST Chemical Kinetics Database record 1989PIR/MIC282:3",
                            "Pirraglia et al., J. Phys. Chem. 93, 282 (1989)",
                            "1989PIR/MIC282:3",
                            "A shock tube study of the reaction H + O2 -> OH + O",
                            "Pirraglia, P. V.; Michael, J. V.; Sutherland, J. W.; Klemm, R. B.",
                            "J. Phys. Chem.",
                            1989,
                            "282-291",
                            "https://kinetics.nist.gov/kinetics/Detail?id=1989PIR/MIC282:3",
                            "EXPERIMENTAL",
                            "SHOCK_TUBE",
                            "+/-15%",
                            "2.79E-10",
                            "cm3 molecule-1 s-1",
                            "1.22E-13",
                            "cm3 molecule-1 s-1",
                            "6.02214076E20"
                    )
            ),
            new KineticProfile(
                    "KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1",
                    "RXN-ELEM-CO-OH-PROPAGATION",
                    KineticRateLaw.of(List.of(
                            new KineticRateLawTerm("COMP-CO", MatterState.GAS, ReactionOrder.of(1)),
                            new KineticRateLawTerm("COMP-RAD-OH", MatterState.GAS, ReactionOrder.of(1))
                    )),
                    RateConstant.of("190356900.0", RateConstantDimension.SECOND_ORDER),
                    new ArrheniusParameters(
                            new BigDecimal("2119793547.52"),
                            BigDecimal.ZERO,
                            Temperature.of("1090.0", TemperatureUnit.KELVIN),
                            MolarEnergy.of("21.867", MolarEnergyUnit.KILOJOULE_PER_MOLE),
                            Temperature.of("1090.0", TemperatureUnit.KELVIN),
                            Temperature.of("2370.0", TemperatureUnit.KELVIN),
                            "STANDARD_ARRHENIUS"
                    ),
                    new KineticReferenceConditions(Temperature.of("1090.0", TemperatureUnit.KELVIN), Pressure.of("0.8300", PressureUnit.BAR), "GAS_PHASE (Ar bath)", null, null, null),
                    KineticEvidenceStatus.EXPERIMENTAL,
                    new KineticProvenance(
                            "NIST-CHEMICAL-KINETICS",
                            "NIST Chemical Kinetics Database record 1994WOO/HAN741-748:1",
                            "Wooldridge et al., Int. J. Chem. Kinet. 26, 741 (1994)",
                            "1994WOO/HAN741-748:1",
                            "A shock tube study of the reaction CO + OH -> CO2 + H",
                            "Wooldridge, M. S.; Hanson, R. K.; Bowman, C. T.",
                            "Int. J. Chem. Kinet.",
                            1994,
                            "741-748",
                            "https://kinetics.nist.gov/kinetics/Detail?id=1994WOO/HAN741-748:1",
                            "DIRECT_ABSOLUTE_EXPERIMENTAL_VALUE",
                            "SHOCK_TUBE",
                            "+/-10%",
                            "3.52E-12",
                            "cm3 molecule-1 s-1",
                            "3.16E-13",
                            "cm3 molecule-1 s-1",
                            "6.02214076E20"
                    )
            )
    );

    @Override
    public Optional<KineticProfile> findByProfileId(String profileId) {
        return activeProfiles.stream().filter(p -> p.profileId().equalsIgnoreCase(profileId)).findFirst();
    }

    @Override
    public List<KineticProfile> findByReactionCode(String reactionCode) {
        return activeProfiles.stream().filter(p -> p.reactionCode().equalsIgnoreCase(reactionCode)).collect(Collectors.toList());
    }

    @Override
    public List<KineticProfile> findAll() {
        return activeProfiles;
    }
}
