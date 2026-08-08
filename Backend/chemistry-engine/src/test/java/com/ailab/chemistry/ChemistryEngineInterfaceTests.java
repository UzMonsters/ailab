package com.ailab.chemistry;

import com.ailab.chemistry.api.ChemistryEngineService;
import com.ailab.chemistry.api.EngineInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        FlywayAutoConfiguration.class
})
class ChemistryEngineInterfaceTests {

    @Autowired
    private ChemistryEngineService chemistryEngineService;

    @Test
    void testGetEngineInfoReturnsMetadataInternally() {
        EngineInfo response = chemistryEngineService.getEngineInfo();

        assertThat(response).isNotNull();
        assertThat(response.serviceName()).isEqualTo("Chemistry Engine");
        assertThat(response.engineVersion()).isEqualTo("1.0.0");
        assertThat(response.status()).isEqualTo("UP");
    }
}
