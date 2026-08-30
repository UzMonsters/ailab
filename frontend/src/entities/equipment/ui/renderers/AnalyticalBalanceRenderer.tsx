import { SvgDefs } from './SvgDefs';
import { calculateLiquidLevel } from './VesselGeometry';
export interface AnalyticalBalanceProps {
  width?: number | string;
  height?: number | string;
  size?: number | string;
  liquidLevel?: number;
  liquidColor?: string;
  temperature?: number;
  volumeMl?: number;
  capacityMl?: number;
  operation?: string;
}

export function AnalyticalBalanceRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(0,100,255,0.5)' }: AnalyticalBalanceProps) {
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100">
      <SvgDefs />
      {/* Skeleton for AnalyticalBalance */}
      <rect x="20" y="20" width="60" height="60" fill="url(#glassReflection)" stroke="black" />
      {liquidLevel > 0 && (
        <rect x="20" y={calculateLiquidLevel(liquidLevel, 20, 80)} width="60" height={80 - calculateLiquidLevel(liquidLevel, 20, 80)} fill={liquidColor} />
      )}
      <text x="50" y="50" textAnchor="middle" fontSize="10">AnalyticalBalance</text>
    </svg>
  );
}

