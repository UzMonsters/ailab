# Reaction Database Core Architecture & Data Model

## Overview
The Reaction Database Core (`com.ailab.chemistry.domain.reaction`) provides versioned, provenance-tracked, scope-aware reference chemical reactions linked directly to catalogue chemical compounds.

## Core Architectural Principles
1. **Reference Data vs. Calculation Engine**: Stores evaluated balanced chemical reactions, species state annotations, reference catalysts, conditions, multi-label type assignments, and provenance. Does NOT perform stoichiometric quantity calculations, limiting-reagent analysis, yield evaluation, thermodynamic feasibility, equilibrium calculations, rate-law kinetic simulations, electrochemistry, or runtime experiment safety blocking (deferred to later modules).
2. **Compound-Linked Reaction Terms**: Every reactant and product term is linked directly to an existing `Compound` aggregate in the catalogue (`COMP-H2`, `COMP-O2`, `COMP-H2O`, etc.). No formula-only or orphan species are accepted.
3. **Exact Equation Balancing**: Every reaction equation is parsed with `FormulaParser` and verified using `DefaultEquationBalancer`. Coefficients are minimal positive integers (GCD = 1) with exact atom and charge conservation.
4. **Distinct Formula Representations**: Preserves `originalEquation`, `normalizedEquation`, `canonicalBalancedEquation`, and machine-deterministic `reactionSignature`.
5. **Directionality & Species States**: Reaction directionality (`IRREVERSIBLE`, `REVERSIBLE`, `EQUILIBRIUM_REPRESENTATION`, `UNKNOWN`) is decoupled from raw equation string syntax. Species states (`SOLID`, `LIQUID`, `GAS`, `AQUEOUS`, `DISSOLVED`, `MOLTEN`, `UNKNOWN`) belong to individual reaction terms without mutating `Compound` aggregate identity.
6. **Multi-Label Reaction Taxonomy**: Classifies reactions across 16 taxonomy types (Synthesis, Decomposition, Combustion, Single/Double Displacement, Acid-Base Neutralization, Precipitation, Gas Evolution, Redox, Oxidation, Reduction, Hydration, Dehydration, Hydrolysis, Reversible Reaction, Other) with explicit derivation basis (`CURATED_REFERENCE` vs `SAFE_RULE_DERIVED`).

## Scope Boundaries
- **Supported**: Reaction identity, balanced equations, compound linkages, minimal coefficients, species states, catalysts, condition sets, multi-label taxonomy, scientific provenance, lookup APIs.
- **Excluded / Deferred**: Grams/moles quantity calculation, yield, limiting reagent, enthalpy/Gibbs free energy, equilibrium position, reaction kinetics/rates, container safety.
