package com.ailab.chemistry.domain.equation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.ailab.chemistry.domain.equation.exception.EquationErrorCode;
import com.ailab.chemistry.domain.equation.exception.InvalidChemicalEquationException;
import com.ailab.chemistry.domain.formula.ChemicalFormula;
import com.ailab.chemistry.domain.formula.FormulaParser;

public final class EquationParser {
    private final FormulaParser formulaParser;

    public EquationParser(FormulaParser formulaParser) {
        this.formulaParser = Objects.requireNonNull(formulaParser, "FormulaParser must not be null");
    }

    public ChemicalEquation parse(String equation) {
        Objects.requireNonNull(equation, "Equation string must not be null");
        if (equation.trim().isEmpty()) {
            throw new InvalidChemicalEquationException("Equation cannot be empty", EquationErrorCode.EMPTY_EQUATION);
        }

        // Normalize separators: ->, →, =
        String normalized = equation.replace("→", "->").replace("=", "->");
        
        // Split by ->
        String[] sides = normalized.split("->", -1);
        if (sides.length != 2) {
            throw new InvalidChemicalEquationException("Equation must contain exactly one separator (->, →, or =)", EquationErrorCode.INVALID_EQUATION_SEPARATOR);
        }

        String reactantsStr = sides[0].trim();
        String productsStr = sides[1].trim();

        if (reactantsStr.isEmpty()) {
            throw new InvalidChemicalEquationException("Equation missing reactant side", EquationErrorCode.MISSING_REACTANT);
        }
        if (productsStr.isEmpty()) {
            throw new InvalidChemicalEquationException("Equation missing product side", EquationErrorCode.MISSING_PRODUCT);
        }

        List<EquationTerm> reactants = parseSide(reactantsStr, EquationSide.REACTANT);
        List<EquationTerm> products = parseSide(productsStr, EquationSide.PRODUCT);

        return new ChemicalEquation(reactants, products);
    }

    private List<EquationTerm> parseSide(String sideStr, EquationSide side) {
        List<EquationTerm> terms = new ArrayList<>();
        int i = 0;
        int len = sideStr.length();
        boolean expectTerm = true;

        while (i < len) {
            // Skip whitespace
            while (i < len && Character.isWhitespace(sideStr.charAt(i))) {
                i++;
            }
            if (i == len) {
                break;
            }

            char c = sideStr.charAt(i);
            if (c == '+') {
                if (expectTerm) {
                    throw new InvalidChemicalEquationException("Unexpected '+' separator on " + side + " side", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
                }
                expectTerm = true;
                i++;
            } else {
                if (!expectTerm) {
                    throw new InvalidChemicalEquationException("Missing '+' separator between terms on " + side + " side", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
                }
                
                int termEnd = findTermEnd(sideStr, i);
                String termStr = sideStr.substring(i, termEnd).trim();
                
                if (termStr.isEmpty()) {
                    throw new InvalidChemicalEquationException("Empty term on " + side + " side", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
                }

                // Parse leading coefficient
                int j = 0;
                while (j < termStr.length() && Character.isDigit(termStr.charAt(j))) {
                    j++;
                }

                BigInteger coeff = BigInteger.ONE;
                String formulaStr = termStr;
                if (j > 0) {
                    if (j == termStr.length()) {
                        throw new InvalidChemicalEquationException("Number-only token not allowed as formula: " + termStr, EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
                    }
                    coeff = new BigInteger(termStr.substring(0, j));
                    if (coeff.compareTo(BigInteger.ZERO) <= 0) {
                        throw new InvalidChemicalEquationException("Coefficient must be positive", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
                    }
                    formulaStr = termStr.substring(j);
                }

                // Validate that it's not a charge-only or digit-only token
                String clean = formulaStr.replaceAll("[0-9^+\\-]", "");
                if (clean.isEmpty() && !formulaStr.equals("e-")) {
                    throw new InvalidChemicalEquationException("Charge-only or malformed token: " + termStr, EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
                }

                ChemicalFormula formula = formulaParser.parse(formulaStr);
                terms.add(new EquationTerm(formula, coeff, side));
                expectTerm = false;
                i = termEnd;
            }
        }

        if (expectTerm && !terms.isEmpty()) {
            throw new InvalidChemicalEquationException("Trailing '+' separator on " + side + " side", EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
        }
        if (terms.isEmpty()) {
            throw new InvalidChemicalEquationException("Empty side: " + side, EquationErrorCode.AMBIGUOUS_EQUATION_TOKENIZATION);
        }

        return terms;
    }

    private static int findTermEnd(String s, int start) {
        int j = start;
        int len = s.length();
        while (j < len) {
            char c = s.charAt(j);
            if (Character.isWhitespace(c)) {
                break;
            }
            if (c == '+' || c == '-') {
                if (isChargeSign(s, j, start)) {
                    j++;
                } else {
                    break;
                }
            } else {
                j++;
            }
        }
        return j;
    }

    private static boolean isChargeSign(String s, int j, int termStart) {
        char c = s.charAt(j);
        if (c != '+' && c != '-') {
            return false;
        }
        if (j - 1 >= termStart && s.charAt(j - 1) == '^') {
            return true;
        }
        if (j - 2 >= termStart && s.charAt(j - 2) == '^' && Character.isDigit(s.charAt(j - 1))) {
            return true;
        }
        if (c == '-' && j - 1 == termStart && s.charAt(termStart) == 'e') {
            return true;
        }
        if (j + 1 < s.length() && (s.charAt(j + 1) == '+' || s.charAt(j + 1) == '-')) {
            return true;
        }
        if (j + 1 == s.length()) {
            return true;
        }
        char next = s.charAt(j + 1);
        if (Character.isWhitespace(next) || next == '+') {
            if (j - 1 >= termStart) {
                char prev = s.charAt(j - 1);
                if (Character.isLetterOrDigit(prev) || prev == ')' || prev == ']') {
                    return true;
                }
            }
        }
        return false;
    }
}
