export interface BunsenBurnerProps {
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

export function BunsenBurnerRenderer({ width, height, size = 100, operation = 'idle' }: BunsenBurnerProps) {
  const lit = operation === 'heating';
  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 120 120" role="img" aria-label="Bunsen Burner">
      <defs>
        <linearGradient id="burner-body" x1="0" x2="1" y1="0" y2="1">
          <stop offset="0" stopColor="#334155" />
          <stop offset="0.45" stopColor="#111827" />
          <stop offset="1" stopColor="#020617" />
        </linearGradient>
        <linearGradient id="burner-metal" x1="0" x2="1">
          <stop offset="0" stopColor="#94a3b8" />
          <stop offset="0.45" stopColor="#e2e8f0" />
          <stop offset="1" stopColor="#64748b" />
        </linearGradient>
        <radialGradient id="burner-flame">
          <stop offset="0" stopColor="#fff7ed" />
          <stop offset="0.35" stopColor="#facc15" />
          <stop offset="0.72" stopColor="#f97316" />
          <stop offset="1" stopColor="#dc2626" stopOpacity="0" />
        </radialGradient>
        <filter id="burner-glow"><feGaussianBlur stdDeviation="3" /></filter>
      </defs>
      {lit && <ellipse cx="60" cy="30" rx="22" ry="25" fill="url(#burner-flame)" opacity=".8" filter="url(#burner-glow)" />}
      {lit && <path d="M60 55 C45 43 53 31 60 19 C67 31 75 43 60 55Z" fill="url(#burner-flame)" stroke="#fb923c" strokeWidth="1.5" />}
      <ellipse cx="60" cy="91" rx="43" ry="11" fill="#020617" stroke="#64748b" strokeWidth="2" />
      <rect x="24" y="67" width="72" height="25" rx="6" fill="url(#burner-body)" stroke="#94a3b8" strokeWidth="1.5" />
      <ellipse cx="60" cy="68" rx="29" ry="8" fill="#0f172a" stroke="url(#burner-metal)" strokeWidth="3" />
      <ellipse cx="60" cy="67" rx="19" ry="4.5" fill={lit ? "#f97316" : "#1e293b"} stroke={lit ? "#fdba74" : "#64748b"} strokeWidth="2" />
      <path d="M54 64 H66 L63 28 H57Z" fill="url(#burner-metal)" stroke="#cbd5e1" strokeWidth="1" />
      <circle cx="36" cy="79" r="4" fill={lit ? "#22c55e" : "#ef4444"} />
      <path d="M30 88 H90" stroke="#475569" strokeWidth="1" />
      <text x="60" y="108" textAnchor="middle" fill="#cbd5e1" fontSize="7" fontWeight="600">BUNSEN</text>
    </svg>
  );
}

