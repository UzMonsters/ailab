'use client';
import { useState, type ReactNode } from 'react';
import { useLocale } from 'next-intl';
import {
  Flame, Snowflake, Sparkles, RotateCcw,
  Thermometer, Scale, Link2, Pencil, Trash2, History, Zap, } from 'lucide-react';
import type { Connection, Item } from '@/widgets/sandbox/types';
import { isVessel } from '@/widgets/sandbox/types';

export const equipmentDescription = (item: Item) => ({
  beaker: 'Сосуд для смешивания, переливания и нагрева жидкостей.',
  erlenmeyer: 'Коническая колба для смешивания веществ и проведения реакций.',
  roundflask: 'Круглодонная колба для нагревания и проведения реакций.',
  funnel: 'Воронка для аккуратного переливания жидкости в сосуд.',
  separatory_funnel: 'Делительная воронка для разделения несмешивающихся жидкостей.',
  thermometer: 'Измеряет температуру только после соединения с сосудом.',
  hotplate: 'Нагревает сосуд, установленный сверху или прикреплённый к нагревателю.',
  burner: 'Источник направленного нагрева. Включается после установки под сосудом.',
  testtube: 'Небольшой сосуд для проб и малых объёмов веществ.',
  graduated_cylinder: 'Мерный цилиндр для точного измерения объёма жидкости.',
  volumetric_flask: 'Мерная колба для приготовления растворов заданной концентрации.',
  burette: 'Точный дозатор жидкости с краном и градуированной шкалой.',
  pipette: 'Инструмент для переноса небольших объёмов жидкости.',
  condenser: 'Холодильник для конденсации паров и охлаждения потока.',
  phmeter: 'Измеряет кислотность раствора после погружения датчика.',
}[item.type] ?? 'Лабораторное оборудование для химического эксперимента.');

interface Props {
  item: Item;
  update: (id: string, patch: Partial<Item>) => void;
  onOperation: (item: Item, op: string) => void;
  connections: Connection[];
  onConnectionDelete: (id: string) => void;
  onConnectionEdit: (id: string) => void;
  onDeviceAction: (item: Item, action: string) => void;
  onMaterialRemove?: (itemId: string, materialId: string, phase: string) => void;
  setPourSource: (id: string | null) => void;
  pourSource: string | null;
  temperatureConnected?: boolean;
  /** Beginner levels intentionally show only the information needed for the current experiment. */
  levelMode?: boolean;
  onQuickAction?: (action: { action: 'pour' | 'heat' | 'measure' | 'tube' | 'material' | null; step: 'idle' | 'select-target' | 'select-port'; sourceId?: string; targetId?: string; targetPort?: string; }) => void;
  }

export function Properties({ item, update, onOperation, connections, onConnectionDelete, onConnectionEdit, onDeviceAction, onMaterialRemove, setPourSource, pourSource, temperatureConnected = false, levelMode = false, onQuickAction }: Props) {
  const cap = item.capabilities ?? {};
  const heater = cap.heater as { maxTemperature?: number } | undefined;
  const cooler = cap.cooler as { minTempC?: number } | undefined;
  const scale = cap.scale as { maxMassG?: number; precisionG?: number } | undefined;
  const isBurner = item.type === 'burner';
  const isHotplate = item.type === 'hotplate';
  const isHeater = isBurner || isHotplate;
  const isPhMeter = item.type === 'phmeter';
  const canHeat = !isHeater && !!heater;
  const canCool = !!cooler;
  const vessel = isVessel(item);

  const allTabs = ['Details', 'Connections', 'History'];
  const tabLabels: Record<string, string> = { Details: 'Детали', Connections: 'Связи', History: 'Журнал' };

  const [rawTab, setTab] = useState('Overview');
  const tab = allTabs.includes(rawTab) ? rawTab : 'Details';

  return (
    <div className="flex h-full flex-col p-4">
      

      {/* Item header */}
      <div className="mb-3 flex items-start gap-2 border-b border-foreground/[.06] pb-2">
        <div className="min-w-0 flex-1">
          <p className="truncate text-sm font-bold text-foreground">{item.name}</p>
          <p className="text-[11px] text-[var(--muted-foreground)]">{equipmentDescription(item)}</p>
        </div>
        {item.broken && <span className="rounded-full bg-red-500/20 px-2 py-0.5 text-[10px] font-bold text-red-400">РАЗБИТ</span>}
      </div>

      {/* Tabs */}
      <div className="mb-3 flex gap-1 overflow-x-auto">
        {allTabs.map(t => (
          <button key={t} onClick={() => setTab(t)}
            className={`rounded-lg px-2.5 py-1.5 text-[11px] font-semibold whitespace-nowrap transition ${
              tab === t ? 'bg-[var(--primary)] text-white' : 'text-[var(--muted-foreground)] hover:bg-foreground/[.06]'
            }`}>
            {tabLabels[t]}
          </button>
        ))}
      </div>

      {/* Tab content */}
      <div className="min-h-0 flex-1 overflow-y-auto space-y-4 pr-1">
        {tab === 'Details' && (
          <>
            {item.broken && (
              <div className="mb-4 rounded-xl border border-red-500/40 bg-red-500/10 p-3 text-red-200">
                <div className="flex items-center gap-2 font-bold text-xs">
                  <span className="h-2 w-2 rounded-full bg-red-500 animate-ping" />
                  ⚠️ СОСУД РАЗБИТ (SHATTERED)
                </div>
                <p className="mt-1 text-[11px] opacity-80">
                  Соединения разорваны, содержимое разлито. Переливание и нагрев заблокированы.
                </p>
              </div>
            )}
            {item.unsafeConfiguration && !item.broken && (
              <div className="mb-4 rounded-xl border border-red-400/50 bg-red-500/10 p-3 text-red-200">
                <div className="font-bold text-xs">⚠ Небезопасная конфигурация</div>
                <p className="mt-1 text-[11px] opacity-80">В закрытом сосуде растёт давление. Откройте систему или остановите симуляцию.</p>
              </div>
            )}

            {isPhMeter && (
              <div className="mb-4 rounded-xl border border-foreground/10 bg-black/40 p-3.5 space-y-2">
                <span className="text-[10px] font-bold uppercase tracking-wider text-cyan-400">Измерение pH</span>
                <p className="text-xs text-orange-300">Погрузите зонд в раствор: без контакта показание не отображается.</p>
              </div>
            )}

            {vessel && !isHeater && item.type !== 'hotplate' && (
              <div className="mb-4 flex items-center justify-between rounded-xl border border-foreground/[.08] bg-foreground/[.02] p-4 shadow-sm">
                <div className="flex flex-col">
                  <span className="text-[10px] font-bold uppercase tracking-wider text-[var(--muted-foreground)]">Объем</span>
                  <span className="text-sm font-semibold text-foreground">{item.volumeMl?.toFixed(1) ?? '0.0'} <span className="text-xs text-foreground/40">/ {item.capacityMl ?? '-'} мл</span></span>
                </div>
                {temperatureConnected && (
                  <div className="flex flex-col text-right">
                    <span className="text-[10px] font-bold uppercase tracking-wider text-[var(--muted-foreground)]">Темп.</span>
                    <span className="text-sm font-semibold text-foreground">{item.measuredTemperatureC?.toFixed(1) ?? '—'} <span className="text-xs text-foreground/40">°C</span></span>
                  </div>
                )}
              </div>
            )}

            {vessel && <ContentsPanel item={item} onRemove={onMaterialRemove} />}

            {item.type === 'thermometer' && !levelMode && (
              <div className="rounded-xl border border-blue-500/20 bg-blue-500/5 p-3 text-xs">
                <div className="flex items-center justify-between"><span className="font-bold uppercase tracking-wider text-blue-300">Measurement</span><span className={item.measurementStatus === 'OVER RANGE' ? 'font-bold text-red-300' : 'text-emerald-300'}>{item.measurementStatus === 'OVER RANGE' ? 'OVER RANGE' : `${item.measuredTemperatureC?.toFixed(1) ?? '—'} °C`}</span></div>
                <p className="mt-1 text-[10px] text-blue-200/70">Target: {item.measurementTarget ?? 'environment'} · range −20…300 °C · resolution 0.1 °C</p>
              </div>
            )}

            {vessel && !levelMode && (
              <label className="block rounded-xl border border-violet-500/20 bg-violet-500/5 p-3 text-xs">
                <span className="font-bold uppercase tracking-wider text-violet-300">System boundary</span>
                <select aria-label="System type" value={item.systemType ?? (item.sealed ? 'closed' : 'open')} onChange={e => { const systemType = e.target.value as Item['systemType']; update(item.id, { systemType, sealed: systemType === 'closed' }); }} className="mt-2 w-full rounded-lg border border-[var(--border)] bg-[var(--background)] px-2 py-1.5">
                  <option value="open">Open — vapor escapes</option>
                  <option value="vented">Vented — controlled gas outlet</option>
                  <option value="closed">Closed — pressure can rise</option>
                </select>
              </label>
            )}

            {(item.type === 'burette' || item.type === 'pipette') && !levelMode && (
              <label className="block rounded-xl border border-cyan-500/20 bg-cyan-500/5 p-3 text-xs">
                <span className="font-bold uppercase tracking-wider text-cyan-300">Stopcock / valve</span>
                <select aria-label="Valve opening" value={Math.round((item.valveOpening ?? 0) * 100)} onChange={e => update(item.id, { valveOpening: Number(e.target.value) / 100 })} className="mt-2 w-full rounded-lg border border-[var(--border)] bg-[var(--background)] px-2 py-1.5">
                  {[0, 25, 50, 75, 100].map(value => <option key={value} value={value}>{value === 0 ? 'Closed' : value === 100 ? 'Open' : `${value}%`}</option>)}
                </select>
                <p className="mt-1 text-[10px] text-cyan-200/70">Flow starts only while the simulation is playing and the outlet is downhill.</p>
              </label>
            )}

            {canHeat && (
              <label className="block text-xs">
                <span className="text-[var(--muted-foreground)]">Целевая температура (°C)</span>
                <input aria-label="Целевая температура" type="number" min="0" max="1500"
                  value={item.targetTemperature ?? 80}
                  onChange={e => update(item.id, { targetTemperature: Number(e.target.value) })}
                  className="mt-1 w-full rounded-lg border border-[var(--border)] bg-[var(--background)] px-2 py-1.5 text-sm" />
              </label>
            )}

            {/* Thermal operations */}
            {(canHeat || canCool) && (
              <div className="grid grid-cols-2 gap-2">
                {canHeat && <ActionButton label={item.operation === 'heating' ? 'Stop heat' : 'Heat'} icon={<Flame size={14} />} active={item.operation === 'heating'} onClick={() => onOperation(item, item.operation === 'heating' ? 'idle' : 'heating')} />}
                {canCool && <ActionButton label={item.operation === 'cooling' ? 'Stop cool' : 'Cool'} icon={<Snowflake size={14} />} active={item.operation === 'cooling'} onClick={() => onOperation(item, item.operation === 'cooling' ? 'idle' : 'cooling')} />}
              </div>
            )}

            {/* Burner */}
            {isBurner && (
              <DevicePanel title="Bunsen Burner" accent="orange">
                <ActionButton
                  label={item.operation === 'heating' ? 'Extinguish' : 'Ignite'}
                  icon={<Flame size={14} />}
                  active={item.operation === 'heating'}
                  onClick={() => onDeviceAction(item, item.operation === 'heating' ? 'Extinguish' : 'Ignite')}
                />
              </DevicePanel>
            )}

            {/* Scale */}
            {scale && (
              <DevicePanel title="Analytical Balance" accent="purple">
                <Metric label="Reading" value={`${item.massG.toFixed(4)} g`} />
                <Metric label="Max load" value={`${scale.maxMassG ?? '—'} g`} />
                <Metric label="Precision" value={scale.precisionG !== undefined ? `±${scale.precisionG} g` : '—'} />
                <div className="col-span-2 flex gap-2">
                  <ActionButton label="Tare" icon={<RotateCcw size={13} />} onClick={() => onDeviceAction(item, 'Tare')} />
                  <ActionButton label="Zero" icon={<Sparkles size={13} />} onClick={() => onDeviceAction(item, 'Zero')} />
                  <ActionButton label="Calibrate" icon={<Scale size={13} />} onClick={() => onDeviceAction(item, 'Calibrate')} />
                </div>
              </DevicePanel>
            )}

            {/* Condenser badge */}
            {cap.condenser && (
              <div className="rounded-xl border border-blue-500/20 bg-blue-500/5 p-2 text-xs text-blue-300">
                Condenser · Water-cooled
              </div>
            )}
          </>
        )}

        {tab === 'Connections' && <ConnectionsPanel item={item} connections={connections} onDelete={onConnectionDelete} onEdit={onConnectionEdit} />}
        {tab === 'History' && <HistoryPanel history={item.history ?? []} />}
      </div>
    </div>
  );
}

function ContentsPanel({ item, onRemove }: { item: Item; onRemove?: (itemId: string, materialId: string, phase: string) => void }) {
  const locale = useLocale();
  if (item.contents.length === 0) return <Empty text="Сосуд пуст. Добавьте вещество из библиотеки." />;
  return (
    <section className="space-y-2">
      <p className="text-[11px] font-bold uppercase text-[var(--muted-foreground)] tracking-wider">Содержимое</p>
      {item.contents.map((c, i) => {
        const total = item.contents.reduce((sum, content) => sum + Math.max(0, content.amount), 0);
        const share = total > 0 ? c.amount / total * 100 : 0;
        const isCopperSulfateSolution = c.materialId === 'CuSO4(aq)' && c.phase === 'aqueous';
        const isHomogeneous = Boolean((c as typeof c & { metadata?: { homogeneous?: boolean } }).metadata?.homogeneous);
        const displayName = isCopperSulfateSolution
          ? (locale === 'ru' ? 'Раствор сульфата меди' : locale === 'uz' ? 'Mis sulfat eritmasi' : 'Copper sulfate solution')
          : (c.name ?? c.formula ?? c.materialId);
        const homogeneousLabel = locale === 'ru' ? 'однородный' : locale === 'uz' ? 'bir jinsli' : 'homogeneous';
        // Removing one liquid component from a vessel would falsely imply that
        // a learner can separate a solution by clicking a trash icon.
        const canRemoveComponent = Boolean(onRemove) && !isHomogeneous && !['liquid', 'aqueous'].includes(c.phase);
        return (
        <div key={`${c.materialId}-${i}`} className="flex min-h-11 items-center justify-between rounded-lg border border-foreground/[.08] bg-foreground/[.02] p-3 text-xs">
          <div className="flex items-center gap-2">
            <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: c.color ?? '#94a3b8' }} />
            <span className="font-semibold text-foreground">{displayName}</span>
            {isCopperSulfateSolution && <span className="font-mono text-[10px] text-cyan-300">CuSO₄(aq)</span>}
            <span className="text-[10px] text-foreground/40">({c.phase})</span>
            {isHomogeneous && <span className="rounded-full bg-cyan-400/10 px-1.5 py-0.5 text-[9px] font-semibold text-cyan-300">{homogeneousLabel}</span>}
          </div>
          <div className="flex items-center gap-2">
            <span className="font-mono text-foreground/80">{c.amount.toFixed(1)} {c.unit ?? 'mL'} · {share.toFixed(0)}%</span>
            {canRemoveComponent && onRemove && (
              <button
                type="button"
                onClick={() => onRemove(item.id, c.materialId, c.phase)}
                className="text-foreground/30 hover:text-red-400 transition-colors p-1"
                aria-label="Удалить компонент"
              >
                <Trash2 size={12} />
              </button>
            )}
          </div>
        </div>
      )})}
    </section>
  );
}

function ConnectionsPanel({ item, connections, onDelete, onEdit }: { item: Item; connections: Connection[]; onDelete: (id: string) => void; onEdit: (id: string) => void }) {
  return (
    <div className="space-y-2">
      <p className="text-[10px] font-bold uppercase tracking-wider text-foreground/50">Ports</p>
      {item.ports.length === 0 ? <Empty text="This device has no connection ports." /> : item.ports.map((port) => {
        const connection = connections.find((candidate) => candidate.fromPort === port.id || candidate.toPort === port.id);
        const target = connection ? (connection.from === item.id ? connection.toName : connection.fromName) : undefined;
        return (
          <div key={port.id} className="flex items-center justify-between rounded-lg border border-foreground/[.08] bg-foreground/[.02] px-3 py-2 text-xs">
            <span><b className="text-foreground">{port.name}</b><small className="ml-1 text-foreground/45">{port.type} · {port.direction ?? 'bidirectional'}</small></span>
            <span className={target ? 'text-emerald-300' : 'text-foreground/35'}>{target ?? 'available'}</span>
          </div>
        );
      })}
      <p className="pt-2 text-[10px] font-bold uppercase tracking-wider text-foreground/50">Active links</p>
      {connections.length === 0 && <Empty text="No active connections." />}
      {connections.map(c => (
        <div key={c.id} className="flex items-center justify-between rounded-xl border border-foreground/[.08] bg-foreground/[.02] p-2.5 text-xs">
          <div className="flex items-center gap-2">
            <Link2 size={14} className="text-[var(--primary)]" />
            <div>
              <p className="font-semibold text-foreground">{c.fromPort ?? 'port'} → {c.toPort ?? 'port'}</p>
              <p className="text-[10px] text-foreground/40">{c.medium ?? c.port}</p>
            </div>
          </div>
          <div className="flex items-center gap-1">
            <button onClick={() => onEdit(c.id)} className="rounded p-1 text-foreground/40 hover:bg-foreground/10 hover:text-foreground" aria-label="Изменить"><Pencil size={12} /></button>
            <button onClick={() => onDelete(c.id)} className="rounded p-1 text-red-400/60 hover:bg-red-500/20 hover:text-red-300" aria-label="Удалить"><Trash2 size={12} /></button>
          </div>
        </div>
      ))}
    </div>
  );
}

function PhysicsPanel({ item }: { item: Item }) {
  const metrics = [
    ['Temperature', `${item.temperature.toFixed(1)} °C`],
    ['Pressure', `${item.pressureBar.toFixed(3)} bar`],
    ['Boundary', item.systemType ?? (item.sealed ? 'closed' : 'open')],
    ['Heat input', item.operation === 'heating' ? `${item.targetTemperature ?? 'active'} °C target` : '0 W / inactive'],
    ['Evaporation', item.temperature >= 78 && item.volumeMl > 0 ? 'active' : 'stable'],
    ['Mixing', item.operation === 'mixing' ? 'in progress' : 'idle'],
    ['Integrity', item.integrity ?? 'intact'],
    ['Capacity use', item.capacityMl ? `${Math.round(item.volumeMl / item.capacityMl * 100)}%` : 'n/a'],
  ];
  return (
    <div className="grid grid-cols-2 gap-2">
      {metrics.map(([label, value]) => (
        <div key={label} className="rounded-xl border border-foreground/[.08] bg-foreground/[.02] p-3">
          <p className="text-[9px] font-bold uppercase tracking-wider text-foreground/40">{label}</p>
          <p className="mt-1 font-mono text-xs font-semibold text-foreground">{value}</p>
        </div>
      ))}
    </div>
  );
}

function HistoryPanel({ history }: { history: string[] }) {
  if (history.length === 0) return <Empty text="No recorded history." />;
  return (
    <div className="space-y-1.5 font-mono text-[11px] text-foreground/70">
      {history.map((h, i) => (
        <div key={i} className="rounded bg-foreground/[.02] p-2 border border-foreground/[.04]">{h}</div>
      ))}
    </div>
  );
}

function DevicePanel({ title, accent, children }: { title: string; accent: string; children: ReactNode }) {
  return (
    <div className="rounded-xl border border-foreground/[.08] bg-foreground/[.02] p-3 space-y-2">
      <p className="text-[10px] font-bold uppercase tracking-wider text-foreground/60">{title}</p>
      <div className="grid grid-cols-2 gap-2">{children}</div>
    </div>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex flex-col">
      <span className="text-[10px] text-foreground/40">{label}</span>
      <span className="text-xs font-semibold text-foreground font-mono">{value}</span>
    </div>
  );
}

function ActionButton({ label, icon, active, onClick }: { label: string; icon: ReactNode; active?: boolean; onClick: () => void }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex items-center justify-center gap-1.5 rounded-lg py-2 text-xs font-semibold transition-all ${
        active ? 'bg-[var(--primary)] text-white shadow-md' : 'bg-foreground/5 text-foreground/70 hover:bg-foreground/10 hover:text-foreground'
      }`}
    >
      {icon}
      <span>{label}</span>
    </button>
  );
}

function Empty({ text }: { text: string }) {
  return (
    <div className="sandbox-empty-state rounded-xl border border-cyan-400/25 bg-cyan-400/[.08] p-4 text-center text-xs font-medium leading-relaxed text-foreground shadow-inner">
      <span className="mb-1 block text-lg not-italic" aria-hidden="true">◌</span>
      <span>{text}</span>
    </div>
  );
}
