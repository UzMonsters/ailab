import type { Connection, Item } from '../types';
import type { RuntimeCondition, RuntimeRuleGroup, RuntimeRuleNode, RuntimeStep } from './runtime.types';
import { areRuntimeUnitsCompatible, convertRuntimeUnit } from './unitConversion';

export type RuntimeFacts = { items: Item[]; connections: Connection[]; reactionIds?: string[]; formedMaterialIds?: string[]; aliases?: Record<string, string>; events?: RuntimeEventFact[] };
export type RuntimeEventFact = { event: string; payload?: Record<string, unknown> };

const matchesAlias = (item: Item, alias: string | undefined, aliases: Record<string, string>) => !alias || item.id === alias || item.id === aliases[alias] || item.metadata?.scenarioAlias === alias;
const compare = (actual: number, condition: RuntimeCondition) => condition.operator === 'LTE' ? actual <= Number(condition.value ?? 0) : condition.operator === 'EQ' ? Math.abs(actual - Number(condition.value ?? 0)) < 1e-9 : actual >= Number(condition.value ?? 0);
const contentRecord = (value: unknown) => value && typeof value === 'object' ? value as Record<string, unknown> : {};
const contentIdentity = (content: Record<string, unknown>) => [content.materialId, content.formula, content.name].filter(Boolean).map(String);

export function collectRuntimeFacts(
  items: Item[],
  connections: Connection[],
  authoritative: Pick<RuntimeFacts, 'reactionIds' | 'formedMaterialIds'> = {},
  events: RuntimeEventFact[] = [],
): RuntimeFacts {
  const aliases: Record<string, string> = {}, reactionIds = new Set<string>(), formedMaterialIds = new Set<string>();
  authoritative.reactionIds?.forEach((id) => reactionIds.add(id));
  authoritative.formedMaterialIds?.forEach((id) => formedMaterialIds.add(id));
  items.forEach((item) => {
    const alias = typeof item.metadata?.scenarioAlias === 'string' ? item.metadata.scenarioAlias : '';
    if (alias) aliases[alias] = item.id;
    item.contents.forEach((rawContent) => {
      const content = contentRecord(rawContent), metadata = contentRecord(content.metadata);
      const reactionId = typeof metadata.reactionId === 'string' ? metadata.reactionId : '';
      if (reactionId) reactionIds.add(reactionId);
      if (reactionId || metadata.source === 'local-reaction') contentIdentity(content).forEach((id) => formedMaterialIds.add(id));
    });
  });
  return { items, connections, aliases, reactionIds: [...reactionIds], formedMaterialIds: [...formedMaterialIds], events };
}

const endpointId = (alias: string | undefined, aliases: Record<string, string>) => alias ? aliases[alias] ?? alias : '';
const eventTargets = (event: RuntimeEventFact, aliases: Record<string, string>, alias: string | undefined) => {
  if (!alias) return true;
  const targetId = endpointId(alias, aliases);
  const payload = event.payload ?? {};
  return [payload.itemId, payload.equipmentId, payload.vesselId, payload.sourceId, payload.targetId].some((id) => String(id ?? '') === targetId);
};
const hasEvent = (facts: RuntimeFacts, event: string, alias?: string) => (facts.events ?? []).some((item) => item.event === event && eventTargets(item, facts.aliases ?? {}, alias));
const range = (actual: number, condition: RuntimeCondition, sourceUnit: string) => {
  const minimum = condition.value === null || condition.value === undefined ? null : (condition.unit ? convertRuntimeUnit(condition.value, condition.unit, sourceUnit) : condition.value);
  const maximum = condition.maxValue === null || condition.maxValue === undefined ? null : (condition.unit ? convertRuntimeUnit(condition.maxValue, condition.unit, sourceUnit) : condition.maxValue);
  return minimum !== null && minimum !== undefined && maximum !== null && maximum !== undefined && actual >= minimum && actual <= maximum;
};
const exactConnection = (condition: RuntimeCondition, facts: RuntimeFacts) => {
  const aliases = facts.aliases ?? {}, from = endpointId(condition.fromAlias, aliases), to = endpointId(condition.toAlias, aliases);
  if (!from || !to || !condition.fromPortId || !condition.toPortId) return false;
  const fromPort = facts.items.find((item) => item.id === from)?.ports?.find((port) => port.id === condition.fromPortId);
  const toPort = facts.items.find((item) => item.id === to)?.ports?.find((port) => port.id === condition.toPortId);
  const reverseAllowed = fromPort?.direction === 'bidirectional' && toPort?.direction === 'bidirectional';
  return facts.connections.some((connection) => {
    const direct = connection.from === from && connection.fromPort === condition.fromPortId && connection.to === to && connection.toPort === condition.toPortId;
    const reverse = connection.from === to && connection.fromPort === condition.toPortId && connection.to === from && connection.toPort === condition.fromPortId;
    return direct || (reverseAllowed && reverse);
  });
};

export function evaluateRuntimeCondition(condition: RuntimeCondition, facts: RuntimeFacts) {
  const aliases = facts.aliases ?? {};
  const targets = facts.items.filter((item) => matchesAlias(item, condition.targetAlias, aliases));
  if (condition.type === 'OBJECT_EXISTS') return targets.length > 0;
  if (condition.type === 'MATERIAL_PRESENT') return targets.some((item) => item.contents.some((raw) => contentIdentity(contentRecord(raw)).includes(condition.materialId ?? '') && Number(contentRecord(raw).amount ?? 0) > 0));
  if (condition.type === 'MATERIAL_PHASE_IS') return targets.some((item) => item.contents.some((raw) => {
    const content = contentRecord(raw);
    return contentIdentity(content).includes(condition.materialId ?? '') && String(content.phase ?? '').toUpperCase() === String(condition.portId ?? '').toUpperCase();
  }));
  if (condition.type === 'MATERIAL_AMOUNT') {
    if (condition.value === null || condition.value === undefined || !condition.unit) return false;
    let actual = 0, compatible = false;
    targets.forEach((item) => item.contents.forEach((raw) => {
      const content = contentRecord(raw);
      if (!contentIdentity(content).includes(condition.materialId ?? '')) return;
      const sourceUnit = String(content.unit ?? '');
      if (!areRuntimeUnitsCompatible(sourceUnit, condition.unit)) return;
      const converted = convertRuntimeUnit(Number(content.amount ?? 0), sourceUnit, condition.unit);
      if (converted !== null) { actual += converted; compatible = true; }
    }));
    return compatible && compare(actual, condition);
  }
  if (condition.type === 'CONNECTION_EXISTS') {
    if (condition.fromAlias || condition.toAlias) return exactConnection(condition, facts);
    const targetIds = new Set(targets.map((item) => item.id));
    return facts.connections.some((connection) => (!targetIds.size || targetIds.has(connection.from) || targetIds.has(connection.to)) && (!condition.portId || connection.fromPort === condition.portId || connection.toPort === condition.portId));
  }
  if (condition.type === 'REACTION_OBSERVED') return facts.reactionIds?.includes(condition.materialId ?? condition.portId ?? '') ?? false;
  if (condition.type === 'PRODUCT_FORMED') return facts.formedMaterialIds?.includes(condition.materialId ?? '') ?? false;
  if (condition.type === 'HEATING_STARTED') return hasEvent(facts, 'HEAT_START', condition.targetAlias);
  if (condition.type === 'COOLING_STARTED') return hasEvent(facts, 'COOL', condition.targetAlias);
  if (condition.type === 'MIXING_STARTED') return hasEvent(facts, 'MIXING_STARTED', condition.targetAlias) || hasEvent(facts, 'STIR_START', condition.targetAlias);
  if (condition.type === 'TRANSFER_COMPLETED') return (facts.events ?? []).some((event) => {
    const payload = event.payload ?? {};
    return event.event === 'POUR'
      && (!condition.fromAlias || String(payload.sourceId ?? '') === endpointId(condition.fromAlias, aliases))
      && (!condition.toAlias || String(payload.targetId ?? '') === endpointId(condition.toAlias, aliases))
      && (!condition.materialId || String(payload.materialId ?? '') === condition.materialId);
  });
  if (condition.type === 'TEMPERATURE_IN_RANGE') return targets.some((item) => range(Number(item.temperature), condition, '°C'));
  if (condition.type === 'PH_IN_RANGE') return targets.some((item) => {
    const pH = item.metadata?.pH ?? item.metadata?.ph;
    return typeof pH === 'number' && range(pH, condition, 'pH');
  });
  return targets.some((item) => {
    const key = condition.portId ?? 'temperatureC';
    const values: Record<string, { value: number; unit: string }> = { temperature: { value: item.temperature, unit: '°C' }, temperatureC: { value: item.temperature, unit: '°C' }, volume: { value: item.volumeMl, unit: 'mL' }, volumeMl: { value: item.volumeMl, unit: 'mL' }, mass: { value: item.massG, unit: 'g' }, massG: { value: item.massG, unit: 'g' }, pressure: { value: item.pressureBar, unit: 'bar' }, pressureBar: { value: item.pressureBar, unit: 'bar' } };
    const known = values[key], metadataValue = item.metadata?.[key];
    if (!known && typeof metadataValue !== 'number') return false;
    const actual = known?.value ?? metadataValue as number, sourceUnit = known?.unit ?? condition.unit;
    const normalized = condition.unit ? convertRuntimeUnit(actual, sourceUnit, condition.unit) : actual;
    return normalized !== null && Number.isFinite(normalized) && compare(normalized, condition);
  });
}

const isRuleGroup = (node: RuntimeRuleNode): node is RuntimeRuleGroup => !('type' in node);
export function evaluateRuntimeRule(rule: RuntimeRuleGroup, facts: RuntimeFacts) {
  const values = rule.conditions.map((node) => isRuleGroup(node) ? evaluateRuntimeRule(node, facts) : evaluateRuntimeCondition(node, facts));
  if (!values.length) return false;
  if (rule.operator === 'ANY') return values.some(Boolean);
  if (rule.operator === 'NOT') return values.every((value) => !value);
  return values.every(Boolean);
}

export function evaluateRuntimeStep(step: RuntimeStep, facts: RuntimeFacts, engine?: unknown) {
  if (step.legacyCheck) return step.legacyCheck(engine);
  return evaluateRuntimeRule(step.completionRule, facts);
}
