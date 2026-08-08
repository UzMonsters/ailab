# Chemical Classification Data Sources & Provenance

## Data Sources
- **Curated Functional Classifications**: CRC Handbook of Chemistry and Physics, 104th Edition (CRC Press / Taylor & Francis Group). Source identifier: `CRC-HANDBOOK-104`.
- **Derived Composition Rules**: AI Laboratory Safe Derivation Engine (`v1.0.0`). Source identifier: `RULE-ELEMENTAL-COMPOSITION`, `RULE-DISTINCT-ELEMENT-COUNT`, `RULE-HYDRATE-PRESENCE`, `RULE-NET-CHARGE`.

## Dataset Versioning
- Taxonomy Version: `chemical-classification-v1.0.0`
- Manifest File: `chemistry-engine/src/main/resources/chemistry-data/chemical-classification-v1.json`
- Database Migration: `V10__create_chemical_classification_schema.sql` & `V11__seed_chemical_classification.sql`
