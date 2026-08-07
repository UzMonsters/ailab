# Kinetic Reference Data Sources

Phase 9 incorporates authentic chemical kinetics reference dataset `kinetic-reference-v1.0.0` checked in as `kinetic-reference-v1.json` and persisted in Flyway migrations `V31__create_kinetic_reference.sql`, `V32__seed_kinetic_reference.sql`, `V33__correct_kinetic_reference_integrity.sql`, and `V34__deactivate_and_seed_elementary_kinetics.sql`.

## Provenance and Sources

- **Source Dataset**: NIST Chemical Kinetics Database (Standard Reference Database 17; National Institute of Standards and Technology)
- **Evidence Status**: `EXPERIMENTAL` / `REVIEWED`
- **Dataset Version**: `kinetic-reference-v1.0.0`

## Deactivated Profiles

Synthetic global profiles and non-record-specific entries (`KP-WATER-SYNTHESIS`, `KP-CO-OXIDATION`, `KP-METHANE-COMBUSTION`, `KP-ELEM-H-O2-PIRRAGLIA-1989`, `KP-ELEM-OH-CO-WOOLDRIDGE-1994`) have been deactivated (`is_active = FALSE`).

## Authentic Active Sourced Elementary Profiles

Every active production profile references an exact elementary reaction channel, uses exact radical/molecular participant codes, preserves original source units alongside normalized internal units, and satisfies source-specific Arrhenius reference invariants. A database squib alone is not sufficient provenance when multiple NIST detail records share the same publication; the stored `nist_squib` includes the record suffix.

### 1. `KP-ELEM-H-O2-PIRRAGLIA-1989-REC3` ($\text{H}^\bullet + \text{O}_2 \to \text{OH}^\bullet + \text{O}^\bullet$)
- **Reaction Code**: `RXN-ELEM-H-O2-PROPAGATION`
- **NIST Record Detail ID**: `1989PIR/MIC282:3`
- **Authors**: Pirraglia, P. V.; Michael, J. V.; Sutherland, J. W.; Klemm, R. B.
- **Paper Title**: "A shock tube study of the reaction H + O2 -> OH + O"
- **Journal**: J. Phys. Chem., 1989, Vol. 93, pp. 282-291
- **Rate Law**: $r = k [\text{H}^\bullet]^1 [\text{O}_2]^1$ (Participants: `COMP-RAD-H`, `COMP-O2`)
- **Original Source Units**:
  - $A_{\text{orig}} = 2.79 \times 10^{-10}\text{ cm}^3\text{ molecule}^{-1}\text{s}^{-1}$
  - $k(1050\text{ K})_{\text{orig}} = 1.22 \times 10^{-13}\text{ cm}^3\text{ molecule}^{-1}\text{s}^{-1}$
- **Unit Conversion Factor**: $1\text{ cm}^3\text{ molecule}^{-1}\text{s}^{-1} = 6.02214076 \times 10^{20}\text{ L mol}^{-1}\text{s}^{-1}$
- **Normalized Internal Units**:
  - $A_{\text{norm}} = 1.68017727204 \times 10^{11}\text{ L mol}^{-1}\text{s}^{-1}$
  - $k(1050\text{ K})_{\text{norm}} = 7.3470117272 \times 10^7\text{ L mol}^{-1}\text{s}^{-1}$
  - $E_a = 67.514\text{ kJ/mol}$ ($67514\text{ J/mol}$), $n = 0.0$
- **Validity Range**: $962\text{ K} - 1700\text{ K}$, $0.0133\text{ bar} - 0.0413\text{ bar}$ (Ar bath gas)
- **Data Rule**: Uses one exact NIST detail record and does not merge Arrhenius or reference-rate values from adjacent records.

### 2. `KP-ELEM-CO-OH-WOOLDRIDGE-1994-REC1` ($\text{CO} + \text{OH}^\bullet \to \text{CO}_2 + \text{H}^\bullet$)
- **Reaction Code**: `RXN-ELEM-CO-OH-PROPAGATION`
- **NIST Record Detail ID**: `1994WOO/HAN741-748:1`
- **Authors**: Wooldridge, M. S.; Hanson, R. K.; Bowman, C. T.
- **Paper Title**: "A shock tube study of the reaction CO + OH -> CO2 + H"
- **Journal**: Int. J. Chem. Kinet., 1994, Vol. 26, pp. 741-748
- **Rate Law**: $r = k [\text{CO}]^1 [\text{OH}^\bullet]^1$ (Participants: `COMP-CO`, `COMP-RAD-OH`)
- **Original Source Units**:
  - $A_{\text{orig}} = 3.52 \times 10^{-12}\text{ cm}^3\text{ molecule}^{-1}\text{s}^{-1}$
  - $k(1090\text{ K})_{\text{orig}} = 3.16 \times 10^{-13}\text{ cm}^3\text{ molecule}^{-1}\text{s}^{-1}$
- **Data Type**: Directly measured absolute experimental value.
- **Unit Conversion Factor**: $1\text{ cm}^3\text{ molecule}^{-1}\text{s}^{-1} = 6.02214076 \times 10^{20}\text{ L mol}^{-1}\text{s}^{-1}$
- **Normalized Internal Units**:
  - $A_{\text{norm}} = 2.11979354752 \times 10^9\text{ L mol}^{-1}\text{s}^{-1}$
  - $k(1090\text{ K})_{\text{norm}} = 1.903569 \times 10^8\text{ L mol}^{-1}\text{s}^{-1}$
  - $E_a = 21.867\text{ kJ/mol}$ ($21867\text{ J/mol}$), $n = 0.0$
- **Validity Range**: $1090\text{ K} - 2370\text{ K}$, $0.19\text{ bar} - 0.83\text{ bar}$ (Ar bath gas)
- **Data Rule**: Does not retain the incorrect 389-402 citation, `n = 0.70`, `Ea = 0`, or `O2` rate-law participant from the deactivated profile.
