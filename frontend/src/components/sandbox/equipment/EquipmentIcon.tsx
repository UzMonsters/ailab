'use client';

export type EquipmentOperation = 'idle' | 'heating' | 'cooling' | 'stirring';

export type EquipmentIconProps = {
  type: string;
  size?: number;
  liquidLevel?: number;
  liquidColor?: string;
  operation?: EquipmentOperation;
};

const liquidPath = (level: number, color: string) => {
  const top = Math.max(38, 88 - Math.min(1, Math.max(0, level)) * 46);
  return <path className="sandbox-liquid-surface" d={`M22 ${top} Q50 ${top - 4} 78 ${top} V84 Q78 90 70 90 H30 Q22 90 22 84Z`} fill={color} opacity=".58" stroke="none" />;
};

export default function EquipmentIcon({ type, size = 100, liquidLevel = 0, liquidColor = '#22D3EE', operation = 'idle' }: EquipmentIconProps) {
  const animated = operation !== 'idle';
  const common = { width: size, height: size, viewBox: '0 0 100 100', fill: 'none', stroke: 'currentColor', strokeWidth: 3 };
  if (type === 'thermometer') return <svg {...common} aria-hidden="true"><path d="M50 17a9 9 0 0 0-9 9v39a16 16 0 1 0 18 0V26a9 9 0 0 0-9-9Z" strokeWidth="4" /><path d="M50 42v25" stroke="#F59E0B" strokeWidth="7" strokeLinecap="round" /><circle cx="50" cy="76" r="9" fill="#F59E0B" /><path d="M65 39h8M65 51h5M65 63h8" strokeWidth="3" strokeLinecap="round" /></svg>;
  if (type === 'burner') return <svg {...common} aria-hidden="true">
    <path d="M20 88h60l-7 7H27z" fill="currentColor" opacity=".2" stroke="currentColor" />
    <path d="M30 86h40M36 82h28V54H36z" fill="var(--background)" stroke="currentColor" strokeLinejoin="round" />
    <path d="M42 54V38h16v16M34 64h32M31 76h38" stroke="currentColor" strokeLinecap="round" />
    <path d="M58 46h17l8 5" stroke="currentColor" strokeLinecap="round" />
    <path className={operation === 'heating' ? 'sandbox-flame' : ''} d="M50 38c-7-9-3-17 3-23 1 7 8 9 7 16-1 4-4 6-10 7Z" fill="#F59E0B" stroke="#FBBF24" strokeLinejoin="round" />
    <path className={operation === 'heating' ? 'sandbox-flame' : ''} d="M51 35c-3-5-1-8 2-12 1 4 4 6 3 9-1 2-2 3-5 3Z" fill="#FEF3C7" stroke="#F59E0B" strokeWidth="1.5" />
  </svg>;
  if (type === 'hotplate') return <svg width={size} height={size} viewBox="0 0 140 100" fill="none" stroke="currentColor" strokeWidth="3" aria-hidden="true"><rect x="14" y="28" width="112" height="54" rx="10" fill="var(--background)" /><ellipse cx="58" cy="53" rx="28" ry="14" fill="#7C3AED" opacity={animated ? '.4' : '.22'} stroke="#A78BFA" /><ellipse cx="58" cy="53" rx="19" ry="9" stroke="#C4B5FD" /><circle cx="103" cy="52" r="8" fill="#F59E0B" stroke="#FBBF24" /><path d="M26 82h88" strokeLinecap="round" opacity=".5" /></svg>;
  if (type === 'condenser') return <svg width={size} height={size * .56} viewBox="0 0 180 100" fill="none" stroke="currentColor" strokeWidth="3" aria-hidden="true"><path d="M20 25h35M20 75h35M145 25h15M145 75h15" strokeLinecap="round" /><rect x="45" y="18" width="100" height="64" rx="12" fill="var(--background)" /><path d="M58 24v52M70 24v52M82 24v52M94 24v52M106 24v52M118 24v52M130 24v52" stroke="#22D3EE" opacity=".65" /><path d="M20 25h12v18M160 75h-12V57M20 75h12V57M160 25h-12v18" stroke="#34D399" /></svg>;
  if (type === 'mantle') return <svg {...common} aria-hidden="true"><path d="M20 82h60M27 76h46l-5-42H32z" fill="var(--background)" /><path d="M34 38h32M38 47h24M41 56h18M44 65h12" stroke="#F59E0B" opacity=".8" /><path d="M38 31h24" strokeWidth="5" strokeLinecap="round" /></svg>;
  if (['icebath', 'coolingbath'].includes(type)) return <svg {...common} aria-hidden="true"><path d="M18 42h64v34a8 8 0 0 1-8 8H26a8 8 0 0 1-8-8Z" fill="#67E8F9" opacity=".18" /><path d="M18 42h64M27 50l8 8m0-8-8 8m25-8 8 8m0-8-8 8m-9 8 8 8m0-8-8 8" stroke="#67E8F9" strokeLinecap="round" /><path d="M12 86h76" /></svg>;
  if (type === 'refrigerator') return <svg {...common} aria-hidden="true"><rect x="27" y="12" width="46" height="76" rx="4" fill="var(--background)" /><path d="M27 43h46M65 25v9M65 54v9" stroke="#67E8F9" strokeLinecap="round" /><circle cx="39" cy="27" r="4" fill="#67E8F9" opacity=".5" /><circle cx="50" cy="61" r="4" fill="#67E8F9" opacity=".5" /></svg>;
  if (['scales', 'digitalbalance'].includes(type)) return <svg {...common} aria-hidden="true"><path d="M19 78h62M25 78V43h50v35M32 43a18 18 0 0 1 36 0M40 43h20" fill="var(--background)" /><rect x="37" y="55" width="26" height="10" rx="2" fill="#34D399" opacity=".45" stroke="#34D399" /><path d="M45 82h10" /></svg>;
  if (['pipette', 'burette'].includes(type)) return <svg {...common} aria-hidden="true"><path d={type === 'pipette' ? 'M18 25h48l15 15-8 8-15-15H18zM57 40l-25 25' : 'M40 10h20v65a10 10 0 0 1-20 0zM40 28h20M40 48h20'} fill="var(--background)" /><path d={type === 'pipette' ? 'M27 34h22M32 65l-8 12' : 'M44 68h12M44 58h12'} stroke="#22D3EE" /></svg>;
  if (['funnel', 'buchner'].includes(type)) return <svg {...common} aria-hidden="true"><path d="M18 18h64L57 51v29H43V51z" fill="var(--background)" strokeLinejoin="round" /><path d="M27 27h46M34 35h32" stroke="#CBD5E1" opacity=".65" /><path d="M43 80h14" /></svg>;
  const isRound = type === 'roundflask';
  const isErlenmeyer = type === 'erlenmeyer';
  const isTube = type === 'testtube';
  return <svg {...common} aria-hidden="true">
    {isRound ? <path d="M40 10 H60 M45 10 V40 A30 30 0 1 0 55 40 V10" /> : isErlenmeyer ? <path d="M40 10 H60 M45 10 V35 L20 80 Q15 90 25 90 H75 Q85 90 80 80 L55 35 V10" strokeLinecap="round" strokeLinejoin="round" /> : isTube ? <path d="M35 12h30M38 12v57a12 12 0 0 0 24 0V12" strokeLinecap="round" /> : <path d="M20 10 H80 M30 10 V80 Q30 90 40 90 H60 Q70 90 70 80 V10" strokeLinecap="round" strokeLinejoin="round" />}
    {!isTube && liquidLevel > 0 && liquidPath(liquidLevel, liquidColor)}
    {isTube && liquidLevel > 0 && <path d={`M39 ${Math.max(38, 70 - liquidLevel * 25)}h22v${Math.min(31, 70 - Math.max(38, 70 - liquidLevel * 25))}a11 11 0 0 1-22 0Z`} fill={liquidColor} opacity=".58" stroke="none" />}
    {!isRound && !isTube && <path d="M28 62h44" stroke={liquidColor} opacity=".55" />}
    <path className="sandbox-glass-highlight" d="M27 18v54" stroke="white" strokeWidth="2" strokeLinecap="round" opacity=".35" />
    {liquidLevel >= 1 && <path d="M31 39q19 8 38 0" stroke="#F43F5E" strokeWidth="2" strokeDasharray="3 3" />}
    {operation === 'stirring' && <path className="sandbox-stir" d="M36 52q14 10 28 0" stroke="#fff" opacity=".8" />}
    {operation === 'heating' && <g className="sandbox-bubbles"><circle cx="42" cy="48" r="2" fill="#fff" stroke="none" /><circle cx="58" cy="58" r="2" fill="#fff" stroke="none" /><circle cx="50" cy="70" r="1.7" fill="#fff" stroke="none" /></g>}
  </svg>;
}
