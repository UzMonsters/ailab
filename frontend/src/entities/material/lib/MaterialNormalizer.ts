/**
 * Frontend override for known substance phases.
 * If backend returns wrong phase for a substance, use this map.
 * Backend should not be modified — this adapter handles the discrepancy.
 */

export type MaterialPhase = 'liquid' | 'gas' | 'solid' | 'aqueous' | 'unknown';

/** Known substance phases at standard conditions (25°C, 1 bar) */
const KNOWN_PHASES: Record<string, MaterialPhase> = {
  // Gases
  H2: 'gas', O2: 'gas', N2: 'gas', F2: 'gas', Cl2: 'gas',
  CO: 'gas', CO2: 'gas', NO: 'gas', NO2: 'gas', SO2: 'gas',
  SO3: 'gas', NH3: 'gas', HCl: 'gas', HF: 'gas', HBr: 'gas',
  HI: 'gas', CH4: 'gas', C2H6: 'gas', C3H8: 'gas', C4H10: 'gas',
  He: 'gas', Ne: 'gas', Ar: 'gas', Kr: 'gas', Xe: 'gas',
  H2S: 'gas', PH3: 'gas', BF3: 'gas', BCl3: 'gas', SiH4: 'gas',
  // Liquids
  H2O: 'liquid', Br2: 'liquid', Hg: 'liquid',
  C2H5OH: 'liquid', CH3OH: 'liquid', C6H6: 'liquid',
  CH3COOH: 'liquid', HNO3: 'liquid', H2SO4: 'liquid', H3PO4: 'liquid',
  CHCl3: 'liquid', CCl4: 'liquid', CH2Cl2: 'liquid', CS2: 'liquid',
  C7H8: 'liquid', C8H10: 'liquid', C6H5CH3: 'liquid',
  'CH3COCH3': 'liquid', 'C6H12': 'liquid',
  // Solids
  NaCl: 'solid', KCl: 'solid', CaCl2: 'solid', MgCl2: 'solid',
  NaOH: 'solid', KOH: 'solid', Ca: 'solid', Na: 'solid', K: 'solid',
  Fe: 'solid', Cu: 'solid', Zn: 'solid', Al: 'solid', Mg: 'solid',
  S: 'solid', C: 'solid', P: 'solid', I2: 'solid', Si: 'solid',
  CuSO4: 'solid', Na2SO4: 'solid', K2SO4: 'solid', FeSO4: 'solid',
  CaCO3: 'solid', Na2CO3: 'solid', K2CO3: 'solid',
  NaHCO3: 'solid', KHCO3: 'solid',
  NaNO3: 'solid', KNO3: 'solid', NH4NO3: 'solid', NH4Cl: 'solid',
  AgNO3: 'solid', BaSO4: 'solid', CaSO4: 'solid',
  CuO: 'solid', FeO: 'solid', Fe2O3: 'solid', Al2O3: 'solid',
  SiO2: 'solid', TiO2: 'solid', MnO2: 'solid',
  // Aqueous solutions (common lab reagents as solutions)
  'HCl(aq)': 'aqueous', 'NaOH(aq)': 'aqueous', 'H2SO4(aq)': 'aqueous',
  'HNO3(aq)': 'aqueous', 'KOH(aq)': 'aqueous', 'NH3(aq)': 'aqueous',
};

/**
 * Normalizes a substance's phase using frontend metadata.
 * If the formula is known, returns the corrected phase.
 * Otherwise returns the backend-provided phase as-is.
 */
export function normalizePhase(formula: string, backendPhase?: string): MaterialPhase {
  // Check formula directly
  if (KNOWN_PHASES[formula]) return KNOWN_PHASES[formula];
  // Strip whitespace and check again
  const clean = formula.trim();
  if (KNOWN_PHASES[clean]) return KNOWN_PHASES[clean];
  // Check common name patterns
  if (backendPhase === 'gas' || backendPhase === 'liquid' || backendPhase === 'solid' || backendPhase === 'aqueous') {
    return backendPhase as MaterialPhase;
  }
  return 'unknown';
}

/**
 * Normalizes a material object from the backend.
 */
export function normalizeMaterial<T extends { formula?: string; state?: string; phase?: string }>(material: T): T & { state: MaterialPhase } {
  const formula = material.formula ?? '';
  const backendPhase = material.state ?? material.phase ?? undefined;
  return { ...material, state: normalizePhase(formula, backendPhase) };
}
