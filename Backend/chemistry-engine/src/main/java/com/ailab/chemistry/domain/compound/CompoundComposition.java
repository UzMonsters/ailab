package com.ailab.chemistry.domain.compound;

import java.util.*;

public final class CompoundComposition {
    private final List<CompoundElementCount> elementCounts;

    public CompoundComposition(List<CompoundElementCount> elementCounts) {
        if (elementCounts == null || elementCounts.isEmpty()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_COMPOSITION, "CompoundComposition cannot be empty or null");
        }
        List<CompoundElementCount> sorted = new ArrayList<>(elementCounts);
        Collections.sort(sorted);

        // Check for duplicates
        for (int i = 0; i < sorted.size() - 1; i++) {
            if (sorted.get(i).getAtomicNumber() == sorted.get(i + 1).getAtomicNumber()) {
                throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_COMPOSITION,
                        "Duplicate element in composition: " + sorted.get(i).getSymbol());
            }
        }
        this.elementCounts = Collections.unmodifiableList(sorted);
    }

    public List<CompoundElementCount> getElementCounts() {
        return elementCounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundComposition that = (CompoundComposition) o;
        return Objects.equals(elementCounts, that.elementCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementCounts);
    }

    @Override
    public String toString() {
        return elementCounts.toString();
    }
}
