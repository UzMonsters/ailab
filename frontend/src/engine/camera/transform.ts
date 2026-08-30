export type CameraState = { x: number; y: number; zoom: number };

/** The single screen/world conversion used by canvas, hit testing and gestures. */
export function screenToWorld(point: { x: number; y: number }, camera: CameraState) {
  return { x: (point.x - camera.x) / camera.zoom, y: (point.y - camera.y) / camera.zoom };
}

export function worldToScreen(point: { x: number; y: number }, camera: CameraState) {
  return { x: (point.x + camera.x) * camera.zoom, y: (point.y + camera.y) * camera.zoom };
}
