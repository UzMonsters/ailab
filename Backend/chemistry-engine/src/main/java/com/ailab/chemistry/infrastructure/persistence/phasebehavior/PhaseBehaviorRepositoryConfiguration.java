package com.ailab.chemistry.infrastructure.persistence.phasebehavior;

import com.ailab.chemistry.domain.phasebehavior.PhaseBehaviorRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class PhaseBehaviorRepositoryConfiguration {

    @Bean
    @Profile("test & !local")
    @ConditionalOnMissingBean(PhaseBehaviorRepository.class)
    PhaseBehaviorRepository standalonePhaseBehaviorRepository() {
        return InMemoryPhaseBehaviorRepository.reference();
    }
}
