INSERT INTO chemistry.laboratory_dataset_versions (dataset_id, version, immutable_snapshot, description) VALUES
('laboratory-equipment-reference-v1.1.0', '1.1.0', TRUE, 'Corrected minimal operational equipment dataset with sourced performance-qualified records.'),
('laboratory-container-reference-v1.1.0', '1.1.0', TRUE, 'Corrected minimal operational container dataset with sourced profile and compatibility records.')
ON CONFLICT (dataset_id) DO NOTHING;

INSERT INTO chemistry.laboratory_source_documents (
    source_code, title, citation, edition, table_or_section, page_or_record_identifier, reuse_terms, source_url, accessed_on, evidence_status
) VALUES
('OHAUS-PX224-2026', 'OHAUS PX224 Pioneer Analytical balance specifications', 'OHAUS Pioneer Analytical PX224 product specifications: maximum capacity 220 g, readability 0.1 mg, linearity +/- 0.0002 g, repeatability typical 0.0001 g, working environment limits.', 'online product record accessed 2026-08-06', 'Specifications', 'PX224 item number 30428792', 'Use as cited manufacturer specification metadata.', 'https://pl.ohaus.com/en-ap/products/balances-scales/analytical-balances/pioneer-analytical-%281%29/electronic-balance-px224/', '2026-08-06', 'SOURCED_REFERENCE_VALUE'),
('THERMO-ORION-A211-REVD', 'Thermo Scientific Orion Star A211 specification sheet', 'Thermo Scientific Orion Star A211 specification sheet S-STARA211-E-0216-RevD: pH range -2.000 to 20.000, pH resolution 0.1/0.01/0.001, relative accuracy +/-0.002, temperature range -5 to 105 C.', 'RevD, 2016-02', 'Specifications', 'S-STARA211-E-0216-RevD', 'Use as cited manufacturer specification metadata.', 'https://assets.fishersci.com/TFS-Assets/LSG/Specification-Sheets/S-STARA211-E-0216-RevD_HIRES.pdf', '2026-08-06', 'SOURCED_REFERENCE_VALUE'),
('IKA-CMAG-HS7-2024', 'IKA C-MAG HS 7 data sheet', 'IKA C-MAG HS 7 data sheet: speed range 100-1500 rpm, stirring quantity max 10 L water, heating temperature range 50-500 C, permissible ambient 5-40 C and relative humidity 80%.', 'online data sheet accessed 2026-08-06', 'Technical Data', 'C-MAG HS 7 Package data sheet', 'Use as cited manufacturer specification metadata.', 'https://bernerlab.no/wp-content/uploads/2024/04/050420241712309105-Data_Sheet_C-MAG_HS_7_Package-1.pdf', '2026-08-06', 'SOURCED_REFERENCE_VALUE'),
('DWK-KIMAX-28014B-100-2026', 'DWK KIMBLE KIMAX Colorware volumetric flask specifications', 'DWK KIMBLE KIMAX Colorware Volumetric Flask, Class A, Blue, 100 mL, catalog 28014B-100: ASTM E288 Class A, borosilicate glass ASTM E438 Type I Class A, tolerance +/-0.08 mL.', 'online product record accessed 2026-08-06', 'Product specifications', 'Catalog Number 28014B-100', 'Use as cited manufacturer specification metadata.', 'https://www.dwk.com/na/kimble-kimax-colorware-volumetric-flask-class-a-blue-100-ml-28014b-100', '2026-08-06', 'SOURCED_REFERENCE_VALUE'),
('THERMO-NALGENE-N319-0500-2026', 'Thermo Scientific Nalgene certified narrow-mouth HDPE bottle specifications', 'Thermo Scientific Nalgene Certified Narrow-Mouth HDPE Bottle with Polypropylene Screw Closure, catalog N319-0500: capacity 500 mL, brim 520 mL, HDPE material, polypropylene closure, ambient temperature and pressure leakproof note for water.', 'online product record accessed 2026-08-06', 'Specifications and note', 'Catalog N319-0500', 'Use as cited manufacturer specification metadata.', 'https://www.thermofisher.com/order/catalog/product/N319-0500', '2026-08-06', 'SOURCED_REFERENCE_VALUE')
ON CONFLICT (source_code) DO NOTHING;

INSERT INTO chemistry.equipment_reference_profiles (
    profile_id, dataset_id, equipment_type, display_name, manufacturer, model_identifier,
    provenance_source_code, provenance_note, performance_qualified, model_or_standard_class, evidence_status
) VALUES
('EQ-OHAUS-PX224-MASS', 'laboratory-equipment-reference-v1.1.0', 'ANALYTICAL_BALANCE', 'OHAUS PX224 Pioneer Analytical Balance', 'OHAUS', 'PX224 item 30428792', 'OHAUS-PX224-2026', 'Performance-qualified mass measurement profile from manufacturer specifications.', TRUE, 'OHAUS PX224 item 30428792', 'SOURCED_REFERENCE_VALUE'),
('EQ-THERMO-ORION-A211-PH-METER', 'laboratory-equipment-reference-v1.1.0', 'PH_METER', 'Thermo Scientific Orion Star A211 pH Benchtop Meter', 'Thermo Scientific', 'Orion Star A211', 'THERMO-ORION-A211-REVD', 'Performance-qualified pH and temperature measurement profile from manufacturer specification sheet.', TRUE, 'Orion Star A211', 'SOURCED_REFERENCE_VALUE'),
('EQ-DWK-KIMAX-28014B-100-VOLUMETRIC', 'laboratory-equipment-reference-v1.1.0', 'VOLUMETRIC_FLASK', 'DWK KIMBLE KIMAX Class A 100 mL Volumetric Flask', 'DWK Life Sciences', '28014B-100', 'DWK-KIMAX-28014B-100-2026', 'Performance-qualified precise contained-volume apparatus profile from manufacturer specifications.', TRUE, 'KIMBLE KIMAX 28014B-100 ASTM Class A', 'SOURCED_REFERENCE_VALUE'),
('EQ-IKA-CMAG-HS7', 'laboratory-equipment-reference-v1.1.0', 'HOT_PLATE', 'IKA C-MAG HS 7 magnetic stirrer with heating plate', 'IKA', 'C-MAG HS 7', 'IKA-CMAG-HS7-2024', 'Performance-qualified heating and stirring profile from manufacturer data sheet.', TRUE, 'IKA C-MAG HS 7', 'SOURCED_REFERENCE_VALUE'),
('EQ-INACTIVE-BALANCE-FIXTURE', 'laboratory-equipment-reference-v1.1.0', 'ANALYTICAL_BALANCE', 'Inactive balance fixture', 'OHAUS', 'inactive fixture', 'OHAUS-PX224-2026', 'Inactive filtering fixture; must not satisfy production lookup.', TRUE, 'inactive fixture', 'SOURCED_REFERENCE_VALUE')
ON CONFLICT (profile_id) DO NOTHING;

UPDATE chemistry.equipment_reference_profiles SET is_active = FALSE WHERE profile_id = 'EQ-INACTIVE-BALANCE-FIXTURE';

INSERT INTO chemistry.equipment_capabilities (
    capability_id, profile_id, capability_type, quantity, unit, minimum_value, maximum_value,
    resolution_value, accuracy_value, uncertainty_value, capacity_value, provenance_source_code,
    provenance_note, original_unit, normalized_unit, environmental_restrictions, source_record_id, evidence_status
) VALUES
('CAP-OHAUS-PX224-MASS', 'EQ-OHAUS-PX224-MASS', 'MEASURE', 'MASS', 'g', 0.00000000, 220.00000000, 0.00010000, 0.00020000, NULL, 220.00000000, 'OHAUS-PX224-2026', 'Readability is resolution; linearity is represented as accuracy, not resolution.', 'g; mg', 'g', '10-30 C and <=80% RH non-condensing for stated working environment', 'PX224-specifications', 'SOURCED_REFERENCE_VALUE'),
('CAP-THERMO-A211-PH', 'EQ-THERMO-ORION-A211-PH-METER', 'MEASURE', 'PH', 'pH', -2.00000000, 20.00000000, 0.00100000, 0.00200000, NULL, NULL, 'THERMO-ORION-A211-REVD', 'pH range, resolution and relative accuracy from manufacturer specification sheet.', 'pH', 'pH', 'meter/electrode conditions must match caller-supplied calibration policy', 'A211-pH-specifications', 'SOURCED_REFERENCE_VALUE'),
('CAP-THERMO-A211-TEMP', 'EQ-THERMO-ORION-A211-PH-METER', 'MEASURE', 'TEMPERATURE', 'degC', -5.00000000, 105.00000000, 0.10000000, 0.10000000, NULL, NULL, 'THERMO-ORION-A211-REVD', 'Temperature range, resolution and relative accuracy from manufacturer specification sheet.', 'degC; degF', 'degC', 'requires compatible ATC probe', 'A211-temperature-specifications', 'SOURCED_REFERENCE_VALUE'),
('CAP-DWK-28014B-100-VOLUME', 'EQ-DWK-KIMAX-28014B-100-VOLUMETRIC', 'MEASURE', 'VOLUME', 'mL', 100.00000000, 100.00000000, 0.08000000, 0.08000000, NULL, 100.00000000, 'DWK-KIMAX-28014B-100-2026', 'Class A tolerance is represented as accuracy for contained volume.', 'mL', 'mL', 'calibrated to contain; no direct heating or pressure containment capability', '28014B-100-specifications', 'SOURCED_REFERENCE_VALUE'),
('CAP-IKA-HS7-HEAT', 'EQ-IKA-CMAG-HS7', 'HEAT', 'TEMPERATURE', 'degC', 50.00000000, 500.00000000, NULL, NULL, NULL, NULL, 'IKA-CMAG-HS7-2024', 'Heating temperature range only; does not claim accurate sample temperature measurement.', 'degC', 'degC', 'ambient 5-40 C; <=80% RH', 'C-MAG-HS7-heating-range', 'SOURCED_REFERENCE_VALUE'),
('CAP-IKA-HS7-STIR', 'EQ-IKA-CMAG-HS7', 'STIR', 'SPEED', 'rpm', 100.00000000, 1500.00000000, NULL, NULL, NULL, 1500.00000000, 'IKA-CMAG-HS7-2024', 'Speed range from manufacturer data sheet.', 'rpm', 'rpm', 'water stirring quantity max 10 L; ambient 5-40 C; <=80% RH', 'C-MAG-HS7-speed-range', 'SOURCED_REFERENCE_VALUE')
ON CONFLICT (capability_id) DO NOTHING;

INSERT INTO chemistry.equipment_calibration_requirements (
    requirement_id, capability_id, calibration_required, interval_seconds, due_soon_seconds, provenance_source_code, provenance_note
) VALUES
('CALREQ-OHAUS-PX224-MASS', 'CAP-OHAUS-PX224-MASS', TRUE, 2592000, 259200, 'LAB-POLICY-CAL-2026', 'Internal governed policy requiring caller-supplied balance calibration record every 30 days; no completion record is seeded.'),
('CALREQ-THERMO-A211-PH', 'CAP-THERMO-A211-PH', TRUE, 2592000, 259200, 'LAB-POLICY-CAL-2026', 'Internal governed policy requiring caller-supplied pH calibration record; no completion record is seeded.'),
('CALREQ-THERMO-A211-TEMP', 'CAP-THERMO-A211-TEMP', TRUE, 2592000, 259200, 'LAB-POLICY-CAL-2026', 'Internal governed policy requiring caller-supplied temperature calibration record; no completion record is seeded.'),
('CALREQ-DWK-28014B-VOLUME', 'CAP-DWK-28014B-100-VOLUME', FALSE, NULL, NULL, 'DWK-KIMAX-28014B-100-2026', 'Manufacturer class/tolerance record; no runtime calibration completion is seeded.'),
('CALREQ-IKA-HS7-HEAT', 'CAP-IKA-HS7-HEAT', FALSE, NULL, NULL, 'IKA-CMAG-HS7-2024', 'No calibration completion is seeded.'),
('CALREQ-IKA-HS7-STIR', 'CAP-IKA-HS7-STIR', FALSE, NULL, NULL, 'IKA-CMAG-HS7-2024', 'No calibration completion is seeded.')
ON CONFLICT (requirement_id) DO NOTHING;

INSERT INTO chemistry.container_reference_profiles (
    profile_id, dataset_id, container_type, material, closure_type, closure_material, geometry_description,
    nominal_capacity_ml, maximum_working_volume_ml, min_temperature_c, max_temperature_c, max_pressure_bar,
    provenance_source_code, provenance_note, performance_qualified, model_or_standard_class, evidence_status
) VALUES
('CON-DWK-KIMAX-28014B-100-VOLUMETRIC', 'laboratory-container-reference-v1.1.0', 'VOLUMETRIC_FLASK', 'BOROSILICATE_GLASS', 'STOPPER', 'BOROSILICATE_GLASS', 'narrow-neck ASTM Class A volumetric flask calibrated to contain', 100.00000000, 100.00000000, 0.00000000, 40.00000000, NULL, 'DWK-KIMAX-28014B-100-2026', 'Room-temperature aqueous contained-volume workflow only; no heating or pressure rating.', TRUE, 'KIMBLE KIMAX 28014B-100 ASTM Class A', 'SOURCED_REFERENCE_VALUE'),
('CON-HDPE-NARROW-MOUTH-500', 'laboratory-container-reference-v1.1.0', 'BOTTLE', 'HDPE', 'SCREW_CAP', 'POLYPROPYLENE', 'Nalgene certified narrow-mouth HDPE bottle with polypropylene screw closure', 500.00000000, 450.00000000, 0.00000000, 40.00000000, NULL, 'THERMO-NALGENE-N319-0500-2026', 'Ambient temperature and pressure bottle workflow; not pressure rated.', TRUE, 'Thermo Scientific Nalgene N319-0500', 'SOURCED_REFERENCE_VALUE'),
('CON-INACTIVE-FIXTURE', 'laboratory-container-reference-v1.1.0', 'BOTTLE', 'HDPE', 'SCREW_CAP', 'POLYPROPYLENE', 'inactive filtering fixture', 500.00000000, 450.00000000, 0.00000000, 40.00000000, NULL, 'THERMO-NALGENE-N319-0500-2026', 'Inactive filtering fixture; must not satisfy production lookup.', TRUE, 'inactive fixture', 'SOURCED_REFERENCE_VALUE')
ON CONFLICT (profile_id) DO NOTHING;

UPDATE chemistry.container_reference_profiles SET is_active = FALSE WHERE profile_id = 'CON-INACTIVE-FIXTURE';

INSERT INTO chemistry.container_compatibility_records (
    compatibility_id, dataset_id, compound_or_family, physical_state, container_material, closure_material,
    compatibility_status, concentration_condition, min_temperature_c, max_temperature_c, contact_duration_limit,
    source_code, evidence_status, source_record_id, source_defined_boundaries
) VALUES
('COMPAT-V11-BORO-WATER-AQ-48H', 'laboratory-container-reference-v1.1.0', 'COMP-H2O', 'AQUEOUS', 'BOROSILICATE_GLASS', NULL, 'COMPATIBLE', 'water only', 0.00000000, 22.00000000, '48 hr exposure basis from compatibility source warning', 'COLE-PARMER-CHEM-COMPAT', 'SOURCED_REFERENCE_VALUE', 'chemical-compatibility-database-water-borosilicate-glass-48h', TRUE),
('COMPAT-V11-HDPE-WATER-AQ-48H', 'laboratory-container-reference-v1.1.0', 'COMP-H2O', 'AQUEOUS', 'HDPE', 'POLYPROPYLENE', 'COMPATIBLE_WITH_LIMITS', 'water and low-viscosity aqueous samples only', 0.00000000, 22.00000000, '48 hr exposure basis from compatibility source warning; bottle source separately states ambient temperature and pressure leakproof note for water', 'COLE-PARMER-CHEM-COMPAT', 'SOURCED_REFERENCE_VALUE', 'chemical-compatibility-database-water-hdpe-48h', TRUE),
('COMPAT-V11-HDPE-AROMATIC-LIQ-48H', 'laboratory-container-reference-v1.1.0', 'FAMILY-AROMATIC-HYDROCARBONS', 'LIQUID', 'HDPE', 'POLYPROPYLENE', 'INCOMPATIBLE', 'source-defined aromatic hydrocarbons family', 0.00000000, 22.00000000, '48 hr exposure basis from compatibility source warning', 'COLE-PARMER-CHEM-COMPAT', 'SOURCED_REFERENCE_VALUE', 'chemical-compatibility-database-aromatic-hydrocarbons-hdpe-48h', TRUE)
ON CONFLICT (compatibility_id) DO NOTHING;
