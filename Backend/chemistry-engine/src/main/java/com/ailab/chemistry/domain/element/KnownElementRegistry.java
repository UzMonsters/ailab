package com.ailab.chemistry.domain.element;

import java.util.*;

public final class KnownElementRegistry {
    private static final Map<String, KnownElementRecord> BY_SYMBOL;
    private static final Map<Integer, KnownElementRecord> BY_ATOMIC_NUMBER;

    static {
        Map<String, KnownElementRecord> bySymbol = new HashMap<>();
        Map<Integer, KnownElementRecord> byAtomicNumber = new HashMap<>();

        // List of all 118 elements in order of atomic number
        String[] elements = {
            "H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne", "Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar",
            "K", "Ca", "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Ga", "Ge", "As", "Se", "Br", "Kr",
            "Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe",
            "Cs", "Ba", "La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb", "Lu",
            "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt", "Au", "Hg", "Tl", "Pb", "Bi", "Po", "At", "Rn",
            "Fr", "Ra", "Ac", "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr",
            "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cn", "Nh", "Fl", "Mc", "Lv", "Ts", "Og"
        };

        Set<String> ambiguousSymbols = Set.of(
            "H", "He", "B", "C", "N", "O", "F", "Ne", "Si", "P", "S", "Cl", "Ar", 
            "Ge", "As", "Se", "Br", "Kr", "Sb", "Te", "I", "Xe", "Po", "At", "Rn"
        );

        for (int i = 0; i < elements.length; i++) {
            int atomicNumber = i + 1;
            String symbol = elements[i];
            boolean ambiguous = ambiguousSymbols.contains(symbol);
            KnownElementRecord record = new KnownElementRecord(atomicNumber, symbol, ambiguous);
            bySymbol.put(symbol, record);
            byAtomicNumber.put(atomicNumber, record);
        }

        BY_SYMBOL = Collections.unmodifiableMap(bySymbol);
        BY_ATOMIC_NUMBER = Collections.unmodifiableMap(byAtomicNumber);
    }

    public static Set<String> getKnownSymbols() {
        return BY_SYMBOL.keySet();
    }

    public static boolean isKnownSymbol(String symbol) {
        return BY_SYMBOL.containsKey(symbol);
    }

    public static boolean isAmbiguousChargeShorthand(String symbol) {
        KnownElementRecord record = BY_SYMBOL.get(symbol);
        return record != null && record.ambiguousChargeShorthand();
    }

    public static KnownElementRecord getBySymbol(String symbol) {
        return BY_SYMBOL.get(symbol);
    }

    public static KnownElementRecord getByAtomicNumber(int atomicNumber) {
        return BY_ATOMIC_NUMBER.get(atomicNumber);
    }

    public static Collection<KnownElementRecord> getAll() {
        return BY_ATOMIC_NUMBER.values();
    }
}
