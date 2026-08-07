package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.acidbase.DistributionFraction;
import com.ailab.chemistry.domain.acidbase.PolyproticEquilibriumRequest;
import com.ailab.chemistry.domain.acidbase.PolyproticEquilibriumResult;
import com.ailab.chemistry.domain.measurement.PhValue;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;

public interface PolyproticEquilibriumService {

    PolyproticEquilibriumResult calculate(PolyproticEquilibriumRequest request);

    List<DistributionFraction> calculateDistribution(String acidFamilyCode, PhValue ph, Temperature temperature);
}
