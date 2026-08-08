-- Flyway Migration V30: Seed Temperature-Dependent Thermodynamic Correlations
-- Dataset Version: thermodynamic-temperature-functions-v1.0.0

INSERT INTO chemistry.thermodynamic_temperature_dataset_versions (version, description, publication_date)
VALUES (
    'thermodynamic-temperature-functions-v1.0.0',
    'Minimal phase-specific Shomate heat-capacity correlation subset for Phase 8C temperature corrections',
    '2026-08-06'
)
ON CONFLICT (version) DO NOTHING;

INSERT INTO chemistry.thermodynamic_temperature_source_documents (source_identifier, citation, publisher, reuse_limitations)
VALUES (
    'NIST-WEBBOOK-SHOMATE',
    'NIST Chemistry WebBook Shomate heat capacity correlations; Chase 1998',
    'National Institute of Standards and Technology',
    'NIST SRD 69 copyright applies; subset used for educational verification with source attribution.'
)
ON CONFLICT (source_identifier) DO NOTHING;

WITH source AS (
    SELECT source_identifier, citation, reuse_limitations
    FROM chemistry.thermodynamic_temperature_source_documents
    WHERE source_identifier = 'NIST-WEBBOOK-SHOMATE'
),
values_table(
    compound_code,
    physical_state,
    coefficient_a,
    coefficient_b,
    coefficient_c,
    coefficient_d,
    coefficient_e,
    coefficient_f,
    coefficient_g,
    coefficient_h,
    temperature_min_kelvin,
    temperature_max_kelvin
) AS (
    VALUES
        ('COMP-H2', 'GAS', 33.066178, -11.363417, 11.432816, -2.772874, -0.158558, -9.980797, 172.707974, 0.000000, 298.0000, 1000.0000),
        ('COMP-O2', 'GAS', 31.322340, -20.235310, 57.866440, -36.506240, -0.007374, -8.903471, 246.794500, 0.000000, 100.0000, 700.0000),
        ('COMP-O2', 'GAS', 30.032350, 8.772972, -3.988133, 0.788313, -0.741599, -11.324680, 236.166300, 0.000000, 700.0000, 2000.0000),
        ('COMP-H2O', 'GAS', 30.092000, 6.832514, 6.793435, -2.534480, 0.082139, -250.881000, 223.396700, -241.826400, 500.0000, 1700.0000),
        ('COMP-H2O', 'LIQUID', -203.606000, 1523.290000, -3196.413000, 2474.455000, 3.855326, -256.547800, -488.716300, -285.830400, 298.0000, 500.0000),
        ('COMP-CO2', 'GAS', 24.997350, 55.186960, -33.691370, 7.948387, -0.136638, -403.607500, 228.243100, -393.522400, 298.0000, 1200.0000),
        ('COMP-CO', 'GAS', 25.567590, 6.096130, 4.054656, -2.671301, 0.131021, -118.008900, 227.366500, -110.527100, 298.0000, 1300.0000),
        ('COMP-CH4', 'GAS', -0.703029, 108.477300, -42.521570, 5.862788, 0.678565, -76.843760, 158.716300, -74.873100, 298.0000, 1300.0000)
)
INSERT INTO chemistry.thermodynamic_temperature_correlations (
    dataset_version,
    compound_code,
    physical_state,
    correlation_type,
    coefficient_a,
    coefficient_b,
    coefficient_c,
    coefficient_d,
    coefficient_e,
    coefficient_f,
    coefficient_g,
    coefficient_h,
    temperature_min_kelvin,
    temperature_max_kelvin,
    heat_capacity_unit,
    enthalpy_unit,
    entropy_unit,
    scaling_convention,
    source_identifier,
    citation,
    reuse_limitations
)
SELECT
    'thermodynamic-temperature-functions-v1.0.0',
    v.compound_code,
    v.physical_state,
    'SHOMATE',
    v.coefficient_a,
    v.coefficient_b,
    v.coefficient_c,
    v.coefficient_d,
    v.coefficient_e,
    v.coefficient_f,
    v.coefficient_g,
    v.coefficient_h,
    v.temperature_min_kelvin,
    v.temperature_max_kelvin,
    'J/(mol*K)',
    'kJ/mol',
    'J/(mol*K)',
    'Shomate equation with t=T/1000; Cp J/(mol*K); H-H(298.15 K) kJ/mol; S J/(mol*K)',
    s.source_identifier,
    s.citation,
    s.reuse_limitations
FROM values_table v
CROSS JOIN source s
ON CONFLICT (
    dataset_version,
    compound_code,
    physical_state,
    correlation_type,
    temperature_min_kelvin,
    temperature_max_kelvin,
    source_identifier
) DO NOTHING;
