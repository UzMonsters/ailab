import { useState, useCallback, useRef, useEffect } from 'react';
import { useTranslations } from 'next-intl';
import { useToast } from "@/shared/ui/ToastContainer";
import type { Engine as LabEngine } from '@/engine/core/Engine';
import type { Item } from '@/widgets/sandbox/types';
import { isLiquidConduit, isVessel } from '@/widgets/sandbox/types';
import { getTransferAnimationProfile, type TransferAnimationKind } from '@/widgets/sandbox/animationProfiles';

export function usePour(engine: LabEngine | null, items: Item[], queueWorkspaceEvent: (event: string, payload: any) => void, history: any) {
  const [pourSource, setPourSource] = useState<string | null>(null);
  const [pourAmount, setPourAmount] = useState(25);
  const [pourAnimation, setPourAnimation] = useState<{ sourceId: string; targetId: string; amountMl: number; kind: TransferAnimationKind; durationMs: number; arcLift: number; streamWidth: number } | null>(null);
  const [spillAnimation, setSpillAnimation] = useState<string | null>(null); // For overflow visuals
  
  const { addToast } = useToast();
  const ts = useTranslations("sandbox");
  const pourAnimationTimer = useRef<number | null>(null);
  const spillAnimationTimer = useRef<number | null>(null);

  useEffect(() => () => {
    if (pourAnimationTimer.current !== null) window.clearTimeout(pourAnimationTimer.current);
    if (spillAnimationTimer.current !== null) window.clearTimeout(spillAnimationTimer.current);
  }, []);

  const triggerPourAnimation = useCallback((sourceId: string, targetId: string, amount: number, overflowAmount: number = 0) => {
    if (pourAnimationTimer.current !== null) window.clearTimeout(pourAnimationTimer.current);
    const source = items.find((item) => item.id === sourceId);
    const profile = getTransferAnimationProfile(source, amount);
    setPourAnimation({ sourceId, targetId, amountMl: amount, ...profile });
    const durationMs = profile.durationMs;
    
    if (overflowAmount > 0) {
      if (spillAnimationTimer.current !== null) window.clearTimeout(spillAnimationTimer.current);
      setSpillAnimation(targetId);
      spillAnimationTimer.current = window.setTimeout(() => setSpillAnimation(null), durationMs + 1500);
    }
    
    pourAnimationTimer.current = window.setTimeout(() => {
      setPourAnimation((current) => (current?.sourceId === sourceId && current.targetId === targetId ? null : current));
      pourAnimationTimer.current = null;
    }, durationMs);
  }, [items]);

  const pour = useCallback((sourceId: string, targetId: string, customAmount: number) => {
    const source = items.find((item) => item.id === sourceId);
    const target = items.find((item) => item.id === targetId);
    if (!source?.material || (source.material.state !== "liquid" && source.material.state !== "aqueous") || !target || (!isVessel(target) && !isLiquidConduit(target))) {
      addToast(ts("pour.onlyLiquids"), "error");
      return;
    }
    
    const remainingCapacity = Math.max(0, (target.capacityMl ?? 0) - target.volumeMl);
    const availableAmount = Math.min(customAmount, source.volumeMl);
    
    if (availableAmount <= 0) {
      addToast(ts("pour.sourceEmpty"), "error");
      return;
    }

    const acceptedAmount = Math.min(availableAmount, remainingCapacity);
    const overflowAmount = Math.max(0, availableAmount - remainingCapacity);
    
    if (engine) {
      engine.fluid.startPour(source.id, target.id, availableAmount, 25 * Math.max(.5, Number(engine.workspace.simulation.speed ?? 1)));
      engine.notifyUpdate();

      queueWorkspaceEvent("POUR", { sourceId: source.id, targetId: target.id, materialId: source.material.id, amountMl: availableAmount, overflowAmount });
      triggerPourAnimation(source.id, target.id, acceptedAmount, overflowAmount);
      setPourSource(null);
      
      if (overflowAmount > 0) {
        addToast(ts("pour.overflow", { amount: overflowAmount.toFixed(1) }), "info");
      } else {
        addToast(ts("pour.success", { amount: acceptedAmount, name: target.name }), "success");
      }
    }
  }, [items, engine, queueWorkspaceEvent, triggerPourAnimation, addToast, ts]);

  return {
    pourSource, setPourSource,
    pourAmount, setPourAmount,
    pourAnimation, spillAnimation,
    pour, triggerPourAnimation
  };
}
