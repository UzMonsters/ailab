package com.ailab.chemistry.domain.phasebehavior;

import java.util.Optional;

public interface PhaseBehaviorRepository {
    Optional<PhaseTransitionRecord> findTransition(String compoundCode, PhaseTransitionType forwardType);
    Optional<AntoineCoefficientSet> findAntoine(String compoundCode, com.ailab.chemistry.domain.element.MatterState initialPhase, com.ailab.chemistry.domain.element.MatterState finalPhase);
    Optional<PhaseBoundaryPoint> findTriplePoint(String compoundCode);
    Optional<PhaseBoundaryPoint> findCriticalPoint(String compoundCode);
}
