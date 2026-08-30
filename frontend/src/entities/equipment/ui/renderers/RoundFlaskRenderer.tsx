import { SvgDefs } from './SvgDefs';

export interface RoundFlaskProps {
  width?: number | string;
  height?: number | string;
  size?: number | string;
  liquidLevel?: number;
  liquidColor?: string;
  temperature?: number;
  volumeMl?: number;
  capacityMl?: number;
  operation?: string;
  sealed?: boolean;
}

export function RoundFlaskRenderer({
  width,
  height,
  size = 120,
  liquidLevel = 0,
  liquidColor = 'rgba(34,211,238,0.7)',
  sealed = false,
}: RoundFlaskProps) {
  const clampedLevel = Math.max(0, Math.min(1, liquidLevel));
  const fillY = 88 - clampedLevel * 48;

  return (
    <svg
      width={width ?? size}
      height={height ?? size}
      preserveAspectRatio="xMidYMid meet"
      viewBox="0 0 100 100"
      role="img"
      aria-label="Round-bottom flask"
    >
      <defs>
        <SvgDefs />
        <clipPath id="round-flask-clip">
          <path d="M43 10 H57 V38 A 28 28 0 1 1 43 38 Z" />
        </clipPath>
        <linearGradient id="round-glass-gradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stopColor="rgba(255,255,255,0.4)" />
          <stop offset="30%" stopColor="rgba(255,255,255,0.08)" />
          <stop offset="70%" stopColor="rgba(255,255,255,0.03)" />
          <stop offset="100%" stopColor="rgba(255,255,255,0.25)" />
        </linearGradient>
      </defs>

      {sealed && (
        <path d="M 45 4 L 55 4 L 53 14 L 47 14 Z" fill="#2d3748" stroke="#1a202c" strokeWidth="1" />
      )}

      {/* Glass Body Wall */}
      <path
        d="M43 10 H57 V38 A 28 28 0 1 1 43 38 Z"
        fill="url(#round-glass-gradient)"
        stroke="rgba(255,255,255,0.85)"
        strokeWidth="2"
      />

      {/* Liquid Fill */}
      {clampedLevel > 0 && (
        <g clipPath="url(#round-flask-clip)">
          <rect
            x="10"
            y={fillY}
            width="80"
            height={90 - fillY}
            fill={liquidColor}
            opacity="0.85"
          />
          <ellipse
            cx="50"
            cy={fillY}
            rx="24"
            ry="4"
            fill={liquidColor}
            opacity="0.95"
          />
        </g>
      )}

      {/* Top Rim */}
      <ellipse
        cx="50"
        cy="10"
        rx="7.5"
        ry="2.5"
        fill="none"
        stroke="rgba(255,255,255,0.9)"
        strokeWidth="2"
      />

      {/* Glass Highlights */}
      <path
        d="M26 50 A 24 24 0 0 1 36 74"
        fill="none"
        stroke="rgba(255,255,255,0.6)"
        strokeWidth="2.5"
        strokeLinecap="round"
      />
      <path
        d="M44 14 V 34"
        fill="none"
        stroke="rgba(255,255,255,0.5)"
        strokeWidth="1.5"
        strokeLinecap="round"
      />

      {/* Graduation Mark */}
      <line x1="43" y1="38" x2="57" y2="38" stroke="rgba(255,255,255,0.3)" strokeWidth="1" strokeDasharray="2 2" />
    </svg>
  );
}
