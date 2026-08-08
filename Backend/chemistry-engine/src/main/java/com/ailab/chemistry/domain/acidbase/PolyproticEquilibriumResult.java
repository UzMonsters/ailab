package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.PhValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PolyproticEquilibriumResult {
    private final PolyproticAcidFamily family;
    private final PolyproticInitialForm initialForm;
    private final PhValue ph;
    private final PhValue poh;
    private final BigDecimal hydroniumConcentration;
    private final BigDecimal hydroxideConcentration;
    private final SpeciesDistribution distribution;
    private final Map<String, BigDecimal> constants;
    private final List<PolyproticAssumption> assumptions;
    private final PolyproticResidual residual;
    private final PolyproticSolverStatus solverStatus;
    private final PolyproticCalculationMethod calculationMethod;

    public PolyproticEquilibriumResult(
            PolyproticAcidFamily family,
            PolyproticInitialForm initialForm,
            PhValue ph,
            PhValue poh,
            BigDecimal hydroniumConcentration,
            BigDecimal hydroxideConcentration,
            SpeciesDistribution distribution,
            Map<String, BigDecimal> constants,
            List<PolyproticAssumption> assumptions,
            PolyproticResidual residual,
            PolyproticSolverStatus solverStatus,
            PolyproticCalculationMethod calculationMethod) {
        this.family = Objects.requireNonNull(family);
        this.initialForm = Objects.requireNonNull(initialForm);
        this.ph = Objects.requireNonNull(ph);
        this.poh = Objects.requireNonNull(poh);
        this.hydroniumConcentration = Objects.requireNonNull(hydroniumConcentration);
        this.hydroxideConcentration = Objects.requireNonNull(hydroxideConcentration);
        this.distribution = Objects.requireNonNull(distribution);
        this.constants = Map.copyOf(Objects.requireNonNull(constants));
        this.assumptions = List.copyOf(Objects.requireNonNull(assumptions));
        this.residual = Objects.requireNonNull(residual);
        this.solverStatus = Objects.requireNonNull(solverStatus);
        this.calculationMethod = Objects.requireNonNull(calculationMethod);
    }

    public PolyproticAcidFamily getFamily() { return family; }
    public PolyproticInitialForm getInitialForm() { return initialForm; }
    public PhValue getPh() { return ph; }
    public PhValue getPoh() { return poh; }
    public BigDecimal getHydroniumConcentration() { return hydroniumConcentration; }
    public BigDecimal getHydroxideConcentration() { return hydroxideConcentration; }
    public SpeciesDistribution getDistribution() { return distribution; }
    public Map<String, BigDecimal> getConstants() { return constants; }
    public List<PolyproticAssumption> getAssumptions() { return assumptions; }
    public PolyproticResidual getResidual() { return residual; }
    public PolyproticSolverStatus getSolverStatus() { return solverStatus; }
    public PolyproticCalculationMethod getCalculationMethod() { return calculationMethod; }
}
