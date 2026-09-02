import { SvgDefs } from './SvgDefs';
import { calculateLiquidLevel } from './VesselGeometry';
export interface WatchGlassProps {
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

export function WatchGlassRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(0,100,255,0.5)' }: WatchGlassProps) {
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100" role="img" aria-label="Watch glass">
      <SvgDefs />
      <path d="M12 52c8 25 25 36 38 36s30-11 38-36c-11 10-25 15-38 15S23 62 12 52z" fill="url(#glassReflection)" stroke="#dbeafe" strokeWidth="2" />
      <path d="M12 52c10 9 23 13 38 13s28-4 38-13" fill="none" stroke="#f8fafc" strokeOpacity=".85" strokeWidth="2" />
      {liquidLevel > 0 && <path d="M28 62c7 4 15 5 22 5s15-1 22-5c-6 12-15 18-22 18s-16-6-22-18z" fill={liquidColor} opacity=".7" />}
      <path d="M28 52c9 5 18 7 22 7s13-2 22-7" fill="none" stroke="white" strokeOpacity=".4" />
    </svg>
  );
}

