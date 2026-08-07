package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.gas.GasMixture;
import com.ailab.chemistry.domain.gas.GasMixtureResult;
import com.ailab.chemistry.domain.gas.GasStateRequest;
import com.ailab.chemistry.domain.gas.GasStateResult;
import com.ailab.chemistry.domain.gas.GasStateTransformation;

public interface GasLawService {
    GasStateResult calculateState(GasStateRequest request);
    GasMixtureResult calculateMixture(GasMixture request);
    GasStateResult calculateTransformation(GasStateTransformation request);
}
