import { SvgDefs } from './SvgDefs';

export interface ThermometerProps {
  size?: number;
  width?: number;
  height?: number;
  temperature?: number;
  connected?: boolean;
}

// Scale: -20°C to 120°C, mapped to tube y: 25 → 170
// Tube: x=28–46, Bulb: cx=37, cy=192
// Scale lines: x=48–62, labels: x=65
const TUBE_TOP_Y = 30;    // y where max temp (400°C) liquid level sits
const TUBE_BOT_Y = 172;   // y where liquid meets bulb
const MIN_TEMP = 0;
const MAX_TEMP = 400;

function liquidY(temp: number): number {
  const clamped = Math.max(MIN_TEMP, Math.min(MAX_TEMP, temp));
  const ratio = (clamped - MIN_TEMP) / (MAX_TEMP - MIN_TEMP); // 0..1
  // ratio=1 → TUBE_TOP_Y, ratio=0 → TUBE_BOT_Y
  return TUBE_BOT_Y - ratio * (TUBE_BOT_Y - TUBE_TOP_Y);
}

const SCALE_TICKS = [0, 50, 100, 150, 200, 250, 300, 350, 400];
const TICK_Y = (temp: number) => {
  const ratio = (temp - MIN_TEMP) / (MAX_TEMP - MIN_TEMP);
  return TUBE_BOT_Y - ratio * (TUBE_BOT_Y - TUBE_TOP_Y);
};

export function ThermometerRenderer({
  size,
  width,
  height,
  temperature = 24.5,
  connected = false,
}: ThermometerProps) {
  const liqY = liquidY(temperature);
  // Natural aspect: 80 wide, 220 tall
  const svgW = width ?? size ?? 80;
  const svgH = height ?? size ?? 220;

  return (
    <svg
      width={svgW}
      height={svgH}
      viewBox="0 0 80 220"
      preserveAspectRatio="xMidYMid meet"
      role="img"
      aria-label={`Термометр ${temperature.toFixed(1)} °C`}
    >
      <SvgDefs />

      {/* ── Glass body ───────────────────────────────────── */}
      <g id="glass-body" filter="url(#dropShadow)">
        {/* Tube outer */}
        <rect
          x="28" y="20"
          width="18" height="160"
          rx="9" ry="9"
          fill="url(#glassReflection)"
          stroke="rgba(255,255,255,0.6)"
          strokeWidth="1.5"
        />
        {/* Bulb */}
        <circle
          cx="37" cy="192"
          r="16"
          fill="url(#glassReflection)"
          stroke="rgba(255,255,255,0.6)"
          strokeWidth="1.5"
        />
        {/* Tube highlight */}
        <rect
          x="30" y="22"
          width="4" height="155"
          rx="2"
          fill="rgba(255,255,255,0.35)"
          opacity="0.7"
        />
      </g>

      {/* ── Liquid (mercury / alcohol) ───────────────────── */}
      <g id="thermometer-liquid">
        {/* Tube fill — transitions smoothly on temperature change */}
        <rect
          x="31" y={liqY}
          width="12" height={Math.max(0, TUBE_BOT_Y - liqY + 9)}
          rx="5" ry="5"
          fill="url(#thermoLiquid)"
          style={{ transition: 'y 0.6s ease-in-out, height 0.6s ease-in-out' }}
        />
        {/* Bulb fill */}
        <circle
          cx="37" cy="192"
          r="12"
          fill="url(#thermoLiquid)"
        />
      </g>

      {/* ── Scale lines ─────────────────────────────────── */}
      <g id="scale-lines" stroke="rgba(255,255,255,0.75)" strokeWidth="0.8" fill="none">
        {SCALE_TICKS.map((t) => (
          <line
            key={t}
            x1="48" y1={TICK_Y(t)}
            x2={t % 40 === 0 ? 60 : 55}
            y2={TICK_Y(t)}
          />
        ))}
        {/* Minor ticks every 10°C */}
        {Array.from({ length: 15 }, (_, i) => MIN_TEMP + i * 10).filter(t => !SCALE_TICKS.includes(t)).map(t => (
          <line
            key={`minor-${t}`}
            x1="48" y1={TICK_Y(t)}
            x2="52"
            y2={TICK_Y(t)}
            opacity="0.5"
          />
        ))}
      </g>

      {/* ── Scale labels ────────────────────────────────── */}
      <g
        id="scale-labels"
        fill="rgba(255,255,255,0.9)"
        fontSize="6.5"
        fontFamily="monospace"
        textAnchor="start"
      >
        {SCALE_TICKS.filter(t => t % 100 === 0).map((t) => (
          <text key={`label-${t}`} x="62" y={TICK_Y(t) + 2.5}>
            {t}°
          </text>
        ))}
        {/* Unit label at top */}
        <text x="47" y="18" fontSize="5.5" fill="rgba(255,255,255,0.6)">°C</text>
      </g>

      {/* ── Current temperature indicator ────────────────── */}
      {connected && (
        <g id="temperature-label">
          <rect
            x="4" y={liqY - 8}
            width="22" height="11"
            rx="3"
            fill="rgba(20,25,40,0.85)"
            stroke="rgba(163,230,53,0.6)"
            strokeWidth="0.8"
            style={{ transition: 'y 0.6s ease-in-out' }}
          />
          <text
            x="15" y={liqY + 0.5}
            textAnchor="middle"
            fill="rgba(163,230,53,0.95)"
            fontSize="5"
            fontFamily="monospace"
            fontWeight="bold"
            style={{ transition: 'y 0.6s ease-in-out' }}
          >
            {temperature.toFixed(1)}
          </text>
        </g>
      )}

      {/* ── Connected check ──────────────────────────────── */}
      {connected && (
        <g id="connected-check">
          <circle cx="37" cy="210" r="5" fill="#34D399" opacity="0.95" />
          <text x="37" y="213" textAnchor="middle" fontSize="6" fill="#052e16" fontWeight="bold">✓</text>
        </g>
      )}
    </svg>
  );
}
