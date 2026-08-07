package com.ailab.chemistry.domain.acidbase;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record PolyproticAcidFamily(
        String familyCode,
        List<PolyproticSpecies> species,
        List<PolyproticDissociationConstant> constants,
        boolean firstDissociationComplete,
        List<String> sources
) {
    public PolyproticAcidFamily {
        if (familyCode == null || familyCode.isBlank()) {
            throw new PolyproticException(PolyproticErrorCode.INSUFFICIENT_REFERENCE_DATA, "Family code must not be blank");
        }
        familyCode = familyCode.trim();
        species = List.copyOf(Objects.requireNonNull(species, "species must not be null")).stream()
                .sorted(Comparator.comparingInt(PolyproticSpecies::protonsRemaining).reversed())
                .toList();
        constants = List.copyOf(Objects.requireNonNull(constants, "constants must not be null")).stream()
                .sorted(Comparator.comparingInt(PolyproticDissociationConstant::stepNumber))
                .toList();
        sources = List.copyOf(sources == null ? List.of() : sources);
        validateSpecies(species);
        validateConstants(species, constants, firstDissociationComplete);
    }

    public int totalProtons() {
        return species.get(0).protonsRemaining();
    }

    public PolyproticDissociationConstant constantForStep(int stepNumber) {
        return constants.stream()
                .filter(constant -> constant.stepNumber() == stepNumber)
                .findFirst()
                .orElseThrow(() -> new PolyproticException(PolyproticErrorCode.MISSING_KA_STEP, "Missing Ka step " + stepNumber));
    }

    public PolyproticSpecies speciesForInitialForm(PolyproticInitialForm form) {
        return switch (form) {
            case FULLY_PROTONATED_ACID -> species.get(0);
            case INTERMEDIATE_AMPHIPROTIC_SALT -> {
                if (species.size() < 3) {
                    throw new PolyproticException(PolyproticErrorCode.INVALID_INITIAL_FORM, "Intermediate form requires at least three species");
                }
                yield species.get(1);
            }
            case FULLY_DEPROTONATED_SALT -> species.get(species.size() - 1);
        };
    }

    private static void validateSpecies(List<PolyproticSpecies> species) {
        if (species.size() < 3) {
            throw new PolyproticException(PolyproticErrorCode.INVALID_INITIAL_FORM, "Polyprotic family requires at least three protonation states");
        }
        int total = species.get(0).protonsRemaining();
        for (int i = 0; i < species.size(); i++) {
            if (species.get(i).protonsRemaining() != total - i) {
                throw new PolyproticException(PolyproticErrorCode.NONCONTIGUOUS_DISSOCIATION_STEPS, "Species protonation states must be contiguous");
            }
        }
    }

    private static void validateConstants(List<PolyproticSpecies> species, List<PolyproticDissociationConstant> constants, boolean firstDissociationComplete) {
        int totalSteps = species.size() - 1;
        int expectedFirst = firstDissociationComplete ? 2 : 1;
        int expectedCount = totalSteps - expectedFirst + 1;
        if (constants.size() != expectedCount) {
            throw new PolyproticException(PolyproticErrorCode.MISSING_KA_STEP, "Missing dissociation constants for family");
        }
        for (int i = 0; i < constants.size(); i++) {
            int expectedStep = expectedFirst + i;
            if (constants.get(i).stepNumber() != expectedStep) {
                throw new PolyproticException(PolyproticErrorCode.NONCONTIGUOUS_DISSOCIATION_STEPS, "Dissociation constants must be contiguous");
            }
        }
        PolyproticDissociationConstant first = constants.get(0);
        for (PolyproticDissociationConstant constant : constants) {
            if (!constant.temperature().equals(first.temperature()) || !constant.solventCode().equalsIgnoreCase(first.solventCode())) {
                throw new PolyproticException(PolyproticErrorCode.MIXED_REFERENCE_CONDITIONS, "All Ka values must share temperature and solvent context");
            }
        }
    }
}
