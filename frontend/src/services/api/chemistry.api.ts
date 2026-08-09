import { api } from './client';
import type {
  ChemicalFormula,
  BalancedEquation,
  ElementSummary,
  ElementDetails,
  ElementPropertyDetails,
  CompoundSummary,
  CompoundDetails,
  CompoundPhysicalPropertyDetails,
  ThermodynamicProfileDetails,
  ReactionThermodynamicsResult,
  HessLawResult,
  SensibleHeatResult,
  ThermalMixingResult,
  ReactionCalorimetryResult,
  AcidBaseResponse,
  BufferCalculationResult,
  BufferPreparationResult,
  BufferPerturbationResult,
  TitrationCurveResult,
  PolyproticTitrationCurveResult,
  RateEvaluationResult,
  IntegratedRateLawResult,
  HalfLifeResult,
  ArrheniusResult,
  KineticProgressResult,
  ElectrochemicalCellResult,
  NernstResult,
  ElectrolysisResult,
  GasStateResult,
  GasMixtureResult,
  SafetyEvaluationResult,
  ReactionThermodynamicsRequest,
  HessLawRequest,
  SensibleHeatRequest,
  ThermalMixingRequest,
  ReactionCalorimetryRequest,
  BufferCalculationRequest,
  BufferPreparationRequest,
  BufferPerturbationRequest,
  TitrationRequest,
  PolyproticTitrationRequest,
  RateEvaluationRequest,
  IntegratedRateLawRequest,
  ArrheniusRequest,
  KineticProgressRequest,
  ElectrochemicalCellRequest,
  NernstRequest,
  ElectrolysisRequest,
  GasStateRequest,
  GasMixtureRequest,
  GasTransformationRequest,
  LaboratorySafetyEvaluationRequest,
} from '@/types';

export const chemistryApi = {
  parseFormula: (formula: string) =>
    api.post<ChemicalFormula>('/api/v1/chemistry/formulas/parse', { formula }),

  balanceEquation: (equation: string) =>
    api.post<BalancedEquation>('/api/v1/chemistry/equations/balance', { equation }),

  getElements: () =>
    api.get<ElementSummary[]>('/api/v1/chemistry/elements'),

  getElement: (identifier: string) =>
    api.get<ElementDetails>(`/api/v1/chemistry/elements/${identifier}`),

  getElementProperties: (identifier: string) =>
    api.get<ElementPropertyDetails>(`/api/v1/chemistry/elements/${identifier}/properties`),

  getCompounds: (query?: { name?: string; formula?: string; composition?: string }) => {
    const params = new URLSearchParams();
    if (query?.name) params.append('name', query.name);
    if (query?.formula) params.append('formula', query.formula);
    if (query?.composition) params.append('composition', query.composition);
    const qs = params.toString();
    return api.get<CompoundSummary[]>(`/api/v1/chemistry/compounds${qs ? `?${qs}` : ''}`);
  },

  getCompound: (identifier: string) =>
    api.get<CompoundDetails>(`/api/v1/chemistry/compounds/${identifier}`),

  getCompoundProperties: (identifier: string) =>
    api.get<CompoundPhysicalPropertyDetails>(`/api/v1/chemistry/compounds/${identifier}/properties`),

  getThermodynamicReference: (compoundCode: string) =>
    api.get<ThermodynamicProfileDetails>(`/api/v1/chemistry/thermodynamics/reference/${compoundCode}`),

  calculateThermodynamics: (data: ReactionThermodynamicsRequest) =>
    api.post<ReactionThermodynamicsResult>('/api/v1/chemistry/thermodynamics/calculate', data),

  hessLaw: (data: HessLawRequest) =>
    api.post<HessLawResult>('/api/v1/chemistry/thermodynamics/hess-law', data),

  sensibleHeat: (data: SensibleHeatRequest) =>
    api.post<SensibleHeatResult>('/api/v1/chemistry/thermodynamics/calorimetry/sensible-heat', data),

  thermalMixing: (data: ThermalMixingRequest) =>
    api.post<ThermalMixingResult>('/api/v1/chemistry/thermodynamics/calorimetry/thermal-mixing', data),

  reactionHeat: (data: ReactionCalorimetryRequest) =>
    api.post<ReactionCalorimetryResult>('/api/v1/chemistry/thermodynamics/calorimetry/reaction-heat', data),

  acidBaseWater: (data?: { temperatureKelvin?: number }) =>
    api.post<AcidBaseResponse>('/api/v1/chemistry/acid-base/water', data ?? {}),

  acidBaseStrongAcid: (data: { speciesCode: string; concentrationMolar: number; temperatureKelvin?: number }) =>
    api.post<AcidBaseResponse>('/api/v1/chemistry/acid-base/strong-acid', data),

  acidBaseStrongBase: (data: { speciesCode: string; concentrationMolar: number; temperatureKelvin?: number }) =>
    api.post<AcidBaseResponse>('/api/v1/chemistry/acid-base/strong-base', data),

  acidBaseWeakAcid: (data: { speciesCode: string; concentrationMolar: number; temperatureKelvin?: number }) =>
    api.post<AcidBaseResponse>('/api/v1/chemistry/acid-base/weak-acid', data),

  acidBaseWeakBase: (data: { speciesCode: string; concentrationMolar: number; temperatureKelvin?: number }) =>
    api.post<AcidBaseResponse>('/api/v1/chemistry/acid-base/weak-base', data),

  acidBaseSaltHydrolysis: (data: { speciesCode: string; concentrationMolar: number; temperatureKelvin?: number }) =>
    api.post<AcidBaseResponse>('/api/v1/chemistry/acid-base/salt-hydrolysis', data),

  bufferCalculation: (data: BufferCalculationRequest) =>
    api.post<BufferCalculationResult>('/api/v1/chemistry/acid-base/buffer', data),

  bufferPreparation: (data: BufferPreparationRequest) =>
    api.post<BufferPreparationResult>('/api/v1/chemistry/acid-base/buffer/preparation', data),

  bufferPerturbation: (data: BufferPerturbationRequest) =>
    api.post<BufferPerturbationResult>('/api/v1/chemistry/acid-base/buffer/perturbation', data),

  titrationCharacteristicPoints: (data: TitrationRequest) =>
    api.post<TitrationCurveResult>('/api/v1/chemistry/acid-base/titration/characteristic-points', data),

  polyproticTitration: (data: PolyproticTitrationRequest) =>
    api.post<PolyproticTitrationCurveResult>('/api/v1/chemistry/acid-base/polyprotic-titration/characteristic-points', data),

  kineticsRate: (data: RateEvaluationRequest) =>
    api.post<RateEvaluationResult>('/api/v1/chemistry/kinetics/rate', data),

  kineticsIntegratedLaw: (data: IntegratedRateLawRequest) =>
    api.post<IntegratedRateLawResult>('/api/v1/chemistry/kinetics/integrated-law', data),

  kineticsHalfLife: (data: IntegratedRateLawRequest) =>
    api.post<HalfLifeResult>('/api/v1/chemistry/kinetics/half-life', data),

  kineticsArrhenius: (data: ArrheniusRequest) =>
    api.post<ArrheniusResult>('/api/v1/chemistry/kinetics/arrhenius', data),

  kineticsProgress: (data: KineticProgressRequest) =>
    api.post<KineticProgressResult>('/api/v1/chemistry/kinetics/progress', data),

  electrochemicalStandardCell: (data: ElectrochemicalCellRequest) =>
    api.post<ElectrochemicalCellResult>('/api/v1/chemistry/electrochemistry/standard-cell', data),

  electrochemicalNernst: (data: NernstRequest) =>
    api.post<NernstResult>('/api/v1/chemistry/electrochemistry/nernst', data),

  electrolysis: (data: ElectrolysisRequest) =>
    api.post<ElectrolysisResult>('/api/v1/chemistry/electrochemistry/electrolysis', data),

  gasState: (data: GasStateRequest) =>
    api.post<GasStateResult>('/api/v1/chemistry/gas/state', data),

  gasMixture: (data: GasMixtureRequest) =>
    api.post<GasMixtureResult>('/api/v1/chemistry/gas/mixture', data),

  gasTransformation: (data: GasTransformationRequest) =>
    api.post<GasStateResult>('/api/v1/chemistry/gas/transformation', data),

  safetyEvaluate: (data: LaboratorySafetyEvaluationRequest) =>
    api.post<SafetyEvaluationResult>('/api/v1/chemistry/safety/evaluate', data),
};
