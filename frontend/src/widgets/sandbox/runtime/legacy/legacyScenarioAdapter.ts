import type { RuntimeLocale, RuntimeScenario } from '../runtime.types';
import { SCENARIOS } from '../../scenarios';
import { getChemistryLevel } from '@/data/chemistryLevels';
import { emptyRuntimeCatalog } from '../runtimeCatalog';
export type { ChemistryLevelDefinition, SupportedLocale } from '@/data/chemistryLevels';

const copy: Record<string, Record<RuntimeLocale, { name: string; steps: Array<{ title: string; instruction: string; hint: string }> }>> = {
  water_intro: { en: { name: 'Sandbox introduction', steps: [{ title: 'Place a vessel', instruction: 'Add an empty vessel to the workspace.', hint: 'Open Equipment and choose a flask or beaker.' }, { title: 'Add water', instruction: 'Add water to the vessel.', hint: 'Open Materials, choose water, then select the vessel.' }] }, ru: { name: 'Введение в песочницу', steps: [{ title: 'Поставьте сосуд', instruction: 'Добавьте пустой сосуд на рабочее поле.', hint: 'Откройте «Оборудование» и выберите сосуд.' }, { title: 'Добавьте воду', instruction: 'Добавьте воду в сосуд.', hint: 'Откройте «Материалы» и выберите воду.' }] }, uz: { name: 'Laboratoriyaga kirish', steps: [{ title: 'Idish qo‘ying', instruction: 'Ish maydoniga bo‘sh idish qo‘shing.', hint: 'Uskunalar bo‘limidan idish tanlang.' }, { title: 'Suv qo‘shing', instruction: 'Idishga suv qo‘shing.', hint: 'Materiallardan suvni tanlang.' }] } },
};

const fallbackStep = (id: string, index: number) => ({ title: `Step ${index + 1}`, instruction: '', hint: `Complete step ${index + 1} of ${id}.` });

export function legacyScenarioRuntime(id: string | undefined, locale: RuntimeLocale): RuntimeScenario | null {
  if (!id || !SCENARIOS[id]) return null;
  const scenario = SCENARIOS[id];
  const localized = copy[id]?.[locale] ?? copy[id]?.en;
  return {
    id: scenario.id,
    code: scenario.id,
    title: localized?.name ?? scenario.id.replaceAll('_', ' '),
    description: '',
    source: 'legacy',
    mode: 'LEARNING',
    equipmentIds: [],
    materialIds: [],
    catalog: emptyRuntimeCatalog(),
    steps: scenario.steps.map((step, index) => {
      const strings = localized?.steps[index] ?? fallbackStep(id, index);
      return { id: `${id}-${index + 1}`, title: strings.title, instruction: strings.instruction, hints: [{ id: `${id}-${index + 1}-hint`, type: 'TEXT', text: strings.hint }], completionRule: { operator: 'ALL', conditions: [] }, legacyCheck: step.check };
    }),
  };
}

export const legacyScenarioForLevel = (level: string | null) => level ? ({ '1': 'water_intro', '2': 'measure_water', '3': 'heat_water', '4': 'transfer_water', '5': 'cuso4', '6': 'hcl_naoh', '7': 'sulfur_heat', '8': 'distillation', '9': 'zn_hcl', '10': 'kmno4' } as Record<string, string>)[level] : undefined;
export const legacyScenarioForExperiment = (experiment: string | null) => experiment ? ({ 'heating-water': 'heat_water', 'simple-distillation': 'distillation', 'acid-base-titration': 'hcl_naoh', 'Heating Water': 'heat_water', 'Simple Distillation': 'distillation', 'Acid–Base Titration': 'hcl_naoh' } as Record<string, string>)[experiment] : undefined;

export const legacyLevelDefinition = (level: string | null) => {
  const number = level ? Number(level) : undefined;
  return getChemistryLevel(Number.isInteger(number) ? number : undefined);
};

export const legacyHelpTab = (scenarioId?: string, step?: number): 'equipment' | 'materials' => {
  if (!scenarioId || step === undefined) return 'equipment';
  const materialSteps: Record<string, number[]> = { water_intro: [1], cuso4: [1, 2], kmno4: [0, 1], hcl_naoh: [0, 1], zn_hcl: [0, 1], sulfur_heat: [0], measure_water: [1], heat_water: [1], transfer_water: [1], distillation: [0] };
  return materialSteps[scenarioId]?.includes(step) ? 'materials' : 'equipment';
};

export const legacyHelpTargets = (scenarioId?: string, step?: number): string[] | undefined => {
  if (!scenarioId || step === undefined) return undefined;
  const material: Record<string, string[][]> = { water_intro: [[], ['H2O']], cuso4: [[], ['CuSO4'], ['H2O'], ['action:mix']], kmno4: [['KMnO4'], ['H2O']], hcl_naoh: [['HCl'], ['NaOH']], zn_hcl: [['Zn'], ['HCl']], sulfur_heat: [['sulfur']] };
  if (material[scenarioId]?.[step]?.length) return material[scenarioId][step];
  if (scenarioId === 'measure_water') return step === 0 ? ['beaker'] : step === 1 ? ['H2O'] : ['thermometer', 'port:beaker:sensor'];
  if (scenarioId === 'distillation') return step === 1 ? ['hotplate', 'thermometer', 'condenser'] : ['beaker', 'erlenmeyer', 'condenser'];
  return ['beaker', 'erlenmeyer', 'hotplate'];
};

/** @deprecated Compatibility for old client material slugs in event payloads. */
export const canonicalizeLegacyMaterialId = (id: string) => ({ water: 'H2O', h2o: 'H2O', acid: 'HCl', hcl: 'HCl', naoh: 'NaOH', nacl: 'NaCl', cuso4: 'CuSO4', 'cuso4(aq)': 'CuSO4', kmno4: 'KMnO4', 'kmno4(aq)': 'KMnO4', zn: 'Zn' } as Record<string, string>)[id.toLowerCase()] || id;
