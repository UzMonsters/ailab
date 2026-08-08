package com.ailab.chemistry.infrastructure.persistence.acidbase;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSet;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSetRepository;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
@Primary
@Profile("!(test | standalone-engine)")
@Transactional(readOnly = true)
public class JpaActivityParameterSetRepositoryAdapter implements ActivityParameterSetRepository {
    private final SpringDataJpaActivityParameterSetRepository repository;

    public JpaActivityParameterSetRepositoryAdapter(SpringDataJpaActivityParameterSetRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ActivityParameterSet> findBy(ActivityModel model, Temperature temperature, String solventCode) {
        if (model == ActivityModel.IDEAL) {
            return Optional.of(new ActivityParameterSet(ActivityModel.IDEAL, solventCode, temperature, BigDecimal.ONE, BigDecimal.ZERO, new BigDecimal("999"), "ideal", "no activity correction", "n/a"));
        }
        return repository.findByModelIgnoreCaseAndTemperatureCelsiusAndSolventCodeIgnoreCase(
                        model.name(),
                        temperature.in(TemperatureUnit.CELSIUS).setScale(2, RoundingMode.HALF_UP),
                        solventCode
                )
                .map(entity -> new ActivityParameterSet(
                        ActivityModel.valueOf(entity.getModel()),
                        entity.getSolventCode(),
                        Temperature.of(entity.getTemperatureCelsius(), TemperatureUnit.CELSIUS),
                        entity.getDaviesA(),
                        entity.getMinIonicStrength(),
                        entity.getMaxIonicStrength(),
                        entity.getSourceDocument(),
                        entity.getEvidence(),
                        entity.getLicense()
                ));
    }
}
