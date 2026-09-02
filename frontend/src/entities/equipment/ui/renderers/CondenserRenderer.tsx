import { SvgDefs } from './SvgDefs';
export interface CondenserProps {
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

export function CondenserRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(0,100,255,0.5)' }: CondenserProps) {
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100" role="img" aria-label="Condenser">
      <SvgDefs />
      <path d="M42 10h16v13l-5 7v40l5 7v13H42V77l5-7V30l-5-7z" fill="url(#glassReflection)" stroke="#dbeafe" strokeWidth="2" />
      <path d="M25 25h14M61 25h14M25 75h14M61 75h14" stroke="#bae6fd" strokeWidth="6" strokeLinecap="round" opacity=".9" />
      <path d="M25 25v14M75 25v14M25 61v14M75 61v14" stroke="#94a3b8" strokeWidth="2" />
      <path d="M50 14v70" stroke="white" strokeOpacity=".45" strokeWidth="2" />
      {liquidLevel > 0 && <path d="M45 50h10v18l3 6H42l3-6z" fill={liquidColor} opacity=".8" />}
      <circle cx="50" cy="49" r="2" fill="#67e8f9" />
    </svg>
  );
}

