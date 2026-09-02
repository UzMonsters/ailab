import type { LaboratoryObject } from '../objects/LaboratoryObject';
export const glassTypes = new Set(['beaker','erlenmeyer','roundflask','testtube','volumetric_flask','graduated_cylinder','distillation_flask','crucible']);
export const updateIntegrity = (object: LaboratoryObject, stress: number) => {
  if (!glassTypes.has(object.type)) return false;
  const limit = Number(object.properties.thermalShockLimit ?? 35);
  const shatterThreshold = limit;
  const crackThreshold = limit * (20 / 35);
  const microcrackThreshold = limit * (9 / 35);
  const stressThreshold = limit * (4 / 35);

  const prior = String(object.properties.integrity ?? 'intact');
  const next = stress > shatterThreshold ? 'shattered' : 
               stress > crackThreshold ? 'cracked' : 
               stress > microcrackThreshold ? 'microcracked' : 
               stress > stressThreshold ? 'stressed' : 
               prior === 'leaking' ? 'leaking' : 'intact';
               
  if (next === prior) return false;
  object.properties.integrity = next; 
  object.properties.broken = next === 'shattered'; 
  object.history.push(
    next === 'stressed' ? 'Thermal stress detected' : 
    next === 'microcracked' ? 'Glass developed a microcrack' : 
    next === 'cracked' ? 'Glass developed a crack' : 
    next === 'shattered' ? 'Container lost integrity' : 'Thermal stress relieved'
  );
  return true;
};
