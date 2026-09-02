import { SvgDefs } from './SvgDefs';

export interface BuretteProps {
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

export function BuretteRenderer({
  width,
  height,
  size = 180,
  liquidLevel = 0,
  liquidColor = 'rgba(34,211,238,0.75)',
}: BuretteProps) {
  const clampedLevel = Math.max(0, Math.min(1, liquidLevel));
  const fillY = 160 - clampedLevel * 140;

  return (
    <svg
      width={width ?? size}
      height={height ?? size}
      preserveAspectRatio="xMidYMid meet"
      viewBox="0 0 60 200"
      role="img"
      aria-label="Burette"
    >
      <defs>
        <SvgDefs />
        <clipPath id="burette-clip">
          <rect x="20" y="15" width="20" height="145" rx="2" />
        </clipPath>
        <linearGradient id="burette-glass-gradient" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" stopColor="rgba(255,255,255,0.4)" />
          <stop offset="30%" stopColor="rgba(255,255,255,0.1)" />
          <stop offset="70%" stopColor="rgba(255,255,255,0.05)" />
          <stop offset="100%" stopColor="rgba(255,255,255,0.3)" />
        </linearGradient>
      </defs>

      {/* Main Long Glass Tube */}
      <rect
        x="20"
        y="15"
        width="20"
        height="145"
        rx="2"
        fill="url(#burette-glass-gradient)"
        stroke="rgba(255,255,255,0.85)"
        strokeWidth="1.5"
      />

      {/* Liquid Column */}
      {clampedLevel > 0 && (
        <g clipPath="url(#burette-clip)">
          <rect
            x="20"
            y={fillY}
            width="20"
            height={160 - fillY}
            fill={liquidColor}
            opacity="0.85"
          />
          <ellipse
            cx="30"
            cy={fillY}
            rx="10"
            ry="2"
            fill={liquidColor}
            opacity="0.95"
          />
        </g>
      )}

      {/* Graduation Marks */}
      <g stroke="rgba(255,255,255,0.7)" strokeWidth="0.8">
        {Array.from({ length: 21 }, (_, i) => {
          const y = 20 + i * 6.5;
          const isMajor = i % 5 === 0;
          return (
            <line
              key={i}
              x1="32"
              y1={y}
              x2={isMajor ? "40" : "36"}
              y2={y}
            />
          );
        })}
      </g>
      {/* Graduation Numbers */}
      <g fill="rgba(255,255,255,0.85)" fontSize="5" fontFamily="monospace" textAnchor="start">
        {[0, 10, 20, 30, 40, 50].map((val, idx) => (
          <text key={val} x="41" y={21.5 + idx * 32.5}>
            {val}
          </text>
        ))}
      </g>

      {/* Top Rim */}
      <ellipse cx="30" cy="15" rx="10" ry="2.5" fill="none" stroke="rgba(255,255,255,0.9)" strokeWidth="1.5" />

      {/* Glass Highlight */}
      <line x1="23" y1="17" x2="23" y2="158" stroke="rgba(255,255,255,0.5)" strokeWidth="1" strokeLinecap="round" />

      {/* Stopcock Valve Section */}
      <path d="M26 160 L 34 160 L 32 172 L 28 172 Z" fill="rgba(255,255,255,0.2)" stroke="rgba(255,255,255,0.8)" strokeWidth="1.2" />
      {/* Stopcock Knob / Valve Handle */}
      <circle cx="30" cy="166" r="4" fill="#0f172a" stroke="rgba(255,255,255,0.9)" strokeWidth="1.2" />
      <rect x="23" y="164.5" width="14" height="3" rx="1" fill="#f97316" />

      {/* Dispensing Tip */}
      <path d="M28.5 172 L 31.5 172 L 30.5 192 L 29.5 192 Z" fill="rgba(255,255,255,0.2)" stroke="rgba(255,255,255,0.8)" strokeWidth="1" />
    </svg>
  );
}
