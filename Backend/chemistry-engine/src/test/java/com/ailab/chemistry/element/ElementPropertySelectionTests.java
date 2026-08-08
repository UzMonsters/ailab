package com.ailab.chemistry.element;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.ailab.chemistry.domain.element.property.ElementPropertyRepository;
import com.ailab.chemistry.infrastructure.persistence.element.property.ElementPropertyRepositoryImpl;
import com.ailab.chemistry.infrastructure.persistence.element.property.InMemoryElementPropertyRepository;

import static org.assertj.core.api.Assertions.assertThat;

class ElementPropertySelectionTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void testNormalConfigWithoutActiveProfileOrDatabaseFailsStartup() {
        contextRunner
                .withUserConfiguration(ElementPropertyRepositoryImpl.class)
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .isInstanceOf(UnsatisfiedDependencyException.class);
                });
    }

    @Test
    void testTestProfileUsesInMemoryRepository() {
        contextRunner
                .withPropertyValues("spring.profiles.active=test")
                .withUserConfiguration(InMemoryElementPropertyRepository.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ElementPropertyRepository.class);
                    assertThat(context.getBean(ElementPropertyRepository.class))
                            .isInstanceOf(InMemoryElementPropertyRepository.class);
                });
    }

    @Test
    void testStandaloneEngineProfileUsesInMemoryRepository() {
        contextRunner
                .withPropertyValues("spring.profiles.active=standalone-engine")
                .withUserConfiguration(InMemoryElementPropertyRepository.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ElementPropertyRepository.class);
                    assertThat(context.getBean(ElementPropertyRepository.class))
                            .isInstanceOf(InMemoryElementPropertyRepository.class);
                });
    }
}
