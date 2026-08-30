import { Scene } from '../scene/Scene';
export class SelectionManager { constructor(private readonly scene: Scene) {} select(id: string, additive = false) { const ids = additive ? [...this.scene.selection, id] : [id]; this.scene.select([...new Set(ids)]); } clear() { this.scene.select([]); } }
