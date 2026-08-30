import type { ReactNode } from 'react';
import { BeakerRenderer, type BeakerProps } from './renderers/BeakerRenderer';
import { ErlenmeyerRenderer, type ErlenmeyerProps } from './renderers/ErlenmeyerRenderer';
import { RoundFlaskRenderer, type RoundFlaskProps } from './renderers/RoundFlaskRenderer';
import { VolumetricFlaskRenderer, type VolumetricFlaskProps } from './renderers/VolumetricFlaskRenderer';
import { DistillationFlaskRenderer, type DistillationFlaskProps } from './renderers/DistillationFlaskRenderer';
import { GraduatedCylinderRenderer, type GraduatedCylinderProps } from './renderers/GraduatedCylinderRenderer';
import { TestTubeRenderer, type TestTubeProps } from './renderers/TestTubeRenderer';
import { PetriDishRenderer, type PetriDishProps } from './renderers/PetriDishRenderer';
import { WatchGlassRenderer, type WatchGlassProps } from './renderers/WatchGlassRenderer';
import { CondenserRenderer, type CondenserProps } from './renderers/CondenserRenderer';
import { BunsenBurnerRenderer, type BunsenBurnerProps } from './renderers/BunsenBurnerRenderer';
import { HotPlateRenderer, type HotPlateProps } from './renderers/HotPlateRenderer';
import { MagneticStirrerRenderer, type MagneticStirrerProps } from './renderers/MagneticStirrerRenderer';
import { ThermometerRenderer, type ThermometerProps } from './renderers/ThermometerRenderer';
import { pHMeterRenderer, type pHMeterProps } from './renderers/pHMeterRenderer';
import { AnalyticalBalanceRenderer, type AnalyticalBalanceProps } from './renderers/AnalyticalBalanceRenderer';
import { BuretteRenderer, type BuretteProps } from './renderers/BuretteRenderer';
import { PipetteRenderer, type PipetteProps } from './renderers/PipetteRenderer';
import { ClampStandRenderer, type ClampStandProps } from './renderers/ClampStandRenderer';
import { CrucibleRenderer, type CrucibleProps } from './renderers/CrucibleRenderer';
import { FunnelRenderer, SeparatoryFunnelRenderer } from './renderers/FunnelRenderer';
import { RingStandRenderer } from './renderers/RingStandRenderer';

export type EquipmentIconProps = {
  type: string;
  size?: number | string;
  width?: number | string;
  height?: number | string;
  liquidLevel?: number;
  liquidColor?: string;
  volumeMl?: number;
  capacityMl?: number;
  [key: string]: unknown;
};

export type EquipmentCanvasRenderer = (props: EquipmentIconProps) => ReactNode;

function asCanvasRenderer<T extends object>(renderer: (props: T) => ReactNode): EquipmentCanvasRenderer {
  return (props) => renderer(props as unknown as T);
}

function UnknownEquipmentRenderer({ size = 100, type = 'equipment' }: EquipmentIconProps) {
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" role="img" aria-label={`Unsupported ${type}`}>
      <rect x="16" y="18" width="68" height="64" rx="12" fill="rgba(245,158,11,.08)" stroke="#F59E0B" strokeWidth="2" />
      <path d="M50 30v22M50 63v3" stroke="#FBBF24" strokeWidth="5" strokeLinecap="round" />
      <circle cx="50" cy="76" r="3" fill="#FDE68A" />
    </svg>
  );
}

function SimpleLabRenderer({ size = 100, type = 'equipment' }: EquipmentIconProps) {
  const label = String(type).replaceAll('_', ' ').slice(0, 14);
  const isFunnel = label.includes('funnel') || label.includes('buchner');
  const isSupport = label.includes('clamp') || label.includes('stand') || label.includes('ring');
  const isCooling = label.includes('cool') || label.includes('refrigerator') || label.includes('mantle');
  return <svg width={size} height={size} viewBox="0 0 100 100" role="img" aria-label={label}>
    {isFunnel ? <><path d="M20 18H80L59 52V82H41V52Z" fill="rgba(34,211,238,.16)" stroke="#22D3EE" strokeWidth="3" /><path d="M41 64H59" stroke="#A5F3FC" strokeWidth="2" /></> : isSupport ? <><path d="M30 12V88M30 18H78M30 82H78M64 18V45" stroke="#CBD5E1" strokeWidth="5" strokeLinecap="round" /><circle cx="64" cy="45" r="8" fill="rgba(167,139,250,.25)" stroke="#A78BFA" strokeWidth="2" /></> : <><rect x="18" y="25" width="64" height="50" rx="8" fill={isCooling ? 'rgba(96,165,250,.2)' : 'rgba(148,163,184,.15)'} stroke={isCooling ? '#60A5FA' : '#94A3B8'} strokeWidth="3" /><path d="M28 62H72" stroke={isCooling ? '#BFDBFE' : '#CBD5E1'} strokeWidth="3" /><circle cx="50" cy="48" r="10" fill="none" stroke={isCooling ? '#93C5FD' : '#CBD5E1'} strokeWidth="2" /></>}
  </svg>;
}

const rendererRegistry = new Map<string, EquipmentCanvasRenderer>();

export function registerEquipmentRenderer(rendererId: string, renderer: EquipmentCanvasRenderer) {
  rendererRegistry.set(rendererId, renderer);
}

// Initial registrations
registerEquipmentRenderer('beaker', asCanvasRenderer<BeakerProps>(BeakerRenderer));
registerEquipmentRenderer('erlenmeyer', asCanvasRenderer<ErlenmeyerProps>(ErlenmeyerRenderer));
registerEquipmentRenderer('roundflask', asCanvasRenderer<RoundFlaskProps>(RoundFlaskRenderer));
registerEquipmentRenderer('volumetricflask', asCanvasRenderer<VolumetricFlaskProps>(VolumetricFlaskRenderer));
registerEquipmentRenderer('distillationflask', asCanvasRenderer<DistillationFlaskProps>(DistillationFlaskRenderer));
registerEquipmentRenderer('graduatedcylinder', asCanvasRenderer<GraduatedCylinderProps>(GraduatedCylinderRenderer));
registerEquipmentRenderer('testtube', asCanvasRenderer<TestTubeProps>(TestTubeRenderer));
registerEquipmentRenderer('petridish', asCanvasRenderer<PetriDishProps>(PetriDishRenderer));
registerEquipmentRenderer('watchglass', asCanvasRenderer<WatchGlassProps>(WatchGlassRenderer));
registerEquipmentRenderer('condenser', asCanvasRenderer<CondenserProps>(CondenserRenderer));
registerEquipmentRenderer('bunsenburner', asCanvasRenderer<BunsenBurnerProps>(BunsenBurnerRenderer));
registerEquipmentRenderer('hotplate', asCanvasRenderer<HotPlateProps>(HotPlateRenderer));
registerEquipmentRenderer('magneticstirrer', asCanvasRenderer<MagneticStirrerProps>(MagneticStirrerRenderer));
registerEquipmentRenderer('thermometer', asCanvasRenderer<ThermometerProps>(ThermometerRenderer));
registerEquipmentRenderer('phmeter', asCanvasRenderer<pHMeterProps>(pHMeterRenderer));
registerEquipmentRenderer('analyticalbalance', asCanvasRenderer<AnalyticalBalanceProps>(AnalyticalBalanceRenderer));
registerEquipmentRenderer('burette', asCanvasRenderer<BuretteProps>(BuretteRenderer));
registerEquipmentRenderer('pipette', asCanvasRenderer<PipetteProps>(PipetteRenderer));
registerEquipmentRenderer('clampstand', asCanvasRenderer<ClampStandProps>(ClampStandRenderer));
registerEquipmentRenderer('crucible', asCanvasRenderer<CrucibleProps>(CrucibleRenderer));
registerEquipmentRenderer('digitalbalance', asCanvasRenderer<AnalyticalBalanceProps>(AnalyticalBalanceRenderer));
registerEquipmentRenderer('scales', asCanvasRenderer<AnalyticalBalanceProps>(AnalyticalBalanceRenderer));
registerEquipmentRenderer('burner', asCanvasRenderer<BunsenBurnerProps>(BunsenBurnerRenderer));
registerEquipmentRenderer('magnetic_stirrer', asCanvasRenderer<MagneticStirrerProps>(MagneticStirrerRenderer));
registerEquipmentRenderer('distillation_flask', asCanvasRenderer<DistillationFlaskProps>(DistillationFlaskRenderer));
registerEquipmentRenderer('volumetric_flask', asCanvasRenderer<VolumetricFlaskProps>(VolumetricFlaskRenderer));
registerEquipmentRenderer('graduated_cylinder', asCanvasRenderer<GraduatedCylinderProps>(GraduatedCylinderRenderer));
registerEquipmentRenderer('funnel', FunnelRenderer);
registerEquipmentRenderer('separatory_funnel', SeparatoryFunnelRenderer);
registerEquipmentRenderer('buchner', SimpleLabRenderer);
registerEquipmentRenderer('filterpaper', SimpleLabRenderer);
registerEquipmentRenderer('clamp', SimpleLabRenderer);
registerEquipmentRenderer('stand', RingStandRenderer);
registerEquipmentRenderer('ringstand', RingStandRenderer);
registerEquipmentRenderer('mantle', SimpleLabRenderer);
registerEquipmentRenderer('coolingbath', SimpleLabRenderer);
registerEquipmentRenderer('refrigerator', SimpleLabRenderer);

const rendererAliases: Record<string, string> = {
  round_flask: 'roundflask', round_bottom_flask: 'roundflask', distillation_flask: 'distillationflask',
  bunsen_burner: 'burner', bunsenburner: 'burner', magnetic_stirrer: 'magneticstirrer',
  digital_balance: 'digitalbalance', analytical_balance: 'analyticalbalance', graduated_cylinder: 'graduatedcylinder',
  volumetric_flask: 'volumetricflask', separatoryfunnel: 'separatory_funnel', ring_stand: 'ringstand',
};

export function canonicalRendererId(rendererId: string) {
  return rendererAliases[rendererId] ?? rendererId;
}

export function hasEquipmentRenderer(rendererId: string) {
  return Boolean(rendererRegistry.get(canonicalRendererId(rendererId)));
}

const defaultRenderer: EquipmentCanvasRenderer = (props) =>
  <UnknownEquipmentRenderer {...props} />;

export function renderEquipmentCanvas(rendererId: string | undefined, props: EquipmentIconProps) {
  const canonical = rendererId ? canonicalRendererId(rendererId) : undefined;
  return (canonical ? rendererRegistry.get(canonical) : undefined)?.(props) ?? defaultRenderer({ ...props, type: canonical ?? props.type });
}

export function EquipmentThumbnail(props: EquipmentIconProps) {
  return <span className="equipment-art inline-flex h-full w-full items-center justify-center rounded-lg bg-white/[.03] p-1 leading-none">{renderEquipmentCanvas(props.type, props)}</span>;
}
