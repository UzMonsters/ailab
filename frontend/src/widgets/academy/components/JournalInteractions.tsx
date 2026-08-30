'use client';

import { useEffect, useMemo, useState } from 'react';
import { EquipmentThumbnail } from '@/entities/equipment/ui/EquipmentRendererRegistry';

export function EquipmentMiniLab({ equipmentId, lang }: { equipmentId: string; lang: string }) {
  const [connected, setConnected] = useState(false);
  const [running, setRunning] = useState(false);
  const [temperature, setTemperature] = useState(23.4);
  useEffect(() => { if (!running || !connected) return; const timer = window.setInterval(() => setTemperature(value => Math.min(42, value + .7)), 650); return () => window.clearInterval(timer); }, [running, connected]);
  const text = lang === 'ru' ? { title: 'МИНИ-ЛАБОРАТОРИЯ', connect: 'Подключить', run: 'Запустить', stop: 'Стоп', reset: 'Сбросить', active: 'ТЕПЛОВАЯ СВЯЗЬ АКТИВНА', idle: 'связь не создана' } : lang === 'uz' ? { title: 'MINI-LABORATORIYA', connect: 'Ulash', run: 'Boshlash', stop: 'To‘xtatish', reset: 'Qayta tiklash', active: 'ISSIQLIK ULANISHI FAOL', idle: 'ulanish yaratilmagan' } : { title: 'MINI LAB', connect: 'Connect', run: 'Run', stop: 'Stop', reset: 'Reset', active: 'THERMAL CONNECTION ACTIVE', idle: 'connection not created' };
  return <div className="journal-mini-lab"><div className="journal-interaction-label">{text.title}</div><div className="mini-lab-stage"><div><EquipmentThumbnail type={equipmentId} size={62}/><span>{equipmentId}</span></div><div className={`mini-lab-connection ${connected ? 'active' : ''}`}><i/><small>{connected ? 'thermal' : '—'}</small></div><div><EquipmentThumbnail type="hotplate" size={62}/><span>hot plate</span></div></div><div className="mini-lab-readout"><span className={connected ? 'active' : ''}>{connected ? text.active : text.idle}</span><strong>{temperature.toFixed(1)}°C</strong></div><div className="journal-controls"><button onClick={() => setConnected(true)} disabled={connected}>{text.connect}</button><button onClick={() => setRunning(!running)} disabled={!connected}>{running ? text.stop : text.run}</button><button onClick={() => {setRunning(false);setConnected(false);setTemperature(23.4)}}>{text.reset}</button></div></div>;
}

export function SubstanceStateDemo({ materialId, formula, lang }: { materialId: string; formula: string; lang: string }) {
  const [energy, setEnergy] = useState(28);
  const solidMaterials = ['salt', 'copper_sulfate', 'potassium_permanganate', 'sodium_hydroxide', 'sodium_carbonate', 'zinc', 'copper', 'gold', 'sulfur'];
  const state = energy < 34 ? (solidMaterials.includes(materialId) ? 'solid' : 'liquid') : energy < 72 ? 'liquid' : 'gas';
  const label = lang === 'ru' ? { title:'МОДЕЛЬ СОСТОЯНИЯ', energy:'Условная энергия', note:'Визуальная модель, не лабораторная инструкция.', liquid:'жидкость', solid:'твёрдое', gas:'газ' } : lang === 'uz' ? { title:'HOLAT MODELI', energy:'Shartli energiya', note:'Vizual model, laboratoriya yo‘riqnomasi emas.', liquid:'suyuqlik', solid:'qattiq', gas:'gaz' } : { title:'STATE MODEL', energy:'Relative energy', note:'Visual model, not a laboratory instruction.', liquid:'liquid', solid:'solid', gas:'gas' };
  const stateLabel = label[state as 'liquid' | 'solid' | 'gas'];
  return <div className="substance-state-demo"><div className="journal-interaction-label">{label.title}</div><div className={`particle-vessel state-${state}`}>{Array.from({length:12},(_,i)=><i key={i} style={{'--i':i} as React.CSSProperties}/>) }<strong dangerouslySetInnerHTML={{__html:formula}}/></div><div className="substance-state-row"><span>{label.energy}</span><b>{energy}% · {stateLabel}</b></div><input aria-label={label.energy} type="range" min="10" max="95" value={energy} onChange={event=>setEnergy(Number(event.target.value))}/><small>{label.note}</small></div>;
}

export function SubstanceVisual({ materialId, formula, size = 112 }: { materialId:string; formula:string; size?:number }) {
  const liquid = ['water', 'ethanol', 'sulfuric_acid', 'hydrochloric_acid', 'hydrogen_peroxide', 'copper_sulfate_solution', 'potassium_permanganate_solution', 'ph_indicator'].includes(materialId);
  const color = materialId.includes('permanganate') ? '#d946ef' : materialId.includes('copper') ? '#3b82f6' : materialId === 'water' ? '#38bdf8' : materialId === 'sulfur' ? '#facc15' : liquid ? '#60a5fa' : '#a78bfa';
  const plainFormula = formula.replace(/<[^>]+>/g, '');
  return <div className={`substance-visual cartoon-chemistry substance-${materialId}`} style={{width:size,height:size}} aria-label={materialId}>
    <svg viewBox="0 0 160 160" width="100%" height="100%" role="img" aria-label={`${materialId} cartoon chemistry illustration`}>
      <defs>
        <radialGradient id={`cartoon-bg-${materialId}`}><stop stopColor={color} stopOpacity=".3"/><stop offset="1" stopColor={color} stopOpacity="0"/></radialGradient>
        <linearGradient id={`cartoon-liquid-${materialId}`} x1="0" y1="0" x2="1" y2="1"><stop stopColor={color} stopOpacity=".95"/><stop offset="1" stopColor={color} stopOpacity=".48"/></linearGradient>
        <filter id={`cartoon-shadow-${materialId}`}><feDropShadow dx="0" dy="4" stdDeviation="3" floodColor="#020617" floodOpacity=".45"/></filter>
      </defs>
      <circle cx="80" cy="80" r="76" fill={`url(#cartoon-bg-${materialId})`} />
      <path d="M61 22h38M69 22v35L39 116c-6 12 3 22 16 22h50c13 0 22-10 16-22L91 57V22" fill="#e0f2fe" fillOpacity=".2" stroke="#e2e8f0" strokeWidth="5" strokeLinejoin="round" filter={`url(#cartoon-shadow-${materialId})`} />
      {liquid ? <path d="M44 101c18-8 35 3 50-1 10-3 17-1 22 2l6 14c4 9-3 17-17 17H55c-12 0-19-8-14-18Z" fill={`url(#cartoon-liquid-${materialId})`} stroke="#f8fafc" strokeWidth="2" /> : <g fill={color} stroke="#f8fafc" strokeWidth="2" strokeLinejoin="round"><path d="m48 112 12-26 19-8 26 14-5 29-24 9Z"/><path d="m67 78 24 14 14-7 12 11-5 25-24 9-25-18Z" opacity=".65"/></g>}
      <path d="M73 27v33c0 4-8 13-13 22" fill="none" stroke="white" strokeWidth="5" strokeLinecap="round" opacity=".82" />
      {liquid && <><circle cx="111" cy="78" r="5" fill={color} opacity=".8"/><circle cx="119" cy="61" r="3" fill={color} opacity=".65"/><circle cx="48" cy="73" r="3.5" fill={color} opacity=".7"/></>}
      <rect x="44" y="119" width="72" height="22" rx="8" fill="#0f172a" fillOpacity=".72" />
      <text x="80" y="134" textAnchor="middle" fill="white" fontSize="13" fontFamily="monospace" fontWeight="700">{plainFormula}</text>
      <circle cx="126" cy="28" r="10" fill="#f8fafc" fillOpacity=".12" stroke="#f8fafc" strokeOpacity=".45" strokeWidth="2" />
      <path d="M122 28h8M126 24v8" stroke="#f8fafc" strokeWidth="2" strokeLinecap="round" />
    </svg>
  </div>;
}

const sandboxMaterialImages: Record<string, string> = {
  water: '/journal/substances/water.png',
  salt: '/journal/substances/sodium-chloride.png',
  copper_sulfate: '/journal/substances/copper-sulfate.png',
  copper_sulfate_solution: '/journal/substances/copper-sulfate.png',
  potassium_permanganate: '/journal/substances/potassium-permanganate.png',
  potassium_permanganate_solution: '/journal/substances/potassium-permanganate.png',
  sulfur: '/journal/substances/sulfur.png',
  zinc: '/material-icons/zinc.png',
  copper: '/material-icons/copper.png',
};

export function SandboxMaterialVisual({ materialId, formula, size = 112 }: { materialId: string; formula: string; size?: number }) {
  const image = sandboxMaterialImages[materialId];
  if (!image) return <SubstanceVisual materialId={materialId} formula={formula} size={size} />;
  return <div className="sandbox-material-visual" style={{ width: size, height: size }} aria-label={materialId}>
    <img src={image} alt="" draggable={false} />
    <strong dangerouslySetInnerHTML={{ __html: formula }} />
  </div>;
}

const periods = [
  ['H',...Array(16).fill(''),'He'],
  ['Li','Be',...Array(10).fill(''),'B','C','N','O','F','Ne'],
  ['Na','Mg',...Array(10).fill(''),'Al','Si','P','S','Cl','Ar'],
  ['K','Ca','Sc','Ti','V','Cr','Mn','Fe','Co','Ni','Cu','Zn','Ga','Ge','As','Se','Br','Kr'],
  ['Rb','Sr','Y','Zr','Nb','Mo','Tc','Ru','Rh','Pd','Ag','Cd','In','Sn','Sb','Te','I','Xe'],
  ['Cs','Ba','La*','Hf','Ta','W','Re','Os','Ir','Pt','Au','Hg','Tl','Pb','Bi','Po','At','Rn'],
  ['Fr','Ra','Ac*','Rf','Db','Sg','Bh','Hs','Mt','Ds','Rg','Cn','Nh','Fl','Mc','Lv','Ts','Og'],
];
const lanthanides = ['La','Ce','Pr','Nd','Pm','Sm','Eu','Gd','Tb','Dy','Ho','Er','Tm','Yb','Lu'];
const actinides = ['Ac','Th','Pa','U','Np','Pu','Am','Cm','Bk','Cf','Es','Fm','Md','No','Lr'];
const symbols = ['H','He','Li','Be','B','C','N','O','F','Ne','Na','Mg','Al','Si','P','S','Cl','Ar','K','Ca','Sc','Ti','V','Cr','Mn','Fe','Co','Ni','Cu','Zn','Ga','Ge','As','Se','Br','Kr','Rb','Sr','Y','Zr','Nb','Mo','Tc','Ru','Rh','Pd','Ag','Cd','In','Sn','Sb','Te','I','Xe','Cs','Ba','La','Ce','Pr','Nd','Pm','Sm','Eu','Gd','Tb','Dy','Ho','Er','Tm','Yb','Lu','Hf','Ta','W','Re','Os','Ir','Pt','Au','Hg','Tl','Pb','Bi','Po','At','Rn','Fr','Ra','Ac','Th','Pa','U','Np','Pu','Am','Cm','Bk','Cf','Es','Fm','Md','No','Lr','Rf','Db','Sg','Bh','Hs','Mt','Ds','Rg','Cn','Nh','Fl','Mc','Lv','Ts','Og'];
const special: Record<string,{name:string,mass:string,state:string,group:string,period:string,description:string}> = {
  H:{name:'Hydrogen',mass:'1.008',state:'Gas',group:'1',period:'1',description:'The lightest element and the most abundant element in the universe.'},
  O:{name:'Oxygen',mass:'15.999',state:'Gas',group:'16',period:'2',description:'A reactive nonmetal central to respiration and oxidation.'},
  Na:{name:'Sodium',mass:'22.990',state:'Solid',group:'1',period:'3',description:'A soft alkali metal commonly encountered through its compounds.'},
  Cl:{name:'Chlorine',mass:'35.45',state:'Gas',group:'17',period:'3',description:'A halogen that forms chloride compounds with many elements.'},
  Fe:{name:'Iron',mass:'55.845',state:'Solid',group:'8',period:'4',description:'A transition metal essential to alloys, biology and industry.'},
};
function category(symbol:string){ if(['He','Ne','Ar','Kr','Xe','Rn','Og'].includes(symbol)) return 'noble'; if(['F','Cl','Br','I','At','Ts'].includes(symbol)) return 'halogen'; if(['Li','Na','K','Rb','Cs','Fr'].includes(symbol)) return 'alkali'; if(lanthanides.includes(symbol)) return 'lanthanide'; if(actinides.includes(symbol)) return 'actinide'; if(['H','C','N','O','P','S','Se'].includes(symbol)) return 'nonmetal'; return 'metal'; }

export function PeriodicTableFoldout({ lang }: { lang:string }) {
  const [open,setOpen]=useState(false); const [query,setQuery]=useState(''); const [selected,setSelected]=useState('H');
  const labels=lang==='ru'?{open:'Открыть периодическую таблицу',title:'Карта элементов',search:'поиск элемента...',close:'Свернуть лист',number:'Атомный номер',mass:'Атомная масса',state:'Состояние',group:'Группа',period:'Период'}:lang==='uz'?{open:'Davriy jadvalni ochish',title:'Elementlar xaritasi',search:'element qidirish...',close:'Varaqni yopish',number:'Atom raqami',mass:'Atom massasi',state:'Holati',group:'Guruh',period:'Davr'}:{open:'Open periodic table',title:'Map of elements',search:'search element...',close:'Fold sheet',number:'Atomic number',mass:'Atomic mass',state:'State',group:'Group',period:'Period'};
  const selectedData=special[selected]??{name:selected,mass:'—',state:'Solid',group:'—',period:'—',description:'Element record in the modern periodic system.'};
  const localizedElement = selected === 'H' ? (lang === 'ru' ? {name:'Водород',description:'Самый лёгкий и самый распространённый химический элемент во Вселенной.'} : lang === 'uz' ? {name:'Vodorod',description:'Koinotdagi eng yengil va eng ko‘p tarqalgan kimyoviy element.'} : selectedData) : selectedData;
  const matches=(symbol:string)=>!query||symbol.toLowerCase().includes(query.toLowerCase())||(special[symbol]?.name??'').toLowerCase().includes(query.toLowerCase());
  return <div className="periodic-foldout-wrap"><button className="periodic-open codex-mono" onClick={()=>setOpen(true)}>{labels.open} →</button>{open&&<div className="periodic-foldout"><div className="periodic-foldout-head"><div><span className="journal-interaction-label">PERIODIC TABLE</span><h3 className="codex-cinzel">{labels.title}</h3></div><div><input value={query} onChange={e=>setQuery(e.target.value)} placeholder={labels.search}/><button onClick={()=>setOpen(false)}>×</button></div></div><div className="periodic-body"><div className="periodic-grid">{periods.flatMap((row,r)=>row.map((symbol,c)=>symbol?<button key={`${r}-${c}`} className={`element-cell ${category(symbol.replace('*',''))} ${matches(symbol)?'':'dim'}`} onMouseEnter={()=>setSelected(symbol.replace('*',''))} onClick={()=>setSelected(symbol.replace('*',''))}><small>{symbols.indexOf(symbol.replace('*',''))+1}</small><strong>{symbol}</strong></button>:<i key={`${r}-${c}`}/>))}<div className="element-series">{[lanthanides,actinides].flatMap((row,r)=>row.map(symbol=><button key={symbol} className={`element-cell ${r?'actinide':'lanthanide'} ${matches(symbol)?'':'dim'}`} onMouseEnter={()=>setSelected(symbol)} onClick={()=>setSelected(symbol)}><small>{symbols.indexOf(symbol)+1}</small><strong>{symbol}</strong></button>))}</div></div><aside className="element-insert"><span>{labels.number} · {symbols.indexOf(selected)+1}</span><strong>{selected}</strong><h4>{localizedElement.name}</h4><p>{localizedElement.description}</p><dl><div><dt>{labels.mass}</dt><dd>{selectedData.mass}</dd></div><div><dt>{labels.group}</dt><dd>{selectedData.group}</dd></div><div><dt>{labels.period}</dt><dd>{selectedData.period}</dd></div><div><dt>{labels.state}</dt><dd>{selectedData.state}</dd></div></dl><button onClick={()=>setOpen(false)}>{labels.close}</button></aside></div></div>}</div>;
}

export function BoyleVolumeLab({lang}:{lang:string}){const [volume,setVolume]=useState(50);const pressure=useMemo(()=>Math.round(5000/volume)/10,[volume]);return <div className="boyle-lab"><div className="journal-interaction-label">P · V = const</div><div className="boyle-values"><span>V <b>{volume} ml</b></span><span>P <b>{pressure} kPa</b></span></div><input type="range" min="20" max="90" value={volume} onChange={e=>setVolume(Number(e.target.value))}/><div className="boyle-graph">{[20,30,40,50,60,70,80,90].map(v=><i key={v} style={{height:`${Math.round(1800/v)}%`}}/> )}</div><small>{lang==='ru'?'Измените объём и наблюдайте обратное изменение давления.':lang==='uz'?'Hajmni o‘zgartiring va bosimning teskari o‘zgarishini kuzating.':'Change volume and observe the inverse change in pressure.'}</small></div>}

export function MendeleevCompare({lang}:{lang:string}){const [modern,setModern]=useState(false);const labels=lang==='ru'?['1869','Современная']:lang==='uz'?['1869','Zamonaviy']:['1869','Modern'];return <div className="mendeleev-compare"><div className="comparison-tabs"><button className={!modern?'active':''} onClick={()=>setModern(false)}>{labels[0]}</button><button className={modern?'active':''} onClick={()=>setModern(true)}>{labels[1]}</button></div><div className={`historical-grid ${modern?'modern':''}`}>{(modern?['H','He','Li','Be','B','C','N','O','F','Ne','Na','Mg','Al','Si','P','S','Cl','Ar']:['H','Li','Be','B','C','N','O','Na','Mg','Al','Si','P','S','?','?']).map((s,i)=><span key={`${s}-${i}`} className={s==='?'?'unknown':''}>{s}</span>)}</div><small>{modern?(lang==='ru'?'Неизвестные Менделееву элементы отмечены новым положением в системе.':'Elements unknown in 1869 now occupy predicted and newly discovered positions.'):(lang==='ru'?'Ранний порядок и намеренно оставленные пустые места.':'Early ordering and deliberately empty positions.')}</small></div>}
