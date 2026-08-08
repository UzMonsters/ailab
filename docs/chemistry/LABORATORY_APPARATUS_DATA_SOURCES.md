# Laboratory Apparatus Data Sources

Datasets:

```text
laboratory-equipment-reference-v1.0.0
laboratory-container-reference-v1.0.0
laboratory-equipment-reference-v1.1.0
laboratory-container-reference-v1.1.0
```

Manifests:

```text
Backend/chemistry-engine/src/main/resources/chemistry-data/laboratory-equipment-reference-v1.json
Backend/chemistry-engine/src/main/resources/chemistry-data/laboratory-container-reference-v1.json
```

## Release Integrity

V1.0 remains an immutable taxonomy snapshot. V1.1 is the corrected minimal operational snapshot used by the PostgreSQL JDBC adapters and service-level profile evaluation. No V1 through V41 migration is edited by this correction; V42 adds integrity columns and constraints, and V43 registers and seeds the V1.1 datasets.

Taxonomy records identify equipment/container kinds only. Performance-qualified records must include a specific model or standard class, source code, source record, active status, and sourced-reference evidence status before they can back operational suitability decisions.

## Source Metadata

V1.0 taxonomy sources:

- `ASTM-E288-2010R2019`: ASTM E288-10(2019), Standard Specification for Laboratory Glass Volumetric Flasks; 2019 reapproval; scope and apparatus taxonomy; ASTM E288 record.
- `ASTM-E287-2019`: ASTM E287-19, Standard Specification for Laboratory Glass Graduated Cylinders; 2019 edition; scope and apparatus taxonomy; ASTM E287 record.
- `ASTM-E438-1992R2022`: ASTM E438-92(2022), Standard Specification for Glasses in Laboratory Apparatus; 2022 reapproval; glass type taxonomy; ASTM E438 record.
- `NIST-H44-2026`: NIST Handbook 44, 2026 edition; weighing devices taxonomy; NIST Handbook 44 current-edition record.
- `LAB-POLICY-CAL-2026`: internal governed calibration-policy metadata for calculator behavior, not a manufacturer claim or seeded calibration completion record.
- `COLE-PARMER-CHEM-COMPAT`: supplier compatibility lookup metadata for narrowly named material-chemical records.

V1.1 operational sources:

- `OHAUS-PX224-2026`: OHAUS PX224 product specifications; capacity 220 g, readability 0.1 mg, linearity +/- 0.0002 g, typical repeatability 0.0001 g, working environment limits.
- `THERMO-ORION-A211-REVD`: Thermo Scientific Orion Star A211 specification sheet; pH range -2.000 to 20.000, pH resolution 0.1/0.01/0.001, pH accuracy +/- 0.002, temperature range -5 to 105 C.
- `IKA-CMAG-HS7-2024`: IKA C-MAG HS 7 data sheet; speed 100-1500 rpm, water stirring quantity max 10 L, heating range 50-500 C, ambient 5-40 C and relative humidity limit.
- `DWK-KIMAX-28014B-100-2026`: DWK KIMAX Colorware 100 mL Class A volumetric flask; ASTM E288 Class A, ASTM E438 Type I Class A borosilicate glass, tolerance +/- 0.08 mL.
- `THERMO-NALGENE-N319-0500-2026`: Thermo Scientific Nalgene N319-0500 HDPE narrow-mouth bottle with polypropylene closure; 500 mL capacity, 520 mL brim capacity, ambient temperature/pressure leakproof note for water.

## Coverage

V1.1 equipment profiles:

- OHAUS PX224 analytical balance mass measurement.
- Thermo Scientific Orion Star A211 pH and temperature measurement.
- DWK KIMAX 28014B-100 volumetric flask contained-volume measurement.
- IKA C-MAG HS 7 heating and stirring.

V1.1 container profiles:

- DWK KIMAX 28014B-100 borosilicate volumetric flask.
- Thermo Scientific Nalgene N319-0500 HDPE bottle with polypropylene screw closure.

V1.1 compatibility records:

- water with borosilicate glass, source-bounded 48 hour exposure basis.
- water with HDPE/polypropylene, source-bounded 48 hour exposure basis.
- aromatic hydrocarbon family with HDPE/polypropylene as incompatible, source-bounded 48 hour exposure basis.

Active compatibility rows must carry a source record id, contact-duration boundary, temperature bounds, and `source_defined_boundaries = TRUE`. `UNKNOWN` compatibility is never seeded as compatible production data.

## Electrochemistry Preflight

Phase 11 electrochemical source records retain stable source identifiers in V39. Phase 12 also verifies `FaradayConstant.CODATA_2018_EXACT` has explicit value, unit, source, and version.
