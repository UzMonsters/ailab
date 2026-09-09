import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import Module, { createRequire } from 'node:module';
import ts from 'typescript';

const require = createRequire(import.meta.url), root = path.resolve('.'), originalResolve = Module._resolveFilename;
Module._resolveFilename = (request, parent, isMain, options) => originalResolve(request.startsWith('@/') ? path.join(root, 'src', request.slice(2)) : request, parent, isMain, options);
require.extensions['.ts'] = (module, filename) => { const source = fs.readFileSync(filename, 'utf8'); const output = ts.transpileModule(source, { fileName: filename, compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS, esModuleInterop: true } }).outputText; module._compile(output, filename); };

const { resolvePortWorldPosition } = require(path.join(root, 'src/entities/equipment/lib/equipmentRenderBounds.ts'));
const { collectRuntimeFacts, evaluateRuntimeCondition, evaluateRuntimeStep } = require(path.join(root, 'src/widgets/sandbox/runtime/evaluateRuntimeRule.ts'));
const { convertRuntimeUnit } = require(path.join(root, 'src/widgets/sandbox/runtime/unitConversion.ts'));
const { createRuntimeCatalog, getRuntimeEquipmentRenderer, getRuntimeMaterial } = require(path.join(root, 'src/widgets/sandbox/runtime/runtimeCatalog.ts'));

const unrotated = resolvePortWorldPosition({ x: 1, y: .5 }, { x: 100, y: 200, width: 100, height: 100, rotation: 0 });
assert.deepEqual(unrotated, { x: 190, y: 250 });
const rotated = resolvePortWorldPosition({ x: 1, y: .5 }, { x: 100, y: 200, width: 100, height: 100, rotation: 90 });
assert.ok(Math.abs(rotated.x - 150) < 1e-9);
assert.ok(Math.abs(rotated.y - 290) < 1e-9);
assert.equal(convertRuntimeUnit(1, 'L', 'mL'), 1000);
assert.equal(convertRuntimeUnit(1000, 'mg', 'g'), 1);
assert.equal(convertRuntimeUnit(273.15, 'K', '°C'), 0);
assert.equal(convertRuntimeUnit(1, 'g', 'mL'), null);

const item = { id: 'object-1', metadata: { scenarioAlias: 'main_flask' }, contents: [{ materialId: 'water-id', formula: 'H2O', name: 'Water', amount: 1, unit: 'L', metadata: { reactionId: 'boiling' } }], temperature: 95, volumeMl: 1000, massG: 1000, pressureBar: 1 };
const heater = { id: 'heater-1', metadata: { scenarioAlias: 'heater' }, contents: [], temperature: 24, volumeMl: 0, massG: 0, pressureBar: 1 };
const correctConnection = { from: 'object-1', to: 'heater-1', fromPort: 'mouth', toPort: 'out' };
const facts = collectRuntimeFacts([item, heater], [correctConnection]);
const exists = { id: 'exists', type: 'OBJECT_EXISTS', targetAlias: 'main_flask', operator: 'EQ' };
const present = { id: 'present', type: 'MATERIAL_PRESENT', targetAlias: 'main_flask', materialId: 'water-id', operator: 'EQ' };
const amount = { id: 'amount', type: 'MATERIAL_AMOUNT', targetAlias: 'main_flask', materialId: 'water-id', operator: 'GTE', value: 100, unit: 'mL' };
const tooMuch = { ...amount, id: 'too-much', value: 2, unit: 'L' };
const temperature = { id: 'temperature', type: 'VALUE_COMPARE', targetAlias: 'main_flask', portId: 'temperatureC', operator: 'GTE', value: 368, unit: 'K' };
const exact = { id: 'connection', type: 'CONNECTION_EXISTS', fromAlias: 'main_flask', fromPortId: 'mouth', toAlias: 'heater', toPortId: 'out', operator: 'EQ' };
const wrong = { ...exact, id: 'wrong', toPortId: 'random' };
assert.equal(evaluateRuntimeCondition(exists, facts), true);
assert.equal(evaluateRuntimeCondition(present, facts), true);
assert.equal(evaluateRuntimeCondition(amount, facts), true, '1 L must satisfy 100 mL');
assert.equal(evaluateRuntimeCondition(tooMuch, facts), false, '1 L must not satisfy 2 L');
assert.equal(evaluateRuntimeCondition(temperature, facts), true);
assert.equal(evaluateRuntimeCondition(exact, facts), true);
assert.equal(evaluateRuntimeCondition(wrong, facts), false);
assert.equal(evaluateRuntimeCondition({ id: 'reaction', type: 'REACTION_OBSERVED', materialId: 'boiling', operator: 'EQ' }, facts), true);
assert.equal(evaluateRuntimeCondition({ id: 'product', type: 'PRODUCT_FORMED', materialId: 'water-id', operator: 'EQ' }, facts), true);
assert.equal(evaluateRuntimeStep({ completionRule: { operator: 'ALL', conditions: [exists, amount, exact] } }, facts), true);
assert.equal(evaluateRuntimeStep({ completionRule: { operator: 'ANY', conditions: [wrong, exact] } }, facts), true);
assert.equal(evaluateRuntimeStep({ completionRule: { operator: 'NOT', conditions: [wrong] } }, facts), true);
assert.equal(evaluateRuntimeStep({ completionRule: { operator: 'ALL', conditions: [exists, { id: 'nested-any', operator: 'ANY', conditions: [wrong, exact] }] } }, facts), true, 'nested groups must evaluate recursively');

const catalog = createRuntimeCatalog([{ id: 'eq-1', code: 'BEAKER_250', rendererKey: 'beaker', ports: [] }], [{ id: 'mat-1', code: 'KMNO4', formula: 'KMnO4', phase: 'AQUEOUS', appearance: { color: '#6b21a8', opacity: .8 } }]);
assert.equal(getRuntimeEquipmentRenderer(catalog, 'eq-1'), 'beaker');
assert.equal(getRuntimeMaterial(catalog, 'mat-1').color, '#6b21a8');
assert.equal(getRuntimeMaterial(catalog, 'KMNO4').opacity, .8);

console.log('sandbox runtime correctness tests: 23 passed');
