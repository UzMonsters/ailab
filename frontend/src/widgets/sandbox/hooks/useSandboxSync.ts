import { useCallback, useEffect, useRef, useState } from 'react';
import type { WorkspaceState, WorkspaceEventAck, SandboxEventCommand } from '@/types';
import type { CommandHistory } from '@/engine/history/CommandHistory';
import type { Engine } from '@/engine/core/Engine';
import type { EquipmentRegistry } from '@/engine/registry/EquipmentRegistry';
import type { WorkspaceSnapshot } from '@/engine/workspace/Workspace';
import type { WorkspaceRepository } from '@/engine/workspace/WorkspaceRepository';
import type { SceneSnapshot } from '@/engine/scene/Scene';
import { connectWorkspaceRealtime } from '@/entities/workspace/api/realtime/workspace-realtime';

type UnknownRecord = Record<string, unknown>;
export type SandboxSyncStatus = 'idle' | 'loading' | 'hydrating' | 'ready' | 'reconciling' | 'offline' | 'conflict' | 'saving' | 'saved' | 'error';
type RealtimeEvent = { stateVersion?: number; stateDelta?: UnknownRecord };

interface UseSandboxSyncProps {
  engine: Engine;
  workspaceId: string | null;
  sessionId: string | null;
  setSessionId: (id: string | null) => void;
  history: CommandHistory;
  isAuthenticated: boolean;
  authChecked: boolean;
  showToast: (msg: string, type?: 'info' | 'error' | 'success') => void;
  registry: EquipmentRegistry;
  pan: { x: number; y: number };
  zoom: number;
  setPan: (pan: { x: number; y: number }) => void;
  setZoom: (zoom: number) => void;
  getWorkspaceSnapshot: () => WorkspaceSnapshot | null;
}

function record(value: unknown): UnknownRecord {
  return value && typeof value === 'object' && !Array.isArray(value) ? value as UnknownRecord : {};
}

function asRealtimeEvent(value: unknown): RealtimeEvent | null {
  const event = record(value);
  const version = Number(event.stateVersion);
  if (!Number.isFinite(version)) return null;
  return { stateVersion: version, stateDelta: record(event.stateDelta) };
}

function isConflictError(error: unknown): boolean {
  return Number(record(error).status) === 409;
}

function isConnectionSnapshot(value: unknown): value is SceneSnapshot['connections'][number] {
  const item = record(value);
  const from = record(item.from);
  const to = record(item.to);
  return typeof item.id === 'string' && typeof from.objectId === 'string' && typeof from.portId === 'string' && typeof to.objectId === 'string' && typeof to.portId === 'string' && typeof item.type === 'string' && typeof item.style === 'string';
}

async function retry<T>(operation: () => Promise<T>, attempts = 4): Promise<T> {
  let lastError: unknown;
  for (let attempt = 0; attempt < attempts; attempt += 1) {
    try { return await operation(); } catch (error) {
      lastError = error;
      if (attempt === attempts - 1) break;
      await new Promise((resolve) => window.setTimeout(resolve, 250 * 2 ** attempt));
    }
  }
  throw lastError instanceof Error ? lastError : new Error('Workspace request failed');
}

export function useSandboxSync({
  engine, workspaceId, sessionId, setSessionId, history, isAuthenticated, authChecked,
  showToast, registry, pan, zoom, setPan, setZoom, getWorkspaceSnapshot,
}: UseSandboxSyncProps) {
  const repository = engine.repository as WorkspaceRepository | undefined;
  const [syncStatus, setSyncStatus] = useState<SandboxSyncStatus>('idle');
  const [realtimeEpoch, setRealtimeEpoch] = useState(0);
  const [eventLog, setEventLog] = useState<Array<{ time: string; event: string; detail: string }>>([]);
  const stateVersionRef = useRef(0);
  const lifecycleRef = useRef<SandboxSyncStatus>('idle');
  const hydrated = useRef(false);
  const cancelledRef = useRef(false);
  const incomingEvents = useRef<RealtimeEvent[]>([]);
  const pendingEvents = useRef<SandboxEventCommand[]>([]);
  const queueStorageKey = workspaceId ? `ailab_pending_events_${workspaceId}` : null;
  const persistenceQueue = useRef(Promise.resolve());
  const saveTimer = useRef<number | null>(null);
  const realtimeRef = useRef<ReturnType<typeof connectWorkspaceRealtime> | null>(null);
  const [, setVersionTick] = useState(0);

  const setLifecycle = useCallback((status: SandboxSyncStatus) => {
    lifecycleRef.current = status;
    setSyncStatus(status);
  }, []);

  const applyWorkspaceState = useCallback((state: WorkspaceState) => {
    engine.workspace.scene.objects.clear();
    engine.workspace.scene.connections.clear();
    for (const item of state.items ?? []) {
      const data = record(item);
      try {
        const object = registry.create(String(data.type ?? data.equipmentType ?? 'unsupported'), { id: String(data.id) });
        object.position.x = Number(data.x ?? record(data.position).x ?? 0);
        object.position.y = Number(data.y ?? record(data.position).y ?? 0);
        if (typeof data.displayName === 'string' || typeof data.name === 'string') object.metadata.displayName = String(data.displayName ?? data.name);
        if (data.scale !== undefined) object.scale.x = object.scale.y = Number(data.scale);
        if (data.rotation !== undefined) object.rotation = Number(data.rotation);
        object.state = typeof data.operation === 'string' ? data.operation : object.state;
        object.properties = { ...object.properties, ...record(data.properties) };
        if (data.material && typeof data.material === 'object') object.material = data.material as UnknownRecord;
        if (Array.isArray(data.contents)) object.contents = data.contents as UnknownRecord[];
        engine.workspace.scene.add(object);
      } catch (error) { console.error('Failed to restore workspace item', error); }
    }
    for (const connection of state.connections ?? []) {
      if (!isConnectionSnapshot(connection)) continue;
      try { engine.workspace.scene.connect(connection); } catch (error) { console.error('Failed to restore workspace connection', error); }
    }
    const viewport = record(state.viewport);
    const position = record(viewport.position);
    setPan({ x: Number(position.x ?? viewport.x ?? 0), y: Number(position.y ?? viewport.y ?? 0) });
    setZoom(Number(viewport.zoom ?? 1));
    stateVersionRef.current = state.stateVersion;
    setVersionTick((value) => value + 1);
    setSessionId(state.sessionId ?? null);
    engine.notifyUpdate();
  }, [engine, registry, setPan, setSessionId, setZoom]);

  const applyRealtimeDelta = useCallback((event: RealtimeEvent) => {
    const version = Number(event.stateVersion);
    if (!Number.isFinite(version) || version <= stateVersionRef.current) return;
    const delta = event.stateDelta ?? {};
    const rawItem = [delta.updatedItem, delta.updatedEquipment, delta.addedItem, delta.sourceItem, delta.targetItem].find(Boolean);
    const item = record(rawItem);
    if (item.id) {
      const id = String(item.id);
      let object = engine.workspace.scene.objects.get(id);
      if (!object) {
        try { object = registry.create(String(item.type ?? item.equipmentType ?? 'unsupported'), { id }); engine.workspace.scene.add(object); } catch { object = undefined; }
      }
      if (object) {
        if (item.x !== undefined) object.position.x = Number(item.x);
        if (item.y !== undefined) object.position.y = Number(item.y);
        if (item.scale !== undefined) object.scale.x = object.scale.y = Number(item.scale);
        if (item.scaleX !== undefined) object.scale.x = Number(item.scaleX);
        if (item.scaleY !== undefined) object.scale.y = Number(item.scaleY);
        if (item.rotation !== undefined) object.rotation = Number(item.rotation);
        if (typeof item.operation === 'string') object.state = item.operation;
        for (const key of ['temperatureC', 'targetTemperatureC', 'volumeMl', 'liquidLevel', 'pressureBar', 'massG', 'moles', 'molarAmount']) {
          if (item[key] !== undefined) object.properties[key] = Number(item[key]);
        }
        if (item.material && typeof item.material === 'object') object.material = item.material as UnknownRecord;
        if (Array.isArray(item.contents)) object.contents = item.contents as UnknownRecord[];
      }
    }
    if (delta.deletedItemId) engine.workspace.scene.remove(String(delta.deletedItemId));
    const connection = delta.addedConnection;
    if (connection && isConnectionSnapshot(connection) && !engine.workspace.scene.connections.has(connection.id)) {
      try { engine.workspace.scene.connect(connection); } catch { setLifecycle('conflict'); return; }
    }
    if (delta.disconnectedId) engine.workspace.scene.connections.delete(String(delta.disconnectedId));
    stateVersionRef.current = version;
    history.clear();
    setVersionTick((value) => value + 1);
    setEventLog((current) => [...current.slice(-19), { time: new Date().toLocaleTimeString(), event: 'REMOTE_EVENT', detail: `v${version}` }]);
    engine.notifyUpdate();
  }, [engine, history, registry, setLifecycle]);

  const reconcile = useCallback(async () => {
    if (!workspaceId || !repository) return;
    setLifecycle('reconciling');
    try {
      const state = await retry(() => repository.getState(workspaceId));
      applyWorkspaceState(state);
      history.clear();
      setLifecycle('ready');
    } catch (error) {
      setLifecycle('conflict');
      showToast(error instanceof Error ? error.message : 'Workspace conflict could not be resolved', 'error');
    }
  }, [applyWorkspaceState, history, repository, setLifecycle, showToast, workspaceId]);

  const processRealtimeEvent = useCallback(async (event: RealtimeEvent) => {
    const version = Number(event.stateVersion);
    if (!Number.isFinite(version)) return;
    if (lifecycleRef.current !== 'ready') { incomingEvents.current.push(event); return; }
    if (version > stateVersionRef.current + 1) {
      incomingEvents.current.push(event);
      await reconcile();
      const queued = [...incomingEvents.current].sort((a, b) => Number(a.stateVersion) - Number(b.stateVersion));
      incomingEvents.current = [];
      for (const queuedEvent of queued) applyRealtimeDelta(queuedEvent);
      return;
    }
    applyRealtimeDelta(event);
  }, [applyRealtimeDelta, reconcile]);

  const drainIncomingEvents = useCallback(async () => {
    const queued = [...incomingEvents.current].sort((a, b) => Number(a.stateVersion) - Number(b.stateVersion));
    incomingEvents.current = [];
    for (const event of queued) await processRealtimeEvent(event);
  }, [processRealtimeEvent]);

  const flushPendingEvents = useCallback(async () => {
    if (!workspaceId || !repository || !hydrated.current || pendingEvents.current.length === 0) return;
    while (pendingEvents.current.length > 0) {
      const event = pendingEvents.current[0];
      try {
        const ack = await retry(() => repository.appendEvent(workspaceId, { ...event, expectedVersion: stateVersionRef.current }));
        stateVersionRef.current = ack.stateVersion;
        pendingEvents.current.shift();
        if (queueStorageKey) localStorage.setItem(queueStorageKey, JSON.stringify(pendingEvents.current));
      } catch (error) {
        setLifecycle(isConflictError(error) ? 'conflict' : 'offline');
        return;
      }
    }
    setLifecycle('saved');
  }, [queueStorageKey, repository, setLifecycle, workspaceId]);

  const queueWorkspaceEvent = useCallback((eventType: string, payload: UnknownRecord) => {
    // Keep the local Recent log useful even in a standalone/offline Sandbox
    // without a workspace query parameter. Persistence remains conditional.
    setEventLog((current) => [...current.slice(-39), { time: new Date().toLocaleTimeString(), event: eventType, detail: String(payload.message ?? payload.itemId ?? payload.materialId ?? '') }]);
    if (!workspaceId || !hydrated.current) return;
    pendingEvents.current.push({ clientEventId: crypto.randomUUID(), expectedVersion: stateVersionRef.current, eventType, payload });
    if (queueStorageKey) localStorage.setItem(queueStorageKey, JSON.stringify(pendingEvents.current));
    setLifecycle('saving');
    persistenceQueue.current = persistenceQueue.current.catch(() => undefined).then(flushPendingEvents);
  }, [flushPendingEvents, queueStorageKey, setLifecycle, workspaceId]);

  const historyAction = useCallback(async (direction: 'undo' | 'redo') => {
    if (workspaceId && repository?.[direction]) {
      try { setLifecycle('reconciling'); const state = await retry(() => repository[direction]!(workspaceId, stateVersionRef.current)); applyWorkspaceState(state); history.clear(); setLifecycle('ready'); showToast(direction === 'undo' ? 'Undone' : 'Redone', 'info'); }
      catch (error) { setLifecycle('conflict'); showToast(error instanceof Error ? error.message : 'History operation failed', 'error'); }
      return;
    }
    if (direction === 'undo') history.undo(); else history.redo();
    engine.notifyUpdate();
    setLifecycle('saved');
  }, [applyWorkspaceState, engine, history, repository, setLifecycle, showToast, workspaceId]);

  useEffect(() => {
    if (!workspaceId || !authChecked || !isAuthenticated || hydrated.current || !repository) return;
    let cancelled = false;
    cancelledRef.current = false;
    setLifecycle('loading');
    if (queueStorageKey) {
      try {
        const stored = JSON.parse(localStorage.getItem(queueStorageKey) ?? '[]') as unknown;
        if (Array.isArray(stored)) pendingEvents.current = stored.filter((item): item is SandboxEventCommand => {
          const event = record(item);
          return typeof event.clientEventId === 'string' && typeof event.eventType === 'string' && typeof event.payload === 'object';
        });
      } catch { pendingEvents.current = []; }
    }
    void retry(() => repository.getState(workspaceId)).then(async (state) => {
      if (cancelled || cancelledRef.current) return;
      setLifecycle('hydrating');
      applyWorkspaceState(state);
      hydrated.current = true;
      setLifecycle('ready');
      await drainIncomingEvents();
      await flushPendingEvents();
    }).catch((error: unknown) => {
      if (!cancelled) { hydrated.current = true; setLifecycle('offline'); showToast(error instanceof Error ? error.message : 'Workspace state could not be loaded', 'error'); }
    });
    return () => { cancelled = true; cancelledRef.current = true; };
  }, [applyWorkspaceState, authChecked, drainIncomingEvents, flushPendingEvents, isAuthenticated, queueStorageKey, repository, setLifecycle, showToast, workspaceId]);

  useEffect(() => {
    if (!workspaceId || !authChecked || !isAuthenticated || !hydrated.current || !repository) return;
    if (saveTimer.current !== null) window.clearTimeout(saveTimer.current);
    saveTimer.current = window.setTimeout(() => {
      const viewport = { position: { x: pan.x, y: pan.y }, zoom };
      setLifecycle('saving');
      persistenceQueue.current = persistenceQueue.current.catch(() => undefined).then(() => retry(() => repository.autosave?.(workspaceId, { expectedVersion: stateVersionRef.current, stateHash: `${pan.x}:${pan.y}:${zoom}`, state: { viewport } }) ?? Promise.resolve({ stateVersion: stateVersionRef.current, savedAt: new Date().toISOString() }))).then((ack) => { stateVersionRef.current = ack.stateVersion; setLifecycle('saved'); }).catch((error: unknown) => setLifecycle(isConflictError(error) ? 'conflict' : 'offline'));
    }, 700);
    return () => { if (saveTimer.current !== null) window.clearTimeout(saveTimer.current); };
  }, [authChecked, isAuthenticated, pan.x, pan.y, repository, setLifecycle, workspaceId, zoom]);

  useEffect(() => {
    if (!workspaceId || !authChecked || !isAuthenticated || lifecycleRef.current !== 'ready') return;
    const connection = connectWorkspaceRealtime(workspaceId, sessionId, {
      onWorkspaceEvent: (event) => { const parsed = asRealtimeEvent(event); if (parsed) void processRealtimeEvent(parsed); },
      onAck: (event) => { const ack = record(event) as Partial<WorkspaceEventAck>; if (typeof ack.stateVersion === 'number') stateVersionRef.current = Math.max(stateVersionRef.current, ack.stateVersion); },
      onError: (event) => {
        const error = record(event);
        setLifecycle('offline');
        if (typeof error.message === 'string') showToast(error.message, 'error');
        window.setTimeout(() => { setLifecycle('ready'); setRealtimeEpoch((value) => value + 1); }, 1000);
      },
    });
    realtimeRef.current = connection;
    return () => { connection.close(); realtimeRef.current = null; };
  }, [authChecked, isAuthenticated, processRealtimeEvent, realtimeEpoch, sessionId, setLifecycle, showToast, workspaceId]);

  useEffect(() => {
    const online = () => { setLifecycle('ready'); setRealtimeEpoch((value) => value + 1); void flushPendingEvents(); };
    window.addEventListener('online', online);
    return () => window.removeEventListener('online', online);
  }, [flushPendingEvents, setLifecycle]);

  return { syncStatus, eventLog, queueWorkspaceEvent, applyWorkspaceState, historyAction, stateVersionRef, getWorkspaceSnapshot };
}
