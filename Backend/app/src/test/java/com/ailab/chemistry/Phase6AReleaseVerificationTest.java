package com.ailab.chemistry;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.reaction.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles({"migration-test", "local"})
class Phase6AReleaseVerificationTest {

    static final String LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/ai_laboratory";
    static final String LOCAL_DB_USER = "postgres";
    static final String LOCAL_DB_PASS = "Sardorbek.01";

    @org.junit.jupiter.api.BeforeEach
    void checkPostgres() {
        TestPostgresUtils.assumePostgresAvailable();
    }

    @BeforeAll
    static void setUpClass() {
        if (!TestPostgresUtils.isPostgresAvailable()) {
            return;
        }
        try (Connection conn = DriverManager.getConnection(TestPostgresUtils.LOCAL_DB_URL, TestPostgresUtils.LOCAL_DB_USER, TestPostgresUtils.LOCAL_DB_PASS)) {
            conn.createStatement().execute("DROP SCHEMA IF EXISTS chemistry CASCADE;");
            conn.createStatement().execute("CREATE SCHEMA chemistry;");
            conn.createStatement().execute("DROP TABLE IF EXISTS users CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS refresh_tokens CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE;");
        } catch (Exception ignored) {
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        if (TestPostgresUtils.isPostgresAvailable()) {
            registry.add("spring.datasource.url", () -> TestPostgresUtils.LOCAL_DB_URL);
            registry.add("spring.datasource.username", () -> TestPostgresUtils.LOCAL_DB_USER);
            registry.add("spring.datasource.password", () -> TestPostgresUtils.LOCAL_DB_PASS);
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReactionCatalogService reactionCatalogService;

    @Autowired
    private CompoundCatalogService compoundCatalogService;

    @Autowired
    private ElementCatalogService elementCatalogService;

    @Autowired
    private ReactionRepository reactionRepository;

    @Test
    @DisplayName("Verify PostgreSQL Clean-Install and Migration V1 through V18")
    void testFlywayMigrations() {
        Integer flywayVersion = jdbcTemplate.queryForObject(
                "SELECT max(cast(version as integer)) FROM chemistry.flyway_schema_history_chemistry WHERE success = true", Integer.class);
        assertThat(flywayVersion).isGreaterThanOrEqualTo(18);

    }

    @Test
    @DisplayName("Verify Reaction Database Core Statistics and Counts in PostgreSQL")
    void testReactionDatabaseStatistics() {
        Integer catalogVersionCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_catalog_versions", Integer.class);
        Integer sourceDocCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_source_documents", Integer.class);
        Integer typeDefCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_type_definitions", Integer.class);
        Integer reactionCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reactions", Integer.class);
        Integer aliasCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_aliases", Integer.class);
        Integer termCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_terms", Integer.class);
        Integer reactantTermCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_terms WHERE side = 'REACTANT'", Integer.class);
        Integer productTermCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_terms WHERE side = 'PRODUCT'", Integer.class);
        Integer conditionSetCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_condition_sets", Integer.class);
        Integer catalystCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_catalysts", Integer.class);
        Integer typeAssignmentCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_type_assignments", Integer.class);

        Integer curatedTypeCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_type_assignments WHERE derivation_basis = 'CURATED_REFERENCE'", Integer.class);
        Integer derivedTypeCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reaction_type_assignments WHERE derivation_basis = 'SAFE_RULE_DERIVED'", Integer.class);

        Integer reversibleCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reactions WHERE directionality = 'REVERSIBLE'", Integer.class);
        Integer irreversibleCount = jdbcTemplate.queryForObject("SELECT count(*) FROM chemistry.reactions WHERE directionality = 'IRREVERSIBLE'", Integer.class);

        Integer distinctReactants = jdbcTemplate.queryForObject("SELECT count(DISTINCT compound_id) FROM chemistry.reaction_terms WHERE side = 'REACTANT'", Integer.class);
        Integer distinctProducts = jdbcTemplate.queryForObject("SELECT count(DISTINCT compound_id) FROM chemistry.reaction_terms WHERE side = 'PRODUCT'", Integer.class);
        Integer distinctCatalysts = jdbcTemplate.queryForObject("SELECT count(DISTINCT compound_id) FROM chemistry.reaction_catalysts WHERE compound_id IS NOT NULL", Integer.class);

        System.out.println("=== Phase 6A Reaction Database Statistics ===");
        System.out.println("Catalog Versions: " + catalogVersionCount);
        System.out.println("Source Documents: " + sourceDocCount);
        System.out.println("Type Definitions: " + typeDefCount);
        System.out.println("Reactions: " + reactionCount + " (Irreversible: " + irreversibleCount + ", Reversible: " + reversibleCount + ")");
        System.out.println("Aliases: " + aliasCount);
        System.out.println("Terms: " + termCount + " (Reactants: " + reactantTermCount + ", Products: " + productTermCount + ")");
        System.out.println("Condition Sets: " + conditionSetCount);
        System.out.println("Catalysts: " + catalystCount);
        System.out.println("Type Assignments: " + typeAssignmentCount + " (Curated: " + curatedTypeCount + ", Derived: " + derivedTypeCount + ")");
        System.out.println("Distinct Reactant Compounds: " + distinctReactants);
        System.out.println("Distinct Product Compounds: " + distinctProducts);
        System.out.println("Distinct Catalyst Compounds: " + distinctCatalysts);

        assertThat(catalogVersionCount).isEqualTo(1);
        assertThat(sourceDocCount).isEqualTo(3);
        assertThat(typeDefCount).isEqualTo(16);
        assertThat(reactionCount).isEqualTo(28);
        assertThat(aliasCount).isEqualTo(26);
        assertThat(termCount).isEqualTo(105);
        assertThat(reactantTermCount).isEqualTo(52);
        assertThat(productTermCount).isEqualTo(53);
        assertThat(conditionSetCount).isEqualTo(3);
        assertThat(catalystCount).isEqualTo(1);
        assertThat(typeAssignmentCount).isEqualTo(59);
        assertThat(curatedTypeCount).isEqualTo(49);
        assertThat(derivedTypeCount).isEqualTo(10);
        assertThat(reversibleCount).isEqualTo(5);
        assertThat(irreversibleCount).isEqualTo(23);
    }

    @Test
    @DisplayName("Verify Database Constraints Enforcement")
    void testPostgresConstraints() {
        // 1. Duplicate reaction code
        assertThatThrownBy(() -> jdbcTemplate.execute(
                "INSERT INTO chemistry.reactions (id, reaction_code, primary_name, original_equation, normalized_equation, canonical_balanced_equation, reaction_signature, directionality, catalog_version_id, source_document_id) " +
                "VALUES ('99999999-9999-9999-9999-999999999999', 'RXN-WATER-SYNTHESIS', 'Dup', '2H2+O2->2H2O', '2H2+O2->2H2O', '2H2+O2->2H2O', 'sig', 'IRREVERSIBLE', 'reaction-core-v1.0.0', 'CRC-HANDBOOK-104')"
        )).isNotNull();

        // 2. Orphan compound term
        assertThatThrownBy(() -> jdbcTemplate.execute(
                "INSERT INTO chemistry.reaction_terms (reaction_id, compound_id, compound_code, formula, side, coefficient, term_order) " +
                "VALUES ('11111111-1111-1111-1111-111111111101', '00000000-0000-0000-0000-000000000000', 'COMP-NONEXISTENT', 'X', 'REACTANT', 1, 99)"
        )).isNotNull();

        // 3. Negative coefficient constraint
        assertThatThrownBy(() -> jdbcTemplate.execute(
                "INSERT INTO chemistry.reaction_terms (reaction_id, compound_id, compound_code, formula, side, coefficient, term_order) " +
                "VALUES ('11111111-1111-1111-1111-111111111101', '650b152a-3a54-334b-9006-627007c122b0', 'COMP-H2', 'H2', 'REACTANT', -1, 99)"
        )).isNotNull();

        // 4. Invalid side constraint
        assertThatThrownBy(() -> jdbcTemplate.execute(
                "INSERT INTO chemistry.reaction_terms (reaction_id, compound_id, compound_code, formula, side, coefficient, term_order) " +
                "VALUES ('11111111-1111-1111-1111-111111111101', '650b152a-3a54-334b-9006-627007c122b0', 'COMP-H2', 'H2', 'INVALID_SIDE', 1, 99)"
        )).isNotNull();

        // 5. Duplicate term on same side constraint
        assertThatThrownBy(() -> jdbcTemplate.execute(
                "INSERT INTO chemistry.reaction_terms (reaction_id, compound_id, compound_code, formula, side, coefficient, term_order) " +
                "VALUES ('11111111-1111-1111-1111-111111111101', '650b152a-3a54-334b-9006-627007c122b0', 'COMP-H2', 'H2', 'REACTANT', 1, 99)"
        )).isNotNull();
    }

    @Test
    @DisplayName("Verify ReactionCatalogService Application Integration and Repository Profile Selection")
    void testReactionCatalogServiceIntegration() {
        assertThat(reactionRepository.getClass().getSimpleName()).contains("JpaReactionRepositoryAdapter");

        ReactionDetails waterSyn = reactionCatalogService.getByCode("RXN-WATER-SYNTHESIS");
        assertThat(waterSyn).isNotNull();
        assertThat(waterSyn.canonicalEquation()).isEqualTo("2H2 + O2 -> 2H2O");

        List<ReactionSummary> h2Reactants = reactionCatalogService.findByReactant("COMP-H2");
        assertThat(h2Reactants).isNotEmpty();

        List<ReactionSummary> h2oProducts = reactionCatalogService.findByProduct("COMP-H2O");
        assertThat(h2oProducts).isNotEmpty();

        List<ReactionSummary> co2Reactions = reactionCatalogService.findInvolvingCompound("COMP-CO2");
        assertThat(co2Reactions.size()).isGreaterThanOrEqualTo(10);

        List<ReactionSummary> combustionRxns = reactionCatalogService.findByReactionType("COMBUSTION");
        assertThat(combustionRxns).isNotEmpty();

        List<ReactionSummary> reversibleRxns = reactionCatalogService.findReversible();
        assertThat(reversibleRxns).isNotEmpty();

        // Verify existing services remain fully functional
        assertThat(compoundCatalogService.listCompounds()).hasSizeGreaterThanOrEqualTo(55);
        assertThat(elementCatalogService.listElements()).hasSize(118);
    }
}
