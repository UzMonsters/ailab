-- V8: Seed core educational compound catalogue
-- Dataset Version: compound-core-v1.0.0

INSERT INTO chemistry.compound_catalog_versions (id, name, publication_date)
VALUES ('compound-core-v1.0.0', 'Core Educational Compound Catalogue', '2026-08-04')
ON CONFLICT (id) DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('650b152a-3a54-334b-9006-627007c122b0', 'COMP-H2', 'Hydrogen gas', 'H2', 'H2', 0, NULL, 2.016, 2.01568, 2.01622, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('89110f06-f1c1-3658-bdf9-aedd5264f5b7', '650b152a-3a54-334b-9006-627007c122b0', 'Dihydrogen', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('90078c79-1e77-3f96-8d3e-9178876fa63b', '650b152a-3a54-334b-9006-627007c122b0', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('4da795f0-8c24-30c2-b446-995b70f1c18d', '650b152a-3a54-334b-9006-627007c122b0', 'CAS_REGISTRY_NUMBER', '1333-74-0') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('e5b2a360-4dcb-3b1b-9f9e-f9e561a6c3aa', '650b152a-3a54-334b-9006-627007c122b0', 'PUBCHEM_CID', '783') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('6c11ca8c-0546-3a30-8558-92fb83efacb6', 'COMP-O2', 'Oxygen gas', 'O2', 'O2', 0, NULL, 31.998, 31.98982, 31.99954, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('5f41a10a-2bea-30e1-8b37-5137ad8bc097', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'Dioxygen', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('a909ee0b-b2f8-3735-bde4-9958c1ef3dd9', '6c11ca8c-0546-3a30-8558-92fb83efacb6', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('74598df8-5ecf-3907-beb2-ab6741e89d66', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'CAS_REGISTRY_NUMBER', '7782-44-7') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('c67398d4-a27f-3d43-b55e-3b2d6e07afce', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'PUBCHEM_CID', '977') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('9ee0626a-3425-3597-a497-5cbf32c8570f', 'COMP-N2', 'Nitrogen gas', 'N2', 'N2', 0, NULL, 28.014, 28.01286, 28.01456, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('9fb047c5-7adf-3c2f-9215-c0eaf7444834', '9ee0626a-3425-3597-a497-5cbf32c8570f', 'Dinitrogen', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('5e9e8557-d46b-32c2-93c6-7b589672af97', '9ee0626a-3425-3597-a497-5cbf32c8570f', '87e4c172-8582-34c0-bdf6-dd0d326b9b93', 7, 'N', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('aa444590-d6a7-3e62-b9ee-cd9f9a9fb074', '9ee0626a-3425-3597-a497-5cbf32c8570f', 'CAS_REGISTRY_NUMBER', '7727-37-9') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('ea2364b4-91db-30e6-a453-33642ec16fbb', '9ee0626a-3425-3597-a497-5cbf32c8570f', 'PUBCHEM_CID', '947') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('ef4b615a-9ecf-3a89-981d-b1f8b8fd72da', 'COMP-F2', 'Fluorine gas', 'F2', 'F2', 0, NULL, 37.996, NULL, NULL, 'EXACT_FROM_FIXED_VALUES', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('c5ee5e32-cad4-3f47-812c-d4c3d178c7fd', 'ef4b615a-9ecf-3a89-981d-b1f8b8fd72da', 'Difluorine', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('71841f6f-54f0-3190-9d85-67f16aa68b39', 'ef4b615a-9ecf-3a89-981d-b1f8b8fd72da', '7e240ab5-d96b-33cd-b915-320767f2fdac', 9, 'F', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('61da0ad4-eb7a-38fd-8553-466349f7fd56', 'ef4b615a-9ecf-3a89-981d-b1f8b8fd72da', 'CAS_REGISTRY_NUMBER', '7782-41-4') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('31ac5183-783b-3ffd-bb92-7b247924a42f', 'COMP-CL2', 'Chlorine gas', 'Cl2', 'Cl2', 0, NULL, 70.9, 70.892, 70.914, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('6e2abe75-a618-3994-b685-a7bf981c21d9', '31ac5183-783b-3ffd-bb92-7b247924a42f', 'Dichlorine', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('2b733988-8739-3b59-8dc5-347ef01bc2c6', '31ac5183-783b-3ffd-bb92-7b247924a42f', 'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('43c9cef6-badd-3e8a-a820-446b51467981', '31ac5183-783b-3ffd-bb92-7b247924a42f', 'CAS_REGISTRY_NUMBER', '7782-50-5') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('384ac66c-9749-3b89-8257-d665e1318c93', 'COMP-BR2', 'Bromine liquid', 'Br2', 'Br2', 0, NULL, 159.808, 159.802, 159.814, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('df63f7a1-0588-3e70-9add-1ab42ef342d1', '384ac66c-9749-3b89-8257-d665e1318c93', 'Dibromine', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d177ec01-4893-3d05-9d81-65d3caea78fa', '384ac66c-9749-3b89-8257-d665e1318c93', 'fb26ef36-5d23-3496-b74d-1dea5eec6250', 35, 'Br', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('b6fbdb80-35c5-3bf0-ae40-fe60daadbfa6', '384ac66c-9749-3b89-8257-d665e1318c93', 'CAS_REGISTRY_NUMBER', '7726-95-6') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('6f1a0282-9bfa-350b-bcf4-1924eb58261c', 'COMP-I2', 'Iodine solid', 'I2', 'I2', 0, NULL, 253.8, NULL, NULL, 'EXACT_FROM_FIXED_VALUES', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('768e84cf-4d18-342b-b28a-c2b2fc8abe10', '6f1a0282-9bfa-350b-bcf4-1924eb58261c', 'Diiodine', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('ccd6cf02-e2c8-32d7-b9e8-4d14ca4359d2', '6f1a0282-9bfa-350b-bcf4-1924eb58261c', '0b9284eb-fe2c-38bc-90e1-4fb95e46cd82', 53, 'I', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('fd691da6-492a-3cba-9d62-d3c426b8a053', '6f1a0282-9bfa-350b-bcf4-1924eb58261c', 'CAS_REGISTRY_NUMBER', '7553-56-2') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('f38e4f83-fe95-3243-887e-f448d74ef717', 'COMP-H2O', 'Water', 'H2O', 'H2O', 0, NULL, 18.015, 18.01059, 18.01599, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('be1d864c-109b-38d8-a75c-89625f452604', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'Oxidane', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('bbd224ec-7354-36c9-af5a-5f3d505159c9', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'Dihydrogen monoxide', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('0dba0a61-aef9-375e-aa32-1eb6761862f6', 'f38e4f83-fe95-3243-887e-f448d74ef717', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('85e0efa2-cbb6-383f-9107-ca1638fc97eb', 'f38e4f83-fe95-3243-887e-f448d74ef717', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('dc77d2f8-1122-3283-9d0c-1969abe277bb', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'CAS_REGISTRY_NUMBER', '7732-18-5') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('d85a15b8-4da9-3b7b-88d1-317e86999337', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'PUBCHEM_CID', '962') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('25aa9b6b-0e15-3c35-905d-9c056f332a8a', 'COMP-H2O2', 'Hydrogen peroxide', 'H2O2', 'H2O2', 0, NULL, 34.014, 34.0055, 34.01576, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('9690aecb-55ff-37f3-bf4f-dc2f864fa9a3', '25aa9b6b-0e15-3c35-905d-9c056f332a8a', 'Dihydrogen dioxide', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('17268fd3-ca7f-3996-92a3-081c09c43442', '25aa9b6b-0e15-3c35-905d-9c056f332a8a', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d968120d-e1e8-330e-8114-3746ca7c88b7', '25aa9b6b-0e15-3c35-905d-9c056f332a8a', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('4b0bf11f-8ad6-3d46-9087-2a0bc25aaa44', '25aa9b6b-0e15-3c35-905d-9c056f332a8a', 'CAS_REGISTRY_NUMBER', '7722-84-1') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'COMP-CO2', 'Carbon dioxide', 'CO2', 'CO2', 0, NULL, 44.009, 43.99942, 44.01114, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('750a6a67-80ad-3406-9b4d-72860b87372b', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'Carbonic acid gas', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('6d760500-9256-3b15-9b6f-a123183cdd06', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('01f4ac18-2fd4-3e6b-aa72-eb360cd60a79', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('1c73045a-de2c-343d-a8c1-8be9b8e3b202', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'CAS_REGISTRY_NUMBER', '124-38-9') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('3391c6a1-6aa8-3913-9abe-0dfe070e06fe', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'PUBCHEM_CID', '280') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('44d58472-c681-3f7e-b989-9b8730603a2b', 'COMP-CO', 'Carbon monoxide', 'CO', 'CO', 0, NULL, 28.01, 28.00451, 28.01137, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('bc9bc8e7-adf0-38db-9aed-4c59e1a5d8cb', '44d58472-c681-3f7e-b989-9b8730603a2b', 'Carbon oxide', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('0e30d25d-03ec-371f-9d1b-6b20d8886681', '44d58472-c681-3f7e-b989-9b8730603a2b', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d6483397-b106-3efa-9db7-aecfd6e12b7f', '44d58472-c681-3f7e-b989-9b8730603a2b', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('0eb5f224-1b08-31b5-aece-09e066a7149c', '44d58472-c681-3f7e-b989-9b8730603a2b', 'CAS_REGISTRY_NUMBER', '630-08-0') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('665185a5-e410-38fa-8e02-d4a2be56e2c7', 'COMP-NH3', 'Ammonia', 'NH3', 'NH3', 0, NULL, 17.031, 17.02995, 17.03161, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('790990fa-521b-3c65-9d58-515deb65cc19', '665185a5-e410-38fa-8e02-d4a2be56e2c7', 'Azane', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('59718a3b-39e0-3771-80b3-1403faa647a0', '665185a5-e410-38fa-8e02-d4a2be56e2c7', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('25741142-7ef9-3151-9d1e-4b632e5d07d6', '665185a5-e410-38fa-8e02-d4a2be56e2c7', '87e4c172-8582-34c0-bdf6-dd0d326b9b93', 7, 'N', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('96bdd85d-5d59-3398-a077-f2de6ba6df72', '665185a5-e410-38fa-8e02-d4a2be56e2c7', 'CAS_REGISTRY_NUMBER', '7664-41-7') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('84a8c249-1a4f-34d9-97cb-5a8b75eb1df1', '665185a5-e410-38fa-8e02-d4a2be56e2c7', 'PUBCHEM_CID', '222') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('095d8580-8beb-3e71-800d-add10b6590ae', 'COMP-CH4', 'Methane', 'CH4', 'CH4', 0, NULL, 16.043, 16.04096, 16.04404, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('6622a3ab-1956-3199-ad53-8e46218c2a5b', '095d8580-8beb-3e71-800d-add10b6590ae', 'Marsh gas', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('bbbf5373-9bc5-387a-9c9d-3b2b8cd96b45', '095d8580-8beb-3e71-800d-add10b6590ae', 'Carbane', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('91d93a9e-f3f9-3f6f-9dc6-304e9f5bc4d7', '095d8580-8beb-3e71-800d-add10b6590ae', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('65b751f1-3b92-3837-9478-02f3cc677f92', '095d8580-8beb-3e71-800d-add10b6590ae', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('53605559-baad-3af5-89cc-6ca4274d269e', '095d8580-8beb-3e71-800d-add10b6590ae', 'CAS_REGISTRY_NUMBER', '74-82-8') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('f4e7dea9-eb14-35d5-ab58-2e453f34a8bb', '095d8580-8beb-3e71-800d-add10b6590ae', 'PUBCHEM_CID', '297') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('41536b4f-237a-38ae-9cf9-b0099a36b773', 'COMP-C2H6', 'Ethane', 'C2H6', 'C2H6', 0, NULL, 30.07, 30.06624, 30.07186, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('44aabe74-4c3e-3f93-b481-a752beba5890', '41536b4f-237a-38ae-9cf9-b0099a36b773', 'Bicarbon', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('0c72d585-b552-3c7b-ad33-24f08f62882a', '41536b4f-237a-38ae-9cf9-b0099a36b773', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 6) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('85cb33e2-3ae2-3299-9931-349189576798', '41536b4f-237a-38ae-9cf9-b0099a36b773', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('e360a0d1-c868-369c-bdfa-2ff2cbb04847', '41536b4f-237a-38ae-9cf9-b0099a36b773', 'CAS_REGISTRY_NUMBER', '74-84-0') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('dcaa9932-0ee6-37a4-be71-fb00655d1a14', 'COMP-C3H8', 'Propane', 'C3H8', 'C3H8', 0, NULL, 44.097, 44.09152, 44.09968, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('093e2279-8527-3322-8241-e9210809d09a', 'dcaa9932-0ee6-37a4-be71-fb00655d1a14', 'Dimethylmethane', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('51d91e61-ce74-3ac0-8da7-eddf78d8c7d9', 'dcaa9932-0ee6-37a4-be71-fb00655d1a14', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 8) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('e8529e58-7d6c-34fc-8ec1-1b74e027a333', 'dcaa9932-0ee6-37a4-be71-fb00655d1a14', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('0fc0434f-6e30-341b-8262-99ccbbde535c', 'dcaa9932-0ee6-37a4-be71-fb00655d1a14', 'CAS_REGISTRY_NUMBER', '74-98-6') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('7589a123-6728-3310-b5f0-87d0d514cac5', 'COMP-C4H10', 'Butane', 'C4H10', 'C4H10', 0, NULL, 58.124, 58.1168, 58.1275, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('7e13bb94-3103-31b5-aea0-25bd89f42208', '7589a123-6728-3310-b5f0-87d0d514cac5', 'n-Butane', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('21eec9a1-66f6-3fb8-925c-e247eb5a299f', '7589a123-6728-3310-b5f0-87d0d514cac5', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 10) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('39d5a0bb-64ca-3b4d-a908-67314094ce28', '7589a123-6728-3310-b5f0-87d0d514cac5', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('7b174943-3dd2-3ff3-9f18-5d1e2e68d13a', '7589a123-6728-3310-b5f0-87d0d514cac5', 'CAS_REGISTRY_NUMBER', '106-97-8') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('6b7fc3b3-25c0-3512-bd61-65bf96e79f67', 'COMP-C2H4', 'Ethylene', 'C2H4', 'C2H4', 0, NULL, 28.054, 28.05056, 28.05564, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('c9c7f499-f75a-3e96-83b7-a8151b406656', '6b7fc3b3-25c0-3512-bd61-65bf96e79f67', 'Ethene', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('100eafbf-2019-391b-8e22-f902b99aca05', '6b7fc3b3-25c0-3512-bd61-65bf96e79f67', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('bff4d823-9136-3f68-8331-b0bf1cd94c69', '6b7fc3b3-25c0-3512-bd61-65bf96e79f67', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('a13c7d1f-0160-34aa-9447-3ca4be2bf6a8', '6b7fc3b3-25c0-3512-bd61-65bf96e79f67', 'CAS_REGISTRY_NUMBER', '74-85-1') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('4d0d1768-0572-30f2-a1fb-6bc37b29090f', 'COMP-C2H2', 'Acetylene', 'C2H2', 'C2H2', 0, NULL, 26.038, 26.03488, 26.03942, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('0c32da06-f9d2-31bd-b1fc-e425bc365633', '4d0d1768-0572-30f2-a1fb-6bc37b29090f', 'Ethyne', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('99261b15-98b8-34fa-8b69-8d703899214e', '4d0d1768-0572-30f2-a1fb-6bc37b29090f', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('524cfe0f-0c72-38ad-a578-53b76c55e8a5', '4d0d1768-0572-30f2-a1fb-6bc37b29090f', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('f037993b-12f0-399b-ab56-f2bfdf8e1811', '4d0d1768-0572-30f2-a1fb-6bc37b29090f', 'CAS_REGISTRY_NUMBER', '74-86-2') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('fb70900a-3666-3cff-ad6d-4d827638a1b7', 'COMP-C6H6', 'Benzene', 'C6H6', 'C6H6', 0, NULL, 78.114, 78.10464, 78.11826, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('ac44dc5c-3911-3626-9332-ce898498e56d', 'fb70900a-3666-3cff-ad6d-4d827638a1b7', '[6]Annulene', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('913ee413-b107-329d-93c7-fc7210f8e87e', 'fb70900a-3666-3cff-ad6d-4d827638a1b7', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 6) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d4397ca0-8e95-3bec-ba21-98f6d5010f1c', 'fb70900a-3666-3cff-ad6d-4d827638a1b7', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 6) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('f33740ca-af39-39d5-b6f3-cb88ce096b8f', 'fb70900a-3666-3cff-ad6d-4d827638a1b7', 'CAS_REGISTRY_NUMBER', '71-43-2') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'COMP-HCL', 'Hydrochloric acid', 'HCl', 'HCl', 0, NULL, 36.458, 36.45384, 36.46511, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('7aa5db76-1a82-3ebc-a275-c7fb70e96281', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'Chlorane', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('8d9b7f8e-ba2e-360c-a56b-5cd55f3eecec', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'Muriatic acid', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('a4a66add-6aa0-3a52-8fed-08fe4d8a642f', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('80ba3801-919a-3066-937c-38aa709339c2', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('c15e6b13-a74a-3549-8471-a9365af699bb', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'CAS_REGISTRY_NUMBER', '7647-01-0') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('e08d3f76-f638-368f-b699-ebb68ccad2cc', 'COMP-HNO3', 'Nitric acid', 'HNO3', 'HNO3', 0, NULL, 63.012, 62.999, 63.0147, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('77d4ecb9-1c56-3a7f-9f9d-dc3eda15c993', 'e08d3f76-f638-368f-b699-ebb68ccad2cc', 'Aqua fortis', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('e9b8ddea-7103-3d62-ae1a-cc0521c9392f', 'e08d3f76-f638-368f-b699-ebb68ccad2cc', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('22946519-0231-3ea3-8fc9-bd793bbc4c80', 'e08d3f76-f638-368f-b699-ebb68ccad2cc', '87e4c172-8582-34c0-bdf6-dd0d326b9b93', 7, 'N', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('361ddfeb-4bca-3c7c-809e-25f21dbc67f6', 'e08d3f76-f638-368f-b699-ebb68ccad2cc', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('a49c8739-69f2-3acd-8564-a3836154a458', 'e08d3f76-f638-368f-b699-ebb68ccad2cc', 'CAS_REGISTRY_NUMBER', '7697-37-2') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('025e3c8a-cba6-39cd-bf57-58218839b82e', 'COMP-H2SO4', 'Sulfuric acid', 'H2SO4', 'H2SO4', 0, NULL, 98.072, 98.05432, 98.0913, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('7ac2decc-cc41-3a3b-a85f-3898fe0fbbde', '025e3c8a-cba6-39cd-bf57-58218839b82e', 'Oil of vitriol', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('2ba2b370-a537-3000-8997-484ecae6ce79', '025e3c8a-cba6-39cd-bf57-58218839b82e', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d1e5ca03-026d-3dd9-8561-75f004684097', '025e3c8a-cba6-39cd-bf57-58218839b82e', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('63be7410-41a2-3714-8bdb-9bf0753da318', '025e3c8a-cba6-39cd-bf57-58218839b82e', '85dc52a3-4eef-3bb4-ab47-d8f271e81aae', 16, 'S', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('9d90ad0b-9c67-3969-97ca-b4c148de838c', '025e3c8a-cba6-39cd-bf57-58218839b82e', 'CAS_REGISTRY_NUMBER', '7664-93-9') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('7de78816-03cb-3472-ad1f-37f885402bb7', '025e3c8a-cba6-39cd-bf57-58218839b82e', 'PUBCHEM_CID', '1118') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('7c255813-eade-3fc6-be86-1935ac089ddd', 'COMP-H3PO4', 'Phosphoric acid', 'H3PO4', 'H3PO4', 0, NULL, 97.994, 97.97716, 97.99741, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('a9c6007e-c749-354b-8075-ba8cb14ea33c', '7c255813-eade-3fc6-be86-1935ac089ddd', 'Orthophosphoric acid', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('7f9dad56-1d14-30a2-9546-647479d1fd8f', '7c255813-eade-3fc6-be86-1935ac089ddd', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('6c6e49a6-2cd4-3a04-a86c-903c9a1f5d4a', '7c255813-eade-3fc6-be86-1935ac089ddd', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('a77c69bf-768d-3f80-bdf1-56a29b2159b6', '7c255813-eade-3fc6-be86-1935ac089ddd', 'f79cae91-c553-3766-a773-c8ec3d8f3e1b', 15, 'P', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('1dd608f3-525b-3334-9185-59f5057a2ffd', '7c255813-eade-3fc6-be86-1935ac089ddd', 'CAS_REGISTRY_NUMBER', '7664-38-2') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('a1a20e49-bce3-39ac-98f8-162131991c48', 'COMP-H2CO3', 'Carbonic acid', 'H2CO3', 'H2CO3', 0, NULL, 62.024, 62.01001, 62.02713, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('9a0da1f8-9e67-321d-9d4e-4951ce072d4d', 'a1a20e49-bce3-39ac-98f8-162131991c48', 'Dihydrogen carbonate', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('4bd75a05-77fb-373d-972e-37a12bb73f67', 'a1a20e49-bce3-39ac-98f8-162131991c48', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('66bca766-199c-3bae-ac1d-23cd6f3b588f', 'a1a20e49-bce3-39ac-98f8-162131991c48', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('89e1fb69-4568-330c-9566-572d329bf41f', 'a1a20e49-bce3-39ac-98f8-162131991c48', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('20f3f7c1-6b59-3dcc-bbc7-5a2c2ba21ca2', 'a1a20e49-bce3-39ac-98f8-162131991c48', 'CAS_REGISTRY_NUMBER', '463-79-6') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('fc7272a0-e0f7-3e3f-8092-8404ddba723e', 'COMP-CH3COOH', 'Acetic acid', 'CH3COOH', 'CH3COOH', 0, NULL, 60.052, 60.04038, 60.05518, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('b8c1ec89-027b-3e9b-9057-9a8f0693f213', 'fc7272a0-e0f7-3e3f-8092-8404ddba723e', 'Ethanoic acid', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('1322d7ef-441e-388e-9cc3-6b7fe0fc05be', 'fc7272a0-e0f7-3e3f-8092-8404ddba723e', 'Vinegar acid', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('38d15c78-2111-3fdf-8017-5254312cd325', 'fc7272a0-e0f7-3e3f-8092-8404ddba723e', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('9db1c420-cfee-3a43-84df-440649de86f6', 'fc7272a0-e0f7-3e3f-8092-8404ddba723e', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('79ec4b54-4b46-3818-b49d-a5cf733d9a8e', 'fc7272a0-e0f7-3e3f-8092-8404ddba723e', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('2e9697e3-5724-3a7e-8b81-6e2a25f19a73', 'fc7272a0-e0f7-3e3f-8092-8404ddba723e', 'CAS_REGISTRY_NUMBER', '64-19-7') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('c6258e3d-0693-3248-94bc-8d455560be75', 'COMP-NAOH', 'Sodium hydroxide', 'NaOH', 'NaOH', 0, NULL, 39.997, 39.99275, 39.99788, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('87b36839-f3f6-308d-96a3-c1de0d03b709', 'c6258e3d-0693-3248-94bc-8d455560be75', 'Caustic soda', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('d3ea34cc-7666-361c-9058-bca23351a66d', 'c6258e3d-0693-3248-94bc-8d455560be75', 'Lye', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('7dc9fd1d-b43b-328b-834f-9adef5c7a883', 'c6258e3d-0693-3248-94bc-8d455560be75', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('b9442a07-67bb-34cb-8d68-6c4814c09288', 'c6258e3d-0693-3248-94bc-8d455560be75', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d4d8977b-8c7a-365b-9391-913532a25be0', 'c6258e3d-0693-3248-94bc-8d455560be75', '7155e5b7-5b66-3bc4-a257-a1921f04ffe1', 11, 'Na', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('49818cd1-a52a-39c4-a39a-a3db929baea9', 'c6258e3d-0693-3248-94bc-8d455560be75', 'CAS_REGISTRY_NUMBER', '1310-73-2') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('556b4fcf-2c70-3cd8-b4c5-9e79f120317b', 'COMP-KOH', 'Potassium hydroxide', 'KOH', 'KOH', 0, NULL, 56.105, 56.10075, 56.10588, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('32701698-817d-3f33-a09f-3c6759a5491d', '556b4fcf-2c70-3cd8-b4c5-9e79f120317b', 'Caustic potash', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('cb9602d8-650a-3750-8931-410e014f1d1b', '556b4fcf-2c70-3cd8-b4c5-9e79f120317b', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('4da15fef-7f52-31de-be53-fa66db452bf8', '556b4fcf-2c70-3cd8-b4c5-9e79f120317b', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('5c2e8457-c599-3a94-ae17-1df619e8619d', '556b4fcf-2c70-3cd8-b4c5-9e79f120317b', 'd6d79ea8-ba41-3a93-8ca2-523d89baca92', 19, 'K', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('72916eb9-d4b8-39d1-b1c7-04b0f8532912', '556b4fcf-2c70-3cd8-b4c5-9e79f120317b', 'CAS_REGISTRY_NUMBER', '1310-58-3') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('b0e1b520-700c-3136-a849-6fb348890d68', 'COMP-CA-OH-2', 'Calcium hydroxide', 'Ca(OH)2', 'Ca(OH)2', 0, NULL, 74.092, 74.0835, 74.09376, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('e3a5af10-fe5f-345b-a5a0-7d9843ee4df5', 'b0e1b520-700c-3136-a849-6fb348890d68', 'Slaked lime', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('de2b224e-54bb-3d08-8265-4e5384ae88fb', 'b0e1b520-700c-3136-a849-6fb348890d68', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('34570e20-c200-3d3d-9322-4f2c8bcff4c8', 'b0e1b520-700c-3136-a849-6fb348890d68', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('4f4129d7-3ee6-3684-9a3f-0aeebc07bc8d', 'b0e1b520-700c-3136-a849-6fb348890d68', 'b410bbd7-d275-3d35-ad8b-e8c2ee54999e', 20, 'Ca', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('c67d269f-9b43-3974-9f76-46e18520f9bd', 'b0e1b520-700c-3136-a849-6fb348890d68', 'CAS_REGISTRY_NUMBER', '1305-62-0') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('8c611aaa-209c-3fe5-942e-6b2777fc75c7', 'COMP-MG-OH-2', 'Magnesium hydroxide', 'Mg(OH)2', 'Mg(OH)2', 0, NULL, 58.319, 58.3095, 58.32276, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('5d17a10e-ae3f-3218-bba7-f3fe9e4da5a5', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', 'Milk of magnesia', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('e17001ec-8773-375d-95dd-c3b8c65e4de4', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d4335ffd-20b0-3086-b535-65a5ccea0ab0', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('1aef0092-783b-3fe9-bf6d-7994373ac6ad', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', '3618c84c-86f2-3901-95b7-bb5384dfad5e', 12, 'Mg', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('0fd17d0c-41fa-3f00-bb0d-52bf4f063ba7', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', 'CAS_REGISTRY_NUMBER', '1309-42-8') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('c47aaa91-83ae-37da-a7c8-2c85c2b2a2c2', 'COMP-AL-OH-3', 'Aluminium hydroxide', 'Al(OH)3', 'Al(OH)3', 0, NULL, 78.003, 77.99025, 78.00564, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('a3bcb24d-2433-3df5-ad64-80897cdf9c30', 'c47aaa91-83ae-37da-a7c8-2c85c2b2a2c2', 'Alumina trihydrate', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('f98e8923-3e4a-35d2-9811-61b7502036b2', 'c47aaa91-83ae-37da-a7c8-2c85c2b2a2c2', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('7c8867f3-14ca-3216-b66f-b9d8a46f24d4', 'c47aaa91-83ae-37da-a7c8-2c85c2b2a2c2', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('5bbebd49-2c8e-3856-a537-1b117d007e2d', 'c47aaa91-83ae-37da-a7c8-2c85c2b2a2c2', 'e0ce8d80-729b-3602-82ea-4030848d3286', 13, 'Al', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('e9cb82b6-fdd9-3cc0-9d7f-ec927f33c7c9', 'c47aaa91-83ae-37da-a7c8-2c85c2b2a2c2', 'CAS_REGISTRY_NUMBER', '21645-51-2') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'COMP-NACL', 'Sodium chloride', 'NaCl', 'NaCl', 0, NULL, 58.44, 58.436, 58.447, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('0ae8dd8e-785a-3403-b9c5-239232e98f93', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'Table salt', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('7b20491d-90bd-32a2-8557-bc070e68a832', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'Halite', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('ecb9eab9-185e-3d31-ba35-7de1eee0ba7c', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', '7155e5b7-5b66-3bc4-a257-a1921f04ffe1', 11, 'Na', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('0c3b1abe-e8ef-3d7e-8d16-fcfd66bb9896', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('c3186699-8f47-3cdd-83cd-95f984d9170b', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'CAS_REGISTRY_NUMBER', '7647-14-5') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('c4ae0fd5-5a19-3944-8279-fbf32aed3d91', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'PUBCHEM_CID', '5234') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('99b9b775-d82e-39ce-ad60-26b7ae2490ac', 'COMP-KCL', 'Potassium chloride', 'KCl', 'KCl', 0, NULL, 74.548, 74.544, 74.555, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('1747a2d1-5d63-3078-aaee-c1df43fe6b6f', '99b9b775-d82e-39ce-ad60-26b7ae2490ac', 'Sylvite', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('9ee6a786-d3bd-3864-b472-d65c0e5394b4', '99b9b775-d82e-39ce-ad60-26b7ae2490ac', 'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('2f40f796-a425-3c7a-a935-d35a976705d3', '99b9b775-d82e-39ce-ad60-26b7ae2490ac', 'd6d79ea8-ba41-3a93-8ca2-523d89baca92', 19, 'K', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('34e51c7f-8aa2-3b42-8ada-512add2e0bb0', '99b9b775-d82e-39ce-ad60-26b7ae2490ac', 'CAS_REGISTRY_NUMBER', '7447-40-7') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('5294140a-5234-3cb6-ae81-635a2260a114', 'COMP-NABR', 'Sodium bromide', 'NaBr', 'NaBr', 0, NULL, 102.894, 102.891, 102.897, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('26a0630d-2401-3803-ba68-48f674cb354a', '5294140a-5234-3cb6-ae81-635a2260a114', 'Sedoneural', 'OTHER') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('27360d9b-cca5-383c-a74a-337f676152af', '5294140a-5234-3cb6-ae81-635a2260a114', '7155e5b7-5b66-3bc4-a257-a1921f04ffe1', 11, 'Na', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('e0da6eb4-5515-31f2-a5b5-d877abab0409', '5294140a-5234-3cb6-ae81-635a2260a114', 'fb26ef36-5d23-3496-b74d-1dea5eec6250', 35, 'Br', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('ef081766-9bb8-3805-9c51-33342b130c70', '5294140a-5234-3cb6-ae81-635a2260a114', 'CAS_REGISTRY_NUMBER', '7647-15-6') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('e7ccb540-00ab-3c13-8397-06fc3bded8ef', 'COMP-KI', 'Potassium iodide', 'KI', 'KI', 0, NULL, 165.998, NULL, NULL, 'EXACT_FROM_FIXED_VALUES', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('1467986f-dc11-31bb-9189-ce8f06e0a1d3', 'e7ccb540-00ab-3c13-8397-06fc3bded8ef', 'Potassii iodidum', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('9d2cdf7d-24b5-395c-802f-a1de95818568', 'e7ccb540-00ab-3c13-8397-06fc3bded8ef', 'd6d79ea8-ba41-3a93-8ca2-523d89baca92', 19, 'K', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('8e29a588-03c2-30ba-8c82-d4cb9044dae9', 'e7ccb540-00ab-3c13-8397-06fc3bded8ef', '0b9284eb-fe2c-38bc-90e1-4fb95e46cd82', 53, 'I', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('4ac8028e-2385-367b-abd6-72fc05687a9d', 'e7ccb540-00ab-3c13-8397-06fc3bded8ef', 'CAS_REGISTRY_NUMBER', '7681-11-0') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('1c7585e5-613d-3411-a3e5-cf08960aae4a', 'COMP-CACL2', 'Calcium chloride', 'CaCl2', 'CaCl2', 0, NULL, 110.978, 110.97, 110.992, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('7d777786-3832-32c5-9a58-9bef22c0ee29', '1c7585e5-613d-3411-a3e5-cf08960aae4a', 'Calcium dichloride', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('ba843825-78b6-38c9-9ad3-a0b04b6cb630', '1c7585e5-613d-3411-a3e5-cf08960aae4a', 'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('bc86e8d0-c411-3866-b571-6c266da16ec3', '1c7585e5-613d-3411-a3e5-cf08960aae4a', 'b410bbd7-d275-3d35-ad8b-e8c2ee54999e', 20, 'Ca', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('fff3dea2-ccf2-3e43-9521-ff7079862efd', '1c7585e5-613d-3411-a3e5-cf08960aae4a', 'CAS_REGISTRY_NUMBER', '10043-52-4') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('ca1f3c39-3ecd-3bc7-9758-ddf0513e504d', 'COMP-MGCL2', 'Magnesium chloride', 'MgCl2', 'MgCl2', 0, NULL, 95.205, 95.196, 95.221, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('0b9ae886-59f2-3176-80b2-d6b93bf1e7dc', 'ca1f3c39-3ecd-3bc7-9758-ddf0513e504d', 'Magnesium dichloride', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('fe3327b8-9377-3eb1-8de6-eb057556ea98', 'ca1f3c39-3ecd-3bc7-9758-ddf0513e504d', '3618c84c-86f2-3901-95b7-bb5384dfad5e', 12, 'Mg', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('fe8e2d85-944e-3d9d-99f4-f6f768db043a', 'ca1f3c39-3ecd-3bc7-9758-ddf0513e504d', 'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('8c49908f-b3b3-3315-8b22-2ac6f3b7a4d3', 'ca1f3c39-3ecd-3bc7-9758-ddf0513e504d', 'CAS_REGISTRY_NUMBER', '7786-30-3') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('41ad250b-399a-344c-94b2-13a3a63bc28d', 'COMP-NAHCO3', 'Sodium bicarbonate', 'NaHCO3', 'NaHCO3', 0, NULL, 84.006, 83.99217, 84.00902, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('c00df43b-43b2-3eb8-b938-9ea2415bb061', '41ad250b-399a-344c-94b2-13a3a63bc28d', 'Baking soda', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('7cbd63c9-8933-30a6-a18f-4a9c3ec6d7de', '41ad250b-399a-344c-94b2-13a3a63bc28d', 'Sodium hydrogen carbonate', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('bc1fd140-46dc-39c8-876e-0b0e04e29219', '41ad250b-399a-344c-94b2-13a3a63bc28d', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('bad49484-24eb-316f-82a5-67776816e225', '41ad250b-399a-344c-94b2-13a3a63bc28d', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('965e19ba-a026-3525-a85c-505d245eee76', '41ad250b-399a-344c-94b2-13a3a63bc28d', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('5bd5a0c6-96d0-3dd4-9a1f-0e4e50fc8ae7', '41ad250b-399a-344c-94b2-13a3a63bc28d', '7155e5b7-5b66-3bc4-a257-a1921f04ffe1', 11, 'Na', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('45e79318-59f9-3909-8be1-dc4702802562', '41ad250b-399a-344c-94b2-13a3a63bc28d', 'CAS_REGISTRY_NUMBER', '144-55-8') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('5c5ee053-0520-3976-85d9-78b89adff2e9', 'COMP-NA2CO3', 'Sodium carbonate', 'Na2CO3', 'Na2CO3', 0, NULL, 105.988, 105.97433, 105.99091, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('50bd5196-48cf-33d5-b850-e71f41fda28e', '5c5ee053-0520-3976-85d9-78b89adff2e9', 'Washing soda', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('8bf5f131-942f-3da8-8c58-3dac39e1be4c', '5c5ee053-0520-3976-85d9-78b89adff2e9', 'Soda ash', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('1271bb9e-4b75-35c1-b023-4649d95a334c', '5c5ee053-0520-3976-85d9-78b89adff2e9', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('7b300d97-b05f-387a-9234-3e817b50189a', '5c5ee053-0520-3976-85d9-78b89adff2e9', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('0c535c53-aa1f-3939-ae0d-ecfc61879fd9', '5c5ee053-0520-3976-85d9-78b89adff2e9', '7155e5b7-5b66-3bc4-a257-a1921f04ffe1', 11, 'Na', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('b3ad4654-a876-3fda-a687-e962ddaf9dc5', '5c5ee053-0520-3976-85d9-78b89adff2e9', 'CAS_REGISTRY_NUMBER', '497-19-8') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'COMP-CACO3', 'Calcium carbonate', 'CaCO3', 'CaCO3', 0, NULL, 100.086, 100.07233, 100.08891, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('bafa7b3f-5fe8-3417-a8b9-d3934e68dc32', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'Calcite', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('441b582d-d201-34a4-9c3e-afbb3e516a00', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'Limestone', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('f1590506-868b-3b50-a64d-53c1648d09e0', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('072ed5b8-f641-3620-828f-ab20c415f2c1', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('563fcdd6-3801-3135-be5a-2a42dab420bc', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'b410bbd7-d275-3d35-ad8b-e8c2ee54999e', 20, 'Ca', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('778edad8-725b-3ab0-adfb-d7b5f005f2f7', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'CAS_REGISTRY_NUMBER', '471-34-1') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('b77d9820-d830-3461-902b-bbe170a40038', 'COMP-NA2SO4', 'Sodium sulfate', 'Na2SO4', 'Na2SO4', 0, NULL, 142.036, 142.01864, 142.05508, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('a974c608-8297-377d-9729-bc898cf2cdb1', 'b77d9820-d830-3461-902b-bbe170a40038', 'Glauber''s salt (anhydrous)', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('b0cc290e-aeb2-339b-98de-f33f0e73e3cb', 'b77d9820-d830-3461-902b-bbe170a40038', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('ecf1d492-0d5f-3bff-8455-0f1f8cd43b60', 'b77d9820-d830-3461-902b-bbe170a40038', '7155e5b7-5b66-3bc4-a257-a1921f04ffe1', 11, 'Na', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('b2959268-880b-3d9d-896d-1b1a6ede2441', 'b77d9820-d830-3461-902b-bbe170a40038', '85dc52a3-4eef-3bb4-ab47-d8f271e81aae', 16, 'S', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('4ba731b7-e733-3de9-b474-358cea40c6fe', 'b77d9820-d830-3461-902b-bbe170a40038', 'CAS_REGISTRY_NUMBER', '7757-82-6') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('f8a18806-7192-35ec-af10-9bd0afabcd91', 'COMP-MGSO4', 'Magnesium sulfate', 'MgSO4', 'MgSO4', 0, NULL, 120.361, 120.34264, 120.38208, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('1e92506b-025b-3a51-bf41-41735d0296f8', 'f8a18806-7192-35ec-af10-9bd0afabcd91', 'Epsom salt (anhydrous)', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('129a4773-9639-3575-ad17-98e49b14de3e', 'f8a18806-7192-35ec-af10-9bd0afabcd91', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('ab791946-7f69-3544-8cbc-f12b1f73d3c3', 'f8a18806-7192-35ec-af10-9bd0afabcd91', '3618c84c-86f2-3901-95b7-bb5384dfad5e', 12, 'Mg', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('1fb8e653-68a4-3dcc-883b-4c598af23296', 'f8a18806-7192-35ec-af10-9bd0afabcd91', '85dc52a3-4eef-3bb4-ab47-d8f271e81aae', 16, 'S', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('d5c05c99-f305-3672-bb27-d8d106b2a010', 'f8a18806-7192-35ec-af10-9bd0afabcd91', 'CAS_REGISTRY_NUMBER', '7487-88-9') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('f57d2b69-d3e5-3f85-908e-3a5648015836', 'COMP-AL2O3', 'Aluminium oxide', 'Al2O3', 'Al2O3', 0, NULL, 101.961, 101.94873, 101.96331, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('27eaf4ce-de59-398d-88cd-b3a5aae75b99', 'f57d2b69-d3e5-3f85-908e-3a5648015836', 'Alumina', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('6b4ed057-e3b0-3b59-ace1-7670dbec3b49', 'f57d2b69-d3e5-3f85-908e-3a5648015836', 'Corundum', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('f0750bc1-9b69-3451-a7f4-2b6c06e4d0f8', 'f57d2b69-d3e5-3f85-908e-3a5648015836', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('6ff66de5-7b46-3683-a5e9-5ab2a40ead15', 'f57d2b69-d3e5-3f85-908e-3a5648015836', 'e0ce8d80-729b-3602-82ea-4030848d3286', 13, 'Al', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('d16a6a7a-63eb-3d17-a964-efdaca581d95', 'f57d2b69-d3e5-3f85-908e-3a5648015836', 'CAS_REGISTRY_NUMBER', '1344-28-1') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('b5497acf-a111-3d7e-8a2a-7e315764a440', 'COMP-FE2O3', 'Iron(III) oxide', 'Fe2O3', 'Fe2O3', 0, NULL, 159.687, 159.67473, 159.68931, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('7106baba-6d81-31ca-9310-e5a6426ea0bf', 'b5497acf-a111-3d7e-8a2a-7e315764a440', 'Hematite', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('d69723e4-887d-3e32-838c-8a116c7d083e', 'b5497acf-a111-3d7e-8a2a-7e315764a440', 'Rust', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('81b4ceed-aca2-3b9c-9992-27172e6fcbe1', 'b5497acf-a111-3d7e-8a2a-7e315764a440', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('cd2bccb6-875f-3f13-98cc-d80a6b2a0417', 'b5497acf-a111-3d7e-8a2a-7e315764a440', 'f5133816-62cd-3ec0-af5b-a566977b9ad3', 26, 'Fe', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('685bdcb2-ce90-3132-a739-22ddf2ab2107', 'b5497acf-a111-3d7e-8a2a-7e315764a440', 'CAS_REGISTRY_NUMBER', '1309-37-1') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('73d6dd57-d6ed-3fa5-8580-b636040b00a1', 'COMP-FE3O4', 'Magnetite', 'Fe3O4', 'Fe3O4', 0, NULL, 231.531, 231.51464, 231.53408, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('0ae7b0bb-11fd-377f-8247-e1d4bc2206e0', '73d6dd57-d6ed-3fa5-8580-b636040b00a1', 'Iron(II,III) oxide', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('ff30be8f-6a20-3eef-b7fb-d4a90e8d054c', '73d6dd57-d6ed-3fa5-8580-b636040b00a1', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('0d051133-eaf1-3ebd-a0d5-a33d61e0be3f', '73d6dd57-d6ed-3fa5-8580-b636040b00a1', 'f5133816-62cd-3ec0-af5b-a566977b9ad3', 26, 'Fe', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('4a57d391-7e1d-3104-85e2-e636f6236111', '73d6dd57-d6ed-3fa5-8580-b636040b00a1', 'CAS_REGISTRY_NUMBER', '1317-61-9') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('d3e7fd9b-1962-3e0a-ab37-bc677cfc4b69', 'COMP-CUO', 'Copper(II) oxide', 'CuO', 'CuO', 0, NULL, 79.545, 79.54091, 79.54577, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('18bd6393-51c3-3b47-9ec8-a1e400743d80', 'd3e7fd9b-1962-3e0a-ab37-bc677cfc4b69', 'Tenorite', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('db391b5c-de77-3edb-ad49-572a746bee0c', 'd3e7fd9b-1962-3e0a-ab37-bc677cfc4b69', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('73eefec8-2fe3-304e-ba3e-d5a2dece2454', 'd3e7fd9b-1962-3e0a-ab37-bc677cfc4b69', 'bf72cc72-a94f-3d56-b1c0-4b00b3a2fdb3', 29, 'Cu', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('5b678e0a-48d8-3e16-91b3-819be2038e22', 'd3e7fd9b-1962-3e0a-ab37-bc677cfc4b69', 'CAS_REGISTRY_NUMBER', '1317-38-0') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('6a5ccf10-c302-3c07-a19f-f345106ca4a4', 'COMP-CUSO4', 'Copper(II) sulfate', 'CuSO4', 'CuSO4', 0, NULL, 159.602, 159.58464, 159.62108, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('1f133c59-541b-3c7a-a907-c900d2b3a247', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', 'Anhydrous cupric sulfate', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('5f4a1c70-32b6-33ad-996c-61d454570763', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('2dbdc6e8-b6bd-3014-a391-2c104cf39364', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', '85dc52a3-4eef-3bb4-ab47-d8f271e81aae', 16, 'S', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('007e9f91-e8ab-32f6-b20c-c76590e0fcad', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', 'bf72cc72-a94f-3d56-b1c0-4b00b3a2fdb3', 29, 'Cu', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('c087fd8e-ae8a-32c4-8c8b-51d83e8257ed', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', 'CAS_REGISTRY_NUMBER', '7758-98-7') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'COMP-CUSO4-5H2O', 'Copper(II) sulfate pentahydrate', 'CuSO4.5H2O', 'CuSO4·5H2O', 0, '5H2O', 249.677, 249.63759, 249.70103, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('537be01f-170a-3e4f-8494-0a151efbf01e', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'Blue vitriol', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('16040743-6da4-30c3-b85d-26fcfbddaa62', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'Bluestone', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('074c99f4-e55a-3a41-918e-05a03f43385c', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 10) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('3dd64f07-b00e-356a-8f9a-dcef0f4bf77e', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 9) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d7de8507-1d51-3d37-b56e-c6b80208c5f5', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', '85dc52a3-4eef-3bb4-ab47-d8f271e81aae', 16, 'S', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('afc0f9b1-0b54-3179-8b30-237650ff4459', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'bf72cc72-a94f-3d56-b1c0-4b00b3a2fdb3', 29, 'Cu', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('e27a33f9-81ef-3bcf-ba32-d7f3c5b10851', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'CAS_REGISTRY_NUMBER', '7758-99-8') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('059ab99e-36b8-33f7-b2a0-4fa80d57df6c', 'COMP-AGNO3', 'Silver nitrate', 'AgNO3', 'AgNO3', 0, NULL, 169.874, 169.86116, 169.87659, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('173de327-4342-39f4-92b4-bbcca520d72e', '059ab99e-36b8-33f7-b2a0-4fa80d57df6c', 'Lunar caustic', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('3834dcea-8793-3a71-8405-82a3a8997a5b', '059ab99e-36b8-33f7-b2a0-4fa80d57df6c', '87e4c172-8582-34c0-bdf6-dd0d326b9b93', 7, 'N', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('0ba07d65-5dcb-3c24-bc67-beef146dde1c', '059ab99e-36b8-33f7-b2a0-4fa80d57df6c', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 3) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d4f7a59d-d6de-32ca-a8ca-ca0e7563ffef', '059ab99e-36b8-33f7-b2a0-4fa80d57df6c', '359a62af-367a-3efb-86dc-e3783a49756e', 47, 'Ag', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('9228d1ec-9b98-321d-b07d-10ab88a0d632', '059ab99e-36b8-33f7-b2a0-4fa80d57df6c', 'CAS_REGISTRY_NUMBER', '7761-88-8') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('1d618f24-ee51-3dcc-b281-b5b7afe54bf4', 'COMP-BACL2', 'Barium chloride', 'BaCl2', 'BaCl2', 0, NULL, 208.23, 208.222, 208.244, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('dce48fa7-337e-3383-84bc-734affcd5490', '1d618f24-ee51-3dcc-b281-b5b7afe54bf4', 'Barium dichloride', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('8b293d0e-38a9-3a93-b588-8e64d8d37b2e', '1d618f24-ee51-3dcc-b281-b5b7afe54bf4', 'caaf1bb2-9b22-3a8c-b847-9c87af032804', 17, 'Cl', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('bc5e6ed7-44a2-3087-92a7-034a153c1467', '1d618f24-ee51-3dcc-b281-b5b7afe54bf4', '678bd0b1-d318-3fc6-8825-7f9754113558', 56, 'Ba', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('397fd217-51ed-3f2a-a87d-8082ea50dd04', '1d618f24-ee51-3dcc-b281-b5b7afe54bf4', 'CAS_REGISTRY_NUMBER', '10361-37-2') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('e10c4399-2bd8-351d-87e5-752acf2b27d7', 'COMP-KMNO4', 'Potassium permanganate', 'KMnO4', 'KMnO4', 0, NULL, 158.032, 158.01564, 158.03508, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('7fa01218-3ed4-3256-957d-84e06c5b227a', 'e10c4399-2bd8-351d-87e5-752acf2b27d7', 'Condy''s crystals', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('bd5745d3-0150-353c-a04e-34cb3b4d57dc', 'e10c4399-2bd8-351d-87e5-752acf2b27d7', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 4) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('4636c472-4823-328f-af31-ee543a8741e1', 'e10c4399-2bd8-351d-87e5-752acf2b27d7', 'd6d79ea8-ba41-3a93-8ca2-523d89baca92', 19, 'K', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('be817f97-8879-34fa-9558-2a6929a46e3e', 'e10c4399-2bd8-351d-87e5-752acf2b27d7', '42a57138-f54d-3292-8884-09bd10e5d0ac', 25, 'Mn', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('bfcffd58-3893-3e9f-82b8-64e41bfd9720', 'e10c4399-2bd8-351d-87e5-752acf2b27d7', 'CAS_REGISTRY_NUMBER', '7722-64-7') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('de46be3c-3f03-3c24-b32b-d51b403aea4a', 'COMP-K2CR2O7', 'Potassium dichromate', 'K2Cr2O7', 'K2Cr2O7', 0, NULL, 294.181, 294.15237, 294.18639, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('e948d31d-3897-3732-8b0c-b77ba4b2de34', 'de46be3c-3f03-3c24-b32b-d51b403aea4a', 'Potassium bichromate', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('92c3d50c-3e9b-3ab3-a868-19b801068178', 'de46be3c-3f03-3c24-b32b-d51b403aea4a', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 7) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('169673a0-60a3-360f-8364-e95b7c7f1fee', 'de46be3c-3f03-3c24-b32b-d51b403aea4a', 'd6d79ea8-ba41-3a93-8ca2-523d89baca92', 19, 'K', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('fae2ac67-4260-3829-adf2-e08cb7715654', 'de46be3c-3f03-3c24-b32b-d51b403aea4a', '20df4212-a434-3b4c-870f-ff4db46cecd4', 24, 'Cr', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('dbbed30e-18ea-3f8c-9135-9544ef924205', 'de46be3c-3f03-3c24-b32b-d51b403aea4a', 'CAS_REGISTRY_NUMBER', '7778-50-9') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('10b0b074-d84b-3e34-8392-20f74663472d', 'COMP-ETHANOL', 'Ethanol', 'C2H5OH', 'C2H5OH', 0, NULL, 46.069, 46.06115, 46.07163, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('6c717107-9a76-3bdd-bb24-5ee2cd73f46a', '10b0b074-d84b-3e34-8392-20f74663472d', 'Ethyl alcohol', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('faa95c50-dd4c-30b4-b7a4-2ced0d7b93a0', '10b0b074-d84b-3e34-8392-20f74663472d', 'Grain alcohol', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('97ba21b1-8dca-3d61-886d-0bf60b1cff72', '10b0b074-d84b-3e34-8392-20f74663472d', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 6) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('c18aff1f-26a0-3b5c-96f8-d3be55e7b465', '10b0b074-d84b-3e34-8392-20f74663472d', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('3fb44193-982c-3fdb-8f77-5f0942067a3b', '10b0b074-d84b-3e34-8392-20f74663472d', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('441a0172-1ad1-3eb3-9cfd-b7ae4f68aa8d', '10b0b074-d84b-3e34-8392-20f74663472d', 'CAS_REGISTRY_NUMBER', '64-17-5') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('a8de3a69-b8af-39ce-ae06-074877a43e68', '10b0b074-d84b-3e34-8392-20f74663472d', 'PUBCHEM_CID', '702') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('d1461121-ac57-31a4-bb71-8ae345d27f33', 'COMP-DIMETHYL-ETHER', 'Dimethyl ether', 'CH3OCH3', 'CH3OCH3', 0, NULL, 46.069, 46.06115, 46.07163, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('4ef71369-dcb4-31d8-87b4-4f94269303cf', 'd1461121-ac57-31a4-bb71-8ae345d27f33', 'Methoxymethane', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('a854126c-6d3b-3f87-ae3f-5da5b0c15e11', 'd1461121-ac57-31a4-bb71-8ae345d27f33', 'Wood ether', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('1e6cfc13-5d87-3388-81d4-3105c7756bb9', 'd1461121-ac57-31a4-bb71-8ae345d27f33', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 6) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('4f8a7e6b-9d4b-33bc-aff9-ab631fe25cef', 'd1461121-ac57-31a4-bb71-8ae345d27f33', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 2) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('37a57f9b-ef9e-34d1-ba41-00eb833ea4c6', 'd1461121-ac57-31a4-bb71-8ae345d27f33', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 1) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('965efea9-1033-3f22-b7a8-e798565bff3b', 'd1461121-ac57-31a4-bb71-8ae345d27f33', 'CAS_REGISTRY_NUMBER', '115-10-6') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('c06091c6-d6a5-3779-bccf-5d9201083f0c', 'd1461121-ac57-31a4-bb71-8ae345d27f33', 'PUBCHEM_CID', '8254') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'COMP-GLUCOSE', 'Glucose', 'C6H12O6', 'C6H12O6', 0, NULL, 180.156, 180.12114, 180.16554, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('65ea04eb-8a80-36f1-b803-7445fc7930b3', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'D-Glucose', 'SYSTEMATIC') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('aa259fe0-f8af-39a0-97ae-c8ae9ed2d386', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'Dextrose', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('08f826ca-102c-3ba9-b0af-f4dd8d1cbee9', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'Grape sugar', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('32ee70f3-f8de-397b-be97-c6499cb0c50e', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 12) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('dd2ca010-8e7d-3e34-bbdb-d13214f4daaa', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 6) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('f96e7cb2-fefc-3a01-af2e-4977abe86bb8', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 6) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('341d43d7-3a97-3be5-9de3-c5a2ce962902', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'CAS_REGISTRY_NUMBER', '50-99-7') ON CONFLICT DO NOTHING;

INSERT INTO chemistry.compounds (id, compound_code, primary_name, original_formula, normalized_formula, net_charge, hydrate_info, molar_mass_value, molar_mass_lower_bound, molar_mass_upper_bound, molar_mass_kind, element_catalog_version, compound_catalog_version_id, source_identifier, source_title)
VALUES ('af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', 'COMP-SUCROSE', 'Sucrose', 'C12H22O11', 'C12H22O11', 0, NULL, 342.297, 342.23169, 342.31509, 'INTERVAL', 'v1.1.0', 'compound-core-v1.0.0', 'CRC-HANDBOOK-104', 'CRC Handbook of Chemistry and Physics, 104th Edition')
ON CONFLICT (compound_code, compound_catalog_version_id) DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('2bb0b3ae-c0ff-31ad-bce1-a0d3aae874d5', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', 'Table sugar', 'COMMON') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_aliases (id, compound_id, name, role)
VALUES ('30aa407c-1cfa-380e-bf3e-5a586db4d4d6', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', 'Saccharose', 'HISTORICAL') ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('467eafe0-d7c0-38e8-b746-61f00694a39e', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', '6207a804-03dc-3cc1-aa3b-5b7303315c4b', 1, 'H', 22) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('d19dbd1c-0378-3b23-96aa-890e3a193441', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', 'ee9d16b6-8c34-35e6-b221-796fe15d9424', 6, 'C', 12) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_components (id, compound_id, element_id, atomic_number, symbol, atom_count)
VALUES ('9065143c-e63e-3ebb-a0c2-a14f2a2c332e', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', '2f51c0fc-fbf7-31dd-b8b2-de15529ee4be', 8, 'O', 11) ON CONFLICT DO NOTHING;
INSERT INTO chemistry.compound_external_identifiers (id, compound_id, scheme, identifier_value)
VALUES ('cdb06592-7b94-3fbc-a45b-ddb15e30be09', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', 'CAS_REGISTRY_NUMBER', '57-50-1') ON CONFLICT DO NOTHING;

