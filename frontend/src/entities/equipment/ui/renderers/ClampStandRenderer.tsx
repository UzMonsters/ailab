import type { EquipmentIconProps } from '../EquipmentRendererRegistry';

export type ClampStandProps = EquipmentIconProps;

export function ClampStandRenderer({ width, height, size = 100 }: EquipmentIconProps) {
  return (
    <svg
      width={width ?? size}
      height={height ?? size}
      preserveAspectRatio="xMidYMid meet"
      viewBox="0 0 100 100"
      role="img"
      aria-label="Clamp stand"
    >
      <defs>
        <linearGradient id="clampStandBase" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stopColor="#475569" />
          <stop offset="100%" stopColor="#1e293b" />
        </linearGradient>
        <linearGradient id="clampStandRod" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" stopColor="#94a3b8" />
          <stop offset="40%" stopColor="#f8fafc" />
          <stop offset="100%" stopColor="#475569" />
        </linearGradient>
      </defs>

      {/* Heavy Base Plate */}
      <rect x="15" y="82" width="70" height="10" rx="3" fill="url(#clampStandBase)" stroke="#0f172a" strokeWidth="1.5" />
      <rect x="18" y="83" width="64" height="2" rx="1" fill="#94a3b8" opacity="0.4" />

      {/* Vertical Metallic Support Rod */}
      <rect x="28" y="10" width="6" height="73" rx="2" fill="url(#clampStandRod)" stroke="#334155" strokeWidth="0.8" />

      {/* Adjustable Clamp Bosshead */}
      <rect x="25" y="36" width="12" height="12" rx="2" fill="#334155" stroke="#94a3b8" strokeWidth="1" />
      <circle cx="21" cy="42" r="3.5" fill="#64748b" stroke="#cbd5e1" strokeWidth="1" />

      {/* Extension Arm */}
      <rect x="37" y="40" width="18" height="4" rx="1" fill="url(#clampStandRod)" stroke="#334155" strokeWidth="0.8" />

      {/* 3-Finger Flask Clamp */}
      <path d="M55 42 Q62 30 75 32" fill="none" stroke="#e2e8f0" strokeWidth="3" strokeLinecap="round" />
      <path d="M55 42 Q62 54 75 52" fill="none" stroke="#e2e8f0" strokeWidth="3" strokeLinecap="round" />
      {/* Rubber Sleeves on Clamp Fingers */}
      <path d="M68 31 Q72 31.5 75 32" fill="none" stroke="#ef4444" strokeWidth="4" strokeLinecap="round" />
      <path d="M68 53 Q72 52.5 75 52" fill="none" stroke="#ef4444" strokeWidth="4" strokeLinecap="round" />
    </svg>
  );
}
