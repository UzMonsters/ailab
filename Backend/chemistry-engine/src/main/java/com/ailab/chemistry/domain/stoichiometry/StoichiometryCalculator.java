package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.compound.MolarMass;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.reaction.Reaction;
import com.ailab.chemistry.domain.reaction.ReactionSide;
import com.ailab.chemistry.domain.reaction.ReactionTerm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public final class StoichiometryCalculator {

    public AmountOfSubstance convertMassToMoles(Mass mass, MolarMass molarMass) {
        Objects.requireNonNull(mass, "Mass must not be null");
        Objects.requireNonNull(molarMass, "MolarMass must not be null");
        BigDecimal g = mass.in(MassUnit.GRAM);
        BigDecimal repMolarMass = molarMass.getRepresentativeValue();
        if (repMolarMass.compareTo(BigDecimal.ZERO) <= 0) {
            throw new StoichiometryException(StoichiometryErrorCode.CALCULATION_ERROR, "Molar mass must be strictly positive: " + repMolarMass);
        }
        BigDecimal moles = g.divide(repMolarMass, ScientificMath.CALCULATION_CONTEXT);
        return AmountOfSubstance.of(moles, AmountOfSubstanceUnit.MOLE);
    }

    public Mass convertMolesToMass(AmountOfSubstance amount, MolarMass molarMass) {
        Objects.requireNonNull(amount, "AmountOfSubstance must not be null");
        Objects.requireNonNull(molarMass, "MolarMass must not be null");
        BigDecimal mol = amount.in(AmountOfSubstanceUnit.MOLE);
        BigDecimal repMolarMass = molarMass.getRepresentativeValue();
        BigDecimal massInGrams = mol.multiply(repMolarMass, ScientificMath.CALCULATION_CONTEXT);
        return Mass.of(massInGrams, MassUnit.GRAM);
    }

    public LimitingReagentResult determineLimitingReagent(
            Reaction reaction,
            Map<String, StoichiometricQuantity> reactantQuantities,
            Map<String, MolarMass> molarMasses) {
        Objects.requireNonNull(reaction, "Reaction must not be null");
        Objects.requireNonNull(reactantQuantities, "Reactant quantities map must not be null");
        Objects.requireNonNull(molarMasses, "MolarMasses map must not be null");

        List<ReactionTerm> reactants = reaction.getReactants();
        if (reactants.isEmpty()) {
            throw new StoichiometryException(StoichiometryErrorCode.MISSING_REACTANTS, "Reaction has no reactants");
        }

        Map<String, ReactionExtent> extents = new LinkedHashMap<>();
        BigDecimal minExtentVal = null;

        for (ReactionTerm term : reactants) {
            String code = term.getCompoundCode();
            StoichiometricQuantity q = reactantQuantities.get(code);
            if (q == null) {
                // Try case-insensitive lookup
                q = reactantQuantities.entrySet().stream()
                        .filter(e -> e.getKey().equalsIgnoreCase(code))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElseThrow(() -> new StoichiometryException(StoichiometryErrorCode.MISSING_REACTANTS, "Missing reactant quantity for compound: " + code));
            }
            MolarMass mm = molarMasses.get(code);
            if (mm == null) {
                mm = molarMasses.entrySet().stream()
                        .filter(e -> e.getKey().equalsIgnoreCase(code))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElseThrow(() -> new StoichiometryException(StoichiometryErrorCode.COMPOUND_NOT_FOUND, "Missing molar mass for compound: " + code));
            }

            AmountOfSubstance pureMoles = q.toPureMoles(mm);
            AmountOfSubstance lowMoles = q.toPureMolesLowerBound(mm);
            AmountOfSubstance uppMoles = q.toPureMolesUpperBound(mm);

            ReactionExtent extent = ReactionExtent.calculate(pureMoles, lowMoles, uppMoles, term.getCoefficient());
            extents.put(code, extent);

            BigDecimal extentVal = extent.getValueInMoles();
            if (minExtentVal == null || extentVal.compareTo(minExtentVal) < 0) {
                minExtentVal = extentVal;
            }
        }

        List<String> limitingCodes = new ArrayList<>();
        for (Map.Entry<String, ReactionExtent> entry : extents.entrySet()) {
            if (entry.getValue().getValueInMoles().compareTo(minExtentVal) == 0) {
                limitingCodes.add(entry.getKey());
            }
        }

        return new LimitingReagentResult(limitingCodes, extents);
    }

    public TheoreticalYieldResult calculateTheoreticalYield(
            Reaction reaction,
            Map<String, StoichiometricQuantity> reactantQuantities,
            Map<String, MolarMass> molarMasses,
            String productCompoundCode) {
        Objects.requireNonNull(reaction, "Reaction must not be null");
        Objects.requireNonNull(reactantQuantities, "Reactant quantities map must not be null");
        Objects.requireNonNull(molarMasses, "MolarMasses map must not be null");
        Objects.requireNonNull(productCompoundCode, "Product compound code must not be null");

        // Verify product exists in reaction
        ReactionTerm productTerm = reaction.getProducts().stream()
                .filter(t -> t.getCompoundCode().equalsIgnoreCase(productCompoundCode))
                .findFirst()
                .orElseThrow(() -> new StoichiometryException(StoichiometryErrorCode.COMPOUND_NOT_IN_REACTION, "Target product compound " + productCompoundCode + " is not a product in reaction " + reaction.getReactionCode()));

        // Check if any product was accidentally supplied as reactant in input
        for (String suppCode : reactantQuantities.keySet()) {
            if (reaction.getProducts().stream().anyMatch(p -> p.getCompoundCode().equalsIgnoreCase(suppCode))) {
                throw new StoichiometryException(StoichiometryErrorCode.PRODUCT_SUPPLIED_AS_REACTANT, "Product compound " + suppCode + " cannot be supplied as an input reactant quantity");
            }
        }

        LimitingReagentResult limitingResult = determineLimitingReagent(reaction, reactantQuantities, molarMasses);
        ReactionExtent limitingExtent = limitingResult.getLimitingExtent();

        // 1. Calculate theoretical yield for target product
        MolarMass prodMM = molarMasses.get(productTerm.getCompoundCode());
        if (prodMM == null) {
            prodMM = molarMasses.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(productTerm.getCompoundCode()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElseThrow(() -> new StoichiometryException(StoichiometryErrorCode.COMPOUND_NOT_FOUND, "Molar mass not found for product " + productTerm.getCompoundCode()));
        }

        BigInteger prodCoeff = productTerm.getCoefficient();
        BigDecimal prodCoeffDec = new BigDecimal(prodCoeff);

        BigDecimal theoMolesVal = limitingExtent.getValueInMoles().multiply(prodCoeffDec, ScientificMath.CALCULATION_CONTEXT);
        AmountOfSubstance theoMoles = AmountOfSubstance.of(theoMolesVal, AmountOfSubstanceUnit.MOLE);

        AmountOfSubstance theoMolesLow = limitingExtent.getLowerBoundInMoles() != null ?
                limitingExtent.getLowerBoundInMoles().multiply(prodCoeffDec) : null;
        AmountOfSubstance theoMolesUpp = limitingExtent.getUpperBoundInMoles() != null ?
                limitingExtent.getUpperBoundInMoles().multiply(prodCoeffDec) : null;

        Mass theoMass = convertMolesToMass(theoMoles, prodMM);
        Mass theoMassLow = (theoMolesLow != null && prodMM.getLowerBound() != null) ?
                Mass.of(theoMolesLow.in(AmountOfSubstanceUnit.MOLE).multiply(prodMM.getLowerBound(), ScientificMath.CALCULATION_CONTEXT), MassUnit.GRAM) : null;
        Mass theoMassUpp = (theoMolesUpp != null && prodMM.getUpperBound() != null) ?
                Mass.of(theoMolesUpp.in(AmountOfSubstanceUnit.MOLE).multiply(prodMM.getUpperBound(), ScientificMath.CALCULATION_CONTEXT), MassUnit.GRAM) : null;

        // 2. Calculate theoretical mass yields for ALL products
        Map<String, Mass> allProductYields = new LinkedHashMap<>();
        for (ReactionTerm pTerm : reaction.getProducts()) {
            MolarMass pMM = molarMasses.get(pTerm.getCompoundCode());
            if (pMM == null) {
                pMM = molarMasses.entrySet().stream()
                        .filter(e -> e.getKey().equalsIgnoreCase(pTerm.getCompoundCode()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(prodMM);
            }
            BigDecimal pMolesVal = limitingExtent.getValueInMoles().multiply(new BigDecimal(pTerm.getCoefficient()), ScientificMath.CALCULATION_CONTEXT);
            AmountOfSubstance pMoles = AmountOfSubstance.of(pMolesVal, AmountOfSubstanceUnit.MOLE);
            Mass pMass = convertMolesToMass(pMoles, pMM);
            allProductYields.put(pTerm.getCompoundCode(), pMass);
        }

        // 3. Calculate excess reactant results
        List<ExcessReactantResult> excessResults = new ArrayList<>();
        for (ReactionTerm rTerm : reaction.getReactants()) {
            String rCode = rTerm.getCompoundCode();
            StoichiometricQuantity q = reactantQuantities.get(rCode);
            if (q == null) {
                q = reactantQuantities.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(rCode)).map(Map.Entry::getValue).findFirst().orElseThrow();
            }
            MolarMass rMM = molarMasses.get(rCode);
            if (rMM == null) {
                rMM = molarMasses.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(rCode)).map(Map.Entry::getValue).findFirst().orElseThrow();
            }

            AmountOfSubstance initMoles = q.toPureMoles(rMM);
            BigDecimal consMolesVal = limitingExtent.getValueInMoles().multiply(new BigDecimal(rTerm.getCoefficient()), ScientificMath.CALCULATION_CONTEXT);
            AmountOfSubstance consMoles = AmountOfSubstance.of(consMolesVal, AmountOfSubstanceUnit.MOLE);

            BigDecimal initMolesVal = initMoles.in(AmountOfSubstanceUnit.MOLE);
            BigDecimal remMolesVal = initMolesVal.subtract(consMolesVal, ScientificMath.CALCULATION_CONTEXT);

            // Excess quantities must NEVER become negative due to rounding precision
            if (remMolesVal.compareTo(BigDecimal.ZERO) < 0) {
                remMolesVal = BigDecimal.ZERO;
            }

            AmountOfSubstance remMoles = AmountOfSubstance.of(remMolesVal, AmountOfSubstanceUnit.MOLE);
            Mass remMass = convertMolesToMass(remMoles, rMM);

            BigDecimal excessPct;
            if (consMolesVal.compareTo(BigDecimal.ZERO) == 0) {
                excessPct = BigDecimal.ZERO;
            } else {
                excessPct = remMolesVal.divide(consMolesVal, ScientificMath.CALCULATION_CONTEXT).multiply(new BigDecimal("100"), ScientificMath.CALCULATION_CONTEXT);
            }

            excessResults.add(new ExcessReactantResult(rCode, initMoles, consMoles, remMoles, remMass, excessPct));
        }

        return new TheoreticalYieldResult(
                reaction.getReactionCode().getValue(),
                limitingResult,
                productTerm.getCompoundCode(),
                theoMoles,
                theoMolesLow,
                theoMolesUpp,
                theoMass,
                theoMassLow,
                theoMassUpp,
                excessResults,
                allProductYields
        );
    }

    public ActualYieldResult evaluateActualYield(TheoreticalYieldResult theoreticalYield, Mass actualMass, MolarMass productMolarMass) {
        Objects.requireNonNull(theoreticalYield, "TheoreticalYieldResult must not be null");
        Objects.requireNonNull(actualMass, "Actual mass must not be null");
        Objects.requireNonNull(productMolarMass, "Product MolarMass must not be null");

        AmountOfSubstance actualMoles = convertMassToMoles(actualMass, productMolarMass);
        PercentYield pctYield = PercentYield.of(actualMass.in(MassUnit.GRAM), theoreticalYield.getTheoreticalMass().in(MassUnit.GRAM));

        return new ActualYieldResult(theoreticalYield, actualMass, actualMoles, pctYield);
    }
}
