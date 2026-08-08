package com.ailab.chemistry.service;

import com.ailab.chemistry.api.GasLawService;
import com.ailab.chemistry.domain.gas.GasLawCalculator;
import com.ailab.chemistry.domain.gas.GasMixture;
import com.ailab.chemistry.domain.gas.GasMixtureResult;
import com.ailab.chemistry.domain.gas.GasStateRequest;
import com.ailab.chemistry.domain.gas.GasStateResult;
import com.ailab.chemistry.domain.gas.GasStateTransformation;
import org.springframework.stereotype.Service;

@Service
public class GasLawServiceImpl implements GasLawService {
    private final GasLawCalculator calculator = new GasLawCalculator();

    @Override
    public GasStateResult calculateState(GasStateRequest request) {
        return calculator.calculateState(request);
    }

    @Override
    public GasMixtureResult calculateMixture(GasMixture request) {
        return calculator.calculateMixture(request);
    }

    @Override
    public GasStateResult calculateTransformation(GasStateTransformation request) {
        return calculator.calculateTransformation(request);
    }
}
