package com.ailab.chemistry.domain.formula;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import com.ailab.chemistry.domain.formula.exception.FormulaSyntaxException;
import com.ailab.chemistry.domain.formula.exception.FormulaErrorCode;
import com.ailab.chemistry.domain.formula.exception.UnknownElementSymbolException;

public final class ElementSymbol implements Comparable<ElementSymbol> {
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^[A-Z][a-z]?$");
    private final String symbol;
    
    public ElementSymbol(String symbol) {
        Objects.requireNonNull(symbol, "Element symbol must not be null");
        if (!SYMBOL_PATTERN.matcher(symbol).matches()) {
            throw new FormulaSyntaxException("Invalid element symbol syntax: " + symbol, FormulaErrorCode.INVALID_ELEMENT_SYMBOL);
        }
        if (!com.ailab.chemistry.domain.element.KnownElementRegistry.isKnownSymbol(symbol)) {
            throw new UnknownElementSymbolException("Unknown chemical element symbol: " + symbol);
        }
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public int compareTo(ElementSymbol other) {
        return this.symbol.compareTo(other.symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementSymbol that = (ElementSymbol) o;
        return symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public String toString() {
        return symbol;
    }
}
