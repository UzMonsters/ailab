export type Vector2 = { x: number; y: number };
export type Size2 = { width: number; height: number };
export type BoundingBox = Vector2 & Size2;

export type PortType = 'Liquid' | 'Gas' | 'Electric' | 'Thermal' | 'Sensor' | 'Glass';
export type PortDirection = 'in' | 'out' | 'bidirectional';

export interface ContainerCapability {
  capacity: number;
}

export interface HeaterCapability {
  maxTemperature: number;
}

export interface SensorCapability {
  measures: string;
}

export interface ScaleCapability {
  maxMassG: number;
  precisionG: number;
}

export interface ThermometerCapability {
  minC: number;
  maxC: number;
  precisionC: number;
  accuracyC?: number;
  responseTimeS?: number;
}

export type VesselIntegrity = 'intact' | 'stressed' | 'microcracked' | 'cracked' | 'leaking' | 'shattered';
export interface ThermalState {
  ambientTemperature: number;
  surfaceTemperature: number;
  wallTemperature: number;
  bottomTemperature: number;
  contentsTemperature: number;
  gasTemperature: number;
  stress: number;
}

export interface Capabilities {
  container?: ContainerCapability;
  heater?: HeaterCapability;
  sensor?: SensorCapability;
  scale?: ScaleCapability;
  temperatureSensor?: ThermometerCapability;
  phSensor?: Record<string, unknown>;
  pressureSensor?: Record<string, unknown>;
  condenser?: Record<string, unknown>;
  thermalOutput?: { powerW: number };
  cooler?: { minTempC: number };
  heatTarget?: boolean;
  pourable?: boolean;
  stirrable?: boolean;
  [key: string]: unknown;
}

export interface PortDefinition {
  id: string;
  name: string;
  role?: string;
  type: PortType;
  position: Vector2;
  direction: PortDirection;
  capacity?: number;
  requiredConnector?: 'glass-tube' | 'rubber-hose' | 'wire' | 'direct';
  isOpen?: boolean;
}

export interface LaboratoryObjectSnapshot {
  id: string;
  type: string;
  capabilities: Capabilities;
  position: Vector2;
  rotation: number;
  scale: Vector2;
  boundingBox: Size2;
  zIndex: number;
  visible: boolean;
  selected: boolean;
  hovered: boolean;
  locked: boolean;
  state: string;
  material?: Record<string, unknown>;
  properties: Record<string, unknown>;
  ports: PortDefinition[];
  connections: string[];
  contents: Record<string, unknown>[];
  animations: string[];
  history: string[];
  metadata: Record<string, unknown>;
}

export interface EquipmentDefinition {
  type: string;
  labelKey?: string;
  rendererId?: string;
  capabilities?: Capabilities;
  label: string;
  defaultSize: Size2;
  capacity?: number;
  allowedConnections: PortType[];
  defaultPorts: PortDefinition[];
  metadata?: Record<string, unknown>;
  historyYear?: number;
  historyText?: string;
  referenceLink?: string;
}

export type ContentComponent = {
  materialId: string;
  amount: number;
  unit: 'mL' | 'g' | 'mol';
  phase: 'liquid' | 'gas' | 'solid' | 'aqueous' | 'unknown';
  temperatureC?: number;
  meltingPointC?: number;
  boilingPointC?: number;
  heatCapacityJPerG?: number;
  densityGPerMl?: number;
  latentHeatVaporizationJPerG?: number;
  latentHeatFusionJPerG?: number;
  metadata?: Record<string, unknown>;
};
