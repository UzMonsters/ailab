import type { Locale, EntityStatus } from '@/shared/types/catalog';

export type LevelDifficulty = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export type LevelTranslation = {
  title: string;
  summary: string;
  goal: string;
  intro: string;
  learningObjectives: string[];
};

export type LevelDraft = {
  id?: string;
  trackId: string;
  levelNumber: number;
  order: number;
  difficulty: LevelDifficulty;
  estimatedMinutes: number;
  status: EntityStatus;
  version?: number;
  scenarioId: string;
  translations: Record<Locale, LevelTranslation>;
};

export const emptyLevelTranslation = (): LevelTranslation => ({
  title: '',
  summary: '',
  goal: '',
  intro: '',
  learningObjectives: [],
});

export const emptyLevel = (): LevelDraft => ({
  trackId: 'track-chemistry',
  levelNumber: 1,
  order: 1,
  difficulty: 'BEGINNER',
  estimatedMinutes: 10,
  status: 'DRAFT',
  scenarioId: '',
  translations: {
    ru: emptyLevelTranslation(),
    uz: emptyLevelTranslation(),
    en: emptyLevelTranslation(),
  },
});

export const difficultyLabel: Record<LevelDifficulty, Record<Locale, string>> = {
  BEGINNER: { ru: 'Начинающий', uz: 'Boshlang\'ich', en: 'Beginner' },
  INTERMEDIATE: { ru: 'Средний', uz: 'O\'rta', en: 'Intermediate' },
  ADVANCED: { ru: 'Продвинутый', uz: 'Yuqori', en: 'Advanced' },
};

export const TRACK_OPTIONS = [
  { id: 'track-chemistry', label: { ru: 'Химия', uz: 'Kimyo', en: 'Chemistry' } },
] as const;
