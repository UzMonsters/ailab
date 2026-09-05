import type { JsonObject } from '@/shared/api/contracts/platform';
import { scenarioDraft } from '@/widgets/admin/scenario/scenario.model';
import type { RuntimeHint, RuntimeLocale, RuntimeRuleGroup, RuntimeScenario } from './runtime.types';
import type { ScenarioDraft } from '@/widgets/admin/scenario/scenario.types';
import { createRuntimeCatalog, runtimeCatalogFromPayload } from './runtimeCatalog';

const object = (value: unknown): JsonObject => value && typeof value === 'object' && !Array.isArray(value) ? value as JsonObject : {};
const array = (value: unknown): unknown[] => Array.isArray(value) ? value : [];
const text = (value: unknown, fallback = '') => typeof value === 'string' && value.trim() ? value : fallback;

const localized = (value: unknown, locale: RuntimeLocale): JsonObject => {
  const translations = object(value);
  return object(translations[locale] ?? translations.en ?? translations.ru ?? translations.uz);
};

const findScenario = (payload: JsonObject): JsonObject => {
  const candidates = [payload.scenarioSnapshot, payload.scenario, object(payload.level).scenario, object(payload.levelSnapshot).scenario];
  return object(candidates.find((candidate) => Object.keys(object(candidate)).length));
};

export function normalizeScenarioRuntime(payload: JsonObject, locale: RuntimeLocale, mode: RuntimeScenario['mode'] = 'LEARNING'): RuntimeScenario | null {
  const raw = findScenario(payload);
  if (!Object.keys(raw).length) return null;
  const draft = scenarioDraft(raw);
  const copy = localized(raw.translations, locale);
  const code = text(raw.code, text(raw.id, 'scenario'));
  return {
    id: text(raw.id, code),
    code,
    title: text(copy.name ?? copy.title, text(raw.name ?? raw.title, code)),
    description: text(copy.description ?? copy.summary, text(raw.description)),
    successTitle: text(copy.successTitle ?? raw.successTitle),
    successDescription: text(copy.successDescription ?? raw.successDescription),
    source: 'backend',
    mode,
    initialScene: draft.initialScene,
    equipmentIds: draft.resources.equipmentIds,
    materialIds: draft.resources.materialIds,
    catalog: runtimeCatalogFromPayload(payload, raw),
    steps: draft.steps.map((step, index) => {
      const stepCopy = step.translations[locale] ?? step.translations.en;
      return {
        id: step.id || `step-${index + 1}`,
        title: text(stepCopy.title, `Step ${index + 1}`),
        instruction: text(stepCopy.instruction),
        completionRule: step.completionRule as RuntimeRuleGroup,
        hints: step.hints.map((hint) => ({
          id: hint.id,
          type: hint.type,
          text: hint.translations[locale]?.text ?? hint.translations.en.text,
          targetAlias: hint.targetAlias || undefined,
          targetPortId: hint.targetPortId || undefined,
          fromAlias: hint.fromAlias || undefined,
          fromPortId: hint.fromPortId || undefined,
          toAlias: hint.toAlias || undefined,
          toPortId: hint.toPortId || undefined,
        } satisfies RuntimeHint)),
      };
    }),
    raw,
  };
}

export const scenarioPayloadFromAttempt = (payload: JsonObject): JsonObject => findScenario(payload);

export const runtimeStepIndex = (attempt: JsonObject | null) => {
  if (!attempt) return 0;
  const value = attempt.currentStepIndex ?? attempt.stepIndex ?? object(attempt.progress).currentStepIndex;
  return typeof value === 'number' && Number.isFinite(value) ? Math.max(0, value) : 0;
};

export function runtimeScenarioFromDraft(draft: ScenarioDraft, locale: RuntimeLocale, sourceCatalog: { equipment?: JsonObject[]; materials?: JsonObject[] } = {}): RuntimeScenario {
  return {
    id: draft.id ?? (draft.code || 'draft-scenario'), code: draft.code || 'draft-scenario', title: draft.translations[locale].name || draft.translations.en.name || 'Untitled Scenario', description: draft.translations[locale].description || draft.translations.en.description,
    source: 'draft', mode: 'ADMIN_PREVIEW', initialScene: draft.initialScene, equipmentIds: draft.resources.equipmentIds, materialIds: draft.resources.materialIds, catalog: createRuntimeCatalog(sourceCatalog.equipment, sourceCatalog.materials),
    steps: draft.steps.map((step, index) => ({ id: step.id, title: step.translations[locale].title || step.translations.en.title || `Step ${index + 1}`, instruction: step.translations[locale].instruction || step.translations.en.instruction, completionRule: step.completionRule as RuntimeRuleGroup, hints: step.hints.map((hint) => ({ id: hint.id, type: hint.type, text: hint.translations[locale].text || hint.translations.en.text, targetAlias: hint.targetAlias || undefined, targetPortId: hint.targetPortId || undefined, fromAlias: hint.fromAlias || undefined, fromPortId: hint.fromPortId || undefined, toAlias: hint.toAlias || undefined, toPortId: hint.toPortId || undefined })) })),
  };
}
