import { SvgDefs } from './SvgDefs';
import { calculateLiquidLevel } from './VesselGeometry';
export interface ErlenmeyerProps {
  width?: number | string;
  height?: number | string;
  size?: number | string;
  liquidLevel?: number;
  liquidColor?: string;
  temperature?: number;
  volumeMl?: number;
  capacityMl?: number;
  operation?: string;
  hasSolid?: boolean;
  solidColor?: string;
  broken?: boolean;
  sealed?: boolean;
}

export function ErlenmeyerRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(2, 132, 199, 0.8)', temperature = 24.5, volumeMl = 0, capacityMl = 250, hasSolid = false, solidColor = '#CBD5E1', operation, broken = false, sealed = false }: ErlenmeyerProps) {
  const minY = 35; // Neck starts here basically (liquid max height)
  const maxY = 88; // Base
  
  // Calculate liquid fill
  const liquidY = calculateLiquidLevel(liquidLevel, minY, maxY);
  
  // Conical math: Neck is at x=40..60, Base is at x=15..85
  const baseR = 35;
  const neckR = 10;
  const heightTotal = maxY - minY;
  const rY = neckR + ((liquidY - minY) / heightTotal) * (baseR - neckR);
  
  const tickStep = capacityMl >= 250 ? 50 : Math.max(10, capacityMl / 5);
  const ticks = Array.from({ length: Math.floor(capacityMl / tickStep) + 1 }, (_, index) => index * tickStep);

  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100">
      <defs>
        <clipPath id="flask-body">
          <path d={`M 38 15 L 38 30 C 38 35, 15 80, 15 ${maxY} A 35 7 0 0 0 85 ${maxY} C 85 80, 62 35, 62 30 L 62 15 Z`} />
        </clipPath>
      </defs>
      <SvgDefs />
      <g filter="url(#dropShadow)">
        {sealed && !broken && (
          <path d="M 40 8 L 60 8 L 58 20 L 42 20 Z" fill="#2d3748" stroke="#1a202c" strokeWidth="1" />
        )}
        {/* Back of the neck rim */}
        <ellipse cx="50" cy="15" rx="12" ry="3" fill="url(#glassReflection)" stroke="rgba(255,255,255,0.3)" strokeWidth="1" />
        
        {/* Liquid */}
        {!broken && liquidLevel > 0 && (
          <g>
            <g clipPath="url(#flask-body)">
              <rect x="0" y={liquidY} width="100" height={100 - liquidY} fill={liquidColor} style={{ transition: 'all 0.3s ease-in-out' }} />
              <rect x="0" y={liquidY} width="100" height={100 - liquidY} fill="url(#liquidGradient)" style={{ mixBlendMode: 'multiply', transition: 'all 0.3s ease-in-out' }} />
              <rect x="0" y={liquidY} width="100" height={100 - liquidY} fill="url(#liquidHighlight)" style={{ mixBlendMode: 'screen', transition: 'all 0.3s ease-in-out' }} />
            </g>
            
            {/* Liquid surface ellipse */}
            <ellipse cx="50" cy={liquidY} rx={rY} ry="6" fill={liquidColor} stroke="rgba(255,255,255,0.4)" strokeWidth="0.5" style={{ transition: 'all 0.3s ease-in-out' }} />
            
            {/* Boiling Bubbles */}
            {operation === 'heating' && temperature > 80 && (
              <g className="animate-pulse">
                <circle cx="45" cy={maxY - 10} r="2" fill="white" opacity="0.6" />
                <circle cx="55" cy={maxY - 15} r="3" fill="white" opacity="0.4" />
                <circle cx="40" cy={maxY - 5} r="1.5" fill="white" opacity="0.5" />
                <circle cx="60" cy={maxY - 8} r="2" fill="white" opacity="0.5" />
                <circle cx="50" cy={liquidY + 10} r="2.5" fill="white" opacity="0.7" />
              </g>
            )}
          </g>
        )}

        {!broken && hasSolid && (
          <g clipPath="url(#flask-body)">
            <path d="M 23 84 Q 50 78 77 84 L 74 89 Q 50 94 26 89 Z" fill={solidColor} opacity=".88" />
            <circle cx="34" cy="84" r="2.2" fill={solidColor} /><circle cx="45" cy="87" r="1.7" fill={solidColor} />
            <circle cx="57" cy="83" r="2.4" fill={solidColor} /><circle cx="67" cy="87" r="1.5" fill={solidColor} />
          </g>
        )}

        {/* Flask Glass Body */}
        <path d={`M 38 15 L 38 30 C 38 35, 15 80, 15 ${maxY} A 35 7 0 0 0 85 ${maxY} C 85 80, 62 35, 62 30 L 62 15`} fill="url(#glassReflection)" stroke={broken ? "#ef4444" : "rgba(255,255,255,0.6)"} strokeWidth="1.5" />

        {/* Cracked / Broken Glass overlay */}
        {broken && (
          <g stroke="#f87171" strokeWidth="1.8" strokeLinecap="round" fill="none">
            <path d="M 42 20 L 52 45 L 30 75 L 50 88" />
            <path d="M 52 45 L 75 58 L 70 82" />
            <text x="50" y="60" fill="#f87171" fontSize="7" fontWeight="800" textAnchor="middle">SHATTERED</text>
          </g>
        )}

        {/* Glass edge highlights */}
        <path d="M 39 16 L 39 29 C 39 34, 17 78, 17 86" stroke="rgba(255,255,255,0.8)" strokeWidth="1.5" fill="none" opacity="0.7" />
        <path d="M 61 16 L 61 29 C 61 34, 83 78, 83 86" stroke="rgba(255,255,255,0.5)" strokeWidth="1" fill="none" opacity="0.5" />

        {/* Front of the neck rim */}
        <path d="M 38 15 A 12 3 0 0 0 62 15" fill="none" stroke="rgba(255,255,255,0.9)" strokeWidth="2" />

        {/* Markings */}
        <g stroke="rgba(255,255,255,0.72)" strokeWidth="0.8" fill="none">
          {ticks.map((tick) => {
            const y = maxY - (tick / capacityMl) * (maxY - minY);
            return <line key={tick} x1="50" y1={y} x2={tick % (tickStep * 2) === 0 ? 59 : 56} y2={y} />;
          })}
        </g>
        <g fill="rgba(255,255,255,0.9)" fontSize="4.5" fontFamily="monospace" textAnchor="end">
          {ticks.filter((tick) => tick % (tickStep * 2) === 0 || tick === capacityMl).map((tick) => {
            const y = maxY - (tick / capacityMl) * (maxY - minY);
            return <text key={tick} x="48" y={y + 1.5}>{Math.round(tick)}</text>;
          })}
        </g>
        {volumeMl > 0 && <text x="50" y="96" textAnchor="middle" fill="rgba(255,255,255,0.95)" fontSize="5.5" fontFamily="monospace">{Math.round(Math.min(volumeMl, capacityMl))} mL</text>}
      </g>
    </svg>
  );
}
