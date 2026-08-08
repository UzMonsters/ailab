package com.ailab.chemistry.infrastructure.persistence.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.HeatCapacityCorrelation;
import com.ailab.chemistry.domain.thermodynamics.HeatCapacityCorrelationType;
import com.ailab.chemistry.domain.thermodynamics.PolynomialCoefficientSet;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrelationRepository;
import com.ailab.chemistry.domain.thermodynamics.TemperatureValidityRange;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicProvenance;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemoryTemperatureCorrelationRepository implements TemperatureCorrelationRepository {
    public static final String DATASET_VERSION = "thermodynamic-temperature-functions-v1.0.0";
    private static final String SCALING = "Shomate equation with t=T/1000; Cp J/(mol*K); H-H(298.15 K) kJ/mol; S J/(mol*K)";
    private static final ThermodynamicProvenance NIST = new ThermodynamicProvenance("NIST-WEBBOOK-SHOMATE",
            "NIST Chemistry WebBook Shomate heat capacity correlations; Chase 1998",
            "NIST SRD 69 copyright applies; subset used for educational verification with source attribution.");

    private final List<HeatCapacityCorrelation> correlations = List.of(
            shomate("COMP-H2", MatterState.GAS, "298.0", "1000.0",
                    "33.066178", "-11.363417", "11.432816", "-2.772874", "-0.158558", "-9.980797", "172.707974", "0"),
            shomate("COMP-O2", MatterState.GAS, "100.0", "700.0",
                    "31.32234", "-20.23531", "57.86644", "-36.50624", "-0.007374", "-8.903471", "246.7945", "0"),
            shomate("COMP-O2", MatterState.GAS, "700.0", "2000.0",
                    "30.03235", "8.772972", "-3.988133", "0.788313", "-0.741599", "-11.32468", "236.1663", "0"),
            shomate("COMP-H2O", MatterState.GAS, "500.0", "1700.0",
                    "30.09200", "6.832514", "6.793435", "-2.534480", "0.082139", "-250.8810", "223.3967", "-241.8264"),
            shomate("COMP-H2O", MatterState.LIQUID, "298.0", "500.0",
                    "-203.6060", "1523.290", "-3196.413", "2474.455", "3.855326", "-256.5478", "-488.7163", "-285.8304"),
            shomate("COMP-CO2", MatterState.GAS, "298.0", "1200.0",
                    "24.99735", "55.18696", "-33.69137", "7.948387", "-0.136638", "-403.6075", "228.2431", "-393.5224"),
            shomate("COMP-CO", MatterState.GAS, "298.0", "1300.0",
                    "25.56759", "6.096130", "4.054656", "-2.671301", "0.131021", "-118.0089", "227.3665", "-110.5271"),
            shomate("COMP-CH4", MatterState.GAS, "298.0", "1300.0",
                    "-0.703029", "108.4773", "-42.52157", "5.862788", "0.678565", "-76.84376", "158.7163", "-74.87310")
    );

    @Override
    public Optional<HeatCapacityCorrelation> find(String compoundCode, MatterState state, Temperature targetTemperature) {
        return correlations.stream()
                .filter(correlation -> correlation.compoundCode().equalsIgnoreCase(compoundCode))
                .filter(correlation -> correlation.state() == state)
                .filter(correlation -> correlation.validityRange().contains(targetTemperature))
                .sorted(Comparator.comparing(correlation -> correlation.validityRange().minimum()))
                .findFirst();
    }

    @Override
    public List<HeatCapacityCorrelation> findAll() {
        return correlations;
    }

    private static HeatCapacityCorrelation shomate(String compoundCode, MatterState state, String minimum, String maximum,
                                                   String a, String b, String c, String d, String e, String f, String g, String h) {
        return new HeatCapacityCorrelation(compoundCode, state, HeatCapacityCorrelationType.SHOMATE,
                new PolynomialCoefficientSet(a, b, c, d, e, f, g, h),
                new TemperatureValidityRange(Temperature.of(minimum, TemperatureUnit.KELVIN),
                        Temperature.of(maximum, TemperatureUnit.KELVIN)),
                "J/(mol*K)", SCALING, NIST);
    }
}
