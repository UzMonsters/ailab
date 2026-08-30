import { SvgDefs } from './SvgDefs';

export interface pHMeterProps {
  width?: number | string;
  height?: number | string;
  size?: number | string;
  liquidLevel?: number;
  liquidColor?: string;
  temperature?: number;
  volumeMl?: number;
  capacityMl?: number;
  operation?: string;
  phValue?: number | string;
  indicatorColor?: string;
  statusText?: string;
  connected?: boolean;
}

export function pHMeterRenderer({
  width,
  height,
  size = 100,
  liquidLevel = 0,
  liquidColor = 'rgba(0,100,255,0.5)',
  operation = 'idle',
  phValue,
  indicatorColor = '#10b981',
  statusText,
  connected = true,
}: pHMeterProps) {
  const isON = operation === 'active' || operation === 'measuring';

  let displayVal = 'pH --';
  if (!connected) {
    displayVal = 'NO LINK';
  } else if (!isON) {
    displayVal = 'OFF';
  } else if (statusText && statusText.startsWith('pH')) {
    displayVal = statusText;
  } else if (typeof phValue === 'number') {
    displayVal = `pH ${phValue.toFixed(2)}`;
  } else if (phValue) {
    displayVal = `pH ${phValue}`;
  }

  const ledColor = isON ? (connected ? indicatorColor : '#f97316') : '#ef4444';

  return (
    <svg width={width ?? size} height={height ?? size} preserveAspectRatio="xMidYMid meet" viewBox="0 0 100 100" role="img" aria-label="pH meter">
      <SvgDefs />
      <rect x="24" y="13" width="52" height="52" rx="8" fill="#111827" stroke="#cbd5e1" strokeWidth="2" />
      <rect x="30" y="20" width="40" height="22" rx="3" fill="#07111f" stroke={isON ? ledColor : '#64748b'} strokeWidth="1.5" />
      
      {/* Dynamic text displaying live pH numbers inside SVG! */}
      <text x="50" y="34" textAnchor="middle" fill={isON ? ledColor : '#475569'} fontSize="8" fontWeight="700" fontFamily="monospace">
        {displayVal}
      </text>

      <path d="M38 65v16c0 6 5 10 12 10s12-4 12-10V65" fill="url(#glassReflection)" stroke="#dbeafe" strokeWidth="2" />
      <path d="M45 65v19" stroke="white" strokeOpacity=".55" strokeWidth="2" />
      {liquidLevel > 0 && <path d="M40 77h20v5c0 4-4 6-10 6s-10-2-10-6z" fill={liquidColor} opacity=".85" />}
      
      {/* Power LED Indicator */}
      <circle cx="50" cy="54" r="3.5" fill={ledColor} />
    </svg>
  );
}
