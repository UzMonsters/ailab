import type { EngineSystem } from './EngineSystem';
import { Workspace } from '../workspace/Workspace';

/** Simplified ideal-gas pressure model for educational closed/vented vessels. */
export class PressureSystem implements EngineSystem {
  constructor(private readonly workspace: Workspace) {}

  update(deltaSeconds: number): boolean {
    if (!this.workspace.simulation.running) return false;
    let changed = false;
    for (const object of this.workspace.scene.objects.values()) {
      if (!object.capabilities.container) continue;
      const systemType = String(object.properties.systemType ?? (object.properties.sealed ? 'closed' : 'open'));
      const gasAmount = object.contents
        .filter((content) => content.phase === 'gas')
        .reduce((sum, content) => sum + Math.max(0, Number(content.amount ?? 0)), 0);
      if (systemType === 'vented' && gasAmount > 0) {
        const vented = Math.min(gasAmount, 3 * deltaSeconds);
        let remaining = vented;
        for (const content of object.contents.filter((candidate) => candidate.phase === 'gas')) {
          const removed = Math.min(Number(content.amount ?? 0), remaining);
          content.amount = Number(content.amount ?? 0) - removed;
          remaining -= removed;
          if (remaining <= 0) break;
        }
        object.properties.escapedMassG = Number(object.properties.escapedMassG ?? 0) + vented;
        object.properties.gasVentedMassG = Number(object.properties.gasVentedMassG ?? 0) + vented;
        changed = true;
      }
      const temperatureK = Number(object.properties.gasTemperature ?? object.properties.temperature ?? 24.5) + 273.15;
      const pressure = systemType === 'closed'
        ? Math.max(1, (temperatureK / 297.65) * (1 + gasAmount / 100))
        : systemType === 'vented' ? 1.01 : 1;
        
      const maxPressure = Number(object.properties.maxPressureBar ?? 2.5);
      const unsafe = systemType === 'closed' && pressure > maxPressure * 0.9;
      
      if (Number(object.properties.pressureBar ?? 1) !== pressure || Boolean(object.properties.unsafeConfiguration) !== unsafe) {
        object.properties.pressureBar = pressure;
        object.properties.unsafeConfiguration = unsafe;
        if (unsafe && !object.history.some((entry) => entry === 'Unsafe configuration: pressure is rising in a closed vessel')) {
          object.history.push('Unsafe configuration: pressure is rising in a closed vessel');
        }
        
        // Break vessel if pressure significantly exceeds max pressure
        if (pressure > maxPressure * 1.1 && !object.properties.broken) {
          object.properties.integrity = 'shattered';
          object.properties.broken = true;
          object.history.push('Explosion: Container shattered due to overpressure');
        }
        
        changed = true;
      }
    }
    return changed;
  }
}
