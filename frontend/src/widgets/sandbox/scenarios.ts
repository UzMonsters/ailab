export type ScenarioStep = { titleKey: string; descKey: string; hintKey: string; check: (engine: any) => boolean };
export type Scenario = { id: string; nameKey: string; descKey: string; steps: ScenarioStep[] };

const objects = (engine: any) => Array.from(engine.workspace.scene.objects.values()) as any[];
const connections = (engine: any) => Array.from(engine.workspace.scene.connections.values()) as any[];
const vesselTypes = ['beaker', 'beaker50', 'beaker100', 'beaker250', 'beaker500', 'erlenmeyer', 'roundflask', 'volumetric_flask', 'testtube', 'graduated_cylinder', 'separatory_funnel', 'distillation_flask'];
const vessels = (engine: any) => objects(engine).filter((object) => object.capabilities?.container || vesselTypes.includes(object.type));
const hasMaterial = (engine: any, ids: string[], minimumVessels = 1) => vessels(engine).filter((object) => object.contents?.some((content: any) => ids.includes(String(content.materialId)) || ids.includes(String(content.formula)) || ids.includes(String(content.name)))).length >= minimumVessels;
const hasMaterialsInSameVessel = (engine: any, group1: string[], group2: string[]) => vessels(engine).some((vessel) => {
  const contents = vessel.contents || [];
  const hasFirst = contents.some((c: any) => group1.includes(String(c.materialId)) || group1.includes(String(c.formula)) || group1.includes(String(c.name)));
  const hasSecond = contents.some((c: any) => group2.includes(String(c.materialId)) || group2.includes(String(c.formula)) || group2.includes(String(c.name)));
  return hasFirst && hasSecond;
});
const hasDevice = (engine: any, types: string[]) => objects(engine).some((object) => types.includes(object.type));
const linkedDevice = (engine: any, types: string[]) => connections(engine).some((connection) => {
  const fromId = typeof connection.from === 'string' ? connection.from : connection.from?.objectId;
  const toId = typeof connection.to === 'string' ? connection.to : connection.to?.objectId;
  const from = objects(engine).find((object) => object.id === fromId);
  const to = objects(engine).find((object) => object.id === toId);
  const isVessel = (object: any) => Boolean(object?.capabilities?.container) || vesselTypes.includes(object?.type);
  return !!from && !!to && ((types.includes(from.type) && isVessel(to)) || (types.includes(to.type) && isVessel(from)));
});
const linkedLiquid = (engine: any) => connections(engine).some((connection) => connection.medium === 'liquid');
const playing = (engine: any) => Boolean(engine.workspace.simulation.running);
const hasBoiled = (engine: any) => vessels(engine).some((vessel) => vessel.contents?.some((content: any) => content.phase === 'gas') || Number(vessel.properties?.escapedMassG ?? 0) > 0);
const hasHeatedWater = (engine: any) => vessels(engine).some((vessel) => Number(vessel.properties?.contentsTemperature ?? vessel.properties?.temperature ?? 0) >= 35 && vessel.contents?.some((content: any) => ['H2O', 'water', 'H₂O'].includes(String(content.materialId))));
const hasCondensate = (engine: any) => vessels(engine).some((vessel) => Number(vessel.properties?.volumeMl ?? 0) > 0 && vessel.history?.some((entry: string) => entry.includes('condens')));
const reactionCompleted = (engine: any, product: string) => vessels(engine).some((vessel) => vessel.contents?.some((content: any) => String(content.materialId) === product));
const step = (id: string, n: number, check: (engine: any) => boolean): ScenarioStep => ({ titleKey: `scenarios.${id}.step${n}.title`, descKey: `scenarios.${id}.descKey`, hintKey: `scenarios.${id}.step${n}.hint`, check });

export const SCENARIOS: Record<string, Scenario> = {
  water_intro: { id: 'water_intro', nameKey: 'scenarios.water_intro.name', descKey: 'scenarios.water_intro.desc', steps: [step('water_intro', 1, (e) => vessels(e).length > 0), step('water_intro', 2, (e) => hasMaterial(e, ['H2O', 'water', 'H₂O']))] },
  measure_water: { id: 'measure_water', nameKey: 'scenarios.measure_water.name', descKey: 'scenarios.measure_water.desc', steps: [step('measure_water', 1, (e) => vessels(e).length > 0), step('measure_water', 2, (e) => hasMaterial(e, ['H2O', 'water', 'H₂O'])), step('measure_water', 3, (e) => hasDevice(e, ['thermometer']) && linkedDevice(e, ['thermometer']))] },
  heat_water: { id: 'heat_water', nameKey: 'scenarios.heat_water.name', descKey: 'scenarios.heat_water.desc', steps: [step('heat_water', 1, (e) => vessels(e).length > 0), step('heat_water', 2, (e) => hasMaterial(e, ['H2O', 'water', 'H₂O'])), step('heat_water', 3, (e) => hasDevice(e, ['hotplate', 'burner', 'magnetic_stirrer']) && linkedDevice(e, ['hotplate', 'burner', 'magnetic_stirrer'])), step('heat_water', 4, (e) => playing(e) && hasHeatedWater(e)), step('heat_water', 5, (e) => playing(e) && hasBoiled(e))] },
  transfer_water: { id: 'transfer_water', nameKey: 'scenarios.transfer_water.name', descKey: 'scenarios.transfer_water.desc', steps: [step('transfer_water', 1, (e) => vessels(e).length > 0), step('transfer_water', 2, (e) => hasMaterial(e, ['H2O', 'water', 'H₂O'])), step('transfer_water', 3, (e) => vessels(e).length >= 2), step('transfer_water', 4, (e) => linkedLiquid(e) || (hasMaterial(e, ['H2O', 'water', 'H₂O'], 2) && connections(e).length > 0))] },
  cuso4: { id: 'cuso4', nameKey: 'scenarios.cuso4.name', descKey: 'scenarios.cuso4.desc', steps: [step('cuso4', 1, (e) => vessels(e).length > 0), step('cuso4', 2, (e) => hasMaterial(e, ['CuSO4', 'CuSO4(aq)', 'cuso4'])), step('cuso4', 3, (e) => hasMaterial(e, ['H2O', 'water', 'H₂O'])), step('cuso4', 4, (e) => vessels(e).some((vessel) => {
    const mixed = ['homogeneous', 'reacted'].includes(String(vessel.properties?.mixtureState));
    const copperSulfateSolution = vessel.contents?.some((content: any) => ['CuSO4(aq)', 'CuSO4'].includes(String(content.materialId)) && (content.phase === 'aqueous' || content.metadata?.homogeneous));
    return mixed && copperSulfateSolution;
  }))] },
  kmno4: { id: 'kmno4', nameKey: 'scenarios.kmno4.name', descKey: 'scenarios.kmno4.desc', steps: [step('kmno4', 1, (e) => hasMaterial(e, ['KMnO4', 'KMnO4(aq)', 'kmno4'])), step('kmno4', 2, (e) => hasMaterial(e, ['H2O', 'water', 'H₂O'])), step('kmno4', 3, (e) => hasMaterialsInSameVessel(e, ['KMnO4', 'KMnO4(aq)', 'kmno4'], ['H2O', 'water', 'H₂O']) || (linkedLiquid(e) && hasMaterial(e, ['KMnO4', 'KMnO4(aq)', 'kmno4']) && hasMaterial(e, ['H2O', 'water', 'H₂O'])))] },
  hcl_naoh: { id: 'hcl_naoh', nameKey: 'scenarios.hcl_naoh.name', descKey: 'scenarios.hcl_naoh.desc', steps: [step('hcl_naoh', 1, (e) => hasMaterial(e, ['HCl', 'acid', 'hcl'])), step('hcl_naoh', 2, (e) => hasMaterial(e, ['NaOH', 'naoh'])), step('hcl_naoh', 3, (e) => playing(e) && reactionCompleted(e, 'NaCl'))] },
  zn_hcl: { id: 'zn_hcl', nameKey: 'scenarios.zn_hcl.name', descKey: 'scenarios.zn_hcl.desc', steps: [step('zn_hcl', 1, (e) => hasMaterial(e, ['Zn', 'zinc', 'zn'])), step('zn_hcl', 2, (e) => hasMaterial(e, ['HCl', 'acid', 'hcl'])), step('zn_hcl', 3, (e) => playing(e) && reactionCompleted(e, 'ZnCl2'))] },
  sulfur_heat: { id: 'sulfur_heat', nameKey: 'scenarios.sulfur_heat.name', descKey: 'scenarios.sulfur_heat.desc', steps: [step('sulfur_heat', 1, (e) => hasMaterial(e, ['sulfur', 'S'])), step('sulfur_heat', 2, (e) => hasDevice(e, ['hotplate', 'burner', 'magnetic_stirrer']) && linkedDevice(e, ['hotplate', 'burner', 'magnetic_stirrer'])), step('sulfur_heat', 3, (e) => vessels(e).some((o) => o.contents?.some((c: any) => ['sulfur', 'S'].includes(String(c.materialId)) && (c.phase === 'liquid' || o.temperature >= 115))))] },
  distillation: { id: 'distillation', nameKey: 'scenarios.distillation.name', descKey: 'scenarios.distillation.desc', steps: [step('distillation', 1, (e) => hasMaterial(e, ['H2O', 'water', 'H₂O']) && vessels(e).length >= 2), step('distillation', 2, (e) => hasDevice(e, ['condenser']) && hasDevice(e, ['hotplate', 'burner']) && hasDevice(e, ['thermometer'])), step('distillation', 3, (e) => connections(e).some((c: any) => c.medium === 'gas') && connections(e).some((c: any) => c.medium === 'liquid')), step('distillation', 4, (e) => playing(e) && hasCondensate(e))] },
};
