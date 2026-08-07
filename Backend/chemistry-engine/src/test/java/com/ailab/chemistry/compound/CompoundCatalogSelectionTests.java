package com.ailab.chemistry.compound;

import com.ailab.chemistry.domain.compound.CompoundRepository;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.infrastructure.persistence.compound.CatalogElementMassProvider;
import com.ailab.chemistry.infrastructure.persistence.compound.CompoundRepositoryImpl;
import com.ailab.chemistry.infrastructure.persistence.compound.InMemoryCompoundRepository;
import com.ailab.chemistry.infrastructure.persistence.element.InMemoryElementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class CompoundCatalogSelectionTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void testNormalConfigWithoutProfileOrDatabaseFailsStartup() {
        contextRunner
                .withUserConfiguration(CompoundRepositoryImpl.class)
                .run(context -> {
                    assertThat(context).hasFailed();
                });
    }

    @Test
    void testTestProfileSelectsInMemoryCompoundRepository() {
        contextRunner
                .withUserConfiguration(
                        CompoundRepositoryImpl.class,
                        InMemoryCompoundRepository.class,
                        CatalogElementMassProvider.class,
                        InMemoryElementRepository.class
                )
                .withPropertyValues("spring.profiles.active=test")
                .run(context -> {
                    assertThat(context).hasSingleBean(CompoundRepository.class);
                    assertThat(context.getBean(CompoundRepository.class))
                            .isInstanceOf(InMemoryCompoundRepository.class);
                    assertThat(context.getBean(CompoundRepository.class).count()).isEqualTo(55);
                });
    }

    @Test
    void testStandaloneEngineProfileSelectsInMemoryCompoundRepository() {
        contextRunner
                .withUserConfiguration(
                        CompoundRepositoryImpl.class,
                        InMemoryCompoundRepository.class,
                        CatalogElementMassProvider.class,
                        InMemoryElementRepository.class
                )
                .withPropertyValues("spring.profiles.active=standalone-engine")
                .run(context -> {
                    assertThat(context).hasSingleBean(CompoundRepository.class);
                    assertThat(context.getBean(CompoundRepository.class))
                            .isInstanceOf(InMemoryCompoundRepository.class);
                });
    }
}
