-- V13: Seed compound physical properties
INSERT INTO chemistry.compound_physical_property_dataset_versions (id, name, publication_date)
VALUES ('compound-physical-properties-v1.0.0', 'CRC Handbook 104th Edition Compound Physical Properties', '2026-08-05')
ON CONFLICT (id) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('88f836e9-9e36-30fe-86ba-201f88a02caf', '650b152a-3a54-334b-9006-627007c122b0', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7d8605b5-515c-3c6d-aa0e-7badc52ac973', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a21466e5-105a-35e2-9732-d1f3c2feb37a', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1988baeb-fa1d-3aad-88c2-1c048605003e', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('be2dabcd-eaf4-31db-94e2-0f129b17e812', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4baeb431-35ae-3c00-811c-ec974df5e72a', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ebc09736-8e68-3a39-b3d7-724a8333d235', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f1d6a3f7-d705-3fcc-9d94-4012b39cfa9a', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8f140f36-0b30-360f-b429-7d97f5d39ccc', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('280dcbfc-9599-3ff3-b00e-9f4dabea2a35', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('411e0d55-50f4-3f92-8e57-0fc05b8a1ab7', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('17cd28c1-9c36-34b4-9dac-4674aefe1cae', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1b1f705b-3efd-3c94-a80b-6dab53f64059', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('66a3ce57-2c43-3a16-8c59-46357553a857', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7141bf34-eaf7-313e-a139-0544ff59b7d1', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bd984575-92d1-3b55-9e63-69962b70f8c1', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('26ee5cb7-c3c9-3b61-ba00-2cca0cb8f26e', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ad051cc6-a088-3fbd-ac3f-1c4f6706b358', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('47e714e2-11cb-36de-9ba7-0ddd36548b24', '88f836e9-9e36-30fe-86ba-201f88a02caf', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('1ff6efed-7d2b-36be-8ee2-047dea9c4d59', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('346282b1-15a9-3377-a71f-c9607645a534', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bbaadc62-4a13-311f-9003-e8215b0c2a94', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b1ddc5a0-e76e-3f0d-976c-5485c5bbd466', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('63ca2b13-330e-385f-a3cf-b6077482e8f6', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('719a8080-35fc-33f2-9cf0-7d8bddb8390e', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8ce5a779-4bfa-3d88-9348-0c4d6a61b35b', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0b157e5c-996e-36bb-82ef-a784674b5818', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4e7e5913-46a9-33ec-bdab-5ee676c55b42', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4898e23d-f1f5-37fb-84bb-a3cdf262fbd7', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7291c0c9-093f-3b8f-838f-5c90a673dd21', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9431b5e4-eebc-33fa-953c-848addf0aa7a', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c5909733-b765-3879-8fe0-20d52a9ab5a8', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('94226334-3c1b-389f-923b-26d18c5bae6b', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('579d67ee-07cd-3f34-a0e2-6d26a00ed4fd', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('437ed9ff-754b-3e98-8dea-9e994dc49285', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e44630d0-0c97-30b7-b9bf-e85f85fc18d0', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('83ed3bd8-bb0f-3e62-a53e-08f025b47dd3', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bbc5d09b-9ed7-35b1-a36f-3732092f2017', '1ff6efed-7d2b-36be-8ee2-047dea9c4d59', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('aa48bf37-b696-3e70-8d30-0702a562f664', '9ee0626a-3425-3597-a497-5cbf32c8570f', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cea95662-c94b-394f-997c-0a48fc148846', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d9100be1-1086-3f1e-aa17-1a64f0dcc6f1', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f2e7b36b-7983-3087-9b7f-070898179b69', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('52901938-6444-31b8-9eeb-49d6e37b9d96', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a7e929cb-ac4d-350c-8b65-c818b45f141b', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2b9361b5-b46b-3f0b-b06f-6fbfbe640993', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3d68758c-2217-3c4c-974f-1c135610685e', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('783f388c-643d-35b9-bdc4-92ae9c7bded6', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1a8b8abb-2d07-3175-9f4d-658dbdf3d2ca', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('90b999bf-5a54-3346-98b2-5af36a05b6dd', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ff9e3e73-7d5b-3474-8643-51b69f570e83', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3ddcc3bb-3741-33a2-93ee-e93e02aacc1a', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('81212e56-9b24-3cb9-8552-59010e2dd90c', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bd97db8d-df63-33a5-851e-08f010ef5005', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e76f97df-60d7-3756-b41b-73d6ad71e537', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('05662bc2-be99-36fc-a375-6eb344ae840c', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5884e622-9d25-3df6-86ce-71c216d1d2b6', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e35e35bd-6227-30f9-888d-0622efdee934', 'aa48bf37-b696-3e70-8d30-0702a562f664', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'ef4b615a-9ecf-3a89-981d-b1f8b8fd72da', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('54e1cc47-4ca5-3b83-90ab-8d31118a2e4c', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('64ef1b20-9214-3c99-bbd2-06f7c8b77faa', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8f1fe7ea-ee88-3b30-aa5b-e9c1fdaaf2dd', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e1c02314-0997-3ff7-b1b7-5d53ecccac8e', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2e917bda-31c0-3e99-9126-97af61592dc2', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3fc0b146-997e-3442-bac1-2829fc6db640', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('08a54d4c-05db-30f4-ba52-379115debc53', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9c5cb21b-67fd-3ce0-ba81-176342d54c3a', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7b57ddc6-eab7-37c2-9900-dee7acc4a95c', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bf112bf0-e056-34db-be08-d88cbb0bea80', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2e91474b-8ea8-37de-8999-410f864705a2', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('68b260c8-e96f-398f-a107-0264f1488c91', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('765f8eb4-ea10-3ceb-8d47-ce4196438a46', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d132a15b-5d40-3232-8ed8-800e602806bc', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ead7a445-869a-3104-aa35-44ba0d080038', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5e4f4b2a-5cbf-3198-80d4-9a95f00751ad', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c06d3d59-406a-3e89-8a79-cac2dd9b41f3', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3f1ae95c-ba86-3146-bc8e-13681fbe9f19', 'c60d4bf1-7616-3bac-a90a-8dbf3a5c83e1', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('740ed374-ad9b-372b-82fa-7e1052645fd5', '31ac5183-783b-3ffd-bb92-7b247924a42f', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f3d88fc5-b2af-3d85-9080-842d78e6f95f', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('439e0fa2-257d-303c-86e8-e664ba70b8cb', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c86c5b1e-4c54-371f-8188-5cd048ab30fd', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2a35b6ef-1fb4-360c-8a36-d75f6ca2f7e8', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c7d6567d-8bce-36fc-ad14-a44be8a42551', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('15e3185d-704e-36c2-847d-cf1d2c9760e4', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e11f06dd-48e5-30cb-a186-a91bb60d5477', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('14d498af-f067-3b1f-a8c1-11cc995d0b64', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('423af95a-93f1-3882-aab1-79845af914d2', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e4ad1409-35ba-323c-b577-8fa0de77646e', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('473a08b9-9937-3c32-8eaf-162758919f87', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f0104210-e040-3fd2-8dce-d1049de00619', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3f41c21c-c5f9-325b-a930-636df4e5d6c1', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a9bf58b2-beec-3370-8d09-3868781cc387', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ecf5024c-2751-30ed-b2e4-4295baa66f27', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6e50891b-1452-3e41-a2e2-6089231e6d44', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d8a0a741-dae3-3ecf-a7c3-7cfc5e64d127', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f1149560-ce92-3e48-b4c4-3a954bf513af', '740ed374-ad9b-372b-82fa-7e1052645fd5', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('50f5441c-8072-3958-ab95-e317b5e7caa2', '384ac66c-9749-3b89-8257-d665e1318c93', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('60761efc-240e-36f4-90b0-6247d23b72ed', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4d5a7d7e-2f98-380a-bb13-dbca7049d94f', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0ee86c20-effb-3a51-b034-3fa4ad5fe404', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6d7b1aa5-37be-3078-b0ea-8f6dd344bd89', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6d85aeef-4b63-3974-83fb-9261b250fda8', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9957f0da-db92-3001-8de0-9a5304157db0', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('123f9ce9-afc3-3340-b67f-5aa2c635d411', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('eb4e2403-bf76-3dcc-abfa-4089c8981d08', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('eafe668c-4435-357c-a36b-2225300798b6', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6f9bea0b-7345-3703-bcd5-3006f8277269', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b0413826-04f0-3603-9c2a-6ff2df06545d', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e63f5a0c-3e4f-3eb8-8bf3-5ffc36415d92', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b16dead2-5cf8-31fd-a2fd-dd0c33283a42', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e0cb91bb-ea46-3e55-87d7-ba538dd962e9', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('55a68c04-27eb-3de9-afce-17e47a7bca03', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('26f3f993-6fa2-3e9f-979f-de6488a8fd9d', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e3d30c36-5d69-393e-ba8f-000489b9afbe', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4f4d714b-429c-3143-9c6f-95bc9d310438', '50f5441c-8072-3958-ab95-e317b5e7caa2', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('6553b610-0385-31a9-8557-4412722e0986', '6f1a0282-9bfa-350b-bcf4-1924eb58261c', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3e17931b-dc9f-377e-84c2-c3a8e44ad4fb', '6553b610-0385-31a9-8557-4412722e0986', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a0a580c0-4c26-3f88-aa0f-6d438e7daae6', '6553b610-0385-31a9-8557-4412722e0986', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('66f699a8-dd86-3636-9f79-0c579db42b6b', '6553b610-0385-31a9-8557-4412722e0986', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('22017f09-b3dc-33ef-8352-1b2cc885b2b6', '6553b610-0385-31a9-8557-4412722e0986', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cf57a0cf-43ee-3553-aefe-27bda8e0c2c1', '6553b610-0385-31a9-8557-4412722e0986', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0849f315-1abd-3277-af95-3f33a2fb1196', '6553b610-0385-31a9-8557-4412722e0986', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('97fae8db-e072-3387-a239-378d829e78dc', '6553b610-0385-31a9-8557-4412722e0986', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('42c16fee-b343-30ae-b58c-9b68247095f9', '6553b610-0385-31a9-8557-4412722e0986', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e09bd98e-fece-3806-ac8c-ef4eb2290a13', '6553b610-0385-31a9-8557-4412722e0986', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('76cd35f2-3eab-30bb-9c1f-1964582ac81e', '6553b610-0385-31a9-8557-4412722e0986', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6b33f864-a0f2-372c-a00d-1f61c4cb4121', '6553b610-0385-31a9-8557-4412722e0986', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4725b6e3-4140-3a50-bf68-909c23894347', '6553b610-0385-31a9-8557-4412722e0986', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('22017ccc-d76b-30dd-b221-d6a83501be47', '6553b610-0385-31a9-8557-4412722e0986', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c7f1200c-7342-32d2-9d76-00ce6654e555', '6553b610-0385-31a9-8557-4412722e0986', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('06717af5-5292-330b-9311-2e866a0d4e6d', '6553b610-0385-31a9-8557-4412722e0986', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0a01db66-33bf-312b-8e62-b4225c89f083', '6553b610-0385-31a9-8557-4412722e0986', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e540c64e-264f-30ea-bc8f-14407e3ad1db', '6553b610-0385-31a9-8557-4412722e0986', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('83c10439-0a9e-39ee-951d-f961307f7690', '6553b610-0385-31a9-8557-4412722e0986', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('59fbb753-7ede-3270-9703-ace5aec5eaf6', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('13c246d1-b205-32a8-98c2-aa79b7bc144f', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'VAPOR_PRESSURE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3ea327dc-08c8-3bbb-be21-f3f386a17edf', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'PH_OBSERVATION', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('03fbae6a-c50d-3f0e-879f-1574c2f9537f', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'REFRACTIVE_INDEX', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f55689f6-31df-39a9-a0a1-424210b277f8', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('51ad7a39-a989-3a51-8dd9-0fdbaa96ab36', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a95c5601-1ee1-3713-b766-825788ec99b0', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'BOILING', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a1a4de61-e0ff-30c8-8310-709f50301436', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'ODOR', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0a2998dc-68b9-3dce-9f1d-4f993ba58824', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'MOLAR_HEAT_CAPACITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d433ffed-ad8c-3a72-8f2c-0c17f9e7af59', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'SURFACE_TENSION', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6be4cdfe-2a7b-34e4-8f70-7ce87b17b25d', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'SPECIFIC_HEAT_CAPACITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5d47fe74-f257-35be-946d-0691bce52252', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('06af94cd-785b-3284-acc2-3339b9768dac', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'DENSITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5d14b0f8-e6a2-3d10-905f-f5e2165f1802', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'APPEARANCE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('af1f6ce7-b3c1-33f1-bcbe-e6af0191f922', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'MELTING', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9d72decb-2579-34f3-9a19-2b8822351c4d', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ed29dcbb-7b57-371f-a277-f1061faff853', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'POLARITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('aa45c1e3-d1e5-3499-8a6c-53620102d4c9', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'VISCOSITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('29600fa2-c050-3576-8f6f-f6d670639007', '59fbb753-7ede-3270-9703-ace5aec5eaf6', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', '25aa9b6b-0e15-3c35-905d-9c056f332a8a', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a9c5ad4f-2158-3270-a73a-b83ba1642efa', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4f900cd5-f61e-3509-91e7-cf86903c9cc8', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('27307f74-e007-3ab4-a88d-eb71b6d78abb', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('326f45e3-6a7a-3962-9de4-71ffbb0fe957', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('55bb38fe-51c5-3e1c-9ef9-110ec9bcfd67', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d994cbe6-eead-337c-ad02-6991486b4e8f', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3d985bea-82b1-35db-a2a2-6b879c7a2e17', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9eb2d11e-fcb5-3d56-92b3-2e05ba663487', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7a246ca1-90b4-38c4-a2bd-c952e4fea113', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ba82f7c0-d051-3d0a-9ff3-c4e0cf35f332', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fa20cef0-ee0b-3329-98ca-dba882e176f2', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('025c86a9-7703-3664-9133-f5ce7e6ed01d', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('453ce43b-2048-399a-bd81-f6b530d2fe65', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0d3495f3-89e4-334b-b525-2cd04c2ba73b', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d1caed72-13c5-3833-839b-c055dbef1743', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('16bf18e8-df7a-3e78-bbf2-0bf847894db1', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7ed80150-34f0-3a4e-9778-791860c1ef17', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('56f8aae3-e0a3-327e-93da-8a4e6f224f81', '88f7f9f8-c2b8-3e80-8c2c-281fdaafae60', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('06140170-3bec-39fa-af6a-01c883f62548', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('66cbd6ca-82db-3e52-ae81-0e36a0c47305', '06140170-3bec-39fa-af6a-01c883f62548', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9bfd722d-4f91-317a-82fe-21a4618a1bc8', '06140170-3bec-39fa-af6a-01c883f62548', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7a0a0f49-4e92-3d27-a092-58d05b1470df', '06140170-3bec-39fa-af6a-01c883f62548', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('48b7df99-f7d7-3389-abbf-ef4ae36738e2', '06140170-3bec-39fa-af6a-01c883f62548', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f7d34484-e01a-3efd-9c8f-8b296df41686', '06140170-3bec-39fa-af6a-01c883f62548', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cf158f45-3b01-3c7d-893f-29a15d812074', '06140170-3bec-39fa-af6a-01c883f62548', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('450c8d66-cf56-3648-86e6-9212dfde3a0e', '06140170-3bec-39fa-af6a-01c883f62548', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6510e9ca-2c48-3d32-b4ab-7026412a154e', '06140170-3bec-39fa-af6a-01c883f62548', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9bfe3021-c5ec-3c05-a67a-eba421c19d0b', '06140170-3bec-39fa-af6a-01c883f62548', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f280df5a-0014-3a2d-a433-712fc820b505', '06140170-3bec-39fa-af6a-01c883f62548', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b9e1444c-bc8a-37d1-a784-d68195a0dccb', '06140170-3bec-39fa-af6a-01c883f62548', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bee23de6-5451-31ce-9048-38d86a1f69f7', '06140170-3bec-39fa-af6a-01c883f62548', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4a1066b6-2168-3a85-9340-68616976c801', '06140170-3bec-39fa-af6a-01c883f62548', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d05acd70-b632-3331-85fd-428fc4bf5fa7', '06140170-3bec-39fa-af6a-01c883f62548', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0456579c-99c9-3f17-ae17-989597c2b9fd', '06140170-3bec-39fa-af6a-01c883f62548', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('46b9580d-1037-3d7b-bf37-314d58e784b0', '06140170-3bec-39fa-af6a-01c883f62548', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0c7ffb42-8854-32c8-8a2f-df55725e4284', '06140170-3bec-39fa-af6a-01c883f62548', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ff6c9075-5e10-3618-9391-ec2d2b8cf459', '06140170-3bec-39fa-af6a-01c883f62548', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('6151fcd7-96aa-3149-be43-93c2955f4664', '44d58472-c681-3f7e-b989-9b8730603a2b', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8fafb18e-f80e-3d4b-af2c-732973de8158', '6151fcd7-96aa-3149-be43-93c2955f4664', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f959a400-863a-3715-a111-91cc6c1c2f83', '6151fcd7-96aa-3149-be43-93c2955f4664', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1b12aeb0-aa16-3dab-80f2-27cd0a38c11a', '6151fcd7-96aa-3149-be43-93c2955f4664', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('41c2d2db-3807-37d2-87f0-abc884335355', '6151fcd7-96aa-3149-be43-93c2955f4664', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c4860e11-c73a-3103-9539-7c6ed3ba395f', '6151fcd7-96aa-3149-be43-93c2955f4664', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('73ee18ee-f8e7-3a4b-ac3c-1151614e2325', '6151fcd7-96aa-3149-be43-93c2955f4664', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('03faaf83-81fd-35a3-bead-82151b255536', '6151fcd7-96aa-3149-be43-93c2955f4664', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('aee7b337-cc0e-3c7a-89f9-591c54227b83', '6151fcd7-96aa-3149-be43-93c2955f4664', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ebddf2f4-651a-318c-9d0c-60d894b8a707', '6151fcd7-96aa-3149-be43-93c2955f4664', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('11b8e48a-fa82-3a79-b44f-247c73c673c9', '6151fcd7-96aa-3149-be43-93c2955f4664', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ca144797-febc-37fe-ae70-94a584d1f4a0', '6151fcd7-96aa-3149-be43-93c2955f4664', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a6770c09-b745-3409-82b2-05eb83fcad29', '6151fcd7-96aa-3149-be43-93c2955f4664', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('69f31118-b931-379c-aa89-8d1826300a9c', '6151fcd7-96aa-3149-be43-93c2955f4664', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a9a245f8-6a82-339a-a354-dc7e3eda1075', '6151fcd7-96aa-3149-be43-93c2955f4664', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('52189fb9-2f25-38c8-90f1-e63856160dd7', '6151fcd7-96aa-3149-be43-93c2955f4664', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4e3e86f5-84e1-3c45-83be-192a3fb342f4', '6151fcd7-96aa-3149-be43-93c2955f4664', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('85e4092e-21d5-353d-bfd1-cd2dfb0acfd1', '6151fcd7-96aa-3149-be43-93c2955f4664', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d9d1bcb0-80b7-39c5-89a6-8d5990da3cda', '6151fcd7-96aa-3149-be43-93c2955f4664', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('51db5b49-ae44-30ac-ab22-52edbf62ba23', '665185a5-e410-38fa-8e02-d4a2be56e2c7', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('65e504e5-7ecf-34a4-a55d-70837b49658e', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7ede975e-911d-3f22-b8ae-5833dbccda92', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7efb9074-8d61-32a5-bae2-b5c723ebb1d9', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('be12a46f-214f-3a28-8b2d-2af650c43c8a', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f5d60547-8b3d-3dd7-9382-3b236aec0715', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('974f6de9-a5a6-350f-a89c-de5736eae70e', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('af6d0086-e40b-3d7f-beed-c578af8ca270', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c130a091-49bd-3c1e-9904-813c0ca32f6e', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b50d86eb-e294-36b6-a804-caba67ee8656', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3fc22dd7-2f2c-3967-be2b-49ebadf647bd', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('90fa2fbe-388b-3747-a54c-a37096d4e948', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5a934b60-607e-3b60-8775-49582acc84ec', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('40bfff35-1ff3-3b4d-866a-1e5c06421a68', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1f8918df-19f9-3a83-b577-f553d4e9157d', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('94c9cb78-1f2d-3850-af18-8809bcb0193f', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bd48d2b6-4c15-393a-af60-22675f9de806', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a995772a-efc8-3e7c-a94c-a3006511e0a0', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a6424f3d-ac18-3dc3-9ada-6ee0fb4986c8', '51db5b49-ae44-30ac-ab22-52edbf62ba23', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('e4f4f13c-8898-30cf-9ddb-87b92c8983b4', '095d8580-8beb-3e71-800d-add10b6590ae', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f99f1d7e-86a3-35e3-91d1-7ba1fec3b155', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f9e7f8e3-5d16-370c-bbc9-e8831b28c6ca', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e8bc4668-d45d-3b70-a67b-479141a17b13', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6a3aed06-26a8-381d-9e6b-86cff18d8e60', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('aecce932-9583-33eb-9699-885bc6879be8', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('73c01bac-35cc-3d74-921f-1190a439ccaa', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0311adaf-acfb-3427-9c7e-3bb50bb546e7', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('49876bb6-d403-3a91-a596-66631896d086', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1580ee07-b3ad-37b8-9a13-09acb70a67b6', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ad0399bd-b8b6-367f-89cc-0e924c1bdec9', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d077cd32-9bfd-3c12-b670-b0c8272efb04', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c4463f3c-5a59-3662-9815-e993ebb52c17', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('02ee624d-8389-3151-9315-9bc841fbbbba', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9502e936-2c6c-3736-99ad-ef0820234e31', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d74bd2a4-b90c-3cdf-801d-213a74670b04', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f2395a4a-0490-3d0c-86ad-ef01e9d2cef2', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8bddbbfb-5421-3bd0-a6e0-a77b42ce58e1', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('49e584d8-d2b1-3e21-bfd2-e3b882e432b7', 'e4f4f13c-8898-30cf-9ddb-87b92c8983b4', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', '41536b4f-237a-38ae-9cf9-b0099a36b773', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('10d35ac7-6a82-3e49-b78e-7092f2e6cc5e', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('963e7e10-529e-35fe-9f10-464a29942586', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('74676b76-266c-329d-9d86-78bedbd96418', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('610e181f-4833-3df7-b56b-1e3415683e60', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3cd9417e-b304-3d68-a797-27d535871a6d', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6ef86b91-c4e8-314a-9639-45c9c706917c', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('eafa736d-4497-3fdb-a25b-be1fb127ddce', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cbd0098a-929c-3b1a-a715-2ba6b3401bf5', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a641330d-f454-3357-a49d-e8aa45dd99cb', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('edcbdcbc-b5b3-3f3b-afbe-70958c06aced', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6663c1b4-56a3-35dc-b1e6-9b44ba60268a', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('37a33818-de28-384c-b1f7-a0f597efcd24', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('57488c08-e58e-35de-9b50-7837221568c3', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4f546968-f262-3c78-a7d8-c0769c267b85', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4d0a5a52-8821-32f1-9f1c-9be582c48464', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9d6dfea2-2e37-3105-b564-9cc24099e314', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b66395d7-c2eb-35cf-8e00-24f098a416d4', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8bb76c50-8ded-3eb1-91e4-2fcc5468e60c', '3e6e7ce8-6c4a-3d31-a7f3-869d98e9e86f', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('9b32bd69-342d-38f4-99c3-f69019351751', 'dcaa9932-0ee6-37a4-be71-fb00655d1a14', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ad87d52b-a317-31a7-b960-c0879e297221', '9b32bd69-342d-38f4-99c3-f69019351751', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('84bcce54-4b14-3c9f-af6c-ee57cee238b7', '9b32bd69-342d-38f4-99c3-f69019351751', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ab14b7ec-db8b-3a51-92c1-92acf49fb76a', '9b32bd69-342d-38f4-99c3-f69019351751', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5629da86-63b1-3b5e-9308-1151165476ab', '9b32bd69-342d-38f4-99c3-f69019351751', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6a836408-ab1b-328e-b2cd-cda38e0c25ff', '9b32bd69-342d-38f4-99c3-f69019351751', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bfacaa35-30b9-3473-baf5-a82537ba0159', '9b32bd69-342d-38f4-99c3-f69019351751', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d7378b31-d1a2-31cc-9b6c-2cd13ede607c', '9b32bd69-342d-38f4-99c3-f69019351751', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3bddcf7a-29ec-3d35-82d6-debaf2c87360', '9b32bd69-342d-38f4-99c3-f69019351751', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('86d41090-f077-3a56-b58f-7015a3ede623', '9b32bd69-342d-38f4-99c3-f69019351751', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9d69e53e-5b7d-3ab7-82c2-d01ce4306dd5', '9b32bd69-342d-38f4-99c3-f69019351751', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7057c8fa-4bb0-3b71-9813-586e2b2c00dc', '9b32bd69-342d-38f4-99c3-f69019351751', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('be1a5aee-dc17-38cc-bec1-4dbcd19aa6cb', '9b32bd69-342d-38f4-99c3-f69019351751', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('afc82379-e8f1-3019-aded-10b396bb32fb', '9b32bd69-342d-38f4-99c3-f69019351751', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('da1fc6ff-76f4-3cd7-8c58-c7958458c01b', '9b32bd69-342d-38f4-99c3-f69019351751', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4ea7da10-aeb3-3dfc-95ce-667891cb9897', '9b32bd69-342d-38f4-99c3-f69019351751', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('db82b6a3-da47-382f-b34e-d4aff156e02f', '9b32bd69-342d-38f4-99c3-f69019351751', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c4bc3428-e004-3d74-9357-1f8e28835acc', '9b32bd69-342d-38f4-99c3-f69019351751', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4cf568c5-fa0a-3ee7-aa1f-600cd44c5ec5', '9b32bd69-342d-38f4-99c3-f69019351751', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', '7589a123-6728-3310-b5f0-87d0d514cac5', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d5e0fd15-cbda-327f-866a-0ba37df2c03c', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bc2522bd-256b-3a08-aea0-f3364e5555a6', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('acecacaa-1479-3a62-ad6c-ed4b8c10f0b7', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a22a0db8-02f7-3bce-bec5-3f48ded316a3', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7668c2c7-a13b-3b59-9e40-431bea006ee0', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f9f18cbb-3ad4-3852-afe2-b3d3fdf8ba1f', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('dd07c143-ffaf-3bda-b24f-886ce91b3732', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('facf887f-4688-3841-9bdc-efb93e34913c', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('94ed60cf-a613-3419-bffc-c8fbd6c6ee29', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9b34ffbd-472e-33eb-ad13-ea2f5976a584', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('23980392-3a2c-313f-b6e1-8d305ba82e0c', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a9ab1984-e10c-364e-8552-529ad720bcdd', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bf1228f9-37b5-3ad9-8801-4d1d9a7bfbdf', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0c6347ae-83fb-3e4d-8491-9f02899e15ed', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d80f4298-1b61-3cdd-8a02-62b045c77e95', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('89c4ede2-d109-39a1-a927-91ef778a4a16', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2013da7f-9d56-357e-91ee-5923b0387b00', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d8a846a1-54ec-39a8-877b-3919810ee527', '895ceb9f-1e7b-3b59-9e2e-878d2c1eda4b', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', '6b7fc3b3-25c0-3512-bd61-65bf96e79f67', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0af52297-a6d7-3bd6-9bc6-bdf0432df0bc', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('068aa554-9d03-350c-877e-60ffe11de1cb', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7b6e774a-6aac-3c31-bd48-fa279e86f8e9', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('93971619-5ca3-3721-83b1-a36f9449472e', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('19de5f7a-fe4b-3bc4-b58d-0e69df447ac0', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('09fa1285-9854-34d5-a038-75c308f99ec9', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6eb2cafd-dae2-3382-92c4-e38a30cd9049', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e4772763-c24d-3ab7-b258-87675268edcf', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('eda5ce3c-39df-3bd3-8c27-97b28df0bac1', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9c2ec850-3db0-3125-a08c-07594a0de66d', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('83cd41df-ad35-3b2c-92f5-69751a345bf2', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('318f1770-0329-3f0c-8f3e-6b0fe08df611', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c9609af3-cfec-3c67-be60-26603ae628f1', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8782fea9-39b1-3539-9514-dfaf75f2d393', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('87c40bd3-802e-39d6-8b93-23bab09d5577', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6771b6c3-533e-355a-9a87-2ce626e744c2', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('854e2293-b55e-3e11-98fb-b248b49cf39f', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6134947f-22ba-3543-8735-923925e2c759', '86f5e1d8-3ea8-38f7-9da9-bcd68340eee5', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', '4d0d1768-0572-30f2-a1fb-6bc37b29090f', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ab8f3efa-e676-391e-a050-f69fc54feb91', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c451c0f7-8ac1-3fe9-9b71-c6ed2af1ee34', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6128c95f-955b-3856-9655-8f642896cc8c', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('97b736bf-0b61-3ebd-9b3e-8af4b1ba303d', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a2868c71-4d12-3618-900d-03f48b80122c', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('52cae15c-b8dd-37bf-807a-543195311336', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('17bacf56-c8f9-3481-8d86-a2b79a87ea04', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('748cd2f4-a35d-3dca-8a0d-a544ba9ad860', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('056406ab-d37d-3187-ac25-606bfc03f266', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e6d394ec-b057-3af1-9ba6-af46a9060032', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6c67479f-c4c7-3648-8835-75b940470583', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1a336d9f-fbb8-3130-8a1b-fe6742699ee1', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('144e3d86-4982-3638-a160-3585e62a0998', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a364d59e-0874-3dbf-9d9e-2ccb52272dcd', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a6fa0023-88d0-318d-b554-e7becaffadff', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e553f248-7dd2-38bf-ac64-e43500311fb0', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b74cef13-cb64-3170-a12a-f754d2cf63c9', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f42581b6-263a-3a29-b3a0-011bf42fab8e', 'fc7a4e1b-dde4-3c5f-bc9f-8db14355f0ff', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('c1ec3386-588d-3128-9fe5-0b2499751376', 'fb70900a-3666-3cff-ad6d-4d827638a1b7', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4d325004-91d7-3eaf-a7e4-627b43760ff2', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3b0559d8-5623-36fb-a6ea-125af474e0a0', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('18df4592-e6ee-3c93-8a76-14dd70d6eea4', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6e495c28-8c53-3ab4-b4e1-4092b1013f4d', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('90dc211e-f292-342e-8959-d644792e21a3', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('99ac88dd-808c-3582-b897-ff8891f7862c', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6014e995-69ca-3424-99f2-91574831b899', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('be40b7f5-e868-33df-9d80-90405aa27752', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('78c3fae6-345b-32b6-8b42-dcad2e8944a1', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('062a42ad-abcc-3fc4-ba9e-7e890a9f99fb', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('11ccac16-e22a-37a7-b0df-e89e4ba37cb6', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f17cfd94-8a73-33e9-91c2-c354423419ac', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8346eb76-d6b4-3c61-93d8-464b99a369e5', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('dadea3c9-eb2d-304e-8311-ca6ee4c71cb0', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bdab5ab4-3222-31db-b817-dfe330f850f8', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e39700d6-3d88-3e36-ab68-7f04604587fb', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5b50494c-1bed-39cc-a30d-f4131b24c001', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('12c09157-2f04-3c9b-9b5d-c3d6b83c80dc', 'c1ec3386-588d-3128-9fe5-0b2499751376', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('d00f88d1-f8e2-3099-bfcd-6d50ffb50426', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ef95b20d-1499-3dc9-844d-a7f44dbb2ffe', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fa583954-30ee-3db2-a2c9-8f3fdafb64fa', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4c0b7059-516e-36ac-ace4-1f2465f544d0', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c87a5f7e-508a-362f-a94d-0496b1d5144a', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('34dd9f07-ed8c-335d-9105-ede82a36d711', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('59dca1d8-cbad-3d13-9218-01b88dddc295', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('92522170-ff83-3817-91ea-0fa16711c22c', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6c76dfee-037f-3317-9d25-121c2ed87313', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('618ed170-252e-3703-be29-1a6359bf4e35', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('40c1ca8e-3b59-3c6c-bd6b-29eb144c2d92', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('da846f2f-72b8-37ff-a02d-e9f106cfc092', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8c5fc266-eabc-3892-a47c-bbd63f9355c0', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('968f769d-a6a7-30ea-9cf7-f1bde7cd5e47', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('02d042f9-48c7-348f-8020-dd2c0cfac762', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8c821411-1b57-3461-a77a-1b4bf740cffd', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('945d07b2-4452-37a8-bf0f-740d6054aff5', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f699857e-ff72-32d2-8719-82ffde016548', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d8529c77-7453-3766-9400-0ccee4242375', 'd00f88d1-f8e2-3099-bfcd-6d50ffb50426', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'e08d3f76-f638-368f-b699-ebb68ccad2cc', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fa64b848-902b-3dde-aae0-f37ef2f39927', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9fbddde4-0f85-37a4-905a-1ec7d653322b', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ca95796d-4ac0-37d8-942c-720deca750be', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bc722f93-391c-3391-9ca1-97ef8af106dd', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('09ff313c-e5ac-3857-b152-e76c492e43b0', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('49b32edb-974e-384a-84ad-da28d630362d', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8af4642e-2d5b-35cd-a45b-097961e0b647', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6c22ed3e-fcb5-3bf1-85d3-99e67d84928d', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6b4f015e-6478-34ef-9349-7fb5531dbfa9', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('76a7a81f-7af5-34b8-93c7-453c2636f324', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4a335aef-d42f-3fc1-b91c-95368bb4a548', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('639c0eac-bb43-3a37-b745-1cf3276a853a', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9813a644-fb17-3619-b3a0-fd999342eb6a', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b14df44e-b60e-3a8f-9787-901ba7f286b8', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('275edbe3-10ac-31a1-88ab-374b9bdf54dc', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9c6bd7fe-7ded-3088-b269-0646bfb53278', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a0092184-d669-31f6-9b0c-8e6cc45c2a41', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3174c62b-2449-3b11-84cb-13099f7de208', '5ef677f5-e1eb-3c7a-8c31-763f1a4cc503', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('5ec09713-0d39-3a27-aa45-f4e587928e84', '025e3c8a-cba6-39cd-bf57-58218839b82e', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f15d1fa2-ea62-3696-9afa-4c78f34bbbf5', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bbf1c88d-3df2-3071-a68b-ebf53b1eee0a', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('023c6b92-16d5-3c70-978e-f1c7bddfef65', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a04ac447-7e0a-3a55-8db3-9df698e5dd20', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1cb79896-e144-3b0f-b416-5b9462bca30c', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f7a4fe9c-d2c6-377d-a4b3-9a0f98d41955', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fcbf3e3d-de5b-373a-a9fd-f1462f1f726d', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5e43f61b-46ad-3470-9406-074519f81bd7', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('616a169d-3f5e-398a-9e76-69da615c74ce', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('eb74fba2-9f71-3247-a1e2-2b1ec5261372', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2e0929ae-ce84-3fd3-bf10-6b94a599f40e', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1a6dcede-05af-3c4f-82c8-752221cba8b1', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('09bf1fd6-98ea-3980-a880-2e676499ddae', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6864a0f3-5577-3441-a5bd-67c373bbb258', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3841084f-8bf4-3ffb-9b9b-2a62bef4cf37', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8ffdeb76-f52f-3f3f-9204-8cd7a81b835e', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0fce4694-14b5-331f-9a57-7f23af1081cc', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b755ca33-2176-34c2-a7c7-365f9683d655', '5ec09713-0d39-3a27-aa45-f4e587928e84', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('95069ca4-8042-3919-9e27-2b4421d6d007', '7c255813-eade-3fc6-be86-1935ac089ddd', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2ef7fca0-9d5e-314d-875b-9ba406547f46', '95069ca4-8042-3919-9e27-2b4421d6d007', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('579dd732-72d5-3d41-9d66-e962cf23fdaf', '95069ca4-8042-3919-9e27-2b4421d6d007', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ac4f4f9e-f75c-39dc-a653-72e882157159', '95069ca4-8042-3919-9e27-2b4421d6d007', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7f22ca8b-c914-32e0-9001-1d75e8d9b44f', '95069ca4-8042-3919-9e27-2b4421d6d007', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d3f3b7c6-1e74-3657-b5da-5ddda3306940', '95069ca4-8042-3919-9e27-2b4421d6d007', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1fe3c75e-9b5b-3a28-bc6c-5839a1d5fb5d', '95069ca4-8042-3919-9e27-2b4421d6d007', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c0891633-3c9d-3e38-b14b-8704b6ee744c', '95069ca4-8042-3919-9e27-2b4421d6d007', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3bae9ccf-a976-3eae-a2ae-4e83ec76fde5', '95069ca4-8042-3919-9e27-2b4421d6d007', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6ef6a020-eddb-35e9-b36f-07d30dac3fe4', '95069ca4-8042-3919-9e27-2b4421d6d007', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d5f1a9dd-0980-3241-8f02-de6278ead345', '95069ca4-8042-3919-9e27-2b4421d6d007', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c066fa26-d506-3b5f-9d4f-fe9db213f93e', '95069ca4-8042-3919-9e27-2b4421d6d007', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d824b0d7-6e1a-357b-b6dc-dcb88974b4c5', '95069ca4-8042-3919-9e27-2b4421d6d007', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cadba31a-7a75-314a-a25f-30b155a78b1c', '95069ca4-8042-3919-9e27-2b4421d6d007', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cf4f0102-aed2-36ad-a12e-3965daae2f1c', '95069ca4-8042-3919-9e27-2b4421d6d007', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('19b5242a-8b99-3b07-aa82-1a2d22aada41', '95069ca4-8042-3919-9e27-2b4421d6d007', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('21d6b95b-c078-3e54-9500-8f55fd984e12', '95069ca4-8042-3919-9e27-2b4421d6d007', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('108bce70-6cf8-3257-97d1-67b76b3344c8', '95069ca4-8042-3919-9e27-2b4421d6d007', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7af4849f-b17f-3bb7-97f8-c1ad6c87509c', '95069ca4-8042-3919-9e27-2b4421d6d007', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('82a2dd3e-1557-37c0-ac72-827355bf51ee', 'a1a20e49-bce3-39ac-98f8-162131991c48', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('40ba3507-54de-342f-a687-a5c5ec31d376', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('56d4b91f-ba9d-3cab-bef5-5aac8da21697', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9881e5f0-d966-329f-a0c5-c6964c8be9a9', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d5b3bc38-0adb-34c5-bf65-00f4d914f856', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('acbaf2c7-e558-3b1b-9f83-a5d11d2b0fa4', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('78e61581-a569-3793-8bfb-56b4583c9eb2', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8a3d3c8c-b004-35e6-a974-5328781c0a8e', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8eecc9f3-a15b-31ff-a61f-5f0304dab8a0', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f605e809-0d3f-31ba-a5b1-efcd7874713f', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5a4d3f29-485c-36d8-b4e8-210408c00216', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3207e01c-a1cd-3e02-b5bf-cd4d452a956a', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6e93605b-39e2-36db-8b01-0db6ea43eb2b', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6dbab723-502f-3a58-be00-c85bca9ebc95', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6eb18f7a-d627-3501-95d1-c5a6d8543e3c', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1d382119-cb92-3318-8b4d-159a3574385a', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('db0ce672-88a4-31d1-8e52-eacc60624919', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a47f9cae-d46f-30ea-a292-bd018ce86929', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4cade3c1-0136-3996-b7a5-8f00c6f4b429', '82a2dd3e-1557-37c0-ac72-827355bf51ee', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('803ea6e1-740c-317f-a455-67f6f4d61424', 'fc7272a0-e0f7-3e3f-8092-8404ddba723e', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('06f7ec4a-ddde-3bff-b3b9-2c1245622fc0', '803ea6e1-740c-317f-a455-67f6f4d61424', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('47b59145-6944-3226-9045-5b0429c8424e', '803ea6e1-740c-317f-a455-67f6f4d61424', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('44bd49c8-1aaf-3013-898f-3812ad0eed2f', '803ea6e1-740c-317f-a455-67f6f4d61424', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f1dd1ca8-c469-39a0-a79e-980a99430d6e', '803ea6e1-740c-317f-a455-67f6f4d61424', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('efa171f7-2640-33bb-9c8e-fd18bf004a98', '803ea6e1-740c-317f-a455-67f6f4d61424', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5dfd0b5f-4ef6-3ac1-a439-8abf8ea01a10', '803ea6e1-740c-317f-a455-67f6f4d61424', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e9c4787a-1d40-3e9d-b309-b2a3e6195a87', '803ea6e1-740c-317f-a455-67f6f4d61424', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ccf171c1-5a00-3511-a7d6-f4658d28130f', '803ea6e1-740c-317f-a455-67f6f4d61424', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b4bdc803-6e7f-3346-82c6-1a8b04db59e4', '803ea6e1-740c-317f-a455-67f6f4d61424', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('526fc82f-6d82-3c21-92a3-e5c4536abdc3', '803ea6e1-740c-317f-a455-67f6f4d61424', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('43da1306-cda8-32cf-95fe-d8a17bab9477', '803ea6e1-740c-317f-a455-67f6f4d61424', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('33c63556-6519-3d10-a3cf-d9325978baad', '803ea6e1-740c-317f-a455-67f6f4d61424', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a468c666-9495-3b5c-b5b0-eb08d37c5aeb', '803ea6e1-740c-317f-a455-67f6f4d61424', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b86c2ab5-2a20-32db-b6f3-5f632ece365c', '803ea6e1-740c-317f-a455-67f6f4d61424', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('71ed11b0-1146-3fb4-9f40-6b9e321fddb3', '803ea6e1-740c-317f-a455-67f6f4d61424', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('90a76bb6-dfb0-3c01-9fd1-3b0d3214ac7b', '803ea6e1-740c-317f-a455-67f6f4d61424', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('26d389fb-1e2e-3b76-a39a-14969aee87b8', '803ea6e1-740c-317f-a455-67f6f4d61424', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('378f8033-5d85-3c87-89bc-58f846c6ea9b', '803ea6e1-740c-317f-a455-67f6f4d61424', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('a8a4d850-064a-3908-ad7c-9daf1743db8b', 'c6258e3d-0693-3248-94bc-8d455560be75', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b2651538-f96c-397b-a330-ec10ae8fd2c0', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2a6c8da0-e5a0-36e8-b0a3-e235f18e5eb8', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f8daf72a-98ef-359e-8781-91c6b32c1674', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b6165be5-e37e-3cc0-90ed-fcc0085a9e34', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('288786dd-09ad-348d-aeb0-011d708504b5', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3b90a840-cdf7-3d05-8cb6-875c7f619118', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cc713b44-b9d9-3052-a432-41a05c3ab2c4', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('048c62db-a22e-3246-81ff-1942b8b29b34', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('926345ea-3875-3712-bf45-93c4da0aed56', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1d8967ec-a071-30ef-9d2d-fcb5034cd48a', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8c61de44-f1fc-3906-9905-7302d55e93b0', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('da4fadd2-681c-3b93-b5bc-f395f4106ce8', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('49e640d2-f45a-30b8-ad50-cc8dfddd4ec9', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8d5f3d7a-fead-33f3-9a68-0a05f4802247', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0a6756b0-5bc2-30bf-a900-5061c58faee2', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('56866579-4a6e-3081-b5a1-446f43f6f5f2', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1396f3db-a4a2-30a1-af74-895fc7cfbb0d', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('05f68f3f-03e4-3215-b710-852ce2c02a03', 'a8a4d850-064a-3908-ad7c-9daf1743db8b', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('f940a662-89e3-38fa-b3fd-8bd36ef5c53a', '556b4fcf-2c70-3cd8-b4c5-9e79f120317b', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d54bd06a-9291-3593-a6a4-056e24e801b2', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('23633da6-e9b1-35a5-a43b-891f5f276ed1', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('37267f44-f335-39aa-be90-6ee44ba36682', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a9706a35-f7c0-3507-bb1c-f4e833676c88', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4da7a9d6-21ed-39f0-8f9f-e6f0bdcdd4fc', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8f58e12b-c8d5-3c57-9db6-49e4e9c1bb8e', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bb9b94cf-f7e4-332c-82e8-b8efe7ffb48d', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d8871ea5-04cd-3bac-80b3-8d2c4aa5cc3d', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('20286238-698a-3d91-9448-cfb65b8346dc', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('52cd7d02-5c70-3b7c-8c01-7dd04eadf9f9', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('729e5071-4bab-38fd-9de9-42a6ae34f70f', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('76ce8953-a730-31d8-b591-2e8a6c244ddd', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('19d267ca-114a-3be2-ad30-0dca061aa946', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d82a0c2f-66ef-3003-9a3e-5107435c3edd', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c68e3f3b-81a1-3438-ae74-2f9ab0e73599', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('04e62f38-e19d-3dda-9d6e-9da3c8978779', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('edd871dc-ea0d-3533-9c3b-e43535f681a3', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4c201da2-8c39-38c0-a770-721794949a30', 'f940a662-89e3-38fa-b3fd-8bd36ef5c53a', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'b0e1b520-700c-3136-a849-6fb348890d68', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('babc5606-0aa8-3bdd-985f-99d1c715fe93', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d4aa60af-6eb5-3a7f-95dc-b6f2664789de', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6bcdd0bc-1485-3fa0-ac1a-26207b56ae25', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ce966f58-5962-3b2a-99ae-6ef28f820a7c', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c298bc55-f314-3f2c-8937-fa43b1e32c49', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e4e1e5d1-5caa-32e9-986f-1b6d532e19d9', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('45a0304c-c4c1-3b13-a59f-ed65a6c6a8c1', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c78499bf-d575-3e10-8e0d-0d81245cc3d7', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('def4fa7d-06e5-3619-85f4-4d742fc82b99', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f55bab7c-312f-3363-84af-05ee4450950e', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d159aefb-0966-34ea-a3b8-5c7f98780be6', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7295a86a-58fe-3d3c-9671-950c631b899e', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d4314bc2-03b6-3e27-a855-edc2adcf253e', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b26b276d-6128-37ab-9326-d1367dc6bca4', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('683cdb0f-4d5d-3aaa-a11d-6470466694c2', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a878c38b-d6bc-3f26-baae-7ada9c25ea64', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('53390e0e-10dd-3be8-bfcb-16b496069ebc', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4fc67b57-946d-3e9d-8378-8a7d063467b1', '18c23a60-7991-3fa5-8aeb-5fb83d1685c4', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('04d92570-8b03-38e5-8d8a-e96bbf018c9f', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f61151db-5064-3981-8595-6462b9215a4b', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a5184c6f-ff99-38aa-9251-26c03caef352', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('23883008-67f9-3f59-aae0-c4d0cb858228', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a3a7f888-7254-398d-9793-42796edddf4d', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f8ff0ba4-51eb-36e9-9a29-244b7b9bc632', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f711f806-30fe-3c21-a7db-073d576d4aab', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('256f1635-3f66-322a-938a-67954935326c', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('eb4cc392-e97b-32b2-a85a-eff238f425b0', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('25cfaedc-b32e-3502-a775-0a8d3e68e7c4', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e94d9479-ebb0-35f2-8878-b1a83bc7cec4', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5ba260b2-8290-3f2b-8346-522c53754ae9', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ea4257c0-9387-3548-9ab0-a2edd27fa50f', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fb5f0950-a0aa-39b9-a5cf-d72cb985187c', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ad6f7d3e-a7df-318e-b8a0-537abe7dbc4a', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3d473d50-316b-3d51-a38b-53f91187e98a', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('042bfee4-54ab-3581-ad6c-d40f80c3456d', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('47ca8a5e-9363-38ab-8480-e84a907e99ea', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cd61b663-9620-3285-aeff-b71e306431b4', '04d92570-8b03-38e5-8d8a-e96bbf018c9f', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('96e277d1-a241-3d35-bdd0-c5880d7ba944', 'c47aaa91-83ae-37da-a7c8-2c85c2b2a2c2', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('82c121a5-043e-3e3c-9e91-d76e49897604', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('195181e9-90a1-30b0-ad1f-adb537d27dfd', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('643371b0-15a6-3f36-9e45-896bf78cd91f', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f46d1cab-5fe8-34cb-b958-24e419f36ca6', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5388bad4-ea70-3218-b620-f322c857a020', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2061cf94-ac59-34ad-b772-11abb1ac328d', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('028966e4-8cea-3a4b-a107-13a14baebbf7', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5f71f8df-0b50-3193-bc66-a5eceea39785', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('84ed4735-4862-3fa1-a5c9-361b1650b1d9', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3b3513ef-3337-3c2e-a660-5d0884998257', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6a443f58-386f-3b1f-8eea-c61ad3bcb5ed', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('00640dda-319d-3ea8-861e-cada97591cfa', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8a0ad0d4-ea9a-3e34-8bf0-cca838ec024c', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d515950b-72b4-311e-a908-c9b8ec1142c0', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b7a221a3-742a-3e79-b59c-9bbf053787d2', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('aac55d48-c59f-3cdc-b085-a0f2da889eb9', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2a2da6e5-e08a-33ba-89d8-e90ab89a5b1e', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7df07eec-067e-36ac-bf6b-100a15980b96', '96e277d1-a241-3d35-bdd0-c5880d7ba944', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0e61cbc1-ab09-31b5-a005-0758346bf2a2', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('42bae94f-d188-38d3-9f4c-315f88477919', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'PH_OBSERVATION', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e375262c-ec03-3b71-8aba-41070069d46b', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f53012d5-392d-3135-8fae-a6b62c917eda', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a6505061-8be8-31c8-8ec4-553106b09ade', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('656b8cb0-9c2b-30e2-8bb8-4e7d5a80c779', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bea59ed4-e64f-36e1-8cd1-8974232fe416', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ccc24432-77fe-3818-a642-8d912c5921ec', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8e7f9066-e3e1-3c9b-8947-e44574402ca0', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('35e7ca7e-3f59-3e4b-9701-23b19d45fe2f', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9038ce9d-6e45-3cf4-aa8a-72625b6d0fee', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'SOLUBILITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('17361746-2434-370b-934a-4d6f737a9ba9', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'DENSITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3d4239dc-c7b7-3ac8-a88c-20127e73a4fc', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('443c7345-ad53-3b64-a4d8-852e82bcfd4c', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'MELTING', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('71591a7c-50c0-344b-b7aa-4fc13f4cf3e2', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8d49cdb3-3bfe-3096-a352-3c025e488a23', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'POLARITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('312acd3a-b068-36cd-911f-4a20995e948c', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ec6a0138-e8ba-3077-bae0-9f50351a1635', 'ad9bc16c-8af2-3190-acb1-fa8cec0eddc8', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', '99b9b775-d82e-39ce-ad60-26b7ae2490ac', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e56a4ef6-e6df-3e25-9af2-f3f37fcd5491', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('69532b44-34c1-3751-bbca-0eeab79d2c29', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e2d09823-716e-34d9-8e21-c2857df56e3c', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('10bf0ee5-eac8-37ef-a8ef-bcdc29e2342d', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4606f9de-e34c-3509-b245-76124db1fd2d', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('824e05f1-23ce-3578-b30e-6fa8b4a4505e', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8f4d14a2-eae5-3cb9-acde-6f0d095403e9', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c72415cf-0496-3296-99d9-3080d8884950', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1891c913-6a28-3a29-827f-f2a8a621d6f4', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('31b84f3d-e12f-3c3c-a1f2-822fd0f77b0c', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('99c31f27-27f6-351b-ad5e-087d8b5ed9a6', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a533a6f6-6532-328c-b85f-a5f18f91d25a', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fdcbef23-7c37-361a-9dce-6e1782d7e315', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ea132b15-4018-3d1f-bde5-e4cc30564e16', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5221871e-f535-3249-8d14-b5bf3030b372', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fa568fdd-2de8-35ab-86cc-e936ed9c4128', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('22bd8582-3221-3370-9fca-24999d4b4d18', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('63d125b7-77c0-3f0e-bea2-f4e67cd250e6', 'b8acacf1-6ce1-3f7e-b0dc-9f067bd3587f', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('584fb294-1ab6-3d19-82ca-4f1bba49332b', '5294140a-5234-3cb6-ae81-635a2260a114', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3ada2219-1363-3224-a5a6-d7750c4220fe', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('08f1dde4-2d30-3b5f-8812-109ed7934d31', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c5e19a7d-5c50-3e25-97e6-fc187874d619', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e54f7403-968d-31ca-b640-77cd2d2c5ff1', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3fdcdfda-a9a3-3f00-ae43-9fc1772d8759', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('122b6f6c-c1cd-30a8-9a1b-533b60c36db4', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b831a269-93b6-3701-bfec-3e1bb0692731', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6769c3a5-10f9-34a1-b3fb-70b3f295352f', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d44e50b1-7d1b-3914-814d-73caf25a34aa', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fe22dec2-5e54-3989-8214-42ff682720ee', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4271fd66-5abb-362e-9c84-941d8b25e5e2', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('16cb0a01-94aa-32d3-b7c2-e8b6569d1918', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6dbd0864-7657-33bd-9db0-df6a66f29ce7', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9e850e06-50f9-3579-a20e-deffd66614cf', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('253dcf4c-0209-3bc8-af22-ebf802d2d842', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('97162b11-4202-3dfc-922b-718c6f9710a3', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ecfaf470-ae34-3796-a828-393ae0d71a7b', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cca3677c-a2dd-3b83-ac91-3f1ecddbe4aa', '584fb294-1ab6-3d19-82ca-4f1bba49332b', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('868f3f02-7b48-3138-a7a5-33beffe163d6', 'e7ccb540-00ab-3c13-8397-06fc3bded8ef', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bbed4e43-d515-3465-92fd-30e9ec63acf3', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0c04b6a2-d304-338a-b98d-bbf8e9f75824', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('826d8e5a-8e9d-33f5-8e9c-e0cce1e703e6', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4f9603c4-ec10-3569-ab8b-cb2bd71e9d55', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1fc2f4f0-045a-35e8-96df-9b28e4097c52', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('382a1c7c-a522-3e20-9d21-4d9df4599da2', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('23435e22-0e01-3381-8077-df7eef34f090', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0afd30ad-2415-3dce-93ba-0dbf6c34286d', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9e8a0240-7f58-34cd-beba-1f2ebdf06c2e', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ed6c5878-fa32-34db-830f-1aaa3d05482f', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bc315ab9-8ab6-38d7-bf2f-2f3e158f855a', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('454719cb-eba2-3fac-ab20-59aa6019caa3', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('719fc730-aad8-3051-9c51-a604ebdc8094', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7cec72a9-b679-312d-9a14-67c9a4a31fad', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('87a9ff97-b791-31a9-befd-c892c6c4e2a7', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1cf731d1-9b60-3f04-82bc-2eec8e76d631', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d4076d06-1272-3cdd-9a97-f153edf21561', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('dc405509-c288-3abb-b61e-b9745ffa545c', '868f3f02-7b48-3138-a7a5-33beffe163d6', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('6c1fec92-c3c3-340a-a9b4-ea7f208f1903', '1c7585e5-613d-3411-a3e5-cf08960aae4a', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9b83795a-9942-3880-8389-430aeffbb3d0', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1dbf463f-8115-31b6-a816-09207a3f2049', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7dbca028-a4ff-30b5-91b1-6d9e5aba06f0', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('de5986c6-68e4-3205-9f1a-e483ea5cdcb2', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6d3644e5-eb7d-3ffe-a8ac-53d72890e0c7', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d96bee8c-243f-3bbe-9338-9f8b2c8485bf', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('56e0c629-38d8-3ff9-94dc-4ebda8833c34', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('233c4d19-d8c7-30e1-8e78-3a5e801c642f', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7b1653a1-0e63-3c19-b1db-9307905b46b9', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5a78245c-00a8-32d7-bd93-bcb3663ef6e9', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8e8f9969-715f-39d1-8b1e-04718b0b673c', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9bbba75f-875e-3606-a01c-23008425d589', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('279ff712-e6da-3244-8474-e1f80104b12c', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bf75eaee-117b-3cee-9371-a426ad59aaf2', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ea3f0829-af16-3067-b36e-8a11527a0279', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('edd5f777-415a-3503-b91e-3b0a57e76f18', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('22abf335-57fc-3a6e-b55c-7e5f60881623', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f922f315-fc77-3027-bbae-e2965b290332', '6c1fec92-c3c3-340a-a9b4-ea7f208f1903', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'ca1f3c39-3ecd-3bc7-9758-ddf0513e504d', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f030ce38-10a8-3175-9014-f4e633ab51da', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e36e9ad3-9bc7-3a5f-90a5-38f4f6a2ae69', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cc1c136a-1388-3c7d-bdbb-e7a78ae817bf', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('db82613f-4e40-3a58-b458-cfc513f490c2', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('db416723-9cb1-3d83-91c0-0d592888a35e', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3061202d-c504-3547-9153-5b64c09f608c', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('061daf22-0750-34bc-9a25-9bac29e0e4c7', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e9a55b81-8451-3002-a8d6-9a3c786ab7be', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('61de2817-0a12-350e-89b3-4c18c8f7c940', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6ac45923-ac54-3f4d-b2e8-0b2b9e34f2ad', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ca584a6f-2efa-396f-a1c9-941f805df11a', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d617647b-2a79-31fa-927d-32e59853cf76', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('adebe294-d4eb-316a-8333-dbe7c9d8502a', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('83477c1f-b075-3276-b33b-7264c38b5572', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c08119c5-1218-3fe2-a2d0-3cfd84a4daac', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1eea95e8-b0bc-3203-8028-49fdabf59fca', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('74b4b992-bfc2-3de6-9d1b-118c6b79aea9', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ae496a5b-9eb2-3ce2-8238-91590ae971d6', 'c35a46e7-0f70-3e62-bb01-eb0c218964ea', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('82cc2c1c-db83-3d21-8908-1639c6b53965', '41ad250b-399a-344c-94b2-13a3a63bc28d', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('958fd506-06d1-3ec9-947f-65d6145b7437', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('040ab839-9bd4-3677-bb59-717cbc1a6373', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3d23bde6-1334-34c2-8c4c-b64421d2f85d', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('edc3e0af-5281-3273-97b4-0801ccdfd6d5', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('601058a3-4a8f-3c8c-b566-39bd1be3d555', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1e711ddb-3060-3d9a-9a6f-84fcd1efed33', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4fa0b5e2-988c-3ae3-8ed4-59b52679c2b3', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a43c87be-2d2c-3716-8f6b-317995d5f866', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c7a58cb8-e7fe-3e2f-8ce4-eaf5cd34c3bb', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8f121075-3ba3-32de-9f20-d314bb42f831', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('33de4cda-157e-316a-88ff-d230eec6846e', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d18b8ab0-1f4d-376f-9e53-01d222e7ece8', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ca5d579b-154f-382f-93a9-75fa20b0f485', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b7dd629a-6f6c-388f-95ea-38067b39d385', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('924f0b74-2b0a-37fe-ba6a-71bda48f81f1', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5f004d18-a239-375f-9842-0f3ab7c1efa3', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a83d1f71-5e5b-3205-96d3-f00f6e3e3b71', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('555728fe-1b2d-37db-8415-2ab39ecff044', '82cc2c1c-db83-3d21-8908-1639c6b53965', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('ea4a59b2-b285-30e6-9e71-578a4421cebc', '5c5ee053-0520-3976-85d9-78b89adff2e9', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d51436c3-16f6-3629-8685-4fb488ffbc1b', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('64fcaa63-7e31-31f4-a1f7-e0fd0e35ed6a', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d5b4522d-56e6-389b-834c-509227d30610', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5c0324f7-9f09-3365-b548-a4c7752b52d7', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ca828dfd-81cf-3d16-b1bb-0c4c5359afcc', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b4d7a856-5174-3cbd-b6bb-b6280b233e49', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b0d3ead5-d3d4-3425-8660-b6bc36a79172', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0d52381f-69f6-3a43-89de-bb6bfb456961', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('dc551d84-3b27-3dcf-beef-5535aff6f04f', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ef1330d7-b3b4-36b2-a134-e073a39b56ef', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2f72d2bf-df91-35ae-b59f-d9d90a40bbf6', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a2381583-40b7-33ee-bb8e-6eed86fefb43', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b371f4bc-96aa-391f-b326-d51837a0214a', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('81d1f0ab-a2da-39aa-b874-beda7a4f1986', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('80835b2b-6155-3555-b7c8-982790b0f1ed', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0ac48a59-b30c-3f58-875e-40c4327c128b', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b54942b4-7d04-3ea0-a9de-55c75f84f5bf', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f5b6b195-d49a-3226-a066-468a096271ff', 'ea4a59b2-b285-30e6-9e71-578a4421cebc', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('991c0c63-ff90-39db-85f8-5fb490185103', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a76b6c41-8e6c-332f-a1aa-34c68112f51a', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d1145611-909f-3df4-a840-a4eec57a1522', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8c65bb4c-16e9-337d-a1db-afe4f96e15f4', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ae8fae84-f45e-35e9-b630-2785fb8cefbe', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a6966caf-4db7-3d0f-833d-23cc9f61b034', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7b3ed17d-936e-3bac-aa1d-fb2e35769682', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('335b6e6e-87e6-30fd-b85d-39d90b5a6de8', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('be6b0cc6-2dd2-35c2-a779-683efa1a6a8a', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2449f2e0-7eb9-3fef-89de-3ae266565bd3', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e1fa7736-0a73-3027-80c6-7bb2b2bb96a6', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a6e330e3-b6e0-3daa-a3f3-d192f7aeaa66', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6b28e169-bd0c-356b-8506-70f06bc1be96', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8b6f5113-540f-3e50-ba99-fb32ade5ad48', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b097c1a0-7310-3be5-b4f4-1cfe66f596c4', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('05df0fc5-c519-3153-b0fa-98c740c3418e', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('29f07def-1008-34df-95bb-0c86479a0b39', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7b6c8216-3e22-30fc-b11b-59ab92f53fbf', '6d7d2266-b5b5-3a8a-b107-c9b04ab016d9', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('c79258d4-f9e4-3054-9c09-72f5a8269513', 'b77d9820-d830-3461-902b-bbe170a40038', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e8e54543-e8e8-3d44-9303-1282f0d47a33', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('452e6a55-1734-36a8-9bc4-45c57a498a2b', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f96fd62c-ab54-302c-826d-bf398a01ad91', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('656fa743-7938-3683-b203-59a8db0ef7b3', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('09e5af5a-27f3-32bc-b587-e1ba217fbee7', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1c19a8b7-d4d3-3525-8fed-3edf6891cc6b', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2e633bca-e1a8-332e-8ff5-ab02f6157560', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('47f19d25-7f1d-38a2-8ffb-41b723aaff35', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('460bf9c8-7ed4-363e-b3d2-9375333d9bbd', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b93f87d7-c88b-36a2-a20d-b12da8395735', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('60759c03-dd10-318d-bb16-8f4831151d35', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('67334835-b909-334f-bfa3-bd4fc4a973d6', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f0ed61e7-ed16-380c-a948-d363e66953e7', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1200660a-559a-3a43-9397-307743fc2720', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('26f6fa55-79eb-3f4e-880d-5605d0b5275f', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3e2c5d15-f00d-379c-83ea-1f5ad4891bf2', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c967e3a5-ba6c-3da8-9feb-e34e9f2f0866', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('28932fc7-1a65-34c8-885d-3b722b7c8bcb', 'c79258d4-f9e4-3054-9c09-72f5a8269513', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('952501d4-98ed-3433-bbfb-8d77435c69ae', 'f8a18806-7192-35ec-af10-9bd0afabcd91', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bdf661d3-c94c-3aba-932e-19b2e70c4050', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('038e2971-afe3-3d8f-b2ad-936ff4db8407', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('519d720b-5fcf-3fb8-93da-495770beeb32', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('913aca17-bee6-3112-bc51-758029f8ea5f', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d0698a4a-2dac-3636-88e1-8a673035888e', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('10e32be0-f652-30be-9b9e-6d831a6063d3', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('416a7b00-77ac-322e-aa70-f4c2cfea45c7', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a18ed7b2-cae5-34ed-a1af-7c2b9e4fcfa7', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9b9e8ba8-eb74-383a-898f-730713eaa6bf', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('91018ecf-6cd0-3365-8981-55968af9cb22', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('867172c2-33f1-34a3-9319-a3fb45eda0ec', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3a042198-e356-3dbf-a896-c0e072d23caa', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1844ce84-86fa-3943-9023-8daa54d7643c', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7bf9202f-c461-3c25-bb74-d6bbc567ec5e', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f96b2362-4e8f-340a-8de0-e25d7e0f4f61', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('600e1a1d-4483-3cda-aec2-c7a6a3cf4fb9', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ddc62b8c-796b-3905-8be3-c2d43d863566', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b70cf98e-b4c9-3b08-90e2-ce390e6bbfa2', '952501d4-98ed-3433-bbfb-8d77435c69ae', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('cad4adc8-b482-34f0-9727-c3adb27b4af3', 'f57d2b69-d3e5-3f85-908e-3a5648015836', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('20b62c1a-f327-3578-880c-6ab47af8af60', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('162e2bb0-6efb-3047-8112-918288d82ea5', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('08e12606-8864-3a37-80a2-84080ce473c4', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3996d4a7-f567-3ff8-82d3-b8eaf78c18b8', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('006e6794-4e22-313b-9afb-7253ff5bb605', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9f603de8-b319-313d-ba35-472490f68d96', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('60487602-0a81-3183-b026-cedc3fe07ef7', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('579360a8-4134-3ffc-b5ac-3d17bd7143b0', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2eab0595-d67e-3645-8fac-63d0845ba33a', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bc6fba56-ce00-33c8-8946-75c24746d4b9', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('79283a04-95c1-3f51-92cf-c36f0bce0ad3', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('80f5e43e-2177-389b-8c1d-f1b52925107e', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2b1b44d4-2df7-362d-a1fd-71a6fdae6f11', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5a2138aa-924b-3df0-9948-dd186d4656de', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('84a1be79-cce2-3639-abb7-2c8d48cdf809', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2e51e50c-99bb-3fb5-a758-4d1f88011695', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7606e6d6-8508-3f9d-804a-ccf35492570b', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a885e185-a754-3a35-9eb4-4c74b803e530', 'cad4adc8-b482-34f0-9727-c3adb27b4af3', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('594a0cf5-ce6d-316e-b704-b600043b6b69', 'b5497acf-a111-3d7e-8a2a-7e315764a440', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3e527a47-1e8b-3c1c-94f7-6eb938209e05', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6cdf09df-0676-3fe5-b8f7-7c2118aed0ad', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3df8b027-4bec-3e5b-aaa0-71ec0f4fa25a', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('78da01d7-c8e1-375b-ac5d-0ff28c79c8fd', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('47ffa42a-6762-319e-aafb-f7111370773e', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('583ccfda-1056-3422-a5ee-e92fb3fc15e0', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('af406051-9c99-3456-bf8c-228e59bf9fc6', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('47753b4f-9d1e-36ad-bfca-5020b11570f3', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('425f68b8-7078-32ae-abd2-5bbb31e3e649', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b8a1b029-a52d-3969-8f0c-72efda299f08', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('05988ea9-dcfd-3d6a-99a8-b6e93c401c94', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1b7b9e90-049f-34a8-8f7f-1e57f153b118', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c624c2ca-e25e-3e4d-afb7-63d87662a279', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('994594e0-522a-3c46-947b-181a9cb3af91', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('211a9cc8-a3b7-3b9d-a5d1-982343d47048', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8815de7c-39f1-3070-959b-7661027cc647', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ace261f2-d3e1-3dae-be78-8f9756d06581', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3ed9175c-cb34-3756-9aad-4a03d2f038cf', '594a0cf5-ce6d-316e-b704-b600043b6b69', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('cd0f9866-9679-31cb-bb89-a124a538bf58', '73d6dd57-d6ed-3fa5-8580-b636040b00a1', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cd35709e-353b-3e05-994a-513218fdb6c2', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3966b854-c4cf-3d4b-8d73-458711e99893', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6b8a3ad9-2806-323a-9666-331d9744f142', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1b3c4829-fc78-3c74-b536-ee12135a309d', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8f89f127-43d4-3511-85a8-6d86c94561fa', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('53398dc8-2f54-32aa-8468-b31223fbfb25', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8ebc7189-8557-36cb-9f5c-9cddb1008480', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7f57987a-ba5a-3b5d-9d50-f965a5dec8cd', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9ebee068-29f5-316e-9b1e-d50705b58c5e', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('27d8f5c0-e2b9-3bf5-a79f-db3af72673c4', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('009b49ad-76b7-35ef-80dd-4b0bfecd22bb', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5e2c1254-6787-3b7c-a1aa-3d08e2276cfa', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ed2a7593-a055-321c-917a-d40c8bc0298b', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e17ce929-1795-33ff-b17d-85a4c865300a', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('18b2d8b8-f068-3169-a81e-282f6a717f68', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b0b864d6-3eff-3288-9075-1ec21939c787', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9ec4d403-063a-308b-be1e-f7f8442e0532', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fcf7a40b-1ddc-353a-aa95-fbeb4a5c63e2', 'cd0f9866-9679-31cb-bb89-a124a538bf58', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'd3e7fd9b-1962-3e0a-ab37-bc677cfc4b69', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('72bdfcb7-d167-3342-acb0-718d4b06333f', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2b7a9a5d-ae5e-3e5d-b4ff-b9887c750c9f', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6ac1e57a-668e-34c2-8faf-4338aa61399f', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('795435f0-6bae-3afa-b50b-9a3495dab6de', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f87264be-69ca-3ef9-a424-618f6dfb4ad4', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('910bba27-dbb5-3ecc-b91d-22f6c5ccdbc4', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('32fcd926-1929-32f1-8d1d-574f4f2644f3', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('39458cb2-8c60-30a0-a0c4-fc3cde0de910', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('85a868ea-962b-377e-8133-62bc54bec94b', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('465b3404-370d-3e25-8d02-ad440b542718', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f9b5b43a-e627-32be-a49e-b8ce0db53b3f', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('34d8ef46-1d7b-3f7d-b946-5d0e20045d6f', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f25ad1ee-8834-3a88-b363-5db7791a9148', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('500ef9ab-0e84-3b4a-951f-0428c65cfffe', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('70449285-a2fa-3e99-bd06-0d29962eba22', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b2334e92-f654-300c-880f-e3c5cb5a2bba', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cfdc6f10-308c-309b-b275-547e7a5a6922', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('011ad9e3-6768-3bfe-b99b-406dd9695a71', 'f56c98c8-b8af-3db8-b7b1-423d54ebc9b1', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('daeda408-7936-33cc-b456-9166d8a3a82b', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a1b6c763-7c40-3acb-8ded-2bec48271a24', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e5847f8a-a2f9-3363-9f3e-e7c670055665', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e94d8a4e-2136-332a-8923-74b5c77a4870', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cfd61016-ae25-334e-b569-bed74231d6c6', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('eaafeed7-02f6-3ae7-bf99-09c9137a4137', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0f969cde-4f37-3bfa-9b46-7d7c96d15048', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('05c8c7a3-72f1-3acb-bf79-208f9aacc858', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f2431f64-2a98-360a-b445-2038581a9802', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d99a7a97-6499-3a06-a345-3413d0b0fe33', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('34fc8660-d976-3fb0-bde3-3b91bfb83b50', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('63d01b9d-c260-36f9-b503-383f81ce7df9', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ad473922-2aec-332b-9111-f048cef590f4', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('64c5b22e-be3a-3891-ba2e-34ff33318f50', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c6dd03f6-4957-37f7-94f3-a6349fab991d', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('59835af3-ac8c-3809-9898-727ee90b7e6e', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6f8ce0e4-624a-31b3-8bc7-96554504c596', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('67df8d07-7600-3752-9b52-7af715115863', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('24166d41-eb94-34b4-93f5-e23161dc8087', 'daeda408-7936-33cc-b456-9166d8a3a82b', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('c79423e0-0975-3daa-813b-baf96aa9b62b', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('42ad4043-47b3-33bb-be45-a42e82ee3890', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4feb6975-d930-34a2-a4a3-328495ef7594', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2ee50130-2605-352d-a45f-e1ebec71ec77', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c76fc585-8e42-3efd-b16f-a50ccf76db01', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('373f9e07-1995-33df-aab5-b69cfe490bcb', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5b2c13dd-1fee-31d0-b8e6-74782e33d960', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2df7de2a-bedd-34a8-999f-bfa350a4126c', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ca253dad-dcd9-3dce-8dd8-233aa32a0ba6', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('acb2c87f-c6c6-3865-b5b3-345766055de8', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5a3a60aa-18fc-30e7-8114-d65113de657c', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('94b9769c-bfda-3be7-b7c1-f295af124de4', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'SOLUBILITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9ab00d00-7979-3d38-bcdf-324c1c6505bd', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'DENSITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4e4c8d64-82e1-3708-abc0-cdf469fe70cb', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'APPEARANCE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c17128a1-0c14-34ed-9997-3686714a4067', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'MELTING', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('250dbf4a-0abd-3db4-937e-279d0ce9cec5', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e0e0e912-9542-309c-a516-88a4e5e1983f', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'POLARITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0bfd509c-0438-3b1e-adff-7b43d22b4085', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bdc1e6ba-e698-3771-b67c-2b1d4ad7be19', 'c79423e0-0975-3daa-813b-baf96aa9b62b', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('72de0086-06d8-39fa-8baf-e807776c92aa', '059ab99e-36b8-33f7-b2a0-4fa80d57df6c', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e40eea09-7be9-3d41-bb5d-9404da38e0ad', '72de0086-06d8-39fa-8baf-e807776c92aa', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2ccfb22e-405b-3c2e-8b5a-9d3e1a2b03ca', '72de0086-06d8-39fa-8baf-e807776c92aa', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c3265c71-fe5c-390b-ac1d-242051568e24', '72de0086-06d8-39fa-8baf-e807776c92aa', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b2dad243-3f46-3814-aae4-cc7e58049be4', '72de0086-06d8-39fa-8baf-e807776c92aa', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('63bf16b5-3b74-3ccb-b35c-07753780891d', '72de0086-06d8-39fa-8baf-e807776c92aa', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e7853782-12d6-3039-8bdc-59d2939082e4', '72de0086-06d8-39fa-8baf-e807776c92aa', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8a091613-24f2-3a54-842e-7e2199cd3447', '72de0086-06d8-39fa-8baf-e807776c92aa', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('235cd0d4-585c-30ce-8fa8-6f46254f53ae', '72de0086-06d8-39fa-8baf-e807776c92aa', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('63fa37e7-267b-3049-a9dc-53006364a855', '72de0086-06d8-39fa-8baf-e807776c92aa', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0dc5a66e-a63a-3a9d-85df-7dda0cefac6d', '72de0086-06d8-39fa-8baf-e807776c92aa', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('da28b9cb-c8ac-355e-a378-4506783970c3', '72de0086-06d8-39fa-8baf-e807776c92aa', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('49645d8a-f56f-30c7-b880-5d9ef09a4edc', '72de0086-06d8-39fa-8baf-e807776c92aa', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('96441e95-f715-37fc-811b-93ab25c0d052', '72de0086-06d8-39fa-8baf-e807776c92aa', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('39e18aca-e16f-3b7e-bb55-936af0969ec8', '72de0086-06d8-39fa-8baf-e807776c92aa', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d876b111-35d6-3457-9696-10fc64dde3af', '72de0086-06d8-39fa-8baf-e807776c92aa', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bc42e879-3823-369f-9d3e-226caa765473', '72de0086-06d8-39fa-8baf-e807776c92aa', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('81852010-2a28-3c89-b99d-2ca90d1f9dc6', '72de0086-06d8-39fa-8baf-e807776c92aa', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7cddfb93-2934-3156-aca4-e3e6f4089266', '72de0086-06d8-39fa-8baf-e807776c92aa', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('8ddea931-0ed2-37fc-a7ed-146a56a249ce', '1d618f24-ee51-3dcc-b281-b5b7afe54bf4', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('13916535-877c-319e-84ee-ecade0fa2332', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('44e4dc5c-fe37-305c-b96a-8eeaffeef8c8', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8e36132f-b7e8-3346-bde6-1a06b16fae54', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4a7e510d-b999-372e-8c0c-600e897620e2', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('af2fd1d6-a805-3cde-8ae2-cd5eaeac3c32', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7907a161-5c07-3bff-ad34-fae6822a5256', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ccd6839e-0558-3dc9-b14b-eb99c91c4dd6', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('085aaa12-d85e-3cef-9817-2aaf5ff4c235', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('403fb0e1-e69a-3f6e-a339-a36365fcc088', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5ef8878c-307c-3acc-85aa-d619d1987a8f', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7936da93-86e8-395c-9bb2-4c76a20eed92', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b03f9fbd-a55f-31fc-a88e-713b0a0bc541', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('643e0646-aa7e-3bce-a31c-6533d14ae20e', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4c556f5c-7e66-3720-8dbe-e80ab8a935b3', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f31be22c-ff9f-32ab-8dd6-71f899f703c6', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7dec7630-2634-3e62-8067-999e37db0c4f', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('402d9d33-6f9a-364a-9e19-03835637d2a3', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('64bbb358-3db4-3508-b77a-08eebb825c94', '8ddea931-0ed2-37fc-a7ed-146a56a249ce', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('9ebfabb6-6225-3813-8165-59da21733c39', 'e10c4399-2bd8-351d-87e5-752acf2b27d7', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1d8f9da4-a22a-348a-ad4c-2b46cd084178', '9ebfabb6-6225-3813-8165-59da21733c39', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a7fce3e4-a0ea-306a-9aa6-014487722c40', '9ebfabb6-6225-3813-8165-59da21733c39', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('05885579-6717-3a52-94e6-a3b4e0838755', '9ebfabb6-6225-3813-8165-59da21733c39', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9b081386-9988-32dd-acca-42f1697fe002', '9ebfabb6-6225-3813-8165-59da21733c39', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ab9a48d8-a32a-3fd0-a4b4-31e3f37736f5', '9ebfabb6-6225-3813-8165-59da21733c39', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('71a30219-9def-3a80-bf3e-9426197f5518', '9ebfabb6-6225-3813-8165-59da21733c39', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4d559559-6043-3380-9bd6-de8680ce9abe', '9ebfabb6-6225-3813-8165-59da21733c39', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('cbad6196-67a2-3860-9265-16223dc4f65b', '9ebfabb6-6225-3813-8165-59da21733c39', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('841da3ab-3ec5-384e-9f67-eec527f87ba2', '9ebfabb6-6225-3813-8165-59da21733c39', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c670c7d8-a32c-3708-8432-fa118e7e6586', '9ebfabb6-6225-3813-8165-59da21733c39', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('7ee46d39-561a-33d1-8fb5-336a1360ae03', '9ebfabb6-6225-3813-8165-59da21733c39', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('807a0dc7-3efa-3d9f-ba0f-57acdb5086d3', '9ebfabb6-6225-3813-8165-59da21733c39', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e7ab03bc-b9a8-3b39-b671-7958a373cefd', '9ebfabb6-6225-3813-8165-59da21733c39', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('30656557-6c72-3b0b-bbbb-4f0737bdbfa0', '9ebfabb6-6225-3813-8165-59da21733c39', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6f591c1d-e75f-3986-a706-28009780c631', '9ebfabb6-6225-3813-8165-59da21733c39', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('aae101ae-f63d-3411-9c56-3cc79ba74ab4', '9ebfabb6-6225-3813-8165-59da21733c39', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('dcd9266e-eade-33c8-8611-5981f74b503f', '9ebfabb6-6225-3813-8165-59da21733c39', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('fdb8934a-0bb8-398c-9eb2-c0f5a9a35a47', '9ebfabb6-6225-3813-8165-59da21733c39', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('5dbd6037-a031-3eba-ae29-f9bdfac566df', 'de46be3c-3f03-3c24-b32b-d51b403aea4a', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('535cfce9-8519-31ce-811a-daee261c93ca', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c22909e9-589b-32d1-862f-54523513f950', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('17f724c0-9fd5-3804-a5e8-acff6c5ba294', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c0dd3611-a325-37eb-90aa-1498f2965034', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('57aaab2c-9311-3f87-9d8d-574f8aaba629', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5f1b69ea-d642-319f-ac8e-a3d638a71e6a', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('666a331c-1548-3af4-b84a-f751449c251c', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e0effea1-6522-3f45-9afe-324568868b0c', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('883ee647-b378-3e34-a7b8-7b9715be758b', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('3f8e7759-c5be-3d47-bb03-87e2fc38fe3e', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d210d10e-e700-3339-98cc-64bf7c1dc41d', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('68ead6c4-703b-36df-8706-bc413fc2f38e', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2bc40acd-535d-3585-b278-431aa7bf3d5c', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('154f1cf4-0451-39f7-9b84-4828b0bf28db', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f2347bfc-9dd5-3cef-a788-f87eed4435ef', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e19a7ae9-56c9-39b5-ae3f-900a4cc423ba', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('27a95b00-4d7d-3161-96a9-5084b255bcbc', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d019ea4d-4d93-3095-9387-4d6760f01bd6', '5dbd6037-a031-3eba-ae29-f9bdfac566df', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('4027ae08-655d-3751-ac3b-f8f6898fdea4', '10b0b074-d84b-3e34-8392-20f74663472d', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c61dbb0c-0ca2-365a-bf19-e516b8eea5f1', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6acf0b60-adbd-3e6e-bfec-dd57d8c6db3f', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('4b3a917d-1ded-398a-a81a-08ead47f1782', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'REFRACTIVE_INDEX', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5ca97da1-f83a-3bf0-9b3a-5fd9e6ea3ee3', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('04079c0c-41c3-3256-8359-cf645b309c13', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('115f6624-9671-3392-96ea-46e3bccfab4d', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'BOILING', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c5e0f673-bdc1-31c6-9fbd-7a2e17c11c90', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6cd3b871-9590-38cd-9b3e-0b4261de29d2', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('098bc9eb-dde9-3a9f-bccd-0a361a620b54', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('45cc7975-0344-308a-90a2-d9ba0c39d776', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a9eed57c-4a75-31ef-bc6b-9f1e05c21063', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'SOLUBILITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('115e691b-b4f3-312e-bb75-b43afc435b78', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'DENSITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bf6d1344-72b0-3457-9744-2373a29920d7', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b764fca0-ec97-31af-bdf8-1fb973096e87', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'MELTING', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8af421c5-f0cb-37bb-be27-ebff48deaed9', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8e827298-cfe3-3578-8bfc-c72cef139795', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'POLARITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('08fd6161-c030-3565-a754-2f136b798788', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'VISCOSITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('352d0330-7a64-38a3-b549-df987cd0ffc2', '4027ae08-655d-3751-ac3b-f8f6898fdea4', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'd1461121-ac57-31a4-bb71-8ae345d27f33', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c54ff684-3078-3fdd-b25d-0d80f4283a7a', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a7eb3940-6c3b-38ab-96da-7b96bec6d252', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('67977aeb-62c7-3db6-9b10-b59679483a18', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('8e6b1591-be65-3c93-8090-19740315ed41', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('6a67f2e2-480b-3475-9e3e-305ffb98e274', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('657a4d08-bdac-375f-855d-8a3c62b94573', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'BOILING', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f50d418e-b5ad-3e50-ab2b-59902d924758', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ba08e369-d3e9-3822-a8e8-4fbf2b98f208', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('06f10655-b149-3354-a9b0-efab87725991', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('45b09881-347a-398f-9e77-bb24eaa41b97', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('26706180-0afd-3908-81d4-c2c2e6f9ffe3', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'SOLUBILITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('17335178-0481-336a-aa15-8d9eb2928adb', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'DENSITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d066ff21-675d-30bb-935b-6d52ee88b955', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('90b513d7-4e66-377c-8839-1c7d7bd6c4d5', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('57900f54-58b2-3dd5-a053-9b7d62158743', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('49000ed5-02ca-326a-a8f0-7a3343287b94', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'POLARITY', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('21fe49c5-f6b1-3558-9ef3-df4b1e8553f5', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('da04db78-45ce-3ee5-9a76-17702e2107ce', 'ae8f07eb-930d-3d49-ade5-bc8a80c8d110', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('f8170912-e19e-3e10-838b-e7324c024714', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0012e45c-8807-397a-a54a-d01f8a5e96cd', 'f8170912-e19e-3e10-838b-e7324c024714', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('35f42018-93c5-30d1-908b-d0692f58bf33', 'f8170912-e19e-3e10-838b-e7324c024714', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('56067b0f-667f-3ed7-9963-118905ddba87', 'f8170912-e19e-3e10-838b-e7324c024714', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('99e5815a-62be-3e1f-995b-3330a2cac006', 'f8170912-e19e-3e10-838b-e7324c024714', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('67a3deea-5440-3e41-92fe-eece632bb8b9', 'f8170912-e19e-3e10-838b-e7324c024714', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0a842ece-67a2-339d-8223-812e9150a090', 'f8170912-e19e-3e10-838b-e7324c024714', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d62932c1-99de-347c-a091-90af99c8557b', 'f8170912-e19e-3e10-838b-e7324c024714', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('077d6a63-dc9d-3e88-8cbe-1d0410999743', 'f8170912-e19e-3e10-838b-e7324c024714', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b6498c87-1fb6-383b-8f1d-e851d1f21d33', 'f8170912-e19e-3e10-838b-e7324c024714', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('f12156df-2765-34aa-b35e-cb20c5cf0819', 'f8170912-e19e-3e10-838b-e7324c024714', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('b1f371cc-f199-35a7-b6f0-8a5b17e465e3', 'f8170912-e19e-3e10-838b-e7324c024714', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('981aed7f-1bf2-3041-9f19-ec495e7c8174', 'f8170912-e19e-3e10-838b-e7324c024714', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d79290fe-cac3-30b8-aec8-49135a5af1ae', 'f8170912-e19e-3e10-838b-e7324c024714', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('bde42216-de0a-324d-9b31-9fb1ae55b2bc', 'f8170912-e19e-3e10-838b-e7324c024714', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d0eca8a7-eb36-389b-a0be-dadca2473a93', 'f8170912-e19e-3e10-838b-e7324c024714', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ecc96da4-eff4-39e5-8d59-ad42ed4d2569', 'f8170912-e19e-3e10-838b-e7324c024714', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('095dbbad-fc65-3913-9979-f80102cbf77e', 'f8170912-e19e-3e10-838b-e7324c024714', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0d6733f5-ea2f-31cc-8335-1b772a2b5f21', 'f8170912-e19e-3e10-838b-e7324c024714', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

INSERT INTO chemistry.compound_physical_property_profiles (id, compound_id, dataset_version_id)
VALUES ('1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', 'compound-physical-properties-v1.0.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('ebc078a3-a1b9-3eaf-9aa3-ec7849cb6df7', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'VAPOR_PRESSURE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e52d6415-b1d2-3b27-887d-2eef8e95446e', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'PH_OBSERVATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('19fceafe-5458-37c5-8e0c-699dd4795c4f', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'REFRACTIVE_INDEX', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('5692cc8f-be72-34f0-9bb0-b11665b37949', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'ELECTRICAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('60fbed58-3c4c-37b1-a8a2-e429f88dec17', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'STANDARD_STATE', 'AVAILABLE') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a950538f-6225-38a6-b22b-d93418429cca', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'BOILING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('434a042f-256a-34cf-bc7c-2af65ee2578d', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'ODOR', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('a1eb8e4f-f547-3dd2-9ab7-b426b81e12d2', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'MOLAR_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('c1f43be7-1d6e-3f9d-b4bf-775722cc26f3', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'SURFACE_TENSION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('0820bde6-a56f-3c05-a82c-b39280aae4a5', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'SPECIFIC_HEAT_CAPACITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('d821f08c-e921-3a04-9242-aa807e88388f', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'SOLUBILITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('9945d668-f2a7-3748-a62e-c2b993a2b503', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'DENSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('59c9ce6b-a312-33ca-adc8-caaffa212c64', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'APPEARANCE', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('daa6365d-ebc8-3d0c-8c0e-56c259346d80', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'MELTING', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('2e4370c9-52f3-347b-a21e-107f8e9cd95a', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'SUBLIMATION', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('1d85506b-4f6b-3eff-87b2-ffcbc729c3f5', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'POLARITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('e8506aa4-9e95-314a-9f2a-a0130af6078b', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'VISCOSITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;
INSERT INTO chemistry.compound_property_availability (id, profile_id, property_type, availability_status)
VALUES ('97f815a1-4483-3169-8361-d9e65be16d94', '1a151ebe-f2dc-3f5c-9fb0-ee1db33fa30b', 'THERMAL_CONDUCTIVITY', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, property_type) DO NOTHING;

