package com.ailab.chemistry.domain.hazard;

import java.util.*;
import java.util.stream.Collectors;

public final class HazardSummaryDerivationEngine {

    private HazardSummaryDerivationEngine() {}

    public static Set<HazardSummaryFlag> deriveSummaryFlags(List<HazardClassification> classifications, List<SupplementalLaboratoryHazard> supplementalHazards) {
        Set<HazardSummaryFlag> flags = EnumSet.noneOf(HazardSummaryFlag.class);

        if (classifications != null) {
            for (HazardClassification c : classifications) {
                String classCode = c.getHazardClassCode().toUpperCase(Locale.ROOT);

                if (isFlammableClass(classCode)) flags.add(HazardSummaryFlag.FLAMMABLE);
                if (isOxidizerClass(classCode)) flags.add(HazardSummaryFlag.OXIDIZER);
                if (isExplosiveClass(classCode)) flags.add(HazardSummaryFlag.EXPLOSIVE);
                if (isToxicClass(classCode)) flags.add(HazardSummaryFlag.TOXIC);
                if (isCorrosiveClass(classCode)) flags.add(HazardSummaryFlag.CORROSIVE);
                if (isCarcinogenicClass(classCode)) flags.add(HazardSummaryFlag.CARCINOGENIC);
                if (isIrritantClass(classCode)) flags.add(HazardSummaryFlag.IRRITANT);
                if (isEnvironmentalClass(classCode)) flags.add(HazardSummaryFlag.ENVIRONMENTAL_HAZARD);
            }
        }

        if (supplementalHazards != null) {
            for (SupplementalLaboratoryHazard supp : supplementalHazards) {
                if (supp == SupplementalLaboratoryHazard.RADIOACTIVE) {
                    flags.add(HazardSummaryFlag.RADIOACTIVE);
                }
            }
        }

        return Collections.unmodifiableSet(flags);
    }

    public static HazardExplanation explain(String compoundCode, HazardSummaryFlag flag, List<HazardClassification> classifications, List<SupplementalLaboratoryHazard> supplementalHazards) {
        List<HazardClassification> matchingClassifications = new ArrayList<>();
        List<SupplementalLaboratoryHazard> matchingSupplemental = new ArrayList<>();

        if (classifications != null) {
            for (HazardClassification c : classifications) {
                String classCode = c.getHazardClassCode().toUpperCase(Locale.ROOT);
                boolean matches = switch (flag) {
                    case FLAMMABLE -> isFlammableClass(classCode);
                    case OXIDIZER -> isOxidizerClass(classCode);
                    case EXPLOSIVE -> isExplosiveClass(classCode);
                    case TOXIC -> isToxicClass(classCode);
                    case CORROSIVE -> isCorrosiveClass(classCode);
                    case CARCINOGENIC -> isCarcinogenicClass(classCode);
                    case IRRITANT -> isIrritantClass(classCode);
                    case ENVIRONMENTAL_HAZARD -> isEnvironmentalClass(classCode);
                    case RADIOACTIVE -> false;
                };
                if (matches) matchingClassifications.add(c);
            }
        }

        if (flag == HazardSummaryFlag.RADIOACTIVE && supplementalHazards != null) {
            for (SupplementalLaboratoryHazard supp : supplementalHazards) {
                if (supp == SupplementalLaboratoryHazard.RADIOACTIVE) matchingSupplemental.add(supp);
            }
        }

        if (matchingClassifications.isEmpty() && matchingSupplemental.isEmpty()) {
            throw new HazardException(HazardErrorCode.SUMMARY_FLAG_MISMATCH, "Summary flag " + flag + " is not supported by detailed evidence for compound: " + compoundCode);
        }

        String explanationText = "Flag " + flag.name() + " for compound " + compoundCode + " derived from " +
                matchingClassifications.size() + " detailed GHS classification(s) and " +
                matchingSupplemental.size() + " supplemental hazard record(s).";

        return new HazardExplanation(compoundCode, flag, matchingClassifications, matchingSupplemental, explanationText);
    }

    private static boolean isFlammableClass(String code) {
        return code.contains("FLAMMABLE") || code.contains("PYROPHORIC") || code.contains("SELF_HEATING");
    }

    private static boolean isOxidizerClass(String code) {
        return code.contains("OXIDIZING");
    }

    private static boolean isExplosiveClass(String code) {
        return code.contains("EXPLOSIVE") || code.contains("SELF_REACTIVE") || code.contains("ORGANIC_PEROXIDE");
    }

    private static boolean isToxicClass(String code) {
        return code.contains("ACUTE_TOXICITY") || code.contains("TARGET_ORGAN") || code.contains("ASPIRATION");
    }

    private static boolean isCorrosiveClass(String code) {
        return code.contains("CORROSION") || code.contains("CORROSIVE") || code.contains("SERIOUS_EYE_DAMAGE");
    }

    private static boolean isCarcinogenicClass(String code) {
        return code.contains("CARCINOGEN") || code.contains("MUTAGEN") || code.contains("REPRODUCTIVE_TOXICITY");
    }

    private static boolean isIrritantClass(String code) {
        return code.contains("IRRITATION") || code.contains("SENSITIZATION");
    }

    private static boolean isEnvironmentalClass(String code) {
        return code.contains("AQUATIC") || code.contains("ENVIRONMENT") || code.contains("OZONE");
    }
}
