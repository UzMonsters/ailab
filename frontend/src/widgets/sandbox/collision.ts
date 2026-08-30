export type CollisionRect = { x: number; y: number; width: number; height: number };

export function rectanglesOverlap(a: CollisionRect, b: CollisionRect): boolean {
  return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
}

export function canPlace(rect: CollisionRect, others: CollisionRect[]): boolean {
  return others.every((other) => !rectanglesOverlap(rect, other));
}
