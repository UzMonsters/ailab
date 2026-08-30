import { SvgDefs } from './SvgDefs';
import { calculateLiquidLevel } from './VesselGeometry';
export interface TestTubeProps {
  width?: number | string;
  height?: number | string;
  size?: number | string;
  liquidLevel?: number;
  liquidColor?: string;
  temperature?: number;
  volumeMl?: number;
  capacityMl?: number;
  operation?: string;
  sealed?: boolean;
}

export function TestTubeRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(0,100,255,0.5)', sealed = false }: TestTubeProps) {
  const levelY = calculateLiquidLevel(liquidLevel, 22, 82);
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100" role="img" aria-label="Test tube">
      <SvgDefs />
      {sealed && (
        <path d="M 33 5 L 67 5 L 65 17 L 35 17 Z" fill="#2d3748" stroke="#1a202c" strokeWidth="1" />
      )}
      <path d="M31 15h38v55c0 13-8 22-19 22S31 83 31 70z" fill="url(#glassReflection)" stroke="#dbeafe" strokeWidth="2" />
      {liquidLevel > 0 && (
        <path d={`M33 ${levelY}h34v${82 - levelY}c0 6-7 8-17 8s-17-2-17-8z`} fill={liquidColor} opacity=".9" />
      )}
      <path d="M31 15h38M36 20v46" fill="none" stroke="white" strokeOpacity=".72" strokeWidth="2" strokeLinecap="round" />
      <path d="M38 78c2 7 6 10 12 10" fill="none" stroke="white" strokeOpacity=".35" strokeWidth="2" strokeLinecap="round" />
      <g stroke="#e2e8f0" strokeOpacity=".7" strokeWidth="1"><path d="M62 35h6M62 45h6M62 55h6M62 65h6" /></g>
    </svg>
  );
}
