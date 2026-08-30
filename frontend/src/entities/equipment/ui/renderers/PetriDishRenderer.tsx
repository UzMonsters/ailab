import { SvgDefs } from './SvgDefs';
import { calculateLiquidLevel } from './VesselGeometry';
export interface PetriDishProps {
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

export function PetriDishRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(0,100,255,0.5)' }: PetriDishProps) {
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100" role="img" aria-label="Petri dish">
      <SvgDefs />
      <ellipse cx="50" cy="57" rx="38" ry="22" fill="url(#glassReflection)" stroke="#dbeafe" strokeWidth="2" />
      {liquidLevel > 0 && <ellipse cx="50" cy="58" rx="34" ry="16" fill={liquidColor} opacity=".75" />}
      <ellipse cx="50" cy="46" rx="38" ry="19" fill="#dbeafe" fillOpacity=".08" stroke="#f8fafc" strokeOpacity=".85" strokeWidth="2" />
      <ellipse cx="50" cy="46" rx="31" ry="13" fill="none" stroke="#93c5fd" strokeOpacity=".45" />
      <path d="M20 58c4 16 18 25 30 25s26-9 30-25" fill="none" stroke="#94a3b8" strokeOpacity=".8" strokeWidth="2" />
      <circle cx="40" cy="48" r="2" fill="#67e8f9" /><circle cx="59" cy="52" r="1.5" fill="#67e8f9" />
    </svg>
  );
}

