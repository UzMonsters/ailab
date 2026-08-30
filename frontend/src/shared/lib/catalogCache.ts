/**
 * Singleton cache for equipment and material catalog data.
 * Fetches once, returns cached data on subsequent calls.
 */

import type { EquipmentSummary, MaterialSummary } from '@/types';
import { catalogApi } from '@/entities/equipment/api/catalog.api';

type CacheState = {
  equipment: EquipmentSummary[] | null;
  materials: MaterialSummary[] | null;
  equipmentPromise: Promise<EquipmentSummary[]> | null;
  materialsPromise: Promise<MaterialSummary[]> | null;
};

const cache: CacheState = {
  equipment: null,
  materials: null,
  equipmentPromise: null,
  materialsPromise: null,
};

export const catalogCache = {
  async getEquipment(): Promise<EquipmentSummary[]> {
    if (cache.equipment) return cache.equipment;
    if (!cache.equipmentPromise) {
      cache.equipmentPromise = catalogApi
        .listEquipment()
        .then((data) => {
          cache.equipment = data;
          return data;
        })
        .catch(() => {
          cache.equipmentPromise = null;
          return [];
        });
    }
    return cache.equipmentPromise;
  },

  async getMaterials(): Promise<MaterialSummary[]> {
    if (cache.materials) return cache.materials;
    if (!cache.materialsPromise) {
      cache.materialsPromise = catalogApi
        .listMaterials()
        .then((data) => {
          cache.materials = data;
          return data;
        })
        .catch(() => {
          cache.materialsPromise = null;
          return [];
        });
    }
    return cache.materialsPromise;
  },

  invalidate() {
    cache.equipment = null;
    cache.materials = null;
    cache.equipmentPromise = null;
    cache.materialsPromise = null;
  },
};
