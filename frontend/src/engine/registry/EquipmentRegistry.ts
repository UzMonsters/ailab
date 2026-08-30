import { LaboratoryObject } from '../objects/LaboratoryObject';
import type { EquipmentDefinition } from '../core/types';

export class EquipmentRegistry {
  private readonly definitions = new Map<string, EquipmentDefinition>();
  private readonly aliases: Record<string, string> = {
    stand: 'clampstand',
    ringstand: 'clampstand',
    magneticstirrer: 'magnetic_stirrer',
    distillationflask: 'distillation_flask',
    graduatedcylinder: 'graduated_cylinder',
    volumetricflask: 'volumetric_flask',
    beaker50: 'beaker',
    beaker100: 'beaker',
    beaker250: 'beaker',
    beaker500: 'beaker',
    graduated: 'graduated_cylinder',
  };
  register(definition: EquipmentDefinition) { this.definitions.set(definition.type, definition); return this; }
  private canonical(type: string) { return this.aliases[type] ?? type; }
  get(type: string) { return this.definitions.get(this.canonical(type)); }
  list() { return [...this.definitions.values()]; }
  getScaleBounds(type: string) {
    const canonicalType = this.canonical(type);
    const definition = this.definitions.get(canonicalType);
    if (!definition) return { min: 0.5, max: 2 };
    return { min: 0.5, max: 2 };
  }
  create(type: string, options: { id?: string; position?: { x: number; y: number } } = {}): LaboratoryObject {
    const canonicalType = this.canonical(type);
    const definition = this.definitions.get(canonicalType);
    if (!definition) throw new Error(`Unknown equipment type: ${type}`);
    const object = new LaboratoryObject({ id: options.id, type: canonicalType, capabilities: definition.capabilities, position: options.position, size: definition.defaultSize, ports: definition.defaultPorts });
    object.metadata = { 
      ...definition.metadata, 
      label: definition.label, 
      capacity: definition.capacity,
      historyYear: definition.historyYear,
      historyText: definition.historyText,
      referenceLink: definition.referenceLink
    };
    object.properties = {
      temperature: 24.5,
      pressureBar: 1,
      integrity: 'intact',
      systemType: 'open',
      thermalState: {
        ambientTemperature: 24.5,
        surfaceTemperature: 24.5,
        wallTemperature: 24.5,
        bottomTemperature: 24.5,
        contentsTemperature: 24.5,
        gasTemperature: 24.5,
        stress: 0,
      },
      ...(canonicalType === 'burette' || canonicalType === 'pipette' ? { valveOpening: 0 } : {}),
    };
    return object;
  }
}

const port = (id: string, type: EquipmentDefinition['allowedConnections'][number], x: number, y: number, options: Record<string, unknown> = {}) => ({ id, name: id, role: id, type, position: { x, y }, direction: 'bidirectional' as const, capacity: 1, isOpen: true, ...options });
export const createDefaultEquipmentRegistry = () => new EquipmentRegistry()
  .register({ 
    type: 'beaker', 
    capabilities: { container: { capacity: 250 }, heatTarget: true, pourable: true, stirrable: true }, 
    label: 'Стакан', 
    defaultSize: { width: 112, height: 138 }, 
    capacity: 250, 
    allowedConnections: ['Liquid', 'Thermal', 'Sensor', 'Glass'], 
    defaultPorts: [
      port('liquid', 'Liquid', .5, .20, { capacity: 2 }), 
      port('thermal', 'Thermal', .5, .90, { direction: 'in', role: 'thermal-contact' }), 
      port('sensor', 'Sensor', .82, .30, { direction: 'in', role: 'temperature-target' }), 
      port('glass-left', 'Glass', -.12, .50), 
      port('glass-right', 'Glass', 1.12, .50)
    ], 
    historyYear: 1836, 
    historyText: 'Лабораторный стакан в его современной форме был изобретен Джоном Джозефом Гриффином, шотландским химиком.', 
    referenceLink: 'https://ru.wikipedia.org/wiki/Мензурка' 
  })
  .register({ type: 'testtube', capabilities: { container: { capacity: 50 }, heatTarget: true, pourable: true }, label: 'Пробирка', defaultSize: { width: 70, height: 156 }, capacity: 50, allowedConnections: ['Liquid', 'Thermal', 'Sensor', 'Glass'], defaultPorts: [port('liquid', 'Liquid', .5, 0), port('thermal', 'Thermal', .5, 1, { direction: 'in' }), port('sensor', 'Sensor', .82, .30, { direction: 'in', role: 'temperature-target' }), port('glass-left', 'Glass', -.12, .50), port('glass-right', 'Glass', 1.12, .50)], historyYear: 1840, historyText: 'Специализированная стеклянная трубка для проведения химических реакций в малых объемах. Использовалась со времен Йёнса Якоба Берцелиуса.', referenceLink: 'https://ru.wikipedia.org/wiki/Пробирка' })
  .register({ 
    type: 'erlenmeyer', 
    capabilities: { container: { capacity: 250 }, heatTarget: true, pourable: true, stirrable: true }, 
    label: 'Колба Эрленмейера', 
    defaultSize: { width: 120, height: 140 }, 
    capacity: 250, 
    allowedConnections: ['Liquid', 'Gas', 'Thermal', 'Sensor', 'Glass'], 
    defaultPorts: [port('top', 'Gas', .5, .15), port('liquid', 'Liquid', .5, .70), port('thermal', 'Thermal', .5, .90), port('sensor', 'Sensor', .82, .30, { direction: 'in', role: 'temperature-target' })], 
    historyYear: 1860, 
    historyText: 'Колба создана немецким химиком Эмилем Эрленмейером. Её форма позволяет легко перемешивать жидкости без расплёскивания.', 
    referenceLink: 'https://ru.wikipedia.org/wiki/Колба_Эрленмейера' 
  })
  .register({ 
    type: 'burner', 
    capabilities: { heater: { maxTemperature: 1200 }, thermalOutput: { powerW: 2500 } }, 
    label: 'Горелка', 
    defaultSize: { width: 100, height: 100 }, 
    allowedConnections: ['Gas', 'Thermal'], 
    defaultPorts: [port('gas-in', 'Gas', .2, .8), port('heat', 'Thermal', .5, .28, { direction: 'out', role: 'thermal-output' })], 
    historyYear: 1855, 
    historyText: 'Горелка Бунзена обеспечивает безопасное и эффективное смешивание газа с воздухом перед сжиганием.', 
    referenceLink: 'https://ru.wikipedia.org/wiki/Горелка_Бунзена' 
  })
  .register({ 
    type: 'hotplate', 
    capabilities: { heater: { maxTemperature: 400 }, thermalOutput: { powerW: 800 }, stirrer: { maxRpm: 1200 } }, 
    label: 'Плитка', 
    defaultSize: { width: 150, height: 78 }, 
    allowedConnections: ['Thermal', 'Electric'], 
    defaultPorts: [port('heat', 'Thermal', .5, .73, { direction: 'out', role: 'thermal-output' }), port('power', 'Electric', .1, .5)], 
    historyYear: 1910, 
    historyText: 'Магнитная мешалка с подогревом значительно упростила процесс автоматического перемешивания и нагрева смесей.', 
    referenceLink: 'https://ru.wikipedia.org/wiki/Магнитная_мешалка' 
  })
  .register({ type: 'thermometer', capabilities: { sensor: { measures: 'temperature' }, temperatureSensor: { minC: -20, maxC: 300, precisionC: .1 } }, label: 'Термометр', defaultSize: { width: 56, height: 150 }, allowedConnections: ['Sensor', 'Thermal'], defaultPorts: [port('sensor', 'Sensor', .5, 1, { direction: 'out', role: 'temperature-probe' })], historyYear: 1714, historyText: 'Первый современный ртутный термометр был изобретен Габриелем Фаренгейтом.', referenceLink: 'https://ru.wikipedia.org/wiki/Термометр' })
  .register({ type: 'scales', capabilities: { scale: { maxMassG: 220, precisionG: .0001 } }, label: 'Аналитические весы', defaultSize: { width: 150, height: 105 }, allowedConnections: ['Electric'], defaultPorts: [port('power', 'Electric', .1, .5)], historyYear: 1947, historyText: 'Первые одночашечные аналитические весы были представлены компанией Mettler.', referenceLink: 'https://ru.wikipedia.org/wiki/Весы' })
  .register({ type: 'digitalbalance', capabilities: { scale: { maxMassG: 220, precisionG: .0001 } }, label: 'Аналитические весы', defaultSize: { width: 150, height: 105 }, allowedConnections: ['Electric'], defaultPorts: [port('power', 'Electric', .1, .5)], historyYear: 1947, historyText: 'Первые одночашечные аналитические весы были представлены компанией Mettler.', referenceLink: 'https://ru.wikipedia.org/wiki/Весы' })
  .register({ type: 'phmeter', capabilities: { phSensor: {} }, label: 'pH-метр', defaultSize: { width: 72, height: 128 }, allowedConnections: ['Sensor', 'Electric'], defaultPorts: [port('probe', 'Sensor', .5, 1), port('power', 'Electric', .1, .5)], historyYear: 1934, historyText: 'Арнольд Бекман изобрел pH-метр для быстрого и точного измерения кислотности.', referenceLink: 'https://ru.wikipedia.org/wiki/PH-метр' })
  .register({ type: 'condenser', capabilities: { condenser: {}, connector: {} }, label: 'Холодильник Либиха', defaultSize: { width: 180, height: 100 }, allowedConnections: ['Gas', 'Liquid', 'Thermal', 'Glass'], defaultPorts: [port('vapor-in', 'Gas', .05, .5, { direction: 'in', role: 'vapor-inlet' }), port('condensate-out', 'Liquid', .95, .5, { direction: 'out', role: 'condensate-outlet' }), port('coolant-in', 'Liquid', .5, .1, { direction: 'in', role: 'coolant-inlet' }), port('coolant-out', 'Liquid', .5, .9, { direction: 'out', role: 'coolant-outlet' })], historyYear: 1843, historyText: 'Часто называется холодильником Либиха, хотя был придуман немецким химиком Христианом Эренфридом Вайгелем.', referenceLink: 'https://ru.wikipedia.org/wiki/Холодильник_(химия)' })
  .register({ 
    type: 'magnetic_stirrer', 
    capabilities: { heater: { maxTemperature: 400 }, thermalOutput: { powerW: 800 }, stirrer: { maxRpm: 1200 } }, 
    label: 'Магнитная мешалка', 
    defaultSize: { width: 150, height: 78 }, 
    allowedConnections: ['Thermal', 'Electric'], 
    defaultPorts: [port('heat', 'Thermal', .5, 0), port('power', 'Electric', .1, .5)] 
  })
  .register({ 
    type: 'distillation_flask', 
    capabilities: { container: { capacity: 250 }, heatTarget: true, pourable: true, stirrable: true }, 
    label: 'Колба Вюрца', 
    defaultSize: { width: 130, height: 150 }, 
    capacity: 250, 
    allowedConnections: ['Liquid', 'Gas', 'Thermal', 'Glass'], 
    defaultPorts: [port('top', 'Gas', .5, 0), port('side', 'Gas', .8, .3), port('liquid', 'Liquid', .5, .85), port('thermal', 'Thermal', .5, 1)] 
  })
  .register({ 
    type: 'roundflask', 
    capabilities: { container: { capacity: 250 }, heatTarget: true, pourable: true, stirrable: true }, 
    label: 'Круглодонная колба', 
    defaultSize: { width: 120, height: 128 }, 
    capacity: 250, 
    allowedConnections: ['Liquid', 'Gas', 'Thermal', 'Glass'], 
    defaultPorts: [port('top', 'Gas', .5, 0), port('liquid', 'Liquid', .5, .85), port('thermal', 'Thermal', .5, 1)] 
  })
  .register({ 
    type: 'volumetric_flask', 
    capabilities: { container: { capacity: 100 }, pourable: true }, 
    label: 'Мерная колба', 
    defaultSize: { width: 100, height: 148 }, 
    capacity: 100, 
    allowedConnections: ['Liquid', 'Glass'], 
    defaultPorts: [port('top', 'Liquid', .5, 0)] 
  })
  .register({ 
    type: 'graduated_cylinder', 
    capabilities: { container: { capacity: 100 }, pourable: true }, 
    label: 'Мерный цилиндр', 
    defaultSize: { width: 60, height: 160 }, 
    capacity: 100, 
    allowedConnections: ['Liquid', 'Glass'], 
    defaultPorts: [port('top', 'Liquid', .5, 0)] 
  })
  .register({ 
    type: 'separatory_funnel', 
    capabilities: { container: { capacity: 250 }, pourable: true }, 
    label: 'Делительная воронка', 
    defaultSize: { width: 90, height: 150 }, 
    capacity: 250, 
    allowedConnections: ['Liquid', 'Glass'], 
    defaultPorts: [port('top', 'Liquid', .5, 0), port('bottom', 'Liquid', .5, 1)] 
  })
  .register({ 
    type: 'icebath', 
    capabilities: { cooler: { minTempC: 0 }, container: { capacity: 2000 }, heatTarget: true }, 
    label: 'Ледяная баня', 
    defaultSize: { width: 160, height: 100 }, 
    capacity: 2000, 
    allowedConnections: ['Thermal', 'Glass'], 
    defaultPorts: [port('thermal', 'Thermal', .5, 0)] 
  })
  .register({
    type: 'burette', 
    capabilities: { container: { capacity: 50 }, pourable: true, valve: { min: 0, max: 1 } }, 
    label: 'Бюретка', 
    defaultSize: { width: 50, height: 180 }, 
    capacity: 50, 
    allowedConnections: ['Liquid', 'Glass'], 
    defaultPorts: [port('top', 'Liquid', .5, 0, { direction: 'in', role: 'liquid-inlet' }), port('tip', 'Liquid', .5, 1, { direction: 'out', role: 'liquid-outlet' })]
  })
  .register({ 
    type: 'pipette', 
    capabilities: { container: { capacity: 10 }, pourable: true }, 
    label: 'Пипетка', 
    defaultSize: { width: 44, height: 110 }, 
    capacity: 10, 
    allowedConnections: ['Liquid', 'Glass'], 
    defaultPorts: [port('top', 'Liquid', .5, 0), port('tip', 'Liquid', .5, 1)] 
  })
  .register({ 
    type: 'funnel', 
    capabilities: { pourable: true, liquidConduit: true }, 
    label: 'Воронка', 
    defaultSize: { width: 90, height: 100 }, 
    allowedConnections: ['Liquid', 'Glass'], 
    defaultPorts: [port('top', 'Liquid', .5, 0, { direction: 'in', role: 'liquid-inlet' }), port('stem', 'Liquid', .5, 1, { direction: 'out', role: 'liquid-outlet' })] 
  })
  .register({ type: 'petridish', capabilities: { container: { capacity: 50 } }, label: 'Чашка Петри', defaultSize: { width: 110, height: 55 }, capacity: 50, allowedConnections: ['Liquid', 'Glass'], defaultPorts: [port('top', 'Liquid', .5, 0)] })
  .register({ type: 'watchglass', capabilities: { container: { capacity: 20 } }, label: 'Часовое стекло', defaultSize: { width: 90, height: 40 }, capacity: 20, allowedConnections: ['Liquid', 'Glass'], defaultPorts: [port('top', 'Liquid', .5, 0.2)] })
  .register({ type: 'clampstand', capabilities: {}, label: 'Штатив', defaultSize: { width: 100, height: 160 }, allowedConnections: ['Glass'], defaultPorts: [port('clamp', 'Glass', 0.8, 0.45, { direction: 'in', role: 'structural' })] })
  .register({ type: 'crucible', capabilities: { container: { capacity: 30 }, heatTarget: true }, label: 'Тигель', defaultSize: { width: 50, height: 40 }, capacity: 30, allowedConnections: ['Thermal', 'Liquid', 'Glass'], defaultPorts: [port('top', 'Liquid', .5, 0), port('thermal', 'Thermal', .5, 1)] })
  .register({ type: 'unsupported', capabilities: {}, label: 'Неподдерживаемое оборудование', defaultSize: { width: 100, height: 100 }, allowedConnections: ['Glass'], defaultPorts: [] });
