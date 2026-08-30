import { SvgDefs } from './SvgDefs';
import { calculateLiquidLevel } from './VesselGeometry';
export interface VolumetricFlaskProps {
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

export function VolumetricFlaskRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(0,100,255,0.5)', sealed = false }: VolumetricFlaskProps) {
  const levelY = calculateLiquidLevel(liquidLevel, 48, 84);
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100" role="img" aria-label="Volumetric flask">
      <defs>
        <clipPath id="vol-flask-body">
          <path d="M42 12h16v30l22 35c4 7-2 13-10 13H30c-8 0-14-6-10-13l22-35z" />
        </clipPath>
      </defs>
      <SvgDefs />
      {sealed && (
        <path d="M 44 4 L 56 4 L 54 14 L 46 14 Z" fill="#2d3748" stroke="#1a202c" strokeWidth="1" />
      )}
      <path d="M42 12h16v30l22 35c4 7-2 13-10 13H30c-8 0-14-6-10-13l22-35z" fill="url(#glassReflection)" stroke="#dbeafe" strokeWidth="2" />
      
      {liquidLevel > 0 && (
        <g clipPath="url(#vol-flask-body)">
          <rect x="0" y={levelY} width="100" height={100 - levelY} fill={liquidColor} opacity=".85" style={{ transition: 'all 0.3s ease-in-out' }} />
        </g>
      )}
      
      <path d="M38 17h24M46 17v25" stroke="white" strokeOpacity=".7" strokeWidth="2" strokeLinecap="round" />
      <path d="M36 59h28" stroke="#f8fafc" strokeOpacity=".7" strokeWidth="1.5" />
      <path d="M31 78h38" stroke="#94a3b8" strokeOpacity=".7" strokeWidth="1" />
    </svg>
  );
}
