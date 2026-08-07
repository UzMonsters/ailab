package com.ailab.chemistry;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.stoichiometry.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles({"migration-test", "local"})
class Phase7AReleaseVerificationTest {

    static final String LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/ai_laboratory";
    static final String LOCAL_DB_USER = "postgres";
    static final String LOCAL_DB_PASS = "Sardorbek.01";

    @BeforeAll
    static void setUpClass() {
        try (Connection conn = DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USER, LOCAL_DB_PASS)) {
            conn.createStatement().execute("DROP SCHEMA IF EXISTS chemistry CASCADE;");
            conn.createStatement().execute("CREATE SCHEMA chemistry;");
            conn.createStatement().execute("DROP TABLE IF EXISTS users CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS refresh_tokens CASCADE;");
            conn.createStatement().execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> LOCAL_DB_URL);
        registry.add("spring.datasource.username", () -> LOCAL_DB_USER);
        registry.add("spring.datasource.password", () -> LOCAL_DB_PASS);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StoichiometryService stoichiometryService;

    @Autowired
    private ReactionCatalogService reactionCatalogService;

    @Autowired
    private CompoundCatalogService compoundCatalogService;

    @Test
    @DisplayName("Verify StoichiometryService is Injectable and Works with PostgreSQL V18 Database")
    void testStoichiometryServiceIntegration() {
        assertThat(stoichiometryService).isNotNull();

        // 1. Mass to Moles conversion using PostgreSQL compound molar mass (H2O)
        Mass massH2O = Mass.of("36.03056", MassUnit.GRAM);
        AmountOfSubstance molesH2O = stoichiometryService.convertMassToMoles("COMP-H2O", massH2O);
        assertThat(molesH2O.in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("2.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.05")));

        // 2. 2H2 + O2 -> 2H2O Stoichiometry from reactant
        StoichiometricQuantity qtyH2 = StoichiometricQuantity.fromMoles(AmountOfSubstance.of("2.0", AmountOfSubstanceUnit.MOLE));
        StoichiometryCalculationResult resWater = stoichiometryService.calculateFromReactant("RXN-WATER-SYNTHESIS", "COMP-H2", qtyH2);
        assertThat(resWater.getExpectedProductMoles().get("COMP-H2O").in(AmountOfSubstanceUnit.MOLE))
                .isCloseTo(new BigDecimal("2.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.05")));

        // 3. CH4 + 2O2 -> CO2 + 2H2O Limiting reagent determination
        List<ReactionParticipantQuantity> ch4Inputs = List.of(
                ReactionParticipantQuantity.ofMoles("COMP-CH4", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE)),
                ReactionParticipantQuantity.ofMoles("COMP-O2", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE)) // Limiting
        );
        LimitingReagentResult lrCH4 = stoichiometryService.determineLimitingReagent("RXN-METHANE-COMBUSTION", ch4Inputs);
        assertThat(lrCH4.getPrimaryLimitingCompoundCode()).isEqualTo("COMP-O2");

        // 4. HCl + NaOH -> NaCl + H2O Neutralization
        List<ReactionParticipantQuantity> neutInputs = List.of(
                ReactionParticipantQuantity.ofMoles("COMP-HCL", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE)),
                ReactionParticipantQuantity.ofMoles("COMP-NAOH", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE))
        );
        TheoreticalYieldResult theoNeut = stoichiometryService.calculateTheoreticalYield("RXN-NEUT-HCL-NAOH", neutInputs, "COMP-NACL");
        assertThat(theoNeut.getLimitingReagentResult().isTied()).isTrue();

        // 5. 2H2O2 -> 2H2O + O2 Decomposition
        List<ReactionParticipantQuantity> decompInputs = List.of(
                ReactionParticipantQuantity.ofMoles("COMP-H2O2", AmountOfSubstance.of("2.0", AmountOfSubstanceUnit.MOLE))
        );
        TheoreticalYieldResult theoDecomp = stoichiometryService.calculateTheoreticalYield("RXN-H2O2-DECOMP", decompInputs, "COMP-O2");
        assertThat(theoDecomp.getTheoreticalMoles().in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("1.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.05")));

        // 6. Actual Yield Evaluation & Percent Yield > 100%
        ActualYieldResult actualAbove = stoichiometryService.evaluateActualYield(theoDecomp, Mass.of("50.0", MassUnit.GRAM));
        assertThat(actualAbove.getPercentYield().getStatus()).isEqualTo(YieldStatus.ABOVE_THEORETICAL);
    }
}
