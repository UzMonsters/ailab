import type { EquipmentIconProps } from '../EquipmentRendererRegistry';
import { SvgDefs } from './SvgDefs';

export function FunnelRenderer({ width, height, size = 100 }: EquipmentIconProps) {
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100" role="img" aria-label="Funnel">
      <SvgDefs />
      <g filter="url(#dropShadow)">
        {/* Back of top rim */}
        <ellipse cx="50" cy="15" rx="35" ry="5" fill="url(#glassReflection)" stroke="rgba(255,255,255,0.2)" strokeWidth="1" />
        
        {/* Funnel Body */}
        <path 
          d="M 15 15 L 45 55 L 45 90 A 5 2 0 0 0 55 90 L 55 55 L 85 15" 
          fill="url(#glassReflection)" 
          stroke="rgba(255,255,255,0.7)" 
          strokeWidth="1.5" 
          strokeLinejoin="round" 
        />
        
        {/* Highlights */}
        <path d="M 18 17 L 46 54 L 46 88" stroke="rgba(255,255,255,0.9)" strokeWidth="2" fill="none" opacity="0.6" />
        <path d="M 82 17 L 54 54 L 54 88" stroke="rgba(255,255,255,0.4)" strokeWidth="1" fill="none" opacity="0.4" />

        {/* Front of top rim */}
        <path d="M 15 15 A 35 5 0 0 0 85 15" fill="none" stroke="rgba(255,255,255,0.9)" strokeWidth="2" />
        
        {/* Connection Port indicator */}
        <circle cx="50" cy="50" r="8" fill="rgba(255,255,255,0.1)" stroke="rgba(255,255,255,0.3)" strokeWidth="1" strokeDasharray="2 2" />
        <text x="50" y="53" fill="rgba(255,255,255,0.5)" fontSize="10" textAnchor="middle">+</text>
      </g>
    </svg>
  );
}

export function SeparatoryFunnelRenderer({ width, height, size = 100, liquidLevel = 0, liquidColor = 'rgba(2, 132, 199, 0.8)' }: EquipmentIconProps) {
  const minY = 20;
  const maxY = 75;
  const liquidY = maxY - (maxY - minY) * liquidLevel;

  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100" role="img" aria-label="Separatory funnel">
      <SvgDefs />
      <defs>
        <clipPath id="separatoryFunnelClip">
          <path d="M 30 15 L 30 25 C 30 40, 15 45, 15 55 C 15 65, 45 75, 45 80 L 45 95 A 5 2 0 0 0 55 95 L 55 80 C 55 75, 85 65, 85 55 C 85 45, 70 40, 70 25 L 70 15 Z" />
        </clipPath>
      </defs>
      <g filter="url(#dropShadow)">
        <ellipse cx="50" cy="15" rx="20" ry="4" fill="url(#glassReflection)" stroke="rgba(255,255,255,0.2)" strokeWidth="1" />
        
        {/* Liquid */}
        {liquidLevel > 0 && (
          <g clipPath="url(#separatoryFunnelClip)">
            <rect x="0" y={liquidY} width="100" height={100 - liquidY} fill={liquidColor} style={{ transition: 'all 0.3s ease-in-out' }} />
            <rect x="0" y={liquidY} width="100" height={100 - liquidY} fill="url(#liquidGradient)" style={{ mixBlendMode: 'multiply', transition: 'all 0.3s ease-in-out' }} />
            {/* Liquid surface */}
            <ellipse cx="50" cy={liquidY} rx="35" ry="5" fill={liquidColor} stroke="rgba(255,255,255,0.4)" strokeWidth="0.5" style={{ transition: 'all 0.3s ease-in-out' }} />
          </g>
        )}

        <path 
          d="M 30 15 L 30 25 C 30 40, 15 45, 15 55 C 15 65, 45 75, 45 80 L 45 95 A 5 2 0 0 0 55 95 L 55 80 C 55 75, 85 65, 85 55 C 85 45, 70 40, 70 25 L 70 15" 
          fill="url(#glassReflection)" 
          stroke="rgba(255,255,255,0.7)" 
          strokeWidth="1.5" 
        />
        <path d="M 30 15 A 20 4 0 0 0 70 15" fill="none" stroke="rgba(255,255,255,0.9)" strokeWidth="2" />
        {/* Stopcock valve */}
        <rect x="35" y="82" width="30" height="4" rx="2" fill="rgba(200,200,200,0.8)" stroke="#fff" />
        <circle cx="50" cy="84" r="3" fill="#facc15" />
      </g>
    </svg>
  );
}

