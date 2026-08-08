package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.compound.MolarMass;
import com.ailab.chemistry.domain.compound.MolarMassCalculationBasis;
import com.ailab.chemistry.domain.compound.MolarMassKind;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.reaction.*;
import com.ailab.chemistry.domain.stoichiometry.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StoichiometryServiceImpl implements StoichiometryService {

    private final ReactionRepository reactionRepository;
    private final CompoundCatalogService compoundCatalogService;
    private final StoichiometryCalculator calculator;

    @Autowired
    public StoichiometryServiceImpl(ReactionRepository reactionRepository, CompoundCatalogService compoundCatalogService) {
        this.reactionRepository = Objects.requireNonNull(reactionRepository, "ReactionRepository must not be null");
        this.compoundCatalogService = Objects.requireNonNull(compoundCatalogService, "CompoundCatalogService must not be null");
        this.calculator = new StoichiometryCalculator();
    }

    @Override
    public AmountOfSubstance convertMassToMoles(String compoundCode, Mass mass) {
        MolarMass molarMass = getMolarMassForCompound(compoundCode);
        return calculator.convertMassToMoles(mass, molarMass);
    }

    @Override
    public Mass convertMolesToMass(String compoundCode, AmountOfSubstance amount) {
        MolarMass molarMass = getMolarMassForCompound(compoundCode);
        return calculator.convertMolesToMass(amount, molarMass);
    }

    @Override
    public StoichiometryCalculationResult calculateFromReactant(String reactionCode, String reactantCompoundCode, StoichiometricQuantity quantity) {
        Objects.requireNonNull(reactionCode, "Reaction code must not be null");
        Objects.requireNonNull(reactantCompoundCode, "Reactant compound code must not be null");
        Objects.requireNonNull(quantity, "Quantity must not be null");

        Reaction reaction = reactionRepository.findByCode(new ReactionCode(reactionCode))
                .orElseThrow(() -> new StoichiometryException(StoichiometryErrorCode.REACTION_NOT_FOUND, "Reaction not found: " + reactionCode));

        ReactionTerm sourceTerm = reaction.getReactants().stream()
                .filter(t -> t.getCompoundCode().equalsIgnoreCase(reactantCompoundCode))
                .findFirst()
                .orElseThrow(() -> new StoichiometryException(StoichiometryErrorCode.COMPOUND_NOT_IN_REACTION, "Compound " + reactantCompoundCode + " is not a reactant in reaction " + reactionCode));

        MolarMass sourceMM = getMolarMassForCompound(sourceTerm.getCompoundCode());
        AmountOfSubstance pureSourceMoles = quantity.toPureMoles(sourceMM);
        ReactionExtent extent = ReactionExtent.calculate(pureSourceMoles, sourceTerm.getCoefficient());

        Map<String, AmountOfSubstance> reqMoles = new LinkedHashMap<>();
        Map<String, Mass> reqMasses = new LinkedHashMap<>();
        for (ReactionTerm rTerm : reaction.getReactants()) {
            MolarMass rMM = getMolarMassForCompound(rTerm.getCompoundCode());
            BigDecimal molesVal = extent.getValueInMoles().multiply(new BigDecimal(rTerm.getCoefficient()), ScientificMath.CALCULATION_CONTEXT);
            AmountOfSubstance molesAmt = AmountOfSubstance.of(molesVal, AmountOfSubstanceUnit.MOLE);
            Mass massAmt = calculator.convertMolesToMass(molesAmt, rMM);
            reqMoles.put(rTerm.getCompoundCode(), molesAmt);
            reqMasses.put(rTerm.getCompoundCode(), massAmt);
        }

        Map<String, AmountOfSubstance> expProductMoles = new LinkedHashMap<>();
        Map<String, Mass> expProductMasses = new LinkedHashMap<>();
        for (ReactionTerm pTerm : reaction.getProducts()) {
            MolarMass pMM = getMolarMassForCompound(pTerm.getCompoundCode());
            BigDecimal molesVal = extent.getValueInMoles().multiply(new BigDecimal(pTerm.getCoefficient()), ScientificMath.CALCULATION_CONTEXT);
            AmountOfSubstance molesAmt = AmountOfSubstance.of(molesVal, AmountOfSubstanceUnit.MOLE);
            Mass massAmt = calculator.convertMolesToMass(molesAmt, pMM);
            expProductMoles.put(pTerm.getCompoundCode(), molesAmt);
            expProductMasses.put(pTerm.getCompoundCode(), massAmt);
        }

        return new StoichiometryCalculationResult(
                reaction.getReactionCode().getValue(),
                sourceTerm.getCompoundCode(),
                quantity,
                pureSourceMoles,
                extent,
                reqMoles,
                reqMasses,
                expProductMoles,
                expProductMasses
        );
    }

    @Override
    public LimitingReagentResult determineLimitingReagent(String reactionCode, List<ReactionParticipantQuantity> reactants) {
        Objects.requireNonNull(reactionCode, "Reaction code must not be null");
        Objects.requireNonNull(reactants, "Reactants list must not be null");

        Reaction reaction = reactionRepository.findByCode(new ReactionCode(reactionCode))
                .orElseThrow(() -> new StoichiometryException(StoichiometryErrorCode.REACTION_NOT_FOUND, "Reaction not found: " + reactionCode));

        Map<String, StoichiometricQuantity> quantitiesMap = new LinkedHashMap<>();
        Map<String, MolarMass> molarMassesMap = new LinkedHashMap<>();

        for (ReactionParticipantQuantity rp : reactants) {
            quantitiesMap.put(rp.getCompoundCode(), rp.getQuantity());
            molarMassesMap.put(rp.getCompoundCode(), getMolarMassForCompound(rp.getCompoundCode()));
        }

        return calculator.determineLimitingReagent(reaction, quantitiesMap, molarMassesMap);
    }

    @Override
    public TheoreticalYieldResult calculateTheoreticalYield(String reactionCode, List<ReactionParticipantQuantity> reactants, String productCompoundCode) {
        Objects.requireNonNull(reactionCode, "Reaction code must not be null");
        Objects.requireNonNull(reactants, "Reactants list must not be null");
        Objects.requireNonNull(productCompoundCode, "Product compound code must not be null");

        Reaction reaction = reactionRepository.findByCode(new ReactionCode(reactionCode))
                .orElseThrow(() -> new StoichiometryException(StoichiometryErrorCode.REACTION_NOT_FOUND, "Reaction not found: " + reactionCode));

        Map<String, StoichiometricQuantity> quantitiesMap = new LinkedHashMap<>();
        Map<String, MolarMass> molarMassesMap = new LinkedHashMap<>();

        for (ReactionParticipantQuantity rp : reactants) {
            quantitiesMap.put(rp.getCompoundCode(), rp.getQuantity());
            molarMassesMap.put(rp.getCompoundCode(), getMolarMassForCompound(rp.getCompoundCode()));
        }

        for (ReactionTerm pTerm : reaction.getProducts()) {
            if (!molarMassesMap.containsKey(pTerm.getCompoundCode())) {
                molarMassesMap.put(pTerm.getCompoundCode(), getMolarMassForCompound(pTerm.getCompoundCode()));
            }
        }

        return calculator.calculateTheoreticalYield(reaction, quantitiesMap, molarMassesMap, productCompoundCode);
    }

    @Override
    public ActualYieldResult evaluateActualYield(TheoreticalYieldResult theoreticalYield, Mass actualYield) {
        Objects.requireNonNull(theoreticalYield, "TheoreticalYieldResult must not be null");
        Objects.requireNonNull(actualYield, "Actual yield mass must not be null");

        MolarMass prodMM = getMolarMassForCompound(theoreticalYield.getProductCompoundCode());
        return calculator.evaluateActualYield(theoreticalYield, actualYield, prodMM);
    }

    private MolarMass getMolarMassForCompound(String compoundCode) {
        CompoundDetails details = compoundCatalogService.getByCode(compoundCode);
        BigDecimal repVal = details.getMolarMassValue();
        BigDecimal lower = details.getMolarMassLowerBound();
        BigDecimal upper = details.getMolarMassUpperBound();

        MolarMassKind kind;
        try {
            kind = MolarMassKind.valueOf(details.getMolarMassKind());
        } catch (Exception e) {
            kind = MolarMassKind.EXACT_FROM_FIXED_VALUES;
        }

        MolarMassCalculationBasis basis = new MolarMassCalculationBasis("IUPAC-2021", "1.0");

        if (lower != null && upper != null) {
            return MolarMass.interval(repVal, lower, upper, basis);
        } else {
            return MolarMass.exact(repVal, basis);
        }
    }
}
