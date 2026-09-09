import type { JsonObject } from '@/shared/api/contracts/platform';
import type { Locale, SandboxMode, ScenarioFactType, ScenarioHintType, ScenarioRuleOperator } from '@/shared/types/catalog';

export type ScenarioTranslation = { name: string; description: string };
export type ScenarioResourceSet = { equipmentIds: string[]; materialIds: string[] };
export type ScenarioContent = { materialId: string; amount: number; unit: 'µL'|'mL'|'L'|'mg'|'g'|'kg'|'mmol'|'mol'; phase: string };
export type ScenarioSceneObject = { id: string; alias: string; equipmentId: string; x: number; y: number; width: number; height: number; rotation: number; temperatureC: number; contents: ScenarioContent[] };
export type ScenarioConnection = { id: string; fromAlias: string; fromPortId: string; toAlias: string; toPortId: string };
export type ScenarioScene = { objects: ScenarioSceneObject[]; connections: ScenarioConnection[] };
export type ScenarioCondition = { id: string; type: ScenarioFactType; targetAlias: string; materialId: string; portId: string; fromAlias: string; fromPortId: string; toAlias: string; toPortId: string; operator: 'EQ'|'GTE'|'LTE'; value: number|null; maxValue?: number|null; unit: string };
export type ScenarioRuleGroup = { id: string; operator: ScenarioRuleOperator; conditions: ScenarioRuleNode[] };
export type ScenarioRuleNode = ScenarioCondition | ScenarioRuleGroup;
export type ScenarioCompletionRule = ScenarioRuleGroup;
export const isScenarioRuleGroup = (node: ScenarioRuleNode): node is ScenarioRuleGroup => !('type' in node);
export const flattenScenarioConditions = (rule: ScenarioRuleGroup): ScenarioCondition[] => rule.conditions.flatMap((node) => isScenarioRuleGroup(node) ? flattenScenarioConditions(node) : [node]);
export const mapScenarioRuleConditions = (rule: ScenarioRuleGroup, map: (condition: ScenarioCondition) => ScenarioCondition | null): ScenarioRuleGroup => ({
  ...rule,
  conditions: rule.conditions.reduce<ScenarioRuleNode[]>((nodes, node) => {
    if (isScenarioRuleGroup(node)) nodes.push(mapScenarioRuleConditions(node, map));
    else { const next = map(node); if (next) nodes.push(next); }
    return nodes;
  }, []),
});
export type ScenarioHint = { id: string; type: ScenarioHintType; translations: Record<Locale,{text:string}>; targetAlias: string; targetPortId: string; fromAlias: string; fromPortId: string; toAlias: string; toPortId: string };
export type ScenarioStepTranslation = { title: string; instruction: string };
export type ScenarioStep = { id:string; order:number; translations:Record<Locale,ScenarioStepTranslation>; expectedReactionId:string; completionRule:ScenarioCompletionRule; hints:ScenarioHint[] };
export type ScenarioDraft = { id?:string; code:string; status:string; version?:number; translations:Record<Locale,ScenarioTranslation>; resources:ScenarioResourceSet; initialScene:ScenarioScene; steps:ScenarioStep[]; raw:JsonObject };
export type ScenarioCanvasMode = Extract<SandboxMode,'ADMIN_AUTHORING'|'ADMIN_PREVIEW'>;
