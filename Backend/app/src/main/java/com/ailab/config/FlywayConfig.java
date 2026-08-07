package com.ailab.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Value("${app.flyway.enabled:true}")
    private boolean flywayEnabled;

    @Bean
    public Flyway identityFlyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/identity")
                .table("flyway_schema_history")
                .baselineOnMigrate(true)
                .load();
        if (flywayEnabled) {
            flyway.migrate();
        }
        return flyway;
    }

    @Bean
    public Flyway chemistryFlyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/chemistry")
                .schemas("chemistry")
                .createSchemas(true)
                .table("flyway_schema_history_chemistry")
                .baselineOnMigrate(true)
                .load();
        if (flywayEnabled) {
            flyway.migrate();
        }
        return flyway;
    }
}
