/**
 * Normalizes backend equipment taxonomy names into human-readable labels.
 * Backend taxonomy names (like 'Laboratory balance taxonomy profile') 
 * are mapped to friendly display names.
 * Backend should NOT be modified to fix naming — this adapter handles it.
 */

/** Map of backend taxonomy keywords to display labels */
const TAXONOMY_LABEL_MAP: Array<{ pattern: RegExp; label: string }> = [
  { pattern: /analytical.?balance|precision.?scale|laboratory.?balance/i, label: 'Analytical Balance' },
  { pattern: /ph.?meter/i, label: 'pH Meter' },
  { pattern: /bunsen.?burner|gas.?burner/i, label: 'Bunsen Burner' },
  { pattern: /hot.?plate|heating.?plate/i, label: 'Hot Plate' },
  { pattern: /erlenmeyer/i, label: 'Erlenmeyer Flask' },
  { pattern: /round.?bottom.?flask|round.?flask/i, label: 'Round-Bottom Flask' },
  { pattern: /volumetric.?flask/i, label: 'Volumetric Flask' },
  { pattern: /graduated.?cylinder/i, label: 'Graduated Cylinder' },
  { pattern: /separatory.?funnel/i, label: 'Separatory Funnel' },
  { pattern: /condenser|liebig/i, label: 'Condenser' },
  { pattern: /beaker/i, label: 'Beaker' },
  { pattern: /test.?tube/i, label: 'Test Tube' },
  { pattern: /thermometer/i, label: 'Thermometer' },
  { pattern: /burette/i, label: 'Burette' },
  { pattern: /pipette|pasteur/i, label: 'Pipette' },
  { pattern: /petri.?dish/i, label: 'Petri Dish' },
  { pattern: /watch.?glass/i, label: 'Watch Glass' },
  { pattern: /funnel/i, label: 'Funnel' },
  { pattern: /ice.?bath/i, label: 'Ice Bath' },
  { pattern: /clamp|boss.?head/i, label: 'Clamp' },
  { pattern: /stand|ring.?stand|retort/i, label: 'Ring Stand' },
];

/** Map of backend equipment type codes to frontend EquipmentType keys */
const TYPE_CODE_MAP: Record<string, string> = {
  // Common backend taxonomy type patterns
  'BEAKER': 'beaker',
  'TEST_TUBE': 'testtube',
  'ERLENMEYER': 'erlenmeyer',
  'ERLENMEYER_FLASK': 'erlenmeyer',
  'ROUND_FLASK': 'roundflask',
  'ROUND_BOTTOM_FLASK': 'roundflask',
  'VOLUMETRIC_FLASK': 'volumetric_flask',
  'GRADUATED_CYLINDER': 'graduated_cylinder',
  'SEPARATORY_FUNNEL': 'separatory_funnel',
  'BUNSEN_BURNER': 'burner',
  'GAS_BURNER': 'burner',
  'HOT_PLATE': 'hotplate',
  'HOTPLATE': 'hotplate',
  'CONDENSER': 'condenser',
  'THERMOMETER': 'thermometer',
  'ANALYTICAL_BALANCE': 'scales',
  'PRECISION_SCALE': 'scales',
  'DIGITAL_BALANCE': 'digitalbalance',
  'PH_METER': 'phmeter',
  'ICE_BATH': 'icebath',
  'BURETTE': 'burette',
  'PIPETTE': 'pipette',
  'PETRI_DISH': 'petridish',
  'WATCH_GLASS': 'watchglass',
  'FUNNEL': 'funnel',
  'CLAMP': 'clamp',
  'RING_STAND': 'stand',
};

/**
 * Normalizes a backend equipment display name into a human-readable label.
 * Strips 'taxonomy profile', manufacturer names, and other noise.
 */
export function normalizeEquipmentLabel(backendName: string): string {
  if (!backendName) return 'Equipment';
  // Check taxonomy label map first
  for (const { pattern, label } of TAXONOMY_LABEL_MAP) {
    if (pattern.test(backendName)) return label;
  }
  // Strip common backend noise
  return backendName
    .replace(/taxonomy profile/gi, '')
    .replace(/dwk kimble|fisher scientific|pyrex|kimax|corning/gi, '')
    .replace(/\s+/g, ' ')
    .trim() || 'Equipment';
}

/**
 * Maps a backend equipment type code or taxonomy name to a frontend EquipmentType.
 * Returns 'unsupported' if no match found.
 */
export function normalizeEquipmentType(backendType: string, backendName?: string): string {
  // Direct type code match (case-insensitive)
  const upperType = backendType.toUpperCase().replace(/-/g, '_');
  if (TYPE_CODE_MAP[upperType]) return TYPE_CODE_MAP[upperType];
  // Try lowercase match
  const lowerType = backendType.toLowerCase().replace(/[^a-z0-9_]/g, '');
  const foundKey = Object.keys(TYPE_CODE_MAP).find(k => k.toLowerCase() === lowerType);
  if (foundKey) return TYPE_CODE_MAP[foundKey];
  // Try matching via taxonomy name
  if (backendName) {
    for (const { pattern } of TAXONOMY_LABEL_MAP) {
      if (pattern.test(backendName)) {
        const label = TAXONOMY_LABEL_MAP.find(m => m.pattern.test(backendName))?.label ?? '';
        const mapped = Object.entries(TYPE_CODE_MAP).find(([, v]) => 
          v === label.toLowerCase().replace(/[^a-z]/g, '')
        );
        if (mapped) return mapped[1];
      }
    }
  }
  // Try direct lowercase search in type code map values
  const directMatch = Object.values(TYPE_CODE_MAP).find(v => 
    v === lowerType || lowerType.includes(v) || v.includes(lowerType)
  );
  return directMatch ?? 'unsupported';
}

export function canonicalEquipmentId(value: string): string {
  const key = value.toLowerCase().replace(/[-\s]/g, '_');
  const aliases: Record<string, string> = {
    round_flask: 'roundflask', round_bottom_flask: 'roundflask',
    bunsen_burner: 'burner', bunsenburner: 'burner',
    magnetic_stirrer: 'magnetic_stirrer', magneticstirrer: 'magnetic_stirrer',
    graduated_cylinder: 'graduated_cylinder', volumetric_flask: 'volumetric_flask',
    ring_stand: 'clampstand', stand: 'clampstand', ringstand: 'clampstand',
  };
  return aliases[key] ?? key;
}

/** Full equipment DTO normalization */
export function normalizeEquipmentSummary<T extends { id?: string; type?: string; name?: string; displayName?: string }>(dto: T) {
  return {
    ...dto,
    displayName: normalizeEquipmentLabel(dto.name ?? dto.displayName ?? ''),
    frontendType: normalizeEquipmentType(dto.type ?? '', dto.name),
  };
}
