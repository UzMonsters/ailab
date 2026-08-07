package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.acidbase.PolyproticTitrationCurveResult;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationPointResult;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationRequest;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.List;

public interface PolyproticTitrationService {

    PolyproticTitrationPointResult calculatePoint(
            PolyproticTitrationRequest request,
            Volume addedTitrantVolume
    );

    PolyproticTitrationCurveResult calculateCurve(
            PolyproticTitrationRequest request,
            List<Volume> addedVolumes
    );

    PolyproticTitrationCurveResult calculateCharacteristicPoints(
            PolyproticTitrationRequest request
    );
}
