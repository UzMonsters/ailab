# Standard Reaction Thermodynamics

Phase 8B adds stateless standard reaction thermodynamics for balanced reactions already present in the Reaction Database and covered by the Phase 8A thermodynamic reference catalogue.

## Scope

Supported standard-state properties:

- Standard reaction enthalpy, `delta_r H deg`
- Standard reaction Gibbs energy, `delta_r G deg`
- Standard reaction entropy, `delta_r S deg`
- Standard reaction heat-capacity change, `delta_r Cp deg`

The calculation is reference-data based. It does not perform temperature correction, equilibrium-constant calculation, nonstandard Gibbs-energy calculation, reaction quotient handling, calorimetry, phase-transition thermodynamics or kinetics.

## Sign Convention

Reaction terms are converted to signed stoichiometric coefficients:

- Products are positive.
- Reactants are negative.
- Coefficients are exact and come from the validated Reaction Database.

For a reaction as written:

```text
delta_r H deg = sum(nu_i * delta_f H_i deg)
delta_r G deg = sum(nu_i * delta_f G_i deg)
delta_r S deg = sum(nu_i * S_i deg)
delta_r Cp deg = sum(nu_i * Cp_i deg)
```

The result is per canonical stoichiometric reaction as written. Reversing the reaction negates every reaction property. Multiplying the reaction by a scalar multiplies every reaction property.

## Phase Sensitivity

Thermodynamic records are matched by compound, physical state, temperature and pressure. Formula-identical compounds in different phases remain distinct. Water gas and water liquid therefore produce different reaction properties.

If a reaction term has an unknown state, the caller must provide a state override. The calculator never silently chooses gas, liquid, solid or aqueous state. Aqueous, dissolved or molten reaction terms remain incomplete unless explicit supported reference records are available or a scientifically valid override is supplied.

## Conditions

Phase 8B uses the standard reference records at 298.15 K and 1 bar from `thermodynamic-reference-v1.0.0`. No interpolation is performed. A temperature or pressure mismatch returns incomplete coverage rather than using nearby data.

Standard-state convention is validated from the matched phase-specific record:

- Gas: ideal gas standard state
- Liquid: pure substance standard state
- Solid: solid reference state

Mixed-phase reactions use phase-specific conventions per term; they are not forced into one global convention.

## Term Contributions

Results preserve per-term source values, provenance and signed contributions. A missing formation value, entropy or heat capacity is unavailable, never zero. Elemental formation values are zero only when the reference catalogue explicitly stores them as standard-reference-state values.

## Gibbs-Energy Caution

Negative standard Gibbs energy does not prove a reaction is fast. It also does not prove spontaneity under nonstandard concentrations, pressures or temperatures. Phase 8B reports only the standard-state thermodynamic value for the reaction as written.

## Phase 8 Thermodynamics Completion & Closure

Phase 8 is fully closed with complete thermodynamics capabilities across subphases 8A through 8F:

- **Phase 8A**: Thermodynamic Reference Catalogue (`thermodynamic-reference-v1.0.0`)
- **Phase 8B**: Reaction Thermodynamics at 298.15 K & Hess's Law
- **Phase 8C**: Temperature-Dependent Thermodynamics (Shomate correlations)
- **Phase 8D**: Thermodynamic Equilibrium Constants $K(T)$, Reaction Quotients $Q$, and Nonstandard Gibbs Energy $\Delta_r G$
- **Phase 8E**: Stateless Equilibrium Composition Solver (Ideal gas, Ideal aqueous, Davies aqueous)
- **Phase 8F**: Calorimetry & Thermal Balance (Sensible heat, Thermal mixing, Reaction calorimetry, Adiabatic temperature solving)

### Release Evidence
- **Flyway Migrations**: V1–V30 checksums preserved; 0 new migrations.
- **Test Suite**: 398 passing unit and integration tests with 0 skipped tests.
- **Architecture**: Domain remains framework-independent and decoupled from persistence/HTTP layers.

