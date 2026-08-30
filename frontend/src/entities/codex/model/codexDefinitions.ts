import type { PortDefinition } from "@/engine/core/types";
import { createDefaultEquipmentRegistry } from "@/engine/registry/EquipmentRegistry";

export type EquipmentDefinition = {
  id: string;
  name: string;
  category: string;
  image: string;
  capacity?: string;
  material?: string;
  description: string;
  alt: string;
  ports: PortDefinition[];
};

const registry = createDefaultEquipmentRegistry();

const rawEquipment = [
  { id: "erlenmeyer", name: "Erlenmeyer Flask", category: "Vessels", image: "/codex/equipment/erlenmeyer-flask-v1.png", capacity: "250 ml", material: "Borosilicate 3.3", description: "Reaction vessel for mixing, heating and controlled experiments.", alt: "Illustrated 500 ml borosilicate Erlenmeyer flask with cyan liquid" },
  { id: "beaker", name: "Beaker", category: "Vessels", image: "/codex/equipment/beaker.png", capacity: "250 ml", description: "Open vessel for transfer and observation.", alt: "Illustrated laboratory beaker" },
  { id: "burette", name: "Burette", category: "Measurement", image: "/codex/equipment/burette.png", capacity: "50 ml", description: "Precision delivery instrument for titration.", alt: "Illustrated laboratory burette" },
  { id: "condenser", name: "Liebig Condenser", category: "Cooling", image: "/codex/equipment/condenser.png", description: "Cooling jacket for condensing vapor into liquid.", alt: "Illustrated laboratory Liebig condenser" },
  { id: "burner", name: "Bunsen Burner", category: "Heating", image: "/codex/equipment/bunsen-burner.png", description: "Adjustable flame source for controlled heating.", alt: "Illustrated laboratory Bunsen burner with blue flame" },
  { id: "hotplate", name: "Hot Plate", category: "Heating", image: "/codex/equipment/hot-plate.png", description: "Stable ceramic heating surface.", alt: "Illustrated laboratory hot plate" },
  { id: "thermometer", name: "Glass Thermometer", category: "Measurement", image: "/codex/equipment/thermometer.png", description: "Direct temperature measurement instrument.", alt: "Illustrated laboratory glass thermometer" },
  { id: "roundflask", name: "Round-bottom Flask", category: "Vessels", image: "/codex/equipment/round-bottom-flask.png", description: "Spherical vessel for heating and distillation.", alt: "Illustrated round-bottom laboratory flask" },
  { id: "petridish", name: "Petri Dish", category: "Vessels", image: "/codex/equipment/petri-dish.png", capacity: "90 mm", description: "Shallow vessel for observation and culture work.", alt: "Illustrated laboratory petri dish" },
  { id: "clampstand", name: "Ring Stand", category: "Support", image: "/codex/equipment/ring-stand.png", description: "Mechanical support for vessels and clamps.", alt: "Illustrated laboratory ring stand with clamp" },
  { id: "pipette", name: "Volumetric Pipette", category: "Transfer", image: "/codex/equipment/pipette.png", capacity: "10 ml", description: "Precision transfer instrument for measured volumes.", alt: "Illustrated volumetric laboratory pipette" },
];

export const equipmentDefinitions: EquipmentDefinition[] = rawEquipment.map(eq => {
  const engineDef = registry.get(eq.id);
  return {
    ...eq,
    ports: engineDef?.defaultPorts || []
  };
});

export const codexSections = [
  { id: "equipment", label: "Equipment", description: "43 instruments & apparatus" },
  { id: "substances", label: "Substances", description: "Elements, compounds & mixtures" },
  { id: "ports", label: "Connection Atlas", description: "Fluid, vapor, heat and interfaces" },
  { id: "experiments", label: "Experiments", description: "Interactive laboratory studies" },
  { id: "physics", label: "Physics", description: "The rules behind the laboratory" },
  { id: "safety", label: "Safety", description: "Hazards and equipment limits" },
] as const;
