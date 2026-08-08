package com.ailab.chemistry.domain.formula;

import java.util.HashMap;
import java.util.Map;

public final class FormulaNormalizer {
    private static final Map<Character, Character> SUBSCRIPTS;
    private static final Map<Character, Character> SUPERSCRIPTS;
    
    static {
        SUBSCRIPTS = new HashMap<>();
        SUBSCRIPTS.put('₀', '0');
        SUBSCRIPTS.put('₁', '1');
        SUBSCRIPTS.put('₂', '2');
        SUBSCRIPTS.put('₃', '3');
        SUBSCRIPTS.put('₄', '4');
        SUBSCRIPTS.put('₅', '5');
        SUBSCRIPTS.put('₆', '6');
        SUBSCRIPTS.put('₇', '7');
        SUBSCRIPTS.put('₈', '8');
        SUBSCRIPTS.put('₉', '9');

        SUPERSCRIPTS = new HashMap<>();
        SUPERSCRIPTS.put('⁰', '0');
        SUPERSCRIPTS.put('¹', '1');
        SUPERSCRIPTS.put('²', '2');
        SUPERSCRIPTS.put('³', '3');
        SUPERSCRIPTS.put('⁴', '4');
        SUPERSCRIPTS.put('⁵', '5');
        SUPERSCRIPTS.put('⁶', '6');
        SUPERSCRIPTS.put('⁷', '7');
        SUPERSCRIPTS.put('⁸', '8');
        SUPERSCRIPTS.put('⁹', '9');
        SUPERSCRIPTS.put('⁺', '+');
        SUPERSCRIPTS.put('⁻', '-');
    }

    private FormulaNormalizer() {}

    public static String normalize(String formula) {
        if (formula == null) {
            return null;
        }
        String trimmed = formula.trim();
        StringBuilder sb = new StringBuilder();
        boolean inSuper = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (SUPERSCRIPTS.containsKey(c)) {
                if (!inSuper) {
                    sb.append('^');
                    inSuper = true;
                }
                sb.append(SUPERSCRIPTS.get(c));
            } else if (SUBSCRIPTS.containsKey(c)) {
                inSuper = false;
                sb.append(SUBSCRIPTS.get(c));
            } else {
                inSuper = false;
                if (c == '.') {
                    sb.append('·');
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}
