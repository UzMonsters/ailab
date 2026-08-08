# Phase 7H Polyprotic Titration

Phase 7H adds stateless diprotic titration calculations without adding
database migrations, REST endpoints, persistence, graph generation, activity
corrections, or precipitation equilibrium.

## Implementation

New internal API:

```java
public interface PolyproticTitrationService {
    PolyproticTitrationPointResult calculatePoint(
            PolyproticTitrationRequest request,
            Volume addedTitrantVolume
    );

    PolyproticTitrationCurveResult calculateCurve(
            PolyproticTitrationRequest request,
            List<Volume> addedVolumes
    );

    PolyproticTitrationCurveResult calculateCharacteristicPoints(
            PolyproticTitrationRequest request
    );
}
```

The service resolves catalogue data through `AcidBaseReferenceService`, verifies
the initial equilibrium path through `PolyproticEquilibriumService`, and then
delegates each point to the framework-independent `PolyproticTitrationCalculator`.

## Domain Types

- `PolyproticTitrationRequest`
- `PolyproticTitrationPointResult`
- `PolyproticTitrationCurveResult`
- `PolyproticTitrationSystemType`
- `PolyproticTitrationRegion`
- `PolyproticEquivalencePoint`
- `PolyproticTitrationResidual`
- `PolyproticTitrationMethod`
- `PolyproticTitrationErrorCode`
- `PolyproticTitrationException`
- `PolyproticTitrationCalculator`

## Scientific Behavior

For every point, Phase 7H uses:

```text
Ct = nFamily / (Vanalyte + Vtitrant)
fixed charge = (initial counterion charge + titrant spectator charge) / Vtotal
charge balance = [H+] - Kw/[H+] + fixed charge + sum(species charge * Ct * alpha) = 0
```

The species distribution and charge-balance calculation are reused from Phase
7G through `PolyproticEquilibriumCalculator.calculateForFixedCharge`.

## Verification Coverage

Regression tests cover:

- carbonic acid titrated with sodium hydroxide;
- carbonate titrated with strong acid;
- bicarbonate titrated with strong acid;
- bicarbonate titrated with strong base;
- sulfuric acid titrated with sodium hydroxide;
- two explicit equivalence points;
- monotonic curve direction;
- continuity near both equivalence points;
- first and second half-equivalence behavior;
- residual thresholds;
- duplicate volume rejection;
- invalid spectator-ion rejection;
- zero concentration rejection;
- unsupported family and solvent rejection;
- solver convergence failure mapping;
- deterministic ordering of curve volumes;
- Spring service injection against PostgreSQL/Flyway V22.

## Release Notes

No V1-V22 migration file was changed. PostgreSQL/Flyway remains at chemistry
schema V22. The acid-base domain remains framework-independent.

Activity coefficients, ionic-strength correction, precipitation, triprotic
titration, weak titrants, indicators, graph generation, and REST endpoints are
explicitly out of scope for Phase 7H.
