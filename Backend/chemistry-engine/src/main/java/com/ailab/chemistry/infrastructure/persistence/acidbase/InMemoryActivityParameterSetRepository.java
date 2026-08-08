package com.ailab.chemistry.infrastructure.persistence.acidbase;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSet;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSetRepository;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemoryActivityParameterSetRepository implements ActivityParameterSetRepository {

    private final ActivityParameterSet daviesWater25 = new ActivityParameterSet(
            ActivityModel.DAVIES,
            "COMP-H2O",
            Temperature.of("25.0", TemperatureUnit.CELSIUS),
            new BigDecimal("0.509"),
            BigDecimal.ZERO,
            new BigDecimal("0.5"),
            "CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)",
            "Davies limiting-law A parameter for water at 298.15 K",
            "CRC tabular data are copyrighted; reuse is limited to a minimal cited educational subset in this project"
    );

    @Override
    public Optional<ActivityParameterSet> findBy(ActivityModel model, Temperature temperature, String solventCode) {
        if (model == ActivityModel.IDEAL) {
            return Optional.of(new ActivityParameterSet(ActivityModel.IDEAL, solventCode, temperature, BigDecimal.ONE, BigDecimal.ZERO, new BigDecimal("999"), "ideal", "no activity correction", "n/a"));
        }
        if (model == ActivityModel.DAVIES && daviesWater25.temperature().equals(temperature) && daviesWater25.solventCode().equalsIgnoreCase(solventCode)) {
            return Optional.of(daviesWater25);
        }
        return Optional.empty();
    }
}
