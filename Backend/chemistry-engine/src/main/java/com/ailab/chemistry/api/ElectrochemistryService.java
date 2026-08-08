package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalCellRequest;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalCellResult;
import com.ailab.chemistry.domain.electrochemistry.ElectrolysisRequest;
import com.ailab.chemistry.domain.electrochemistry.ElectrolysisResult;
import com.ailab.chemistry.domain.electrochemistry.NernstRequest;
import com.ailab.chemistry.domain.electrochemistry.NernstResult;

public interface ElectrochemistryService {
    ElectrochemicalCellResult calculateStandardCell(ElectrochemicalCellRequest request);

    NernstResult calculateNonstandardCell(NernstRequest request);

    ElectrolysisResult calculateElectrolysis(ElectrolysisRequest request);
}
