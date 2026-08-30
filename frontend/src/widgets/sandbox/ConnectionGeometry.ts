import type { Vector2 } from '@/engine/core/types';

export type ConnectionCurve = { points: Vector2[]; bend?: number };

export function connectionPath(curve: ConnectionCurve) {
  const pts = curve.points;
  if (pts.length < 2) return '';
  if (pts.length === 2) {
    const start = pts[0];
    const end = pts[1];
    const dx = end.x - start.x;
    const direction = dx >= 0 ? 1 : -1;
    const bend = Math.max(36, Math.abs(dx) * 0.35) * direction + (curve.bend || 0);
    const c1 = { x: start.x + bend, y: start.y };
    const c2 = { x: end.x - bend, y: end.y };
    return `M ${start.x} ${start.y} C ${c1.x} ${c1.y}, ${c2.x} ${c2.y}, ${end.x} ${end.y}`;
  }
  
  // Basic path for >2 points
  let path = `M ${pts[0].x} ${pts[0].y}`;
  for (let i = 1; i < pts.length; i++) {
    const p1 = pts[i - 1];
    const p2 = pts[i];
    const dx = p2.x - p1.x;
    const direction = dx >= 0 ? 1 : -1;
    const bend = Math.max(36, Math.abs(dx) * 0.35) * direction;
    
    if (i === 1) {
      const cx = p1.x + bend;
      path += ` C ${cx} ${p1.y}, ${p2.x} ${p1.y}, ${p2.x} ${p2.y}`;
    } else if (i === pts.length - 1) {
      const cx = p2.x - bend;
      path += ` C ${p1.x} ${p2.y}, ${cx} ${p2.y}, ${p2.x} ${p2.y}`;
    } else {
      path += ` L ${p2.x} ${p2.y}`;
    }
  }
  return path;
}
