package com.ailab.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Configuration
public class FlywayConfig {

    @Value("${app.flyway.enabled:true}")
    private boolean flywayEnabled;

    @Bean
    public Flyway identityFlyway(DataSource dataSource) {
        resetHistoryIfOwnerTableMissing(dataSource, "users", "flyway_schema_history");
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/identity")
                .table("flyway_schema_history")
                .baselineOnMigrate(true)
                .baselineVersion(org.flywaydb.core.api.MigrationVersion.fromVersion("0"))
                .cleanDisabled(false)
                .load();
        if (flywayEnabled) {
            flyway.migrate();
        }
        return flyway;
    }

    @Bean
    @DependsOn("identityFlyway")
    public Flyway chemistryFlyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/chemistry")
                .schemas("chemistry")
                .createSchemas(true)
                .table("flyway_schema_history_chemistry")
                .baselineOnMigrate(true)
                .cleanDisabled(false)
                .load();
        if (flywayEnabled) {
            flyway.migrate();
        }
        return flyway;
    }

    @Bean
    @DependsOn("identityFlyway")
    public Flyway workspaceFlyway(DataSource dataSource) {
        resetHistoryIfOwnerTableMissing(dataSource, "workspaces", "flyway_schema_history_workspace");
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/workspace")
                .table("flyway_schema_history_workspace")
                .baselineOnMigrate(true)
                .baselineVersion(org.flywaydb.core.api.MigrationVersion.fromVersion("0"))
                .cleanDisabled(false)
                .load();
        if (flywayEnabled) {
            flyway.migrate();
        }
        return flyway;
    }

    @Bean(name = "flyway")
    @Primary
    @DependsOn({"identityFlyway", "chemistryFlyway", "workspaceFlyway"})
    public Flyway flyway(Flyway workspaceFlyway) {
        return workspaceFlyway;
    }

    private void resetHistoryIfOwnerTableMissing(DataSource dataSource, String ownerTable, String historyTable) {
        try (Connection conn = dataSource.getConnection()) {
            boolean ownerExists = tableExists(conn, ownerTable);
            boolean historyExists = tableExists(conn, historyTable);
            if (!ownerExists && historyExists) {
                try (Statement statement = conn.createStatement()) {
                    statement.execute("DROP TABLE IF EXISTS " + historyTable);
                }
            }
        } catch (Exception ignored) {
            // Let Flyway surface the real migration problem with its normal diagnostics.
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws Exception {
        try (ResultSet rs = conn.getMetaData().getTables(null, "public", tableName, null)) {
            return rs.next();
        }
    }
}
