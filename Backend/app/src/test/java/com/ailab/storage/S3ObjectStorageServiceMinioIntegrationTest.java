package com.ailab.storage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class S3ObjectStorageServiceMinioIntegrationTest {
    private static final String MINIO_IMAGE = "greptime/minio:2025.4.22-debian-12-r1";
    private static final String ACCESS_KEY = "minioadmin";
    private static final String SECRET_KEY = "minioadmin";
    private static final String CONTAINER_NAME = "ailab-minio-it-" + UUID.randomUUID().toString().substring(0, 8);
    private static int mappedPort;

    @BeforeAll
    static void startMinio() throws Exception {
        Assumptions.assumeTrue(run("docker", "info").exitCode() == 0, "Docker CLI is not available");
        Assumptions.assumeTrue(run("docker", "pull", MINIO_IMAGE).exitCode() == 0, "Pinned MinIO image is not available");
        CommandResult started = run("docker", "run", "-d", "--name", CONTAINER_NAME, "-P",
                "-e", "MINIO_ROOT_USER=" + ACCESS_KEY,
                "-e", "MINIO_ROOT_PASSWORD=" + SECRET_KEY,
                MINIO_IMAGE);
        Assumptions.assumeTrue(started.exitCode() == 0, "MinIO container could not be started: " + started.output());
        mappedPort = mappedPort();
        waitUntilHealthy();
    }

    @AfterAll
    static void stopMinio() throws Exception {
        run("docker", "rm", "-f", CONTAINER_NAME);
    }

    @Test
    void uploadsDownloadsPresignsAndDeletesAgainstMinio() throws Exception {
        StorageProperties properties = new StorageProperties(
                "s3",
                URI.create("http://localhost:" + mappedPort),
                "us-east-1",
                "ailab-test-" + UUID.randomUUID(),
                ACCESS_KEY,
                SECRET_KEY,
                true,
                true,
                Duration.ofMinutes(10),
                DataSize.ofMegabytes(10),
                DataSize.ofMegabytes(2),
                DataSize.ofMegabytes(10),
                "./storage/objects"
        );
        S3ObjectStorageService storage = new S3ObjectStorageService(properties);
        storage.initializeBucket();

        byte[] bytes = "minio integration payload".getBytes(StandardCharsets.UTF_8);
        String key = "integration/" + UUID.randomUUID() + ".txt";

        StoredObject stored = storage.upload(new StorageUpload(
                key,
                "text/plain",
                bytes.length,
                new ByteArrayInputStream(bytes)
        ));

        assertThat(stored.storageKey()).isEqualTo(key);
        assertThat(storage.exists(key)).isTrue();

        try (StoredObjectDownload download = storage.download(key)) {
            assertThat(download.contentType()).isEqualTo("text/plain");
            assertThat(download.inputStream().readAllBytes()).isEqualTo(bytes);
        }

        assertThat(storage.createPresignedDownloadUrl(key, "payload.txt", Duration.ofMinutes(5)).toString())
                .contains(key);

        storage.delete(key);
        assertThat(storage.exists(key)).isFalse();
    }

    private static int mappedPort() throws Exception {
        CommandResult port = run("docker", "port", CONTAINER_NAME, "9000/tcp");
        Assumptions.assumeTrue(port.exitCode() == 0, "Could not resolve mapped MinIO port");
        String line = port.output().trim().lines().findFirst().orElseThrow();
        return Integer.parseInt(line.substring(line.lastIndexOf(':') + 1));
    }

    private static void waitUntilHealthy() throws Exception {
        long deadline = System.nanoTime() + Duration.ofSeconds(45).toNanos();
        while (System.nanoTime() < deadline) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + mappedPort + "/minio/health/live").openConnection();
                connection.setConnectTimeout(1_000);
                connection.setReadTimeout(1_000);
                if (connection.getResponseCode() == 200) {
                    return;
                }
            } catch (Exception ignored) {
            }
            Thread.sleep(1_000);
        }
        Assumptions.abort("MinIO container did not become healthy");
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
