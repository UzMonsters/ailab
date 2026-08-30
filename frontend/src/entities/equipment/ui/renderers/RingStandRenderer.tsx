import type { EquipmentIconProps } from '../EquipmentRendererRegistry';

export function RingStandRenderer({ width, height, size = 100 }: EquipmentIconProps) {
  return (
    <svg
      width={width ?? size}
      height={height ?? size}
      preserveAspectRatio="xMidYMid meet"
      viewBox="0 0 100 100"
      role="img"
      aria-label="Ring stand"
    >
      <defs>
        <linearGradient id="ringStandBase" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stopColor="#475569" />
          <stop offset="100%" stopColor="#1e293b" />
        </linearGradient>
        <linearGradient id="ringStandRod" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" stopColor="#94a3b8" />
          <stop offset="40%" stopColor="#f8fafc" />
          <stop offset="100%" stopColor="#475569" />
        </linearGradient>
      </defs>

      {/* Heavy Base Plate */}
      <rect x="15" y="82" width="70" height="10" rx="3" fill="url(#ringStandBase)" stroke="#0f172a" strokeWidth="1.5" />
      <rect x="18" y="83" width="64" height="2" rx="1" fill="#94a3b8" opacity="0.4" />

      {/* Vertical Metallic Support Rod */}
      <rect x="28" y="10" width="6" height="73" rx="2" fill="url(#ringStandRod)" stroke="#334155" strokeWidth="0.8" />

      {/* Adjustable Clamp Bosshead */}
      <rect x="25" y="36" width="12" height="12" rx="2" fill="#334155" stroke="#94a3b8" strokeWidth="1" />
      {/* Tightening Knob */}
      <circle cx="21" cy="42" r="3.5" fill="#64748b" stroke="#cbd5e1" strokeWidth="1" />

      {/* Extension Arm */}
      <rect x="37" y="40" width="16" height="4" rx="1" fill="url(#ringStandRod)" stroke="#334155" strokeWidth="0.8" />

      {/* Iron Ring Support */}
      <ellipse cx="65" cy="42" rx="18" ry="7" fill="none" stroke="url(#ringStandRod)" strokeWidth="3.5" />
      <ellipse cx="65" cy="42" rx="18" ry="7" fill="none" stroke="#1e293b" strokeWidth="0.8" />
    </svg>
  );
}

