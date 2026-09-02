import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import { createRequire } from 'node:module';
import { fileURLToPath } from 'node:url';
import ts from 'typescript';

const require = createRequire(import.meta.url);
const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
require.extensions['.ts'] = (module, filename) => {
  const source = fs.readFileSync(filename, 'utf8');
  const output = ts.transpileModule(source, {
    fileName: filename,
    compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS, esModuleInterop: true },
  }).outputText;
  module._compile(output, filename);
};

const { Workspace } = require(path.join(root, 'src/engine/workspace/Workspace.ts'));
const { LaboratoryObject } = require(path.join(root, 'src/engine/objects/LaboratoryObject.ts'));
const { ThermalSystem } = require(path.join(root, 'src/engine/simulation/ThermalSystem.ts'));
const { FluidSystem } = require(path.join(root, 'src/engine/simulation/FluidSystem.ts'));
const { ReactionSystem } = require(path.join(root, 'src/engine/simulation/ReactionSystem.ts'));
const { CommandHistory } = require(path.join(root, 'src/engine/history/CommandHistory.ts'));
const { SceneSnapshotCommand, PourCommand, OperationCommand } = require(path.join(root, 'src/engine/history/SandboxCommands.ts'));
const { RouteEditCommand, MaterialRemoveCommand } = require(path.join(root, 'src/engine/history/SandboxCommands.ts'));
const { ConnectionEngine } = require(path.join(root, 'src/engine/connections/ConnectionEngine.ts'));
const { createDefaultEquipmentRegistry } = require(path.join(root, 'src/engine/registry/EquipmentRegistry.ts'));
const { canPlace } = require(path.join(root, 'src/widgets/sandbox/collision.ts'));
const { validateEquipmentContainment } = require(path.join(root, 'src/engine/simulation/SafetyValidation.ts'));

function object(type, options = {}) {
  const item = new LaboratoryObject({ id: options.id, type, capabilities: options.capabilities, ports: options.ports, size: { width: 100, height: 100 } });
  item.properties = { ...options.properties };
  item.contents = options.contents ?? [];
  return item;
}

function testHeatingAttachment() {
  const workspace = new Workspace();
  workspace.simulation.running = true;
  const vessel = object('beaker', { id: 'vessel', capabilities: { container: { capacity: 100 }, heatTarget: true }, properties: { temperature: 24.5, massG: 100 } });
  const burner = object('burner', { id: 'burner', capabilities: { thermalOutput: { powerW: 1200 } }, properties: { attachedTo: 'vessel' } });
  burner.state = 'heating';
  workspace.scene.add(vessel);
  workspace.scene.add(burner);
  new ThermalSystem(workspace).update(1);
  assert.ok(Number(vessel.properties.temperature) > 24.5, 'attached burner must heat the vessel');
}

function testCapacitySafePour() {
  const workspace = new Workspace();
  workspace.simulation.running = true;
  const source = object('beaker', { id: 'source', capabilities: { container: { capacity: 100 } }, properties: { volumeMl: 80, capacityMl: 100 }, contents: [{ materialId: 'water', phase: 'liquid', amount: 80 }] });
  const target = object('beaker', { id: 'target', capabilities: { container: { capacity: 100 } }, properties: { volumeMl: 50, capacityMl: 100 }, contents: [] });
  workspace.scene.add(source);
  workspace.scene.add(target);
  const fluid = new FluidSystem(workspace);
  assert.equal(fluid.pourNow('source', 'target', 60), true);
  assert.equal(source.properties.volumeMl, 30);
  assert.equal(target.properties.volumeMl, 100);
  assert.equal(fluid.pourNow('source', 'target', 1), false);
}

function testFullSnapshotHistory() {
  const workspace = new Workspace();
  const item = object('beaker', { id: 'item', properties: { volumeMl: 10, temperature: 25 } });
  workspace.scene.add(item);
  const history = new CommandHistory();
  history.execute(new SceneSnapshotCommand(workspace.scene, 'compound edit', () => {
    item.properties.volumeMl = 80;
    item.properties.temperature = 90;
    item.contents = [{ materialId: 'water', phase: 'liquid', amount: 80 }];
  }));
  history.undo();
  let restored = workspace.scene.objects.get('item');
  assert.equal(restored.properties.volumeMl, 10);
  assert.equal(restored.properties.temperature, 25);
  assert.deepEqual(restored.contents, []);
  history.redo();
  restored = workspace.scene.objects.get('item');
  assert.equal(restored.properties.volumeMl, 80);
  assert.equal(restored.properties.temperature, 90);
  assert.equal(restored.contents[0].amount, 80);
}

function testPourAndOperationHistory() {
  const workspace = new Workspace();
  const source = object('beaker', { id: 'source', capabilities: { container: { capacity: 100 } }, properties: { volumeMl: 80, capacityMl: 100 }, contents: [{ materialId: 'water', phase: 'liquid', amount: 80 }] });
  const target = object('beaker', { id: 'target', capabilities: { container: { capacity: 100 } }, properties: { volumeMl: 0, capacityMl: 100 }, contents: [] });
  workspace.scene.add(source);
  workspace.scene.add(target);
  const fluid = new FluidSystem(workspace);
  const history = new CommandHistory();
  history.execute(new PourCommand(workspace.scene, 'Pour 30mL', () => assert.equal(fluid.pourNow('source', 'target', 30), true)));
  assert.equal(workspace.scene.objects.get('target').properties.volumeMl, 30);
  history.undo();
  assert.equal(workspace.scene.objects.get('target').properties.volumeMl, 0);
  history.redo();
  assert.equal(workspace.scene.objects.get('target').properties.volumeMl, 30);
  history.execute(new OperationCommand(workspace.scene, 'target', 'heating'));
  assert.equal(workspace.scene.objects.get('target').state, 'heating');
  history.undo();
  assert.equal(workspace.scene.objects.get('target').state, 'idle');
}

function testGasFlowAndReaction() {
  const gasPort = (id) => ({ id, name: id, type: 'Gas', position: { x: 0.5, y: 0 }, direction: 'bidirectional' });
  const workspace = new Workspace();
  workspace.simulation.running = true;
  const source = object('flask', { id: 'gas-source', capabilities: { container: { capacity: 100 } }, ports: [gasPort('out')], properties: { volumeMl: 80, capacityMl: 100 }, contents: [{ materialId: 'H2', phase: 'gas', amount: 80 }] });
  const target = object('flask', { id: 'gas-target', capabilities: { container: { capacity: 100 } }, ports: [gasPort('in')], properties: { volumeMl: 0, capacityMl: 100 }, contents: [] });
  workspace.scene.add(source);
  workspace.scene.add(target);
  workspace.scene.connect({ id: 'gas-link', from: { objectId: source.id, portId: 'out' }, to: { objectId: target.id, portId: 'in' }, type: 'Gas', medium: 'gas', connector: 'direct', style: 'bezier-tube' });
  new FluidSystem(workspace).update(1);
  assert.ok(Number(target.properties.volumeMl) > 0, 'gas connection must transfer gas volume');
  assert.ok(target.contents.some((content) => content.materialId === 'H2' && content.phase === 'gas'), 'gas contents must follow passive flow');

  const reactionWorkspace = new Workspace();
  reactionWorkspace.simulation.running = true;
  const vessel = object('beaker', { id: 'reaction-vessel', properties: { temperature: 25 }, contents: [{ materialId: 'HCl', phase: 'liquid', amount: 10 }, { materialId: 'NaOH', phase: 'liquid', amount: 10 }] });
  reactionWorkspace.scene.add(vessel);
  new ReactionSystem(reactionWorkspace).update(1);
  assert.ok(vessel.contents.some((content) => content.materialId === 'NaCl' && content.phase === 'aqueous'), 'reaction products must preserve configured phase');
}

function testRouteUndoRedo() {
  const workspace = new Workspace();
  const a = object('beaker', { id: 'a', ports: [{ id: 'out', name: 'out', type: 'Liquid', position: { x: 1, y: .5 }, direction: 'out' }] });
  const b = object('beaker', { id: 'b', ports: [{ id: 'in', name: 'in', type: 'Liquid', position: { x: 0, y: .5 }, direction: 'in' }] });
  workspace.scene.add(a); workspace.scene.add(b);
  workspace.scene.connect({ id: 'route', from: { objectId: 'a', portId: 'out' }, to: { objectId: 'b', portId: 'in' }, type: 'Liquid', medium: 'liquid', connector: 'direct', style: 'orthogonal-tube', routePoints: [] });
  const history = new CommandHistory();
  history.execute(new RouteEditCommand(workspace.scene, 'route', [], [{ x: 42, y: 18 }]));
  assert.equal(workspace.scene.connections.get('route').routePoints[0].x, 42);
  history.undo(); assert.deepEqual(workspace.scene.connections.get('route').routePoints, []);
  history.redo(); assert.equal(workspace.scene.connections.get('route').routePoints[0].y, 18);
}

function testMaterialRemoveAndMixtureTransfer() {
  const workspace = new Workspace();
  const source = object('beaker', { id: 'source', capabilities: { container: { capacity: 200 } }, properties: { volumeMl: 100, capacityMl: 200 }, contents: [{ materialId: 'water', phase: 'liquid', amount: 60, unit: 'mL' }, { materialId: 'ethanol', phase: 'liquid', amount: 40, unit: 'mL' }] });
  const target = object('beaker', { id: 'target', capabilities: { container: { capacity: 200 } }, properties: { volumeMl: 0, capacityMl: 200 }, contents: [] });
  workspace.scene.add(source); workspace.scene.add(target);
  const fluid = new FluidSystem(workspace); assert.equal(fluid.pourNow('source', 'target', 50), true);
  assert.ok(target.contents.some((content) => content.materialId === 'water' && content.amount > 0));
  assert.ok(target.contents.some((content) => content.materialId === 'ethanol' && content.amount > 0));
  const history = new CommandHistory(); history.execute(new MaterialRemoveCommand(workspace.scene, 'target', 'water'));
  assert.equal(workspace.scene.objects.get('target').contents.some((content) => content.materialId === 'water'), false);
  history.undo(); assert.equal(workspace.scene.objects.get('target').contents.some((content) => content.materialId === 'water'), true);
}

function testTopologyAndCollision() {
  const engine = new ConnectionEngine();
  const burner = object('burner', { id: 'burner', capabilities: { thermalOutput: { powerW: 1200 } }, ports: [{ id: 'heat', name: 'heat', type: 'Thermal', position: { x: .5, y: 0 }, direction: 'out', role: 'thermal-output' }] });
  const vessel = object('beaker', { id: 'vessel', capabilities: { container: { capacity: 100 }, heatTarget: true }, ports: [{ id: 'thermal', name: 'thermal', type: 'Thermal', position: { x: .5, y: 1 }, direction: 'in' }] });
  assert.equal(engine.canConnect(burner, 'heat', vessel, 'thermal'), true);
  assert.equal(canPlace({ x: 0, y: 0, width: 10, height: 10 }, [{ x: 5, y: 5, width: 10, height: 10 }]), false);
  assert.equal(canPlace({ x: 20, y: 20, width: 10, height: 10 }, [{ x: 5, y: 5, width: 10, height: 10 }]), true);
}

function testThermometerAndHeaterCanShareVessel() {
  const registry = createDefaultEquipmentRegistry();
  const engine = new ConnectionEngine();
  const vessel = registry.create('beaker', { id: 'shared-vessel' });
  const thermometer = registry.create('thermometer', { id: 'shared-thermometer' });
  const burner = registry.create('burner', { id: 'shared-burner' });
  assert.equal(engine.canConnect(thermometer, 'sensor', vessel, 'sensor'), true, 'thermometer must use the vessel sensor port');
  assert.equal(engine.canConnect(burner, 'heat', vessel, 'thermal'), true, 'heater must keep the vessel thermal port');
  assert.equal(engine.canConnect(thermometer, 'sensor', vessel, 'thermal'), false, 'sensor must not occupy the thermal heating port');
}

function testConnectionVariants() {
  const registry = createDefaultEquipmentRegistry();
  const engine = new ConnectionEngine();
  const source = registry.create('beaker', { id: 'source-vessel' });
  const target = registry.create('erlenmeyer', { id: 'target-vessel' });
  const burner = registry.create('burner', { id: 'variant-burner' });
  const thermometer = registry.create('thermometer', { id: 'variant-thermometer' });

  assert.equal(engine.validate(source, 'liquid', target, 'liquid').status, 'compatible', 'liquid vessel connection must be allowed');
  assert.equal(engine.validate(burner, 'heat', target, 'thermal').status, 'compatible', 'thermal heater connection must be allowed');
  assert.equal(engine.validate(thermometer, 'sensor', target, 'sensor').status, 'compatible', 'sensor connection must be allowed');
  assert.equal(engine.validate(thermometer, 'sensor', target, 'thermal').status, 'incompatible', 'sensor to thermal connection must be blocked');
  assert.equal(engine.validate(source, 'liquid', source, 'liquid').status, 'incompatible', 'self connection must be blocked');

  const connection = engine.create(source, 'liquid', target, 'liquid');
  assert.equal(engine.validate(source, 'liquid', target, 'liquid', [connection]).status, 'incompatible', 'duplicate connection must be blocked');
}

function testSafetyResetAndFrontendParity() {
  const closed = object('gas-cylinder', {
    id: 'closed-gas',
    capabilities: { container: { capacity: 100 } },
    ports: [{ id: 'gas', type: 'Gas', position: { x: 1, y: .5 }, direction: 'out' }],
    properties: { isClosed: true, pressureBar: 1.5 },
    contents: [{ materialId: 'oxygen', phase: 'gas', amount: 10 }],
  });
  assert.deepEqual(validateEquipmentContainment(closed), [], 'closed gas equipment is contained');

  const open = object('beaker', {
    id: 'open-gas',
    capabilities: { container: { capacity: 100 } },
    ports: [{ id: 'gas', type: 'Gas', position: { x: 1, y: .5 }, direction: 'out' }],
    properties: { isClosed: false, pressureBar: 1 },
    contents: [{ materialId: 'oxygen', phase: 'gas', amount: 10 }],
  });
  assert.ok(validateEquipmentContainment(open).includes('validation.gasContainment'));

  const condenser = object('condenser', {
    id: 'bad-condenser', capabilities: { container: { capacity: 100 } },
    ports: [{ id: 'gas-in', type: 'Gas', position: { x: 0, y: .5 }, direction: 'in' }],
    properties: { isClosed: true }, contents: [{ materialId: 'vapour', phase: 'gas', amount: 1 }],
  });
  assert.ok(validateEquipmentContainment(condenser).includes('validation.condenserContainment'));

  const registrySource = fs.readFileSync(path.join(root, 'src/entities/equipment/ui/EquipmentRendererRegistry.tsx'), 'utf8');
  assert.match(registrySource, /registerEquipmentRenderer\('funnel'/);
  assert.match(registrySource, /registerEquipmentRenderer\('mantle'/);
  assert.match(registrySource, /canonicalRendererId/);
  const workspaceSource = fs.readFileSync(path.join(root, 'src/widgets/sandbox/SandboxWorkspace.tsx'), 'utf8');
  assert.match(workspaceSource, /mobilePanel === ['"]inspector['"]/);
  assert.match(workspaceSource, /onMaterialRemove=\{removeMaterial\}/);
  const engineSource = fs.readFileSync(path.join(root, 'src/engine/core/Engine.ts'), 'utf8');
  assert.match(engineSource, /resetSimulation\(snapshot\?/);
  assert.match(engineSource, /scene\.objects\.clear\(\)/);
  assert.match(engineSource, /workspace\.simulation = \{ running: false, time: 0 \}/);
}

testHeatingAttachment();
testCapacitySafePour();
testFullSnapshotHistory();
testPourAndOperationHistory();
testGasFlowAndReaction();
testRouteUndoRedo();
testMaterialRemoveAndMixtureTransfer();
testTopologyAndCollision();
testThermometerAndHeaterCanShareVessel();
testConnectionVariants();
testSafetyResetAndFrontendParity();
console.log('sandbox runtime tests: 11 passed');
