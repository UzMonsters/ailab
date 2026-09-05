import type { Locale } from '@/shared/api/contracts/definitions';

export type { Locale };

export const CONTENT_LOCALES = ['ru', 'uz', 'en'] as const satisfies readonly Locale[];

export type EntityStatus = 'DRAFT' | 'IN_REVIEW' | 'PUBLISHED' | 'ARCHIVED';

export type EquipmentKind =
  | 'ANALYTICAL_BALANCE'
  | 'LABORATORY_BALANCE'
  | 'THERMOMETER'
  | 'TEMPERATURE_PROBE'
  | 'VOLUMETRIC_FLASK'
  | 'GRADUATED_CYLINDER'
  | 'VOLUMETRIC_PIPETTE'
  | 'BURETTE'
  | 'PH_METER'
  | 'MAGNETIC_STIRRER'
  | 'HOT_PLATE';

export type EquipmentCategory = 'CONTAINER' | 'HEATER' | 'SENSOR' | 'APPARATUS' | 'TOOL';

export type EquipmentCapability =
  | 'CONTAINER'
  | 'LIQUID_HOLDING'
  | 'HEATING_RECEPTIVE'
  | 'SWIRLABLE'
  | 'HEAT_SOURCE'
  | 'FLAME_GENERATOR'
  | 'MAGNETIC_STIRRER'
  | 'SENSOR'
  | 'TEMPERATURE_MEASURING'
  | 'PH_MEASURING'
  | 'CONDENSER'
  | 'COOLING'
  | 'TRANSFER'
  | 'FILTRATION'
  | 'MEASURING_DISPENSE'
  | 'TITRATION'
  | 'PRECISION_DISPENSE';

export type PortKind =
  | 'FLUID'
  | 'GAS'
  | 'SENSOR'
  | 'THERMAL'
  | 'POWER';

export type PortDirection = 'INPUT' | 'OUTPUT' | 'BIDIRECTIONAL';
export type PortMedium = 'LIQUID' | 'GAS' | 'COOLANT' | 'THERMAL' | 'ELECTRICAL' | 'GENERIC';
export type MaterialKind = 'ELEMENT' | 'COMPOUND' | 'MIXTURE' | 'SOLUTION' | 'SAMPLE' | 'OTHER';
export type MaterialPhase = 'SOLID' | 'LIQUID' | 'GAS' | 'PLASMA' | 'AQUEOUS' | 'UNKNOWN';
export type ReactionRole = 'REACTANT' | 'PRODUCT' | 'CATALYST' | 'SOLVENT';
export type ScenarioHintType = 'TEXT' | 'HIGHLIGHT' | 'ARROW' | 'GHOST_PLACEMENT' | 'CONNECT_PORTS';
export type ScenarioFactType = 'OBJECT_EXISTS' | 'MATERIAL_PRESENT' | 'MATERIAL_AMOUNT' | 'MATERIAL_PHASE_IS' | 'CONNECTION_EXISTS' | 'VALUE_COMPARE' | 'TEMPERATURE_IN_RANGE' | 'PH_IN_RANGE' | 'HEATING_STARTED' | 'COOLING_STARTED' | 'MIXING_STARTED' | 'TRANSFER_COMPLETED' | 'REACTION_OBSERVED' | 'PRODUCT_FORMED';
export type ScenarioRuleOperator = 'ALL' | 'ANY' | 'NOT';
export type SandboxMode = 'NORMAL' | 'LEARNING' | 'ADMIN_AUTHORING' | 'ADMIN_PREVIEW';

export type LocalizedContent<T> = Partial<Record<Locale, T>>;

export type UnitValue = {
  value: number | null;
  unit: string;
};
