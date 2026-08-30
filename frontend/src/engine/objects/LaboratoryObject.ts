import type { BoundingBox, Capabilities, LaboratoryObjectSnapshot, PortDefinition, Vector2 } from '../core/types';

const copy = <T,>(value: T): T => structuredClone(value);

export class LaboratoryObject {
  readonly id: string;
  type: string;
  capabilities: Capabilities;
  position: Vector2;
  rotation = 0;
  scale: Vector2 = { x: 1, y: 1 };
  boundingBox: BoundingBox;
  zIndex = 0;
  visible = true;
  selected = false;
  hovered = false;
  locked = false;
  state = 'idle';
  material?: Record<string, unknown>;
  properties: Record<string, unknown> = {};
  ports: PortDefinition[];
  connections: string[] = [];
  contents: Record<string, unknown>[] = [];
  animations: string[] = [];
  history: string[] = [];
  metadata: Record<string, unknown> = {};

  constructor(definition: { id?: string; type: string; capabilities?: Capabilities; position?: Vector2; size: { width: number; height: number }; ports?: PortDefinition[] }) {
    this.id = definition.id ?? crypto.randomUUID();
    this.type = definition.type;
    this.capabilities = definition.capabilities ?? {};
    this.position = definition.position ?? { x: 0, y: 0 };
    this.boundingBox = { ...this.position, ...definition.size };
    this.ports = copy(definition.ports ?? []);
  }

  move(position: Vector2) { if (!this.locked) this.position = { ...position }; return this; }
  rotate(degrees: number) { if (!this.locked) this.rotation = (this.rotation + degrees) % 360; return this; }
  resize(size: { width: number; height: number }) { if (!this.locked) this.boundingBox = { ...this.boundingBox, ...size }; return this; }
  duplicate() { return LaboratoryObject.deserialize({ ...this.serialize(), id: crypto.randomUUID(), position: { x: this.position.x + 24, y: this.position.y + 24 }, selected: false }); }
  destroy() { this.visible = false; this.state = 'destroyed'; return this; }
  update(patch: Partial<Pick<LaboratoryObjectSnapshot, 'position' | 'rotation' | 'scale' | 'properties' | 'state'>>) { Object.assign(this, patch); return this; }
  render() { return this.serialize(); }
  serialize(): LaboratoryObjectSnapshot { return copy({ ...this, boundingBox: { width: this.boundingBox.width, height: this.boundingBox.height }, position: { ...this.position } }); }
  static deserialize(snapshot: LaboratoryObjectSnapshot) { 
    const object = new LaboratoryObject({ ...snapshot, size: snapshot.boundingBox, capabilities: snapshot.capabilities }); 
    Object.assign(object, copy(snapshot)); 
    // Legacy scale migration
    if (typeof object.scale === 'number') {
      object.scale = { x: object.scale as number, y: object.scale as number };
    }
    return object; 
  }
}
