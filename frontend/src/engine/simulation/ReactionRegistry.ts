export interface ReactionDef {
  id: string;
  label: string;
  reactants: Record<string, number>;
  products: Record<string, number>;
  activationTempC?: number;
  enthalpyJ?: number;
  productPhases?: Record<string, 'liquid' | 'gas' | 'solid' | 'aqueous'>;
  productColors?: Record<string, string>;
  requiredSolvents?: string[];
  temperatureDeltaC?: number;
}

export interface MaterialProperties {
  phase?: 'solid' | 'liquid' | 'gas' | 'aqueous';
  specificHeatJPerGC?: number;
  boilingPointC?: number;
  meltingPointC?: number;
  latentHeatJPerG?: number;
  densityGPerMl?: number;
  latentHeatVaporizationJPerG?: number;
  latentHeatFusionJPerG?: number;
}

export class ReactionRegistry {
  public readonly reactions: ReactionDef[] = [
    {
      id: 'acid-base-neutralization',
      label: 'HCl + NaOH → NaCl + H₂O',
      // Acid/Base: HCl + NaOH -> NaCl + H2O
      reactants: { 'HCl': 1, 'NaOH': 1 },
      products: { 'NaCl': 1, 'H2O': 1 },
      activationTempC: 0,
      enthalpyJ: -57300,
      productPhases: { NaCl: 'aqueous', H2O: 'liquid' },
      temperatureDeltaC: 6,
    },
    {
      id: 'copper-sulfate-dissolution',
      label: 'CuSO₄ dispersed in water',
      // CuSO4 dissolution: CuSO4(s) + H2O(l) -> CuSO4(aq)
      // Note: we can just model this as CuSO4(s) turning into aqueous in presence of H2O.
      reactants: { 'CuSO4': 1 },
      products: { 'CuSO4(aq)': 1 },
      activationTempC: 0,
      enthalpyJ: -66000,
      productPhases: { 'CuSO4(aq)': 'aqueous' },
      productColors: { 'CuSO4(aq)': '#3B82F6' }, // Blue solution
      requiredSolvents: ['H2O'],
      temperatureDeltaC: 1.5,
    },
    {
      id: 'permanganate-dissolution',
      label: 'KMnO₄ dispersed in water',
      // KMnO4 dissolution: KMnO4(s) + H2O(l) -> KMnO4(aq)
      reactants: { 'KMnO4': 1 },
      products: { 'KMnO4(aq)': 1 },
      activationTempC: 0,
      enthalpyJ: 43600, // endothermic
      productPhases: { 'KMnO4(aq)': 'aqueous' },
      productColors: { 'KMnO4(aq)': '#D946EF' }, // Purple solution
      requiredSolvents: ['H2O'],
      temperatureDeltaC: -1,
    },
    {
      id: 'zinc-hydrochloric-acid',
      label: 'Zn + 2HCl → ZnCl₂ + H₂↑',
      // Zn + 2HCl -> ZnCl2 + H2
      reactants: { 'Zn': 1, 'HCl': 2 },
      products: { 'ZnCl2': 1, 'H2': 1 },
      activationTempC: 0,
      enthalpyJ: -153000,
      productPhases: { 'ZnCl2': 'aqueous', 'H2': 'gas' },
      temperatureDeltaC: 4,
    }
  ];

  public readonly materials: Record<string, MaterialProperties> = {
    'H2O': {
      specificHeatJPerGC: 4.184,
      meltingPointC: 0,
      boilingPointC: 100,
      latentHeatJPerG: 2260,
      densityGPerMl: 1,
    },
    'HCl': { phase: 'aqueous', specificHeatJPerGC: 2.1, densityGPerMl: 1.05, boilingPointC: 108 },
    'NaOH': { phase: 'solid', specificHeatJPerGC: 3.2, meltingPointC: 318, boilingPointC: 1388, densityGPerMl: 2.13 },
    'NaCl': { phase: 'aqueous', specificHeatJPerGC: 0.864, meltingPointC: 801, boilingPointC: 1465, densityGPerMl: 2.16 },
    'H2': { phase: 'gas', specificHeatJPerGC: 14.3, meltingPointC: -259.2, boilingPointC: -252.9 },
    'O2': { phase: 'gas', specificHeatJPerGC: 0.918, meltingPointC: -218.8, boilingPointC: -183 },
    'ethanol': { phase: 'liquid', specificHeatJPerGC: 2.44, meltingPointC: -114.1, boilingPointC: 78.37, latentHeatJPerG: 841, densityGPerMl: .789 },
    'H2SO4': { phase: 'liquid', specificHeatJPerGC: 1.38, meltingPointC: 10.3, boilingPointC: 337, latentHeatJPerG: 500, densityGPerMl: 1.84 },
    'H2O2': { phase: 'liquid', specificHeatJPerGC: 3.0, meltingPointC: - .4, boilingPointC: 150, densityGPerMl: 1.45 },
    'Na2CO3': { phase: 'solid', specificHeatJPerGC: 1.09, meltingPointC: 851, boilingPointC: 1600, densityGPerMl: 2.54 },
    'CuSO4': { phase: 'solid', specificHeatJPerGC: 0.84, meltingPointC: 110, boilingPointC: 560, densityGPerMl: 2.28 },
    'CuSO4(aq)': { specificHeatJPerGC: 3.5 },
    'KMnO4': { phase: 'solid', specificHeatJPerGC: 0.8, meltingPointC: 240, boilingPointC: 240, densityGPerMl: 2.7 },
    'KMnO4(aq)': { specificHeatJPerGC: 3.5 },
    'Zn': { phase: 'solid', specificHeatJPerGC: 0.39, meltingPointC: 419.5, boilingPointC: 907, densityGPerMl: 7.14 },
    'Cu': { specificHeatJPerGC: 0.385, meltingPointC: 1084.6, boilingPointC: 2562, densityGPerMl: 8.96 },
    'sulfur': { specificHeatJPerGC: 0.71, meltingPointC: 115.21, boilingPointC: 444.6, densityGPerMl: 2.07 },
    'S': { specificHeatJPerGC: 0.71, meltingPointC: 115.21, boilingPointC: 444.6, densityGPerMl: 2.07 },
    'ZnCl2': { specificHeatJPerGC: 0.8, meltingPointC: 290, boilingPointC: 732 },
  };

  public getMaterialProperties(materialId: string): MaterialProperties {
    return this.materials[materialId] || { specificHeatJPerGC: 1.0 };
  }
}

export const defaultReactionRegistry = new ReactionRegistry();
