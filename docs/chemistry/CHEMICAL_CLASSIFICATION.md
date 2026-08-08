# Chemical Classification Module Architecture & Taxonomy

## Overview
The Chemical Classification Module (`com.ailab.chemistry.domain.classification`) provides an immutable, hierarchical, versioned taxonomy for classifying chemical compounds across orthogonal dimensions.

## Taxonomy Version
- Current Version: `chemical-classification-v1.0.0`
- Taxonomy Definitions: 41 definitions across 6 dimensions
- Curated Profiles: Exactly 55 compound profiles matching the Compound catalogue

## Dimensions & Cardinalities
1. `SUBSTANCE_DOMAIN`: Exactly 1 per profile (`ELEMENTAL_SUBSTANCE`, `INORGANIC_COMPOUND`, `ORGANIC_COMPOUND`).
2. `COMPOSITION_PATTERN`: Multi-valued safe rule-derived patterns (`MONOATOMIC_OR_ELEMENTAL`, `BINARY_COMPOSITION`, `TERNARY_COMPOSITION`, `QUATERNARY_OR_HIGHER_COMPOSITION`, `HYDRATE`, `NEUTRAL_SPECIES`, `CHARGED_SPECIES`).
3. `INORGANIC_FUNCTIONAL_CLASS`: Multi-valued curated functional classes (`OXIDE`, `PEROXIDE`, `HYDRIDE`, `HYDROXIDE`, `ACID`, `BASE`, `SALT`, `OTHER_INORGANIC`).
4. `ACID_SUBTYPE`: Single-valued optional subtype (`BINARY_ACID`, `OXYACID`, `OTHER_ACID`). Requires `ACID` or `CARBOXYLIC_ACID`.
5. `SALT_SUBTYPE`: Multi-valued optional subtype (`NORMAL_SALT`, `ACID_SALT`, `BASIC_SALT`, `DOUBLE_SALT`, `HYDRATED_SALT`, `OTHER_SALT`). Requires `SALT`.
6. `ORGANIC_FUNCTIONAL_CLASS`: Multi-valued curated organic functional classes (`HYDROCARBON`, `ALCOHOL`, `ETHER`, `ALDEHYDE`, `KETONE`, `CARBOXYLIC_ACID`, `ESTER`, `AMINE`, `AMIDE`, `CARBOHYDRATE`, `OTHER_ORGANIC`).

## Formula Limitations & Isomer Handling
Molecular formula alone cannot distinguish structural isomers (e.g. `C2H6O` is Ethanol or Dimethyl ether). Therefore, functional classifications are curated per compound aggregate identity, while composition counts, element counts, hydrate presence, and net charge are safely derived via explicit rule engines.
