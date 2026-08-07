package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.compound.MolarMass;
import com.ailab.chemistry.domain.measurement.*;

import java.util.Objects;
import java.util.Optional;

public final class ConcentrationConversionResult {

    private final String soluteCompoundCode;
    private final MolarConcentration molarity;
    private final MolarConcentration molarityLowerBound;
    private final MolarConcentration molarityUpperBound;
    private final Molality molality;
    private final MassConcentration massConcentration;
    private final MassFraction massFraction;
    private final MoleFraction moleFraction;
    private final Density densityUsed;
    private final MolarMass molarMassUsed;

    public ConcentrationConversionResult(
            String soluteCompoundCode,
            MolarConcentration molarity,
            MolarConcentration molarityLowerBound,
            MolarConcentration molarityUpperBound,
            Molality molality,
            MassConcentration massConcentration,
            MassFraction massFraction,
            MoleFraction moleFraction,
            Density densityUsed,
            MolarMass molarMassUsed) {
        this.soluteCompoundCode = Objects.requireNonNull(soluteCompoundCode, "Solute compound code must not be null");
        this.molarity = molarity;
        this.molarityLowerBound = molarityLowerBound;
        this.molarityUpperBound = molarityUpperBound;
        this.molality = molality;
        this.massConcentration = massConcentration;
        this.massFraction = massFraction;
        this.moleFraction = moleFraction;
        this.densityUsed = densityUsed;
        this.molarMassUsed = Objects.requireNonNull(molarMassUsed, "MolarMass must not be null");
    }

    public String getSoluteCompoundCode() {
        return soluteCompoundCode;
    }

    public Optional<MolarConcentration> getMolarity() {
        return Optional.ofNullable(molarity);
    }

    public Optional<MolarConcentration> getMolarityLowerBound() {
        return Optional.ofNullable(molarityLowerBound);
    }

    public Optional<MolarConcentration> getMolarityUpperBound() {
        return Optional.ofNullable(molarityUpperBound);
    }

    public Optional<Molality> getMolality() {
        return Optional.ofNullable(molality);
    }

    public Optional<MassConcentration> getMassConcentration() {
        return Optional.ofNullable(massConcentration);
    }

    public Optional<MassFraction> getMassFraction() {
        return Optional.ofNullable(massFraction);
    }

    public Optional<MoleFraction> getMoleFraction() {
        return Optional.ofNullable(moleFraction);
    }

    public Optional<Density> getDensityUsed() {
        return Optional.ofNullable(densityUsed);
    }

    public MolarMass getMolarMassUsed() {
        return molarMassUsed;
    }

    @Override
    public String toString() {
        return "ConcentrationConversionResult{" + soluteCompoundCode + ": molarity=" + molarity + ", massConc=" + massConcentration + '}';
    }
}
