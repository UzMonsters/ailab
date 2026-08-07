package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.acidbase.TitrationCurveResult;
import com.ailab.chemistry.domain.acidbase.TitrationPointRequest;
import com.ailab.chemistry.domain.acidbase.TitrationPointResult;
import com.ailab.chemistry.domain.acidbase.TitrationRequest;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.List;

public interface TitrationCalculationService {

    TitrationRequest resolveTitrationSystem(TitrationRequest request);

    TitrationPointResult calculatePoint(TitrationRequest request, Volume addedTitrantVolume);

    TitrationPointResult calculatePoint(TitrationPointRequest request);

    TitrationCurveResult calculateCurve(TitrationRequest request, List<Volume> titrantVolumes);

    TitrationCurveResult calculateCharacteristicPoints(TitrationRequest request);
}
