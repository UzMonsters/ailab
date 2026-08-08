package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.PhValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class AcidBaseEquilibriumResult {

    private final AcidBaseSystemType systemType;
    private final PhValue ph;
    private final PhValue poh;
    private final HydroniumConcentration hydroniumConcentration;
    private final HydroxideConcentration hydroxideConcentration;
    private final BigDecimal kw;
    private final BigDecimal pKw;
    private final BigDecimal kActive; // Ka or Kb if applicable
    private final AcidBaseCalculationMethod calculationMethod;
    private final List<EquilibriumAssumption> assumptions;
    private final EquilibriumResidual residual;
    private final SolverStatus solverStatus;

    public AcidBaseEquilibriumResult(
            AcidBaseSystemType systemType,
            PhValue ph,
            PhValue poh,
            HydroniumConcentration hydroniumConcentration,
            HydroxideConcentration hydroxideConcentration,
            BigDecimal kw,
            BigDecimal pKw,
            BigDecimal kActive,
            AcidBaseCalculationMethod calculationMethod,
            List<EquilibriumAssumption> assumptions,
            EquilibriumResidual residual,
            SolverStatus solverStatus) {
        this.systemType = Objects.requireNonNull(systemType);
        this.ph = Objects.requireNonNull(ph);
        this.poh = Objects.requireNonNull(poh);
        this.hydroniumConcentration = Objects.requireNonNull(hydroniumConcentration);
        this.hydroxideConcentration = Objects.requireNonNull(hydroxideConcentration);
        this.kw = Objects.requireNonNull(kw);
        this.pKw = Objects.requireNonNull(pKw);
        this.kActive = kActive;
        this.calculationMethod = Objects.requireNonNull(calculationMethod);
        this.assumptions = List.copyOf(Objects.requireNonNull(assumptions));
        this.residual = Objects.requireNonNull(residual);
        this.solverStatus = Objects.requireNonNull(solverStatus);
    }

    public AcidBaseSystemType getSystemType() {
        return systemType;
    }

    public PhValue getPh() {
        return ph;
    }

    public PhValue getPoh() {
        return poh;
    }

    public HydroniumConcentration getHydroniumConcentration() {
        return hydroniumConcentration;
    }

    public HydroxideConcentration getHydroxideConcentration() {
        return hydroxideConcentration;
    }

    public BigDecimal getKw() {
        return kw;
    }

    public BigDecimal getPKw() {
        return pKw;
    }

    public Optional<BigDecimal> getKActive() {
        return Optional.ofNullable(kActive);
    }

    public AcidBaseCalculationMethod getCalculationMethod() {
        return calculationMethod;
    }

    public List<EquilibriumAssumption> getAssumptions() {
        return assumptions;
    }

    public EquilibriumResidual getResidual() {
        return residual;
    }

    public SolverStatus getSolverStatus() {
        return solverStatus;
    }
}
