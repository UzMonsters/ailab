import { SvgDefs } from './SvgDefs';
import { calculateLiquidLevel } from './VesselGeometry';
export interface BeakerProps {
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

export function BeakerRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(2, 132, 199, 0.8)', temperature = 24.5, volumeMl = 0, capacityMl = 250, hasSolid = false, solidColor = '#CBD5E1', broken = false, sealed = false }: BeakerProps) {
  // SVG viewBox is 0 0 100 100
  // Beaker body goes from y=20 to y=90
  const minY = 20;
  const maxY = 88;
  const beakerWidth = 56;
  const beakerX = 22;
  
  // Calculate liquid fill
  const liquidY = calculateLiquidLevel(liquidLevel, minY, maxY);
  const liquidHeight = maxY - liquidY;
  const volume = Math.max(0, Math.min(capacityMl, volumeMl));
  const tickStep = capacityMl >= 250 ? 50 : Math.max(10, capacityMl / 5);
  const ticks = Array.from({ length: Math.floor(capacityMl / tickStep) + 1 }, (_, index) => index * tickStep);

  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100">
      <SvgDefs />
      <g filter="url(#dropShadow)">
        {sealed && !broken && (
          <path d="M 26 10 L 74 10 L 72 26 L 28 26 Z" fill="#2d3748" stroke="#1a202c" strokeWidth="1" />
        )}
        {/* Back of the glass rim */}
        <ellipse cx="50" cy="20" rx="28" ry="6" fill="url(#glassReflection)" stroke="rgba(255,255,255,0.3)" strokeWidth="1" />
        
        {/* Liquid */}
        {!broken && liquidLevel > 0 && (
          <g className="sandbox-vessel-liquid">
            {/* Liquid body */}
            <path 
              d={`M ${beakerX} ${liquidY} L ${beakerX} ${maxY} A 28 6 0 0 0 ${beakerX + beakerWidth} ${maxY} L ${beakerX + beakerWidth} ${liquidY} Z`} 
              fill={liquidColor} 
              className="sandbox-liquid-body"
              style={{ transition: 'all 0.55s cubic-bezier(.22,1,.36,1)' }}
            />
            {/* Liquid surface ellipse */}
            <ellipse 
              cx="50" 
              cy={liquidY} 
              rx="28" 
              ry="6" 
              fill={liquidColor} 
              stroke="rgba(255,255,255,0.4)" 
              strokeWidth="0.5" 
              className="sandbox-liquid-surface"
              style={{ transition: 'all 0.55s cubic-bezier(.22,1,.36,1)' }}
            />
            {/* Inner liquid depth gradient to make it look volumetric */}
            <path 
              d={`M ${beakerX} ${liquidY} L ${beakerX} ${maxY} A 28 6 0 0 0 ${beakerX + beakerWidth} ${maxY} L ${beakerX + beakerWidth} ${liquidY} Z`} 
              fill="url(#liquidGradient)" 
              style={{ mixBlendMode: 'multiply', transition: 'all 0.3s ease-in-out' }}
            />
            <path 
              d={`M ${beakerX} ${liquidY} L ${beakerX} ${maxY} A 28 6 0 0 0 ${beakerX + beakerWidth} ${maxY} L ${beakerX + beakerWidth} ${liquidY} Z`} 
              fill="url(#liquidHighlight)" 
              className="sandbox-liquid-shimmer"
              style={{ mixBlendMode: 'screen', transition: 'all 0.55s cubic-bezier(.22,1,.36,1)' }}
            />
            {/* Boiling Bubbles */}
            {temperature >= 95 && (
              <g fill="rgba(255,255,255,0.6)">
                <circle cx="35" cy="85" r="1.5" className="animate-rise" />
                <circle cx="45" cy="83" r="2" className="animate-rise delay-100" />
                <circle cx="55" cy="86" r="1" className="animate-rise delay-300" />
                <circle cx="65" cy="84" r="2.5" className="animate-rise delay-500" />
                <circle cx="40" cy="82" r="1.5" className="animate-rise delay-700" />
                <circle cx="60" cy="85" r="2" className="animate-rise delay-300" />
              </g>
            )}
          </g>
        )}

        {!broken && hasSolid && (
          <g>
            <path d="M 24 83 Q 50 78 76 83 L 74 89 Q 50 94 26 89 Z" fill={solidColor} opacity=".88" />
            <circle cx="34" cy="84" r="2" fill={solidColor} /><circle cx="45" cy="87" r="1.6" fill={solidColor} />
            <circle cx="56" cy="83" r="2.2" fill={solidColor} /><circle cx="67" cy="87" r="1.5" fill={solidColor} />
          </g>
        )}

        {/* Beaker Glass Body */}
        <path 
          d={`M ${beakerX} 20 L ${beakerX} ${maxY} A 28 6 0 0 0 ${beakerX + beakerWidth} ${maxY} L ${beakerX + beakerWidth} 20`} 
          fill="url(#glassReflection)" 
          stroke={broken ? "#ef4444" : "rgba(255,255,255,0.6)"} 
          strokeWidth="1.5" 
        />

        {/* Cracked / Broken Glass overlay */}
        {broken && (
          <g stroke="#f87171" strokeWidth="1.8" strokeLinecap="round" fill="none">
            <path d="M 28 25 L 42 45 L 35 68 L 48 85" />
            <path d="M 42 45 L 68 38 L 74 65" />
            <path d="M 68 38 L 78 22" />
            <path d="M 35 68 L 22 75" />
            <text x="50" y="55" fill="#f87171" fontSize="7" fontWeight="800" textAnchor="middle">SHATTERED</text>
          </g>
        )}

        {/* Beaker Rim Lip (Spout) */}
        <path d="M 22 20 Q 18 18 16 16 Q 20 20 22 23" fill="rgba(255,255,255,0.8)" />

        {/* Front of the glass rim */}
        <path d="M 22 20 A 28 6 0 0 0 78 20" fill="none" stroke="rgba(255,255,255,0.9)" strokeWidth="2" />
        
        {/* Glass edge highlights */}
        <path d={`M 23 23 L 23 ${maxY - 2}`} stroke="rgba(255,255,255,0.8)" strokeWidth="1.5" fill="none" opacity="0.7" />
        <path d={`M 77 23 L 77 ${maxY - 2}`} stroke="rgba(255,255,255,0.5)" strokeWidth="1" fill="none" opacity="0.5" />

        {/* Markings */}
        <g stroke="rgba(255,255,255,0.72)" strokeWidth="0.8" fill="none">
          {ticks.map((tick) => {
            const y = maxY - (tick / capacityMl) * (maxY - minY);
            return <line key={tick} x1="39" y1={y} x2={tick % (tickStep * 2) === 0 ? 49 : 46} y2={y} />;
          })}
        </g>
        <g fill="rgba(255,255,255,0.9)" fontSize="4.5" fontFamily="monospace" textAnchor="end">
          {ticks.filter((tick) => tick % (tickStep * 2) === 0 || tick === capacityMl).map((tick) => {
            const y = maxY - (tick / capacityMl) * (maxY - minY);
            return <text key={tick} x="37" y={y + 1.5}>{Math.round(tick)}</text>;
          })}
        </g>
        {!broken && volume > 0 && <text x="50" y="96" textAnchor="middle" fill="rgba(255,255,255,0.95)" fontSize="5.5" fontFamily="monospace">{Math.round(volumeMl)} mL</text>}
      </g>
    </svg>
  );
}
