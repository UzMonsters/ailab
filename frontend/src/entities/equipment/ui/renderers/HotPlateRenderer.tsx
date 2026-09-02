import { SvgDefs } from './SvgDefs';

export interface HotPlateProps {
  width?: number | string;
  height?: number | string;
  size?: number | string;
  liquidLevel?: number;
  liquidColor?: string;
  temperature?: number;
  targetTemperature?: number;
  volumeMl?: number;
  capacityMl?: number;
  operation?: string;
}

export function HotPlateRenderer({
  width,
  height,
  size = 100,
  operation = 'idle',
  targetTemperature = 80,
}: HotPlateProps) {
  const heating = operation === 'heating';
  const tempText = heating ? `${targetTemperature}°C` : 'OFF';

  return (
    <svg width={width ?? size} height={height ?? size} viewBox="0 0 100 100" preserveAspectRatio="xMidYMid meet" role="img" aria-label="Hotplate">
      <SvgDefs />
      <g filter="url(#dropShadow)">
        {/* Hotplate Base Body */}
        <path 
          d="M 15 75 L 85 75 A 5 5 0 0 1 90 80 L 90 90 A 5 5 0 0 1 85 95 L 15 95 A 5 5 0 0 1 10 90 L 10 80 A 5 5 0 0 1 15 75 Z" 
          fill="url(#hotplateBody)" 
          stroke="#94a3b8" 
          strokeWidth="2.5" 
        />
        
        {/* Top Surface (ceramic/metal) */}
        <ellipse cx="50" cy="73" rx="42" ry="12" fill="url(#hotplateSurface)" stroke="#cbd5e1" strokeWidth="2.5" />
        <ellipse cx="50" cy="73" rx="35" ry="9" fill="#1e293b" stroke="#64748b" strokeWidth="1.5" />
        
        {/* Heating Coils */}
        <g stroke={heating ? "#ef4444" : "#64748b"} strokeWidth="2.5" fill="none" opacity={heating ? 1 : 0.75}>
          <ellipse cx="50" cy="73" rx="28" ry="7" />
          <ellipse cx="50" cy="73" rx="20" ry="5" />
          <ellipse cx="50" cy="73" rx="12" ry="3" />
        </g>

        {/* Heating Glow */}
        {heating && (
          <ellipse cx="50" cy="73" rx="36" ry="10" fill="url(#hotCoilGlow)" opacity="0.8" style={{ mixBlendMode: 'screen' }} />
        )}

        {/* Dial / Knob */}
        <g transform="translate(77, 85)">
          <circle cx="0" cy="0" r="5" fill="#1e293b" stroke="#475569" strokeWidth="1" />
          <circle cx="0" cy="0" r="3" fill="#334155" />
          <line 
            x1="0" y1="0" 
            x2={heating ? "2" : "-2"} 
            y2={heating ? "-3" : "-3"} 
            stroke="#e2e8f0" 
            strokeWidth="1.5" 
            strokeLinecap="round" 
          />
        </g>

        {/* LED Power Indicator */}
        <circle cx="21" cy="85" r="2" fill={heating ? "#ef4444" : "#475569"} />
        {heating && (
          <circle cx="21" cy="85" r="4" fill="url(#ledGlow)" style={{ mixBlendMode: 'screen' }} />
        )}

        {/* Digital LED Screen displaying live numbers! */}
        <rect x="33" y="81" width="34" height="8.5" rx="2" fill="#07111f" stroke={heating ? "#f97316" : "#475569"} strokeWidth="1" />
        <text x="50" y="87.5" fill={heating ? "#fb923c" : "#64748b"} fontSize="5.5" fontWeight="700" fontFamily="monospace" textAnchor="middle">
          {tempText}
        </text>
      </g>
    </svg>
  );
}
