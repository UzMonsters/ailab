import { SvgDefs } from './SvgDefs';
import { calculateLiquidLevel } from './VesselGeometry';
export interface GraduatedCylinderProps {
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

export function GraduatedCylinderRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(0,100,255,0.5)', volumeMl = 0, capacityMl = 100 }: GraduatedCylinderProps) {
  const tickStep = capacityMl >= 100 ? 10 : Math.max(5, capacityMl / 10);
  const ticks = Array.from({ length: Math.floor(capacityMl / tickStep) + 1 }, (_, index) => index * tickStep);
  const levelY = calculateLiquidLevel(liquidLevel, 20, 80);
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100">
      <SvgDefs />
      <path d="M27 16h46v66c0 5-4 8-8 8H35c-4 0-8-3-8-8z" fill="url(#glassReflection)" stroke="rgba(255,255,255,.85)" strokeWidth="1.8" />
      {liquidLevel > 0 && (
        <path d={`M29 ${levelY}h42v${82 - levelY}c0 3-3 5-6 5H35c-3 0-6-2-6-5z`} fill={liquidColor} opacity=".85" />
      )}
      <path d="M34 20v58" stroke="white" strokeOpacity=".55" strokeWidth="2" />
      <g stroke="rgba(255,255,255,.8)" strokeWidth=".7">
        {ticks.map((tick) => {
          const y = 80 - (tick / capacityMl) * 60;
          return <line key={tick} x1="62" y1={y} x2={tick % (tickStep * 2) === 0 ? 76 : 70} y2={y} />;
        })}
      </g>
      <g fill="rgba(255,255,255,.9)" fontSize="4.5" fontFamily="monospace" textAnchor="start">
        {ticks.filter((tick) => tick % (tickStep * 2) === 0 || tick === capacityMl).map((tick) => {
          const y = 80 - (tick / capacityMl) * 60;
          return <text key={tick} x="77" y={y + 1.5}>{Math.round(tick)}</text>;
        })}
      </g>
      {volumeMl > 0 && <text x="50" y="90" textAnchor="middle" fill="rgba(255,255,255,.95)" fontSize="5.5" fontFamily="monospace">{Math.round(Math.min(volumeMl, capacityMl))} mL</text>}
    </svg>
  );
}

