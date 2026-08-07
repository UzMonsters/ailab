package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.measurement.MassUnit;
import com.ailab.chemistry.domain.measurement.ScientificConstants;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ElectrochemistryCalculator {
    private static final BigDecimal R = ScientificConstants.IDEAL_GAS_CONSTANT_SI;
    private static final BigDecimal F = FaradayConstant.CODATA_2018_EXACT.coulombsPerMole();
    private static final BigDecimal LN10 = BigDecimal.valueOf(Math.log(10.0));
    private static final BigDecimal STATUS_TOLERANCE_V = new BigDecimal("0.0000001");

    public ElectrochemicalCellResult calculateStandardCell(ElectrochemicalCellRequest request, ElectrochemicalReferenceRepository repository) {
        StandardReductionPotential cathode = find(request.cathodeReductionRecordId(), repository);
        StandardReductionPotential anode = find(request.anodeReductionRecordId(), repository);
        BigDecimal eCell = cathode.standardPotential().inVolts().subtract(anode.standardPotential().inVolts(), ScientificMath.CALCULATION_CONTEXT);
        BigDecimal baseElectrons = lcm(cathode.electronCount().value().toBigIntegerExact(), anode.electronCount().value().toBigIntegerExact());
        BigDecimal n = baseElectrons.multiply(request.reactionScale(), ScientificMath.CALCULATION_CONTEXT);
        CellReaction reaction = combine(cathode, anode, request.reactionScale(), baseElectrons);
        BigDecimal deltaG = n.multiply(F, ScientificMath.CALCULATION_CONTEXT).multiply(eCell, ScientificMath.CALCULATION_CONTEXT).negate();
        BigDecimal rt = R.multiply(cathode.conditions().temperature().in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
        BigDecimal lnK = n.multiply(F, ScientificMath.CALCULATION_CONTEXT).multiply(eCell, ScientificMath.CALCULATION_CONTEXT).divide(rt, ScientificMath.CALCULATION_CONTEXT);
        BigDecimal log10K = lnK.divide(LN10, ScientificMath.CALCULATION_CONTEXT);
        return new ElectrochemicalCellResult(
                classify(eCell),
                cathode,
                anode,
                new ElectronCount(n.stripTrailingZeros()),
                new CellPotential(eCell.stripTrailingZeros()),
                reaction,
                notation(anode, cathode),
                new ElectrochemicalGibbsEnergy(new StandardGibbsEnergy(deltaG.setScale(6, RoundingMode.HALF_UP).stripTrailingZeros())),
                lnK,
                log10K,
                List.of("Anode record remains stored as a reduction potential; cell construction reverses it.", "Electrode potentials are intensive and are not multiplied by half-reaction scaling."),
                new ElectrochemicalResidual(reaction.atomResidual(), reaction.chargeResidual(), BigDecimal.ZERO)
        );
    }

    public NernstResult calculateNonstandardCell(NernstRequest request, ElectrochemicalReferenceRepository repository) {
        if (request.temperature().in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_TEMPERATURE, "Temperature must be positive");
        }
        ElectrochemicalCellResult standard = calculateStandardCell(new ElectrochemicalCellRequest(
                request.cathodeReductionRecordId(), request.anodeReductionRecordId(), ElectrochemicalCellType.GALVANIC, BigDecimal.ONE), repository);
        Map<String, ElectrochemicalActivity> activities = new LinkedHashMap<>();
        for (ElectrochemicalActivity activity : request.activities()) {
            activities.put(activity.speciesCode() + "|" + activity.phase(), activity);
        }
        BigDecimal lnQ = BigDecimal.ZERO;
        BigDecimal q = BigDecimal.ONE;
        List<String> sources = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> term : standard.cellReaction().terms().entrySet()) {
            if (term.getValue().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            ElectrochemicalActivity activity = activities.get(term.getKey());
            if (activity == null) {
                throw new ElectrochemicalException(ElectrochemicalErrorCode.MISSING_ACTIVITY, "Missing explicit activity for " + term.getKey());
            }
            BigDecimal a = resolveActivity(activity, sources);
            lnQ = lnQ.add(term.getValue().multiply(BigDecimal.valueOf(Math.log(a.doubleValue())), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            q = multiplyActivityTerm(q, a, term.getValue());
        }
        BigDecimal rtNf = R.multiply(request.temperature().in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT)
                .divide(standard.electronCount().value().multiply(F, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        BigDecimal e = standard.standardCellPotential().inVolts().subtract(rtNf.multiply(lnQ, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        return new NernstResult(classify(e), standard, q.stripTrailingZeros(), lnQ.stripTrailingZeros(), new CellPotential(e), sources,
                List.of("Nernst equation evaluated with natural logarithm; no base-10 shortcut is authoritative.", "Concentrations are used only through declared activity bases."));
    }

    public ElectrolysisResult calculateElectrolysis(ElectrolysisRequest request, ElectrochemicalReferenceRepository repository) {
        StandardReductionPotential record = find(request.halfReactionRecordId(), repository);
        HalfReactionParticipant target = record.participants().stream()
                .filter(p -> p.speciesCode().equals(request.substanceCode()) && p.phase().equals(request.substancePhase()))
                .findFirst()
                .orElseThrow(() -> new ElectrochemicalException(ElectrochemicalErrorCode.UNSUPPORTED_HALF_REACTION, "Target substance is not in half-reaction"));
        if (request.molarMassGramsPerMole() == null || request.molarMassGramsPerMole().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.MISSING_MOLAR_MASS, "Molar mass must be supplied by the compound catalogue");
        }
        ElectricCharge charge = request.charge();
        if (charge == null) {
            if (request.current() == null || request.duration() == null) {
                throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_ELECTROLYSIS_REQUEST, "Current and duration or charge is required");
            }
            charge = new ElectricCharge(request.current().inAmperes().multiply(request.duration().in(DurationUnit.SECOND), ScientificMath.CALCULATION_CONTEXT));
        }
        CurrentEfficiency efficiency = request.efficiency() == null ? CurrentEfficiency.of("1") : request.efficiency();
        ElectricCharge effective = new ElectricCharge(charge.inCoulombs().multiply(efficiency.fraction(), ScientificMath.CALCULATION_CONTEXT));
        BigDecimal electronMoles = effective.inCoulombs().divide(F, ScientificMath.CALCULATION_CONTEXT);
        BigDecimal substanceMoles = electronMoles.multiply(target.coefficient(), ScientificMath.CALCULATION_CONTEXT)
                .divide(record.electronCount().value(), ScientificMath.CALCULATION_CONTEXT);
        BigDecimal massGrams = substanceMoles.multiply(request.molarMassGramsPerMole(), ScientificMath.CALCULATION_CONTEXT);
        return new ElectrolysisResult(
                ElectrochemicalStatus.SUCCESS,
                charge,
                effective,
                AmountOfSubstance.of(electronMoles, AmountOfSubstanceUnit.MOLE),
                AmountOfSubstance.of(substanceMoles, AmountOfSubstanceUnit.MOLE),
                Mass.of(massGrams, MassUnit.GRAM),
                target.side() == HalfReactionParticipantSide.PRODUCT ? "reduction deposition/product formation" : "oxidation consumption/reactant depletion",
                new ElectrochemicalEquivalent(request.molarMassGramsPerMole().multiply(target.coefficient(), ScientificMath.CALCULATION_CONTEXT)
                        .divide(record.electronCount().value().multiply(F, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT)),
                FaradayConstant.CODATA_2018_EXACT
        );
    }

    private BigDecimal resolveActivity(ElectrochemicalActivity activity, List<String> sources) {
        Objects.requireNonNull(activity.value(), "activity value must not be null");
        if (activity.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_ACTIVITY, "Activities must be positive and finite");
        }
        return switch (activity.basis()) {
            case PURE_SOLID, PURE_LIQUID -> {
                sources.add(activity.basis() + " excluded from Q by explicit activity 1");
                yield BigDecimal.ONE;
            }
            case IDEAL_GAS_PARTIAL_PRESSURE -> {
                sources.add("IDEAL_GAS_PARTIAL_PRESSURE normalized as p/p0 with p0 = 1 bar");
                yield activity.value();
            }
            case AQUEOUS_IDEAL -> {
                sources.add("AQUEOUS_IDEAL normalized as c/c0 with c0 = 1 mol/L");
                yield activity.value();
            }
            case AQUEOUS_DAVIES -> {
                if (activity.ionicStrength() == null || activity.ionicStrength().compareTo(BigDecimal.ZERO) < 0 || activity.ionicStrength().compareTo(new BigDecimal("0.5")) > 0) {
                    throw new ElectrochemicalException(ElectrochemicalErrorCode.ACTIVITY_MODEL_OUT_OF_RANGE, "Davies mode requires ionic strength through 0.5 mol/L");
                }
                BigDecimal gamma = daviesGamma(activity.charge(), activity.ionicStrength());
                sources.add("AQUEOUS_DAVIES activity from c*gamma with Davies valid through I = 0.5 mol/L");
                yield activity.value().multiply(gamma, ScientificMath.CALCULATION_CONTEXT);
            }
            case EXPLICIT_DIMENSIONLESS_ACTIVITY -> {
                sources.add("EXPLICIT_DIMENSIONLESS_ACTIVITY supplied directly");
                yield activity.value();
            }
        };
    }

    private BigDecimal daviesGamma(Integer charge, BigDecimal ionicStrength) {
        int z = charge == null ? 0 : charge;
        if (z == 0 || ionicStrength.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        BigDecimal sqrtI = ionicStrength.sqrt(ScientificMath.CALCULATION_CONTEXT);
        BigDecimal term = sqrtI.divide(BigDecimal.ONE.add(sqrtI, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT)
                .subtract(new BigDecimal("0.3").multiply(ionicStrength, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        double exponent = -0.509 * z * z * term.doubleValue();
        return BigDecimal.valueOf(Math.pow(10, exponent));
    }

    private BigDecimal multiplyActivityTerm(BigDecimal q, BigDecimal activity, BigDecimal exponent) {
        int power = exponent.abs().intValueExact();
        BigDecimal term = activity.pow(power, ScientificMath.CALCULATION_CONTEXT);
        if (exponent.signum() > 0) {
            return q.multiply(term, ScientificMath.CALCULATION_CONTEXT);
        }
        return q.divide(term, ScientificMath.CALCULATION_CONTEXT);
    }

    private StandardReductionPotential find(String recordId, ElectrochemicalReferenceRepository repository) {
        return repository.findByRecordId(recordId)
                .orElseThrow(() -> new ElectrochemicalException(ElectrochemicalErrorCode.MISSING_HALF_REACTION, "Missing half-reaction " + recordId));
    }

    private ElectrochemicalStatus classify(BigDecimal eCell) {
        if (eCell.compareTo(STATUS_TOLERANCE_V) > 0) {
            return ElectrochemicalStatus.GALVANIC_AS_WRITTEN;
        }
        if (eCell.compareTo(STATUS_TOLERANCE_V.negate()) < 0) {
            return ElectrochemicalStatus.NONSPONTANEOUS_AS_WRITTEN;
        }
        return ElectrochemicalStatus.EQUILIBRIUM_WITHIN_TOLERANCE;
    }

    private CellReaction combine(StandardReductionPotential cathode, StandardReductionPotential anode, BigDecimal reactionScale, BigDecimal baseElectrons) {
        Map<String, BigDecimal> terms = new LinkedHashMap<>();
        BigDecimal cathodeScale = baseElectrons.divide(cathode.electronCount().value(), ScientificMath.CALCULATION_CONTEXT).multiply(reactionScale, ScientificMath.CALCULATION_CONTEXT);
        BigDecimal anodeScale = baseElectrons.divide(anode.electronCount().value(), ScientificMath.CALCULATION_CONTEXT).multiply(reactionScale, ScientificMath.CALCULATION_CONTEXT);
        addHalfReaction(terms, cathode.participants(), cathodeScale, false);
        addHalfReaction(terms, anode.participants(), anodeScale, true);
        terms.entrySet().removeIf(e -> e.getValue().compareTo(BigDecimal.ZERO) == 0);
        return new CellReaction(terms, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private void addHalfReaction(Map<String, BigDecimal> terms, List<HalfReactionParticipant> participants, BigDecimal scale, boolean reversed) {
        for (HalfReactionParticipant participant : participants) {
            BigDecimal sign = participant.side() == HalfReactionParticipantSide.PRODUCT ? BigDecimal.ONE : BigDecimal.ONE.negate();
            if (reversed) {
                sign = sign.negate();
            }
            terms.merge(participant.speciesKey(), participant.coefficient().multiply(scale, ScientificMath.CALCULATION_CONTEXT).multiply(sign), BigDecimal::add);
        }
    }

    private CellNotation notation(StandardReductionPotential anode, StandardReductionPotential cathode) {
        return new CellNotation(electrodeSide(anode, true) + " || " + electrodeSide(cathode, false));
    }

    private String electrodeSide(StandardReductionPotential record, boolean reversed) {
        List<HalfReactionParticipant> reactants = record.participants().stream().filter(p -> p.side() == HalfReactionParticipantSide.REACTANT).toList();
        List<HalfReactionParticipant> products = record.participants().stream().filter(p -> p.side() == HalfReactionParticipantSide.PRODUCT).toList();
        List<HalfReactionParticipant> left = reversed ? products : reactants;
        List<HalfReactionParticipant> right = reversed ? reactants : products;
        return compact(left) + " | " + compact(right);
    }

    private String compact(List<HalfReactionParticipant> participants) {
        return participants.stream()
                .map(p -> p.displayFormula() + "(" + phaseSymbol(p.phase()) + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    private String phaseSymbol(String phase) {
        return switch (phase) {
            case "SOLID" -> "s";
            case "LIQUID" -> "l";
            case "GAS" -> "g";
            case "AQUEOUS" -> "aq";
            default -> phase.toLowerCase();
        };
    }

    private BigDecimal lcm(java.math.BigInteger a, java.math.BigInteger b) {
        return new BigDecimal(a.multiply(b).abs().divide(a.gcd(b)));
    }
}
