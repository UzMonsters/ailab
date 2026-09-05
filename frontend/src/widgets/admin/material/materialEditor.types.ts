import type { JsonObject } from '@/shared/api/contracts/platform';
import type { LocalizedContent, Locale, MaterialKind, MaterialPhase, ReactionRole, UnitValue } from '@/shared/types/catalog';

export type MaterialTranslation = { name: string; shortDescription: string; detailedDescription: string; usageDescription: string; safetyInformation: string; educationalNotes: string };
export type MaterialSafety = { hazard: string; severity: string; signalWord: string; ppe: string[]; handling: string; storage: string; firstAid: string; disposal: string; incompatibilities: string[] };
export type ReactionRelationship = { reactionId: string; role: ReactionRole; notes: string };
export type MaterialLinks = { bookId: string; chapterId: string; pageId: string; wikipedia: Partial<Record<Locale, string>>; references: Array<{ id: string; label: string; url: string; locale: Locale | '' }> };
export type MaterialDraft = {
  id?: string; code: string; internalName: string; formula: string; kind: MaterialKind; phase: MaterialPhase; tags: string[]; status: string; version?: number; publishedVersion: number | null;
  translations: LocalizedContent<MaterialTranslation>; appearance: { color: string; opacity: number; texture: string; particleColor: string };
  properties: { casNumber: string; molarMass: UnitValue; density: UnitValue; meltingPoint: UnitValue; boilingPoint: UnitValue; solubility: UnitValue; concentrationM: number | null; ph: number | null };
  safety: MaterialSafety; reactionRelationships: ReactionRelationship[]; links: MaterialLinks; raw: JsonObject;
};
export type MaterialValidationIssue = { id: string; severity: 'error' | 'warning'; tab: string; message: string };
