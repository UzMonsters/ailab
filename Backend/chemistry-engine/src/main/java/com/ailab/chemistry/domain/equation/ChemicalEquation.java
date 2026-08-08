package com.ailab.chemistry.domain.equation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ChemicalEquation {
    private final List<EquationTerm> reactants;
    private final List<EquationTerm> products;

    public ChemicalEquation(List<EquationTerm> reactants, List<EquationTerm> products) {
        Objects.requireNonNull(reactants, "Reactants list must not be null");
        Objects.requireNonNull(products, "Products list must not be null");
        this.reactants = Collections.unmodifiableList(reactants);
        this.products = Collections.unmodifiableList(products);
    }

    public List<EquationTerm> getReactants() {
        return reactants;
    }

    public List<EquationTerm> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemicalEquation that = (ChemicalEquation) o;
        return reactants.equals(that.reactants) && products.equals(that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reactants, products);
    }

    @Override
    public String toString() {
        String reactantsStr = reactants.stream().map(EquationTerm::toString).collect(Collectors.joining(" + "));
        String productsStr = products.stream().map(EquationTerm::toString).collect(Collectors.joining(" + "));
        return reactantsStr + " -> " + productsStr;
    }
}
