package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.acidbase.AcidBaseEquilibriumResult;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.Temperature;

public interface AcidBaseEquilibriumService {

    AcidBaseEquilibriumResult calculatePureWater(
            Temperature temperature
    );

    AcidBaseEquilibriumResult calculateStrongAcid(
            String speciesCode,
            MolarConcentration concentration,
            Temperature temperature
    );

    AcidBaseEquilibriumResult calculateStrongBase(
            String speciesCode,
            MolarConcentration concentration,
            Temperature temperature
    );

    AcidBaseEquilibriumResult calculateWeakAcid(
            String speciesCode,
            MolarConcentration concentration,
            Temperature temperature
    );

    AcidBaseEquilibriumResult calculateWeakBase(
            String speciesCode,
            MolarConcentration concentration,
            Temperature temperature
    );

    AcidBaseEquilibriumResult calculateSaltHydrolysis(
            String speciesCode,
            MolarConcentration concentration,
            Temperature temperature
    );
}
