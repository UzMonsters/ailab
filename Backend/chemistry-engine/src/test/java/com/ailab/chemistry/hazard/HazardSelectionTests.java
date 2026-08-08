package com.ailab.chemistry.hazard;

import com.ailab.chemistry.domain.hazard.HazardProfileRepository;
import com.ailab.chemistry.infrastructure.persistence.compound.CatalogElementMassProvider;
import com.ailab.chemistry.infrastructure.persistence.element.InMemoryElementRepository;
import com.ailab.chemistry.infrastructure.persistence.hazard.HazardProfileRepositoryImpl;
import com.ailab.chemistry.infrastructure.persistence.hazard.InMemoryHazardProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class HazardSelectionTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void testNormalConfigWithoutDatabaseFailsStartup() {
        contextRunner
                .withUserConfiguration(HazardProfileRepositoryImpl.class)
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void testTestProfileSelectsInMemoryHazardRepository() {
        contextRunner
                .withUserConfiguration(
                        HazardProfileRepositoryImpl.class,
                        InMemoryHazardProfileRepository.class,
                        CatalogElementMassProvider.class,
                        InMemoryElementRepository.class
                )
                .withPropertyValues("spring.profiles.active=test")
                .run(context -> {
                    assertThat(context).hasSingleBean(HazardProfileRepository.class);
                    assertThat(context.getBean(HazardProfileRepository.class))
                            .isInstanceOf(InMemoryHazardProfileRepository.class);
                    assertThat(context.getBean(HazardProfileRepository.class).count()).isEqualTo(55);
                });
    }

    @Test
    void testStandaloneEngineProfileSelectsInMemoryHazardRepository() {
        contextRunner
                .withUserConfiguration(
                        HazardProfileRepositoryImpl.class,
                        InMemoryHazardProfileRepository.class,
                        CatalogElementMassProvider.class,
                        InMemoryElementRepository.class
                )
                .withPropertyValues("spring.profiles.active=standalone-engine")
                .run(context -> {
                    assertThat(context).hasSingleBean(HazardProfileRepository.class);
                    assertThat(context.getBean(HazardProfileRepository.class))
                            .isInstanceOf(InMemoryHazardProfileRepository.class);
                });
    }
}
