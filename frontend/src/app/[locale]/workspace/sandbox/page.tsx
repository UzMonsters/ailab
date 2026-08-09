'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useRef, useState, useCallback } from 'react';
import {
  Beaker, FlaskConical, Flame, Snowflake,
  Thermometer, Scale, Droplets, Filter,
  ArrowLeft, Settings, Play, Save, X, Plus,
  Gauge, Pipette, TestTubes, Atom, Trash2, AlertTriangle,
  Search, Undo2, Redo2, ZoomIn, ZoomOut, Maximize2, Grid3X3, Radio,
  Circle, Square, Move, RefreshCw, ArrowUp, ArrowDown, Activity, List,
} from 'lucide-react';
import { useTranslations } from 'next-intl';

type CanvasItemType = 'equipment' | 'material';

interface CanvasItem {
  id: string;
  type: string;
  name: string;
  kind: CanvasItemType;
  formula?: string;
  color?: string;
  x: number;
  y: number;
  w: number;
  h: number;
}

interface Connection {
  id: string;
  from: string;
  to: string;
  kind: string;
}

const equipmentGroups = [
  {
    key: 'groupContainers',
    title: 'Containers',
    items: [
      { id: 'beaker', name: 'Beaker', icon: Beaker, w: 88, h: 72, color: '#8B5CF6' },
      { id: 'erlenmeyer', name: 'Erlenmeyer Flask', icon: FlaskConical, w: 84, h: 88, color: '#8B5CF6' },
      { id: 'roundflask', name: 'Round-bottom Flask', icon: FlaskConical, w: 96, h: 84, color: '#A78BFA' },
      { id: 'testtube', name: 'Test Tube', icon: TestTubes, w: 56, h: 96, color: '#22D3EE' },
      { id: 'vessel', name: 'Reaction Vessel', icon: Beaker, w: 104, h: 80, color: '#8B5CF6' },
    ],
  },
  {
    key: 'groupHeating',
    title: 'Heating',
    items: [
      { id: 'burner', name: 'Bunsen Burner', icon: Flame, w: 68, h: 96, color: '#F59E0B' },
      { id: 'hotplate', name: 'Hot Plate', icon: Flame, w: 96, h: 64, color: '#F59E0B' },
    ],
  },
  {
    key: 'groupTransfer',
    title: 'Transfer',
    items: [
      { id: 'pipette', name: 'Pipette', icon: Pipette, w: 64, h: 96, color: '#34D399' },
      { id: 'dropper', name: 'Dropper', icon: Droplets, w: 56, h: 88, color: '#22D3EE' },
      { id: 'funnel', name: 'Funnel', icon: Filter, w: 76, h: 72, color: '#34D399' },
      { id: 'tubing', name: 'Tube', icon: ArrowUp, w: 48, h: 120, color: '#22D3EE' },
    ],
  },
  {
    key: 'groupMeasurement',
    title: 'Measurement',
    items: [
      { id: 'thermometer', name: 'Thermometer', icon: Thermometer, w: 40, h: 110, color: '#F43F5E' },
      { id: 'gauge', name: 'Pressure Gauge', icon: Gauge, w: 76, h: 76, color: '#F59E0B' },
      { id: 'phmeter', name: 'pH Sensor', icon: Scale, w: 72, h: 84, color: '#22D3EE' },
      { id: 'balance', name: 'Balance', icon: Scale, w: 96, h: 64, color: '#8B5CF6' },
    ],
  },
  {
    key: 'groupAdvanced',
    title: 'Advanced',
    items: [
      { id: 'condenser', name: 'Condenser', icon: Snowflake, w: 72, h: 120, color: '#22D3EE' },
      { id: 'gascylinder', name: 'Gas Cylinder', icon: Radio, w: 64, h: 112, color: '#F59E0B' },
      { id: 'electrodes', name: 'Electrodes', icon: Atom, w: 84, h: 88, color: '#34D399' },
    ],
  },
];

const materialsList = [
  { id: 'water', formula: 'H₂O', name: 'Water', category: 'Elements', color: '#22D3EE' },
  { id: 'hcl', formula: 'HCl', name: 'Hydrochloric Acid', category: 'Solutions', color: '#F59E0B' },
  { id: 'naoh', formula: 'NaOH', name: 'Sodium Hydroxide', category: 'Solutions', color: '#A78BFA' },
  { id: 'cuso4', formula: 'CuSO₄', name: 'Copper Sulfate', category: 'Solutions', color: '#34D399' },
  { id: 'o2', formula: 'O₂', name: 'Oxygen', category: 'Gases', color: '#22D3EE' },
  { id: 'h2', formula: 'H₂', name: 'Hydrogen', category: 'Gases', color: '#8B5CF6' },
  { id: 'al', formula: 'Al', name: 'Aluminium', category: 'Metals', color: '#F59E0B' },
  { id: 'nacl', formula: 'NaCl', name: 'Sodium Chloride', category: 'Compounds', color: '#A78BFA' },
  { id: 'caco3', formula: 'CaCO₃', name: 'Calcium Carbonate', category: 'Compounds', color: '#34D399' },
];

const materialFilters = ['All', 'Elements', 'Compounds', 'Solutions', 'Gases', 'Metals'];

const bottomTabs = [
  { key: 'results', labelKey: 'results', icon: Activity },
  { key: 'log', labelKey: 'logTab', icon: List },
  { key: 'safety', labelKey: 'safetyTab', icon: AlertTriangle },
  { key: 'measurements', labelKey: 'measurementsTab', icon: Gauge },
] as const;

export default function SandboxPage() {
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const t = useTranslations('sandbox');
  const [activePanel, setActivePanel] = useState<'equipment' | 'materials'>('equipment');
  const [materialFilter, setMaterialFilter] = useState('All');
  const [materialSearch, setMaterialSearch] = useState('');
  const [selectedItem, setSelectedItem] = useState<string | null>(null);
  const [canvasItems, setCanvasItems] = useState<CanvasItem[]>([]);
  const [connections, setConnections] = useState<Connection[]>([]);
  const [draggingIdx, setDraggingIdx] = useState<number | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [workspaceName, setWorkspaceName] = useState(t('untitled'));
  const [safetyWarning, setSafetyWarning] = useState(false);
  const [bottomTab, setBottomTab] = useState<'results' | 'log' | 'safety' | 'measurements'>('log');
  const [bottomOpen, setBottomOpen] = useState(false);
  const [zoom, setZoom] = useState(1);
  const [showGrid, setShowGrid] = useState(true);
  const [connectionMode, setConnectionMode] = useState(false);
  const [pendingConnection, setPendingConnection] = useState<string | null>(null);
  const [previewPoint, setPreviewPoint] = useState<{ x: number; y: number } | null>(null);

  // Mobile bottom sheet
  const [mobileSheet, setMobileSheet] = useState<'equipment' | 'materials' | 'properties' | 'results' | null>(null);

  const canvasRef = useRef<HTMLDivElement>(null);
  const spawnOffset = useRef(0);

  const showToast = useCallback((msg: string) => {
    setToast(msg);
    setTimeout(() => setToast(null), 2000);
  }, []);

  const spawnCounter = useRef(0);

  const addToCanvas = useCallback((item: { id: string; name: string; w: number; h: number; color?: string; formula?: string }, kind: CanvasItemType) => {
    const canvas = canvasRef.current;
    const rect = canvas?.getBoundingClientRect();
    const cw = rect?.width ?? 800;
    const ch = rect?.height ?? 500;

    const nth = spawnCounter.current++;
    const cx = cw / 2 - item.w / 2;
    const cy = ch / 2 - item.h / 2;
    const offsetX = (nth % 3) * 90;
    const offsetY = Math.floor(nth / 3) * 70;

    const canvasItem: CanvasItem = {
      id: `${item.id}_${Date.now()}_${nth}`,
      type: item.id,
      name: item.name,
      kind,
      formula: item.formula,
      color: item.color,
      w: item.w,
      h: item.h,
      x: Math.max(10, Math.min(cx + offsetX, cw - item.w - 10)),
      y: Math.max(10, Math.min(cy + offsetY, ch - item.h - 10)),
    };
    setCanvasItems(prev => [...prev, canvasItem]);
    setSelectedItem(canvasItem.id);
    if (kind === 'equipment') showToast(t('addedToCanvas', { name: item.name }));
    else showToast(t('addedToContainer', { name: item.name }));
  }, [showToast, t]);

  const handleMouseDown = (idx: number) => (e: React.MouseEvent) => {
    e.stopPropagation();
    e.preventDefault();
    setDraggingIdx(idx);
    setSelectedItem(canvasItems[idx].id);

    const rect = (e.currentTarget as HTMLElement).parentElement!.getBoundingClientRect();
    const startX = e.clientX - canvasItems[idx].x;
    const startY = e.clientY - canvasItems[idx].y;

    const handleMove = (me: MouseEvent) => {
      const newX = me.clientX - startX;
      const newY = me.clientY - startY;
      setCanvasItems(prev => prev.map((item, i) =>
        i === idx ? { ...item, x: Math.max(0, Math.min(newX, rect.width - item.w)), y: Math.max(0, Math.min(newY, rect.height - item.h)) } : item
      ));
      setPreviewPoint(null);
    };

    const handleUp = () => {
      setDraggingIdx(null);
      window.removeEventListener('mousemove', handleMove);
      window.removeEventListener('mouseup', handleUp);
    };

    window.addEventListener('mousemove', handleMove);
    window.addEventListener('mouseup', handleUp);
  };

  const removeItem = (id: string) => {
    setCanvasItems(prev => prev.filter(item => item.id !== id));
    setConnections(prev => prev.filter(c => c.from !== id && c.to !== id));
    if (selectedItem === id) setSelectedItem(null);
  };

  const selected = canvasItems.find(item => item.id === selectedItem);
  const equipmentIcon = (type: string) => equipmentGroups.flatMap(g => g.items).find(e => e.id === type);

  const handleRun = () => {
    setSafetyWarning(true);
    setTimeout(() => setSafetyWarning(false), 3000);
  };

  const toggleConnectionMode = () => {
    setConnectionMode(v => !v);
    setPendingConnection(null);
    if (!connectionMode) showToast(t('connectionModeToast'));
  };

  const handleCanvasClick = (e: React.MouseEvent) => {
    if (connectionMode) {
      setPendingConnection(null);
      setPreviewPoint(null);
      return;
    }
    setSelectedItem(null);
  };

  const handleItemClick = (item: CanvasItem, e: React.MouseEvent) => {
    e.stopPropagation();
    if (connectionMode) {
      if (!pendingConnection) {
        setPendingConnection(item.id);
        showToast(t('selectTargetToast'));
      } else if (pendingConnection === item.id) {
        setPendingConnection(null);
      } else {
        const conn: Connection = {
          id: `conn_${Date.now()}`,
          from: pendingConnection,
          to: item.id,
          kind: 'GLASS_JOINT',
        };
        setConnections(prev => [...prev, conn]);
        setPendingConnection(null);
        setConnectionMode(false);
        showToast(t('connectionCreated'));
      }
      return;
    }
    setSelectedItem(item.id);
  };

  const filteredMaterials = materialsList.filter(m =>
    (materialFilter === 'All' || m.category === materialFilter) &&
    (m.name.toLowerCase().includes(materialSearch.toLowerCase()) || m.formula.toLowerCase().includes(materialSearch.toLowerCase()))
  );

  const canvasW = canvasRef.current?.getBoundingClientRect().width ?? 800;
  const canvasH = canvasRef.current?.getBoundingClientRect().height ?? 500;

  const renderEquipmentList = () => (
    <div className="space-y-4">
      {equipmentGroups.map((group) => (
        <div key={group.key}>
          <div className="text-[9px] font-mono uppercase tracking-wider text-[var(--muted-foreground)]/60 px-1 mb-1.5">{t(group.key)}</div>
          <div className="space-y-0.5">
            {group.items.map((eq) => (
              <button
                key={eq.id}
                onClick={() => addToCanvas({ id: eq.id, name: t(`eq_${eq.id}`), w: eq.w, h: eq.h, color: eq.color }, 'equipment')}
                className="w-full flex items-center gap-2.5 px-2.5 py-2 rounded-lg text-sm transition-all text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-[#8b5cf6]/10 hover:border-[#8b5cf6]/30 border border-transparent text-left"
              >
                <eq.icon size={15} style={{ color: eq.color }} />
                <span className="text-xs flex-1 min-w-0 truncate">{t(`eq_${eq.id}`)}</span>
                <Plus size={14} className="text-[var(--muted-foreground)]/40 group-hover:text-[#8b5cf6] shrink-0" />
              </button>
            ))}
          </div>
        </div>
      ))}
    </div>
  );

  const renderMaterialsList = () => (
    <div className="space-y-2">
      <div className="relative">
        <input
          type="text"
          placeholder={t('searchMaterials')}
          value={materialSearch}
          onChange={(e) => setMaterialSearch(e.target.value)}
          aria-label={t('searchMaterials')}
          className="w-full bg-[var(--input)] border border-white/10 rounded-lg pl-8 py-2 text-xs text-[var(--foreground)] outline-none focus:border-[#8B5CF6] transition-all"
        />
        <Search size={13} className="absolute left-2.5 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" />
      </div>
      <div className="flex flex-wrap gap-1">
        {materialFilters.map((f) => (
          <button
            key={f}
            onClick={() => setMaterialFilter(f)}
            className={`px-2.5 py-1 rounded-full text-[10px] font-medium transition-all ${materialFilter === f ? 'bg-[#8b5cf6]/20 text-[#C084FC] border border-[#8b5cf6]/40' : 'text-[var(--muted-foreground)] border border-white/10 hover:border-white/25'}`}
          >
            {f === 'All' ? t('filterAll') : t(`cat_${f.toLowerCase()}`)}
          </button>
        ))}
      </div>
      <div className="space-y-0.5">
        {filteredMaterials.map((mat) => (
          <button
            key={mat.id}
            onClick={() => addToCanvas({ id: mat.id, name: t(`mat_${mat.id}`), w: 72, h: 60, color: mat.color, formula: mat.formula }, 'material')}
            className="w-full flex items-center gap-3 px-2.5 py-2 rounded-lg transition-all text-left hover:bg-white/[0.04] border border-transparent hover:border-white/10"
          >
            <div className="w-7 h-7 rounded-md flex items-center justify-center flex-shrink-0 text-[10px] font-mono font-bold" style={{ background: `${mat.color}1a`, color: mat.color, border: `1px solid ${mat.color}40` }}>
              {mat.formula.slice(0, 2)}
            </div>
            <div className="min-w-0">
              <div className="text-xs font-medium text-[var(--foreground)] truncate">{t(`mat_${mat.id}`)}</div>
              <div className="text-[10px] font-mono text-[var(--muted-foreground)]/70">{mat.formula} · {t(`cat_${mat.category.toLowerCase()}`)}</div>
            </div>
            <Plus size={14} className="ml-auto text-[var(--muted-foreground)]/40 shrink-0" />
          </button>
        ))}
      </div>
    </div>
  );

  const renderProperties = () => {
    if (!selected) {
      return (
        <div className="text-center py-10">
          <Settings size={28} className="mx-auto mb-3 text-[var(--muted-foreground)]/20" />
          <p className="text-xs text-[var(--muted-foreground)]">{t('selectProperties')}</p>
        </div>
      );
    }

    const isVessel = selected.kind === 'equipment' && ['beaker', 'erlenmeyer', 'roundflask', 'testtube', 'vessel'].includes(selected.type);
    const isHeater = selected.kind === 'equipment' && ['burner', 'hotplate'].includes(selected.type);

    return (
      <div className="space-y-4">
        <div>
          <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('name')}</label>
          <div className="text-sm font-medium text-[var(--foreground)]">{selected.name}</div>
        </div>
        <div>
          <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('type')}</label>
          <div className="text-sm text-[var(--foreground)] font-mono capitalize">{selected.type}</div>
        </div>

        {isVessel && (
          <>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('capacity')}</label>
              <div className="text-sm text-[var(--foreground)] font-mono">250 mL</div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('contents')}</label>
              <div className="space-y-1 text-xs font-mono">
                <div className="flex justify-between text-[#22D3EE]"><span>H₂O</span><span>100 mL</span></div>
                <div className="flex justify-between text-[#A78BFA]"><span>NaOH</span><span>0.1 mol/L</span></div>
              </div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('temperature')}</label>
              <div className="text-sm text-[#F59E0B] font-mono">25°C</div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('pressure')}</label>
              <div className="text-sm text-[#22D3EE] font-mono">1.0 atm</div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('phLabel')}</label>
              <div className="text-sm text-[#34D399] font-mono">7.0</div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('actions')}</label>
              <div className="grid grid-cols-2 gap-1.5">
                <button className="py-1.5 px-2 rounded-md text-[11px] bg-[#F59E0B]/10 border border-[#F59E0B]/25 text-[#F59E0B] hover:bg-[#F59E0B]/20 transition-all">{t('heat')}</button>
                <button className="py-1.5 px-2 rounded-md text-[11px] bg-[#22D3EE]/10 border border-[#22D3EE]/25 text-[#22D3EE] hover:bg-[#22D3EE]/20 transition-all">{t('cool')}</button>
                <button className="py-1.5 px-2 rounded-md text-[11px] bg-[#8b5cf6]/10 border border-[#8b5cf6]/25 text-[#C084FC] hover:bg-[#8b5cf6]/20 transition-all">{t('stir')}</button>
                <button className="py-1.5 px-2 rounded-md text-[11px] bg-white/5 border border-white/10 text-[var(--muted-foreground)] hover:bg-white/10 transition-all">{t('empty')}</button>
                <button className="py-1.5 px-2 rounded-md text-[11px] bg-white/5 border border-white/10 text-[var(--muted-foreground)] hover:bg-white/10 transition-all">{t('duplicate')}</button>
                <button onClick={() => removeItem(selected.id)} className="py-1.5 px-2 rounded-md text-[11px] bg-[#F43F5E]/10 border border-[#F43F5E]/25 text-[#F43F5E] hover:bg-[#F43F5E]/20 transition-all">{t('remove')}</button>
              </div>
            </div>
          </>
        )}

        {isHeater && (
          <>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('power')}</label>
              <div className="text-sm text-[#F59E0B] font-mono">1200 W</div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('targetTemperature')}</label>
              <div className="text-sm text-[#F59E0B] font-mono">180°C</div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('state')}</label>
              <div className="inline-flex items-center gap-1.5 text-sm text-[#34D399] font-mono"><span className="w-2 h-2 rounded-full bg-[#34D399] shadow-[0_0_8px_#34D399]" />{t('off')}</div>
            </div>
            <button className="w-full py-2 bg-gradient-to-br from-[#F59E0B] to-[#F97316] text-white rounded-[var(--radius-sm)] text-xs font-semibold hover:-translate-y-0.5 transition-all">{t('turnOn')}</button>
          </>
        )}

        {selected.kind === 'material' && (
          <>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('formula')}</label>
              <div className="text-sm font-mono" style={{ color: selected.color }}>{selected.formula}</div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('state')}</label>
              <div className="text-sm text-[var(--foreground)] font-mono">{t('liquid')}</div>
            </div>
            <div>
              <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('amount')}</label>
              <div className="text-sm text-[var(--foreground)] font-mono">100 mL</div>
            </div>
          </>
        )}

        <div className="pt-2 border-t border-white/5">
          <label className="text-[10px] text-[var(--muted-foreground)] uppercase tracking-wider block mb-1">{t('position')}</label>
          <div className="text-xs text-[var(--muted-foreground)] font-mono">X: {Math.round(selected.x)}, Y: {Math.round(selected.y)}</div>
        </div>

        <button
          onClick={() => removeItem(selected.id)}
          className="w-full py-2 border border-[#F43F5E]/30 text-[#F43F5E] rounded-[var(--radius-sm)] text-xs font-medium hover:bg-[#F43F5E]/10 transition-all flex items-center justify-center gap-1.5"
        >
          <Trash2 size={12} />{t('remove')}
        </button>
      </div>
    );
  };

  const renderCanvasItem = (item: CanvasItem, idx: number) => {
    const eq = equipmentIcon(item.type);
    const Icon = eq?.icon || Beaker;
    const isSelected = selectedItem === item.id;
    const isDragging = draggingIdx === idx;
    const isPending = pendingConnection === item.id;

    return (
      <div
        key={item.id}
        className={`absolute flex flex-col items-center gap-1 cursor-move select-none transition-shadow duration-200 ${isDragging ? 'z-30 shadow-[0_15px_40px_rgba(0,0,0,.5)]' : 'z-10'}`}
        style={{ left: item.x, top: item.y, width: item.w }}
        onMouseDown={handleMouseDown(idx)}
        onClick={(e) => handleItemClick(item, e)}
        role="button"
        aria-label={t('objectAria', { name: item.name })}
      >
        <div
          className={`relative flex flex-col items-center justify-end transition-all duration-200 rounded-xl border overflow-hidden ${isSelected ? 'ring-2 ring-[#8B5CF6]/70 shadow-[0_0_25px_rgba(139,92,246,0.45)] border-[#8B5CF6]/70' : isPending ? 'ring-2 ring-[#34D399]/70 shadow-[0_0_20px_rgba(52,211,153,0.4)]' : 'border-white/10 hover:border-white/25 bg-[#0F101A]/90'}`}
          style={{ width: item.w, height: item.h, background: item.kind === 'material' ? `linear-gradient(160deg, ${item.color}18, ${item.color}05)` : 'linear-gradient(160deg, #14162a, #0c0e1a)' }}
        >
          {/* liquid fill for vessels */}
          {item.kind === 'material' && (
            <div className="absolute bottom-0 left-2 right-2 h-2/5 rounded-b-lg opacity-60" style={{ background: item.color }} />
          )}
          <div className="relative z-[1] mb-auto mt-2 flex items-center justify-center w-full">
            <Icon size={item.w * 0.42} style={{ color: item.color || '#8B5CF6' }} strokeWidth={1.4} />
          </div>
          {item.kind === 'material' && (
            <div className="relative z-[1] pb-1 text-[9px] font-mono font-bold" style={{ color: item.color }}>{item.formula}</div>
          )}
          {isSelected && (
            <button
              onClick={(e) => { e.stopPropagation(); removeItem(item.id); }}
              aria-label={t('removeAria', { name: item.name })}
              className="absolute -top-2 -right-2 w-5 h-5 bg-[#F43F5E] rounded-full flex items-center justify-center text-white z-20"
            >
              <X size={10} />
            </button>
          )}
        </div>
        {isSelected && <span className="text-[9px] text-[var(--muted-foreground)] max-w-[90px] truncate text-center">{item.name}</span>}
      </div>
    );
  };

  const renderBottomPanel = () => (
    <div className="h-[150px] bg-[#0A0B14]/95 border-t border-white/5 flex flex-col shrink-0">
      <div className="flex items-center gap-1 px-2 border-b border-white/5 overflow-x-auto">
        {bottomTabs.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setBottomTab(tab.key)}
            className={`flex items-center gap-1.5 px-3 py-2.5 text-[10px] font-semibold transition-all whitespace-nowrap ${bottomTab === tab.key ? 'text-[#C084FC] border-b-2 border-[#8B5CF6]' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)]'}`}
          >
            <tab.icon size={12} />{t(tab.labelKey)}
          </button>
        ))}
        <button onClick={() => setBottomOpen(false)} className="ml-auto px-2 text-[var(--muted-foreground)] hover:text-[var(--foreground)]" aria-label={t('closePanel')}>
          <ArrowDown size={14} />
        </button>
      </div>
      <div className="flex-1 overflow-y-auto p-3 text-xs text-[var(--muted-foreground)]">
        {bottomTab === 'log' && (
          <div className="space-y-1.5 font-mono text-[11px]">
            <div><span className="text-[#34D399]">12:02</span> {t('logEntryMaterialAdded')}</div>
            <div><span className="text-[#34D399]">12:03</span> {t('logEntryFlaskHeated')}</div>
            <div><span className="text-[#34D399]">12:04</span> {t('logEntryReactionStarted')}</div>
            <div className="text-[var(--muted-foreground)]/50">{t('awaitingBackend')}</div>
          </div>
        )}
        {bottomTab === 'results' && (
          <div className="space-y-1.5 font-mono text-[11px]">
            <div className="flex justify-between max-w-[280px]"><span>{t('phLabel')}</span><span className="text-[#34D399]">7.0</span></div>
            <div className="flex justify-between max-w-[280px]"><span>{t('temperature')}</span><span className="text-[#F59E0B]">25°C</span></div>
            <div className="flex justify-between max-w-[280px]"><span>{t('reactionState')}</span><span className="text-[#22D3EE]">{t('idle')}</span></div>
          </div>
        )}
        {bottomTab === 'safety' && (
          <div className="space-y-1.5">
            <div className="flex items-center gap-2 text-[#34D399]"><span className="w-2 h-2 rounded-full bg-[#34D399]" />{t('safeNoViolations')}</div>
            <div className="text-[var(--muted-foreground)]/50 text-[11px]">{t('backendSafetyRequired')}</div>
          </div>
        )}
        {bottomTab === 'measurements' && (
          <div className="space-y-1.5 font-mono text-[11px]">
            <div className="flex justify-between max-w-[280px]"><span>{t('temperature')}</span><span>25°C</span></div>
            <div className="flex justify-between max-w-[280px]"><span>{t('pressure')}</span><span>1.0 atm</span></div>
            <div className="flex justify-between max-w-[280px]"><span>{t('volume')}</span><span>—</span></div>
          </div>
        )}
      </div>
    </div>
  );

  const renderMobileSheet = () => {
    if (!mobileSheet) return null;
    const title = mobileSheet === 'equipment' ? t('equipment') : mobileSheet === 'materials' ? t('materials') : mobileSheet === 'properties' ? t('properties') : t('results');
    return (
      <div className="fixed inset-0 z-[90] flex flex-col justify-end lg:hidden" onClick={() => setMobileSheet(null)}>
        <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" />
        <div className="relative bg-[#0A0B14] border-t border-white/10 rounded-t-[20px] h-[60vh] flex flex-col" onClick={(e) => e.stopPropagation()}>
          <div className="w-10 h-1 bg-white/20 rounded-full mx-auto mt-2.5 flex-shrink-0" />
          <div className="flex items-center justify-between px-4 py-3 border-b border-white/5 flex-shrink-0">
            <div className="flex gap-1">
              <button onClick={() => setMobileSheet('equipment')} className={`px-3 py-1.5 text-xs font-semibold rounded-full transition-all ${mobileSheet === 'equipment' ? 'bg-[#8b5cf6]/20 text-[#C084FC]' : 'text-[var(--muted-foreground)]'}`}>{t('equipment')}</button>
              <button onClick={() => setMobileSheet('materials')} className={`px-3 py-1.5 text-xs font-semibold rounded-full transition-all ${mobileSheet === 'materials' ? 'bg-[#14F195]/15 text-[#14F195]' : 'text-[var(--muted-foreground)]'}`}>{t('materials')}</button>
            </div>
            <button onClick={() => setMobileSheet(null)} aria-label={t('closeSheet')} className="w-8 h-8 grid place-items-center text-[var(--muted-foreground)]"><X size={16} /></button>
          </div>
          <div className="flex-1 overflow-y-auto p-3">
            {mobileSheet === 'equipment' && renderEquipmentList()}
            {mobileSheet === 'materials' && renderMaterialsList()}
            {mobileSheet === 'properties' && renderProperties()}
            {mobileSheet === 'results' && (
              <div className="space-y-3 text-xs text-[var(--muted-foreground)]">
                <div className="font-mono text-[11px]"><span className="text-[#34D399]">12:02</span> {t('logEntryMaterialAdded')}</div>
                <div className="font-mono text-[11px]"><span className="text-[#34D399]">12:03</span> {t('logEntryFlaskHeated')}</div>
                <div className="font-mono text-[11px]"><span className="text-[#34D399]">12:04</span> {t('logEntryReactionStarted')}</div>
              </div>
            )}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="relative h-screen flex flex-col overflow-hidden" style={{ backgroundColor: '#050508' }}>
      {/* Toolbar */}
      <header className="px-4 py-2 bg-[#050508]/90 backdrop-blur-xl border-b border-white/5 flex items-center justify-between gap-2 shrink-0">
        <div className="flex items-center gap-2 min-w-0">
          <Link href={`/${locale}/dashboard`} className="text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors no-underline flex items-center gap-1" aria-label={t('backToDashboard')}>
            <ArrowLeft size={16} />
          </Link>
          <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white flex-shrink-0">
            <FlaskConical size={14} />
          </div>
          <input
            type="text"
            value={workspaceName}
            onChange={(e) => setWorkspaceName(e.target.value)}
            aria-label={t('experimentName')}
            className="bg-transparent text-sm font-medium outline-none border-b border-transparent hover:border-white/10 focus:border-[#8b5cf6] transition-colors text-[var(--foreground)] max-w-[110px] sm:max-w-[220px] truncate"
          />
        </div>
        <div className="flex items-center gap-1.5 sm:gap-2">
          <div className="hidden md:flex items-center gap-1">
            <button onClick={() => showToast(t('undo'))} aria-label={t('undo')} className="p-2 text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.05] rounded-lg transition-all"><Undo2 size={14} /></button>
            <button onClick={() => showToast(t('redo'))} aria-label={t('redo')} className="p-2 text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.05] rounded-lg transition-all"><Redo2 size={14} /></button>
            <button onClick={() => setZoom(z => Math.max(0.5, +(z - 0.1).toFixed(2)))} aria-label={t('zoomOut')} className="p-2 text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.05] rounded-lg transition-all"><ZoomOut size={14} /></button>
            <span className="text-[10px] text-[var(--muted-foreground)] font-mono">{Math.round(zoom * 100)}%</span>
            <button onClick={() => setZoom(z => Math.min(2, +(z + 0.1).toFixed(2)))} aria-label={t('zoomIn')} className="p-2 text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.05] rounded-lg transition-all"><ZoomIn size={14} /></button>
          </div>
          <button
            onClick={() => showToast(t('workspaceSaved'))}
            aria-label={t('save')}
            className="py-1.5 px-3 bg-[var(--input)] border border-white/10 rounded-[var(--radius-sm)] text-xs text-[var(--foreground)] hover:bg-white/[0.08] transition-all flex items-center gap-1.5"
          >
            <Save size={12} /><span className="hidden sm:inline">{t('save')}</span>
          </button>
          <button
            onClick={handleRun}
            className="py-1.5 px-3 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-sm)] text-xs font-semibold flex items-center gap-1.5 shadow-[0_5px_15px_rgba(139,92,246,.3)] hover:-translate-y-0.5 transition-all"
          >
            <Play size={12} /><span className="hidden sm:inline">{t('run')}</span>
          </button>
        </div>
      </header>

      <div className="flex-1 flex overflow-hidden min-h-0">
        {/* Left Panel — desktop */}
        <aside className="w-[230px] bg-[#0A0B14]/90 border-r border-white/5 flex flex-col shrink-0 hidden md:flex min-h-0">
          <div className="flex border-b border-white/5 shrink-0">
            <button
              onClick={() => setActivePanel('equipment')}
              className={`flex-1 py-3 text-xs font-semibold transition-all ${activePanel === 'equipment' ? 'text-[#8B5CF6] border-b-2 border-[#8B5CF6] bg-[#8B5CF6]/5' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)]'}`}
            >
              {t('equipment')}
            </button>
            <button
              onClick={() => setActivePanel('materials')}
              className={`flex-1 py-3 text-xs font-semibold transition-all ${activePanel === 'materials' ? 'text-[#14F195] border-b-2 border-[#14F195] bg-[#14F195]/5' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)]'}`}
            >
              {t('materials')}
            </button>
          </div>
          <div className="flex-1 overflow-y-auto p-2 min-h-0">
            {activePanel === 'equipment' ? renderEquipmentList() : renderMaterialsList()}
          </div>
        </aside>

        {/* Canvas */}
        <main
          ref={canvasRef}
          className="flex-1 bg-[#06070E] relative overflow-hidden min-w-0"
          onClick={handleCanvasClick}
        >
          {showGrid && (
            <div className="absolute inset-0 opacity-[0.03] pointer-events-none" style={{
              backgroundImage: 'linear-gradient(rgba(255,255,255,0.8) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.8) 1px, transparent 1px)',
              backgroundSize: '40px 40px',
            }} />
          )}

          {/* Connections */}
          <svg className="absolute inset-0 w-full h-full z-[1] pointer-events-none" width={canvasW} height={canvasH}>
            {connections.map((conn) => {
              const from = canvasItems.find(i => i.id === conn.from);
              const to = canvasItems.find(i => i.id === conn.to);
              if (!from || !to) return null;
              const x1 = from.x + from.w / 2;
              const y1 = from.y + from.h / 2;
              const x2 = to.x + to.w / 2;
              const y2 = to.y + to.h / 2;
              return (
                <g key={conn.id}>
                  <path d={`M ${x1} ${y1} C ${(x1 + x2) / 2} ${y1}, ${(x1 + x2) / 2} ${y2}, ${x2} ${y2}`} fill="none" stroke="rgba(139,92,246,0.55)" strokeWidth="2" strokeDasharray="5 4" />
                  <circle cx={x1} cy={y1} r="3" fill="#8B5CF6" />
                  <circle cx={x2} cy={y2} r="3" fill="#8B5CF6" />
                </g>
              );
            })}
            {pendingConnection && previewPoint && (() => {
              const from = canvasItems.find(i => i.id === pendingConnection);
              if (!from) return null;
              return (
                <line x1={from.x + from.w / 2} y1={from.y + from.h / 2} x2={previewPoint.x} y2={previewPoint.y} stroke="rgba(52,211,153,0.7)" strokeWidth="2" strokeDasharray="4 4" />
              );
            })()}
          </svg>

          {canvasItems.length === 0 && (
            <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
              <div className="text-center opacity-30 max-w-[280px]">
                <FlaskConical size={48} className="mx-auto mb-3" />
                <p className="text-xs text-[var(--muted-foreground)]">{t('addEquipmentHint')}</p>
              </div>
            </div>
          )}

          {canvasItems.map((item, idx) => renderCanvasItem(item, idx))}

          {/* Canvas controls — top-right */}
          <div className="absolute top-3 right-3 z-20 flex items-center gap-1 p-1 bg-[#0F101A]/85 backdrop-blur-md border border-white/10 rounded-xl">
            <button onClick={() => setZoom(z => Math.max(0.5, +(z - 0.1).toFixed(2)))} aria-label={t('zoomOut')} className="w-7 h-7 grid place-items-center text-[var(--muted-foreground)] hover:text-[var(--foreground)] rounded-lg hover:bg-white/[0.05]"><ZoomOut size={13} /></button>
            <span className="text-[10px] text-[var(--muted-foreground)] font-mono w-11 text-center">{Math.round(zoom * 100)}%</span>
            <button onClick={() => setZoom(z => Math.min(2, +(z + 0.1).toFixed(2)))} aria-label={t('zoomIn')} className="w-7 h-7 grid place-items-center text-[var(--muted-foreground)] hover:text-[var(--foreground)] rounded-lg hover:bg-white/[0.05]"><ZoomIn size={13} /></button>
            <span className="w-px h-5 bg-white/10 mx-0.5" />
            <button onClick={() => setZoom(1)} aria-label={t('fitToScreen')} className="w-7 h-7 grid place-items-center text-[var(--muted-foreground)] hover:text-[var(--foreground)] rounded-lg hover:bg-white/[0.05]"><Maximize2 size={13} /></button>
            <button onClick={() => setShowGrid(v => !v)} aria-label={t('toggleGrid')} className={`w-7 h-7 grid place-items-center rounded-lg ${showGrid ? 'text-[#8b5cf6] bg-[#8b5cf6]/10' : 'text-[var(--muted-foreground)]'} hover:bg-white/[0.05]`}><Grid3X3 size={13} /></button>
            <button onClick={toggleConnectionMode} aria-label={t('connectionMode')} className={`w-7 h-7 grid place-items-center rounded-lg ${connectionMode ? 'text-[#34D399] bg-[#34D399]/10 ring-1 ring-[#34D399]/40' : 'text-[var(--muted-foreground)]'} hover:bg-white/[0.05]`}><Radio size={13} /></button>
          </div>

          {connectionMode && (
            <div className="absolute bottom-3 left-1/2 -translate-x-1/2 z-20 px-4 py-2 bg-[#0F101A]/90 backdrop-blur-md border border-[#34D399]/30 rounded-xl text-xs text-[#34D399] font-mono">
              {pendingConnection ? t('connectionModeTarget') : t('connectionModeSource')}
            </div>
          )}
        </main>

        {/* Right Panel — Properties */}
        <aside className="w-[260px] bg-[#0A0B14]/90 border-l border-white/5 shrink-0 hidden lg:flex flex-col min-h-0">
          <div className="p-3 border-b border-white/5 flex items-center justify-between shrink-0">
            <div className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider">{t('properties')}</div>
            <button onClick={toggleConnectionMode} aria-label={t('connectionMode')} className={`w-7 h-7 grid place-items-center rounded-lg ${connectionMode ? 'text-[#34D399] bg-[#34D399]/10' : 'text-[var(--muted-foreground)]'} hover:bg-white/[0.05]`}><Radio size={13} /></button>
          </div>
          <div className="flex-1 overflow-y-auto p-3 min-h-0">
            {renderProperties()}
          </div>
        </aside>
      </div>

      {/* Bottom panel — desktop (collapsible) */}
      <div className="hidden md:block shrink-0">
        {bottomOpen ? renderBottomPanel() : (
          <div className="h-9 bg-[#0A0B14]/90 border-t border-white/5 flex items-center px-3 gap-1 overflow-x-auto">
            {bottomTabs.map((tab) => (
              <button key={tab.key} onClick={() => { setBottomTab(tab.key); setBottomOpen(true); }} className="flex items-center gap-1.5 px-3 py-1.5 text-[10px] font-semibold text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-all whitespace-nowrap">
                <tab.icon size={11} />{t(tab.labelKey)}
              </button>
            ))}
            <span className="ml-auto flex items-center gap-3 text-[10px] text-[var(--muted-foreground)] font-mono">
              <span>{t('itemsCount', { count: canvasItems.length })}</span>
              <span>25°C</span>
              <span>pH 7.0</span>
            </span>
          </div>
        )}
      </div>

      {/* Mobile bottom toolbar */}
      <div className="md:hidden grid grid-cols-4 border-t border-white/10 bg-[#0A0B14]/95 shrink-0">
        <button onClick={() => setMobileSheet('equipment')} className="flex flex-col items-center gap-1 py-2.5 text-[10px] text-[var(--muted-foreground)] active:text-[#C084FC]"><FlaskConical size={17} />{t('equipment')}</button>
        <button onClick={() => setMobileSheet('materials')} className="flex flex-col items-center gap-1 py-2.5 text-[10px] text-[var(--muted-foreground)] active:text-[#14F195]"><Atom size={17} />{t('materials')}</button>
        <button onClick={() => { setMobileSheet(selected ? 'properties' : null); }} className="flex flex-col items-center gap-1 py-2.5 text-[10px] text-[var(--muted-foreground)] active:text-[#8b5cf6]"><Settings size={17} />{t('properties')}</button>
        <button onClick={() => setMobileSheet('results')} className="flex flex-col items-center gap-1 py-2.5 text-[10px] text-[var(--muted-foreground)] active:text-[#22D3EE]"><Activity size={17} />{t('results')}</button>
      </div>

      {/* Mobile bottom sheet */}
      {renderMobileSheet()}

      {/* Safety Warning */}
      {safetyWarning && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm z-[100] flex items-center justify-center p-5">
          <div className="bg-[#0F101A] border border-[#F43F5E]/30 rounded-[24px] w-full max-w-[440px] p-8 shadow-[0_25px_50px_rgba(0,0,0,0.7)]">
            <div className="text-center">
              <AlertTriangle size={40} className="text-[#F59E0B] mx-auto mb-4" />
              <h3 className="text-lg font-bold mb-2">{t('safetyCheckTitle')}</h3>
              <p className="text-sm text-[var(--muted-foreground)] mb-6">
                {t('safetyCheckMsg1')}
                {t('safetyCheckMsg2')}
              </p>
            </div>
            <div className="flex justify-center gap-3">
              <button onClick={() => setSafetyWarning(false)} className="py-2.5 px-5 border border-white/10 rounded-[var(--radius-md)] text-sm text-[var(--foreground)] hover:bg-white/[0.05]">
                {t('cancel')}
              </button>
              <button onClick={() => { setSafetyWarning(false); showToast(t('safetyBackendToast')); }} className="py-2.5 px-5 bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_8px_20px_rgba(139,92,246,0.35)]">
                {t('evaluate')}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && (
        <div className="fixed bottom-24 md:bottom-6 right-6 z-[200] px-4 py-3 bg-[#14F195]/10 border border-[#14F195]/30 text-[#14F195] rounded-[var(--radius-md)] text-sm font-medium shadow-lg flex items-center gap-2">
          {toast}
          <button onClick={() => setToast(null)} className="opacity-60 hover:opacity-100"><X size={14} /></button>
        </div>
      )}
    </div>
  );
}
