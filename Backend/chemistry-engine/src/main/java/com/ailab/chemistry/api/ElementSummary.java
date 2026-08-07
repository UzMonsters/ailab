package com.ailab.chemistry.api;

public final class ElementSummary {
    private final int atomicNumber;
    private final String symbol;
    private final String name;
    private final String category;

    public ElementSummary(int atomicNumber, String symbol, String name, String category) {
        this.atomicNumber = atomicNumber;
        this.symbol = symbol;
        this.name = name;
        this.category = category;
    }

    public int getAtomicNumber() { return atomicNumber; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getCategory() { return category; }
}
