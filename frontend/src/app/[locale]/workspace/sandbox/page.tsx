'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState, useRef, useCallback } from 'react';
import {
  ArrowLeft, Settings, Play, Save, X, Plus, Trash2, AlertTriangle,
  Search, Undo2, Redo2, ZoomIn, ZoomOut, Move, Activity, List, Gauge, Beaker,
  Thermometer, Droplets, Filter, Radio, Snowflake, Atom, Flame, FlaskConical
} from 'lucide-react';
import { useTranslations } from 'next-intl';

// --- Types ---
type CanvasItemType = 'equipment' | 'material';

interface CanvasItem {
  id: string;
  type: string;
  name: string;
  kind: CanvasItemType;
  x: number;
  y: number;
  w: number;
  h: number;
}

// --- SVGs for Equipment ---
const EquipmentIcon = ({ type, size }: { type: string, size: number }) => {
  if (type === 'beaker') {
    return (
      <svg width={size} height={size} viewBox="0 0 100 100" fill="none" stroke="currentColor" strokeWidth="4">
        <path d="M20 10 H80 M30 10 V80 Q30 90 40 90 H60 Q70 90 70 80 V10" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M32 60 H68 M35 70 H65 M30 50 H70" stroke="var(--primary)" opacity="0.5"/>
      </svg>
    );
  }
  if (type === 'erlenmeyer') {
    return (
      <svg width={size} height={size} viewBox="0 0 100 100" fill="none" stroke="currentColor" strokeWidth="4">
        <path d="M40 10 H60 M45 10 V35 L20 80 Q15 90 25 90 H75 Q85 90 80 80 L55 35 V10" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M26 70 H74 M32 60 H68 M38 50 H62" stroke="var(--primary)" opacity="0.5"/>
      </svg>
    );
  }
  if (type === 'roundflask') {
    return (
      <svg width={size} height={size} viewBox="0 0 100 100" fill="none" stroke="currentColor" strokeWidth="4">
        <path d="M40 10 H60 M45 10 V40 A 30 30 0 1 0 55 40 V10" strokeLinecap="round" strokeLinejoin="round"/>
        <circle cx="50" cy="65" r="24" fill="var(--primary)" opacity="0.3" stroke="none" />
      </svg>
    );
  }
  if (type === 'burner') {
    return (
      <svg width={size} height={size} viewBox="0 0 100 100" fill="none" stroke="currentColor" strokeWidth="4">
        <path d="M30 90 H70 M40 90 V50 M60 90 V50 M40 50 H60 M45 50 V30 H55 V50" strokeLinecap="round" strokeLinejoin="round"/>
        <path d="M50 30 Q 40 15 50 5 Q 60 15 50 30" fill="#F59E0B" stroke="#F59E0B" />
      </svg>
    );
  }
  return <div className="w-full h-full flex items-center justify-center border-2 border-dashed border-[var(--border)] text-[var(--muted-foreground)] rounded-lg font-bold text-xs">{type}</div>;
};

// --- Library ---
const equipmentGroups = [
  {
    key: 'containers',
    title: 'Containers',
    items: [
      { id: 'beaker', name: 'Beaker', icon: Beaker, w: 100, h: 100 },
      { id: 'erlenmeyer', name: 'Erlenmeyer Flask', icon: Beaker, w: 100, h: 100 },
      { id: 'roundflask', name: 'Round-bottom Flask', icon: Beaker, w: 100, h: 110 },
    ],
  },
  {
    key: 'heating',
    title: 'Heating',
    items: [
      { id: 'burner', name: 'Bunsen Burner', icon: Flame, w: 80, h: 120 },
      { id: 'hotplate', name: 'Hot Plate', icon: Flame, w: 120, h: 60 },
    ],
  },
];

export default function SandboxPage() {
  const pathname = usePathname();
  const t = useTranslations('sandbox');
  const tc = useTranslations('common');
  
  const [activePanel, setActivePanel] = useState<'equipment' | 'materials'>('equipment');
  const [canvasItems, setCanvasItems] = useState<CanvasItem[]>([]);
  const [selectedItem, setSelectedItem] = useState<string | null>(null);
  const [draggingIdx, setDraggingIdx] = useState<number | null>(null);
  const [zoom, setZoom] = useState(1);
  const [tool, setTool] = useState<'select' | 'pan' | 'connect'>('select');

  const canvasRef = useRef<HTMLDivElement>(null);

  const addToCanvas = (item: any) => {
    const canvas = canvasRef.current;
    const cw = canvas?.getBoundingClientRect().width || 800;
    const ch = canvas?.getBoundingClientRect().height || 600;
    
    // Smart placement near center
    const x = (cw / 2) - (item.w / 2) + (Math.random() * 40 - 20);
    const y = (ch / 2) - (item.h / 2) + (Math.random() * 40 - 20);

    const newItem: CanvasItem = {
      id: `${item.id}_${Date.now()}`,
      type: item.id,
      name: item.name,
      kind: 'equipment',
      x, y, w: item.w, h: item.h
    };
    setCanvasItems(prev => [...prev, newItem]);
    setSelectedItem(newItem.id);
  };

  const handleMouseDown = (idx: number) => (e: React.MouseEvent) => {
    e.stopPropagation();
    if (tool !== 'select') return;
    setDraggingIdx(idx);
    setSelectedItem(canvasItems[idx].id);
    const startX = e.clientX - canvasItems[idx].x * zoom;
    const startY = e.clientY - canvasItems[idx].y * zoom;

    const handleMove = (me: MouseEvent) => {
      const newX = (me.clientX - startX) / zoom;
      const newY = (me.clientY - startY) / zoom;
      setCanvasItems(prev => prev.map((it, i) => i === idx ? { ...it, x: newX, y: newY } : it));
    };

    const handleUp = () => {
      setDraggingIdx(null);
      window.removeEventListener('mousemove', handleMove);
      window.removeEventListener('mouseup', handleUp);
    };
    window.addEventListener('mousemove', handleMove);
    window.addEventListener('mouseup', handleUp);
  };

  const selected = canvasItems.find(it => it.id === selectedItem);

  return (
    <div className="flex h-screen bg-[var(--background)] text-[var(--foreground)] overflow-hidden font-sans">
      
      {/* LEFT PANEL */}
      <div className="w-[320px] shrink-0 border-r border-[var(--border)] bg-[var(--card)] flex flex-col z-20">
        <header className="p-4 border-b border-[var(--border)] flex items-center justify-between">
          <Link href={`/${pathname.split('/')[1]}/dashboard`} className="p-2 -ml-2 rounded-lg text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-[var(--accent)] transition-colors">
            <ArrowLeft size={18} />
          </Link>
          <div className="font-semibold text-sm">Untitled Experiment</div>
          <button className="p-2 -mr-2 rounded-lg text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-[var(--accent)] transition-colors">
            <Settings size={18} />
          </button>
        </header>

        <div className="flex p-2 gap-1 border-b border-[var(--border)]">
          <button onClick={() => setActivePanel('equipment')} className={`flex-1 py-1.5 text-xs font-semibold rounded-md transition-colors ${activePanel === 'equipment' ? 'bg-[var(--primary)] text-[var(--primary-foreground)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--accent)]'}`}>Equipment</button>
          <button onClick={() => setActivePanel('materials')} className={`flex-1 py-1.5 text-xs font-semibold rounded-md transition-colors ${activePanel === 'materials' ? 'bg-[var(--primary)] text-[var(--primary-foreground)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--accent)]'}`}>Materials</button>
        </div>

        <div className="flex-1 overflow-y-auto p-4 space-y-6">
          {activePanel === 'equipment' && equipmentGroups.map(group => (
            <div key={group.key}>
              <h3 className="text-xs font-bold text-[var(--muted-foreground)] uppercase tracking-wider mb-3">{group.title}</h3>
              <div className="grid grid-cols-2 gap-3">
                {group.items.map(item => (
                  <div key={item.id} onClick={() => addToCanvas(item)} className="group cursor-pointer border border-[var(--border)] rounded-lg p-3 bg-[var(--background)] hover:border-[var(--primary)] hover:shadow-md transition-all flex flex-col items-center text-center">
                    <item.icon size={28} className="mb-2 text-[var(--primary)] opacity-80 group-hover:opacity-100" />
                    <span className="text-xs font-medium">{item.name}</span>
                    <button className="mt-2 w-full py-1 text-[10px] uppercase font-bold text-[var(--primary)] bg-[var(--primary)]/10 rounded group-hover:bg-[var(--primary)]/20">Add</button>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* CENTER CANVAS */}
      <div className="flex-1 relative bg-[var(--background)] overflow-hidden flex flex-col">
        {/* Toolbar */}
        <div className="absolute top-4 left-1/2 -translate-x-1/2 z-30 flex items-center gap-1 p-1 bg-[var(--card)]/90 backdrop-blur border border-[var(--border)] rounded-xl shadow-lg">
          <button onClick={() => setTool('select')} className={`p-2.5 rounded-lg ${tool === 'select' ? 'bg-[var(--primary)] text-[var(--primary-foreground)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--accent)]'}`} title="Select"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M3 3l7.07 16.97 2.51-7.39 7.39-2.51L3 3z"/></svg></button>
          <button onClick={() => setTool('pan')} className={`p-2.5 rounded-lg ${tool === 'pan' ? 'bg-[var(--primary)] text-[var(--primary-foreground)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--accent)]'}`} title="Pan"><Move size={18} /></button>
          <button onClick={() => setTool('connect')} className={`p-2.5 rounded-lg ${tool === 'connect' ? 'bg-[var(--primary)] text-[var(--primary-foreground)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--accent)]'}`} title="Connect"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M8 12h8M4 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zm16 0a4 4 0 1 0 0-8 4 4 0 0 0 0 8z"/></svg></button>
          <div className="w-px h-6 bg-[var(--border)] mx-1" />
          <button onClick={() => setZoom(z => z + 0.1)} className="p-2.5 rounded-lg text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-[var(--accent)]"><ZoomIn size={18}/></button>
          <button onClick={() => setZoom(z => z - 0.1)} className="p-2.5 rounded-lg text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-[var(--accent)]"><ZoomOut size={18}/></button>
        </div>

        {/* Canvas Area */}
        <div 
          ref={canvasRef}
          className="flex-1 relative cursor-default"
          style={{ backgroundImage: 'radial-gradient(var(--border) 1px, transparent 1px)', backgroundSize: `${20*zoom}px ${20*zoom}px`, backgroundPosition: 'center' }}
          onClick={() => setSelectedItem(null)}
        >
          {canvasItems.length === 0 && (
            <div className="absolute inset-0 flex flex-col items-center justify-center text-[var(--muted-foreground)] pointer-events-none">
              <FlaskConical size={48} className="opacity-20 mb-4" />
              <h2 className="text-xl font-bold mb-2 text-[var(--foreground)]">Build your experiment</h2>
              <p className="text-sm">Drag equipment from the left panel to begin.</p>
            </div>
          )}

          <div style={{ transform: `scale(${zoom})`, transformOrigin: 'top left', width: '100%', height: '100%' }}>
            {canvasItems.map((item, idx) => {
              const isSelected = selectedItem === item.id;
              return (
                <div
                  key={item.id}
                  className={`absolute group ${isSelected ? 'ring-2 ring-[var(--primary)]' : 'hover:ring-1 hover:ring-[var(--primary)]/50'} rounded-lg p-2 bg-[var(--background)]/50 backdrop-blur-sm cursor-grab active:cursor-grabbing transition-shadow`}
                  style={{ left: item.x, top: item.y, width: item.w, height: item.h }}
                  onMouseDown={handleMouseDown(idx)}
                  onClick={(e) => { e.stopPropagation(); setSelectedItem(item.id); }}
                >
                  <EquipmentIcon type={item.type} size={Math.min(item.w, item.h) - 16} />
                  
                  {/* Connection Ports (visible when selected or connect tool active) */}
                  {(isSelected || tool === 'connect') && (
                    <>
                      <div className="absolute -top-1.5 left-1/2 -translate-x-1/2 w-3 h-3 rounded-full bg-[#C084FC] border-2 border-[var(--background)] cursor-crosshair z-10 hover:scale-125 transition-transform" title="Glass Joint" />
                      <div className="absolute top-1/2 -right-1.5 -translate-y-1/2 w-3 h-3 rounded-full bg-[#34D399] border-2 border-[var(--background)] cursor-crosshair z-10 hover:scale-125 transition-transform" title="Gas Port" />
                      <div className="absolute -bottom-1.5 left-1/2 -translate-x-1/2 w-3 h-3 rounded-full bg-[#F59E0B] border-2 border-[var(--background)] cursor-crosshair z-10 hover:scale-125 transition-transform" title="Thermal Contact" />
                    </>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* BOTTOM PANEL */}
        <div className="h-[200px] shrink-0 border-t border-[var(--border)] bg-[var(--card)] z-20 flex flex-col">
          <div className="flex border-b border-[var(--border)] bg-[var(--background)]/50 px-2">
            <button className="px-4 py-2 text-xs font-bold border-b-2 border-[var(--primary)] text-[var(--primary)] flex items-center gap-2"><Activity size={14}/> Results</button>
            <button className="px-4 py-2 text-xs font-medium border-b-2 border-transparent text-[var(--muted-foreground)] hover:text-[var(--foreground)] flex items-center gap-2"><List size={14}/> Reaction Log</button>
            <button className="px-4 py-2 text-xs font-medium border-b-2 border-transparent text-[var(--muted-foreground)] hover:text-[var(--foreground)] flex items-center gap-2"><AlertTriangle size={14}/> Safety</button>
          </div>
          <div className="flex-1 p-4 grid grid-cols-4 gap-6 items-start">
            <div className="space-y-1">
              <div className="text-[10px] uppercase text-[var(--muted-foreground)] font-bold">Temperature</div>
              <div className="text-xl font-mono text-[var(--foreground)]">24.5 °C</div>
            </div>
            <div className="space-y-1">
              <div className="text-[10px] uppercase text-[var(--muted-foreground)] font-bold">Pressure</div>
              <div className="text-xl font-mono text-[var(--foreground)]">1.0 atm</div>
            </div>
            <div className="space-y-1">
              <div className="text-[10px] uppercase text-[var(--muted-foreground)] font-bold">pH</div>
              <div className="text-xl font-mono text-[var(--foreground)]">7.2</div>
            </div>
            <div className="space-y-1">
              <div className="text-[10px] uppercase text-[var(--muted-foreground)] font-bold">State</div>
              <div className="text-sm font-medium text-[#34D399] flex items-center gap-2"><span className="w-2 h-2 rounded-full bg-[#34D399]"></span> Stable</div>
            </div>
          </div>
        </div>
      </div>

      {/* RIGHT PROPERTIES PANEL */}
      <div className="w-[300px] shrink-0 border-l border-[var(--border)] bg-[var(--card)] flex flex-col z-20">
        <div className="p-4 border-b border-[var(--border)] flex justify-between items-center bg-[var(--background)]/50">
          <div className="font-semibold text-sm">Properties</div>
          <button onClick={() => setSelectedItem(null)} className="p-1 rounded hover:bg-[var(--accent)] text-[var(--muted-foreground)]"><X size={16}/></button>
        </div>
        
        {selected ? (
          <div className="flex-1 overflow-y-auto">
            <div className="p-5 flex flex-col items-center border-b border-[var(--border)]">
              <div className="w-16 h-16 bg-[var(--background)] rounded-xl border border-[var(--border)] flex items-center justify-center mb-3">
                <EquipmentIcon type={selected.type} size={40} />
              </div>
              <h3 className="font-bold text-lg text-[var(--foreground)]">{selected.name}</h3>
              <div className="text-[10px] text-[var(--primary)] font-mono bg-[var(--primary)]/10 px-2 py-1 rounded mt-1">Ready</div>
            </div>
            
            <div className="p-4 space-y-4">
              <div>
                <h4 className="text-xs font-bold text-[var(--muted-foreground)] uppercase tracking-wider mb-2">Details</h4>
                <div className="bg-[var(--background)] border border-[var(--border)] rounded-lg p-3 space-y-2">
                  <div className="flex justify-between text-xs"><span className="text-[var(--muted-foreground)]">Capacity</span><span className="font-mono">500 mL</span></div>
                  <div className="flex justify-between text-xs"><span className="text-[var(--muted-foreground)]">Temperature</span><span className="font-mono text-[#F59E0B]">25.0 °C</span></div>
                  <div className="flex justify-between text-xs"><span className="text-[var(--muted-foreground)]">Material</span><span>Glass</span></div>
                </div>
              </div>
              
              <div>
                <h4 className="text-xs font-bold text-[var(--muted-foreground)] uppercase tracking-wider mb-2">Actions</h4>
                <div className="grid grid-cols-2 gap-2">
                  <button className="py-2 text-xs font-semibold rounded bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30 hover:bg-[#F59E0B]/20">Heat</button>
                  <button className="py-2 text-xs font-semibold rounded bg-[#22D3EE]/10 text-[#22D3EE] border border-[#22D3EE]/30 hover:bg-[#22D3EE]/20">Cool</button>
                  <button className="py-2 text-xs font-semibold rounded bg-[var(--primary)]/10 text-[var(--primary)] border border-[var(--primary)]/30 hover:bg-[var(--primary)]/20">Stir</button>
                  <button className="py-2 text-xs font-semibold rounded bg-[var(--background)] text-[var(--foreground)] border border-[var(--border)] hover:bg-[var(--accent)]">Pour</button>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="flex-1 flex items-center justify-center p-6 text-center text-[var(--muted-foreground)]">
            <p className="text-sm">Select an object on the canvas to view its properties.</p>
          </div>
        )}
      </div>

    </div>
  );
}
