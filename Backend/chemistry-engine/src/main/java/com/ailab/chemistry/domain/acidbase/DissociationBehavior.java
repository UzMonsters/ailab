package com.ailab.chemistry.domain.acidbase;

public enum DissociationBehavior {
    STRONG_ELECTROLYTE,
    WEAK_ELECTROLYTE,
    NON_ELECTROLYTE,
    AUTOIONIZING_SOLVENT,
    NOT_APPLICABLE,
    UNKNOWN;

    public static DissociationBehavior fromString(String str) {
        if (str == null || str.isBlank()) return UNKNOWN;
        try {
            return DissociationBehavior.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
