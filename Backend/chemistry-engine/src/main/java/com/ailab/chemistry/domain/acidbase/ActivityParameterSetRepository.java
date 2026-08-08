package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Optional;

public interface ActivityParameterSetRepository {
    Optional<ActivityParameterSet> findBy(ActivityModel model, Temperature temperature, String solventCode);
}
