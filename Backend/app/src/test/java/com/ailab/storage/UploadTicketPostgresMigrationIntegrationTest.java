package com.ailab.storage;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class UploadTicketPostgresMigrationIntegrationTest {
    private static final String POSTGRES_IMAGE = "postgres:15-alpine";
    private static final String DB_NAME = "ai_laboratory";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";
    private static final String CONTAINER_NAME = "ailab-postgres-upload-ticket-it-" + UUID.randomUUID().toString().substring(0, 8);
    private static int mappedPort;

    @BeforeAll
    static void startPostgres() throws Exception {
        Assumptions.assumeTrue(run("docker", "info").exitCode() == 0, "Docker CLI is not available");
        Assumptions.assumeTrue(run("docker", "pull", POSTGRES_IMAGE).exitCode() == 0, "Pinned PostgreSQL image is not available");
        CommandResult started = run("docker", "run", "-d", "--name", CONTAINER_NAME, "-P",
                "-e", "POSTGRES_DB=" + DB_NAME,
                "-e", "POSTGRES_USER=" + USER,
                "-e", "POSTGRES_PASSWORD=" + PASSWORD,
                POSTGRES_IMAGE);
        Assumptions.assumeTrue(started.exitCode() == 0, "PostgreSQL container could not be started: " + started.output());
        mappedPort = mappedPort();
        waitForJdbc();
        migrate();
    }

    @AfterAll
    static void stopPostgres() throws Exception {
        run("docker", "rm", "-f", CONTAINER_NAME);
    }

    @Test
    void appliesIdentityAndWorkspaceMigrationsAndCreatesUploadTicketSchema() throws Exception {
        try (Connection connection = connect()) {
            String version = queryString(connection, "SELECT version()");
            assertThat(version).contains("PostgreSQL 15");

            assertThat(queryLong(connection, "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true"))
                    .isGreaterThanOrEqualTo(6);
            assertThat(queryString(connection, "SELECT version FROM flyway_schema_history WHERE success = true ORDER BY installed_rank DESC LIMIT 1"))
                    .isIn("6", "7");
            assertThat(queryLong(connection, "SELECT COUNT(*) FROM flyway_schema_history_workspace WHERE success = true AND version <> '0'"))
                    .isEqualTo(5);
            assertThat(queryString(connection, "SELECT version FROM flyway_schema_history_workspace WHERE success = true ORDER BY installed_rank DESC LIMIT 1"))
                    .isEqualTo("5");

            assertThat(tableExists(connection, "upload_tickets")).isTrue();
            assertThat(columns(connection, "upload_tickets")).contains(
                    "id",
                    "token_hash",
                    "asset_id",
                    "actor_id",
                    "scope",
                    "storage_key",
                    "allowed_mime",
                    "max_size_bytes",
                    "expected_checksum",
                    "actual_checksum",
                    "actual_size_bytes",
                    "actual_mime",
                    "status",
                    "expires_at",
                    "uploaded_at",
                    "completed_at",
                    "workspace_id",
                    "preview_id",
                    "variant",
                    "created_at",
                    "updated_at");
            assertThat(indexes(connection, "upload_tickets")).contains(
                    "upload_tickets_pkey",
                    "upload_tickets_token_hash_key",
                    "idx_upload_tickets_token_hash",
                    "idx_upload_tickets_actor_scope",
                    "idx_upload_tickets_asset_scope",
                    "idx_upload_tickets_status",
                    "idx_upload_tickets_expires_at",
                    "idx_upload_tickets_workspace_preview");
            assertThat(checks(connection, "upload_tickets")).contains(
                    "upload_tickets_status_chk",
                    "upload_tickets_size_chk",
                    "upload_tickets_actual_size_chk");
        }
    }

    @Test
    void atomicClaimAllowsOnlyOneConcurrentOwner() throws Exception {
        String id = "ticket_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection connection = connect()) {
            connection.createStatement().executeUpdate("""
                    INSERT INTO upload_tickets (
                        id, token_hash, asset_id, actor_id, scope, storage_key,
                        allowed_mime, max_size_bytes, status, expires_at
                    ) VALUES (
                        '%s', '%s', 'asset_1', 'user_1', 'ADMIN_ASSET', 'assets/asset_1',
                        'image/png', 10485760, 'ISSUED', now() + interval '15 minutes'
                    )
                    """.formatted(id, "hash_" + UUID.randomUUID().toString().replace("-", "")));
        }

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger claimed = new AtomicInteger();
        try (var executor = Executors.newFixedThreadPool(2)) {
            for (int i = 0; i < 2; i++) {
                executor.submit(() -> {
                    ready.countDown();
                    try {
                        start.await(5, TimeUnit.SECONDS);
                        try (Connection connection = connect()) {
                            int rows = connection.createStatement().executeUpdate("""
                                    UPDATE upload_tickets
                                    SET status = 'UPLOADING', updated_at = now()
                                    WHERE id = '%s'
                                      AND status = 'ISSUED'
                                      AND expires_at > now()
                                    """.formatted(id));
                            if (rows == 1) {
                                claimed.incrementAndGet();
                            }
                        }
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                });
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
        }

        assertThat(claimed.get()).isEqualTo(1);
        try (Connection connection = connect()) {
            assertThat(queryString(connection, "SELECT status FROM upload_tickets WHERE id = '" + id + "'"))
                    .isEqualTo("UPLOADING");
        }
    }

    private static void migrate() {
        Flyway.configure()
                .dataSource(jdbcUrl(), USER, PASSWORD)
                .locations("classpath:db/migration/identity")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .table("flyway_schema_history")
                .load()
                .migrate();
        Flyway.configure()
                .dataSource(jdbcUrl(), USER, PASSWORD)
                .locations("classpath:db/migration/workspace")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .table("flyway_schema_history_workspace")
                .load()
                .migrate();
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl(), USER, PASSWORD);
    }

    private static String jdbcUrl() {
        return "jdbc:postgresql://localhost:" + mappedPort + "/" + DB_NAME;
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (var rs = connection.getMetaData().getTables(null, "public", tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static List<String> columns(Connection connection, String tableName) throws SQLException {
        try (var statement = connection.prepareStatement("""
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = ?
                ORDER BY ordinal_position
                """)) {
            statement.setString(1, tableName);
            try (var rs = statement.executeQuery()) {
                var result = new java.util.ArrayList<String>();
                while (rs.next()) {
                    result.add(rs.getString(1));
                }
                return result;
            }
        }
    }

    private static List<String> indexes(Connection connection, String tableName) throws SQLException {
        try (var statement = connection.prepareStatement("""
                SELECT indexname
                FROM pg_indexes
                WHERE schemaname = 'public' AND tablename = ?
                ORDER BY indexname
                """)) {
            statement.setString(1, tableName);
            try (var rs = statement.executeQuery()) {
                var result = new java.util.ArrayList<String>();
                while (rs.next()) {
                    result.add(rs.getString(1));
                }
                return result;
            }
        }
    }

    private static List<String> checks(Connection connection, String tableName) throws SQLException {
        try (var statement = connection.prepareStatement("""
                SELECT conname
                FROM pg_constraint
                WHERE conrelid = ?::regclass AND contype = 'c'
                ORDER BY conname
                """)) {
            statement.setString(1, tableName);
            try (var rs = statement.executeQuery()) {
                var result = new java.util.ArrayList<String>();
                while (rs.next()) {
                    result.add(rs.getString(1));
                }
                return result;
            }
        }
    }

    private static long queryLong(Connection connection, String sql) throws SQLException {
        try (var rs = connection.createStatement().executeQuery(sql)) {
            assertThat(rs.next()).isTrue();
            return rs.getLong(1);
        }
    }

    private static String queryString(Connection connection, String sql) throws SQLException {
        try (var rs = connection.createStatement().executeQuery(sql)) {
            assertThat(rs.next()).isTrue();
            return rs.getString(1);
        }
    }

    private static int mappedPort() throws Exception {
        CommandResult port = run("docker", "port", CONTAINER_NAME, "5432/tcp");
        Assumptions.assumeTrue(port.exitCode() == 0, "Could not resolve mapped PostgreSQL port");
        String line = port.output().trim().lines().findFirst().orElseThrow();
        return Integer.parseInt(line.substring(line.lastIndexOf(':') + 1));
    }

    private static void waitForJdbc() throws Exception {
        long deadline = System.nanoTime() + Duration.ofSeconds(45).toNanos();
        while (System.nanoTime() < deadline) {
            try (Connection ignored = connect()) {
                return;
            } catch (SQLException ignored) {
                Thread.sleep(1_000);
            }
        }
        Assumptions.abort("PostgreSQL container did not become ready");
    }

    private static CommandResult run(String... command) throws Exception {
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        return new CommandResult(exitCode, output);
    }

    private record CommandResult(int exitCode, String output) {
    }
}
