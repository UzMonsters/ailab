package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.acidbase.BufferCalculationRequest;
import com.ailab.chemistry.domain.acidbase.BufferCalculationResult;
import com.ailab.chemistry.domain.acidbase.BufferPerturbationRequest;
import com.ailab.chemistry.domain.acidbase.BufferPerturbationResult;
import com.ailab.chemistry.domain.acidbase.BufferPreparationRequest;
import com.ailab.chemistry.domain.acidbase.BufferPreparationResult;
import com.ailab.chemistry.domain.acidbase.BufferSystem;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;

public interface BufferCalculationService {

    BufferCalculationResult calculateBuffer(BufferCalculationRequest request);

    BufferPreparationResult calculatePreparation(BufferPreparationRequest request);

    BufferPerturbationResult addStrongAcidOrBase(BufferPerturbationRequest request);

    BufferCalculationResult calculateDilution(BufferCalculationRequest request, Volume finalVolume);

    BufferSystem resolveBufferSystem(
            String acidSpeciesCode,
            String baseSpeciesCode,
            Temperature temperature,
            String solventCode
    );
}
