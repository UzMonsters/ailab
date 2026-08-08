package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.solubility.MolarSolubilityRequest;
import com.ailab.chemistry.domain.solubility.MolarSolubilityResult;
import com.ailab.chemistry.domain.solubility.PrecipitationRequest;
import com.ailab.chemistry.domain.solubility.PrecipitationResult;
import com.ailab.chemistry.domain.solubility.SaturationRequest;
import com.ailab.chemistry.domain.solubility.SaturationResult;

public interface SolubilityEquilibriumService {
    SaturationResult calculateSaturation(SaturationRequest request);
    MolarSolubilityResult calculateMolarSolubility(MolarSolubilityRequest request);
    PrecipitationResult calculatePrecipitation(PrecipitationRequest request);
}
