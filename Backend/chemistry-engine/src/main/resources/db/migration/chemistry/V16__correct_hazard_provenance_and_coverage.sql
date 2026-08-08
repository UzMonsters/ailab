-- V16: Correct hazard provenance, scope and coverage integrity
INSERT INTO chemistry.hazard_dataset_versions (id, name, publication_date)
VALUES ('compound-hazards-v1.1.0', 'Corrected Compound Hazard Reference Catalogue v1.1.0', '2026-08-05')
ON CONFLICT (id) DO NOTHING;

INSERT INTO chemistry.hazard_source_documents (id, document_type, issuer_or_supplier, document_title, classification_system, revision_or_edition, jurisdiction)
VALUES ('ECHA-CL-INVENTORY-2025', 'REGULATORY_DATABASE', 'European Chemicals Agency', 'ECHA C&L Inventory Harmonized Classifications', 'EU_CLP', '2025.1', 'EU')
ON CONFLICT (id) DO NOTHING;

INSERT INTO chemistry.hazard_source_documents (id, document_type, issuer_or_supplier, document_title, classification_system, revision_or_edition, jurisdiction)
VALUES ('OSHA-HCS-2025', 'AUTHORITATIVE_CLASSIFICATION', 'US OSHA', 'OSHA Hazard Communication Standard', 'OSHA_HCS', '2024-Final', 'UNITED_STATES')
ON CONFLICT (id) DO NOTHING;
