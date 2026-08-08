# Phase 7E Implementation Report - Buffer Solution Calculator

## Summary
Phase 7E adds a framework-independent buffer domain calculator and an injectable internal `BufferCalculationService`. It supports composition pH, target-pH preparation, proportional dilution, limited strong-acid/strong-base perturbation, exact exhaustion delegation, and ideal buffer-capacity approximation.

## Domain Types
New acid-base domain types include `BufferSystem`, `BufferComponent`, `BufferCalculationRequest`, `BufferCalculationResult`, `BufferPreparationRequest`, `BufferPreparationResult`, `BufferPerturbationRequest`, `BufferPerturbationResult`, `BufferCapacity`, `BufferVolumePolicy`, `BufferRegionStatus`, `BufferCalculationMethod`, `BufferAssumption`, `BufferErrorCode`, `BufferException`, `AcidBaseDecimalMath`, and `BufferCalculator`.

## Reference Semantics
Migration `V22__correct_acid_base_dissociation_semantics.sql` corrects already ionic species from electrolyte behavior to `NOT_APPLICABLE` and marks water as `AUTOIONIZING_SOLVENT`. Roles remain separate as `ACID`, `BASE`, `AMPHIPROTIC`, or `NEUTRAL`.

## Service Flow
`BufferCalculationServiceImpl` resolves a validated aqueous conjugate pair through `AcidBaseReferenceService`, retrieves exact-temperature `Ka`, `Kb`, and `Kw`, then delegates pure arithmetic to `BufferCalculator`. Strong reagent perturbations require `NEGLIGIBLE_ADDED_VOLUME` or `EXPLICIT_FINAL_VOLUME`; post-perturbation concentrations, capacity, and exact-exhaustion delegation use the resulting final volume. Exact exhaustion delegates to `AcidBaseEquilibriumService`; excess strong reagent returns an unsupported mixed-system status.

## Verification Coverage
Tests cover acetic acid / acetate and ammonium / ammonia buffers, target preparation, dilution, strong-acid and strong-base perturbation, exact exhaustion, excess reagent handling, invalid and reversed pairs, unsupported temperature and solvent, zero quantities, outside-range marking, explicit perturbation volume policy, final-volume concentration differences, deterministic repeated calculations, scale-independent equality, centralized decimal transcendental behavior, absence of titration/polyprotic/activity APIs, and domain architecture isolation.

## Phase 7E.1 Integrity Notes
Phase 7E.1 keeps V1-V22 migrations unchanged, adds no titration work, and tightens release integrity around perturbation volume semantics and numerical governance. `AcidBaseDecimalMath` is the single acid-base-domain utility for decimal-to-Java-transcendental conversion.

## Limitations
Phase 7E intentionally defers titration curves, polyprotic buffers, mixed strong/weak excess-reagent equilibrium solving, and activity-coefficient correction.
