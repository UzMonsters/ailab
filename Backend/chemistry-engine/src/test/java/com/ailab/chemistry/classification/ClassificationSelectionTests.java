package com.ailab.chemistry.classification;

import com.ailab.chemistry.domain.classification.ClassificationProfileRepository;
import com.ailab.chemistry.infrastructure.persistence.classification.ClassificationProfileRepositoryImpl;
import com.ailab.chemistry.infrastructure.persistence.classification.InMemoryClassificationProfileRepository;
import com.ailab.chemistry.infrastructure.persistence.compound.CatalogElementMassProvider;
import com.ailab.chemistry.infrastructure.persistence.element.InMemoryElementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassificationSelectionTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void testNormalConfigWithoutDatabaseFailsStartup() {
        contextRunner
                .withUserConfiguration(ClassificationProfileRepositoryImpl.class)
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void testTestProfileSelectsInMemoryClassificationRepository() {
        contextRunner
                .withUserConfiguration(
                        ClassificationProfileRepositoryImpl.class,
                        InMemoryClassificationProfileRepository.class,
                        CatalogElementMassProvider.class,
                        InMemoryElementRepository.class
                )
                .withPropertyValues("spring.profiles.active=test")
                .run(context -> {
                    assertThat(context).hasSingleBean(ClassificationProfileRepository.class);
                    assertThat(context.getBean(ClassificationProfileRepository.class))
                            .isInstanceOf(InMemoryClassificationProfileRepository.class);
                    assertThat(context.getBean(ClassificationProfileRepository.class).count()).isEqualTo(55);
                });
    }

    @Test
    void testStandaloneEngineProfileSelectsInMemoryClassificationRepository() {
        contextRunner
                .withUserConfiguration(
                        ClassificationProfileRepositoryImpl.class,
                        InMemoryClassificationProfileRepository.class,
                        CatalogElementMassProvider.class,
                        InMemoryElementRepository.class
                )
                .withPropertyValues("spring.profiles.active=standalone-engine")
                .run(context -> {
                    assertThat(context).hasSingleBean(ClassificationProfileRepository.class);
                    assertThat(context.getBean(ClassificationProfileRepository.class))
                            .isInstanceOf(InMemoryClassificationProfileRepository.class);
                });
    }
}
