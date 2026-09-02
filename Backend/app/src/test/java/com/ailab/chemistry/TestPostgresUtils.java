package com.ailab.chemistry;

import org.junit.jupiter.api.Assumptions;

import java.sql.Connection;
import java.sql.DriverManager;

public final class TestPostgresUtils {

    public static final String LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/ai_laboratory";
    public static final String LOCAL_DB_USER = "postgres";
    public static final String LOCAL_DB_PASS = System.getProperty("DB_PASSWORD", System.getenv().getOrDefault("DB_PASSWORD", "Sardorbek.01"));

    private TestPostgresUtils() {}

    public static boolean isPostgresAvailable() {
        try (Connection conn = DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASS)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void assumePostgresAvailable() {
        Assumptions.assumeTrue(isPostgresAvailable(), "Local PostgreSQL database is not accessible at " + LOCAL_DB_URL);
    }
}
