-- V15: Seed hazard reference catalogue
INSERT INTO chemistry.hazard_dataset_versions (id, name, publication_date)
VALUES ('compound-hazards-v1.1.0', 'UN GHS Revision 11 Reference Hazards', '2026-08-05')
ON CONFLICT (id) DO NOTHING;

INSERT INTO chemistry.hazard_source_documents (id, document_type, issuer_or_supplier, document_title, classification_system, revision_or_edition, jurisdiction)
VALUES ('UN-GHS-REV11-2025', 'AUTHORITATIVE_CLASSIFICATION', 'United Nations', 'GHS Revision 11', 'UN_GHS', '11th Revised Edition', 'INTERNATIONAL_REFERENCE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('9827b162-c07c-3011-b987-34ff335481a2', '650b152a-3a54-334b-9006-627007c122b0', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('d3ccf5bd-b96b-3486-aa84-af915d6d6ece', '9827b162-c07c-3011-b987-34ff335481a2', 'EU_CLP', 'CLASSIFIED') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('0646a093-965e-378b-9865-ed2f10d7134f', '6c11ca8c-0546-3a30-8558-92fb83efacb6', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('a96a8dac-5423-38b4-8fc1-27faa8aa1237', '0646a093-965e-378b-9865-ed2f10d7134f', 'EU_CLP', 'CLASSIFIED') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('5affcde9-aa01-3faa-9196-9c10a5f521b4', '9ee0626a-3425-3597-a497-5cbf32c8570f', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('028aeb6d-1b78-3111-8f65-e1950e08177f', '5affcde9-aa01-3faa-9196-9c10a5f521b4', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('510e050a-e989-3fa3-8b57-e05991dc6da8', 'ef4b615a-9ecf-3a89-981d-b1f8b8fd72da', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('dd9fc4c0-c610-324f-823b-1fcadd37e30b', '510e050a-e989-3fa3-8b57-e05991dc6da8', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('b9cbf7df-c06b-3dd6-a206-ae9595d0d43c', '31ac5183-783b-3ffd-bb92-7b247924a42f', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('b0c94186-de75-31a0-9816-ed6bd287a7ca', 'b9cbf7df-c06b-3dd6-a206-ae9595d0d43c', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('63a83109-e9a0-312b-95a4-48c0fcaae793', '384ac66c-9749-3b89-8257-d665e1318c93', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('66e14495-c4bf-386f-8b11-c574e4993fef', '63a83109-e9a0-312b-95a4-48c0fcaae793', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('e8f52792-8eba-39b0-a65c-c19806ccc1a9', '6f1a0282-9bfa-350b-bcf4-1924eb58261c', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('70fcb147-8d78-3b15-81e0-18cbccccb272', 'e8f52792-8eba-39b0-a65c-c19806ccc1a9', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('b63f90ff-082f-3af0-bdb1-ad4a37986bca', 'f38e4f83-fe95-3243-887e-f448d74ef717', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('b61458a1-71ea-3bea-8ffa-b82a191a4bc9', 'b63f90ff-082f-3af0-bdb1-ad4a37986bca', 'EU_CLP', 'NOT_CLASSIFIED_BY_SOURCE') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('ada2338e-aee5-3f1f-8617-3bfe15f666f8', '25aa9b6b-0e15-3c35-905d-9c056f332a8a', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('9079e909-d988-3c26-a7e5-c752e98bcd78', 'ada2338e-aee5-3f1f-8617-3bfe15f666f8', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('b6e52f68-f2ad-3563-be35-1d60900c72cd', 'baa7a54f-9112-3a55-8cfe-8b400d31a0b6', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('06ece81b-35fe-3dd0-93f2-77c0b22cee1a', 'b6e52f68-f2ad-3563-be35-1d60900c72cd', 'EU_CLP', 'CLASSIFIED') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('0af3695b-162b-3d90-9316-fe7565bb66d5', '44d58472-c681-3f7e-b989-9b8730603a2b', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('fff82741-af66-3460-9d20-c83101e63049', '0af3695b-162b-3d90-9316-fe7565bb66d5', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('0e5073dd-981d-3cbf-a8b3-dc5226b1e0f8', '665185a5-e410-38fa-8e02-d4a2be56e2c7', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('f4070d6f-2996-3855-bd25-57df731d0eef', '0e5073dd-981d-3cbf-a8b3-dc5226b1e0f8', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('0d3eec0c-85a2-310d-b75d-6275f17b037a', '095d8580-8beb-3e71-800d-add10b6590ae', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('6ae9cbd3-2b0a-38a0-8187-3d918d25ac7f', '0d3eec0c-85a2-310d-b75d-6275f17b037a', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('6d0819b0-c771-371a-afca-d303c48494d0', '41536b4f-237a-38ae-9cf9-b0099a36b773', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('af8ecaab-c888-3355-9f03-65a0e07e7551', '6d0819b0-c771-371a-afca-d303c48494d0', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('9e8c96c9-87fc-3303-8f4a-69ea03c8a9ef', 'dcaa9932-0ee6-37a4-be71-fb00655d1a14', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('6231c76b-41b9-3e4d-a555-17fc85c15f3e', '9e8c96c9-87fc-3303-8f4a-69ea03c8a9ef', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('2ffce157-9f67-376f-99ea-dc0ae4dff91b', '7589a123-6728-3310-b5f0-87d0d514cac5', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('aadfdf21-6bc3-3fc8-b32c-a4c2328a4199', '2ffce157-9f67-376f-99ea-dc0ae4dff91b', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('986cda71-0034-337c-a982-614dd22f3c63', '6b7fc3b3-25c0-3512-bd61-65bf96e79f67', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('9ecd73d3-f87e-375a-bddf-fcc70fdaa9b3', '986cda71-0034-337c-a982-614dd22f3c63', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('63494185-b89d-35c1-8c2a-f14acd6d8665', '4d0d1768-0572-30f2-a1fb-6bc37b29090f', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('985714ff-8361-3efd-9ad2-a45ffba84bba', '63494185-b89d-35c1-8c2a-f14acd6d8665', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('9843e01d-b22d-34c7-a2ec-fc39e920cdaa', 'fb70900a-3666-3cff-ad6d-4d827638a1b7', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('119bff25-ee80-3473-86a2-11b026f65c3f', '9843e01d-b22d-34c7-a2ec-fc39e920cdaa', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('78f79575-b323-37ce-9126-f2b52184d7f6', '3c7d0fda-9a6e-3b79-aebb-1a41aecc6c09', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('49cc2199-dfe9-3313-a793-ba45039630fd', '78f79575-b323-37ce-9126-f2b52184d7f6', 'EU_CLP', 'CLASSIFIED') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('9bd8cd5c-c0c6-3389-9568-7a13888e3885', 'e08d3f76-f638-368f-b699-ebb68ccad2cc', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('ec626a1c-d67d-38e5-882e-9abdfb720c54', '9bd8cd5c-c0c6-3389-9568-7a13888e3885', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('643dcd82-4fb9-383a-a9bb-1e1e49bf7053', '025e3c8a-cba6-39cd-bf57-58218839b82e', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('60737389-f76c-34e9-910c-ac45d1c6bd2e', '643dcd82-4fb9-383a-a9bb-1e1e49bf7053', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('0cee537d-8bfa-34a2-a471-d694bff7ed38', '7c255813-eade-3fc6-be86-1935ac089ddd', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('182794ec-7209-3271-ac53-a324e2fe036f', '0cee537d-8bfa-34a2-a471-d694bff7ed38', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('23011561-e72d-3076-9801-11373cdfb393', 'a1a20e49-bce3-39ac-98f8-162131991c48', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('c8543f84-8db5-3c62-9afb-b6b10c8897ca', '23011561-e72d-3076-9801-11373cdfb393', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('73dad6cd-90f8-3033-8718-31bfcc2882e0', 'fc7272a0-e0f7-3e3f-8092-8404ddba723e', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('59c3192c-cbad-33c7-9181-cb8754bc9310', '73dad6cd-90f8-3033-8718-31bfcc2882e0', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('ea8cb571-94b5-3cc9-843a-d441242b7a1d', 'c6258e3d-0693-3248-94bc-8d455560be75', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('2e765bfb-9c18-3cd1-9f30-75198627b360', 'ea8cb571-94b5-3cc9-843a-d441242b7a1d', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('03fc0770-bda1-3e1e-af78-b2eebc7c4565', '556b4fcf-2c70-3cd8-b4c5-9e79f120317b', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('e895b3c5-d115-3fd5-838c-d005b9963fd4', '03fc0770-bda1-3e1e-af78-b2eebc7c4565', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('15c010d3-07b6-3d88-aa5c-7402977905d2', 'b0e1b520-700c-3136-a849-6fb348890d68', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('5372793d-178c-36c0-afc6-1d672e040278', '15c010d3-07b6-3d88-aa5c-7402977905d2', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('b5964400-0202-3c4d-8019-68d1ce3b8482', '8c611aaa-209c-3fe5-942e-6b2777fc75c7', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('7fb43275-6b8f-3eae-b2b6-e6c9ab01739c', 'b5964400-0202-3c4d-8019-68d1ce3b8482', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('ba4adb9e-e7e2-3073-aa18-9229a9473fce', 'c47aaa91-83ae-37da-a7c8-2c85c2b2a2c2', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('c7f666d4-ffdb-33c2-9b5b-95ec2b5b4357', 'ba4adb9e-e7e2-3073-aa18-9229a9473fce', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('39ffa149-929e-3a5f-bb0a-861aa28240be', '95b7e3d3-ce4a-3239-b362-8a2bd4252950', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('1c8db773-3d71-3108-a90e-df17b58b6acb', '39ffa149-929e-3a5f-bb0a-861aa28240be', 'EU_CLP', 'NOT_CLASSIFIED_BY_SOURCE') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('9bde3f70-6db9-3073-8c7e-3c05d798cea4', '99b9b775-d82e-39ce-ad60-26b7ae2490ac', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('3371336d-d3fa-38c9-b4ab-f39542d72050', '9bde3f70-6db9-3073-8c7e-3c05d798cea4', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('cdf14647-7216-3b1c-8255-545d08b42dc4', '5294140a-5234-3cb6-ae81-635a2260a114', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('c47fcca6-dc99-3cf1-a2a6-3abe894e2454', 'cdf14647-7216-3b1c-8255-545d08b42dc4', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('093d8917-8d4b-3fba-ac6a-9209786c0871', 'e7ccb540-00ab-3c13-8397-06fc3bded8ef', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('0f2e9ef1-73cf-3f71-ad16-aac5c8076ef7', '093d8917-8d4b-3fba-ac6a-9209786c0871', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('efbadedf-b805-381b-9952-ba1c8165ecf8', '1c7585e5-613d-3411-a3e5-cf08960aae4a', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('9206b0e6-862b-391b-957a-53eac0f0a03d', 'efbadedf-b805-381b-9952-ba1c8165ecf8', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('fe2ead2e-faa2-3a5c-85a9-f3d16c360775', 'ca1f3c39-3ecd-3bc7-9758-ddf0513e504d', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('e5822718-a8e9-39e2-9735-f5672fc2bf2d', 'fe2ead2e-faa2-3a5c-85a9-f3d16c360775', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('e28d7057-1cea-3a01-995a-77f839a059b2', '41ad250b-399a-344c-94b2-13a3a63bc28d', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('e5df08b1-a1f2-3624-bf67-28bc30ca6d81', 'e28d7057-1cea-3a01-995a-77f839a059b2', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('37a937d1-6c37-3b48-bf2d-071c2867cc49', '5c5ee053-0520-3976-85d9-78b89adff2e9', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('3a96b694-d2ed-3d8e-b663-f3ac6b71fbf0', '37a937d1-6c37-3b48-bf2d-071c2867cc49', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('bed73c12-0b7b-3bf7-afa8-779a6fb97b3e', 'afc3cd4b-bab8-379c-91fe-d13594d7bbde', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('942e1bc8-1c2f-31ab-a144-776871a07c90', 'bed73c12-0b7b-3bf7-afa8-779a6fb97b3e', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('9d44fba2-e1bb-3ad7-942f-809e7d711bea', 'b77d9820-d830-3461-902b-bbe170a40038', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('25933f74-397c-3982-a373-6a58c4114ff8', '9d44fba2-e1bb-3ad7-942f-809e7d711bea', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('b2941025-7447-3323-8a04-8df978b3a358', 'f8a18806-7192-35ec-af10-9bd0afabcd91', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('6d3ed3a3-7197-3c5e-87de-bdad0a735ae4', 'b2941025-7447-3323-8a04-8df978b3a358', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('c9ccaff5-6ec0-3f9c-9a87-1af9ade19d2e', 'f57d2b69-d3e5-3f85-908e-3a5648015836', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('4e5815fd-7d9f-33fa-af56-68935b9ace70', 'c9ccaff5-6ec0-3f9c-9a87-1af9ade19d2e', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('ab630eae-28d5-336a-9a29-b4e5f46b818f', 'b5497acf-a111-3d7e-8a2a-7e315764a440', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('244c3c78-1779-3fb8-a747-76c9bfe0d044', 'ab630eae-28d5-336a-9a29-b4e5f46b818f', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('270c3fe5-467c-3345-86e5-60efbf76a220', '73d6dd57-d6ed-3fa5-8580-b636040b00a1', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('6268ead6-e8a1-305c-ae85-6b1e3c9768d2', '270c3fe5-467c-3345-86e5-60efbf76a220', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('21e6457f-e6f4-3eab-97c6-3c9eed1d923c', 'd3e7fd9b-1962-3e0a-ab37-bc677cfc4b69', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('392ed997-103e-31ac-8e1b-f4fbbf5f4b7f', '21e6457f-e6f4-3eab-97c6-3c9eed1d923c', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('93c14d8b-14dd-3236-984e-80f052a43324', '6a5ccf10-c302-3c07-a19f-f345106ca4a4', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('29360cbe-67cf-3c2f-86d6-c61211a4a3aa', '93c14d8b-14dd-3236-984e-80f052a43324', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('636421d3-a6e4-3c1d-9ade-9d69eb443094', '60927ca1-04ea-3bc8-a7dd-fc0131ff9f0b', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('25452f54-ee98-3f9d-a18c-bf96c4951878', '636421d3-a6e4-3c1d-9ade-9d69eb443094', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('bd7d77a0-bd4e-37f1-9404-c1f7271573d5', '059ab99e-36b8-33f7-b2a0-4fa80d57df6c', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('995e3871-8a83-314e-bdc1-221b425b9e6e', 'bd7d77a0-bd4e-37f1-9404-c1f7271573d5', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('d11f6fd9-9d94-3d72-857d-565d42b32093', '1d618f24-ee51-3dcc-b281-b5b7afe54bf4', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('8d4562d1-45ba-3b37-aaa6-c27a1d22d318', 'd11f6fd9-9d94-3d72-857d-565d42b32093', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('a2456a6f-ce13-357a-97c6-04867781e2de', 'e10c4399-2bd8-351d-87e5-752acf2b27d7', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('00b2bf26-c6d8-3f96-8737-7dfd162f6fd1', 'a2456a6f-ce13-357a-97c6-04867781e2de', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('1ec56ed9-1238-3166-954c-e48ef3401ee3', 'de46be3c-3f03-3c24-b32b-d51b403aea4a', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('32a2c044-72ed-3b6c-a81f-7ce18fd63c6f', '1ec56ed9-1238-3166-954c-e48ef3401ee3', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('b37b0978-bd14-3fc1-ac75-26030346cdb5', '10b0b074-d84b-3e34-8392-20f74663472d', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('39f3b578-5666-3566-8588-28104d59fbe0', 'b37b0978-bd14-3fc1-ac75-26030346cdb5', 'EU_CLP', 'CLASSIFIED') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('c6be82f1-5a0e-361c-8875-8210be2bcd51', 'd1461121-ac57-31a4-bb71-8ae345d27f33', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('d5f4ab51-7995-3f66-85e5-6203c1216d20', 'c6be82f1-5a0e-361c-8875-8210be2bcd51', 'EU_CLP', 'CLASSIFIED') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('8ede763e-3d3b-3431-91ef-f211453ae439', 'e3b456f2-9873-3a9a-aba2-e1606ffd5f5b', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('bde3880c-ead9-3943-9165-1f5c3810b13e', '8ede763e-3d3b-3431-91ef-f211453ae439', 'EU_CLP', 'NOT_CLASSIFIED_BY_SOURCE') ON CONFLICT (profile_id, classification_system) DO NOTHING;

INSERT INTO chemistry.hazard_profiles (id, compound_id, dataset_version_id)
VALUES ('3235b387-f96c-3a86-9616-be425e27ba55', 'af05f4c0-9722-3faf-8ad8-8ecbebc9ff19', 'compound-hazards-v1.1.0') ON CONFLICT (compound_id, dataset_version_id) DO NOTHING;
INSERT INTO chemistry.hazard_availability (id, profile_id, classification_system, availability_status)
VALUES ('875ebd1b-3c5d-3117-800a-797ddad05cd7', '3235b387-f96c-3a86-9616-be425e27ba55', 'EU_CLP', 'NOT_INCLUDED_IN_DATASET') ON CONFLICT (profile_id, classification_system) DO NOTHING;

