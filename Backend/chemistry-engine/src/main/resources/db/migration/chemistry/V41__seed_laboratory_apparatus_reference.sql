INSERT INTO chemistry.laboratory_dataset_versions (dataset_id, version, immutable_snapshot, description) VALUES
('laboratory-equipment-reference-v1.0.0', '1.0.0', TRUE, 'Small foundational laboratory equipment reference profile set for suitability validation.'),
('laboratory-container-reference-v1.0.0', '1.0.0', TRUE, 'Small foundational laboratory container reference profile set for capacity and compatibility validation.')
ON CONFLICT (dataset_id) DO NOTHING;

INSERT INTO chemistry.laboratory_source_documents (
    source_code, title, citation, edition, table_or_section, page_or_record_identifier, reuse_terms, source_url, accessed_on, evidence_status
) VALUES
('ASTM-E288-2010R2019', 'Laboratory glass volumetric flasks standard metadata', 'ASTM E288-10(2019), Standard Specification for Laboratory Glass Volumetric Flasks.', '2019 reapproval', 'scope and apparatus taxonomy', 'ASTM E288 record', 'Bibliographic metadata only; no proprietary tolerance table copied.', 'https://www.astm.org/e0288-10r19.html', '2026-08-06', 'TYPE_TAXONOMY'),
('ASTM-E287-2019', 'Laboratory glass graduated cylinders standard metadata', 'ASTM E287-19, Standard Specification for Laboratory Glass Graduated Cylinders.', '2019', 'scope and apparatus taxonomy', 'ASTM E287 record', 'Bibliographic metadata only; no proprietary tolerance table copied.', 'https://www.astm.org/e0287-19.html', '2026-08-06', 'TYPE_TAXONOMY'),
('ASTM-E438-1992R2022', 'Borosilicate laboratory glass standard metadata', 'ASTM E438-92(2022), Standard Specification for Glasses in Laboratory Apparatus.', '2022 reapproval', 'glass type taxonomy', 'ASTM E438 record', 'Bibliographic metadata only; no proprietary table copied.', 'https://www.astm.org/e0438-92r22.html', '2026-08-06', 'TYPE_TAXONOMY'),
('NIST-H44-2026', 'NIST weighing device handbook metadata', 'NIST Handbook 44, 2026 edition, Specifications, Tolerances, and Other Technical Requirements for Weighing and Measuring Devices.', '2026', 'weighing devices taxonomy', 'NIST Handbook 44 record', 'Public-domain U.S. government handbook metadata.', 'https://www.nist.gov/pml/owm/handbook-44-current-edition', '2026-08-06', 'TYPE_TAXONOMY'),
('LAB-POLICY-CAL-2026', 'AI Laboratory calibration policy fixture', 'AI Laboratory internal test policy for suitability validation fixtures.', '2026-08-06', 'calibration evaluation tests', 'LAB-POLICY-CAL-2026', 'Internal validation policy; not a manufacturer claim.', NULL, '2026-08-06', 'TYPE_TAXONOMY'),
('COLE-PARMER-CHEM-COMPAT', 'Supplier chemical compatibility chart metadata', 'Cole-Parmer chemical compatibility database, material compatibility lookup records.', 'online record', 'HDPE and borosilicate material compatibility records', 'stable material/chemical lookup labels in this dataset', 'Compatibility labels preserved narrowly; no broad table copied.', 'https://www.coleparmer.com/chemical-resistance', '2026-08-06', 'SOURCED_REFERENCE_VALUE')
ON CONFLICT (source_code) DO NOTHING;

INSERT INTO chemistry.equipment_reference_profiles (
    profile_id, dataset_id, equipment_type, display_name, manufacturer, model_identifier, provenance_source_code, provenance_note
) VALUES
('EQ-ANALYTICAL-BALANCE-TAXONOMY', 'laboratory-equipment-reference-v1.0.0', 'ANALYTICAL_BALANCE', 'Analytical balance taxonomy profile', NULL, NULL, 'NIST-H44-2026', 'Type taxonomy only; no model-specific capacity, resolution or accuracy claim.'),
('EQ-LAB-BALANCE-TAXONOMY', 'laboratory-equipment-reference-v1.0.0', 'LABORATORY_BALANCE', 'Laboratory balance taxonomy profile', NULL, NULL, 'NIST-H44-2026', 'Type taxonomy only; no model-specific performance claim.'),
('EQ-THERMOMETER-TAXONOMY', 'laboratory-equipment-reference-v1.0.0', 'THERMOMETER', 'Thermometer taxonomy profile', NULL, NULL, 'ASTM-E438-1992R2022', 'Type taxonomy only; no model-specific range or accuracy claim.'),
('EQ-VOLUMETRIC-FLASK-TAXONOMY', 'laboratory-equipment-reference-v1.0.0', 'VOLUMETRIC_FLASK', 'Volumetric flask taxonomy profile', NULL, NULL, 'ASTM-E288-2010R2019', 'Type taxonomy only; no copied tolerance table.'),
('EQ-GRADUATED-CYLINDER-TAXONOMY', 'laboratory-equipment-reference-v1.0.0', 'GRADUATED_CYLINDER', 'Graduated cylinder taxonomy profile', NULL, NULL, 'ASTM-E287-2019', 'Type taxonomy only; no copied tolerance table.'),
('EQ-PH-METER-TAXONOMY', 'laboratory-equipment-reference-v1.0.0', 'PH_METER', 'pH meter taxonomy profile', NULL, NULL, 'LAB-POLICY-CAL-2026', 'Capability must be supplied by explicit model profile before suitability.'),
('EQ-HOT-PLATE-TAXONOMY', 'laboratory-equipment-reference-v1.0.0', 'HOT_PLATE', 'Hot plate taxonomy profile', NULL, NULL, 'LAB-POLICY-CAL-2026', 'Heating type taxonomy only; no temperature measurement capability inferred.')
ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.container_reference_profiles (
    profile_id, dataset_id, container_type, material, closure_type, closure_material, geometry_description,
    nominal_capacity_ml, maximum_working_volume_ml, min_temperature_c, max_temperature_c, max_pressure_bar,
    provenance_source_code, provenance_note
) VALUES
('CON-BORO-BEAKER-100ML-TAXONOMY', 'laboratory-container-reference-v1.0.0', 'BEAKER', 'BOROSILICATE_GLASS', 'OPEN', NULL, 'open cylindrical beaker', 100.00000000, 90.00000000, NULL, NULL, NULL, 'ASTM-E438-1992R2022', 'Borosilicate-glass open container taxonomy; not pressure rated.'),
('CON-BORO-ERLENMEYER-TAXONOMY', 'laboratory-container-reference-v1.0.0', 'ERLENMEYER_FLASK', 'BOROSILICATE_GLASS', 'OPEN', NULL, 'open conical flask', 250.00000000, 225.00000000, NULL, NULL, NULL, 'ASTM-E438-1992R2022', 'Borosilicate-glass open container taxonomy; not pressure rated.'),
('CON-VOLUMETRIC-FLASK-TAXONOMY', 'laboratory-container-reference-v1.0.0', 'VOLUMETRIC_FLASK', 'BOROSILICATE_GLASS', 'STOPPER', 'BOROSILICATE_GLASS', 'narrow-neck calibrated flask', 100.00000000, 100.00000000, NULL, NULL, NULL, 'ASTM-E288-2010R2019', 'Volumetric glassware taxonomy; no high-temperature heating suitability asserted.'),
('CON-HDPE-BOTTLE-TAXONOMY', 'laboratory-container-reference-v1.0.0', 'BOTTLE', 'HDPE', 'SCREW_CAP', 'HDPE', 'polymer reagent bottle', 500.00000000, 450.00000000, NULL, NULL, NULL, 'COLE-PARMER-CHEM-COMPAT', 'HDPE bottle taxonomy with narrow compatibility records.'),
('CON-POLYPROPYLENE-TUBE-TAXONOMY', 'laboratory-container-reference-v1.0.0', 'TUBE', 'POLYPROPYLENE', 'SCREW_CAP', 'POLYPROPYLENE', 'polypropylene tube', 50.00000000, 45.00000000, NULL, NULL, NULL, 'COLE-PARMER-CHEM-COMPAT', 'Polypropylene tube taxonomy; no pressure rating asserted.')
ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.container_compatibility_records (
    compatibility_id, dataset_id, compound_or_family, physical_state, container_material, closure_material,
    compatibility_status, concentration_condition, min_temperature_c, max_temperature_c, contact_duration_limit,
    source_code, evidence_status
) VALUES
('COMPAT-BORO-WATER-AQ', 'laboratory-container-reference-v1.0.0', 'COMP-H2O', 'AQUEOUS', 'BOROSILICATE_GLASS', NULL, 'COMPATIBLE', NULL, NULL, NULL, NULL, 'COLE-PARMER-CHEM-COMPAT', 'SOURCED_REFERENCE_VALUE'),
('COMPAT-HDPE-DILUTE-ACID-AQ', 'laboratory-container-reference-v1.0.0', 'FAMILY-DILUTE-ACID', 'AQUEOUS', 'HDPE', 'HDPE', 'COMPATIBLE_WITH_LIMITS', 'dilute aqueous acid family only', 0.00000000, 40.00000000, 'short-term storage only when source-specific limits are satisfied', 'COLE-PARMER-CHEM-COMPAT', 'SOURCED_REFERENCE_VALUE'),
('COMPAT-HDPE-AROMATIC-LIQ', 'laboratory-container-reference-v1.0.0', 'FAMILY-AROMATIC-SOLVENT', 'LIQUID', 'HDPE', 'HDPE', 'INCOMPATIBLE', NULL, NULL, NULL, NULL, 'COLE-PARMER-CHEM-COMPAT', 'SOURCED_REFERENCE_VALUE')
ON CONFLICT (compatibility_id) DO NOTHING;
