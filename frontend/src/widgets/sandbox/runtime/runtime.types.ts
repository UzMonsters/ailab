import type { JsonObject } from '@/shared/api/contracts/platform';
import type { ScenarioScene } from '@/widgets/admin/scenario/scenario.types';

export type RuntimeLocale = 'en' | 'ru' | 'uz';
export type RuntimeLocalizedText = Record<RuntimeLocale, string>;
export type RuntimeMode = 'NORMAL' | 'LEARNING' | 'ADMIN_AUTHORING' | 'ADMIN_PREVIEW';

export type RuntimeHint = {
  id: string;
  type: 'TEXT' | 'HIGHLIGHT' | 'ARROW' | 'GHOST_PLACEMENT' | 'CONNECT_PORTS';
  text: string;
  targetAlias?: string;
  targetPortId?: string;
  fromAlias?: string;
  fromPortId?: string;
  toAlias?: string;
  toPortId?: string;
};

export type RuntimeCondition = {
  id: string;
  type: 'OBJECT_EXISTS' | 'MATERIAL_PRESENT' | 'MATERIAL_AMOUNT' | 'MATERIAL_PHASE_IS' | 'CONNECTION_EXISTS' | 'VALUE_COMPARE' | 'TEMPERATURE_IN_RANGE' | 'PH_IN_RANGE' | 'HEATING_STARTED' | 'COOLING_STARTED' | 'MIXING_STARTED' | 'TRANSFER_COMPLETED' | 'REACTION_OBSERVED' | 'PRODUCT_FORMED';
  targetAlias?: string;
  materialId?: string;
  portId?: string;
  fromAlias?: string;
  fromPortId?: string;
  toAlias?: string;
  toPortId?: string;
  operator: 'EQ' | 'GTE' | 'LTE';
  value?: number | null;
  maxValue?: number | null;
  unit?: string;
};
export type RuntimeRuleGroup = { id?: string; operator: 'ALL' | 'ANY' | 'NOT'; conditions: RuntimeRuleNode[] };
export type RuntimeRuleNode = RuntimeCondition | RuntimeRuleGroup;
export const isRuntimeRuleGroup = (node: RuntimeRuleNode): node is RuntimeRuleGroup => !('type' in node);
export const flattenRuntimeConditions = (rule: RuntimeRuleGroup): RuntimeCondition[] => rule.conditions.flatMap((node) => isRuntimeRuleGroup(node) ? flattenRuntimeConditions(node) : [node]);

export type RuntimeEquipment = { id: string; code: string; rendererKey: string; ports: JsonObject[]; capabilities: string[]; translations: JsonObject; raw: JsonObject };
export type RuntimeMaterial = { id: string; code: string; formula: string; phase: string; color: string; opacity: number; translations: JsonObject; raw: JsonObject };
export type RuntimeCatalog = { equipment: RuntimeEquipment[]; materials: RuntimeMaterial[]; equipmentById: Record<string, RuntimeEquipment>; materialsById: Record<string, RuntimeMaterial> };
export type RuntimeLevelIntro = { id: number | string; scenarioId: string; title: RuntimeLocalizedText; objective: RuntimeLocalizedText; learningPoints: RuntimeLocalizedText[]; duration: RuntimeLocalizedText; allowedEquipment: string[]; allowedMaterials: string[] };

export type RuntimeStep = {
  id: string;
  title: string;
  instruction: string;
  hints: RuntimeHint[];
  completionRule: RuntimeRuleGroup;
  legacyCheck?: (engine: unknown) => boolean;
};

export type RuntimeScenario = {
  id: string;
  code: string;
  title: string;
  description: string;
  successTitle?: string;
  successDescription?: string;
  source: 'backend' | 'legacy' | 'draft';
  mode: RuntimeMode;
  initialScene?: ScenarioScene;
  equipmentIds: string[];
  materialIds: string[];
  catalog: RuntimeCatalog;
  steps: RuntimeStep[];
  raw?: JsonObject;
};

export type RuntimeLoadState = {
  scenario: RuntimeScenario | null;
  level: JsonObject | null;
  attempt: JsonObject | null;
  loading: boolean;
  error: string | null;
};
