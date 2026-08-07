package com.ailab.chemistry.physicalproperty;

import com.ailab.chemistry.domain.physicalproperty.CompoundPhysicalPropertyProfileRepository;
import com.ailab.chemistry.infrastructure.persistence.compound.CatalogElementMassProvider;
import com.ailab.chemistry.infrastructure.persistence.element.InMemoryElementRepository;
import com.ailab.chemistry.infrastructure.persistence.physicalproperty.CompoundPhysicalPropertyProfileRepositoryImpl;
import com.ailab.chemistry.infrastructure.persistence.physicalproperty.InMemoryCompoundPhysicalPropertyProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class PhysicalPropertySelectionTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void testNormalConfigWithoutDatabaseFailsStartup() {
        contextRunner
                .withUserConfiguration(CompoundPhysicalPropertyProfileRepositoryImpl.class)
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void testTestProfileSelectsInMemoryPhysicalPropertyRepository() {
        contextRunner
                .withUserConfiguration(
                        CompoundPhysicalPropertyProfileRepositoryImpl.class,
                        InMemoryCompoundPhysicalPropertyProfileRepository.class,
                        CatalogElementMassProvider.class,
                        InMemoryElementRepository.class
                )
                .withPropertyValues("spring.profiles.active=test")
                .run(context -> {
                    assertThat(context).hasSingleBean(CompoundPhysicalPropertyProfileRepository.class);
                    assertThat(context.getBean(CompoundPhysicalPropertyProfileRepository.class))
                            .isInstanceOf(InMemoryCompoundPhysicalPropertyProfileRepository.class);
                    assertThat(context.getBean(CompoundPhysicalPropertyProfileRepository.class).count()).isEqualTo(55);
                });
    }

    @Test
    void testStandaloneEngineProfileSelectsInMemoryPhysicalPropertyRepository() {
        contextRunner
                .withUserConfiguration(
                        CompoundPhysicalPropertyProfileRepositoryImpl.class,
                        InMemoryCompoundPhysicalPropertyProfileRepository.class,
                        CatalogElementMassProvider.class,
                        InMemoryElementRepository.class
                )
                .withPropertyValues("spring.profiles.active=standalone-engine")
                .run(context -> {
                    assertThat(context).hasSingleBean(CompoundPhysicalPropertyProfileRepository.class);
                    assertThat(context.getBean(CompoundPhysicalPropertyProfileRepository.class))
                            .isInstanceOf(InMemoryCompoundPhysicalPropertyProfileRepository.class);
                });
    }
}
