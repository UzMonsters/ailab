package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.acidbase.ActivityCorrectedEquilibriumResult;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectionRequest;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectionResult;
import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.acidbase.IonicStrength;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;

public interface IonicActivityService {

    IonicStrength calculateIonicStrength(List<IonicSpeciesConcentration> species);

    ActivityCorrectionResult calculateActivities(
            List<IonicSpeciesConcentration> species,
            Temperature temperature,
            String solventCode,
            ActivityModel model
    );

    ActivityCorrectedEquilibriumResult calculateEquilibrium(ActivityCorrectionRequest request);
}
