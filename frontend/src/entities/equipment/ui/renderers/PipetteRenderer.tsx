import { SvgDefs } from './SvgDefs';

export interface PipetteProps {
  width?: number | string;
  height?: number | string;
  size?: number | string;
  liquidLevel?: number;
  liquidColor?: string;
  temperature?: number;
  volumeMl?: number;
  capacityMl?: number;
  operation?: string;
  broken?: boolean;
}

export function PipetteRenderer({
  width,
  height,
  size = 110,
  liquidLevel = 0,
  liquidColor = 'rgba(34,211,238,0.75)',
  broken = false,
}: PipetteProps) {
  const clampedLevel = Math.max(0, Math.min(1, liquidLevel));
  const fillY = 94 - clampedLevel * 62;

  return (
    <svg
      width={width ?? size}
      height={height ?? size}
      preserveAspectRatio="xMidYMid meet"
      viewBox="0 0 36 100"
      role="img"
      aria-label="Pipette"
    >
      <defs>
        <SvgDefs />
        <clipPath id="pipette-glass-clip">
          <path d="M 15 28 H 21 V 42 Q 21 46 25 50 V 70 Q 21 74 21 78 V 92 L 18 97 L 15 92 V 78 Q 15 74 11 70 V 50 Q 15 46 15 42 Z" />
        </clipPath>

        {/* Rubber Bulb 3D Gradient */}
        <radialGradient id="rubber-bulb-grad" cx="35%" cy="30%" r="70%">
          <stop offset="0%" stopColor="#f87171" />
          <stop offset="45%" stopColor="#dc2626" />
          <stop offset="90%" stopColor="#991b1b" />
          <stop offset="100%" stopColor="#7f1d1d" />
        </radialGradient>

        {/* Glass Cylinder Gradient */}
        <linearGradient id="pipette-glass-grad" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" stopColor="rgba(255,255,255,0.45)" />
          <stop offset="25%" stopColor="rgba(255,255,255,0.12)" />
          <stop offset="70%" stopColor="rgba(255,255,255,0.05)" />
          <stop offset="100%" stopColor="rgba(255,255,255,0.35)" />
        </linearGradient>
      </defs>

      {/* --- RUBBER BULB (ТОП ПИПЕТКИ) --- */}
      <path
        d="M 18 3 C 10 3, 7 12, 10 20 C 12 24, 15 25, 18 25 C 21 25, 24 24, 26 20 C 29 12, 26 3, 18 3 Z"
        fill="url(#rubber-bulb-grad)"
        stroke="#7f1d1d"
        strokeWidth="1"
      />
      <ellipse cx="14" cy="10" rx="2.5" ry="3.5" fill="rgba(255,255,255,0.4)" transform="rotate(-15 14 10)" />
      <rect x="14" y="24" width="8" height="4" rx="1" fill="#991b1b" stroke="#7f1d1d" strokeWidth="0.8" />

      {/* --- GLASS PIPETTE BODY --- */}
      <path
        d="M 15 28 H 21 V 42 Q 21 46 25 50 V 70 Q 21 74 21 78 V 92 L 18 97 L 15 92 V 78 Q 15 74 11 70 V 50 Q 15 46 15 42 Z"
        fill="url(#pipette-glass-grad)"
        stroke={broken ? "#ef4444" : "rgba(255,255,255,0.9)"}
        strokeWidth="1.2"
      />

      {/* --- LIQUID FILL --- */}
      {!broken && clampedLevel > 0 && (
        <g clipPath="url(#pipette-glass-clip)">
          <rect
            x="2"
            y={fillY}
            width="32"
            height={98 - fillY}
            fill={liquidColor}
            opacity="0.85"
          />
          <ellipse cx="18" cy={fillY} rx="5" ry="1.2" fill={liquidColor} opacity="0.95" />
        </g>
      )}

      {/* --- BROKEN OVERLAY --- */}
      {broken && (
        <g stroke="#f87171" strokeWidth="1.5" strokeLinecap="round" fill="none">
          <path d="M 14 45 L 22 55 L 15 65" />
          <path d="M 22 55 L 25 75" />
        </g>
      )}

      {/* --- GLASS HIGHLIGHTS --- */}
      <path
        d="M 16 30 V 42 Q 16 45 18 48 V 72 Q 16 75 16 78 V 90"
        fill="none"
        stroke="rgba(255,255,255,0.6)"
        strokeWidth="0.8"
        strokeLinecap="round"
      />

      {/* --- GRADUATION MARKS --- */}
      <g stroke="rgba(255,255,255,0.75)" strokeWidth="0.7">
        <line x1="21" y1="34" x2="23" y2="34" />
        <line x1="21" y1="38" x2="24" y2="38" />
        <line x1="22" y1="56" x2="25" y2="56" />
        <line x1="22" y1="62" x2="26" y2="62" />
        <line x1="22" y1="68" x2="25" y2="68" />
      </g>
    </svg>
  );
}
