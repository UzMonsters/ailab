package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ChemistryEngineService;
import com.ailab.chemistry.api.EngineInfo;
import org.springframework.stereotype.Service;

@Service
public class ChemistryEngineServiceImpl implements ChemistryEngineService {
    @Override
    public EngineInfo getEngineInfo() {
        return new EngineInfo("Chemistry Engine", "1.0.0", "UP");
    }
}
