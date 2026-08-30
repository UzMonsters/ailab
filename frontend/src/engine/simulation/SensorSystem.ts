import type { EngineSystem } from './EngineSystem';
import { Workspace } from '../workspace/Workspace';

const vessels = new Set(['beaker','erlenmeyer','roundflask','testtube','volumetric_flask','graduated_cylinder','distillation_flask','crucible']);
export class SensorSystem implements EngineSystem {
  constructor(private readonly workspace: Workspace) {}
  update(): boolean {
    if (!this.workspace.simulation.running) return false;
    let changed = false;
    for (const sensor of this.workspace.scene.objects.values()) {
      if (sensor.type !== 'thermometer' && sensor.type !== 'phmeter' && sensor.type !== 'scales' && sensor.type !== 'digitalbalance') continue;
      if (sensor.type === 'scales' || sensor.type === 'digitalbalance') {
        const max = Number((sensor.capabilities.scale as { maxMassG?: number } | undefined)?.maxMassG ?? 220);
        const precision = Number((sensor.capabilities.scale as { precisionG?: number } | undefined)?.precisionG ?? .0001);
        const load = [...this.workspace.scene.objects.values()].filter((object) => object.properties.attachedTo === sensor.id).reduce((sum, object) => sum + Number(object.properties.massG ?? 0), 0);
        sensor.properties.measurementTarget = 'equipment';
        sensor.properties.measuredMassG = load;
        sensor.properties.measurementStatus = load > max ? 'OVER RANGE' : 'valid';
        sensor.properties.measuredValue = load > max ? load : Math.round(load / precision) * precision;
        changed = true;
        continue;
      }
      const probeX = sensor.position.x + (sensor.boundingBox.width * (sensor.scale.x ?? 1)) / 2;
      const probeY = sensor.position.y + sensor.boundingBox.height * (sensor.scale.y ?? 1);
      const linkedTargetId = [...this.workspace.scene.connections.values()]
        .find((connection) => connection.medium === 'sensor' && (connection.from.objectId === sensor.id || connection.to.objectId === sensor.id));
      const linkedTarget = linkedTargetId
        ? this.workspace.scene.objects.get(linkedTargetId.from.objectId === sensor.id ? linkedTargetId.to.objectId : linkedTargetId.from.objectId)
        : undefined;
      // Either a semantic sensor link or a physically immersed probe is a valid
      // measurement setup. A link takes precedence over nearby objects.
      const target = linkedTarget && vessels.has(linkedTarget.type)
        ? linkedTarget
        : [...this.workspace.scene.objects.values()].find((object) => vessels.has(object.type) && probeX >= object.position.x && probeX <= object.position.x + object.boundingBox.width && probeY >= object.position.y && probeY <= object.position.y + object.boundingBox.height);
      const targetType = target ? (Number(target.properties.volumeMl ?? 0) > 0 ? 'liquid' : 'equipment') : 'environment';
      const reading = target ? Number(target.properties.contentsTemperature ?? target.properties.temperature ?? 24.5) : 24.5;
      if (sensor.type === 'phmeter') {
        const ph = target?.contents.some((content) => String(content.materialId) === 'HCl') ? 1 : target?.contents.some((content) => String(content.materialId) === 'NaOH') ? 13 : 7;
        sensor.properties.measurementTarget = target ? 'liquid' : 'environment';
        sensor.properties.measuredValue = ph;
        sensor.properties.measurementStatus = target ? 'valid' : 'No valid measurement';
        changed = true;
        continue;
      }
      const range = sensor.capabilities.temperatureSensor ?? { minC: -20, maxC: 300, precisionC: .1 };
      const status = reading < range.minC || reading > range.maxC ? 'OVER RANGE' : 'valid';
      const rounded = status === 'valid' ? Math.round(reading / Number(range.precisionC ?? .1)) * Number(range.precisionC ?? .1) : reading;
      if (sensor.properties.measuredTemperatureC !== rounded || sensor.properties.measurementStatus !== status || sensor.properties.measurementTarget !== targetType) {
        sensor.properties.measuredTemperatureC = rounded;
        sensor.properties.measurementStatus = status;
        sensor.properties.measurementTarget = targetType;
        sensor.properties.temperature = rounded;
        sensor.history.push(status === 'OVER RANGE' ? `Measurement exceeded sensor range (${range.minC}…${range.maxC} °C)` : target ? `Measuring ${targetType} temperature` : 'No valid measurement: probe is not touching a vessel');
        changed = true;
      }
    }
    return changed;
  }
}
