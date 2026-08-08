package com.ailab.chemistry.element;

import com.ailab.chemistry.domain.element.ElementRepository;
import com.ailab.chemistry.infrastructure.persistence.element.ElementRepositoryImpl;
import com.ailab.chemistry.infrastructure.persistence.element.InMemoryElementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class RepositorySelectionTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void testNormalConfigWithoutActiveProfileOrDatabaseFailsStartup() {
        contextRunner
                .withUserConfiguration(ElementRepositoryImpl.class)
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure()
                            .hasMessageContaining("elementRepositoryImpl")
                            .hasMessageContaining("No qualifying bean");
                });
    }

    @Test
    void testTestProfileUsesInMemoryRepository() {
        contextRunner
                .withUserConfiguration(ElementRepositoryImpl.class, InMemoryElementRepository.class)
                .withPropertyValues("spring.profiles.active=test")
                .run(context -> {
                    assertThat(context).hasSingleBean(ElementRepository.class);
                    ElementRepository repo = context.getBean(ElementRepository.class);
                    assertThat(repo).isInstanceOf(InMemoryElementRepository.class);
                });
    }

    @Test
    void testStandaloneEngineProfileUsesInMemoryRepository() {
        contextRunner
                .withUserConfiguration(ElementRepositoryImpl.class, InMemoryElementRepository.class)
                .withPropertyValues("spring.profiles.active=standalone-engine")
                .run(context -> {
                    assertThat(context).hasSingleBean(ElementRepository.class);
                    ElementRepository repo = context.getBean(ElementRepository.class);
                    assertThat(repo).isInstanceOf(InMemoryElementRepository.class);
                });
    }
}
