package com.ailab.chemistry.infrastructure.persistence.electrochemistry;

import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalReferenceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ElectrochemicalReferenceRepositoryConfiguration {

    @Bean
    @Profile("test & !local")
    @ConditionalOnMissingBean(ElectrochemicalReferenceRepository.class)
    ElectrochemicalReferenceRepository standaloneElectrochemicalReferenceRepository() {
        return InMemoryElectrochemicalReferenceRepository.reference();
    }
}
