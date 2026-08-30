import type { Command } from './CommandHistory';
import { LaboratoryObject } from '../objects/LaboratoryObject';
import type { Scene, SceneSnapshot } from '../scene/Scene';

const clone = <T,>(value: T): T => structuredClone(value);

function capture(scene: Scene): SceneSnapshot {
  return clone(scene.serialize());
}

function restore(scene: Scene, snapshot: SceneSnapshot) {
  scene.objects.clear();
  scene.connections.clear();
  for (const object of snapshot.objects) {
    // The deserializer is the single migration boundary for persisted objects.
    scene.objects.set(object.id, LaboratoryObject.deserialize(object));
  }
  for (const connection of snapshot.connections) scene.connections.set(connection.id, clone(connection));
  scene.camera = clone(snapshot.camera);
  scene.selection = new Set(snapshot.selection);
  for (const object of scene.objects.values()) object.selected = scene.selection.has(object.id);
  scene.onUpdate?.();
}

function captureBeforePosition(scene: Scene, objectId: string, from: { x: number; y: number }): SceneSnapshot {
  const current = capture(scene);
  const object = scene.objects.get(objectId);
  if (object) object.position = { x: from.x, y: from.y };
  const before = capture(scene);
  if (object) {
    const currentObject = current.objects.find((item) => item.id === objectId);
    object.position = currentObject?.position ?? object.position;
  }
  return before;
}

/** A reversible domain action. Undo/redo always restores the complete scene. */
export class SceneSnapshotCommand implements Command {
  readonly label: string;
  private readonly before: SceneSnapshot;
  private after?: SceneSnapshot;

  constructor(
    private readonly scene: Scene,
    label: string,
    private readonly mutation: () => void,
    private readonly mutationAlreadyApplied = false,
    beforeSnapshot?: SceneSnapshot,
  ) {
    this.label = label;
    this.before = beforeSnapshot ? clone(beforeSnapshot) : capture(scene);
  }

  execute() {
    if (this.after) {
      restore(this.scene, this.after);
      return;
    }
    if (!this.mutationAlreadyApplied) this.mutation();
    this.after = capture(this.scene);
    this.scene.onUpdate?.();
  }

  undo() { restore(this.scene, this.before); }
}

export class AddItemCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, object: LaboratoryObject) {
    super(scene, `Add ${object.type}`, () => scene.add(object));
  }
}

export class RemoveItemCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, objectId: string) {
    const object = scene.objects.get(objectId);
    if (!object) throw new Error(`Object ${objectId} not found`);
    super(scene, `Remove ${object.type}`, () => scene.remove(objectId));
  }
}

export class MoveItemCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, objectId: string, from: { x: number; y: number }, to: { x: number; y: number }) {
    super(scene, 'Move', () => {
      const object = scene.objects.get(objectId);
      if (object) object.position = { x: to.x, y: to.y };
    }, true, captureBeforePosition(scene, objectId, from));
  }
}

export class ConnectCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, connection: SceneSnapshot['connections'][number]) {
    super(scene, `Connect ${connection.medium ?? connection.type}`, () => scene.connect(connection));
  }
}

export class DisconnectCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, connectionId: string) {
    if (!scene.connections.has(connectionId)) throw new Error(`Connection ${connectionId} not found`);
    super(scene, 'Disconnect', () => scene.connections.delete(connectionId));
  }
}

export class RouteEditCommand extends SceneSnapshotCommand {
  private readonly beforeRoute: { x: number; y: number }[];
  private readonly afterRoute: { x: number; y: number }[];
  constructor(private readonly sceneRef: Scene, private readonly connectionId: string, before: { x: number; y: number }[], after: { x: number; y: number }[], public readonly label = 'Edit route') {
    super(sceneRef, label, () => undefined, true);
    this.beforeRoute = clone(before);
    this.afterRoute = clone(after);
  }
  execute() {
    const connection = this.sceneRef.connections.get(this.connectionId);
    if (connection) this.sceneRef.connections.set(this.connectionId, { ...connection, routePoints: clone(this.afterRoute) });
    this.sceneRef.onUpdate?.();
  }
  undo() {
    const connection = this.sceneRef.connections.get(this.connectionId);
    if (connection) this.sceneRef.connections.set(this.connectionId, { ...connection, routePoints: clone(this.beforeRoute) });
    this.sceneRef.onUpdate?.();
  }
}

export class AddMaterialCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, objectId: string, materialName: string, amountMl: number) {
    super(scene, `Add ${materialName} (${amountMl}mL)`, () => undefined, true);
    void objectId;
  }
}

export class MaterialEditCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, label: string, mutation: () => void) {
    super(scene, label, mutation);
  }
}

export class MaterialRemoveCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, objectId: string, materialId: string, label = 'Remove material') {
    super(scene, label, () => {
      const object = scene.objects.get(objectId);
      if (!object) return;
      object.contents = object.contents.filter((content) => String(content.materialId ?? content.formula ?? '') !== materialId);
      object.material = object.contents[0] as Record<string, unknown> | undefined;
      object.properties.volumeMl = object.contents.filter((content) => content.unit === 'mL').reduce((sum, content) => sum + Number(content.amount ?? 0), 0);
      object.properties.massG = object.contents.filter((content) => content.unit === 'g').reduce((sum, content) => sum + Number(content.amount ?? 0), 0);
      object.properties.moles = object.contents.reduce((sum, content) => sum + Number(content.molarAmount ?? (content.unit === 'mol' ? content.amount : 0)), 0);
    });
  }
}

export class PourCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, label: string, pour: () => void, alreadyApplied = false) {
    super(scene, label, pour, alreadyApplied);
  }
}

export class OperationCommand extends SceneSnapshotCommand {
  constructor(scene: Scene, objectId: string, operation: string) {
    super(scene, operation, () => {
      const object = scene.objects.get(objectId);
      if (object) object.state = operation;
    });
  }
}
