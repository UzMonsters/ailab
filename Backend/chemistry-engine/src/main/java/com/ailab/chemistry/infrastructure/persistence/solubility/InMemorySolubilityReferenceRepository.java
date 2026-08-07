package com.ailab.chemistry.infrastructure.persistence.solubility;

import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.solubility.*;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemorySolubilityReferenceRepository implements SolubilityReferenceRepository {
    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private final List<SolubilityEquilibrium> equilibria = List.of(
            equilibrium("KSP-CACO3-CALCITE", "COMP-CACO3", "4.20e-9",
                    List.of(term("SPEC-CA-2PLUS", "Ca^2+", 2, 1), term("SPEC-CO3-2MINUS", "CO3^2-", -2, 1))),
            equilibrium("KSP-MG-OH-2", "COMP-MG-OH-2", "5.61e-12",
                    List.of(term("SPEC-MG-2PLUS", "Mg^2+", 2, 1), term("SPEC-OH-MINUS", "OH-", -1, 2))),
            equilibrium("KSP-AL-OH-3", "COMP-AL-OH-3", "3.0e-34",
                    List.of(term("SPEC-AL-3PLUS", "Al^3+", 3, 1), term("SPEC-OH-MINUS", "OH-", -1, 3)))
    );

    @Override
    public Optional<SolubilityEquilibrium> findByCode(SolubilityEquilibriumCode code, Temperature temperature, String solventCode) {
        return equilibria.stream()
                .filter(eq -> eq.code().equals(code))
                .filter(eq -> eq.conditions().temperature().equals(temperature) && eq.conditions().solventCode().equalsIgnoreCase(solventCode))
                .findFirst();
    }

    @Override
    public List<SolubilityEquilibrium> findAll() {
        return equilibria;
    }

    private static SolubilityEquilibrium equilibrium(String code, String compound, String ksp, List<DissolutionTerm> terms) {
        return new SolubilityEquilibrium(new SolubilityEquilibriumCode(code), compound, terms, new SolubilityProduct(new BigDecimal(ksp)),
                new SolubilityReferenceConditions(T25, "COMP-H2O", "dimensionless activities relative to c0=1 mol/L"),
                new SolubilityDatasetVersion("solubility-ksp-v1.0.0"),
                new SolubilityProvenance("CRC-HANDBOOK-104",
                        "CRC Handbook of Chemistry and Physics, 104th Edition, Section 8, solubility products at 25 C",
                        "CRC values are copyrighted; reuse is limited to a minimal cited educational subset in this project"));
    }

    private static DissolutionTerm term(String code, String formula, int charge, int coefficient) {
        return new DissolutionTerm(code, formula, charge, coefficient);
    }
}
