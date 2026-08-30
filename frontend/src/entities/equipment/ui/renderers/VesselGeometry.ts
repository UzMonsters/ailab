export function calculateLiquidLevel(
  fillRatio: number,
  minY: number,
  maxY: number
): number {
  return maxY - fillRatio * (maxY - minY);
}
