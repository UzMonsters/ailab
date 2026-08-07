package com.ailab.chemistry.infrastructure.persistence.kinetics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.kinetics.ArrheniusParameters;
import com.ailab.chemistry.domain.kinetics.KineticEvidenceStatus;
import com.ailab.chemistry.domain.kinetics.KineticProfile;
import com.ailab.chemistry.domain.kinetics.KineticProfileRepository;
import com.ailab.chemistry.domain.kinetics.KineticProvenance;
import com.ailab.chemistry.domain.kinetics.KineticRateLaw;
import com.ailab.chemistry.domain.kinetics.KineticRateLawTerm;
import com.ailab.chemistry.domain.kinetics.KineticReferenceConditions;
import com.ailab.chemistry.domain.kinetics.RateConstant;
import com.ailab.chemistry.domain.kinetics.RateConstantDimension;
import com.ailab.chemistry.domain.kinetics.ReactionOrder;
import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("!test & !standalone-engine")
public class JdbcKineticProfileRepositoryAdapter implements KineticProfileRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Autowired
    public JdbcKineticProfileRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<KineticProfile> rowMapper = (rs, rowNum) -> {
        String profileId = rs.getString("profile_id");
        String reactionCode = rs.getString("reaction_code");
        BigDecimal rateConstVal = rs.getBigDecimal("rate_constant_value");
        BigDecimal totalOrder = rs.getBigDecimal("overall_order");

        BigDecimal eaVal = rs.getBigDecimal("activation_energy_kj_mol");
        BigDecimal aFactor = rs.getBigDecimal("pre_exponential_factor_a");
        BigDecimal tempExpN = rs.getBigDecimal("temperature_exponent_n");
        BigDecimal refTKelvin = rs.getBigDecimal("ref_temperature_k");
        BigDecimal minT = rs.getBigDecimal("min_temperature_k");
        BigDecimal maxT = rs.getBigDecimal("max_temperature_k");

        ArrheniusParameters arrhenius = (eaVal != null && aFactor != null)
                ? new ArrheniusParameters(
                aFactor,
                tempExpN != null ? tempExpN : BigDecimal.ZERO,
                refTKelvin != null ? Temperature.of(refTKelvin, TemperatureUnit.KELVIN) : Temperature.of("298.15", TemperatureUnit.KELVIN),
                MolarEnergy.of(eaVal, MolarEnergyUnit.KILOJOULE_PER_MOLE),
                minT != null ? Temperature.of(minT, TemperatureUnit.KELVIN) : null,
                maxT != null ? Temperature.of(maxT, TemperatureUnit.KELVIN) : null,
                tempExpN != null && tempExpN.compareTo(BigDecimal.ZERO) != 0 ? "MODIFIED_ARRHENIUS" : "STANDARD_ARRHENIUS"
        ) : null;

        List<KineticRateLawTerm> terms = loadTermsForProfile(profileId);
        KineticRateLaw rateLaw = KineticRateLaw.of(terms);

        KineticReferenceConditions cond = new KineticReferenceConditions(
                refTKelvin != null ? Temperature.of(refTKelvin, TemperatureUnit.KELVIN) : Temperature.of("298.15", TemperatureUnit.KELVIN),
                rs.getBigDecimal("ref_pressure_bar") != null ? Pressure.of(rs.getBigDecimal("ref_pressure_bar"), PressureUnit.BAR) : Pressure.of("1.000", PressureUnit.BAR),
                rs.getString("solvent"),
                rs.getString("catalyst"),
                rs.getBigDecimal("ph"),
                rs.getBigDecimal("ionic_strength")
        );

        KineticProvenance prov = new KineticProvenance(
                rs.getString("provenance_source_id"),
                rs.getString("provenance_description"),
                rs.getString("provenance_citation"),
                rs.getString("nist_squib"),
                rs.getString("paper_title"),
                rs.getString("authors"),
                rs.getString("journal_name"),
                rs.getObject("publication_year", Integer.class),
                rs.getString("pages"),
                rs.getString("record_url"),
                rs.getString("data_type"),
                rs.getString("experimental_method"),
                rs.getString("uncertainty"),
                rs.getString("original_a_value"),
                rs.getString("original_a_unit"),
                rs.getString("original_k_value"),
                rs.getString("original_k_unit"),
                rs.getString("conversion_factor")
        );

        return new KineticProfile(
                profileId,
                reactionCode,
                rateLaw,
                RateConstant.of(rateConstVal.doubleValue(), RateConstantDimension.ofOrder(totalOrder)),
                arrhenius,
                cond,
                KineticEvidenceStatus.valueOf(rs.getString("evidence_status")),
                prov
        );
    };

    private List<KineticRateLawTerm> loadTermsForProfile(String profileId) {
        String sql = "SELECT compound_code, physical_state, empirical_order FROM chemistry.kinetic_rate_law_terms WHERE profile_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new KineticRateLawTerm(
                rs.getString("compound_code"),
                MatterState.valueOf(rs.getString("physical_state")),
                ReactionOrder.of(rs.getBigDecimal("empirical_order").toPlainString())
        ), profileId);
    }

    @Override
    public Optional<KineticProfile> findByProfileId(String profileId) {
        String sql = "SELECT * FROM chemistry.kinetic_profiles WHERE profile_id = ? AND is_active = TRUE";
        List<KineticProfile> list = jdbcTemplate.query(sql, rowMapper, profileId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<KineticProfile> findByReactionCode(String reactionCode) {
        String sql = "SELECT * FROM chemistry.kinetic_profiles WHERE reaction_code = ? AND is_active = TRUE";
        return jdbcTemplate.query(sql, rowMapper, reactionCode);
    }

    @Override
    public List<KineticProfile> findAll() {
        String sql = "SELECT * FROM chemistry.kinetic_profiles WHERE is_active = TRUE";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
