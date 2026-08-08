package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.PhValue;
import com.ailab.chemistry.domain.measurement.Volume;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TitrationPointResult {
    private final Volume addedTitrantVolume;
    private final Volume totalVolume;
    private final PhValue ph;
    private final PhValue poh;
    private final TitrationRegion region;
    private final TitrationCalculationMethod calculationMethod;
    private final List<TitrationAssumption> assumptions;
    private final Map<String, BigDecimal> constants;
    private final TitrationResidual residual;
    private final TitrationSolverStatus solverStatus;
    private final MolarConcentration analyticalConcentration;

    public TitrationPointResult(
            Volume addedTitrantVolume,
            Volume totalVolume,
            PhValue ph,
            PhValue poh,
            TitrationRegion region,
            TitrationCalculationMethod calculationMethod,
            List<TitrationAssumption> assumptions,
            Map<String, BigDecimal> constants,
            TitrationResidual residual,
            TitrationSolverStatus solverStatus,
            MolarConcentration analyticalConcentration) {
        this.addedTitrantVolume = Objects.requireNonNull(addedTitrantVolume);
        this.totalVolume = Objects.requireNonNull(totalVolume);
        this.ph = Objects.requireNonNull(ph);
        this.poh = Objects.requireNonNull(poh);
        this.region = Objects.requireNonNull(region);
        this.calculationMethod = Objects.requireNonNull(calculationMethod);
        this.assumptions = List.copyOf(Objects.requireNonNull(assumptions));
        this.constants = Map.copyOf(Objects.requireNonNull(constants));
        this.residual = Objects.requireNonNull(residual);
        this.solverStatus = Objects.requireNonNull(solverStatus);
        this.analyticalConcentration = Objects.requireNonNull(analyticalConcentration);
    }

    public Volume getAddedTitrantVolume() { return addedTitrantVolume; }
    public Volume getTotalVolume() { return totalVolume; }
    public PhValue getPh() { return ph; }
    public PhValue getPoh() { return poh; }
    public TitrationRegion getRegion() { return region; }
    public TitrationCalculationMethod getCalculationMethod() { return calculationMethod; }
    public List<TitrationAssumption> getAssumptions() { return assumptions; }
    public Map<String, BigDecimal> getConstants() { return constants; }
    public TitrationResidual getResidual() { return residual; }
    public TitrationSolverStatus getSolverStatus() { return solverStatus; }
    public MolarConcentration getAnalyticalConcentration() { return analyticalConcentration; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TitrationPointResult that)) return false;
        return addedTitrantVolume.equals(that.addedTitrantVolume)
                && totalVolume.equals(that.totalVolume)
                && ph.equals(that.ph)
                && poh.equals(that.poh)
                && region == that.region
                && calculationMethod == that.calculationMethod
                && assumptions.equals(that.assumptions)
                && constants.equals(that.constants)
                && residual.equals(that.residual)
                && solverStatus == that.solverStatus
                && analyticalConcentration.equals(that.analyticalConcentration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addedTitrantVolume, totalVolume, ph, poh, region, calculationMethod, assumptions, constants, residual, solverStatus, analyticalConcentration);
    }
}
