import { useState, useMemo, useCallback } from 'react';
import { useToast } from "@/shared/ui/ToastContainer";
import type { Engine as LabEngine } from '@/engine/core/Engine';
import type { Item } from '@/widgets/sandbox/types';
import { ConnectionEngine } from '@/engine/connections/ConnectionEngine';

export function useConnections(engine: LabEngine | null, items: Item[], setSelectedId: (id: string | null) => void, setTool: (tool: "select" | "pan" | "connect") => void) {
  const [connectSource, setConnectSource] = useState<string | null>(null);
  const [connectSourcePort, setConnectSourcePort] = useState<{ itemId: string; portId: string } | null>(null);
  const [connectionSnap, setConnectionSnap] = useState<{ itemId: string; portId: string; x: number; y: number } | null>(null);
  const [connectionDraft, setConnectionDraft] = useState<{ from: string; to: string } | null>(null);
  const [connectionPointer, setConnectionPointer] = useState<{ x: number; y: number } | null>(null);
  const [selectedConnectionId, setSelectedConnectionId] = useState<string | null>(null);
  
  const { addToast } = useToast();
  const connectionEngine = useMemo(() => new ConnectionEngine(), []);

  const cancelConnection = useCallback((preserveTool = false) => {
    if (!preserveTool) setTool("select");
    setConnectSource(null);
    setConnectSourcePort(null);
    setConnectionPointer(null);
    setConnectionSnap(null);
  }, [setTool]);

  const portCompatibility = useMemo(() => {
    const result: Record<string, "compatible" | "adapter" | "incompatible"> = {};
    if (!connectSourcePort || !engine) return result;
    const source = engine.workspace.scene.objects.get(connectSourcePort.itemId);
    for (const item of items) {
      for (const port of item.ports) {
        const key = `${item.id}:${port.id}`;
        if (item.id === connectSourcePort.itemId) result[key] = "incompatible";
        else if (source) {
          const validation = connectionEngine.validate(
            source,
            connectSourcePort.portId,
            engine.workspace.scene.objects.get(item.id)!,
            port.id
          );
          result[key] = validation.status === "compatible" ? "compatible" : "incompatible";
        } else result[key] = "incompatible";
      }
    }
    return result;
  }, [connectSourcePort, engine, items, connectionEngine]);

  const startPortConnection = useCallback((itemId: string, portId: string) => {
    if (connectSourcePort && connectSourcePort.itemId !== itemId) {
      // Defer to finish
      return;
    }
    setSelectedId(itemId);
    setTool("connect");
    setConnectSource(itemId);
    setConnectSourcePort({ itemId, portId });
    setConnectionSnap(null);
    addToast("Drag to a compatible port", "info");
  }, [connectSourcePort, setSelectedId, setTool, addToast]);

  const hoverPort = useCallback((itemId: string, portId: string, point: { x: number; y: number }) => {
    if (!connectSourcePort || connectSourcePort.itemId === itemId) return;
    setConnectionSnap({ itemId, portId, x: point.x, y: point.y });
    setConnectionPointer(point);
  }, [connectSourcePort]);

  return {
    connectSource, setConnectSource,
    connectSourcePort, setConnectSourcePort,
    connectionSnap, setConnectionSnap,
    connectionDraft, setConnectionDraft,
    connectionPointer, setConnectionPointer,
    selectedConnectionId, setSelectedConnectionId,
    portCompatibility,
    startPortConnection,
    hoverPort,
    cancelConnection,
    connectionEngine
  };
}
