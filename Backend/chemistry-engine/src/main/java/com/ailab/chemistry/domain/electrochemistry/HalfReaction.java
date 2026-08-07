package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record HalfReaction(
        String equation,
        List<HalfReactionParticipant> participants,
        HalfReactionDirection direction,
        ElectronCount electronCount
) {
    public HalfReaction {
        participants = List.copyOf(participants);
        if (participants.isEmpty()) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Half-reaction participants are required");
        }
        if (direction != HalfReactionDirection.REDUCTION) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Reference half-reactions are stored as reductions");
        }
        validate(participants, electronCount);
    }

    public HalfReactionValidation validate() {
        return validate(participants, electronCount);
    }

    private static HalfReactionValidation validate(List<HalfReactionParticipant> participants, ElectronCount electronCount) {
        Map<String, BigDecimal> atoms = new LinkedHashMap<>();
        BigDecimal leftCharge = electronCount.value().negate();
        BigDecimal rightCharge = BigDecimal.ZERO;
        for (HalfReactionParticipant p : participants) {
            BigDecimal sign = p.side() == HalfReactionParticipantSide.REACTANT ? BigDecimal.ONE : BigDecimal.ONE.negate();
            p.formula().forEach((element, count) -> atoms.merge(element, sign.multiply(p.coefficient()).multiply(count), BigDecimal::add));
            BigDecimal participantCharge = p.coefficient().multiply(BigDecimal.valueOf(p.charge()));
            if (p.side() == HalfReactionParticipantSide.REACTANT) {
                leftCharge = leftCharge.add(participantCharge);
            } else {
                rightCharge = rightCharge.add(participantCharge);
            }
        }
        BigDecimal atomResidual = atoms.values().stream().map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal chargeResidual = leftCharge.subtract(rightCharge).abs();
        if (atomResidual.compareTo(BigDecimal.ZERO) != 0 || chargeResidual.compareTo(BigDecimal.ZERO) != 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.UNBALANCED_HALF_REACTION, "Half-reaction is not atom and charge balanced");
        }
        return new HalfReactionValidation(atomResidual, chargeResidual);
    }
}
