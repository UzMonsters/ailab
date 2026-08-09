export type Locale = 'en' | 'ru' | 'uz';

export interface ApiError {
  timestamp?: string;
  status: number;
  error?: string;
  message: string;
  path?: string;
  fieldViolations?: Array<{ field: string; message: string }>;
  errors?: Record<string, string>;
}

export interface AuthRegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthRegisterResponse {
  id: string;
  username: string;
  email: string;
}

export interface AuthLoginRequest {
  email: string;
  password: string;
}

export interface AuthTokenResponse {
  accessToken: string;
  tokenType: 'Bearer';
  expiresInSeconds: number;
}

export interface AuthSuccessResponse {
  message: string;
}

export interface UserMeResponse {
  id: string;
  username: string;
  email: string;
  role: 'ROLE_USER' | 'ROLE_ADMIN' | 'ROLE_BANNED';
  avatarUrl: string | null;
  createdAt: string;
}

export interface UserPublicResponse {
  id: string;
  username: string;
  avatarUrl: string | null;
  createdAt: string;
}

export interface UserUpdateRequest {
  username?: string;
  avatarUrl?: string;
}

export interface UserPreferencesResponse {
  theme: 'LIGHT' | 'DARK' | 'SYSTEM';
  defaultTemperatureUnit: 'KELVIN' | 'CELSIUS' | 'FAHRENHEIT';
  defaultPressureUnit: 'PASCAL' | 'BAR' | 'ATMOSPHERE';
  defaultVolumeUnit: 'LITER' | 'MILLILITER' | 'CUBIC_METER';
  autoSaveEnabled: boolean;
}

export interface UserPreferencesUpdateRequest {
  theme?: 'LIGHT' | 'DARK' | 'SYSTEM';
  defaultTemperatureUnit?: 'KELVIN' | 'CELSIUS' | 'FAHRENHEIT';
  defaultPressureUnit?: 'PASCAL' | 'BAR' | 'ATMOSPHERE';
  defaultVolumeUnit?: 'LITER' | 'MILLILITER' | 'CUBIC_METER';
  autoSaveEnabled?: boolean;
}

export interface UserStatisticsResponse {
  totalExperimentsRun: number;
  totalFormulasParsed: number;
  totalEquationsBalanced: number;
  safetyViolationsTriggered: number;
  lastActiveTimestamp: string;
}

export interface UserAvatarRequest {
  avatarUrl: string;
}

export interface AdminUserResponse {
  id: string;
  username: string;
  email: string;
  role: 'ROLE_USER' | 'ROLE_ADMIN' | 'ROLE_BANNED';
  avatarUrl: string | null;
  active?: boolean;
  level?: number;
  createdAt?: string;
}

export interface AdminUpdateUserRequest {
  username?: string;
  email?: string;
  role?: 'ROLE_USER' | 'ROLE_ADMIN' | 'ROLE_BANNED';
  active?: boolean;
}

export interface ChemicalFormula {
  formula: string;
  name: string;
  elements: Record<string, number>;
  charge: number;
  molarMass: number;
  hydrateWaterCount: number;
  hydrationLabel: string;
  empiricalFormula: string;
  formulaUnits: number;
}

export interface BalancedEquation {
  originalEquation: string;
  balancedEquation: string;
  coefficients: number[];
  reactants: BalancedSide[];
  products: BalancedSide[];
  isBalanced: boolean;
  atomBalance: Record<string, number>;
}

export interface BalancedSide {
  species: string;
  coefficient: number;
}

export interface ElementSummary {
  atomicNumber: number;
  symbol: string;
  name: string;
  atomicMass: number;
  category: string;
  group: number;
  period: number;
  electronConfiguration: string;
}

export interface ElementDetails {
  atomicNumber: number;
  symbol: string;
  name: string;
  atomicMass: number;
  category: string;
  group: number;
  period: number;
  block: string;
  electronConfiguration: string;
  electronegativity: number;
  ionizationEnergy: number;
  electronAffinity: number;
  oxidationStates: number[];
  density: number;
  meltingPoint: number;
  boilingPoint: number;
  discoveryYear: number;
  description: string;
}

export interface ElementPropertyDetails {
  atomicNumber: number;
  symbol: string;
  name: string;
  electronegativity: number;
  ionizationEnergy: number;
  electronAffinity: number;
  atomicRadius: number;
  covalentRadius: number;
  vanDerWaalsRadius: number;
  density: number;
  meltingPoint: number;
  boilingPoint: number;
  heatCapacity: number;
  thermalConductivity: number;
  thermalExpansion: number;
}

export interface CompoundSummary {
  id: string;
  code: string;
  name: string;
  formula: string;
  normalizedFormula: string;
  molecularWeight: number;
  category: string;
  description: string;
}

export interface CompoundDetails {
  id: string;
  code: string;
  name: string;
  formula: string;
  normalizedFormula: string;
  molecularWeight: number;
  category: string;
  description: string;
  casNumber: string;
  synonyms: string[];
}

export interface CompoundPhysicalPropertyDetails {
  compoundId: string;
  compoundCode: string;
  meltingPoint: number;
  boilingPoint: number;
  density: number;
  solubility: number;
  heatCapacity: number;
  formationEnthalpy: number;
  entropy: number;
  gibbsEnergy: number;
}

export interface ThermodynamicProfileDetails {
  compoundCode: string;
  formationEnthalpy: number;
  entropy: number;
  gibbsEnergy: number;
  heatCapacity: number;
  temperature: number;
  pressure: number;
  state: string;
}

export interface ReactionThermodynamicsResult {
  reactionCode: string;
  deltaH: number;
  deltaS: number;
  deltaG: number;
  equilibriumConstant: number;
  isSpontaneous: boolean;
  temperature: number;
  pressure: number;
  method: string;
}

export interface HessLawResult {
  targetReaction: string;
  reactionEnthalpy: number;
  steps: HessStep[];
  verificationSum: number;
}

export interface HessStep {
  reactionCode: string;
  multiplier: number;
  enthalpy: number;
}

export interface SensibleHeatResult {
  heatTransfer: number;
  mass: number;
  specificHeatCapacity: number;
  initialTemperature: number;
  finalTemperature: number;
  temperatureChange: number;
  unit: string;
}

export interface ThermalMixingResult {
  finalTemperature: number;
  totalHeatExchange: number;
  samples: ThermalSampleResult[];
}

export interface ThermalSampleResult {
  sampleId: string;
  heatExchange: number;
  temperatureChange: number;
}

export interface ReactionCalorimetryResult {
  reactionCode: string;
  heatReleased: number;
  reactionExtentMoles: number;
  temperature: number;
}

export interface AcidBaseResponse {
  systemType: string;
  ph: number;
  poh: number;
  hydroniumConcentration: number;
  hydroxideConcentration: number;
  kw: number;
  pKw: number;
  kActive: number | null;
  calculationMethod: string;
  solverStatus: string;
}

export interface BufferCalculationResult {
  ph: number;
  pKa: number;
  acidConcentration: number;
  baseConcentration: number;
  bufferCapacity: number;
  ionicStrength: number;
}

export interface BufferPreparationResult {
  acidMass: number;
  baseMass: number;
  acidVolume: number;
  baseVolume: number;
  acidSpeciesCode: string;
  baseSpeciesCode: string;
}

export interface BufferPerturbationResult {
  initialPh: number;
  finalPh: number;
  phChange: number;
  reagentType: string;
  reagentAmount: number;
}

export interface TitrationCurveResult {
  systemType: string;
  characteristicPoints: TitrationPoint[];
  curvePoints: TitrationCurvePoint[];
}

export interface TitrationPoint {
  label: string;
  ph: number;
  volumeAdded: number;
  description: string;
}

export interface TitrationCurvePoint {
  volumeAdded: number;
  ph: number;
}

export interface PolyproticTitrationCurveResult {
  familyCode: string;
  systemType: string;
  characteristicPoints: PolyproticTitrationPoint[];
  curvePoints: TitrationCurvePoint[];
}

export interface PolyproticTitrationPoint {
  label: string;
  ph: number;
  volumeAdded: number;
  stepNumber: number;
  description: string;
}

export interface RateEvaluationResult {
  reactionCode: string;
  rate: number;
  rateLawExpression: string;
  concentrations: Record<string, number>;
}

export interface IntegratedRateLawResult {
  compoundCode: string;
  initialConcentration: number;
  finalConcentration: number;
  order: number;
  rateConstant: number;
  duration: number;
}

export interface HalfLifeResult {
  compoundCode: string;
  halfLife: number;
  initialConcentration: number;
  order: number;
  rateConstant: number;
}

export interface ArrheniusResult {
  rateConstant: number;
  temperature: number;
  preExponentialFactor: number;
  activationEnergy: number;
  expressionForm: string;
}

export interface KineticProgressResult {
  reactionCode: string;
  timePoints: number[];
  speciesConcentrations: Record<string, number[]>;
}

export interface ElectrochemicalCellResult {
  cellPotential: number;
  cathodePotential: number;
  anodePotential: number;
  cellType: string;
  spontaneous: boolean;
  cathodeReaction: string;
  anodeReaction: string;
  overallReaction: string;
}

export interface NernstResult {
  cellPotential: number;
  standardCellPotential: number;
  temperature: number;
  reactionQuotient: number;
  nernstSlope: number;
}

export interface ElectrolysisResult {
  massDeposited: number;
  molesDeposited: number;
  chargePassed: number;
  duration: number;
  current: number;
  substanceCode: string;
  electronsTransferred: number;
}

export interface GasStateResult {
  pressure: number;
  volume: number;
  amount: number;
  temperature: number;
  model: string;
  solvedVariable: string;
}

export interface GasMixtureResult {
  totalPressure: number;
  partialPressures: Record<string, number>;
  moleFractions: Record<string, number>;
  components: GasMixtureComponentResult[];
}

export interface GasMixtureComponentResult {
  compoundCode: string;
  moles: number;
  moleFraction: number;
  partialPressure: number;
}

export interface SafetyEvaluationResult {
  stage: string;
  verdict: string;
  violations: SafetyViolation[];
  warnings: SafetyWarning[];
}

export interface SafetyViolation {
  ruleCode: string;
  message: string;
  severity: string;
}

export interface SafetyWarning {
  ruleCode: string;
  message: string;
  severity: string;
}

export interface SimulationState {
  sessionId: string;
  processCode: string;
  processVersion: number;
  version: number;
  status: string;
  temperature: Record<string, unknown>;
  pressure: Record<string, unknown>;
  containers: Record<string, unknown>[];
  apparatus: Record<string, unknown>[];
  createdAt: string;
  updatedAt: string;
}

export interface SimulationExecutionResult {
  sessionId: string;
  commandId: string;
  previousVersion: number;
  newVersion: number;
  stateDelta: Record<string, unknown>;
  executionLog: string[];
  timestamp: string;
}

export interface SimulationCalculationAudit {
  eventId: string;
  sessionId: string;
  commandId: string;
  inputs: Record<string, unknown>;
  formulas: string[];
  outputs: Record<string, unknown>;
  safetyEvaluations: Record<string, unknown>[];
  timestamp: string;
}

export interface CreateExperimentRequest {
  sessionId: string;
  processCode: string;
  processVersion: number;
  requestedAt: string;
}

export interface ExecuteOperationRequest {
  expectedStateVersion: number;
  idempotencyKey?: string;
  command: Record<string, unknown>;
}

export interface AppendEventRequest {
  expectedVersion: number;
  idempotencyKey?: string;
  payload: Record<string, unknown>;
}

export interface Workspace {
  id: string;
  name: string;
  science: 'chemistry' | 'physics' | 'biology';
  thumbnail?: string;
  createdAt: string;
  updatedAt: string;
  isFavorite: boolean;
  isDeleted: boolean;
}

export interface User {
  id: string;
  name: string;
  email: string;
  avatar?: string;
  role: 'user' | 'admin';
  discipline?: string;
  isOnline: boolean;
}

export interface Element {
  symbol: string;
  number: number;
  name: string;
  mass: string;
  config: string;
  category: string;
  state: string;
  melting: string;
  boiling: string;
}

export interface Science {
  icon: string;
  name: string;
  copy: string;
  accent: string;
  formula: string;
  meta: Array<[string, string]>;
  image?: string;
}

export interface Molecule {
  formula: string;
  name: string;
  type: string;
  copy: string;
}

export interface BufferCalculationRequest {
  acidSpeciesCode: string;
  baseSpeciesCode: string;
  acidComponent: BufferComponentRequest;
  baseComponent: BufferComponentRequest;
  finalVolume: MeasurementValue;
  temperature: MeasurementValue;
  solventCode: string;
}

export interface BufferComponentRequest {
  speciesCode: string;
  amount: MeasurementValue;
  concentration: MeasurementValue;
}

export interface BufferPreparationRequest {
  system: BufferSystemRequest;
  targetPh: number;
  totalBufferConcentration: number;
  finalVolume: MeasurementValue;
}

export interface BufferSystemRequest {
  pairCode: string;
  acidSpeciesCode: string;
  baseSpeciesCode: string;
  systemType: string;
  ka: number;
  kb: number;
  kw: number;
  temperature: MeasurementValue;
  solventCode: string;
  sources: string[];
}

export interface BufferPerturbationRequest {
  initialBuffer: BufferCalculationRequest;
  reagentType: 'STRONG_ACID' | 'STRONG_BASE';
  reagentAmount: MeasurementValue;
  volumePolicy: 'NEGLIGIBLE_ADDED_VOLUME' | 'EXPLICIT_FINAL_VOLUME';
  finalVolume: MeasurementValue;
}

export interface MeasurementValue {
  value: number | string;
  unit: string;
}

export interface TitrationRequest {
  systemType?: string;
  analyteSpeciesCode: string;
  titrantSpeciesCode: string;
  analyteConcentration: MeasurementValue;
  analyteVolume: MeasurementValue;
  titrantConcentration: MeasurementValue;
  temperature: MeasurementValue;
  solventCode: string;
  ka?: number;
  kb?: number;
  kw?: number;
  sources?: string[];
}

export interface PolyproticTitrationRequest {
  family?: PolyproticAcidFamilyRequest;
  acidFamilyCode?: string;
  systemType: string;
  analyteConcentration: MeasurementValue;
  analyteVolume: MeasurementValue;
  titrantConcentration: MeasurementValue;
  temperature: MeasurementValue;
  solventCode: string;
  analyteSpectatorIonCode?: string;
  analyteSpectatorIonCharge?: number;
  titrantSpectatorIonCode?: string;
  titrantSpectatorIonCharge?: number;
  kw?: number;
  volumeToleranceLiters?: number;
}

export interface PolyproticAcidFamilyRequest {
  familyCode: string;
  species: PolyproticSpeciesRequest[];
  constants: PolyproticDissociationConstantRequest[];
  firstDissociationComplete: boolean;
  sources: string[];
}

export interface PolyproticSpeciesRequest {
  formula: string;
  protonsRemaining: number;
}

export interface PolyproticDissociationConstantRequest {
  stepNumber: number;
  ka: number;
  temperature: MeasurementValue;
  solventCode: string;
}

export interface RateEvaluationRequest {
  reactionCode: string;
  rateLaw: KineticRateLawRequest;
  rateConstant: RateConstantRequest;
  concentrations: Record<string, number>;
}

export interface KineticRateLawRequest {
  terms: KineticRateLawTermRequest[];
  overallOrder?: { totalOrderValue: number };
}

export interface KineticRateLawTermRequest {
  compoundCode: string;
  state: string;
  order: { value: number };
}

export interface RateConstantRequest {
  value: number;
  dimension: { order: number; canonicalUnitSymbol: string };
}

export interface IntegratedRateLawRequest {
  compoundCode: string;
  initialConcentrationMolar: number;
  rateConstant: RateConstantRequest;
  order: { totalOrderValue: number };
  duration: MeasurementValue;
}

export interface ArrheniusRequest {
  parameters: ArrheniusParametersRequest;
  targetTemperature: MeasurementValue;
}

export interface ArrheniusParametersRequest {
  preExponentialFactorA: number;
  temperatureExponentN?: number;
  referenceTemperature?: MeasurementValue;
  activationEnergy: MeasurementValue;
  minTemperature: MeasurementValue;
  maxTemperature: MeasurementValue;
  expressionForm?: string;
}

export interface KineticProgressRequest {
  reactionCode: string;
  profile: KineticProfileRequest;
  initialConcentrations: Record<string, number>;
  systemVolumeLiters?: number;
  totalDuration: MeasurementValue;
  stepSize?: MeasurementValue;
  temperature?: MeasurementValue;
}

export interface KineticProfileRequest {
  profileId: string;
  reactionCode: string;
  rateLaw: KineticRateLawRequest;
  referenceRateConstant: RateConstantRequest;
  arrheniusParameters: ArrheniusParametersRequest;
  conditions?: Record<string, unknown>;
  evidenceStatus: string;
  provenance: Record<string, unknown>;
}

export interface ElectrochemicalCellRequest {
  cathodeReductionRecordId: string;
  anodeReductionRecordId: string;
  cellType?: string;
  reactionScale?: number;
}

export interface NernstRequest {
  cathodeReductionRecordId: string;
  anodeReductionRecordId: string;
  temperature: MeasurementValue;
  activities: ElectrochemicalActivityRequest[];
}

export interface ElectrochemicalActivityRequest {
  speciesCode: string;
  phase: string;
  basis: string;
  value: number;
  charge?: number;
  ionicStrength?: number;
}

export interface ElectrolysisRequest {
  halfReactionRecordId: string;
  substanceCode: string;
  substancePhase: string;
  current?: { inAmperes: number };
  duration?: MeasurementValue;
  charge?: { inCoulombs: number };
  efficiency: { fraction: number };
  molarMassGramsPerMole: number;
}

export interface GasStateRequest {
  model: string;
  pressure?: MeasurementValue;
  volume?: MeasurementValue;
  amount?: MeasurementValue;
  temperature?: MeasurementValue;
  compressibilityFactor?: number;
}

export interface GasMixtureRequest {
  totalPressure: MeasurementValue;
  components: GasMixtureComponentRequest[];
}

export interface GasMixtureComponentRequest {
  compoundCode: string;
  amount: MeasurementValue;
}

export interface GasTransformationRequest {
  constraint: string;
  initialPressure?: MeasurementValue;
  initialVolume?: MeasurementValue;
  initialTemperature?: MeasurementValue;
  finalPressure?: MeasurementValue;
  finalVolume?: MeasurementValue;
  finalTemperature?: MeasurementValue;
}

export interface ReactionThermodynamicsRequest {
  reactionCode: string;
  conditions?: ThermodynamicConditionsRequest;
  stateOverrides?: Record<string, string>;
}

export interface ThermodynamicConditionsRequest {
  temperature: MeasurementValue;
  pressure: MeasurementValue;
  state: string;
  standardStateConvention: string;
}

export interface HessLawRequest {
  reactionTerms: HessReactionTermRequest[];
  targetVector: ReactionVectorRequest;
}

export interface HessReactionTermRequest {
  reactionCode: string;
  multiplier: { numerator: number; denominator: number };
  vector: ReactionVectorRequest;
  properties: Record<string, unknown>;
}

export interface ReactionVectorRequest {
  terms: ReactionVectorTermRequest[];
}

export interface ReactionVectorTermRequest {
  compoundCode: string;
  state: string;
  coefficient: { numerator: number; denominator: number };
}

export interface SensibleHeatRequest {
  sample: ThermalSampleRequest;
  finalTemperature: MeasurementValue;
  method: string;
}

export interface ThermalSampleRequest {
  sampleId: string;
  state: string;
  mass?: MeasurementValue;
  amount?: MeasurementValue;
  specificHeatCapacity?: MeasurementValue;
  molarHeatCapacity?: MeasurementValue;
  initialTemperature: MeasurementValue;
}

export interface ThermalMixingRequest {
  samples: ThermalSampleRequest[];
  calorimeter?: CalorimeterRequest;
  method: string;
}

export interface CalorimeterRequest {
  heatCapacity?: { valueJoulesPerKelvin: number; unit: string };
  initialTemperature?: MeasurementValue;
}

export interface ReactionCalorimetryRequest {
  reactionCode: string;
  reactionExtentMoles: number;
  temperature: MeasurementValue;
  pressure?: MeasurementValue;
  stateOverrides?: Record<string, string>;
}

export interface LaboratorySafetyEvaluationRequest {
  stage: string;
  command: Record<string, unknown>;
  currentState: SimulationState;
  proposedDelta?: Record<string, unknown>;
  environmentContext?: Record<string, string>;
}
