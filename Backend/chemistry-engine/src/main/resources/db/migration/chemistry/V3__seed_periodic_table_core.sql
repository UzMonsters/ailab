-- Seeding Periodic Table Reference Data
INSERT INTO chemistry.periodic_table_catalog_versions (id, version, data_sources, reference_conditions) VALUES (
  'v1.0.0', '1.0.0', 
  'IUPAC Periodic Table of the Elements, NIST Physical Reference Data', 
  'Standard Temperature and Pressure (STP): 273.15 K, 100 kPa'
);

INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 'Hydrogen', NULL, 1.008, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 1, 1, 'S', '1s1', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'd66532e4-57a1-38c8-b8c6-9d130d179937', 2, 'He', 'Helium', NULL, 4.0026, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 1, 18, 'S', '1s2', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'NOBLE_GAS', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '2117f8d3-e041-36e3-9e28-fae00e623811', 3, 'Li', 'Lithium', NULL, 6.94, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 2, 1, 'S', '[He] 2s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALI_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '234a682d-dffe-3df8-8215-cc70661be6f0', 4, 'Be', 'Beryllium', NULL, 9.0122, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 2, 2, 'S', '[He] 2s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALINE_EARTH_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '1cbab575-f605-389f-a77c-ae2dc59a6d6c', 5, 'B', 'Boron', NULL, 10.81, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 2, 13, 'P', '[He] 2s2 2p1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 'Carbon', NULL, 12.011, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 2, 14, 'P', '[He] 2s2 2p2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '87e4c172-8582-34c0-bdf6-dd0d326b9b93', 7, 'N', 'Nitrogen', NULL, 14.007, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 2, 15, 'P', '[He] 2s2 2p3', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 'Oxygen', NULL, 15.999, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 2, 16, 'P', '[He] 2s2 2p4', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '7e240ab5-d96b-33cd-b915-320767f2fdac', 9, 'F', 'Fluorine', NULL, 18.998, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 2, 17, 'P', '[He] 2s2 2p5', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'a9ed65f0-cfa6-3675-bb3b-6aba27c82050', 10, 'Ne', 'Neon', NULL, 20.180, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 2, 18, 'P', '[He] 2s2 2p6', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'NOBLE_GAS', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '7155e5b7-5b66-3bc4-a257-a1921f04ffe1', 11, 'Na', 'Sodium', 'Natrium', 22.990, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 3, 1, 'S', '[Ne] 3s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALI_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '3618c84c-86f2-3901-95b7-bb5384dfad5e', 12, 'Mg', 'Magnesium', NULL, 24.305, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 3, 2, 'S', '[Ne] 3s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALINE_EARTH_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'e0ce8d80-729b-3602-82ea-4030848d3286', 13, 'Al', 'Aluminium', NULL, 26.982, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 3, 13, 'P', '[Ne] 3s2 3p1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '94b1b79b-6f83-3e4f-b8fc-935d25a947af', 14, 'Si', 'Silicon', NULL, 28.085, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 3, 14, 'P', '[Ne] 3s2 3p2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'f79cae91-c553-3766-a773-c8ec3d8f3e1b', 15, 'P', 'Phosphorus', NULL, 30.974, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 3, 15, 'P', '[Ne] 3s2 3p3', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '85dc52a3-4eef-3bb4-ab47-d8f271e81aae', 16, 'S', 'Sulfur', NULL, 32.06, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 3, 16, 'P', '[Ne] 3s2 3p4', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 'Chlorine', NULL, 35.45, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 3, 17, 'P', '[Ne] 3s2 3p5', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'a04e3603-76e8-3da2-9035-1c85f2d328f6', 18, 'Ar', 'Argon', NULL, 39.948, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 3, 18, 'P', '[Ne] 3s2 3p6', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'NOBLE_GAS', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'd6d79ea8-ba41-3a93-8ca2-523d89baca92', 19, 'K', 'Potassium', 'Kalium', 39.098, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 1, 'S', '[Ar] 4s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALI_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'b410bbd7-d275-3d35-ad8b-e8c2ee54999e', 20, 'Ca', 'Calcium', NULL, 40.078, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 2, 'S', '[Ar] 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALINE_EARTH_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '7b45c009-9fe0-3585-9402-3976d0277da1', 21, 'Sc', 'Scandium', NULL, 44.956, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 3, 'D', '[Ar] 3d1 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '0ee1c364-607d-362b-b575-4ba0a1d866ee', 22, 'Ti', 'Titanium', NULL, 47.867, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 4, 'D', '[Ar] 3d2 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '35012873-3544-3625-b852-33851f0fc133', 23, 'V', 'Vanadium', NULL, 50.942, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 5, 'D', '[Ar] 3d3 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '20df4212-a434-3b4c-870f-ff4db46cecd4', 24, 'Cr', 'Chromium', NULL, 51.996, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 6, 'D', '[Ar] 3d5 4s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '42a57138-f54d-3292-8884-09bd10e5d0ac', 25, 'Mn', 'Manganese', NULL, 54.938, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 7, 'D', '[Ar] 3d5 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'f5133816-62cd-3ec0-af5b-a566977b9ad3', 26, 'Fe', 'Iron', 'Ferrum', 55.845, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 8, 'D', '[Ar] 3d6 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'df6b4c63-0b19-3213-9851-e34a2b872fff', 27, 'Co', 'Cobalt', NULL, 58.933, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 9, 'D', '[Ar] 3d7 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '509719b2-e5fd-3c19-8013-302858ccbc0c', 28, 'Ni', 'Nickel', NULL, 58.693, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 10, 'D', '[Ar] 3d8 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'bf72cc72-a94f-3d56-b1c0-4b00b3a2fdb3', 29, 'Cu', 'Copper', 'Cuprum', 63.546, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 11, 'D', '[Ar] 3d10 4s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '322a89df-4874-31d3-bdea-643a3f5ab0d2', 30, 'Zn', 'Zinc', NULL, 65.38, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 12, 'D', '[Ar] 3d10 4s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '9356c18a-d9c8-399b-933f-3029ab649a41', 31, 'Ga', 'Gallium', NULL, 69.723, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 13, 'P', '[Ar] 3d10 4s2 4p1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '60b0ae93-6e4d-3e37-858c-ff45b5124916', 32, 'Ge', 'Germanium', NULL, 72.630, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 14, 'P', '[Ar] 3d10 4s2 4p2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '42f15c27-a2b5-3a99-bb38-472f38552ca6', 33, 'As', 'Arsenic', NULL, 74.922, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 15, 'P', '[Ar] 3d10 4s2 4p3', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '2789b675-30b0-3f85-b202-04d55b18ed0f', 34, 'Se', 'Selenium', NULL, 78.971, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 16, 'P', '[Ar] 3d10 4s2 4p4', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'fb26ef36-5d23-3496-b74d-1dea5eec6250', 35, 'Br', 'Bromine', NULL, 79.904, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 17, 'P', '[Ar] 3d10 4s2 4p5', 'LIQUID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '32458a28-348d-3b73-918a-a4f8de185b2a', 36, 'Kr', 'Krypton', NULL, 83.798, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 4, 18, 'P', '[Ar] 3d10 4s2 4p6', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'NOBLE_GAS', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '1082356d-d9de-3058-a81f-ff2bae8f4131', 37, 'Rb', 'Rubidium', NULL, 85.468, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 1, 'S', '[Kr] 5s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALI_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '403412d4-7fdd-31df-985b-19bc743811df', 38, 'Sr', 'Strontium', NULL, 87.62, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 2, 'S', '[Kr] 5s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALINE_EARTH_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '6a9bdd61-58b9-3d4d-8260-c3a79b618f30', 39, 'Y', 'Yttrium', NULL, 88.906, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 3, 'D', '[Kr] 4d1 5s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'a18d7d40-70e9-3d83-9053-b9905dac8a02', 40, 'Zr', 'Zirconium', NULL, 91.224, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 4, 'D', '[Kr] 4d2 5s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '0d015e48-ce61-3f85-9c2b-3537e45301e9', 41, 'Nb', 'Niobium', NULL, 92.906, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 5, 'D', '[Kr] 4d4 5s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'dc8e1f73-45e5-383b-a710-fb5d49ce410e', 42, 'Mo', 'Molybdenum', NULL, 95.95, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 6, 'D', '[Kr] 4d5 5s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'd799b6ab-0855-377d-8908-cd505a31358b', 43, 'Tc', 'Technetium', NULL, 98, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 5, 7, 'D', '[Kr] 4d5 5s2', 'SOLID', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'ae7291a6-1efb-3660-9f7f-3ec926717407', 44, 'Ru', 'Ruthenium', NULL, 101.07, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 8, 'D', '[Kr] 4d7 5s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'cbc20e2b-ab5e-3050-8e42-7689767a7db0', 45, 'Rh', 'Rhodium', NULL, 102.91, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 9, 'D', '[Kr] 4d8 5s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '0a89b66f-f941-36f6-88be-0eb5889fbfcb', 46, 'Pd', 'Palladium', NULL, 106.42, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 10, 'D', '[Kr] 4d10', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '359a62af-367a-3efb-86dc-e3783a49756e', 47, 'Ag', 'Silver', 'Argentum', 107.87, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 11, 'D', '[Kr] 4d10 5s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'ae4ca758-2113-3478-a073-516bb257614e', 48, 'Cd', 'Cadmium', NULL, 112.41, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 12, 'D', '[Kr] 4d10 5s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'ca54d820-573c-3176-be5f-02cf51ccc5e3', 49, 'In', 'Indium', NULL, 114.82, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 13, 'P', '[Kr] 4d10 5s2 5p1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '82840137-60f5-3ccc-8cb3-2a54c1c27c31', 50, 'Sn', 'Tin', 'Stannum', 118.71, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 14, 'P', '[Kr] 4d10 5s2 5p2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '03d0671f-cb72-3c68-805c-b2048a9278db', 51, 'Sb', 'Antimony', 'Stibium', 121.76, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 15, 'P', '[Kr] 4d10 5s2 5p3', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '6ad44b4b-a17a-3eeb-a82e-9b549731c1b7', 52, 'Te', 'Tellurium', NULL, 127.60, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 16, 'P', '[Kr] 4d10 5s2 5p4', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '0b9284eb-fe2c-38bc-90e1-4fb95e46cd82', 53, 'I', 'Iodine', NULL, 126.90, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 17, 'P', '[Kr] 4d10 5s2 5p5', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'REACTIVE_NONMETAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'f88198c1-facb-39e4-af98-800207a99029', 54, 'Xe', 'Xenon', NULL, 131.29, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 5, 18, 'P', '[Kr] 4d10 5s2 5p6', 'GAS', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'NOBLE_GAS', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '99a584e6-dfd0-3ebb-8653-f60f085f8bf4', 55, 'Cs', 'Caesium', NULL, 132.91, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 1, 'S', '[Xe] 6s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALI_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '678bd0b1-d318-3fc6-8825-7f9754113558', 56, 'Ba', 'Barium', NULL, 137.33, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 2, 'S', '[Xe] 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'ALKALINE_EARTH_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '295e6ae8-8346-3ef6-bae8-1e285eab9e60', 57, 'La', 'Lanthanum', NULL, 138.91, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 5d1 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'ebe72bab-a295-3727-968b-b3480eb7ed1f', 58, 'Ce', 'Cerium', NULL, 140.12, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f1 5d1 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '1a9b134f-e9e9-3b0d-8f1e-76e50df7ec5f', 59, 'Pr', 'Praseodymium', NULL, 140.91, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f3 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '2ffecda0-c2cc-36b3-bc59-4791c274ecb1', 60, 'Nd', 'Neodymium', NULL, 144.24, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f4 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'd4dd9dc1-b8f8-3c50-acf0-28b36921662d', 61, 'Pm', 'Promethium', NULL, 145, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 6, NULL, 'F', '[Xe] 4f5 6s2', 'SOLID', 'RADIOACTIVE', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '9d388111-ae87-3f65-b05f-bf61126a759e', 62, 'Sm', 'Samarium', NULL, 150.36, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f6 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'b799623c-c9a3-3945-a7db-d28f51b00e66', 63, 'Eu', 'Europium', NULL, 151.96, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f7 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'b585dc57-6eb6-3190-b685-3ec9612b2808', 64, 'Gd', 'Gadolinium', NULL, 157.25, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f7 5d1 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '9aedf65f-a061-303e-886b-ca0cba8a1462', 65, 'Tb', 'Terbium', NULL, 158.93, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f9 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '65ae545a-35e1-3133-a443-8d29b45b890d', 66, 'Dy', 'Dysprosium', NULL, 162.50, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f10 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '41d3acd2-1820-37a4-bbe3-61a48865f401', 67, 'Ho', 'Holmium', NULL, 164.93, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f11 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '7be3fd83-c9fc-3cdd-bca4-9b6241c1ba74', 68, 'Er', 'Erbium', NULL, 167.26, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f12 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'ab897525-e8d5-338d-a674-98134bc8a98b', 69, 'Tm', 'Thulium', NULL, 168.93, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f13 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'c995395f-9bc0-341d-a4ef-992ea97c070e', 70, 'Yb', 'Ytterbium', NULL, 173.05, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, NULL, 'F', '[Xe] 4f14 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '7e4bb7ae-5f35-379a-9a9e-46f70c5db764', 71, 'Lu', 'Lutetium', NULL, 174.97, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 3, 'D', '[Xe] 4f14 5d1 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'LANTHANIDE', 'LANTHANIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '8bc18bd7-f8d6-3e11-afba-4132387359cc', 72, 'Hf', 'Hafnium', NULL, 178.49, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 4, 'D', '[Xe] 4f14 5d2 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'a5f43311-6937-3231-82f3-8cd8ab131307', 73, 'Ta', 'Tantalum', NULL, 180.95, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 5, 'D', '[Xe] 4f14 5d3 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '6b9cb580-b298-362b-8f1a-55d24a18f51b', 74, 'W', 'Tungsten', 'Wolfram', 183.84, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 6, 'D', '[Xe] 4f14 5d4 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'b3d8ed3e-5c5a-3564-be4f-38d4c7e01450', 75, 'Re', 'Rhenium', NULL, 186.21, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 7, 'D', '[Xe] 4f14 5d5 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '87ff52f0-264e-3e37-9aef-4d587f80235e', 76, 'Os', 'Osmium', NULL, 190.23, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 8, 'D', '[Xe] 4f14 5d6 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '86fdef98-209d-3645-be77-1b79205fcf80', 77, 'Ir', 'Iridium', NULL, 192.22, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 9, 'D', '[Xe] 4f14 5d7 6s2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '94d7a5ba-bc4d-3b9b-bc58-b2ff48e22ae8', 78, 'Pt', 'Platinum', NULL, 195.08, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 10, 'D', '[Xe] 4f14 5d9 6s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '17a75f44-71d5-3950-8ac6-8f4f0d179472', 79, 'Au', 'Gold', 'Aurum', 196.97, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 11, 'D', '[Xe] 4f14 5d10 6s1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '134e3bcd-f26a-3c0d-8335-774532bf8315', 80, 'Hg', 'Mercury', 'Hydrargyrum', 200.59, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 12, 'D', '[Xe] 4f14 5d10 6s2', 'LIQUID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '475b8dcd-c297-33c2-9ff1-28d59ebdaefb', 81, 'Tl', 'Thallium', NULL, 204.38, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 13, 'P', '[Xe] 4f14 5d10 6s2 6p1', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '48a5bc29-359f-379b-afcd-c8c4b9564219', 82, 'Pb', 'Lead', 'Plumbum', 207.2, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 14, 'P', '[Xe] 4f14 5d10 6s2 6p2', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '1f5a9f43-4bde-3f3c-aa3c-cad327ba7297', 83, 'Bi', 'Bismuth', NULL, 208.98, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 6, 15, 'P', '[Xe] 4f14 5d10 6s2 6p3', 'SOLID', 'STABLE_OR_HAS_STABLE_ISOTOPES', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '9d5570df-80b2-3607-b576-f720933b25a0', 84, 'Po', 'Polonium', NULL, 209, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 6, 16, 'P', '[Xe] 4f14 5d10 6s2 6p4', 'SOLID', 'RADIOACTIVE', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '1cd55faf-8e34-3755-957a-280a6893aa07', 85, 'At', 'Astatine', NULL, 210, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 6, 17, 'P', '[Xe] 4f14 5d10 6s2 6p5', 'SOLID', 'RADIOACTIVE', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '3e4dbc7a-af53-3780-bd6f-7ab7d57b4f30', 86, 'Rn', 'Radon', NULL, 222, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 6, 18, 'P', '[Xe] 4f14 5d10 6s2 6p6', 'GAS', 'RADIOACTIVE', 'NOBLE_GAS', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '1964bad5-aba3-328a-9779-5e46826b81fe', 87, 'Fr', 'Francium', NULL, 223, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 1, 'S', '[Rn] 7s1', 'SOLID', 'RADIOACTIVE', 'ALKALI_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '2c1f6337-471b-3c0f-874a-b48bfa9ac2a5', 88, 'Ra', 'Radium', NULL, 226, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 2, 'S', '[Rn] 7s2', 'SOLID', 'RADIOACTIVE', 'ALKALINE_EARTH_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '9632e14c-8b51-3e38-9ecf-dfa7d438214c', 89, 'Ac', 'Actinium', NULL, 227, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 6d1 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '4947c074-7646-3ca9-9ac7-53fd25282ef0', 90, 'Th', 'Thorium', NULL, 232.04, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 7, NULL, 'F', '[Rn] 6d2 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '1d8405f4-f4bf-32a3-833a-ae05e120d007', 91, 'Pa', 'Protactinium', NULL, 231.04, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 7, NULL, 'F', '[Rn] 5f2 6d1 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'a19fe2e8-404d-3435-8ba1-d8ca72d985ae', 92, 'U', 'Uranium', NULL, 238.03, 'STANDARD_ATOMIC_WEIGHT', NULL, NULL, 7, NULL, 'F', '[Rn] 5f3 6d1 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'fd706841-f288-35b3-bc80-1bde9d5bca65', 93, 'Np', 'Neptunium', NULL, 237, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f4 6d1 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '048aca52-1531-3af1-81d3-1495b56bd6c3', 94, 'Pu', 'Plutonium', NULL, 244, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f6 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '0bf4996c-630a-3aa4-92f6-66af4b0927bf', 95, 'Am', 'Americium', NULL, 243, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f7 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'dc75f6f7-9f2d-3b7c-a11e-b5866a6f5065', 96, 'Cm', 'Curium', NULL, 247, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f7 6d1 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '31bc2d98-ad5c-3cfa-9a51-4d43970631b4', 97, 'Bk', 'Berkelium', NULL, 247, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f9 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'fc3bda44-30f8-3220-b9ff-72f39cdd1f56', 98, 'Cf', 'Californium', NULL, 251, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f10 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'cef9ce30-caf6-3bd7-8eed-f353520f6229', 99, 'Es', 'Einsteinium', NULL, 252, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f11 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'b3541e8f-5040-37b4-981f-bd884a76af10', 100, 'Fm', 'Fermium', NULL, 257, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f12 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '2f3f9e65-0bb5-3598-bfc6-db1e5fb1ce37', 101, 'Md', 'Mendelevium', NULL, 258, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f13 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '35620d2b-42b2-31aa-a78f-f05b74b7b4f1', 102, 'No', 'Nobelium', NULL, 259, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, NULL, 'F', '[Rn] 5f14 7s2', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'd13d81a0-9587-3ae0-ba12-c541437c1d00', 103, 'Lr', 'Lawrencium', NULL, 262, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 3, 'D', '[Rn] 5f14 7s2 7p1', 'SOLID', 'RADIOACTIVE', 'ACTINIDE', 'ACTINIDE', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'f34b9491-1e21-3b65-9962-326dcca8c83a', 104, 'Rf', 'Rutherfordium', NULL, 267, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 4, 'D', '[Rn] 5f14 6d2 7s2', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'f9419a79-adb9-3e99-aafb-cd194dfad984', 105, 'Db', 'Dubnium', NULL, 268, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 5, 'D', '[Rn] 5f14 6d3 7s2', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'b92c6032-7cd0-38c0-9932-cdb917d8ba75', 106, 'Sg', 'Seaborgium', NULL, 271, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 6, 'D', '[Rn] 5f14 6d4 7s2', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '7e61be35-e175-3771-9a46-006df6d45316', 107, 'Bh', 'Bohrium', NULL, 270, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 7, 'D', '[Rn] 5f14 6d5 7s2', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'dafa432f-9694-3c5b-b564-7dc8cdbda634', 108, 'Hs', 'Hassium', NULL, 277, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 8, 'D', '[Rn] 5f14 6d6 7s2', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'd8812857-6525-3042-816d-fc8ed6656ba6', 109, 'Mt', 'Meitnerium', NULL, 278, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 9, 'D', '[Rn] 5f14 6d7 7s2', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '82a28008-d88c-3ae9-959a-4f5432b0e167', 110, 'Ds', 'Darmstadtium', NULL, 281, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 10, 'D', '[Rn] 5f14 6d9 7s1', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '1260602a-9efd-3580-ad24-eb042f8b7dc8', 111, 'Rg', 'Roentgenium', NULL, 282, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 11, 'D', '[Rn] 5f14 6d10 7s1', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'cd55dd73-e814-3033-b892-2251e486bf62', 112, 'Cn', 'Copernicium', NULL, 285, 'RADIOACTIVE_ISOTOPE_MASS_NUMBER', NULL, NULL, 7, 12, 'D', '[Rn] 5f14 6d10 7s2', 'UNKNOWN', 'RADIOACTIVE', 'TRANSITION_METAL', 'TRANSITION', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '445165d9-e5d7-361d-88dc-0c64a9c0209f', 113, 'Nh', 'Nihonium', NULL, 286, 'PREDICTED_OR_PROVISIONAL', NULL, NULL, 7, 13, 'P', '[Rn] 5f14 6d10 7s2 7p1', 'UNKNOWN', 'RADIOACTIVE', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'fdbe94df-c2b7-396e-a587-2ecd9ba09b59', 114, 'Fl', 'Flerovium', NULL, 289, 'PREDICTED_OR_PROVISIONAL', NULL, NULL, 7, 14, 'P', '[Rn] 5f14 6d10 7s2 7p2', 'UNKNOWN', 'RADIOACTIVE', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '3bbb383e-f959-3dcb-95d8-3beb24e93894', 115, 'Mc', 'Moscovium', NULL, 290, 'PREDICTED_OR_PROVISIONAL', NULL, NULL, 7, 15, 'P', '[Rn] 5f14 6d10 7s2 7p3', 'UNKNOWN', 'RADIOACTIVE', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  'ae9f8410-d699-35f2-a0dc-302681989497', 116, 'Lv', 'Livermorium', NULL, 293, 'PREDICTED_OR_PROVISIONAL', NULL, NULL, 7, 16, 'P', '[Rn] 5f14 6d10 7s2 7p4', 'UNKNOWN', 'RADIOACTIVE', 'POST_TRANSITION_METAL', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '3b21b7e7-5ff2-39ea-a222-f00928457fa8', 117, 'Ts', 'Tennessine', NULL, 294, 'PREDICTED_OR_PROVISIONAL', NULL, NULL, 7, 17, 'P', '[Rn] 5f14 6d10 7s2 7p5', 'UNKNOWN', 'RADIOACTIVE', 'METALLOID', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
INSERT INTO chemistry.elements (
  id, atomic_number, symbol, name, latin_name, 
  atomic_mass_value, atomic_mass_kind, atomic_mass_lower_bound, atomic_mass_upper_bound, 
  period_number, group_number, block, electron_configuration, 
  standard_state, radioactivity_status, category, series, 
  catalog_version_id, source_reference
) VALUES (
  '36c21ab2-4bbd-3585-b044-493513db16c4', 118, 'Og', 'Oganesson', NULL, 294, 'PREDICTED_OR_PROVISIONAL', NULL, NULL, 7, 18, 'P', '[Rn] 5f14 6d10 7s2 7p6', 'UNKNOWN', 'RADIOACTIVE', 'NOBLE_GAS', 'MAIN_GROUP', 'v1.0.0', 'IUPAC/NIST Reference');
