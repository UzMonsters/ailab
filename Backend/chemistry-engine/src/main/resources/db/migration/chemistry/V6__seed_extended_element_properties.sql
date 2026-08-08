-- V6: Seed extended element properties for all 118 elements
-- Dataset Version: extended-properties-v1.0.0

INSERT INTO chemistry.element_property_dataset_versions (id, description, publication_date)
VALUES ('extended-properties-v1.0.0', 'IUPAC / CRC / NIST Extended Element Properties Dataset', '2026-08-04')
ON CONFLICT (id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('26a59880-8307-36b0-b10b-308aa50c6ea7', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('bd935f99-e194-34b1-badf-6e5bb1b24c5b', '26a59880-8307-36b0-b10b-308aa50c6ea7', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e8733ae7-8fb3-3666-ba3f-9e2f463b40a7', '26a59880-8307-36b0-b10b-308aa50c6ea7', -1, false, true, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3d63d01b-6dfe-35c5-b3f9-0a54f989b778', '26a59880-8307-36b0-b10b-308aa50c6ea7', 0, false, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a083972c-2ed4-32e1-a88a-4a347301bce0', '26a59880-8307-36b0-b10b-308aa50c6ea7', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('8b85a6df-67eb-3841-abb3-0e49ac34e38e', '26a59880-8307-36b0-b10b-308aa50c6ea7', 2.20, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('efe97ad6-e174-3b41-96a5-3ac007715474', '26a59880-8307-36b0-b10b-308aa50c6ea7', 'EMPIRICAL_ATOMIC', 25, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('fc2ac087-f30e-3410-bb20-c0d7822ac181', '26a59880-8307-36b0-b10b-308aa50c6ea7', 'COVALENT_SINGLE_BOND', 31, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('e6c92275-dff4-3c94-87b4-802d5a514df7', '26a59880-8307-36b0-b10b-308aa50c6ea7', 'VAN_DER_WAALS', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('3e93b5db-d170-3206-9580-4ccd8e2f4167', '26a59880-8307-36b0-b10b-308aa50c6ea7', 0.08988, 273.15, 100, 'GAS', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('4bc0e017-3209-387c-9fc3-92bf8dd73294', '26a59880-8307-36b0-b10b-308aa50c6ea7', 'MELTING', 13.99, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ac677949-859f-3150-a20c-702197258bf3', '26a59880-8307-36b0-b10b-308aa50c6ea7', 'BOILING', 20.27, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('5d24f638-6526-3abd-be56-d1e2b430162a', '26a59880-8307-36b0-b10b-308aa50c6ea7', 'colorless', 'Colorless, odorless, tasteless gas', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('f762b2d0-f68a-3702-b100-855d14e2f2c0', 'd66532e4-57a1-38c8-b8c6-9d130d179937', 2, 'He', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('acb9e6c0-482f-3293-abae-913d7d75ead5', 'f762b2d0-f68a-3702-b100-855d14e2f2c0', 0, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e522c3ed-35f4-3d76-a571-6b79ba626602', 'f762b2d0-f68a-3702-b100-855d14e2f2c0', 0, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ba4ca598-f28a-3f97-9c8b-4daddf326d01', 'f762b2d0-f68a-3702-b100-855d14e2f2c0', 'EMPIRICAL_ATOMIC', 31, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('f32d218f-bd7a-30fe-937a-1607db0a33c6', 'f762b2d0-f68a-3702-b100-855d14e2f2c0', 'VAN_DER_WAALS', 140, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('c45efdef-7176-3708-a78a-1104b010f88e', 'f762b2d0-f68a-3702-b100-855d14e2f2c0', 0.1786, 273.15, 100, 'GAS', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d0c67418-c290-38e9-b225-f9af73df3761', 'f762b2d0-f68a-3702-b100-855d14e2f2c0', 'BOILING', 4.22, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('8d1ca363-2ec4-339e-a34e-b4ea0a086435', 'f762b2d0-f68a-3702-b100-855d14e2f2c0', 'colorless', 'Colorless gas', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('0e919788-17b4-382b-9aea-e4c8d4b051b9', '2117f8d3-e041-36e3-9e28-fae00e623811', 3, 'Li', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('3a7e5cc4-61f4-3fa9-aad1-6789ad13651b', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c5365b70-4e17-3ad5-838d-3360e4783b11', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a4b49b47-8982-3ff1-8f79-d3f84ac7fc01', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 0.98, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('b7d82256-e01f-3997-b8a4-8d82d069125d', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 'EMPIRICAL_ATOMIC', 145, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('5c5a6687-9d06-3d59-87a2-4f915160adff', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 'METALLIC', 152, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('44f33dcc-7bc3-300b-b9ff-da0e011e41de', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 'IONIC', 76, 1, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('e0d71832-96a4-3a29-9779-abea83fe1f8e', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 534.000, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('32caa52e-35d9-3b69-9ad7-1f53217f8f07', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 'MELTING', 453.65, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('6ac59499-c0a0-37d3-9455-d7c1e9f45462', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 'BOILING', 1603, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('ace82018-0cdb-3a3f-ac99-ac4d2cc0d36b', '0e919788-17b4-382b-9aea-e4c8d4b051b9', 'silvery-white', 'Soft silvery-white alkali metal', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('72fa6630-ed39-3cd6-bc95-bead6013dc49', '234a682d-dffe-3df8-8215-cc70661be6f0', 4, 'Be', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a2d052c3-aa9f-310e-a19d-3af40a97f7a0', '72fa6630-ed39-3cd6-bc95-bead6013dc49', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ed2f63bf-ff80-372b-ad69-fa2a03c0ce36', '72fa6630-ed39-3cd6-bc95-bead6013dc49', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('9397ca69-9860-324e-aebe-191724d9d23c', '72fa6630-ed39-3cd6-bc95-bead6013dc49', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('249b3140-5f9e-335e-8261-ac743a317a46', '72fa6630-ed39-3cd6-bc95-bead6013dc49', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('5065d7d3-a469-3938-8565-3f4466add345', '72fa6630-ed39-3cd6-bc95-bead6013dc49', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('1a787dfe-75dd-3f23-8a5a-62289e9053df', '72fa6630-ed39-3cd6-bc95-bead6013dc49', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('c4d9ccde-447d-3f99-9790-6be6cac51a9f', '72fa6630-ed39-3cd6-bc95-bead6013dc49', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('50c864d9-40d9-3027-b460-04ce7b67e17c', '1cbab575-f605-389f-a77c-ae2dc59a6d6c', 5, 'B', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('06e8ac7e-c3ae-30f6-a9bd-3f52b2b5cbd4', '50c864d9-40d9-3027-b460-04ce7b67e17c', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ea56d50b-f28a-3cdd-9876-16b8d7326f1c', '50c864d9-40d9-3027-b460-04ce7b67e17c', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c93b6c68-3746-38fa-9a01-b163eb3432c4', '50c864d9-40d9-3027-b460-04ce7b67e17c', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a2afb439-3cd4-3b28-b2a8-eecdec5d8f24', '50c864d9-40d9-3027-b460-04ce7b67e17c', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('c9e425c7-b62b-37b1-984a-40509b8e94e6', '50c864d9-40d9-3027-b460-04ce7b67e17c', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('60cc0128-5721-3d32-be43-c072491ff125', '50c864d9-40d9-3027-b460-04ce7b67e17c', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('8440a8a2-aa0f-38be-9509-f1edcdbe7b44', '50c864d9-40d9-3027-b460-04ce7b67e17c', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('e514b87d-f72a-36dd-96b6-9dc4ea5d841e', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 2, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('eab0a7d5-dab1-3c68-ae28-5b351b33cc98', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 4, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c9879839-b9fe-3695-9c36-70d819151f51', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', -4, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7d296e44-61f5-34ad-87a8-636835ae7cf4', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', -2, false, true, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cafebdd9-b86e-345f-ba78-020fef94ed89', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 0, false, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('10022c62-85ea-3449-9c1e-9d19f3b69a39', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 2, false, true, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3e666901-3e1b-38f0-8277-003e588b312e', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 4, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a3cff733-9904-332a-9644-98283588abd3', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 2.55, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('6c806bf9-a740-33bb-9aae-c008dd27a380', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 'EMPIRICAL_ATOMIC', 70, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a0371b54-e9c9-38b7-81c3-b7626e8bf865', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 'COVALENT_SINGLE_BOND', 76, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('f49e0448-4519-348d-a357-657af8996f83', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 'VAN_DER_WAALS', 170, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('a5d7f9a2-e801-3868-a577-cc58a27bf50c', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 2267.000, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('7d7f705f-ed3e-330a-b15f-02216d7635be', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 'SUBLIMATION', 3915, 100, 'SUBLIMES', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('fb549ab3-a811-30a0-ac62-27bd9e7607e7', '4dc5eedd-11f7-3ab7-b129-14e7fb0bb145', 'black', 'Black solid (graphite) / Colorless transparent solid (diamond)', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('b44612d4-f152-367b-9b82-43faef81361d', '87e4c172-8582-34c0-bdf6-dd0d326b9b93', 7, 'N', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('8dfdb002-1be8-31d9-9f4e-b4c3f1fa47a6', 'b44612d4-f152-367b-9b82-43faef81361d', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('70d0591b-79c3-389e-8d34-b55c100ec315', 'b44612d4-f152-367b-9b82-43faef81361d', 5, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('dd4c0ad9-04d5-3b9e-97d2-8f4ab7db5bfd', 'b44612d4-f152-367b-9b82-43faef81361d', -3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('89c6e091-884e-3797-9aab-692768562d5d', 'b44612d4-f152-367b-9b82-43faef81361d', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('6ab90c00-d4c7-39cd-ac80-4ec0794a2d7c', 'b44612d4-f152-367b-9b82-43faef81361d', 5, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('986f424d-e0f1-313f-9557-44399bbe828d', 'b44612d4-f152-367b-9b82-43faef81361d', 3.04, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('71c4ffa4-3d89-3f13-a7a8-210b77d2d78e', 'b44612d4-f152-367b-9b82-43faef81361d', 'EMPIRICAL_ATOMIC', 65, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('579bee11-1182-3bf9-a41a-36a2c3aa089b', 'b44612d4-f152-367b-9b82-43faef81361d', 'COVALENT_SINGLE_BOND', 71, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('133cf257-1d71-3208-ab37-c9d05d9a072f', 'b44612d4-f152-367b-9b82-43faef81361d', 'VAN_DER_WAALS', 155, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('bccf6134-412b-3b64-b82b-8e8d358e9965', 'b44612d4-f152-367b-9b82-43faef81361d', 1.251, 273.15, 100, 'GAS', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('32fd1d62-7b5b-30c4-9c50-2f114de7f119', 'b44612d4-f152-367b-9b82-43faef81361d', 'MELTING', 63.15, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('5dfb3ca3-eabb-3997-a3dd-b1aae1d4fa4c', 'b44612d4-f152-367b-9b82-43faef81361d', 'BOILING', 77.36, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('8906b6da-fa35-3961-8c19-150db894ff6c', 'b44612d4-f152-367b-9b82-43faef81361d', 'colorless', 'Colorless gas', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('84fe606c-5db4-3f20-819d-910c3de17737', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('33d74004-77a0-3010-8873-9b01614829f0', '84fe606c-5db4-3f20-819d-910c3de17737', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('6ea6c0db-0a11-33dd-8ede-266f45d5bdd7', '84fe606c-5db4-3f20-819d-910c3de17737', -2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('051ab71e-66e9-3593-acd5-0d18cdd236d6', '84fe606c-5db4-3f20-819d-910c3de17737', -1, false, true, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('aa7c14f0-083b-331c-9fa7-ccd1f29618bf', '84fe606c-5db4-3f20-819d-910c3de17737', 0, false, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('448fbbf8-8e38-3531-b9aa-6ac712faa7f3', '84fe606c-5db4-3f20-819d-910c3de17737', 2, false, true, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('5eb474e1-ca92-381c-9e50-a1aa3a50a4f3', '84fe606c-5db4-3f20-819d-910c3de17737', 3.44, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ce955b03-8250-3643-bab5-259d3938c605', '84fe606c-5db4-3f20-819d-910c3de17737', 'EMPIRICAL_ATOMIC', 60, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('df9bbc25-a4a0-3f66-8c6a-e2817b8c9679', '84fe606c-5db4-3f20-819d-910c3de17737', 'COVALENT_SINGLE_BOND', 66, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ecf1f723-95c6-3416-973e-126f84fb89a3', '84fe606c-5db4-3f20-819d-910c3de17737', 'VAN_DER_WAALS', 152, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ac925424-9361-3513-9917-c40e382633f3', '84fe606c-5db4-3f20-819d-910c3de17737', 'IONIC', 140, -2, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('07c52937-c91b-3b8c-9c3f-7a856805f272', '84fe606c-5db4-3f20-819d-910c3de17737', 1.429, 273.15, 100, 'GAS', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('438ad7dc-3406-3683-aab2-3d9b3a8f257c', '84fe606c-5db4-3f20-819d-910c3de17737', 'MELTING', 54.36, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('cbd876c8-1ed5-3842-ae56-e305e7992c15', '84fe606c-5db4-3f20-819d-910c3de17737', 'BOILING', 90.18, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('0a52ac46-5bec-3ddc-9448-f74c15514674', '84fe606c-5db4-3f20-819d-910c3de17737', 'colorless', 'Colorless gas / Pale blue liquid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('fbf5d83a-9599-3245-803a-ee3f2df9de8a', '7e240ab5-d96b-33cd-b915-320767f2fdac', 9, 'F', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('25cfe74f-8348-3eb5-9b25-b7f0add41e33', 'fbf5d83a-9599-3245-803a-ee3f2df9de8a', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('968c06cb-7698-355a-b4d3-efd31bd08a12', 'fbf5d83a-9599-3245-803a-ee3f2df9de8a', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('053089b4-6d00-3737-8950-e8b62c00c7a8', 'fbf5d83a-9599-3245-803a-ee3f2df9de8a', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('e37ab99c-dd64-334b-b04e-bfbba9383e37', 'fbf5d83a-9599-3245-803a-ee3f2df9de8a', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('2e054fcb-250d-3410-a843-bb6d72ecf9be', 'fbf5d83a-9599-3245-803a-ee3f2df9de8a', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ed0d22cb-cf9a-341f-9762-22835b7affff', 'fbf5d83a-9599-3245-803a-ee3f2df9de8a', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('5e63ff1a-4543-3033-a53d-218c40776d19', 'fbf5d83a-9599-3245-803a-ee3f2df9de8a', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('9125a97b-ef41-3d43-8410-d0fb1c8fe4b1', 'a9ed65f0-cfa6-3675-bb3b-6aba27c82050', 10, 'Ne', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('1b2940b1-411f-300f-91d0-4453f7e7ded3', '9125a97b-ef41-3d43-8410-d0fb1c8fe4b1', 0, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('1516ea80-06ea-31bf-aab6-29b2edf43321', '9125a97b-ef41-3d43-8410-d0fb1c8fe4b1', 0, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('bd2e4773-7bbc-3811-89c9-4ac080b45cdd', '9125a97b-ef41-3d43-8410-d0fb1c8fe4b1', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('39afce6f-53a7-39ac-a520-b6c738b6b191', '9125a97b-ef41-3d43-8410-d0fb1c8fe4b1', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('6168015f-e09d-3dca-b1e0-d788f31ffaf2', '9125a97b-ef41-3d43-8410-d0fb1c8fe4b1', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('81a0e3ba-e525-3f51-b35b-4ba8be2a4a8b', '9125a97b-ef41-3d43-8410-d0fb1c8fe4b1', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('4ccbe57b-efe8-3666-8006-7837945effb4', '7155e5b7-5b66-3bc4-a257-a1921f04ffe1', 11, 'Na', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c745e05f-1739-3622-ae6b-36a5494b5be4', '4ccbe57b-efe8-3666-8006-7837945effb4', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d6055812-fffa-383c-8f6f-4bf8b0253875', '4ccbe57b-efe8-3666-8006-7837945effb4', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('5feadd03-dcee-3f53-98ad-41de45009f87', '4ccbe57b-efe8-3666-8006-7837945effb4', 0.93, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('440860d7-d879-37ba-82d6-fd2baa63c216', '4ccbe57b-efe8-3666-8006-7837945effb4', 'EMPIRICAL_ATOMIC', 180, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a7e1cfa7-62af-3493-89c8-57d6bf9c1a06', '4ccbe57b-efe8-3666-8006-7837945effb4', 'METALLIC', 186, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ca7c005e-e185-348c-a47b-4be7e2ec7c86', '4ccbe57b-efe8-3666-8006-7837945effb4', 'IONIC', 102, 1, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('22e5dab9-2423-3364-8b81-a46af0d39e67', '4ccbe57b-efe8-3666-8006-7837945effb4', 968.000, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('33bbdbab-e7a2-3b72-97df-c2c2a58253ff', '4ccbe57b-efe8-3666-8006-7837945effb4', 'MELTING', 370.94, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('afb87c5c-95d7-389c-b3a3-9a607c6bfdbd', '4ccbe57b-efe8-3666-8006-7837945effb4', 'BOILING', 1156, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('20a09d27-47fa-3884-bdd7-35dd48647309', '4ccbe57b-efe8-3666-8006-7837945effb4', 'silvery-white', 'Silvery-white metallic solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('13f85a18-7546-3ae2-bfc5-1c77b8731e1d', '3618c84c-86f2-3901-95b7-bb5384dfad5e', 12, 'Mg', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('ee7123c9-8811-3716-a147-3c5553044277', '13f85a18-7546-3ae2-bfc5-1c77b8731e1d', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c342191f-eb3e-391b-a28b-c4d09c2b1225', '13f85a18-7546-3ae2-bfc5-1c77b8731e1d', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('b1294430-c019-3233-876b-bc2c30632b16', '13f85a18-7546-3ae2-bfc5-1c77b8731e1d', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('4b32f752-05b0-305a-af15-4ab24923a77b', '13f85a18-7546-3ae2-bfc5-1c77b8731e1d', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('9fc43739-eeef-312e-8742-0234c01a3e0d', '13f85a18-7546-3ae2-bfc5-1c77b8731e1d', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('3f759735-f288-3012-ac9c-7d06f621e92e', '13f85a18-7546-3ae2-bfc5-1c77b8731e1d', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('62e8f294-c4a0-3754-a6e3-4590cb20754c', '13f85a18-7546-3ae2-bfc5-1c77b8731e1d', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('9fb3dc3e-fc18-3864-b6cc-27e5fe6f0ebe', 'e0ce8d80-729b-3602-82ea-4030848d3286', 13, 'Al', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('d58f1eb3-50bf-3e8c-95a5-1c08955cfd3d', '9fb3dc3e-fc18-3864-b6cc-27e5fe6f0ebe', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7cd6395e-f16d-3436-836a-2e8baaa61c7e', '9fb3dc3e-fc18-3864-b6cc-27e5fe6f0ebe', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('9ea0f5c0-e533-3dde-9a0d-441a9dba94f5', '9fb3dc3e-fc18-3864-b6cc-27e5fe6f0ebe', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('72ed0915-850b-3e44-ae2f-225fc4294199', '9fb3dc3e-fc18-3864-b6cc-27e5fe6f0ebe', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('479ebf71-5f0c-3b30-bb72-95d033917a2a', '9fb3dc3e-fc18-3864-b6cc-27e5fe6f0ebe', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('9e6b8ae1-3629-32fb-8db9-1649fcf88689', '9fb3dc3e-fc18-3864-b6cc-27e5fe6f0ebe', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('2290c964-7893-39f5-b10c-6a0ec55f495f', '9fb3dc3e-fc18-3864-b6cc-27e5fe6f0ebe', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('6bbbb3b8-44a5-3279-85fd-43d4dbe199d8', '94b1b79b-6f83-3e4f-b8fc-935d25a947af', 14, 'Si', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('954a441b-38a2-3e9d-a506-b1e7de7eb320', '6bbbb3b8-44a5-3279-85fd-43d4dbe199d8', 4, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('345a6453-4b07-30fa-9441-9f00b875d1a4', '6bbbb3b8-44a5-3279-85fd-43d4dbe199d8', 4, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d2c26f77-1dab-3efb-a218-724652e26be1', '6bbbb3b8-44a5-3279-85fd-43d4dbe199d8', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('8ecebd84-4b26-3242-b6e1-3030f0e3b6b3', '6bbbb3b8-44a5-3279-85fd-43d4dbe199d8', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('aef60577-8fee-3407-ac12-0d91c9dc4670', '6bbbb3b8-44a5-3279-85fd-43d4dbe199d8', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('b23b6029-3040-3574-a6bc-715324bfdbc3', '6bbbb3b8-44a5-3279-85fd-43d4dbe199d8', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('eb98cd0b-144e-3ec8-a786-7faeb34d5a8e', '6bbbb3b8-44a5-3279-85fd-43d4dbe199d8', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('5b8659ba-58a6-3557-9a46-1f7e76337cb5', 'f79cae91-c553-3766-a773-c8ec3d8f3e1b', 15, 'P', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('ae01d263-ed3a-3563-b011-9b2c9df899b6', '5b8659ba-58a6-3557-9a46-1f7e76337cb5', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e58a089c-2e6a-3386-ad1a-2ece98d86150', '5b8659ba-58a6-3557-9a46-1f7e76337cb5', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a24d3e42-21a9-393c-8fe9-d33bbc4f8191', '5b8659ba-58a6-3557-9a46-1f7e76337cb5', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('3c1c547a-5e9c-3132-bca4-6e6ad11b0736', '5b8659ba-58a6-3557-9a46-1f7e76337cb5', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('1baba38e-67d5-362b-8929-0ed684b1bdff', '5b8659ba-58a6-3557-9a46-1f7e76337cb5', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e879ec5d-6110-382a-85e4-4fbdae7c3e80', '5b8659ba-58a6-3557-9a46-1f7e76337cb5', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('4c188eb4-cbae-3db9-b4cc-61f41170e65a', '5b8659ba-58a6-3557-9a46-1f7e76337cb5', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('516fc337-41f7-3e66-80ca-dc3650686085', '85dc52a3-4eef-3bb4-ab47-d8f271e81aae', 16, 'S', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('e747b487-7619-3e8a-898e-e2507ff6820b', '516fc337-41f7-3e66-80ca-dc3650686085', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c752c6fd-0bc9-3cca-a42a-16fca32d6293', '516fc337-41f7-3e66-80ca-dc3650686085', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cf63f0a8-95ec-3731-964c-cf4a5c772a54', '516fc337-41f7-3e66-80ca-dc3650686085', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('028ec119-b2cb-3ea8-88a4-db89094f7fda', '516fc337-41f7-3e66-80ca-dc3650686085', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('8970a34c-3f71-3fc0-9875-9060de42614e', '516fc337-41f7-3e66-80ca-dc3650686085', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('64741c80-70c3-310b-9225-caa0287c704a', '516fc337-41f7-3e66-80ca-dc3650686085', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('ddf7b0ef-eac6-3070-b44c-df888cf3f1e9', '516fc337-41f7-3e66-80ca-dc3650686085', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('e3aa9a47-cdc2-30ce-8859-68827f235ee1', 'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('bffa03ac-5655-3634-b310-10cbdb3e7ede', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('0f969a60-1e97-3233-9948-fa4d49604463', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 3, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('ab4ad540-1827-304d-96e1-28fc421b6f22', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 5, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('48564633-5cd7-3f91-854e-5892d960ea15', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 7, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('bbac5fa6-4d06-3e1e-b1f0-91b5ea99386c', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', -1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('8456dfee-a0ab-324c-9898-dd306c547986', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('44577e19-6db6-3bf2-897f-5efbff8c240a', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('bfd9b621-b8a0-3e25-9217-f061a6549edc', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 5, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d30e4e1f-b736-3dcc-a2a4-efa457a07b14', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 7, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('bdccf9f2-c914-3059-9cc4-436c1681e10d', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 3.16, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('dce0989c-cc97-3900-a64d-69373ff5695c', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 'EMPIRICAL_ATOMIC', 100, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a35ffa9f-fd75-3eef-8966-deedf8d26203', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 'COVALENT_SINGLE_BOND', 102, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('4800bc41-7d94-3e9c-ba44-707d25d8f16f', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 'VAN_DER_WAALS', 175, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('c9b21d5c-c14c-364a-ab11-ccf59de11def', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 'IONIC', 181, -1, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('bff651a9-bf2e-330d-8e9a-f40e4406b970', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 3.214, 273.15, 100, 'GAS', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('3e3780ba-f151-324b-b8f7-4edc34a17154', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 'MELTING', 171.6, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d2ea6755-e4e5-390c-bf4c-b3d8df730aa8', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 'BOILING', 239.11, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('44f4b04f-9a12-34f5-9624-7256f1d5aadc', 'e3aa9a47-cdc2-30ce-8859-68827f235ee1', 'pale yellow-green', 'Pale yellow-green gas', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('0e37e233-c5d4-3b67-8cb5-e5006f43570b', 'a04e3603-76e8-3da2-9035-1c85f2d328f6', 18, 'Ar', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('d996a7da-6152-333a-93b7-d9b4ab22d846', '0e37e233-c5d4-3b67-8cb5-e5006f43570b', 0, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('f6cd0f0e-c788-3c3a-b3fb-c05413245d38', '0e37e233-c5d4-3b67-8cb5-e5006f43570b', 0, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('cd79c463-9f44-3318-a27f-2151a406ea5a', '0e37e233-c5d4-3b67-8cb5-e5006f43570b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('869b1eaa-1107-3bf5-bbd1-028b4857628b', '0e37e233-c5d4-3b67-8cb5-e5006f43570b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('bb860938-cb5b-3685-9239-8dc706bb59c8', '0e37e233-c5d4-3b67-8cb5-e5006f43570b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('9eefd18f-d1ab-3c3f-8d27-ffb8fc666928', '0e37e233-c5d4-3b67-8cb5-e5006f43570b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('b9cf253b-cc41-3160-b944-0001f3c4bdc5', 'd6d79ea8-ba41-3a93-8ca2-523d89baca92', 19, 'K', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('76fe7e69-172e-38a3-bef3-6d6eae8975bc', 'b9cf253b-cc41-3160-b944-0001f3c4bdc5', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('6d47d41e-76b3-3a87-af47-1be152e14a97', 'b9cf253b-cc41-3160-b944-0001f3c4bdc5', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ce95cdbd-bbf5-3f2e-aad7-cfcb5e24efc7', 'b9cf253b-cc41-3160-b944-0001f3c4bdc5', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a15a51fb-aad7-311d-8bb4-4fa3a20e202b', 'b9cf253b-cc41-3160-b944-0001f3c4bdc5', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('7560a87c-c50d-3d6b-8217-1056b9d8937c', 'b9cf253b-cc41-3160-b944-0001f3c4bdc5', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('bb6eaf44-4b10-3e68-b631-78192c3908e6', 'b9cf253b-cc41-3160-b944-0001f3c4bdc5', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('07d1a13f-fa35-3bbd-bb48-a58d9a7745f5', 'b9cf253b-cc41-3160-b944-0001f3c4bdc5', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('a80a7475-463d-3915-abfd-4fe66e08e192', 'b410bbd7-d275-3d35-ad8b-e8c2ee54999e', 20, 'Ca', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('7a546192-d500-3663-a9ce-f6dfacab2700', 'a80a7475-463d-3915-abfd-4fe66e08e192', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('316c1073-f3f3-3d88-bafb-cbe9244a757a', 'a80a7475-463d-3915-abfd-4fe66e08e192', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('8f661273-ae64-39ba-8e12-8ea4b4ed468a', 'a80a7475-463d-3915-abfd-4fe66e08e192', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('39b33ad8-837a-316e-9e6d-169ef45ca95d', 'a80a7475-463d-3915-abfd-4fe66e08e192', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('b9fde316-43fb-3e82-bf31-0ae22a0aa3c7', 'a80a7475-463d-3915-abfd-4fe66e08e192', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ae86c762-4b95-3825-b99a-0d629b7c3eb5', 'a80a7475-463d-3915-abfd-4fe66e08e192', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('5bfd623b-c354-3e22-8e8f-2d54a0b8b66a', 'a80a7475-463d-3915-abfd-4fe66e08e192', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('ff29331d-8e2d-3718-94b8-067623273c60', '7b45c009-9fe0-3585-9402-3976d0277da1', 21, 'Sc', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c52c1e51-1ceb-3b7a-b66c-9bb830d76283', 'ff29331d-8e2d-3718-94b8-067623273c60', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('184e1220-4b46-3d4a-9b05-743409663cc8', 'ff29331d-8e2d-3718-94b8-067623273c60', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('37735937-c347-3c0c-8a04-159b7bee709c', 'ff29331d-8e2d-3718-94b8-067623273c60', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('510019f1-f0c6-3572-9834-a77a97687e7b', 'ff29331d-8e2d-3718-94b8-067623273c60', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('7f9a0e63-c01c-3990-b145-dc3af1d22942', 'ff29331d-8e2d-3718-94b8-067623273c60', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('20df0d40-7299-319f-8463-c7441053d548', 'ff29331d-8e2d-3718-94b8-067623273c60', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('4c28de4c-c7ad-36d6-b78f-3400fe047bf8', 'ff29331d-8e2d-3718-94b8-067623273c60', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('90028ae0-affe-38dc-ad05-b240e74ff484', '0ee1c364-607d-362b-b575-4ba0a1d866ee', 22, 'Ti', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('23b2c505-bf0e-3846-8722-2367cb6ee108', '90028ae0-affe-38dc-ad05-b240e74ff484', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('44e340c2-61dc-3bdf-9587-c47d34eb40cd', '90028ae0-affe-38dc-ad05-b240e74ff484', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('51030848-f7af-30fd-a043-400eea487a7e', '90028ae0-affe-38dc-ad05-b240e74ff484', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('8b891be4-5a43-3a75-87db-1e6a687627fc', '90028ae0-affe-38dc-ad05-b240e74ff484', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('cf74c5dd-515a-3af7-a221-e0e9c2960616', '90028ae0-affe-38dc-ad05-b240e74ff484', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ebbca4b3-0c3f-39bb-9520-070f72938a48', '90028ae0-affe-38dc-ad05-b240e74ff484', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('05a2103d-b9ad-3e8d-983d-631904aea72e', '90028ae0-affe-38dc-ad05-b240e74ff484', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('5f068a0b-3158-3f59-8b1a-6e301cd58b0b', '35012873-3544-3625-b852-33851f0fc133', 23, 'V', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('b517e277-0146-3449-a802-39fc49915bc0', '5f068a0b-3158-3f59-8b1a-6e301cd58b0b', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d9233cc6-22df-32e4-871f-153bc77eaa1c', '5f068a0b-3158-3f59-8b1a-6e301cd58b0b', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3723b768-f9ea-34da-8f91-20842bf10f91', '5f068a0b-3158-3f59-8b1a-6e301cd58b0b', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('c675026a-6047-3b72-9791-0ac8ab7d5273', '5f068a0b-3158-3f59-8b1a-6e301cd58b0b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('f3789a61-7635-357b-b962-1a29eaac3914', '5f068a0b-3158-3f59-8b1a-6e301cd58b0b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('30eb6604-cbeb-320f-a7e7-ac4f9fa01691', '5f068a0b-3158-3f59-8b1a-6e301cd58b0b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('e2a6a44f-4949-37df-8e5c-02c5ca788d9a', '5f068a0b-3158-3f59-8b1a-6e301cd58b0b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('f5d4dd3a-bbc2-3103-8baf-312bd619cb67', '20df4212-a434-3b4c-870f-ff4db46cecd4', 24, 'Cr', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('580c67e2-ef1e-34f7-b7ce-95c7fa8e0e3e', 'f5d4dd3a-bbc2-3103-8baf-312bd619cb67', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('5599a923-9e87-30b0-b230-5003c89c7093', 'f5d4dd3a-bbc2-3103-8baf-312bd619cb67', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d0354fea-88e6-3e8e-bd38-ae413a2959bb', 'f5d4dd3a-bbc2-3103-8baf-312bd619cb67', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('15358199-fab9-3b22-ac38-f56a4117a54c', 'f5d4dd3a-bbc2-3103-8baf-312bd619cb67', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('0189b95e-0f16-3e3d-9b45-5eab93667919', 'f5d4dd3a-bbc2-3103-8baf-312bd619cb67', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e1ae87f9-173d-3827-8166-d1c2cce4d707', 'f5d4dd3a-bbc2-3103-8baf-312bd619cb67', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('b852835a-63db-3191-ac9c-d6c377d7aa8f', 'f5d4dd3a-bbc2-3103-8baf-312bd619cb67', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('4466b922-86f1-3242-9230-cc3214bfbc6d', '42a57138-f54d-3292-8884-09bd10e5d0ac', 25, 'Mn', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('d6c7469c-f3c6-3d6f-a79a-3ffd18699c0f', '4466b922-86f1-3242-9230-cc3214bfbc6d', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('8c99f897-a9b4-3460-bb5a-08aed5490102', '4466b922-86f1-3242-9230-cc3214bfbc6d', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('1757c751-cce2-3b8b-9e21-ee29093d18cd', '4466b922-86f1-3242-9230-cc3214bfbc6d', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('2bd23349-fb12-374f-8b33-747806550258', '4466b922-86f1-3242-9230-cc3214bfbc6d', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('248efce3-c57e-3050-932c-5f21f99a1007', '4466b922-86f1-3242-9230-cc3214bfbc6d', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e98803a9-0fb8-37b2-adf4-d4717dfc568d', '4466b922-86f1-3242-9230-cc3214bfbc6d', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('a61ea21d-0933-3ac9-8f0c-6bc9994b77f0', '4466b922-86f1-3242-9230-cc3214bfbc6d', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('c27d4583-872f-38d5-811f-8997fde7b9b6', 'f5133816-62cd-3ec0-af5b-a566977b9ad3', 26, 'Fe', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a7a5902b-c47b-3315-9de6-2124970de5e2', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('005c3d51-92dc-3149-bbdb-ccfc62ac0fdc', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('02370408-8bec-3db7-91eb-3142a3980ac9', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 6, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('39dd9ba7-49fb-323a-b216-17a98f0c37dc', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('54038817-c096-336e-8e33-ab2deae330e1', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('b66acd70-e874-33a3-b273-da15828ec5da', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 6, false, true, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('4a72a130-edca-3245-9e6f-3592842db8d4', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 1.83, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('b04ca390-959d-3e33-8a44-616908378145', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 'EMPIRICAL_ATOMIC', 140, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('4b0608c1-eca7-3936-86ac-d96da64377fa', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 'METALLIC', 126, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('264c9dd2-4401-3c77-aeaa-df5c59c4159c', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 'IONIC', 78, 2, 6, 'HIGH_SPIN', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('31d0304d-1df3-3f48-9c5c-a8f47dc0eac3', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 'IONIC', 64.5, 3, 6, 'HIGH_SPIN', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('2da3baf3-0b17-3665-8e17-9ac393627d8c', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 7874.000, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('676ef9e9-5669-3c28-82d0-71160995e434', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 'MELTING', 1811, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('a185b21b-2309-3bb3-81da-42b71b04c3cb', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 'BOILING', 3134, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('95fad0b8-2b04-3e53-9254-9a4f7e2ea5a4', 'c27d4583-872f-38d5-811f-8997fde7b9b6', 'silvery-gray', 'Lustrous silvery-gray metallic solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('459d4a61-125c-3768-8d92-77376094d9cf', 'df6b4c63-0b19-3213-9851-e34a2b872fff', 27, 'Co', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('de1e3795-d687-3ebd-b722-5896f2b13139', '459d4a61-125c-3768-8d92-77376094d9cf', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cbe1b3a5-1b4d-3e5c-82db-9729a6393f6d', '459d4a61-125c-3768-8d92-77376094d9cf', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a28a8c39-e061-3c48-9cb7-7b0e382d39f5', '459d4a61-125c-3768-8d92-77376094d9cf', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('f7d20308-2278-35bc-a776-01f9e6bf57ed', '459d4a61-125c-3768-8d92-77376094d9cf', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('e217aac4-fbb2-3c52-9e48-5d1ef2b0e511', '459d4a61-125c-3768-8d92-77376094d9cf', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('7642a0ef-818e-3532-ae12-67a50651cc07', '459d4a61-125c-3768-8d92-77376094d9cf', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('634356f0-78ce-3ed2-bdbf-870ef7e9fd13', '459d4a61-125c-3768-8d92-77376094d9cf', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('c2cbe0b3-c7ce-3db2-a1b5-832b63c9feea', '509719b2-e5fd-3c19-8013-302858ccbc0c', 28, 'Ni', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c6731b60-8a03-35ff-8f23-a3a2bfe15da3', 'c2cbe0b3-c7ce-3db2-a1b5-832b63c9feea', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('9babf0b0-6658-330e-b108-1c152d211a66', 'c2cbe0b3-c7ce-3db2-a1b5-832b63c9feea', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('daa97163-52da-3a96-ac14-b17755b087f6', 'c2cbe0b3-c7ce-3db2-a1b5-832b63c9feea', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('394fd7cb-3db6-3985-914f-f44bd4f8979e', 'c2cbe0b3-c7ce-3db2-a1b5-832b63c9feea', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('dd8f7a9a-ae84-36fc-aac9-71585859ae8c', 'c2cbe0b3-c7ce-3db2-a1b5-832b63c9feea', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('076429f1-4141-32bd-b380-01c260ab512f', 'c2cbe0b3-c7ce-3db2-a1b5-832b63c9feea', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('312db60b-6627-3355-905c-7a300d4fffac', 'c2cbe0b3-c7ce-3db2-a1b5-832b63c9feea', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('d2dfe94c-37c5-369f-8f81-4b9a719080e0', 'bf72cc72-a94f-3d56-b1c0-4b00b3a2fdb3', 29, 'Cu', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('10f02e6e-a767-3614-80c9-789fa32bb350', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('27ce5773-a6e4-3370-b496-543c6185b41e', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('6ca37b12-da99-3206-ad76-ebed495063e1', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7ec596ae-a12f-37f8-bc86-4a67ca77c65e', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c0ea7f3c-d5ed-355b-b711-b13545bbe389', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 1.90, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('65fb156d-57f9-3470-81da-c31b478dd564', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 'EMPIRICAL_ATOMIC', 135, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('7738478a-77a3-3a88-a16a-88760151c3a8', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 'METALLIC', 128, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('08a2cd44-7571-3221-9f30-eb24ad703fc4', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 'IONIC', 77, 1, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('b31f5444-7f74-3ca7-a9ef-e57a0508dce3', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 'IONIC', 73, 2, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('c5aab71f-2574-3d1e-a123-6248b27f25ae', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 8960.00, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('b4127f5f-4141-3cab-9434-7cf47b36b244', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 'MELTING', 1357.77, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('64e32cf2-3a17-3263-8dd2-61ad188dc00d', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 'BOILING', 2835, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('2119d524-9255-315d-91c9-ce6edee11d11', 'd2dfe94c-37c5-369f-8f81-4b9a719080e0', 'reddish-orange', 'Reddish-orange metallic solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('e4fe06d0-01e0-3e94-a31f-63d385fb758c', '322a89df-4874-31d3-bdea-643a3f5ab0d2', 30, 'Zn', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('abaca2c4-1e1c-3b85-8d33-8f059006734a', 'e4fe06d0-01e0-3e94-a31f-63d385fb758c', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d2b88da3-4fdb-3a39-b587-ded28f674314', 'e4fe06d0-01e0-3e94-a31f-63d385fb758c', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c49843cd-0d37-3437-beb9-2f262c86a13b', 'e4fe06d0-01e0-3e94-a31f-63d385fb758c', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('6dbd0ec7-ea0e-3a35-a9a1-586c6b6cbd60', 'e4fe06d0-01e0-3e94-a31f-63d385fb758c', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('a2f266f7-c021-3deb-8c78-349091b0501b', 'e4fe06d0-01e0-3e94-a31f-63d385fb758c', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e1c3fae4-a7ce-3ecc-92c3-8f0e4367fd57', 'e4fe06d0-01e0-3e94-a31f-63d385fb758c', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('89f51992-c6b5-385c-80da-f100a5902595', 'e4fe06d0-01e0-3e94-a31f-63d385fb758c', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('146c83e7-8046-350a-8253-5e518ada646e', '9356c18a-d9c8-399b-933f-3029ab649a41', 31, 'Ga', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('8bd7f400-630e-34da-a29e-d95134b449ea', '146c83e7-8046-350a-8253-5e518ada646e', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cc908281-7c76-3f12-9584-24f2c73891aa', '146c83e7-8046-350a-8253-5e518ada646e', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('1bc6f441-77c0-35b8-926f-5c72f20970ff', '146c83e7-8046-350a-8253-5e518ada646e', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('b4ffa1c0-7441-3f35-b4f6-66ebbb1806ca', '146c83e7-8046-350a-8253-5e518ada646e', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('56118afb-770a-302b-bdd7-12d3b88e12e9', '146c83e7-8046-350a-8253-5e518ada646e', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('1a54ba7f-4ddd-316c-a68b-1cf1b63af11f', '146c83e7-8046-350a-8253-5e518ada646e', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('d9ccf3f6-cc94-3e00-952a-be04cfbef390', '146c83e7-8046-350a-8253-5e518ada646e', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('479154c1-b6d5-3acb-9b3e-a0a38d20ae8f', '60b0ae93-6e4d-3e37-858c-ff45b5124916', 32, 'Ge', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('47322e73-1d5b-36b3-9271-f0423214331c', '479154c1-b6d5-3acb-9b3e-a0a38d20ae8f', 4, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('556dc2b3-00c5-3ba4-9473-2dd4674a0984', '479154c1-b6d5-3acb-9b3e-a0a38d20ae8f', 4, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e5299a0a-9007-3c5c-9049-6c011ecc47e1', '479154c1-b6d5-3acb-9b3e-a0a38d20ae8f', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('b8b10343-bd3f-3dae-95bc-9601fbbe3cde', '479154c1-b6d5-3acb-9b3e-a0a38d20ae8f', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('6815d8a9-955d-3218-a73f-0bf589c85884', '479154c1-b6d5-3acb-9b3e-a0a38d20ae8f', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('c7a11047-342e-3e8a-b9ef-8b66c36094f3', '479154c1-b6d5-3acb-9b3e-a0a38d20ae8f', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('31bd1312-18c3-3deb-ae07-b50d0ec43bd3', '479154c1-b6d5-3acb-9b3e-a0a38d20ae8f', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('f2ccb800-895e-3887-8aff-fa7a96df222f', '42f15c27-a2b5-3a99-bb38-472f38552ca6', 33, 'As', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4fae93fd-6430-3fdd-84a5-e20d825f3dbb', 'f2ccb800-895e-3887-8aff-fa7a96df222f', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('b23fab79-393a-34b4-a4f8-a72a9cf5ca56', 'f2ccb800-895e-3887-8aff-fa7a96df222f', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('bdd0a702-188f-3a95-9d31-97b800fc3f52', 'f2ccb800-895e-3887-8aff-fa7a96df222f', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('604158aa-94bf-3e78-a8a1-1019bbb69f51', 'f2ccb800-895e-3887-8aff-fa7a96df222f', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('b93cf517-ad45-3cf4-b8ae-42a6cc389c8e', 'f2ccb800-895e-3887-8aff-fa7a96df222f', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ad7d5ac3-eaf4-3d40-9251-a84bdf324168', 'f2ccb800-895e-3887-8aff-fa7a96df222f', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('d5ad13e5-d915-39f3-a9bc-f3eac17204df', 'f2ccb800-895e-3887-8aff-fa7a96df222f', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('69897c18-e34f-32a0-9c29-fb9f506d5d52', '2789b675-30b0-3f85-b202-04d55b18ed0f', 34, 'Se', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('94b302e9-de28-333c-8f4c-235a8872857e', '69897c18-e34f-32a0-9c29-fb9f506d5d52', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('36f1f727-af8c-3c6d-b532-d6d444d531a1', '69897c18-e34f-32a0-9c29-fb9f506d5d52', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a078d84c-355e-3eec-a077-4b43fc98cc64', '69897c18-e34f-32a0-9c29-fb9f506d5d52', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('cd0df795-4039-3f6e-825e-82fdd531d32f', '69897c18-e34f-32a0-9c29-fb9f506d5d52', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('7111bcf2-349f-37e1-82ec-8d2c106116bf', '69897c18-e34f-32a0-9c29-fb9f506d5d52', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('7a27808e-b04f-305a-ad6a-2e9b02190b4a', '69897c18-e34f-32a0-9c29-fb9f506d5d52', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('f8ff6da2-950b-3895-af27-1ecce967bbe8', '69897c18-e34f-32a0-9c29-fb9f506d5d52', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 'fb26ef36-5d23-3496-b74d-1dea5eec6250', 35, 'Br', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a5dbb6af-4fa6-3d37-a51e-4656842a6dad', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('e46dfb52-ec9b-37f2-bcdb-e7fc0e2f9b83', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 5, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('683c9f70-d4b6-3bb2-a148-314a5662db37', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', -1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d75938e7-55b7-3fa6-8ed5-45b2a0b3f9b9', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('49348f38-1e19-364b-8a83-83649c727d00', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 5, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d3aed5ca-a4a1-3451-a85a-4c7f42f3bce2', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 2.96, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('c0b43833-305f-36f7-a95d-68f68719ab6b', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 'EMPIRICAL_ATOMIC', 115, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('987bbc6c-eff6-320a-a44e-2faa5d93e9a5', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 'COVALENT_SINGLE_BOND', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('c4ec0e75-fcde-3767-82c6-d3848192d446', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 'VAN_DER_WAALS', 185, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('b435dbc3-5c9d-3c42-b026-b0d3c8f95e25', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 3102.8000, 298.15, 100, 'LIQUID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('fb7db216-2c18-3b18-a89e-0feec76a296f', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 'MELTING', 265.8, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('327797c8-4d01-30c6-9aed-4ff3f65988b2', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 'BOILING', 332.0, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('c649991e-07c4-38a3-be82-844f1886c24f', '0c3ec8ee-6cd4-3547-9a92-4f2cccb8b61d', 'dark reddish-brown', 'Dark reddish-brown liquid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('46cb6017-a9aa-38c5-8b12-0855375153cd', '32458a28-348d-3b73-918a-a4f8de185b2a', 36, 'Kr', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('1000c521-4b4c-389a-8039-8bf6a520832d', '46cb6017-a9aa-38c5-8b12-0855375153cd', 0, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ef63874c-4f30-3c82-9288-3f9a96adda9d', '46cb6017-a9aa-38c5-8b12-0855375153cd', 0, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('fa66c060-0ad1-3867-8ac0-4f0b3d7729c0', '46cb6017-a9aa-38c5-8b12-0855375153cd', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('9bac1e20-2e92-3f8e-b2ad-7022eba25ce8', '46cb6017-a9aa-38c5-8b12-0855375153cd', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('02ceb2f7-6026-3486-8e87-8d3dada93c0c', '46cb6017-a9aa-38c5-8b12-0855375153cd', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('3ee8d348-cb26-3124-b29b-0da0654b328d', '46cb6017-a9aa-38c5-8b12-0855375153cd', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('f1cffae5-6fcc-3e9b-ad70-cde7107207fd', '1082356d-d9de-3058-a81f-ff2bae8f4131', 37, 'Rb', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4ad643bf-3688-32fb-8d40-e1e59367771f', 'f1cffae5-6fcc-3e9b-ad70-cde7107207fd', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('babe97cc-b880-3f2e-b09a-b5f1f1e40b62', 'f1cffae5-6fcc-3e9b-ad70-cde7107207fd', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('813cc5cc-5903-3675-a93d-407804c595e9', 'f1cffae5-6fcc-3e9b-ad70-cde7107207fd', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('58d6ebb5-9e72-359f-8ff0-5c57afdecc65', 'f1cffae5-6fcc-3e9b-ad70-cde7107207fd', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('d4a15939-93fc-3704-bbed-9b8bc94fb9e4', 'f1cffae5-6fcc-3e9b-ad70-cde7107207fd', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('4f9ac438-3ca5-3e50-afbc-bc9a051c6db4', 'f1cffae5-6fcc-3e9b-ad70-cde7107207fd', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('233d0630-1904-3a2d-b4db-75b8953422b0', 'f1cffae5-6fcc-3e9b-ad70-cde7107207fd', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('9362df26-4358-3b1f-87f5-c13c3325eeb8', '403412d4-7fdd-31df-985b-19bc743811df', 38, 'Sr', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('f1c6e32f-ff0c-3b55-aea7-0f677a25238b', '9362df26-4358-3b1f-87f5-c13c3325eeb8', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cfc29861-9bf4-3c7a-bacf-035edc81db58', '9362df26-4358-3b1f-87f5-c13c3325eeb8', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('202cf648-e245-3060-850d-0e50f0e39a90', '9362df26-4358-3b1f-87f5-c13c3325eeb8', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('fc88dcc0-f05b-324f-a005-18388655dffa', '9362df26-4358-3b1f-87f5-c13c3325eeb8', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('a5e93584-9fa8-34e4-b208-7d479197b7b9', '9362df26-4358-3b1f-87f5-c13c3325eeb8', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('2e0a25b3-4915-3d08-a928-bcf8ccc344c3', '9362df26-4358-3b1f-87f5-c13c3325eeb8', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('73ab4d2d-17ba-376e-9855-6fd7cdec8696', '9362df26-4358-3b1f-87f5-c13c3325eeb8', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('f87a9f1c-2480-3f6b-ab4c-670f878c230b', '6a9bdd61-58b9-3d4d-8260-c3a79b618f30', 39, 'Y', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c5233317-347b-346d-a366-8122ce8a1961', 'f87a9f1c-2480-3f6b-ab4c-670f878c230b', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e5bce630-1b02-3183-943a-840aca924dc6', 'f87a9f1c-2480-3f6b-ab4c-670f878c230b', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('92e55327-8931-3309-957a-492713ee91d9', 'f87a9f1c-2480-3f6b-ab4c-670f878c230b', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('782a3b8b-d080-3e13-b4cc-6f4af37503c4', 'f87a9f1c-2480-3f6b-ab4c-670f878c230b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('f951e062-a00f-3275-9b95-2d7c4ed433a5', 'f87a9f1c-2480-3f6b-ab4c-670f878c230b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e32839c0-84c6-3eb6-b56d-450bc31210ea', 'f87a9f1c-2480-3f6b-ab4c-670f878c230b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('e8e9f4cb-8778-3823-a3b5-d61ef84009ac', 'f87a9f1c-2480-3f6b-ab4c-670f878c230b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('39b5bd81-40ef-36b7-b62b-aa0acad4654f', 'a18d7d40-70e9-3d83-9053-b9905dac8a02', 40, 'Zr', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('36fed86e-97b1-346f-b3a2-903073ed0d2f', '39b5bd81-40ef-36b7-b62b-aa0acad4654f', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('958bab3f-e64a-3766-8ec9-388f375104ae', '39b5bd81-40ef-36b7-b62b-aa0acad4654f', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('79f7663d-95f3-3739-a9df-4a4c462ef95a', '39b5bd81-40ef-36b7-b62b-aa0acad4654f', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('85eb5e43-2635-3af1-a6b0-12267fb9d43e', '39b5bd81-40ef-36b7-b62b-aa0acad4654f', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('a3a470ef-2d51-323d-9b3f-f3745a983ff3', '39b5bd81-40ef-36b7-b62b-aa0acad4654f', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('803c06a2-4b0c-3f3d-a2d6-d63c2bce54a6', '39b5bd81-40ef-36b7-b62b-aa0acad4654f', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('be2f2ea5-8056-3b98-ae41-2fb9089e85f2', '39b5bd81-40ef-36b7-b62b-aa0acad4654f', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('0196cb03-206b-30ef-991a-65baf737b7d4', '0d015e48-ce61-3f85-9c2b-3537e45301e9', 41, 'Nb', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('408fbede-7017-3883-8687-c63923b1ad2a', '0196cb03-206b-30ef-991a-65baf737b7d4', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ef62b8c0-264b-37fd-97f7-749aec24b0db', '0196cb03-206b-30ef-991a-65baf737b7d4', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d06b6444-e45b-31d5-a991-d8edea87cdc6', '0196cb03-206b-30ef-991a-65baf737b7d4', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('de623c4b-f922-3e0d-b9f3-15410fd00eb4', '0196cb03-206b-30ef-991a-65baf737b7d4', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('bf8b0a2a-d7c5-3003-bfd7-e1af580dd619', '0196cb03-206b-30ef-991a-65baf737b7d4', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('9319dede-caae-34ff-849d-ea047fd3fc12', '0196cb03-206b-30ef-991a-65baf737b7d4', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('e4692f34-09f8-3642-98eb-3d5c270dd9da', '0196cb03-206b-30ef-991a-65baf737b7d4', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('1f3bb7d6-651d-3b2d-96fa-c9b008eb1850', 'dc8e1f73-45e5-383b-a710-fb5d49ce410e', 42, 'Mo', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('0d732a5e-689b-3b3d-9861-2c43436ab97c', '1f3bb7d6-651d-3b2d-96fa-c9b008eb1850', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('5170bc7c-ad79-3d10-8f7a-b08209ef550f', '1f3bb7d6-651d-3b2d-96fa-c9b008eb1850', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('91038cfa-be3a-37eb-a8a0-eef9dc4f5c8b', '1f3bb7d6-651d-3b2d-96fa-c9b008eb1850', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a781703c-cbbc-390c-be44-ad78cac4ceac', '1f3bb7d6-651d-3b2d-96fa-c9b008eb1850', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('01fb6691-a1bf-3d0e-97d8-70cd96394f6c', '1f3bb7d6-651d-3b2d-96fa-c9b008eb1850', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('a6cbf216-66cf-398c-a64b-443e9e5f3a49', '1f3bb7d6-651d-3b2d-96fa-c9b008eb1850', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('0f3fcc40-6fc0-37c0-b692-f8c1248ecdeb', '1f3bb7d6-651d-3b2d-96fa-c9b008eb1850', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('313ee18f-2df6-329f-bc9c-c25348809b28', 'd799b6ab-0855-377d-8908-cd505a31358b', 43, 'Tc', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('72c18087-2846-3712-ad1d-a154c5fedc4f', '313ee18f-2df6-329f-bc9c-c25348809b28', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('5cf53bbc-831a-3ebf-ac04-2f18122fe7f0', '313ee18f-2df6-329f-bc9c-c25348809b28', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('651295fd-17da-3e44-89fa-f49fcb6b99f3', '313ee18f-2df6-329f-bc9c-c25348809b28', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ddb5b4ad-fea5-3c03-ad75-2df01d91fbc2', '313ee18f-2df6-329f-bc9c-c25348809b28', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('05b3d01e-c22f-3918-81a7-a9cba95b7580', '313ee18f-2df6-329f-bc9c-c25348809b28', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('699b01f0-be47-39c7-b33c-dcd026a05277', '313ee18f-2df6-329f-bc9c-c25348809b28', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('e70b536d-e6ff-38a2-80c0-d7b406d1be82', '313ee18f-2df6-329f-bc9c-c25348809b28', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('dfdfb143-0cc9-31a6-bb3b-bdf50612b16f', 'ae7291a6-1efb-3660-9f7f-3ec926717407', 44, 'Ru', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a3dd3515-9b23-32bf-b63b-c5acf8474cd2', 'dfdfb143-0cc9-31a6-bb3b-bdf50612b16f', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('d1098f1b-3bad-351e-a6a0-c3faca4a1123', 'dfdfb143-0cc9-31a6-bb3b-bdf50612b16f', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7c2fc5c0-6f5e-3f34-976d-22e85d218757', 'dfdfb143-0cc9-31a6-bb3b-bdf50612b16f', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('e6c1f0cb-5a48-3cdc-b2e4-16258e02924a', 'dfdfb143-0cc9-31a6-bb3b-bdf50612b16f', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('52bf86ec-1e95-3595-9c29-1be1586df471', 'dfdfb143-0cc9-31a6-bb3b-bdf50612b16f', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('f2c885fd-8870-3d59-ad40-0eafd893d993', 'dfdfb143-0cc9-31a6-bb3b-bdf50612b16f', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('2d7e7b5c-bb20-3720-bb7a-95ca8fe4bfd2', 'dfdfb143-0cc9-31a6-bb3b-bdf50612b16f', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('63907617-fc45-354c-82a8-8ab3f1208962', 'cbc20e2b-ab5e-3050-8e42-7689767a7db0', 45, 'Rh', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('1bb5801f-e918-3203-a8f1-dbd6453ed40e', '63907617-fc45-354c-82a8-8ab3f1208962', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cda1369d-c5d1-335d-97a1-02a8100ff07a', '63907617-fc45-354c-82a8-8ab3f1208962', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('6d0000ad-bae8-3f72-b007-8c5011fadd95', '63907617-fc45-354c-82a8-8ab3f1208962', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('dfe84d03-657b-36bc-b262-b2176b05ce4e', '63907617-fc45-354c-82a8-8ab3f1208962', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('8b3f396a-8c78-3393-b75c-9382465bedca', '63907617-fc45-354c-82a8-8ab3f1208962', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('58f5cde3-498d-3d2e-bd75-297154e6cfa2', '63907617-fc45-354c-82a8-8ab3f1208962', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('aa5119be-7e76-3afc-8491-7534e8a80aee', '63907617-fc45-354c-82a8-8ab3f1208962', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('304227f6-5f5f-3703-a2f4-d2c3acdbd8b5', '0a89b66f-f941-36f6-88be-0eb5889fbfcb', 46, 'Pd', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('1ee8d054-8110-3cc7-a987-fd0ad1324de2', '304227f6-5f5f-3703-a2f4-d2c3acdbd8b5', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('255a9d31-b0c5-347d-881b-89bb8b77cccc', '304227f6-5f5f-3703-a2f4-d2c3acdbd8b5', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('59e2f636-bcfa-3f80-b1ed-973981c397e3', '304227f6-5f5f-3703-a2f4-d2c3acdbd8b5', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('eb610b39-0731-3401-9741-5ee690c42f5c', '304227f6-5f5f-3703-a2f4-d2c3acdbd8b5', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('2447b384-c799-3e62-8b51-3c0f53a5f205', '304227f6-5f5f-3703-a2f4-d2c3acdbd8b5', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e92e1bde-5acc-3dc9-9f7e-7a1bd0de8ca7', '304227f6-5f5f-3703-a2f4-d2c3acdbd8b5', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('20832594-450a-3b58-bbf3-161818e92468', '304227f6-5f5f-3703-a2f4-d2c3acdbd8b5', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('0b8867fb-ba36-3a77-9f03-19c783358e84', '359a62af-367a-3efb-86dc-e3783a49756e', 47, 'Ag', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('e9b697be-d5a5-3b37-972b-5a2604171bd0', '0b8867fb-ba36-3a77-9f03-19c783358e84', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e3edb1c4-2c6a-3a19-8a8c-e7e8d04486c1', '0b8867fb-ba36-3a77-9f03-19c783358e84', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('65b59298-23c1-3c4c-ba8d-a98460978a53', '0b8867fb-ba36-3a77-9f03-19c783358e84', 1.93, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('78fd6588-7ef3-35ff-aaec-a4d07880c346', '0b8867fb-ba36-3a77-9f03-19c783358e84', 'EMPIRICAL_ATOMIC', 160, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a6a2bd49-babf-3e96-94c2-f2f2443cc88b', '0b8867fb-ba36-3a77-9f03-19c783358e84', 'METALLIC', 144, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('8232f2b9-f13b-31e5-bb47-2f4d52c2170b', '0b8867fb-ba36-3a77-9f03-19c783358e84', 'IONIC', 115, 1, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('340e4f5c-16bc-3f77-8d20-b0c5c694d6e6', '0b8867fb-ba36-3a77-9f03-19c783358e84', 10490.00, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('02d8360b-2a74-3a44-a25f-3a91e4f33f47', '0b8867fb-ba36-3a77-9f03-19c783358e84', 'MELTING', 1234.93, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('2c0a9234-dde5-3745-ae3c-3debef799d0c', '0b8867fb-ba36-3a77-9f03-19c783358e84', 'BOILING', 2435, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('68445917-4836-3281-9961-a76ba3de7229', '0b8867fb-ba36-3a77-9f03-19c783358e84', 'silvery-white', 'Lustrous silver metallic solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('3fbae7c3-297f-3a61-bb63-b6b85bfa3e3a', 'ae4ca758-2113-3478-a073-516bb257614e', 48, 'Cd', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('2862c79f-25e9-30ff-8b9b-222458ac9628', '3fbae7c3-297f-3a61-bb63-b6b85bfa3e3a', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('4dc61619-524f-3501-8195-ffaf1fb117de', '3fbae7c3-297f-3a61-bb63-b6b85bfa3e3a', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('650217c0-8ceb-3e9c-87f6-7489be555a26', '3fbae7c3-297f-3a61-bb63-b6b85bfa3e3a', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('eaff54c0-e391-3ab7-a265-a9b6807d016b', '3fbae7c3-297f-3a61-bb63-b6b85bfa3e3a', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('8159452c-f3b5-39a9-927e-007d431868a5', '3fbae7c3-297f-3a61-bb63-b6b85bfa3e3a', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d024ce41-95a4-37b5-8f9b-cdf97be7ac5e', '3fbae7c3-297f-3a61-bb63-b6b85bfa3e3a', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('24d3b37c-7e1d-3007-95fb-98f199173868', '3fbae7c3-297f-3a61-bb63-b6b85bfa3e3a', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('db331808-5520-301a-892e-59213eab6c71', 'ca54d820-573c-3176-be5f-02cf51ccc5e3', 49, 'In', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4486cd87-c679-3ff3-a131-de36718dcbc4', 'db331808-5520-301a-892e-59213eab6c71', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('0365d115-ca29-36e3-82b5-9b2f12a7a025', 'db331808-5520-301a-892e-59213eab6c71', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('18fd86c5-0ac4-3f13-b22f-7f253d80966f', 'db331808-5520-301a-892e-59213eab6c71', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('55d5c9f8-7606-3ff8-89dc-472ddeffd4a1', 'db331808-5520-301a-892e-59213eab6c71', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('d9ebcccf-613c-3dc3-8c39-27cce3133ef8', 'db331808-5520-301a-892e-59213eab6c71', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('aaf60eba-a64b-37fa-aaab-4f744bf3a070', 'db331808-5520-301a-892e-59213eab6c71', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('595b80a7-96be-38d0-8553-9e3e2c433544', 'db331808-5520-301a-892e-59213eab6c71', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('4d36db28-7809-3b08-8ad2-f668c2239280', '82840137-60f5-3ccc-8cb3-2a54c1c27c31', 50, 'Sn', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('3d7ca6d7-8931-3907-8796-31764365dfbd', '4d36db28-7809-3b08-8ad2-f668c2239280', 4, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('58801d0c-0aba-3f37-afb2-aff908b75e6a', '4d36db28-7809-3b08-8ad2-f668c2239280', 4, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('398d9ae4-b79b-3004-a3a1-043ecc52bc30', '4d36db28-7809-3b08-8ad2-f668c2239280', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('fc2ff4a4-c596-37af-a0e6-d82cf3ca1560', '4d36db28-7809-3b08-8ad2-f668c2239280', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('aaadd45b-9c72-352e-afe9-42da13bdc1d9', '4d36db28-7809-3b08-8ad2-f668c2239280', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('46f7e80e-ba0c-3764-9995-9c7fd0e1af80', '4d36db28-7809-3b08-8ad2-f668c2239280', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('f5cba422-c760-3582-8ef3-73a661adcaa8', '4d36db28-7809-3b08-8ad2-f668c2239280', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('3f5a8c40-2de0-31fb-9ebc-fc93948fecc8', '03d0671f-cb72-3c68-805c-b2048a9278db', 51, 'Sb', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('375ec73a-c531-369f-9104-4f5ae564440e', '3f5a8c40-2de0-31fb-9ebc-fc93948fecc8', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a4eeeb16-1d5c-317c-bec6-c69a1c004cce', '3f5a8c40-2de0-31fb-9ebc-fc93948fecc8', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('6cc9d2ac-a40f-3274-a7f5-0400454f37ba', '3f5a8c40-2de0-31fb-9ebc-fc93948fecc8', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('6fd41a4d-dc74-3f96-8395-5c29285aaec3', '3f5a8c40-2de0-31fb-9ebc-fc93948fecc8', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('3f272f3d-77e9-3751-808a-fd9bc67d7626', '3f5a8c40-2de0-31fb-9ebc-fc93948fecc8', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('be0dae34-6e66-33b8-bced-89482ef17857', '3f5a8c40-2de0-31fb-9ebc-fc93948fecc8', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('de7ed2c0-ab35-3b40-860a-8985a63b4780', '3f5a8c40-2de0-31fb-9ebc-fc93948fecc8', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('53326f9a-0714-37c1-ad4d-550730773294', '6ad44b4b-a17a-3eeb-a82e-9b549731c1b7', 52, 'Te', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a11a1069-fbcc-3a21-a46b-d215dacde1f9', '53326f9a-0714-37c1-ad4d-550730773294', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('24e69b3b-35c9-35dc-a6f6-18b46b868167', '53326f9a-0714-37c1-ad4d-550730773294', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('05f4ad56-911d-359b-b471-760e110857ca', '53326f9a-0714-37c1-ad4d-550730773294', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('fb5ddd6c-7d50-3965-9a22-c2152c18d63c', '53326f9a-0714-37c1-ad4d-550730773294', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('193df727-3f7f-3335-937c-86985c7368f6', '53326f9a-0714-37c1-ad4d-550730773294', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('9989505f-9286-3f19-8c6d-c913f69170d2', '53326f9a-0714-37c1-ad4d-550730773294', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('6d901612-2a23-3348-90f3-b3bdeb5863b5', '53326f9a-0714-37c1-ad4d-550730773294', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('be515b83-0137-3a61-b6d6-78ddc7f36ea6', '0b9284eb-fe2c-38bc-90e1-4fb95e46cd82', 53, 'I', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('03704b89-8a4c-30f3-9c49-c0d5eda50791', 'be515b83-0137-3a61-b6d6-78ddc7f36ea6', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3e6be46b-b0a7-3dc7-9108-751d7cc4c47e', 'be515b83-0137-3a61-b6d6-78ddc7f36ea6', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3ba7167d-a731-37c0-a47f-621d9fa6c177', 'be515b83-0137-3a61-b6d6-78ddc7f36ea6', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('2ae51287-32da-321a-803e-5780c176efd0', 'be515b83-0137-3a61-b6d6-78ddc7f36ea6', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('ba3903f7-2e26-3dab-accd-7f4778445504', 'be515b83-0137-3a61-b6d6-78ddc7f36ea6', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('f0304b27-6b91-3199-89e3-67b2ac32ad80', 'be515b83-0137-3a61-b6d6-78ddc7f36ea6', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('510a4fb4-5897-3655-ac8b-3d0895846f17', 'be515b83-0137-3a61-b6d6-78ddc7f36ea6', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('e6331b91-380a-3734-adfe-85dc0c0eb5e2', 'f88198c1-facb-39e4-af98-800207a99029', 54, 'Xe', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('18a90399-528f-3146-83c9-23c2aa1660cd', 'e6331b91-380a-3734-adfe-85dc0c0eb5e2', 0, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('b270f6d3-066a-3132-b6bd-7e69c4164b71', 'e6331b91-380a-3734-adfe-85dc0c0eb5e2', 0, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('699dfe84-a4a4-3a0e-bd19-3acb94a718d2', 'e6331b91-380a-3734-adfe-85dc0c0eb5e2', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('c501906c-e4d4-35c3-9c0d-a64c684663e6', 'e6331b91-380a-3734-adfe-85dc0c0eb5e2', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('890a4559-d97b-3043-9399-285788dcb51e', 'e6331b91-380a-3734-adfe-85dc0c0eb5e2', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('c94802db-acbf-3423-b096-0287b7288826', 'e6331b91-380a-3734-adfe-85dc0c0eb5e2', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('6430644c-0c14-3ee9-8926-1448b367452b', '99a584e6-dfd0-3ebb-8653-f60f085f8bf4', 55, 'Cs', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('03444b5f-657a-3862-bb47-fe3b9fea1fa4', '6430644c-0c14-3ee9-8926-1448b367452b', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3db644b4-74bf-309e-bb76-0b5d76a84acf', '6430644c-0c14-3ee9-8926-1448b367452b', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('140dfa3d-7baa-335c-b616-d3100926f9ff', '6430644c-0c14-3ee9-8926-1448b367452b', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('d9ec3e3e-107c-3b5d-b766-c55d59f045f2', '6430644c-0c14-3ee9-8926-1448b367452b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('75f7e70b-a65c-3817-bffc-f6d2a05822f4', '6430644c-0c14-3ee9-8926-1448b367452b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('465ba74e-4af1-3db7-a452-4fb5d0037860', '6430644c-0c14-3ee9-8926-1448b367452b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('6921b127-1eea-398b-bee8-73618fb9e968', '6430644c-0c14-3ee9-8926-1448b367452b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('835764df-6fb0-3c3a-b730-999d15863ecf', '678bd0b1-d318-3fc6-8825-7f9754113558', 56, 'Ba', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('b871ca52-2296-3892-ab27-4957be71b391', '835764df-6fb0-3c3a-b730-999d15863ecf', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e9b17498-536f-38d9-9865-a3adc134f398', '835764df-6fb0-3c3a-b730-999d15863ecf', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7c542463-2af1-3e67-88d7-02201545c3bc', '835764df-6fb0-3c3a-b730-999d15863ecf', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('99a20a70-50de-3ba0-b852-cea3e0d33ab4', '835764df-6fb0-3c3a-b730-999d15863ecf', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('30852c5c-e030-341d-a78c-ba811988cb73', '835764df-6fb0-3c3a-b730-999d15863ecf', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('240e7335-c642-333a-ac49-17d643eebb10', '835764df-6fb0-3c3a-b730-999d15863ecf', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('c2c510ed-6f28-38c5-8506-e008bd69a602', '835764df-6fb0-3c3a-b730-999d15863ecf', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('a1520b8b-40ca-3390-906e-f9a27e1cbce0', '295e6ae8-8346-3ef6-bae8-1e285eab9e60', 57, 'La', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4f002f5a-d072-357e-b516-8c58f223ebc3', 'a1520b8b-40ca-3390-906e-f9a27e1cbce0', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('2b6ac0ab-cbca-3d7b-bea9-ddd79a369d15', 'a1520b8b-40ca-3390-906e-f9a27e1cbce0', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ec77e677-aefb-30df-b1dd-95764dd14882', 'a1520b8b-40ca-3390-906e-f9a27e1cbce0', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('d9103b5b-ef34-39ac-ab0f-a4bf38fbf504', 'a1520b8b-40ca-3390-906e-f9a27e1cbce0', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('eb593a4b-57f1-31f4-80cf-24771d56fae3', 'a1520b8b-40ca-3390-906e-f9a27e1cbce0', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('45bbbbb0-0d50-3b55-bbda-40219c24a6c4', 'a1520b8b-40ca-3390-906e-f9a27e1cbce0', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('6ecfc0d8-4b91-344c-b21f-45f66917b073', 'a1520b8b-40ca-3390-906e-f9a27e1cbce0', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('c3f94deb-fff5-37ef-9643-7f126a7152de', 'ebe72bab-a295-3727-968b-b3480eb7ed1f', 58, 'Ce', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('eac49bf2-1b7b-3d05-a04b-c67677fabc02', 'c3f94deb-fff5-37ef-9643-7f126a7152de', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('2ecc1ebb-15ca-3a0b-a25d-aa8db9e51d15', 'c3f94deb-fff5-37ef-9643-7f126a7152de', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('950b0796-075e-38b8-9ac3-35734a6425c8', 'c3f94deb-fff5-37ef-9643-7f126a7152de', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a2f176d3-e69b-3274-8411-d57b29e3ef9a', 'c3f94deb-fff5-37ef-9643-7f126a7152de', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('62cd341c-220d-3525-93f1-4746c182a652', 'c3f94deb-fff5-37ef-9643-7f126a7152de', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('22004d6b-14cb-39f3-8f99-747b9d170f38', 'c3f94deb-fff5-37ef-9643-7f126a7152de', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('989428fd-5a06-3016-92b2-67f78a2aaf14', 'c3f94deb-fff5-37ef-9643-7f126a7152de', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('6ac1a49c-8cb6-3ca2-9a74-050919a02981', '1a9b134f-e9e9-3b0d-8f1e-76e50df7ec5f', 59, 'Pr', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('59eee2a2-e9ed-3bf7-a2c4-ca12a8a50f3c', '6ac1a49c-8cb6-3ca2-9a74-050919a02981', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('de39752a-b907-3fa3-b92e-3d42676b3f66', '6ac1a49c-8cb6-3ca2-9a74-050919a02981', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a62fff63-04ca-3506-b394-1a72efe943ed', '6ac1a49c-8cb6-3ca2-9a74-050919a02981', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('bc3b795c-5a9c-3719-b0f7-70853400a4b6', '6ac1a49c-8cb6-3ca2-9a74-050919a02981', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('363fc91a-d850-31cb-bb96-88d8c74536df', '6ac1a49c-8cb6-3ca2-9a74-050919a02981', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('4b41139e-59a8-338c-9c25-5ac78e2c5db5', '6ac1a49c-8cb6-3ca2-9a74-050919a02981', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('3df52838-43a1-3b68-a863-48d151c1f0d0', '6ac1a49c-8cb6-3ca2-9a74-050919a02981', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('90d596c4-1a0a-3d6e-8300-61d3dca4c4fd', '2ffecda0-c2cc-36b3-bc59-4791c274ecb1', 60, 'Nd', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('fe95a894-2fd7-3315-bea3-783cbe19d4a7', '90d596c4-1a0a-3d6e-8300-61d3dca4c4fd', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('4660c01d-31e2-37bd-a7dc-e622fa439d09', '90d596c4-1a0a-3d6e-8300-61d3dca4c4fd', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('2c899fd3-b977-35f0-86c8-61a4009d0205', '90d596c4-1a0a-3d6e-8300-61d3dca4c4fd', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('62d4da10-7953-3b3f-9fe4-e4cacedb18f9', '90d596c4-1a0a-3d6e-8300-61d3dca4c4fd', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('a9172bbd-c442-3df5-9407-d069719ce332', '90d596c4-1a0a-3d6e-8300-61d3dca4c4fd', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d7a515f1-264b-34d4-9cf5-f26e97e8a991', '90d596c4-1a0a-3d6e-8300-61d3dca4c4fd', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('5a5e6197-8c75-3c94-a1f8-ef3653b54978', '90d596c4-1a0a-3d6e-8300-61d3dca4c4fd', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('8395242a-7df4-3ebb-9993-d71b978fff0d', 'd4dd9dc1-b8f8-3c50-acf0-28b36921662d', 61, 'Pm', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('959f56c9-f8e8-3374-8287-7ef482e2fda3', '8395242a-7df4-3ebb-9993-d71b978fff0d', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('83a4ba3a-3fb1-38a4-a683-84cc7ed4af17', '8395242a-7df4-3ebb-9993-d71b978fff0d', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e28c263e-7fdb-3ff6-bd0b-af5af86796ff', '8395242a-7df4-3ebb-9993-d71b978fff0d', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ec40485c-e72e-39fb-ad33-40dcb210efcb', '8395242a-7df4-3ebb-9993-d71b978fff0d', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('cfb4831d-3d2d-30f8-8c3a-07e21ec6d965', '8395242a-7df4-3ebb-9993-d71b978fff0d', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('9d4416b2-1bfa-3071-937f-58b0d57381f8', '8395242a-7df4-3ebb-9993-d71b978fff0d', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('9a19ec0e-da26-3ace-af61-eab898d5f5c8', '8395242a-7df4-3ebb-9993-d71b978fff0d', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('993920f2-ab9a-34ff-8c59-e61756952d2e', '9d388111-ae87-3f65-b05f-bf61126a759e', 62, 'Sm', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('168250ab-3cea-3b3a-98f2-10bada0739ee', '993920f2-ab9a-34ff-8c59-e61756952d2e', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('fde5bbd4-3143-3d90-a986-99262e1bef57', '993920f2-ab9a-34ff-8c59-e61756952d2e', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('400d2108-a0ec-376f-8c03-3807b8945191', '993920f2-ab9a-34ff-8c59-e61756952d2e', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ddb473e0-220e-34f2-9d99-59c872d81b04', '993920f2-ab9a-34ff-8c59-e61756952d2e', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('21418d6a-3ca7-3897-b4fa-408af8b11606', '993920f2-ab9a-34ff-8c59-e61756952d2e', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('208142c6-fec3-351b-a10a-3cee6018fa15', '993920f2-ab9a-34ff-8c59-e61756952d2e', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('fa8c121d-0216-3074-875b-fd69db613ac6', '993920f2-ab9a-34ff-8c59-e61756952d2e', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('b0c76589-c5c9-3645-9d13-2e0ef30a1726', 'b799623c-c9a3-3945-a7db-d28f51b00e66', 63, 'Eu', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c4d5dee3-27b6-3e61-9220-3ddc4062a0f6', 'b0c76589-c5c9-3645-9d13-2e0ef30a1726', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('f2744190-11b1-3d09-857a-88b31f97afb5', 'b0c76589-c5c9-3645-9d13-2e0ef30a1726', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7565063f-f83a-3fe9-be9e-952f34b73451', 'b0c76589-c5c9-3645-9d13-2e0ef30a1726', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('1385f5fd-d0b3-356b-870d-22bc9ddf3dc6', 'b0c76589-c5c9-3645-9d13-2e0ef30a1726', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('c87239d1-af71-37e1-96e3-34af8e0c701e', 'b0c76589-c5c9-3645-9d13-2e0ef30a1726', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('7da1c247-0581-3a24-85db-72736c557d7c', 'b0c76589-c5c9-3645-9d13-2e0ef30a1726', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('62b268d2-3907-3f90-acee-d8f0a1463962', 'b0c76589-c5c9-3645-9d13-2e0ef30a1726', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('b0b4989f-1fe5-34a3-a119-e3cd3279c77b', 'b585dc57-6eb6-3190-b685-3ec9612b2808', 64, 'Gd', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('46de8892-72bd-3e0b-8248-c23482a5e162', 'b0b4989f-1fe5-34a3-a119-e3cd3279c77b', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cf39dfe3-9178-3179-861a-14b5f430260c', 'b0b4989f-1fe5-34a3-a119-e3cd3279c77b', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a15b4e1e-8e04-3f64-a012-ead98773a0c5', 'b0b4989f-1fe5-34a3-a119-e3cd3279c77b', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('1ba4409b-ee8e-3c05-9767-cb6a099ddce6', 'b0b4989f-1fe5-34a3-a119-e3cd3279c77b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('9dbd2b19-247c-3fbd-af7f-1539f2f8e327', 'b0b4989f-1fe5-34a3-a119-e3cd3279c77b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('417b3328-d48b-38a5-ad44-6963d0349b76', 'b0b4989f-1fe5-34a3-a119-e3cd3279c77b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('d6a680ff-839f-391a-8362-7df70844d570', 'b0b4989f-1fe5-34a3-a119-e3cd3279c77b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('181cdb69-f2d5-34c6-ae10-4a19d056c513', '9aedf65f-a061-303e-886b-ca0cba8a1462', 65, 'Tb', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('83f6a6c1-1cce-3128-af1d-4fa4cfc7dd02', '181cdb69-f2d5-34c6-ae10-4a19d056c513', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('1d7f59b6-84bb-3d3d-887b-43af913fca95', '181cdb69-f2d5-34c6-ae10-4a19d056c513', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('17f155ab-a750-3a33-abdb-69d63f165690', '181cdb69-f2d5-34c6-ae10-4a19d056c513', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('eb11690e-e92f-3e69-ba51-e6182008cb40', '181cdb69-f2d5-34c6-ae10-4a19d056c513', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('9d2c746f-d56a-3ce7-814b-bb6dad6eac92', '181cdb69-f2d5-34c6-ae10-4a19d056c513', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('fe852706-09ea-30b7-8962-33d5e6775186', '181cdb69-f2d5-34c6-ae10-4a19d056c513', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('3756f845-8b15-3dd7-8af5-84d2dd1425c1', '181cdb69-f2d5-34c6-ae10-4a19d056c513', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('4bafc981-fdba-30ce-a82b-faa298f8d535', '65ae545a-35e1-3133-a443-8d29b45b890d', 66, 'Dy', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a5a0e077-3378-39d5-83d9-a08c135c5fb5', '4bafc981-fdba-30ce-a82b-faa298f8d535', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('81add169-1ab5-3c88-9b09-580cd7127b8c', '4bafc981-fdba-30ce-a82b-faa298f8d535', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7c900aef-af58-3180-9308-d5233132eb26', '4bafc981-fdba-30ce-a82b-faa298f8d535', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('3d626376-3a73-3ee4-a900-ac4a1a0ed12d', '4bafc981-fdba-30ce-a82b-faa298f8d535', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('21d81683-9274-38c3-a311-96dcc98c1855', '4bafc981-fdba-30ce-a82b-faa298f8d535', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d287ae7b-4acb-3958-bcb9-d0c1df326af0', '4bafc981-fdba-30ce-a82b-faa298f8d535', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('cb60b6b2-7b7b-3d8b-85cc-937fb152636b', '4bafc981-fdba-30ce-a82b-faa298f8d535', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('9bcfb7ed-8fad-3d95-bf79-67e4b85ab354', '41d3acd2-1820-37a4-bbe3-61a48865f401', 67, 'Ho', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('8761c35c-4aee-3402-b224-26d5ba75e9e6', '9bcfb7ed-8fad-3d95-bf79-67e4b85ab354', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('1d88533e-5d65-3219-ad77-27bf4326bbe6', '9bcfb7ed-8fad-3d95-bf79-67e4b85ab354', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a5d7f9c8-5794-3751-81fc-8f95bb7613c8', '9bcfb7ed-8fad-3d95-bf79-67e4b85ab354', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('77d071bf-1466-35e7-bc7b-cde1bbed50bb', '9bcfb7ed-8fad-3d95-bf79-67e4b85ab354', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('3b6f3b83-92f1-3cb8-afd6-7e0370b93324', '9bcfb7ed-8fad-3d95-bf79-67e4b85ab354', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('3c00fc04-6bbb-3f82-9aab-0f51df65c833', '9bcfb7ed-8fad-3d95-bf79-67e4b85ab354', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('4dbb1825-3110-3c03-9d06-aade6e45e759', '9bcfb7ed-8fad-3d95-bf79-67e4b85ab354', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('e71188d3-d0ce-3645-87bd-c54d39c0dc63', '7be3fd83-c9fc-3cdd-bca4-9b6241c1ba74', 68, 'Er', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4f733878-2c57-34dd-9127-f2dd76b30482', 'e71188d3-d0ce-3645-87bd-c54d39c0dc63', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3cc1b927-d7f4-33be-b477-2562c49c6f25', 'e71188d3-d0ce-3645-87bd-c54d39c0dc63', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e6e99e2b-3d95-363c-8c6e-95f617d7e0ee', 'e71188d3-d0ce-3645-87bd-c54d39c0dc63', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('43b39c33-5f80-3841-ac62-e585bb3ff993', 'e71188d3-d0ce-3645-87bd-c54d39c0dc63', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('9ad568f0-724a-39fa-83ed-95c6578b87f0', 'e71188d3-d0ce-3645-87bd-c54d39c0dc63', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('beea593f-d4cc-3d51-9dc9-96b0502280f4', 'e71188d3-d0ce-3645-87bd-c54d39c0dc63', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('db933cbb-665f-3b57-a753-9b47a363501d', 'e71188d3-d0ce-3645-87bd-c54d39c0dc63', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('cb8405c2-e72c-327e-8f34-0469213c50f4', 'ab897525-e8d5-338d-a674-98134bc8a98b', 69, 'Tm', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c1a29ef8-7dd1-39a9-947a-4b7f80abdff3', 'cb8405c2-e72c-327e-8f34-0469213c50f4', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('9e2f57bd-430a-3e99-a100-a47e59069306', 'cb8405c2-e72c-327e-8f34-0469213c50f4', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('5f7067ee-7eb8-3db6-85fe-1eda4a6df828', 'cb8405c2-e72c-327e-8f34-0469213c50f4', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('5fd0b0b1-a0e4-35e6-afb0-ac09e31060e0', 'cb8405c2-e72c-327e-8f34-0469213c50f4', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('1f0af98c-8313-30dc-a27c-c690e3542f9f', 'cb8405c2-e72c-327e-8f34-0469213c50f4', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('3cdcde2c-07aa-391e-b84e-7fa7c8c077b0', 'cb8405c2-e72c-327e-8f34-0469213c50f4', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('1cef62e9-6b32-3012-aaf6-414c4664a3d1', 'cb8405c2-e72c-327e-8f34-0469213c50f4', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('e265f53a-c463-384f-95da-81a38dffcd9a', 'c995395f-9bc0-341d-a4ef-992ea97c070e', 70, 'Yb', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c4fc647a-de4c-30be-a310-45afa4d1e8df', 'e265f53a-c463-384f-95da-81a38dffcd9a', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('83516b14-77f0-3387-9fe9-745ae4db25d9', 'e265f53a-c463-384f-95da-81a38dffcd9a', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('5cf83d33-2f51-3b8c-bb06-b9a2141ea7af', 'e265f53a-c463-384f-95da-81a38dffcd9a', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('7c86bb9d-b067-3253-be63-e26c9b023bf1', 'e265f53a-c463-384f-95da-81a38dffcd9a', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('63098736-efaa-377a-bdc8-5f356c880226', 'e265f53a-c463-384f-95da-81a38dffcd9a', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('2b98f98e-87ec-32e4-92ae-8c7dedb00e66', 'e265f53a-c463-384f-95da-81a38dffcd9a', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('20e88b13-3a18-39f9-b812-62d9a3db3952', 'e265f53a-c463-384f-95da-81a38dffcd9a', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('64ee317f-8433-3873-8e4e-298866af220e', '7e4bb7ae-5f35-379a-9a9e-46f70c5db764', 71, 'Lu', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4a4a0b95-a324-37df-9995-bb434b027a71', '64ee317f-8433-3873-8e4e-298866af220e', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ad3e347b-be17-337c-924a-252cc31fa288', '64ee317f-8433-3873-8e4e-298866af220e', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('577cc95c-f486-31cd-a0f6-eeeb84224ea3', '64ee317f-8433-3873-8e4e-298866af220e', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('795b2c08-b66c-3a04-aaf2-5f601da990d1', '64ee317f-8433-3873-8e4e-298866af220e', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('b211fe14-00b6-3917-a949-a9f05dd0cf14', '64ee317f-8433-3873-8e4e-298866af220e', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('2531c8f4-f102-3ab8-ab3a-7375be7a5808', '64ee317f-8433-3873-8e4e-298866af220e', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('f2db611b-9c7b-3c51-90e7-8aa9bf3a4c03', '64ee317f-8433-3873-8e4e-298866af220e', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('64875a11-a442-3979-8252-fe7df8c0d8b5', '8bc18bd7-f8d6-3e11-afba-4132387359cc', 72, 'Hf', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('b4230a80-4293-3a2c-b74b-da5b32f8be00', '64875a11-a442-3979-8252-fe7df8c0d8b5', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('b3471c87-c569-361f-ae4e-a2860d949ddb', '64875a11-a442-3979-8252-fe7df8c0d8b5', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('04f01b3e-42cc-3a49-b318-29d8d9d50922', '64875a11-a442-3979-8252-fe7df8c0d8b5', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('bd20f9e4-1d3e-3627-a7ed-8e2442a61cd6', '64875a11-a442-3979-8252-fe7df8c0d8b5', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('d1a1144e-2445-32fd-a0f6-d040c6802f1f', '64875a11-a442-3979-8252-fe7df8c0d8b5', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('8adfd940-1a09-3cf4-a9ec-9ee55598abd7', '64875a11-a442-3979-8252-fe7df8c0d8b5', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('8d53ae14-714f-324d-a7e9-440d04cc5902', '64875a11-a442-3979-8252-fe7df8c0d8b5', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('0c2b48c2-c446-36a7-ae3b-6e96ced0dd99', 'a5f43311-6937-3231-82f3-8cd8ab131307', 73, 'Ta', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4112b89d-0b27-3389-81b3-69237dddeca0', '0c2b48c2-c446-36a7-ae3b-6e96ced0dd99', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('21301bab-572a-3431-9639-5cc8a5631568', '0c2b48c2-c446-36a7-ae3b-6e96ced0dd99', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ee1c8f64-8b7f-394d-b152-f5d765522553', '0c2b48c2-c446-36a7-ae3b-6e96ced0dd99', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('b1c46484-154e-3ca0-a9a0-bda27e2fda72', '0c2b48c2-c446-36a7-ae3b-6e96ced0dd99', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('b9f3c9c3-a658-331c-a166-98b5a8136c99', '0c2b48c2-c446-36a7-ae3b-6e96ced0dd99', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('6513a235-6aa0-36ac-9d60-f49a0e5357ab', '0c2b48c2-c446-36a7-ae3b-6e96ced0dd99', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('d52c8e73-e096-33b8-a56b-1dc698bd3e61', '0c2b48c2-c446-36a7-ae3b-6e96ced0dd99', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('e5756e71-4507-3816-8c9e-49ee4fc5be42', '6b9cb580-b298-362b-8f1a-55d24a18f51b', 74, 'W', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('1d9b52ba-0b10-366d-a017-b3838eaefa9c', 'e5756e71-4507-3816-8c9e-49ee4fc5be42', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cfa846a0-d8f3-34bf-a3f9-9fed84c5b759', 'e5756e71-4507-3816-8c9e-49ee4fc5be42', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7f457514-e240-34a7-bf68-29e070d362bc', 'e5756e71-4507-3816-8c9e-49ee4fc5be42', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('24f9a055-5a42-3def-88e3-8140cfae8d98', 'e5756e71-4507-3816-8c9e-49ee4fc5be42', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('cf9eb8a5-e8d9-30ad-93da-b66c5db748a3', 'e5756e71-4507-3816-8c9e-49ee4fc5be42', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ca8c9820-b4b1-3593-b5b2-abf867c398da', 'e5756e71-4507-3816-8c9e-49ee4fc5be42', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('7329f989-7fa6-3762-ac78-c018ed22fb8f', 'e5756e71-4507-3816-8c9e-49ee4fc5be42', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('d888aea6-520f-3eea-99f3-28bcf795aa19', 'b3d8ed3e-5c5a-3564-be4f-38d4c7e01450', 75, 'Re', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('6d24f1a9-7557-3673-b6c8-b2b76030fe2a', 'd888aea6-520f-3eea-99f3-28bcf795aa19', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('248039c0-87e5-3578-9587-05a0ef8f69ad', 'd888aea6-520f-3eea-99f3-28bcf795aa19', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('95a14b08-97bb-3d81-a9d2-b516addf0bb1', 'd888aea6-520f-3eea-99f3-28bcf795aa19', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ac386fc1-a354-32a2-9f82-5fe9706ca059', 'd888aea6-520f-3eea-99f3-28bcf795aa19', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('aa037f3c-24ab-30b4-9755-4caa3626c926', 'd888aea6-520f-3eea-99f3-28bcf795aa19', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('c9a1cfe5-898d-33b6-ae00-4fae8e846ab1', 'd888aea6-520f-3eea-99f3-28bcf795aa19', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('fb09040d-ab51-3964-83a0-936a292523c1', 'd888aea6-520f-3eea-99f3-28bcf795aa19', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('39670547-050f-3199-a867-3dcc4ae7e041', '87ff52f0-264e-3e37-9aef-4d587f80235e', 76, 'Os', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('661bd294-4402-3413-b5eb-f6f7582dbcb3', '39670547-050f-3199-a867-3dcc4ae7e041', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('371105fd-7675-3e08-9bb5-5d4b4e2d0fb4', '39670547-050f-3199-a867-3dcc4ae7e041', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a3f88577-0b1e-3491-9a9b-b3a6c7df905b', '39670547-050f-3199-a867-3dcc4ae7e041', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('8a3f7d0b-201e-3684-bdff-c2a8729dcd90', '39670547-050f-3199-a867-3dcc4ae7e041', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('faaac581-8c34-37ec-ace4-6e2922d774a6', '39670547-050f-3199-a867-3dcc4ae7e041', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ebeb3ea3-6de2-3a71-b1af-e8bd71243c10', '39670547-050f-3199-a867-3dcc4ae7e041', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('28c19aa5-4493-3a1e-b50f-98e21fa82b95', '39670547-050f-3199-a867-3dcc4ae7e041', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('fe3bd345-3dcd-3644-b2c2-b99ffc3273c4', '86fdef98-209d-3645-be77-1b79205fcf80', 77, 'Ir', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('66117d4c-8c67-3e0d-80a4-6b129db973a4', 'fe3bd345-3dcd-3644-b2c2-b99ffc3273c4', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('40e965cd-1b27-3e86-8654-1d914b7070dd', 'fe3bd345-3dcd-3644-b2c2-b99ffc3273c4', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('225013e1-2819-3f4c-9af2-c037930d6d60', 'fe3bd345-3dcd-3644-b2c2-b99ffc3273c4', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('71f078ed-90a1-3892-a3b2-0c389bfc3a3f', 'fe3bd345-3dcd-3644-b2c2-b99ffc3273c4', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('e3cada68-a1da-3182-8757-345c1a953933', 'fe3bd345-3dcd-3644-b2c2-b99ffc3273c4', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('b12ee5d5-f7df-345d-ad36-944358b358a7', 'fe3bd345-3dcd-3644-b2c2-b99ffc3273c4', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('c2372c14-a320-32e7-8c16-cb45cedfa12c', 'fe3bd345-3dcd-3644-b2c2-b99ffc3273c4', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('7d06e1d1-3b3a-32cd-98ea-7b4755fde345', '94d7a5ba-bc4d-3b9b-bc58-b2ff48e22ae8', 78, 'Pt', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('5d86ccda-ce4d-32c3-bb93-6114248db15b', '7d06e1d1-3b3a-32cd-98ea-7b4755fde345', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('88d05e97-909d-33f0-b2a8-bafb9646e8df', '7d06e1d1-3b3a-32cd-98ea-7b4755fde345', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c828a40e-a4a0-3fdd-aed1-60cabf53bd22', '7d06e1d1-3b3a-32cd-98ea-7b4755fde345', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('7dd665f5-3a44-35b7-b94c-5115e9e65107', '7d06e1d1-3b3a-32cd-98ea-7b4755fde345', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('931041cf-dbd9-3817-9bab-124ce2b2def2', '7d06e1d1-3b3a-32cd-98ea-7b4755fde345', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('8bb8b3e1-c309-3990-8d93-7b13895ab252', '7d06e1d1-3b3a-32cd-98ea-7b4755fde345', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('042485fa-f6b2-3913-ad09-df873b796192', '7d06e1d1-3b3a-32cd-98ea-7b4755fde345', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('eb406fc0-823b-3465-b1d2-cfb57fc15f19', '17a75f44-71d5-3950-8ac6-8f4f0d179472', 79, 'Au', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('f0dd8472-3fb8-3b8f-87b0-fffef4afe3ac', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('80f35a93-bb0a-3357-8a2d-7cd87b2f18cd', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c0cb15d4-25f4-305c-b786-e8463c3620fa', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('57192bed-1858-3c1d-9bf5-34bcdd0a340b', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('9e506204-5381-3a2f-8bf0-4332c3680756', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 2.54, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('30af6f2a-d20f-3fdf-a930-7ed036ba3736', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 'EMPIRICAL_ATOMIC', 135, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('331d290d-88fb-32f6-8583-596461316ddf', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 'METALLIC', 144, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('9ccf3d15-d9f9-31f7-bfa1-5a6c1fcd764c', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 'IONIC', 85, 3, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('fda4e97e-48bb-39c1-9d48-7008377c5a17', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 19300.00, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('6d10d0a1-c8c1-3410-963c-80b067d9efb1', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 'MELTING', 1337.33, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('333ecd2e-66fb-3486-9b36-3ac7e6063ebd', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 'BOILING', 3129, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('e0a12994-625c-3b15-8bef-391994425729', 'eb406fc0-823b-3465-b1d2-cfb57fc15f19', 'yellow', 'Yellow metallic solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('5be711a2-e07f-377e-afae-fb98f032b782', '134e3bcd-f26a-3c0d-8335-774532bf8315', 80, 'Hg', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('7649dafa-ccb2-3627-b80a-b36c490884d4', '5be711a2-e07f-377e-afae-fb98f032b782', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('cc255889-ae4c-31d2-9055-ff85f2d2cd94', '5be711a2-e07f-377e-afae-fb98f032b782', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('8093cba5-9831-36bd-b536-8b6cfba56ea0', '5be711a2-e07f-377e-afae-fb98f032b782', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('dd830109-dade-3896-bf24-1f9bda72f43d', '5be711a2-e07f-377e-afae-fb98f032b782', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('2606f512-46ad-3bb4-a3be-0c79c77cf964', '5be711a2-e07f-377e-afae-fb98f032b782', 2.00, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('47c6260c-ec35-3e8f-a18c-2f916107e648', '5be711a2-e07f-377e-afae-fb98f032b782', 'EMPIRICAL_ATOMIC', 150, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('8e1191be-97b7-3273-aad0-1e7c2ad22822', '5be711a2-e07f-377e-afae-fb98f032b782', 'METALLIC', 151, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('9a665782-057b-39d4-84fb-1244dab4a424', '5be711a2-e07f-377e-afae-fb98f032b782', 'IONIC', 102, 2, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('381c8e17-a32c-318e-a1e9-8373daf8d344', '5be711a2-e07f-377e-afae-fb98f032b782', 13534.000, 298.15, 100, 'LIQUID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('5c45a535-567b-3d67-8047-3ad9558e94b6', '5be711a2-e07f-377e-afae-fb98f032b782', 'MELTING', 234.32, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e7513fac-6358-337d-a73c-1c642d7ee826', '5be711a2-e07f-377e-afae-fb98f032b782', 'BOILING', 629.88, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('14b5a5ca-a15b-3586-b366-b74de26c0482', '5be711a2-e07f-377e-afae-fb98f032b782', 'silvery-white', 'Silvery liquid metal', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('57d7f438-cd41-3c5e-821a-3875486f4aa2', '475b8dcd-c297-33c2-9ff1-28d59ebdaefb', 81, 'Tl', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c16d8b31-fae5-37f0-8dcf-1dfd19fc9261', '57d7f438-cd41-3c5e-821a-3875486f4aa2', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e78953ba-0e25-34fc-84bf-45977da1594b', '57d7f438-cd41-3c5e-821a-3875486f4aa2', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('28adfdd0-4254-3f02-9b9b-4d64c918a7e5', '57d7f438-cd41-3c5e-821a-3875486f4aa2', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('615c075d-72f1-325b-b3fb-12804482d142', '57d7f438-cd41-3c5e-821a-3875486f4aa2', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('bfbc2923-7789-34a0-a009-eebae7bd392b', '57d7f438-cd41-3c5e-821a-3875486f4aa2', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ea37fc6c-2cc2-3d84-a6a6-4475897f341b', '57d7f438-cd41-3c5e-821a-3875486f4aa2', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('10a73d08-702c-3553-a064-c9372eb02d10', '57d7f438-cd41-3c5e-821a-3875486f4aa2', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('12144bf5-6e59-3238-bceb-e3affc3c5d81', '48a5bc29-359f-379b-afcd-c8c4b9564219', 82, 'Pb', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('607ef142-d177-311d-a0c9-da5c52a075c6', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('75a7c15e-2dcb-343b-a6b3-fe97178cce94', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 4, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('f00e4061-1c3e-3877-ac75-a3afc96437a0', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('5ea1d10a-e6b0-3b91-97f7-74a3cf4f3769', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 4, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('096581af-a2a3-363b-9f46-31786e297402', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 2.33, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('cd585426-2800-36c1-859d-a71dcd5b548c', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 'EMPIRICAL_ATOMIC', 180, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('9267bf36-3ac5-35d4-9532-9aa8513e4ff4', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 'METALLIC', 175, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('934e4cfe-1ef4-3f8f-8dfa-5f124ecb4541', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 'IONIC', 119, 2, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('7b880568-e9ab-3780-877f-899492bcbe62', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 'IONIC', 77.5, 4, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('a5cbd225-1741-3fb6-875d-5377ac4953d8', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 11340.00, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('1bb1c116-3f2f-33c4-a51a-aa2c147a5ab0', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 'MELTING', 600.61, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('9979029b-16ad-3e4f-9110-7e54355f576e', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 'BOILING', 2022, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('aa2a4e87-f227-3697-87d2-21ef6e04efdb', '12144bf5-6e59-3238-bceb-e3affc3c5d81', 'dull gray', 'Dull gray metallic solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('ae591416-a223-3d19-9fa3-020c9960757b', '1f5a9f43-4bde-3f3c-aa3c-cad327ba7297', 83, 'Bi', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a72ea6e6-782b-3777-8521-00c892a2ba5e', 'ae591416-a223-3d19-9fa3-020c9960757b', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('10eb5b8b-9df2-3efd-9310-a854eb082dd4', 'ae591416-a223-3d19-9fa3-020c9960757b', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('0fcdac7f-24de-369e-842a-0d89209db0ec', 'ae591416-a223-3d19-9fa3-020c9960757b', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a5cb00fd-5c6a-3cfd-9537-0f6c665215c1', 'ae591416-a223-3d19-9fa3-020c9960757b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('ee23e6b9-32a4-3e77-8b31-b580c5883b97', 'ae591416-a223-3d19-9fa3-020c9960757b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('217731cf-2b4e-3713-812e-d4f47256048f', 'ae591416-a223-3d19-9fa3-020c9960757b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('6b9f1f30-d98b-38b9-95c7-4e326978e2a0', 'ae591416-a223-3d19-9fa3-020c9960757b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('a525ccb9-1e70-3587-ab6c-4b1082f09d5a', '9d5570df-80b2-3607-b576-f720933b25a0', 84, 'Po', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('d41d95aa-2001-304b-990c-4ea7c88135d2', 'a525ccb9-1e70-3587-ab6c-4b1082f09d5a', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('cc981a8c-e481-3222-867a-7adbe5514640', 'a525ccb9-1e70-3587-ab6c-4b1082f09d5a', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a5dd84a1-9696-3c72-90be-1391673863a6', 'a525ccb9-1e70-3587-ab6c-4b1082f09d5a', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('f0649d13-f1bf-3849-b5a7-39a774699f58', 'a525ccb9-1e70-3587-ab6c-4b1082f09d5a', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('d704c708-f3ba-35c1-b3c9-059cdad9a3ff', 'a525ccb9-1e70-3587-ab6c-4b1082f09d5a', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('c2715bdb-568d-3dc3-af2a-c69e27dc21d1', 'a525ccb9-1e70-3587-ab6c-4b1082f09d5a', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('ce30e732-8821-319b-8c65-894481e87c80', 'a525ccb9-1e70-3587-ab6c-4b1082f09d5a', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('f6929418-3fb2-3484-be2c-6936c00876b9', '1cd55faf-8e34-3755-957a-280a6893aa07', 85, 'At', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('2e809acb-72a9-3639-a09f-e2dc41ab1e69', 'f6929418-3fb2-3484-be2c-6936c00876b9', 1, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('52fa8042-e961-3b0a-ba6b-611930c81852', 'f6929418-3fb2-3484-be2c-6936c00876b9', 1, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3f582532-fcf7-3641-b052-7f03ebae65ac', 'f6929418-3fb2-3484-be2c-6936c00876b9', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a9b540a1-a699-3a50-8f89-9757e98cf1f5', 'f6929418-3fb2-3484-be2c-6936c00876b9', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('b9d5cfac-4fb9-37f7-b851-0f313921253a', 'f6929418-3fb2-3484-be2c-6936c00876b9', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('8a6966bc-4e49-3b61-885b-1e0e5854a826', 'f6929418-3fb2-3484-be2c-6936c00876b9', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('111ae55d-15f2-3901-ac06-43bdd4040c7f', 'f6929418-3fb2-3484-be2c-6936c00876b9', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('a6609999-8105-3a4a-a1df-bb6b267c604b', '3e4dbc7a-af53-3780-bd6f-7ab7d57b4f30', 86, 'Rn', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('36990caf-b898-313c-a452-1f81fa51d1c5', 'a6609999-8105-3a4a-a1df-bb6b267c604b', 0, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3ad15bd5-57de-30c2-9132-61a4a3d54900', 'a6609999-8105-3a4a-a1df-bb6b267c604b', 0, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('4c94606a-896c-3930-bbb7-adc1d44cb776', 'a6609999-8105-3a4a-a1df-bb6b267c604b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('4e88f19a-913f-3efb-8645-acf7b8475479', 'a6609999-8105-3a4a-a1df-bb6b267c604b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('69df65fa-33b4-3600-a47c-109e8ab0cc9a', 'a6609999-8105-3a4a-a1df-bb6b267c604b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('c04e6388-8e38-3b17-a8a2-84232172aa5c', 'a6609999-8105-3a4a-a1df-bb6b267c604b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('98021f78-bd9e-3e16-b2bd-859ca73f256f', '1964bad5-aba3-328a-9779-5e46826b81fe', 87, 'Fr', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('7de976f4-1cf5-3db0-8e12-6b1e3f2c1f18', '98021f78-bd9e-3e16-b2bd-859ca73f256f', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('9d480469-3975-3837-a3e9-77f5f0bd3181', '98021f78-bd9e-3e16-b2bd-859ca73f256f', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('457e8d36-bd78-36bd-bcfe-7edadd83e03e', '98021f78-bd9e-3e16-b2bd-859ca73f256f', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('e4bd8524-a1eb-3713-9d48-f77de38aa8ab', '98021f78-bd9e-3e16-b2bd-859ca73f256f', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('3ee367ec-d2ec-3969-88ff-0e6ad48fe60e', '98021f78-bd9e-3e16-b2bd-859ca73f256f', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('92f561af-b18a-34ab-870e-f6dc4e7087bd', '98021f78-bd9e-3e16-b2bd-859ca73f256f', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('83b9dac8-d783-336e-a7ef-dcd6f761e921', '98021f78-bd9e-3e16-b2bd-859ca73f256f', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('c45c5230-fa4f-3d74-9d7a-e6387c8ffa85', '2c1f6337-471b-3c0f-874a-b48bfa9ac2a5', 88, 'Ra', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('2bd09bc7-4f0a-34b6-ae35-c8ea9eeba948', 'c45c5230-fa4f-3d74-9d7a-e6387c8ffa85', 2, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('6f5299c6-e823-3884-8bfc-aa5a3f09ba89', 'c45c5230-fa4f-3d74-9d7a-e6387c8ffa85', 2, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e1975823-a494-3953-bd30-7084e062a777', 'c45c5230-fa4f-3d74-9d7a-e6387c8ffa85', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a5826619-5989-32bf-837c-1deaea841c2d', 'c45c5230-fa4f-3d74-9d7a-e6387c8ffa85', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('212eaff2-3e5d-3e5c-8348-42a7914626ab', 'c45c5230-fa4f-3d74-9d7a-e6387c8ffa85', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('b038f2a4-ffa2-3930-b809-abdb4d76a94b', 'c45c5230-fa4f-3d74-9d7a-e6387c8ffa85', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('de551978-58e7-31b1-bf09-56f90d0f3710', 'c45c5230-fa4f-3d74-9d7a-e6387c8ffa85', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('fdc60e07-5859-39e0-bab1-1733ca83302e', '9632e14c-8b51-3e38-9ecf-dfa7d438214c', 89, 'Ac', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a34a5a04-96c9-3de9-9660-a2643c87329f', 'fdc60e07-5859-39e0-bab1-1733ca83302e', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3958d7f5-30a0-3ae1-8efc-30e067fbfdda', 'fdc60e07-5859-39e0-bab1-1733ca83302e', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('f9a10d4a-4abd-31ed-823f-651686dc5bc6', 'fdc60e07-5859-39e0-bab1-1733ca83302e', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('eb085286-ad4f-3530-b388-d2a5ed92831d', 'fdc60e07-5859-39e0-bab1-1733ca83302e', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('558bf068-a646-3e4d-96f1-4f51d9105cd7', 'fdc60e07-5859-39e0-bab1-1733ca83302e', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('2e7cc76c-681f-3bdd-9e7a-b9efab51af44', 'fdc60e07-5859-39e0-bab1-1733ca83302e', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('b553f173-cbe3-3d60-b056-cb1f73685a2f', 'fdc60e07-5859-39e0-bab1-1733ca83302e', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('32fd3285-4cf5-300b-9591-e7cf9d019fb3', '4947c074-7646-3ca9-9ac7-53fd25282ef0', 90, 'Th', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('cb51fa84-3ab0-3b6b-9733-12cad384ea43', '32fd3285-4cf5-300b-9591-e7cf9d019fb3', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('8d0ff28d-498c-37d0-a1ce-65819b25c6dc', '32fd3285-4cf5-300b-9591-e7cf9d019fb3', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e0871258-bbe4-3bbc-be0e-ab08169bc618', '32fd3285-4cf5-300b-9591-e7cf9d019fb3', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('160ad629-ebc5-3c55-9941-66751fdd5bd4', '32fd3285-4cf5-300b-9591-e7cf9d019fb3', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('529f42ce-740c-317b-8b70-c1e119331e55', '32fd3285-4cf5-300b-9591-e7cf9d019fb3', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('b1ad0632-f0ad-3e86-a6c8-e82070b1d376', '32fd3285-4cf5-300b-9591-e7cf9d019fb3', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('dd599206-d08c-3faa-80b7-7add880c65d0', '32fd3285-4cf5-300b-9591-e7cf9d019fb3', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('fd14c03c-ce72-34a4-9793-1c894f203bef', '1d8405f4-f4bf-32a3-833a-ae05e120d007', 91, 'Pa', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c493569c-e886-3d11-8d16-5584ea94d1ce', 'fd14c03c-ce72-34a4-9793-1c894f203bef', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('61939ac8-a635-359b-9099-3a76c2e25360', 'fd14c03c-ce72-34a4-9793-1c894f203bef', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ddff6394-3f4c-3e2c-a568-e3bda01d862e', 'fd14c03c-ce72-34a4-9793-1c894f203bef', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('84f44fee-e0d5-365e-9b70-156fc5e7aa3b', 'fd14c03c-ce72-34a4-9793-1c894f203bef', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('de07c5a5-67a5-3d88-a492-e5e6a8db4c1e', 'fd14c03c-ce72-34a4-9793-1c894f203bef', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e249caa8-b131-3fa1-a04b-7d37fd08afe4', 'fd14c03c-ce72-34a4-9793-1c894f203bef', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('761cd964-4913-3316-bfbd-cc33ff4a767e', 'fd14c03c-ce72-34a4-9793-1c894f203bef', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 'a19fe2e8-404d-3435-8ba1-d8ca72d985ae', 92, 'U', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('6f6a54d9-d497-3989-8c22-dc8061f0aeb5', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('489caf77-4584-329d-ae8c-a348a398f82a', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 4, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('6f26fa8f-d8e1-3868-8b03-1e094d1a48a4', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 6, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('0fe6e945-e86b-37d0-b9ec-420a74c843bd', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('423f18a0-3b99-3de8-a176-83f6c6abaaea', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 4, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('1062f574-1435-3c85-85c1-637c4c3792b1', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 5, false, true, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('62adc668-8900-3848-ba64-c3786c8b2bbb', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 6, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('fded865f-eb1b-38f8-ae1b-ce8e1cef5977', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 1.38, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('b928ae43-61ac-3b97-9e88-fa9805171a3d', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 'EMPIRICAL_ATOMIC', 175, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('64dd2578-1561-304c-bb30-9e3edeeb53bb', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 'METALLIC', 156, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('7caf392d-d2a7-3c48-871d-8ec99644a198', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 'IONIC', 89, 4, 6, 'NOT_APPLICABLE', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('9ee9612b-2711-3e1b-855d-76d068d75b85', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 19100.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ee2141ef-9c2e-3361-a792-5dc70200c856', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 'MELTING', 1405.3, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('199aac46-e7c0-31c0-b94b-9605bc850dbc', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 'BOILING', 4404, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('58f57f25-14e2-31f4-b60e-83168be22ece', 'a71b38ea-29d7-320a-b8cf-b87a7ab43ca8', 'silvery-white', 'Silvery-white radioactive metallic solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('3a6178fc-01ed-3599-9ec6-f02bf8846616', 'fd706841-f288-35b3-bc80-1bde9d5bca65', 93, 'Np', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('b4151a6c-0308-3fdb-8d8f-5f3704b5475e', '3a6178fc-01ed-3599-9ec6-f02bf8846616', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('c0574878-bc3f-34f3-8f9c-ac2c531d0958', '3a6178fc-01ed-3599-9ec6-f02bf8846616', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('99528d2c-2b49-3bd1-8705-b1b92894c75e', '3a6178fc-01ed-3599-9ec6-f02bf8846616', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('9ce4d193-a412-3257-8ea6-6edd3f4d5e18', '3a6178fc-01ed-3599-9ec6-f02bf8846616', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('4d6095fb-921a-3efe-abec-e8b99da3e658', '3a6178fc-01ed-3599-9ec6-f02bf8846616', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e464bf3f-ed3b-3462-a797-40ae32c6e60f', '3a6178fc-01ed-3599-9ec6-f02bf8846616', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('59c9e595-9c56-380b-ab65-240c3d796401', '3a6178fc-01ed-3599-9ec6-f02bf8846616', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('343319dc-200e-38bb-8037-4cd681b3fdba', '048aca52-1531-3af1-81d3-1495b56bd6c3', 94, 'Pu', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('31cf7b10-0a2e-34d3-86b3-c71cd078fb57', '343319dc-200e-38bb-8037-4cd681b3fdba', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('815bc814-2d52-3164-a249-9f3f53e6d59e', '343319dc-200e-38bb-8037-4cd681b3fdba', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('17590363-2ec3-3794-adad-533d43e29cab', '343319dc-200e-38bb-8037-4cd681b3fdba', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('db883ebc-84cb-3948-b481-f3cf5249f875', '343319dc-200e-38bb-8037-4cd681b3fdba', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('2a899f91-de5e-39cc-a656-9ae77bd8e5a7', '343319dc-200e-38bb-8037-4cd681b3fdba', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d558f1ff-ae19-377f-8350-8010e774925c', '343319dc-200e-38bb-8037-4cd681b3fdba', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('5132c1ce-9cd5-3a91-8c7c-98e4502e25a2', '343319dc-200e-38bb-8037-4cd681b3fdba', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('4e394db7-6290-356d-b096-07ded9f2734b', '0bf4996c-630a-3aa4-92f6-66af4b0927bf', 95, 'Am', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a55ccffe-6c3f-3475-b480-a44353efb25f', '4e394db7-6290-356d-b096-07ded9f2734b', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('36704566-0e14-37ab-86e4-f39c4a0984bf', '4e394db7-6290-356d-b096-07ded9f2734b', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('26e01c4b-e539-3f1b-abea-de92d69db9c5', '4e394db7-6290-356d-b096-07ded9f2734b', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('3c5473d8-1242-3e19-bb69-3fc65a42c44a', '4e394db7-6290-356d-b096-07ded9f2734b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('8841305f-daf2-32db-95d5-6ff93d242e93', '4e394db7-6290-356d-b096-07ded9f2734b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e1728fcb-04bd-3197-a9da-7dfe349302f1', '4e394db7-6290-356d-b096-07ded9f2734b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('9c8a745b-0958-3e6a-9883-20dd3949ad0e', '4e394db7-6290-356d-b096-07ded9f2734b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('033fef82-be8d-3aef-95f7-4d6ebf48ad86', 'dc75f6f7-9f2d-3b7c-a11e-b5866a6f5065', 96, 'Cm', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('9457cc99-4d68-3f5e-98dd-338c16da59a8', '033fef82-be8d-3aef-95f7-4d6ebf48ad86', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('39893fe4-ecb0-3d7b-9dcb-2d8fd8d87d4d', '033fef82-be8d-3aef-95f7-4d6ebf48ad86', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_electronegativities (id, profile_id, value, scale, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('38b999e6-0d16-3b32-b780-61aa4e0adf4a', '033fef82-be8d-3aef-95f7-4d6ebf48ad86', 1.50, 'PAULING', false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('eafdb247-9a99-3ca7-a7e6-b079362e465a', '033fef82-be8d-3aef-95f7-4d6ebf48ad86', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('700762a6-cfb4-3445-94cc-b90a65bfb4fe', '033fef82-be8d-3aef-95f7-4d6ebf48ad86', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d1092652-810c-3fd1-8be6-b4087875fe94', '033fef82-be8d-3aef-95f7-4d6ebf48ad86', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('4ee73133-6f51-3149-8793-eb74aa7a59c0', '033fef82-be8d-3aef-95f7-4d6ebf48ad86', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('ad723335-eb8a-3e8e-a956-e2f0f4de7f99', '31bc2d98-ad5c-3cfa-9a51-4d43970631b4', 97, 'Bk', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('59818bbd-540d-3779-ac4f-9d67676320cd', 'ad723335-eb8a-3e8e-a956-e2f0f4de7f99', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('081fb465-313c-319c-9b9e-56023d1a4a6e', 'ad723335-eb8a-3e8e-a956-e2f0f4de7f99', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('49f9884a-21cb-32d1-b194-9f5776c3d066', 'ad723335-eb8a-3e8e-a956-e2f0f4de7f99', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('8a157841-eb6f-3072-b159-ba669c68d787', 'ad723335-eb8a-3e8e-a956-e2f0f4de7f99', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('f3ab04a2-761c-3a36-8ff7-7c92bcef1a52', 'ad723335-eb8a-3e8e-a956-e2f0f4de7f99', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('a2ddd4e9-bb46-3d88-8702-b9d357f872b6', 'ad723335-eb8a-3e8e-a956-e2f0f4de7f99', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('5ae46062-a5f1-3784-ad79-4658c8e7d786', 'fc3bda44-30f8-3220-b9ff-72f39cdd1f56', 98, 'Cf', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c8b46d6c-12f3-3a37-a2d5-269f792e079f', '5ae46062-a5f1-3784-ad79-4658c8e7d786', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a307ff8d-dcee-34a1-ac71-6c346d65eea5', '5ae46062-a5f1-3784-ad79-4658c8e7d786', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('833b4f13-e45e-31b5-bcab-0f0703c2a13c', '5ae46062-a5f1-3784-ad79-4658c8e7d786', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('d757a622-0eb1-3800-9cfa-d1fef381d400', '5ae46062-a5f1-3784-ad79-4658c8e7d786', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('db01dc28-4935-3452-82c7-6c4ce6ffdf36', '5ae46062-a5f1-3784-ad79-4658c8e7d786', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('959b5c8d-149e-3182-9d14-df3334df3ec2', '5ae46062-a5f1-3784-ad79-4658c8e7d786', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('7aabe4c9-509a-3a26-8488-fb4944b1bcb9', 'cef9ce30-caf6-3bd7-8eed-f353520f6229', 99, 'Es', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('55052df8-bca6-3909-b09d-6aa7328f19b7', '7aabe4c9-509a-3a26-8488-fb4944b1bcb9', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('2d9fce1a-9636-356d-839e-57a5a692e7cc', '7aabe4c9-509a-3a26-8488-fb4944b1bcb9', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('0fb63104-da0d-3caa-b8a7-b059596b4208', '7aabe4c9-509a-3a26-8488-fb4944b1bcb9', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('57c5d9a8-1fb7-3fdc-89cf-9c26a0f7eb1d', '7aabe4c9-509a-3a26-8488-fb4944b1bcb9', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('07984c2e-6ea5-3c4f-ac95-5365ce07d2bb', '7aabe4c9-509a-3a26-8488-fb4944b1bcb9', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('63f212d2-ac7e-3601-a83a-627f43606b78', '7aabe4c9-509a-3a26-8488-fb4944b1bcb9', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('35edb9b9-8596-3ccc-a082-3e94821b24e8', 'b3541e8f-5040-37b4-981f-bd884a76af10', 100, 'Fm', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('49b5e135-8d7d-3183-a22a-780e5df353a0', '35edb9b9-8596-3ccc-a082-3e94821b24e8', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('8a4e3aab-686c-3db4-8d7d-45f827b68986', '35edb9b9-8596-3ccc-a082-3e94821b24e8', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('cc77a225-205b-31d3-a6c2-e85444838d68', '35edb9b9-8596-3ccc-a082-3e94821b24e8', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('a620267d-0aec-3c54-8fb6-f46fe204e330', '35edb9b9-8596-3ccc-a082-3e94821b24e8', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('5698a3de-c55d-389d-b2c7-09b070df4653', '35edb9b9-8596-3ccc-a082-3e94821b24e8', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('51d6f549-3c8a-3498-a180-a1a06605e371', '35edb9b9-8596-3ccc-a082-3e94821b24e8', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('564b24e4-1461-37be-9600-2f84c84718c1', '2f3f9e65-0bb5-3598-bfc6-db1e5fb1ce37', 101, 'Md', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('bc8be400-e34c-3f9a-97bb-11b0385c7ec1', '564b24e4-1461-37be-9600-2f84c84718c1', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('059882c1-031f-339e-9788-3753f152c764', '564b24e4-1461-37be-9600-2f84c84718c1', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('7527ccca-c942-3c86-9370-c9e9e028b664', '564b24e4-1461-37be-9600-2f84c84718c1', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('3f5b678b-2f9f-31c5-ad3a-311e8854dcbe', '564b24e4-1461-37be-9600-2f84c84718c1', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('a0e89035-091b-3b62-97eb-977890cc1c20', '564b24e4-1461-37be-9600-2f84c84718c1', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('927cfcdd-77fa-34e9-85b2-55264380afd7', '564b24e4-1461-37be-9600-2f84c84718c1', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('277be334-3275-329d-abd0-1158af94f327', '35620d2b-42b2-31aa-a78f-f05b74b7b4f1', 102, 'No', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('e1d13d2d-1113-352f-900e-78742a77d1e8', '277be334-3275-329d-abd0-1158af94f327', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('2aaf7135-0fa2-3f91-beee-5f4c9329ffb3', '277be334-3275-329d-abd0-1158af94f327', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ea0a8abc-6b88-3e1d-997d-b62a4f989b77', '277be334-3275-329d-abd0-1158af94f327', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('1c849db7-841c-31e6-a2bd-ea3fdd87ce3c', '277be334-3275-329d-abd0-1158af94f327', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('f0964122-0b8f-3fac-8b01-98e4eea48dde', '277be334-3275-329d-abd0-1158af94f327', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('aa12445e-77df-36e2-b4df-7addbba0ecc0', '277be334-3275-329d-abd0-1158af94f327', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('becddc25-b2b4-36b5-ad05-5c32cf76d3e0', 'd13d81a0-9587-3ae0-ba12-c541437c1d00', 103, 'Lr', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4b1957ab-413a-35d6-8488-982605e406fe', 'becddc25-b2b4-36b5-ad05-5c32cf76d3e0', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('ea6482eb-aeb9-333c-9594-ffa9a15aab3d', 'becddc25-b2b4-36b5-ad05-5c32cf76d3e0', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('9b9d8707-46d9-3529-af97-b71cd43440c0', 'becddc25-b2b4-36b5-ad05-5c32cf76d3e0', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('969e726f-0d14-32e5-9106-f07e1f659fa9', 'becddc25-b2b4-36b5-ad05-5c32cf76d3e0', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('e2a530c7-4c34-3f7f-a0db-6ddc9ab3365f', 'becddc25-b2b4-36b5-ad05-5c32cf76d3e0', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('86058bd7-bd3a-3a86-8dc5-d29115f0710d', 'becddc25-b2b4-36b5-ad05-5c32cf76d3e0', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('e3958ea5-7272-359c-a909-48dd999de202', 'f34b9491-1e21-3b65-9962-326dcca8c83a', 104, 'Rf', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('e11c737c-3d6c-3337-9884-b705eb92addc', 'e3958ea5-7272-359c-a909-48dd999de202', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e9e20043-4959-3394-bc0d-ad48879e7855', 'e3958ea5-7272-359c-a909-48dd999de202', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('399e3569-dc88-38df-bd0e-5040a08a9f02', 'e3958ea5-7272-359c-a909-48dd999de202', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('6bc69cd5-9f91-36ae-904e-dad63cb49e16', 'e3958ea5-7272-359c-a909-48dd999de202', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('5e8c2909-f1c1-31d2-8ae5-f620375856f1', 'e3958ea5-7272-359c-a909-48dd999de202', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('a4657f69-f590-3331-b741-78c3df94e899', 'e3958ea5-7272-359c-a909-48dd999de202', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('fdd73fc0-be04-3502-8e1d-18e29d5982f1', 'f9419a79-adb9-3e99-aafb-cd194dfad984', 105, 'Db', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('5d1e5061-1929-3500-af8d-ce0eab5a4cdf', 'fdd73fc0-be04-3502-8e1d-18e29d5982f1', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('7373e077-e88e-36e4-aa97-9c8d5ccdbb5e', 'fdd73fc0-be04-3502-8e1d-18e29d5982f1', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('36fc646f-1ce0-3c05-989e-ec94812390b9', 'fdd73fc0-be04-3502-8e1d-18e29d5982f1', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('1d1f1021-a6c4-3541-b3d4-3e256f05becf', 'fdd73fc0-be04-3502-8e1d-18e29d5982f1', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('6abfa7e9-ad0a-369d-b503-c320c1c16572', 'fdd73fc0-be04-3502-8e1d-18e29d5982f1', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('a21421e4-ac6d-3c4b-861f-551eb6e5ef51', 'fdd73fc0-be04-3502-8e1d-18e29d5982f1', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('f71be506-6051-3224-a623-dbede641da48', 'b92c6032-7cd0-38c0-9932-cdb917d8ba75', 106, 'Sg', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('0d555065-25cf-3037-bf69-ee4054c0106c', 'f71be506-6051-3224-a623-dbede641da48', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('67512ea6-9454-3960-bb6d-37e64e57bf9e', 'f71be506-6051-3224-a623-dbede641da48', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('78d91efb-6261-32aa-a0db-27ce922bc46d', 'f71be506-6051-3224-a623-dbede641da48', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('a460ac76-5c36-3d04-801f-fc565834532c', 'f71be506-6051-3224-a623-dbede641da48', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('a5abf2ba-e106-360f-9420-9928a63b1673', 'f71be506-6051-3224-a623-dbede641da48', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('37dba22c-68a4-3210-ba27-37e203fbe850', 'f71be506-6051-3224-a623-dbede641da48', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('0fed3c82-fc65-355c-8bb0-9a4e7af6dbea', '7e61be35-e175-3771-9a46-006df6d45316', 107, 'Bh', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('1fa8beba-a876-3582-923b-68a249036ce4', '0fed3c82-fc65-355c-8bb0-9a4e7af6dbea', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('9e01de8c-2d2b-3bdf-82d6-76bbd0f9fb8c', '0fed3c82-fc65-355c-8bb0-9a4e7af6dbea', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('4a48be22-cd07-3702-98c0-c3b927ed34a8', '0fed3c82-fc65-355c-8bb0-9a4e7af6dbea', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('76b371f6-4031-3f88-9a46-becc21566e44', '0fed3c82-fc65-355c-8bb0-9a4e7af6dbea', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('bd0c5d4e-db26-337d-8ad0-a7e485fb0d54', '0fed3c82-fc65-355c-8bb0-9a4e7af6dbea', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('39baffeb-5817-345b-9f82-bf84370e1920', '0fed3c82-fc65-355c-8bb0-9a4e7af6dbea', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('df565cd5-2ef2-3ef4-bba8-e7cbe1151ab4', 'dafa432f-9694-3c5b-b564-7dc8cdbda634', 108, 'Hs', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('d73f85e3-e970-3bd0-8f00-a155787edb05', 'df565cd5-2ef2-3ef4-bba8-e7cbe1151ab4', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('0f29807a-d91d-378c-960f-9147714e4bbf', 'df565cd5-2ef2-3ef4-bba8-e7cbe1151ab4', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('c614bbd0-c595-3e8f-9c1d-b0395e469318', 'df565cd5-2ef2-3ef4-bba8-e7cbe1151ab4', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('f0faf039-1792-3aba-8f3c-b05de4c81ebd', 'df565cd5-2ef2-3ef4-bba8-e7cbe1151ab4', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('b1bc6001-0845-3eca-ac8c-506f1e6278cd', 'df565cd5-2ef2-3ef4-bba8-e7cbe1151ab4', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('159cd6d6-9c32-32d4-9b70-fcdb84aab5cd', 'df565cd5-2ef2-3ef4-bba8-e7cbe1151ab4', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('8c8bd2f2-8348-33f4-ad12-90636547f496', 'd8812857-6525-3042-816d-fc8ed6656ba6', 109, 'Mt', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('57555ad2-03a2-3310-8ce6-f60220651f57', '8c8bd2f2-8348-33f4-ad12-90636547f496', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('56c2f4f5-2560-305d-982e-28515385295c', '8c8bd2f2-8348-33f4-ad12-90636547f496', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('dd1beb89-669d-328a-953b-3a274bb93ee5', '8c8bd2f2-8348-33f4-ad12-90636547f496', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('be0e734f-3bf2-3564-a6fc-0f1ff2fdba46', '8c8bd2f2-8348-33f4-ad12-90636547f496', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ce861db5-0f48-3f85-9056-a3b3735a2a38', '8c8bd2f2-8348-33f4-ad12-90636547f496', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('ca13d21d-fff6-3b97-84ca-a678a7c38df6', '8c8bd2f2-8348-33f4-ad12-90636547f496', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('05c1f165-ab71-3931-8de5-ee2f96c126be', '82a28008-d88c-3ae9-959a-4f5432b0e167', 110, 'Ds', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('4fe296ca-2e6d-3358-982b-49c92515f0fd', '05c1f165-ab71-3931-8de5-ee2f96c126be', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('76727d29-71e0-37a2-ae4b-c36ac80f1963', '05c1f165-ab71-3931-8de5-ee2f96c126be', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('6e7c4848-e957-3708-a350-bc49fc0f2ed3', '05c1f165-ab71-3931-8de5-ee2f96c126be', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('2fd1e180-c983-3109-ad67-fb7b6e5a0f3f', '05c1f165-ab71-3931-8de5-ee2f96c126be', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('95ddfe29-7116-346e-8e83-df3fbee5dbec', '05c1f165-ab71-3931-8de5-ee2f96c126be', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('96d1751b-ee11-38ed-af05-45b7cf7f384a', '05c1f165-ab71-3931-8de5-ee2f96c126be', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('746e62c4-3542-3147-9b87-6be856d431e3', '1260602a-9efd-3580-ad24-eb042f8b7dc8', 111, 'Rg', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('6a1c5493-f81b-3303-9f4d-f5664142b785', '746e62c4-3542-3147-9b87-6be856d431e3', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('a1b84eef-c13e-398c-ad60-2de81ce58b54', '746e62c4-3542-3147-9b87-6be856d431e3', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('8c412b94-8fc4-30fa-a764-5468466a6747', '746e62c4-3542-3147-9b87-6be856d431e3', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('f5dbdd0d-8dd6-3278-ad6f-3a1660b0fe08', '746e62c4-3542-3147-9b87-6be856d431e3', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('148bee32-950a-351a-9d7a-857e03e5c27d', '746e62c4-3542-3147-9b87-6be856d431e3', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('8c84dabc-cc17-36ff-96de-35d315674253', '746e62c4-3542-3147-9b87-6be856d431e3', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('ba68b336-5e2c-3a25-bb95-f2b1418f0e49', 'cd55dd73-e814-3033-b892-2251e486bf62', 112, 'Cn', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('a286bea9-8db8-30b8-a00c-d6bb733da96e', 'ba68b336-5e2c-3a25-bb95-f2b1418f0e49', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('fa045701-46eb-3c26-9797-f04d0156bb3c', 'ba68b336-5e2c-3a25-bb95-f2b1418f0e49', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('44ff44da-0843-3728-af24-d52bfb827073', 'ba68b336-5e2c-3a25-bb95-f2b1418f0e49', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('34e96587-30c6-3a19-b5b3-390cdc6dc919', 'ba68b336-5e2c-3a25-bb95-f2b1418f0e49', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d8af7fb7-b795-3c06-9622-078557a96ae7', 'ba68b336-5e2c-3a25-bb95-f2b1418f0e49', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('6bc558ff-da42-3acc-9fe7-fd00fac21933', 'ba68b336-5e2c-3a25-bb95-f2b1418f0e49', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('db2c32e9-c3da-3644-8f7f-0fb88fc0b079', '445165d9-e5d7-361d-88dc-0c64a9c0209f', 113, 'Nh', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('c2c7e3e6-e4fa-359d-b783-83b6eddddb20', 'db2c32e9-c3da-3644-8f7f-0fb88fc0b079', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('20ed7e28-8ab2-3316-a6c1-4d12f82e0398', 'db2c32e9-c3da-3644-8f7f-0fb88fc0b079', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('b5745c02-4b8b-391e-866d-be88fb7b9689', 'db2c32e9-c3da-3644-8f7f-0fb88fc0b079', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('50b51c95-fbc3-3c3a-a349-afe2cfa13e13', 'db2c32e9-c3da-3644-8f7f-0fb88fc0b079', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('97a5eef9-b780-3891-af15-5616fcb969b0', 'db2c32e9-c3da-3644-8f7f-0fb88fc0b079', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('91b47dbb-1178-385d-a37d-230d25b5cf77', 'db2c32e9-c3da-3644-8f7f-0fb88fc0b079', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('2a01df6b-fb0d-3aa5-b0b9-39aae24eeb73', 'fdbe94df-c2b7-396e-a587-2ecd9ba09b59', 114, 'Fl', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('da859455-5efd-3753-9bd3-005d1a74e79b', '2a01df6b-fb0d-3aa5-b0b9-39aae24eeb73', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('2fc83eca-2874-3ace-b104-b6834b37bf9a', '2a01df6b-fb0d-3aa5-b0b9-39aae24eeb73', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('ddac22f3-214c-33b1-8afa-0890e47f38aa', '2a01df6b-fb0d-3aa5-b0b9-39aae24eeb73', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('996ba069-8127-388c-b62c-b007474db383', '2a01df6b-fb0d-3aa5-b0b9-39aae24eeb73', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ba0c9539-06c5-341b-9a99-c0fdc2d65568', '2a01df6b-fb0d-3aa5-b0b9-39aae24eeb73', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('2d5dd6ed-b727-3aa7-8123-52be63a61e77', '2a01df6b-fb0d-3aa5-b0b9-39aae24eeb73', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('0eaa7292-6131-32d4-9d74-eaca9ecd564d', '3bbb383e-f959-3dcb-95d8-3beb24e93894', 115, 'Mc', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('65303672-8d82-3c88-8e49-41f90816dcc2', '0eaa7292-6131-32d4-9d74-eaca9ecd564d', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e7d43a7e-5db8-3e19-83a5-7a6060a33b08', '0eaa7292-6131-32d4-9d74-eaca9ecd564d', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('a11ac0f1-f574-376c-b8fb-3ee9c1739df8', '0eaa7292-6131-32d4-9d74-eaca9ecd564d', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('4d7c4f40-3f8d-36a9-a32d-b6d8a6c8daec', '0eaa7292-6131-32d4-9d74-eaca9ecd564d', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('ae3c575e-d97e-3710-936a-2ddf9f4e1617', '0eaa7292-6131-32d4-9d74-eaca9ecd564d', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('ad941c26-0304-342e-80f6-c4a4bc97c70e', '0eaa7292-6131-32d4-9d74-eaca9ecd564d', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('c7fdd9ae-4232-3176-9470-9d544ee0c96b', 'ae9f8410-d699-35f2-a0dc-302681989497', 116, 'Lv', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('92f06af4-1714-3a43-9e6b-40e567e0f5e2', 'c7fdd9ae-4232-3176-9470-9d544ee0c96b', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('3e1170a4-fb7e-3c12-996c-5a4a66ab087a', 'c7fdd9ae-4232-3176-9470-9d544ee0c96b', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('582ad475-8346-3db0-8544-796087884f74', 'c7fdd9ae-4232-3176-9470-9d544ee0c96b', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('310ed1cd-88a5-3bd7-85c4-24995a78e3bf', 'c7fdd9ae-4232-3176-9470-9d544ee0c96b', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('5b4c29be-531c-3783-9a1e-012710bac4ae', 'c7fdd9ae-4232-3176-9470-9d544ee0c96b', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('7146964f-b859-3ae0-bbac-04a0ad6282ed', 'c7fdd9ae-4232-3176-9470-9d544ee0c96b', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('cff21143-391f-3cc1-9d6d-6673361ec09a', '3b21b7e7-5ff2-39ea-a222-f00928457fa8', 117, 'Ts', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('2fe09520-e5e7-3b74-8f52-607f41f08d1e', 'cff21143-391f-3cc1-9d6d-6673361ec09a', 3, true, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('b365156b-b0d0-342c-a496-c52e21ab12be', 'cff21143-391f-3cc1-9d6d-6673361ec09a', 3, true, false, false, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('8b76f145-c356-3626-a2e6-49fe238d9a81', 'cff21143-391f-3cc1-9d6d-6673361ec09a', 'EMPIRICAL_ATOMIC', 120, NULL, NULL, NULL, 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('08d68c5e-37f0-3c7e-a7b2-824832722ac5', 'cff21143-391f-3cc1-9d6d-6673361ec09a', 5000.0, 298.15, 100, 'SOLID', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('847742af-5b92-32f9-8b82-9a59f304bf0f', 'cff21143-391f-3cc1-9d6d-6673361ec09a', 'MELTING', 1000, 100, 'NORMAL_TRANSITION', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('0d95441a-16cd-3acf-be70-068fb5340aa5', 'cff21143-391f-3cc1-9d6d-6673361ec09a', 'metallic solid', 'Standard physical element solid', 'EVALUATED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

INSERT INTO chemistry.element_property_profiles (id, element_id, atomic_number, symbol, dataset_version_id)
VALUES ('ca2e2e1f-8ebf-3966-badc-f68d536443dd', '36c21ab2-4bbd-3585-b044-493513db16c4', 118, 'Og', 'extended-properties-v1.0.0')
ON CONFLICT (element_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.element_valencies (id, profile_id, valency, is_common, evidence_status, source_identifier, source_title)
VALUES ('6d50cd29-b522-39b3-aa83-1636141dce51', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 0, true, 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('76e80afa-31a3-343d-80d7-399523dbf423', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 0, true, false, true, 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('9f13c8e4-d663-3f28-83a6-df5f3e1ee153', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 2, false, true, true, 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_oxidation_states (id, profile_id, state, is_common, is_uncommon, is_predicted, evidence_status, source_identifier, source_title)
VALUES ('e5767fae-f9c6-3e6e-a391-873424fb4e9c', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 4, false, true, true, 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_radii (id, profile_id, kind, radius_pm, ionic_charge, coordination_number, spin_state, evidence_status, source_identifier, source_title)
VALUES ('66c4d0e9-67bc-3db3-a1fb-2ea1b46d732e', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 'CALCULATED_ATOMIC', 152, NULL, NULL, NULL, 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_density_data (id, profile_id, density_kg_m3, ref_temp_k, ref_pressure_kpa, ref_state, evidence_status, source_identifier, source_title)
VALUES ('6113b0e6-74b9-3ecc-8026-fa71a0840fd7', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 4900.0, 298.15, 100, 'SOLID', 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('d74d3f59-8f85-3468-ac19-73d8211790d0', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 'MELTING', 325, 100, 'PREDICTED', 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_phase_transitions (id, profile_id, kind, temp_k, ref_pressure_kpa, behavior, evidence_status, source_identifier, source_title)
VALUES ('6fd2fc61-9956-32c5-b652-edb8711b8b91', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 'BOILING', 350, 100, 'PREDICTED', 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.element_appearance (id, profile_id, normalized_color_name, appearance_description, evidence_status, source_identifier, source_title)
VALUES ('ecd811d0-d5e7-36e7-a318-b0cc904738df', 'ca2e2e1f-8ebf-3966-badc-f68d536443dd', 'unknown', 'Synthetic radioactive superheavy element', 'PREDICTED', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition') ON CONFLICT (profile_id) DO NOTHING;

