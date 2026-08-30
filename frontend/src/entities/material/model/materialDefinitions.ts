export type MaterialDefinition = {
  id: string;
  formulaHtml: string;
  nameKey: string;
  stateKey: string;
  molarMass: string;
  boilingPoint: string;
  density: string;
};

export const materialDefinitions: MaterialDefinition[] = [
  {
    id: 'water',
    formulaHtml: 'H<sub>2</sub>O',
    nameKey: 'substance.water',
    stateKey: 'substance.stateLiquid',
    molarMass: '18.015 g/mol',
    boilingPoint: '100 °C',
    density: '997 kg/m³'
  },
  {
    id: 'salt',
    formulaHtml: 'NaCl',
    nameKey: 'substance.salt',
    stateKey: 'substance.stateSolid',
    molarMass: '58.44 g/mol',
    boilingPoint: '1465 °C',
    density: '2160 kg/m³'
  },
  {
    id: 'ethanol',
    formulaHtml: 'C<sub>2</sub>H<sub>5</sub>OH',
    nameKey: 'substance.ethanol',
    stateKey: 'substance.stateLiquid',
    molarMass: '46.07 g/mol',
    boilingPoint: '78.37 °C',
    density: '789 kg/m³'
  },
  {
    id: 'copper_sulfate',
    formulaHtml: 'CuSO<sub>4</sub>',
    nameKey: 'substance.copper',
    stateKey: 'substance.stateSolid',
    molarMass: '159.609 g/mol',
    boilingPoint: 'N/A (Decomposes)',
    density: '3620 kg/m³'
  },
  {
    id: 'sulfuric_acid', formulaHtml: 'H<sub>2</sub>SO<sub>4</sub>', nameKey: 'substance.sulfuricAcid', stateKey: 'substance.stateLiquid',
    molarMass: '98.079 g/mol', boilingPoint: '337 °C', density: '1840 kg/m³'
  },
  {
    id: 'hydrochloric_acid', formulaHtml: 'HCl', nameKey: 'substance.hydrochloricAcid', stateKey: 'substance.stateLiquid',
    molarMass: '36.46 g/mol', boilingPoint: '−85 °C', density: '1180 kg/m³'
  },
  {
    id: 'hydrogen_peroxide', formulaHtml: 'H<sub>2</sub>O<sub>2</sub>', nameKey: 'substance.hydrogenPeroxide', stateKey: 'substance.stateLiquid',
    molarMass: '34.014 g/mol', boilingPoint: '150.2 °C', density: '1110 kg/m³'
  },
  {
    id: 'copper_sulfate_solution', formulaHtml: 'CuSO<sub>4</sub>(aq)', nameKey: 'substance.copperSolution', stateKey: 'substance.stateLiquid',
    molarMass: '159.609 g/mol', boilingPoint: '100 °C (solution)', density: '1050 kg/m³'
  },
  {
    id: 'potassium_permanganate', formulaHtml: 'KMnO<sub>4</sub>', nameKey: 'substance.potassiumPermanganate', stateKey: 'substance.stateSolid',
    molarMass: '158.034 g/mol', boilingPoint: 'Decomposes', density: '2700 kg/m³'
  },
  {
    id: 'potassium_permanganate_solution', formulaHtml: 'KMnO<sub>4</sub>(aq)', nameKey: 'substance.permanganateSolution', stateKey: 'substance.stateLiquid',
    molarMass: '158.034 g/mol', boilingPoint: '100 °C (solution)', density: '1020 kg/m³'
  },
  {
    id: 'sodium_hydroxide', formulaHtml: 'NaOH', nameKey: 'substance.sodiumHydroxide', stateKey: 'substance.stateSolid',
    molarMass: '39.997 g/mol', boilingPoint: '1388 °C', density: '2130 kg/m³'
  },
  {
    id: 'sodium_carbonate', formulaHtml: 'Na<sub>2</sub>CO<sub>3</sub>', nameKey: 'substance.sodiumCarbonate', stateKey: 'substance.stateSolid',
    molarMass: '105.99 g/mol', boilingPoint: '1600 °C', density: '2540 kg/m³'
  },
  {
    id: 'zinc', formulaHtml: 'Zn', nameKey: 'substance.zinc', stateKey: 'substance.stateSolid',
    molarMass: '65.38 g/mol', boilingPoint: '907 °C', density: '7140 kg/m³'
  },
  {
    id: 'copper', formulaHtml: 'Cu', nameKey: 'substance.copperMetal', stateKey: 'substance.stateSolid',
    molarMass: '63.546 g/mol', boilingPoint: '2562 °C', density: '8960 kg/m³'
  },
  {
    id: 'gold', formulaHtml: 'Au', nameKey: 'substance.gold', stateKey: 'substance.stateSolid',
    molarMass: '196.967 g/mol', boilingPoint: '2970 °C', density: '19320 kg/m³'
  },
  {
    id: 'sulfur', formulaHtml: 'S', nameKey: 'substance.sulfur', stateKey: 'substance.stateSolid',
    molarMass: '32.06 g/mol', boilingPoint: '444.6 °C', density: '2070 kg/m³'
  },
  {
    id: 'ph_indicator', formulaHtml: 'pH', nameKey: 'substance.phIndicator', stateKey: 'substance.stateLiquid',
    molarMass: '—', boilingPoint: '—', density: '1000 kg/m³'
  }
];
