package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.ElectrochemistryService;
import com.ailab.chemistry.domain.electrochemistry.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chemistry/electrochemistry")
@Tag(name = "Electrochemistry", description = "Standard cell potential, Nernst non-standard cell potential, and electrolysis calculations")
@SecurityRequirement(name = "bearerAuth")
public class ElectrochemistryController {

    private final ElectrochemistryService electrochemistryService;

    public ElectrochemistryController(ElectrochemistryService electrochemistryService) {
        this.electrochemistryService = electrochemistryService;
    }

    @PostMapping("/standard-cell")
    @Operation(summary = "Calculate standard cell potential", description = "Compute standard cell electromotive force E°cell = E°cathode - E°anode and spontaneous cell direction.")
    public ElectrochemicalCellResult calculateStandardCell(@Valid @RequestBody ElectrochemicalCellRequest request) {
        return electrochemistryService.calculateStandardCell(request);
    }

    @PostMapping("/nernst")
    @Operation(summary = "Calculate non-standard Nernst cell potential", description = "Compute non-standard cell potential Ecell = E°cell - (R·T / (n·F)) · ln(Q) at given concentrations and temperature.")
    public NernstResult calculateNernstCell(@Valid @RequestBody NernstRequest request) {
        return electrochemistryService.calculateNonstandardCell(request);
    }

    @PostMapping("/electrolysis")
    @Operation(summary = "Calculate electrolysis mass/volume yield", description = "Compute mass or volume of substance deposited/gassed during electrolysis using Faraday's laws m = (I · t · M) / (n · F).")
    public ElectrolysisResult calculateElectrolysis(@Valid @RequestBody ElectrolysisRequest request) {
        return electrochemistryService.calculateElectrolysis(request);
    }
}
