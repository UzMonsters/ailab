import type { JsonObject } from '@/shared/api/contracts/platform';
import type { RuntimeLevelIntro, RuntimeLocalizedText, RuntimeScenario } from './runtime.types';

const object = (value: unknown): JsonObject => value && typeof value === 'object' && !Array.isArray(value) ? value as JsonObject : {};
const text = (value: unknown, fallback = '') => typeof value === 'string' && value.trim() ? value : fallback;
const locales = ['ru', 'uz', 'en'] as const;
const localized = (translations: JsonObject, key: string, fallback: string): RuntimeLocalizedText => Object.fromEntries(locales.map((locale) => [locale, text(object(translations[locale])[key], fallback)])) as RuntimeLocalizedText;

export function normalizeRuntimeLevelIntro(levelValue: JsonObject | null, attempt: JsonObject | null, scenario: RuntimeScenario | null): RuntimeLevelIntro | null {
  if (!scenario) return null;
  const attemptLevel = object(attempt?.level ?? attempt?.levelSnapshot), level = levelValue && Object.keys(levelValue).length ? levelValue : attemptLevel;
  if (!Object.keys(level).length && !attempt) return null;
  const translations = object(level.translations), minutes = Number(level.estimatedMinutes ?? level.durationMinutes ?? 10);
  const title = Object.fromEntries(locales.map((locale) => [locale, text(object(translations[locale]).title, scenario.title)])) as RuntimeLocalizedText;
  const objective = Object.fromEntries(locales.map((locale) => [locale, text(object(translations[locale]).goal ?? object(translations[locale]).summary, scenario.description)])) as RuntimeLocalizedText;
  const objectiveRows = locales.map((locale) => Array.isArray(object(translations[locale]).learningObjectives) ? object(translations[locale]).learningObjectives as unknown[] : []);
  const learningPoints = Array.from({ length: Math.max(0, ...objectiveRows.map((rows) => rows.length)) }, (_, index) => Object.fromEntries(locales.map((locale, localeIndex) => [locale, text(objectiveRows[localeIndex][index], '—')])) as RuntimeLocalizedText);
  return { id: String(level.levelNumber ?? level.order ?? level.id ?? attempt?.levelId ?? ''), scenarioId: scenario.id, title, objective, learningPoints, duration: localized({}, 'duration', `~${Number.isFinite(minutes) ? minutes : 10} minutes`), allowedEquipment: scenario.equipmentIds, allowedMaterials: scenario.materialIds };
}
