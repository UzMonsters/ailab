package com.ailab.chemistry.domain.reaction;

import com.ailab.chemistry.domain.equation.*;
import com.ailab.chemistry.domain.formula.ChemicalFormula;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.ElementSymbol;
import com.ailab.chemistry.domain.formula.FormulaParser;

import java.math.BigInteger;
import java.util.*;

public final class ReactionValidator {

    private final FormulaParser formulaParser;
    private final EquationBalancer equationBalancer;

    public ReactionValidator(FormulaParser formulaParser, EquationBalancer equationBalancer) {
        this.formulaParser = formulaParser != null ? formulaParser : new DefaultFormulaParser();
        this.equationBalancer = equationBalancer != null ? equationBalancer : new DefaultEquationBalancer();
    }

    public void validateAndVerify(Reaction reaction) {
        Objects.requireNonNull(reaction, "Reaction must not be null");

        List<ReactionTerm> reactants = reaction.getReactants();
        List<ReactionTerm> products = reaction.getProducts();

        if (reactants.isEmpty() || products.isEmpty()) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_TERM, "Reaction must have at least one reactant and one product");
        }

        // 1. Verify all coefficients positive BigInteger
        BigInteger gcd = null;
        for (ReactionTerm term : reaction.getTerms()) {
            if (term.getCoefficient() == null || term.getCoefficient().compareTo(BigInteger.ZERO) <= 0) {
                throw new ReactionException(ReactionErrorCode.REACTION_COEFFICIENT_INVALID, "Coefficient must be positive: " + term.getCoefficient());
            }
            if (gcd == null) {
                gcd = term.getCoefficient();
            } else {
                gcd = gcd.gcd(term.getCoefficient());
            }
        }

        // 2. Verify minimal ratio (GCD = 1)
        if (gcd != null && !gcd.equals(BigInteger.ONE)) {
            throw new ReactionException(ReactionErrorCode.REACTION_COEFFICIENTS_NOT_MINIMAL, "Coefficients must be minimal whole numbers (GCD was " + gcd + ")");
        }

        // 3. Parse formulas and check Atom & Charge Conservation
        Map<ElementSymbol, BigInteger> reactantAtoms = new HashMap<>();
        Map<ElementSymbol, BigInteger> productAtoms = new HashMap<>();
        int reactantNetCharge = 0;
        int productNetCharge = 0;

        List<EquationTerm> eqReactants = new ArrayList<>();
        List<EquationTerm> eqProducts = new ArrayList<>();

        for (ReactionTerm term : reactants) {
            ChemicalFormula parsed = formulaParser.parse(term.getFormula());
            eqReactants.add(new EquationTerm(parsed, term.getCoefficient(), EquationSide.REACTANT));
            for (Map.Entry<ElementSymbol, BigInteger> entry : parsed.getElementCounts().entrySet()) {
                BigInteger count = entry.getValue().multiply(term.getCoefficient());
                reactantAtoms.merge(entry.getKey(), count, BigInteger::add);
            }
            reactantNetCharge += parsed.getNetCharge() * term.getCoefficient().intValueExact();
        }

        for (ReactionTerm term : products) {
            ChemicalFormula parsed = formulaParser.parse(term.getFormula());
            eqProducts.add(new EquationTerm(parsed, term.getCoefficient(), EquationSide.PRODUCT));
            for (Map.Entry<ElementSymbol, BigInteger> entry : parsed.getElementCounts().entrySet()) {
                BigInteger count = entry.getValue().multiply(term.getCoefficient());
                productAtoms.merge(entry.getKey(), count, BigInteger::add);
            }
            productNetCharge += parsed.getNetCharge() * term.getCoefficient().intValueExact();
        }

        if (!reactantAtoms.equals(productAtoms)) {
            throw new ReactionException(ReactionErrorCode.REACTION_UNBALANCED, "Atom balance failed for reaction " + reaction.getReactionCode() + ": reactants=" + reactantAtoms + ", products=" + productAtoms);
        }

        if (reactantNetCharge != productNetCharge) {
            throw new ReactionException(ReactionErrorCode.REACTION_CHARGE_NOT_CONSERVED, "Charge balance failed for reaction " + reaction.getReactionCode() + ": reactantCharge=" + reactantNetCharge + ", productCharge=" + productNetCharge);
        }

        // 4. Verify Equation Balancer produces identical minimal coefficients
        ChemicalEquation unbalanceEq = new ChemicalEquation(
                eqReactants.stream().map(t -> new EquationTerm(t.getFormula(), BigInteger.ONE, EquationSide.REACTANT)).toList(),
                eqProducts.stream().map(t -> new EquationTerm(t.getFormula(), BigInteger.ONE, EquationSide.PRODUCT)).toList()
        );

        BalancedEquation balanced = equationBalancer.balance(unbalanceEq);
        for (int i = 0; i < eqReactants.size(); i++) {
            BigInteger expected = balanced.getBalancedReactants().get(i).getCoefficient();
            BigInteger actual = eqReactants.get(i).getCoefficient();
            if (!expected.equals(actual)) {
                throw new ReactionException(ReactionErrorCode.REACTION_FORMULA_MISMATCH, "Persisted coefficient " + actual + " does not match balancer result " + expected + " for " + eqReactants.get(i).getFormula());
            }
        }
        for (int i = 0; i < eqProducts.size(); i++) {
            BigInteger expected = balanced.getBalancedProducts().get(i).getCoefficient();
            BigInteger actual = eqProducts.get(i).getCoefficient();
            if (!expected.equals(actual)) {
                throw new ReactionException(ReactionErrorCode.REACTION_FORMULA_MISMATCH, "Persisted coefficient " + actual + " does not match balancer result " + expected + " for " + eqProducts.get(i).getFormula());
            }
        }
    }
}
