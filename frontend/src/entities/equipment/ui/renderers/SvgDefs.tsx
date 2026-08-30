export function SvgDefs() {
  return (
    <defs>
      {/* Glass Reflections */}
      <linearGradient id="glassReflection" x1="0" x2="1" y1="0" y2="0">
        <stop offset="0%" stopColor="rgba(255,255,255,0.7)" />
        <stop offset="5%" stopColor="rgba(255,255,255,0.9)" />
        <stop offset="15%" stopColor="rgba(255,255,255,0.1)" />
        <stop offset="85%" stopColor="rgba(255,255,255,0.05)" />
        <stop offset="95%" stopColor="rgba(255,255,255,0.6)" />
        <stop offset="100%" stopColor="rgba(255,255,255,0.3)" />
      </linearGradient>

      <linearGradient id="glassEdge" x1="0" x2="1" y1="0" y2="0">
        <stop offset="0%" stopColor="rgba(200,220,255,0.8)" />
        <stop offset="50%" stopColor="rgba(255,255,255,0.1)" />
        <stop offset="100%" stopColor="rgba(200,220,255,0.6)" />
      </linearGradient>

      <linearGradient id="glassRim" x1="0" x2="0" y1="0" y2="1">
        <stop offset="0%" stopColor="rgba(255,255,255,0.9)" />
        <stop offset="50%" stopColor="rgba(255,255,255,0.3)" />
        <stop offset="100%" stopColor="rgba(255,255,255,0.8)" />
      </linearGradient>
      
      <linearGradient id="liquidGradient" x1="0" x2="0" y1="0" y2="1">
        <stop offset="0%" stopColor="rgba(255,255,255,0.4)" />
        <stop offset="20%" stopColor="rgba(0,0,0,0.1)" />
        <stop offset="100%" stopColor="rgba(0,0,0,0.4)" />
      </linearGradient>

      <linearGradient id="liquidHighlight" x1="0" x2="1" y1="0" y2="0">
        <stop offset="0%" stopColor="rgba(255,255,255,0.5)" />
        <stop offset="10%" stopColor="rgba(255,255,255,0.0)" />
        <stop offset="90%" stopColor="rgba(255,255,255,0.0)" />
        <stop offset="100%" stopColor="rgba(255,255,255,0.3)" />
      </linearGradient>

      {/* Thermometer specific */}
      <linearGradient id="thermoLiquid" x1="0" x2="1" y1="0" y2="0">
        <stop offset="0%" stopColor="#b91c1c" />
        <stop offset="50%" stopColor="#ef4444" />
        <stop offset="100%" stopColor="#991b1b" />
      </linearGradient>
      <linearGradient id="thermoGlass" x1="0" x2="1" y1="0" y2="0">
        <stop offset="0%" stopColor="rgba(255,255,255,0.6)" />
        <stop offset="20%" stopColor="rgba(255,255,255,0.9)" />
        <stop offset="40%" stopColor="rgba(255,255,255,0.1)" />
        <stop offset="80%" stopColor="rgba(255,255,255,0.0)" />
        <stop offset="100%" stopColor="rgba(255,255,255,0.4)" />
      </linearGradient>

      {/* Hotplate specific */}
      <linearGradient id="hotplateBody" x1="0" x2="0" y1="0" y2="1">
        <stop offset="0%" stopColor="#2A2D35" />
        <stop offset="10%" stopColor="#1A1C23" />
        <stop offset="100%" stopColor="#0D0E12" />
      </linearGradient>
      <linearGradient id="hotplateSurface" x1="0" x2="0" y1="0" y2="1">
        <stop offset="0%" stopColor="#E2E8F0" />
        <stop offset="100%" stopColor="#94A3B8" />
      </linearGradient>
      
      <radialGradient id="hotCoilGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="rgba(249,115,22,1)" />
        <stop offset="40%" stopColor="rgba(239,68,68,0.8)" />
        <stop offset="100%" stopColor="rgba(239,68,68,0)" />
      </radialGradient>

      <radialGradient id="ledGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#ef4444" />
        <stop offset="50%" stopColor="rgba(239,68,68,0.5)" />
        <stop offset="100%" stopColor="rgba(239,68,68,0)" />
      </radialGradient>

      {/* Filters */}
      <filter id="glow">
        <feGaussianBlur stdDeviation="2.5" result="coloredBlur"/>
        <feMerge>
          <feMergeNode in="coloredBlur"/>
          <feMergeNode in="SourceGraphic"/>
        </feMerge>
      </filter>
      
      <filter id="intenseGlow">
        <feGaussianBlur stdDeviation="4" result="coloredBlur"/>
        <feMerge>
          <feMergeNode in="coloredBlur"/>
          <feMergeNode in="SourceGraphic"/>
        </feMerge>
      </filter>
      
      <filter id="dropShadow" x="-10%" y="-10%" width="120%" height="120%">
        <feDropShadow dx="0" dy="4" stdDeviation="6" floodColor="#000000" floodOpacity="0.5" />
      </filter>

      <filter id="glassThickness">
        <feDropShadow dx="0" dy="0" stdDeviation="3" floodColor="#ffffff" floodOpacity="0.4" />
      </filter>
    </defs>
  );
}
