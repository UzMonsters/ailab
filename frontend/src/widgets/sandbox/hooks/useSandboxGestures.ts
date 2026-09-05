import { useState, useRef, useEffect } from "react";
import type { Item, EquipmentType } from "../types";
import { isVessel } from "../types";
import type { CommandHistory } from "@/engine/history/CommandHistory";
import { MoveItemCommand } from "@/engine/history/SandboxCommands";
import type { Engine } from "@/engine/core/Engine";
import { moveObjectWithAttachedChildren } from "../sandboxActions";
import { canPlace } from "../collision";

interface UseSandboxGesturesProps {
  canvasRef: React.RefObject<HTMLDivElement | null>;
  items: Item[];
  engine: Engine | null;
  history: CommandHistory;
  connectSource: string | null;
  connectSourcePort: { itemId: string; portId: string } | null;
  queueWorkspaceEvent: (eventType: string, payload: Record<string, unknown>) => void;
  updateItem: (id: string, patch: Partial<Item>) => void;
  setSelectedIds: (ids: Set<string>) => void;
  selectedIds: Set<string>;
  setConnectionPointer: (pointer: { x: number; y: number } | null) => void;
  setConnectionSnap: (snap: { itemId: string; portId: string; x: number; y: number } | null) => void;
  setConnectSource: (source: string | null) => void;
  setConnectSourcePort: (source: { itemId: string; portId: string } | null) => void;
  showToast: (msg: string, type?: 'info'|'error'|'success') => void;
  connectObjects: (fromId: string, toId: string) => boolean;
  connectFromPort: (fromId: string, fromPortId: string, toId: string) => boolean;
  tool: "select" | "pan" | "connect";
  setTool: (tool: "select" | "pan" | "connect") => void;
  zoom: number;
  pan: { x: number; y: number };
  setPan: (pan: { x: number; y: number }) => void;
  setCollisionItemId: (id: string | null) => void;
  marquee: { startX: number; startY: number; currentX: number; currentY: number } | null;
  setMarquee: (marquee: { startX: number; startY: number; currentX: number; currentY: number } | null) => void;
}

export function useSandboxGestures({
  canvasRef,
  items,
  engine,
  history,
  connectSource,
  connectSourcePort,
  queueWorkspaceEvent,
  updateItem,
  setSelectedIds,
  selectedIds,
  marquee,
  setMarquee,
  setConnectionPointer,
  setConnectionSnap,
  setConnectSource,
  setConnectSourcePort,
  showToast,
  connectObjects,
  connectFromPort,
  tool,
  setTool,
  zoom,
  pan,
  setPan,
  setCollisionItemId,
}: UseSandboxGesturesProps) {
  const dragRef = useRef<{ ids: string[]; initialPositions: Record<string, {x: number, y: number}>; startX: number; startY: number } | null>(null);
  const panRef = useRef<{
    x: number;
    y: number;
    startX: number;
    startY: number;
  } | null>(null);
  const pointerRef = useRef({ x: 0, y: 0 });
  const [isPanning, setIsPanning] = useState(false);

  const resetPan = () => {
    panRef.current = null;
    setIsPanning(false);
  };

  const endDrag = () => {
    const drag = dragRef.current;
    if (!drag) {
      resetPan();
      if (tool !== "connect") {
        setConnectSource(null);
        if (connectSourcePort) setConnectSourcePort(null);
        setConnectionSnap(null);
        setConnectionPointer(null);
      }
      if (canvasRef.current) canvasRef.current.style.cursor = tool === "pan" ? "move" : tool === "connect" ? "crosshair" : "default";
      return;
    }
    const heater = drag.ids.length === 1 ? items.find((item) => item.id === drag.ids[0]) : null;
    if (heater?.type === "burner" || heater?.type === "hotplate" || heater?.type === "magnetic_stirrer") {
      const nearest = items
        .filter(isVessel)
        .map((item) => ({
          item,
          distance: Math.hypot(item.x - heater.x, item.y - heater.y),
        }))
        .sort((a, b) => a.distance - b.distance)[0];
      if (nearest && nearest.distance < 130) {
        updateItem(heater.id, {
          x: nearest.item.x + nearest.item.w / 2 - heater.w / 2,
          y: nearest.item.y + nearest.item.h - heater.h * 0.1,
          attachedTo: nearest.item.id,
        });
        showToast(`${heater.name} установлен под ${nearest.item.name}`, 'info');
      }
    }
    const movedEquipment = drag.ids.length === 1 ? items.find((item) => item.id === drag.ids[0]) : null;
    const stand = items.find((item) => item.type === 'clampstand' || item.type === 'stand' || item.type === 'ringstand');
    const mountable = movedEquipment && ['burette', 'condenser', 'thermometer', 'separatory_funnel', 'flask', 'erlenmeyer', 'roundflask', 'testtube'].includes(movedEquipment.type);
    if (mountable && stand && Math.hypot(movedEquipment.x - stand.x, movedEquipment.y - stand.y) < 150) {
      updateItem(movedEquipment.id, {
        x: stand.x + stand.w * 0.72 - movedEquipment.w * 0.5,
        y: stand.y + stand.h * 0.28 - movedEquipment.h * 0.1,
        attachedTo: stand.id,
      });
      showToast(`${movedEquipment.name} закреплён на штативе`, 'info');
    }
    if (drag) {
      drag.ids.forEach(id => {
        const moved = items.find(item => item.id === id);
        const startPos = drag.initialPositions[id];
        if (moved && startPos && (moved.x !== startPos.x || moved.y !== startPos.y)) {
           queueWorkspaceEvent("ITEM_MOVED", { itemId: moved.id, x: moved.x, y: moved.y });
           history.execute(new MoveItemCommand(engine.workspace.scene, moved.id, { x: startPos.x, y: startPos.y }, { x: moved.x, y: moved.y }));
        }
      });
    }
    setCollisionItemId(null);
    dragRef.current = null;
    resetPan();
    if (canvasRef.current) canvasRef.current.style.cursor = tool === "pan" ? "move" : tool === "connect" ? "crosshair" : "default";
  };

  const onPointerDown = (
    event: React.PointerEvent<HTMLDivElement>,
    id: string,
  ) => {
    event.stopPropagation();

    // Pan is a left-button interaction; keep right-click available for the
    // canvas context menu and never start a hidden drag from it.
    if (event.button !== 0) return;

    if (tool === "pan") {
      panRef.current = { x: pan.x, y: pan.y, startX: event.clientX, startY: event.clientY };
      setIsPanning(true);
      event.currentTarget.setPointerCapture(event.pointerId);
      if (canvasRef.current) canvasRef.current.style.cursor = "grabbing";
      return;
    }

    let currentSelection = selectedIds;
    if (tool === "select") {
      if (event.shiftKey) {
        currentSelection = new Set(selectedIds);
        if (currentSelection.has(id)) currentSelection.delete(id);
        else currentSelection.add(id);
        setSelectedIds(currentSelection);
      } else if (!selectedIds.has(id)) {
        currentSelection = new Set([id]);
        setSelectedIds(currentSelection);
      }
    }

    if (tool === "connect") {
      if (connectSource && connectSource !== id) {
        connectObjects(connectSource, id);
        setConnectSource(null);
        setConnectSourcePort(null);
        setConnectionPointer(null);
        setConnectionSnap(null);
        return;
      }
      setConnectSource(id);
      setConnectSourcePort(null);
      const item = items.find((value) => value.id === id);
      if (item) {
        setConnectionPointer({ x: item.x + item.w / 2, y: item.y + item.h / 2 });
      }
      return;
    }
    const bounds = canvasRef.current?.getBoundingClientRect();
    if (!bounds) return;

    event.currentTarget.setPointerCapture(event.pointerId);
    if (canvasRef.current) canvasRef.current.style.cursor = "grabbing";
    
    const dragIds = Array.from(currentSelection);
    const initialPositions: Record<string, {x: number, y: number}> = {};
    dragIds.forEach(did => {
      const it = items.find(v => v.id === did);
      if (it) initialPositions[did] = { x: it.x, y: it.y };
    });
    
    dragRef.current = {
      ids: dragIds,
      initialPositions,
      startX: (event.clientX - bounds.left - pan.x) / zoom,
      startY: (event.clientY - bounds.top - pan.y) / zoom,
    };
  };

  const onPointerMove = (event: React.PointerEvent<HTMLDivElement>) => {
    pointerRef.current = { x: event.clientX, y: event.clientY };
    const bounds = canvasRef.current?.getBoundingClientRect();
    if (tool === "connect" && connectSource && bounds) {
      setConnectionPointer({
        x: (event.clientX - bounds.left - pan.x) / zoom,
        y: (event.clientY - bounds.top - pan.y) / zoom,
      });
    }
    // Pan only starts from the empty canvas. Equipment clicks are intentionally
    // ignored in this mode, so they cannot accidentally move an item.
    if (tool === "pan" && panRef.current) {
      setPan({
        x: panRef.current.x + (event.clientX - panRef.current.startX) / zoom,
        y: panRef.current.y + (event.clientY - panRef.current.startY) / zoom,
      });
      return;
    }

    const drag = dragRef.current;
    if (drag && bounds && engine) {
      let dx = (event.clientX - bounds.left - pan.x) / zoom - drag.startX;
      let dy = (event.clientY - bounds.top - pan.y) / zoom - drag.startY;

      // Group drag constraint checks - ensure NO item in the group goes out of bounds
      let minXOffset = 0, minYOffset = 0, maxXOffset = 0, maxYOffset = 0;
      drag.ids.forEach(did => {
         const it = items.find(i => i.id === did);
         const initPos = drag.initialPositions[did];
         if (it && initPos) {
           const nextX = initPos.x + dx;
           const nextY = initPos.y + dy;
           const itemWidth = it.w * (it.scaleX ?? it.scale);
           const itemHeight = it.h * (it.scaleY ?? it.scale);
           const minXLimit = Math.max(0, -pan.x / zoom);
           const minYLimit = Math.max(70, -pan.y / zoom);
           const maxXLimit = Math.max(minXLimit, (bounds.width - pan.x) / zoom - itemWidth);
           const maxYLimit = Math.max(minYLimit, (bounds.height - pan.y) / zoom - itemHeight);
           
           if (nextX < minXLimit) minXOffset = Math.max(minXOffset, minXLimit - nextX);
           if (nextX > maxXLimit) maxXOffset = Math.max(maxXOffset, nextX - maxXLimit);
           if (nextY < minYLimit) minYOffset = Math.max(minYOffset, minYLimit - nextY);
           if (nextY > maxYLimit) maxYOffset = Math.max(maxYOffset, nextY - maxYLimit);
         }
      });
      
      dx = dx + minXOffset - maxXOffset;
      dy = dy + minYOffset - maxYOffset;
      
      drag.ids.forEach(did => {
         const initPos = drag.initialPositions[did];
         if (initPos) {
           moveObjectWithAttachedChildren(
             engine.workspace.scene,
             did,
             initPos.x + dx,
             initPos.y + dy,
           );
         }
      });
      
      engine.notifyUpdate();
      return;
    }
    
    // Marquee logic
    if (marquee && bounds) {
       const x = (event.clientX - bounds.left);
       const y = (event.clientY - bounds.top);
       setMarquee({ ...marquee, currentX: x, currentY: y });
       
       // calculate intersection
       const mMinX = Math.min(marquee.startX, x) / zoom - pan.x / zoom;
       const mMaxX = Math.max(marquee.startX, x) / zoom - pan.x / zoom;
       const mMinY = Math.min(marquee.startY, y) / zoom - pan.y / zoom;
       const mMaxY = Math.max(marquee.startY, y) / zoom - pan.y / zoom;
       
       const newSelection = new Set<string>();
       items.forEach(item => {
          const itemWidth = item.w * (item.scaleX ?? item.scale);
          const itemHeight = item.h * (item.scaleY ?? item.scale);
          if (
            item.x < mMaxX &&
            item.x + itemWidth > mMinX &&
            item.y < mMaxY &&
            item.y + itemHeight > mMinY
          ) {
            newSelection.add(item.id);
          }
       });
       setSelectedIds(newSelection);
       return;
    }
  };

  // ─── Global pointerup fallback ────────────────────────────────────────────
  // When the pointer is released outside the canvas (e.g., over a panel or
  // outside the browser window), the canvas-level onPointerUp never fires.
  // We listen on window to guarantee dragRef is always cleaned up.
  useEffect(() => {
    const handleWindowPointerUp = () => {
      if (dragRef.current) {
        endDrag();
      }
      if (marquee) { setMarquee(null); }
      if (panRef.current) {
        resetPan();
        if (canvasRef.current) canvasRef.current.style.cursor = tool === 'pan' ? 'move' : tool === 'connect' ? 'crosshair' : 'default';
      }
    };
    window.addEventListener('pointerup', handleWindowPointerUp);
    window.addEventListener('pointercancel', handleWindowPointerUp);
    return () => {
      window.removeEventListener('pointerup', handleWindowPointerUp);
      window.removeEventListener('pointercancel', handleWindowPointerUp);
    };
  }, [canvasRef, tool]);

  

  useEffect(() => {
    if (canvasRef.current)
      canvasRef.current.style.cursor =
        tool === "connect" ? "crosshair" : tool === "pan" ? "move" : "default";
  }, [tool, canvasRef]);

  
  const onCanvasPointerDown = (event: React.PointerEvent<HTMLDivElement>) => {
    if (event.button === 0) {
      if (tool === 'pan') {
        panRef.current = {
          x: pan.x,
          y: pan.y,
          startX: event.clientX,
          startY: event.clientY,
        };
        setIsPanning(true);
        event.currentTarget.setPointerCapture(event.pointerId);
        if (canvasRef.current) canvasRef.current.style.cursor = 'grabbing';
      } else if (tool === 'select') {
        const bounds = canvasRef.current?.getBoundingClientRect();
        if (bounds) {
          const x = event.clientX - bounds.left;
          const y = event.clientY - bounds.top;
          setMarquee({ startX: x, startY: y, currentX: x, currentY: y });
          if (!event.shiftKey) setSelectedIds(new Set());
          event.currentTarget.setPointerCapture(event.pointerId);
        }
      }
    }
  };

  return { onCanvasPointerDown, endDrag, isPanning,
    onPointerDown,
    onPointerMove,
  };
}
