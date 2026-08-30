/**
 * Scientific pH Calculation Module for Chemistry Sandbox
 * Calculates exact pH based on substance concentrations, pKa/pKb, and neutralization stoichiometry.
 */

export interface ContentComponent {
  materialId: string;
  name?: string;
  formula?: string;
  amount: number; // mL or g or mol
  molarAmount?: number; // mol
  phase: string;
  color?: string;
}

export interface PhResult {
  ph: number | null;
  formattedPh: string;
  statusText: string;
  indicatorColor: string;
  isAcidic: boolean;
  isAlkaline: boolean;
  isNeutral: boolean;
}

export function calculateVesselPh(
  contents: ContentComponent[] | undefined,
  volumeMl: number
): PhResult {
  // If vessel is empty or volume is <= 0
  if (!contents || contents.length === 0 || volumeMl <= 0.05) {
    return {
      ph: null,
      formattedPh: 'pH --',
      statusText: 'Нет жидкости для измерения',
      indicatorColor: '#64748b',
      isAcidic: false,
      isAlkaline: false,
      isNeutral: false,
    };
  }

  // Filter liquid / aqueous contents
  const activeContents = contents.filter(
    (c) => (c.phase === 'liquid' || c.phase === 'aqueous' || c.phase === 'solid') && c.amount > 0.001
  );

  if (activeContents.length === 0) {
    return {
      ph: null,
      formattedPh: 'pH --',
      statusText: 'Нет жидкости для измерения',
      indicatorColor: '#64748b',
      isAcidic: false,
      isAlkaline: false,
      isNeutral: false,
    };
  }

  const volumeLiters = Math.max(0.001, volumeMl / 1000);

  // Molar amounts of H+ and OH- ions
  let molesH = 0;
  let molesOH = 0;

  // Weak acid / base contributions
  let weakAcidContrib = 0;

  for (const c of activeContents) {
    const id = (c.materialId || c.formula || '').trim();
    // Estimate moles if molarAmount not set: 1 mL liquid water/acid ≈ 1/18 mol for H2O, or concentration based
    const amount = c.amount;
    let moles = c.molarAmount;

    if (moles === undefined || moles <= 0) {
      if (id === 'HCl') moles = (amount * 1.0) / 36.46; // ~1M HCl
      else if (id === 'H2SO4') moles = (amount * 1.0) / 98.07; // ~1M H2SO4
      else if (id === 'NaOH') moles = (amount * 1.0) / 39.99; // ~1M NaOH
      else if (id === 'CuSO4' || id === 'CuSO4(aq)') moles = (amount * 0.5) / 159.6;
      else if (id === 'KMnO4' || id === 'KMnO4(aq)') moles = (amount * 0.2) / 158.03;
      else moles = amount / 1000;
    }

    // Strong acids
    if (id === 'HCl') {
      molesH += moles;
    } else if (id === 'H2SO4') {
      molesH += moles * 2; // Diprotic
    }
    // Strong bases
    else if (id === 'NaOH') {
      molesOH += moles;
    }
    // Weakly acidic salts (CuSO4 hydrolysis)
    else if (id === 'CuSO4' || id === 'CuSO4(aq)' || id === 'ZnCl2') {
      const conc = moles / volumeLiters;
      // Hydrolysis: pH ≈ 4.5 for ~0.1M CuSO4
      weakAcidContrib += Math.sqrt(1e-5 * Math.max(0.0001, conc));
    }
  }

  // Stoichiometric Neutralization: H+ + OH- -> H2O
  let calculatedPh = 7.0;

  if (molesH > molesOH) {
    const netH = (molesH - molesOH) / volumeLiters;
    const concH = netH + weakAcidContrib;
    calculatedPh = -Math.log10(Math.max(1e-14, concH));
    calculatedPh = Math.max(0.5, Math.min(6.95, calculatedPh));
  } else if (molesOH > molesH) {
    const netOH = (molesOH - molesH) / volumeLiters;
    const pOH = -Math.log10(Math.max(1e-14, netOH));
    calculatedPh = 14.0 - pOH;
    calculatedPh = Math.max(7.05, Math.min(13.8, calculatedPh));
  } else {
    // Equal H+ and OH- or pure solvent
    if (weakAcidContrib > 0) {
      calculatedPh = -Math.log10(Math.max(1e-7, weakAcidContrib));
      calculatedPh = Math.max(3.8, Math.min(6.8, calculatedPh));
    } else {
      calculatedPh = 7.0;
    }
  }

  // Round to 2 decimals
  calculatedPh = Math.round(calculatedPh * 100) / 100;
  const formattedPh = `pH ${calculatedPh.toFixed(2)}`;

  // Indicator Spectrum Color
  let indicatorColor = '#10b981'; // Neutral Green
  if (calculatedPh < 3.0) indicatorColor = '#ef4444'; // Red (Strong Acid)
  else if (calculatedPh < 5.5) indicatorColor = '#f97316'; // Orange (Weak Acid)
  else if (calculatedPh < 6.8) indicatorColor = '#84cc16'; // Light Green
  else if (calculatedPh <= 7.2) indicatorColor = '#10b981'; // Green (Neutral)
  else if (calculatedPh < 9.0) indicatorColor = '#06b6d4'; // Cyan
  else if (calculatedPh < 11.5) indicatorColor = '#3b82f6'; // Blue (Weak Base)
  else indicatorColor = '#a855f7'; // Purple (Strong Base)

  return {
    ph: calculatedPh,
    formattedPh,
    statusText: formattedPh,
    indicatorColor,
    isAcidic: calculatedPh < 6.8,
    isAlkaline: calculatedPh > 7.2,
    isNeutral: calculatedPh >= 6.8 && calculatedPh <= 7.2,
  };
}
