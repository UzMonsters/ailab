package com.ailab.chemistry.domain.equation;

import java.math.BigInteger;
import java.util.*;
import com.ailab.chemistry.domain.equation.exception.*;
import com.ailab.chemistry.domain.formula.ElementSymbol;

public final class DefaultEquationBalancer implements EquationBalancer {

    @Override
    public BalancedEquation balance(ChemicalEquation equation) {
        Objects.requireNonNull(equation, "Equation must not be null");

        List<EquationTerm> reactants = equation.getReactants();
        List<EquationTerm> products = equation.getProducts();

        if (reactants.isEmpty() || products.isEmpty()) {
            throw new InvalidChemicalEquationException("Reactants and products must not be empty", EquationErrorCode.EMPTY_EQUATION);
        }

        // 1. Gather all unique elements
        Set<ElementSymbol> allElements = new HashSet<>();
        for (EquationTerm term : reactants) {
            allElements.addAll(term.getFormula().getElementCounts().keySet());
        }
        for (EquationTerm term : products) {
            allElements.addAll(term.getFormula().getElementCounts().keySet());
        }

        // 2. Validate that every element appears on both sides (except for electrons e- which have no elements)
        for (ElementSymbol sym : allElements) {
            boolean onReactant = reactants.stream().anyMatch(t -> t.getFormula().getElementCounts().containsKey(sym));
            boolean onProduct = products.stream().anyMatch(t -> t.getFormula().getElementCounts().containsKey(sym));
            if (!onReactant || !onProduct) {
                throw new InvalidChemicalEquationException("Element " + sym + " only appears on one side of the equation", EquationErrorCode.UNBALANCEABLE_EQUATION);
            }
        }

        // 3. Collect unique elements list for matrix rows
        List<ElementSymbol> elementList = new ArrayList<>(allElements);
        Collections.sort(elementList, (e1, e2) -> e1.getSymbol().compareTo(e2.getSymbol()));

        // Determine if we need charge conservation
        boolean hasCharge = false;
        for (EquationTerm term : reactants) {
            if (term.getFormula().getNetCharge() != 0 || term.getFormula().isElectron()) {
                hasCharge = true;
                break;
            }
        }
        for (EquationTerm term : products) {
            if (term.getFormula().getNetCharge() != 0 || term.getFormula().isElectron()) {
                hasCharge = true;
                break;
            }
        }

        int numElements = elementList.size();
        int numRows = numElements + (hasCharge ? 1 : 0);
        int numCols = reactants.size() + products.size();

        RationalNumber[][] matrix = new RationalNumber[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                matrix[r][c] = RationalNumber.ZERO;
            }
        }

        // Fill conservation matrix
        // Reactants (positive columns), Products (negative columns)
        int colIdx = 0;
        for (EquationTerm term : reactants) {
            // Elements
            for (int r = 0; r < numElements; r++) {
                ElementSymbol sym = elementList.get(r);
                BigInteger count = term.getFormula().getElementCounts().getOrDefault(sym, BigInteger.ZERO);
                matrix[r][colIdx] = RationalNumber.of(count, BigInteger.ONE);
            }
            // Charge
            if (hasCharge) {
                int ch = term.getFormula().isElectron() ? -1 : term.getFormula().getNetCharge();
                matrix[numRows - 1][colIdx] = RationalNumber.of(BigInteger.valueOf(ch), BigInteger.ONE);
            }
            colIdx++;
        }

        for (EquationTerm term : products) {
            // Elements (negative coefficients)
            for (int r = 0; r < numElements; r++) {
                ElementSymbol sym = elementList.get(r);
                BigInteger count = term.getFormula().getElementCounts().getOrDefault(sym, BigInteger.ZERO);
                matrix[r][colIdx] = RationalNumber.of(count.negate(), BigInteger.ONE);
            }
            // Charge
            if (hasCharge) {
                int ch = term.getFormula().isElectron() ? -1 : term.getFormula().getNetCharge();
                matrix[numRows - 1][colIdx] = RationalNumber.of(BigInteger.valueOf(-ch), BigInteger.ONE);
            }
            colIdx++;
        }

        // 4. Perform Gauss-Jordan row reduction (RREF)
        RationalNumber[][] rrefMatrix = rref(matrix);

        // Find pivots and free variables
        int rows = rrefMatrix.length;
        int cols = rrefMatrix[0].length;
        int[] pivotRowOfCol = new int[cols];
        Arrays.fill(pivotRowOfCol, -1);
        int pivotCount = 0;

        for (int r = 0; r < rows; r++) {
            int lead = -1;
            for (int c = 0; c < cols; c++) {
                if (!rrefMatrix[r][c].isZero()) {
                    lead = c;
                    break;
                }
            }
            if (lead != -1) {
                pivotRowOfCol[lead] = r;
                pivotCount++;
            }
        }

        int nullity = cols - pivotCount;
        if (nullity == 0) {
            throw new InvalidChemicalEquationException("Equation has no non-trivial solution", EquationErrorCode.UNBALANCEABLE_EQUATION);
        }
        if (nullity > 1) {
            throw new InvalidChemicalEquationException("Equation has multiple independent solutions", EquationErrorCode.MULTIPLE_INDEPENDENT_SOLUTIONS);
        }

        // Find the single free column
        int freeCol = -1;
        for (int c = 0; c < cols; c++) {
            if (pivotRowOfCol[c] == -1) {
                freeCol = c;
                break;
            }
        }

        // Set free variable to 1
        RationalNumber[] solution = new RationalNumber[cols];
        solution[freeCol] = RationalNumber.ONE;

        for (int c = 0; c < cols; c++) {
            if (c != freeCol) {
                int r = pivotRowOfCol[c];
                // x[c] + rrefMatrix[r][freeCol] * x[freeCol] = 0  => x[c] = -rrefMatrix[r][freeCol]
                solution[c] = rrefMatrix[r][freeCol].negate();
            }
        }

        // Ensure all solutions have the same sign (if they are all negative, we can negate all of them)
        boolean hasPositive = false;
        boolean hasNegative = false;
        for (RationalNumber val : solution) {
            int comp = val.compareTo(RationalNumber.ZERO);
            if (comp > 0) hasPositive = true;
            if (comp < 0) hasNegative = true;
            if (comp == 0) {
                throw new InvalidChemicalEquationException("Equation has coefficient zero in balancing", EquationErrorCode.UNBALANCEABLE_EQUATION);
            }
        }

        if (hasPositive && hasNegative) {
            throw new InvalidChemicalEquationException("Equation cannot be balanced with all positive coefficients", EquationErrorCode.UNBALANCEABLE_EQUATION);
        }

        if (hasNegative) {
            for (int i = 0; i < cols; i++) {
                solution[i] = solution[i].negate();
            }
        }

        // 5. Convert Rational coefficients to whole numbers
        // Compute LCM of all denominators
        BigInteger lcm = BigInteger.ONE;
        for (RationalNumber val : solution) {
            lcm = lcm(lcm, val.getDenominator());
        }

        // Multiply by LCM to get integers
        BigInteger[] intCoeffs = new BigInteger[cols];
        for (int i = 0; i < cols; i++) {
            intCoeffs[i] = solution[i].getNumerator().multiply(lcm).divide(solution[i].getDenominator());
        }

        // Divide by GCD of all coefficients to get minimal whole-number ratio
        BigInteger overallGcd = intCoeffs[0];
        for (int i = 1; i < cols; i++) {
            overallGcd = overallGcd.gcd(intCoeffs[i]);
        }

        for (int i = 0; i < cols; i++) {
            intCoeffs[i] = intCoeffs[i].divide(overallGcd);
        }

        // 6. Build balanced reactant and product terms
        List<EquationTerm> balancedReactants = new ArrayList<>();
        int termIdx = 0;
        for (EquationTerm term : reactants) {
            balancedReactants.add(new EquationTerm(term.getFormula(), intCoeffs[termIdx], EquationSide.REACTANT));
            termIdx++;
        }

        List<EquationTerm> balancedProducts = new ArrayList<>();
        for (EquationTerm term : products) {
            balancedProducts.add(new EquationTerm(term.getFormula(), intCoeffs[termIdx], EquationSide.PRODUCT));
            termIdx++;
        }

        // 7. Verify atom and charge conservation
        boolean atomBalanced = verifyAtomConservation(balancedReactants, balancedProducts, allElements);
        boolean chargeBalanced = verifyChargeConservation(balancedReactants, balancedProducts);

        if (!atomBalanced || !chargeBalanced) {
            throw new InvalidChemicalEquationException("Balanced equation conservation check failed", EquationErrorCode.CHARGE_NOT_CONSERVED);
        }

        // Check if the original equation was already balanced
        boolean originallyBalanced = checkOriginallyBalanced(reactants, products, intCoeffs);

        return new BalancedEquation(balancedReactants, balancedProducts, originallyBalanced, atomBalanced, chargeBalanced);
    }

    private static RationalNumber[][] rref(RationalNumber[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int lead = 0;
        for (int r = 0; r < rows; r++) {
            if (lead >= cols) {
                break;
            }
            int i = r;
            while (matrix[i][lead].isZero()) {
                i++;
                if (i == rows) {
                    i = r;
                    lead++;
                    if (lead == cols) {
                        return matrix;
                    }
                }
            }
            // Swap rows
            RationalNumber[] temp = matrix[i];
            matrix[i] = matrix[r];
            matrix[r] = temp;

            RationalNumber lv = matrix[r][lead];
            for (int j = 0; j < cols; j++) {
                matrix[r][j] = matrix[r][j].divide(lv);
            }

            for (int k = 0; k < rows; k++) {
                if (k != r) {
                    RationalNumber factor = matrix[k][lead];
                    for (int j = 0; j < cols; j++) {
                        matrix[k][j] = matrix[k][j].subtract(factor.multiply(matrix[r][j]));
                    }
                }
            }
            lead++;
        }
        return matrix;
    }

    private static BigInteger lcm(BigInteger a, BigInteger b) {
        return a.multiply(b).abs().divide(a.gcd(b));
    }

    private boolean verifyAtomConservation(List<EquationTerm> reactants, List<EquationTerm> products, Set<ElementSymbol> elements) {
        for (ElementSymbol sym : elements) {
            BigInteger reactantSum = BigInteger.ZERO;
            for (EquationTerm term : reactants) {
                BigInteger count = term.getFormula().getElementCounts().getOrDefault(sym, BigInteger.ZERO);
                reactantSum = reactantSum.add(count.multiply(term.getCoefficient()));
            }
            BigInteger productSum = BigInteger.ZERO;
            for (EquationTerm term : products) {
                BigInteger count = term.getFormula().getElementCounts().getOrDefault(sym, BigInteger.ZERO);
                productSum = productSum.add(count.multiply(term.getCoefficient()));
            }
            if (!reactantSum.equals(productSum)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyChargeConservation(List<EquationTerm> reactants, List<EquationTerm> products) {
        BigInteger reactantCharge = BigInteger.ZERO;
        for (EquationTerm term : reactants) {
            int ch = term.getFormula().isElectron() ? -1 : term.getFormula().getNetCharge();
            reactantCharge = reactantCharge.add(BigInteger.valueOf(ch).multiply(term.getCoefficient()));
        }
        BigInteger productCharge = BigInteger.ZERO;
        for (EquationTerm term : products) {
            int ch = term.getFormula().isElectron() ? -1 : term.getFormula().getNetCharge();
            productCharge = productCharge.add(BigInteger.valueOf(ch).multiply(term.getCoefficient()));
        }
        return reactantCharge.equals(productCharge);
    }

    private boolean checkOriginallyBalanced(List<EquationTerm> originalReactants, List<EquationTerm> originalProducts, BigInteger[] balancedCoeffs) {
        // First check if original ratio matches balanced coefficients (scaled by a factor)
        // Original reactant coefficients:
        List<BigInteger> originalCoeffs = new ArrayList<>();
        for (EquationTerm term : originalReactants) {
            originalCoeffs.add(term.getCoefficient());
        }
        for (EquationTerm term : originalProducts) {
            originalCoeffs.add(term.getCoefficient());
        }

        // Verify that originalCoeffs[i] / balancedCoeffs[i] is constant for all i
        RationalNumber firstRatio = new RationalNumber(originalCoeffs.get(0), balancedCoeffs[0]);
        for (int i = 1; i < balancedCoeffs.length; i++) {
            RationalNumber ratio = new RationalNumber(originalCoeffs.get(i), balancedCoeffs[i]);
            if (!ratio.equals(firstRatio)) {
                return false;
            }
        }
        
        // Also check if they are already balanced
        Set<ElementSymbol> allElements = new HashSet<>();
        for (EquationTerm term : originalReactants) {
            allElements.addAll(term.getFormula().getElementCounts().keySet());
        }
        for (EquationTerm term : originalProducts) {
            allElements.addAll(term.getFormula().getElementCounts().keySet());
        }

        return verifyAtomConservation(originalReactants, originalProducts, allElements) &&
               verifyChargeConservation(originalReactants, originalProducts);
    }
}
