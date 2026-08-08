package com.ailab.chemistry.domain.formula;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.TreeMap;
import com.ailab.chemistry.domain.formula.exception.*;

public final class DefaultFormulaParser implements FormulaParser {
    private static final int MAX_INPUT_LENGTH = 500;
    private static final int MAX_NESTING_DEPTH = 20;
    private static final BigInteger MAX_MULTIPLIER_SIZE = new BigInteger("1000000");
    @Override
    public ChemicalFormula parse(String formula) {
        Objects.requireNonNull(formula, "Formula must not be null");
        if (formula.trim().isEmpty()) {
            throw new FormulaSyntaxException("Formula cannot be empty", FormulaErrorCode.EMPTY_FORMULA);
        }
        if (formula.length() > MAX_INPUT_LENGTH) {
            throw new FormulaComplexityException("Formula input length exceeds limit of " + MAX_INPUT_LENGTH);
        }

        String normalized = FormulaNormalizer.normalize(formula);
        
        // 1. Parse Charge
        int charge = 0;
        String working = normalized;
        if (working.endsWith("+") || working.endsWith("-")) {
            int signIndex = working.length() - 1;
            char sign = working.charAt(signIndex);
            int chargeMag = 1;
            int startOfCharge = signIndex;

            // Check if there is a caret '^' before the sign (e.g. SO4^2-)
            int caretIndex = working.lastIndexOf('^');
            if (caretIndex != -1 && caretIndex < signIndex) {
                String chargeBlock = working.substring(caretIndex + 1, signIndex);
                if (!chargeBlock.isEmpty()) {
                    try {
                        chargeMag = Integer.parseInt(chargeBlock);
                        if (chargeMag <= 0) {
                            throw new FormulaSyntaxException("Charge magnitude must be positive: " + chargeBlock, FormulaErrorCode.INVALID_CHARGE);
                        }
                    } catch (NumberFormatException e) {
                        throw new FormulaSyntaxException("Charge magnitude too large: " + chargeBlock, FormulaErrorCode.INVALID_CHARGE);
                    }
                }
                startOfCharge = caretIndex;
            } else {
                // No caret. Check if there is a digit immediately before the sign
                if (signIndex - 1 >= 0 && Character.isDigit(working.charAt(signIndex - 1))) {
                    // Check if the prefix before the digit is a single element symbol
                    int digitStart = signIndex - 1;
                    while (digitStart >= 0 && Character.isDigit(working.charAt(digitStart))) {
                        digitStart--;
                    }
                    String prefix = working.substring(0, digitStart + 1);
                    if (prefix.matches("^[A-Z][a-z]?$")) {
                        if (com.ailab.chemistry.domain.element.KnownElementRegistry.isAmbiguousChargeShorthand(prefix)) {
                            throw new FormulaSyntaxException("Ambiguous molecular charge notation: " + normalized + ". Use caret notation (e.g. O2^+).", FormulaErrorCode.AMBIGUOUS_CHARGE_NOTATION);
                        }
                        String magStr = working.substring(digitStart + 1, signIndex);
                        chargeMag = Integer.parseInt(magStr);
                        startOfCharge = digitStart + 1;
                    }
                }
            }

            charge = (sign == '+') ? chargeMag : -chargeMag;
            working = working.substring(0, startOfCharge);
            
            if (working.endsWith("+") || working.endsWith("-")) {
                throw new FormulaSyntaxException("Malformed charge format: duplicate signs", FormulaErrorCode.INVALID_CHARGE);
            }
            
            if (working.isEmpty()) {
                throw new FormulaSyntaxException("Empty formula after stripping charge", FormulaErrorCode.EMPTY_FORMULA);
            }
        }
        
        // Check for isolated trailing caret or any unused caret in the formula (charge caret must be at the end)
        if (working.contains("^")) {
            throw new FormulaSyntaxException("Malformed charge caret notation", FormulaErrorCode.INVALID_CHARGE);
        }

        // 2. Check for Electron special case
        if (working.equals("e") && charge == -1) {
            return new ChemicalFormula(formula, normalized, new TreeMap<>(), -1, true);
        }

        // 3. Parse Hydrates
        if (working.startsWith("·") || working.endsWith("·")) {
            throw new FormulaSyntaxException("Hydrate separator cannot be at the start or end of the formula", FormulaErrorCode.INVALID_HYDRATE);
        }

        String[] segments = working.split("·", -1);
        Map<ElementSymbol, BigInteger> totalComposition = new TreeMap<>(Comparator.comparing(ElementSymbol::getSymbol));

        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (segment.isEmpty()) {
                throw new FormulaSyntaxException("Repeated or empty hydrate segment", FormulaErrorCode.INVALID_HYDRATE);
            }

            if (i == 0) {
                // Main segment
                Map<ElementSymbol, BigInteger> mainComp = parseBasicFormula(segment);
                mergeCompositions(totalComposition, mainComp, BigInteger.ONE);
            } else {
                // Hydrate segment (may have leading coefficient)
                int coeffEnd = 0;
                while (coeffEnd < segment.length() && Character.isDigit(segment.charAt(coeffEnd))) {
                    coeffEnd++;
                }

                BigInteger coeff = BigInteger.ONE;
                String hydrateFormula = segment;
                if (coeffEnd > 0) {
                    String coeffStr = segment.substring(0, coeffEnd);
                    coeff = new BigInteger(coeffStr);
                    if (coeff.compareTo(BigInteger.ZERO) <= 0) {
                        throw new FormulaSyntaxException("Leading hydrate coefficient must be positive", FormulaErrorCode.ZERO_ELEMENT_COUNT);
                    }
                    if (coeff.compareTo(MAX_MULTIPLIER_SIZE) > 0) {
                        throw new FormulaComplexityException("Hydrate coefficient too large");
                    }
                    hydrateFormula = segment.substring(coeffEnd);
                }

                if (hydrateFormula.isEmpty()) {
                    throw new FormulaSyntaxException("Empty hydrate segment after coefficient", FormulaErrorCode.INVALID_HYDRATE);
                }

                Map<ElementSymbol, BigInteger> hydrateComp = parseBasicFormula(hydrateFormula);
                mergeCompositions(totalComposition, hydrateComp, coeff);
            }
        }

        return new ChemicalFormula(formula, normalized, totalComposition, charge, false);
    }

    private Map<ElementSymbol, BigInteger> parseBasicFormula(String basic) {
        Stack<Map<ElementSymbol, BigInteger>> stack = new Stack<>();
        stack.push(new TreeMap<>(Comparator.comparing(ElementSymbol::getSymbol)));
        int nestingDepth = 0;
        int i = 0;
        int length = basic.length();

        while (i < length) {
            char c = basic.charAt(i);

            if (c == '(' || c == '[') {
                nestingDepth++;
                if (nestingDepth > MAX_NESTING_DEPTH) {
                    throw new FormulaComplexityException("Nesting depth exceeds limit of " + MAX_NESTING_DEPTH);
                }
                stack.push(new TreeMap<>(Comparator.comparing(ElementSymbol::getSymbol)));
                i++;
            } else if (c == ')' || c == ']') {
                nestingDepth--;
                if (nestingDepth < 0) {
                    throw new FormulaSyntaxException("Unmatched closing bracket: " + c, FormulaErrorCode.UNMATCHED_GROUP);
                }

                // Parse multiplier
                int multStart = i + 1;
                int multEnd = multStart;
                while (multEnd < length && Character.isDigit(basic.charAt(multEnd))) {
                    multEnd++;
                }

                BigInteger multiplier = BigInteger.ONE;
                if (multEnd > multStart) {
                    String multStr = basic.substring(multStart, multEnd);
                    multiplier = new BigInteger(multStr);
                    if (multiplier.compareTo(BigInteger.ZERO) == 0) {
                        throw new FormulaSyntaxException("Group multiplier cannot be zero", FormulaErrorCode.ZERO_ELEMENT_COUNT);
                    }
                    if (multiplier.compareTo(MAX_MULTIPLIER_SIZE) > 0) {
                        throw new FormulaComplexityException("Group multiplier exceeds limit");
                    }
                }

                Map<ElementSymbol, BigInteger> groupMap = stack.pop();
                if (groupMap.isEmpty()) {
                    throw new FormulaSyntaxException("Empty group inside brackets", FormulaErrorCode.EMPTY_GROUP);
                }

                Map<ElementSymbol, BigInteger> targetMap = stack.peek();
                mergeCompositions(targetMap, groupMap, multiplier);
                i = multEnd;
            } else if (Character.isLowerCase(c)) {
                throw new FormulaSyntaxException("Element symbol must start with an uppercase letter: " + c, FormulaErrorCode.INVALID_ELEMENT_SYMBOL);
            } else if (Character.isUpperCase(c)) {
                // Parse element symbol
                int symbolEnd = i + 1;
                if (symbolEnd < length && Character.isLowerCase(basic.charAt(symbolEnd))) {
                    symbolEnd++;
                }

                // Check for 3-letter symbols: reject them
                if (symbolEnd < length && Character.isLowerCase(basic.charAt(symbolEnd))) {
                    throw new FormulaSyntaxException("Three-letter element symbols are not supported", FormulaErrorCode.INVALID_ELEMENT_SYMBOL);
                }

                String symStr = basic.substring(i, symbolEnd);
                ElementSymbol elementSymbol = new ElementSymbol(symStr);

                // Parse optional count
                int countStart = symbolEnd;
                int countEnd = countStart;
                while (countEnd < length && Character.isDigit(basic.charAt(countEnd))) {
                    countEnd++;
                }

                BigInteger count = BigInteger.ONE;
                if (countEnd > countStart) {
                    String countStr = basic.substring(countStart, countEnd);
                    count = new BigInteger(countStr);
                    if (count.compareTo(BigInteger.ZERO) == 0) {
                        throw new FormulaSyntaxException("Element count cannot be zero: " + symStr + "0", FormulaErrorCode.ZERO_ELEMENT_COUNT);
                    }
                    if (count.compareTo(MAX_MULTIPLIER_SIZE) > 0) {
                        throw new FormulaComplexityException("Element count too large: " + countStr);
                    }
                }

                Map<ElementSymbol, BigInteger> targetMap = stack.peek();
                BigInteger existing = targetMap.getOrDefault(elementSymbol, BigInteger.ZERO);
                targetMap.put(elementSymbol, existing.add(count));
                i = countEnd;
            } else {
                throw new FormulaSyntaxException("Unexpected character: " + c, FormulaErrorCode.UNEXPECTED_TOKEN);
            }
        }

        if (stack.size() > 1) {
            throw new FormulaSyntaxException("Unmatched opening bracket", FormulaErrorCode.UNMATCHED_GROUP);
        }

        Map<ElementSymbol, BigInteger> finalMap = stack.pop();
        if (finalMap.isEmpty()) {
            throw new FormulaSyntaxException("Empty formula composition", FormulaErrorCode.EMPTY_FORMULA);
        }
        return finalMap;
    }

    private void mergeCompositions(Map<ElementSymbol, BigInteger> target, Map<ElementSymbol, BigInteger> source, BigInteger multiplier) {
        for (Map.Entry<ElementSymbol, BigInteger> entry : source.entrySet()) {
            BigInteger multiplied = entry.getValue().multiply(multiplier);
            BigInteger existing = target.getOrDefault(entry.getKey(), BigInteger.ZERO);
            BigInteger merged = existing.add(multiplied);
            if (merged.compareTo(MAX_MULTIPLIER_SIZE.multiply(MAX_MULTIPLIER_SIZE)) > 0) {
                throw new FormulaComplexityException("Total expanded atom count exceeds limit");
            }
            target.put(entry.getKey(), merged);
        }
    }
}

// Helper comparator for element symbol sorting
class Comparator {
    static java.util.Comparator<ElementSymbol> comparing(java.util.function.Function<ElementSymbol, String> keyExtractor) {
        return (s1, s2) -> keyExtractor.apply(s1).compareTo(keyExtractor.apply(s2));
    }
}
