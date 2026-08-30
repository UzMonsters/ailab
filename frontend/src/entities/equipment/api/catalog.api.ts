import { api } from '@/shared/api/client';
import type { EquipmentDetails, EquipmentSummary, MaterialSummary } from '@/types';

export interface CatalogQuery {
  query?: string;
  phase?: string;
  category?: string;
  page?: number;
  size?: number;
}

function toQuery(query: CatalogQuery = {}): string {
  const params = new URLSearchParams();
  Object.entries(query).forEach(([key, value]) => {
    if (value !== undefined && value !== '') params.set(key, String(value));
  });
  const value = params.toString();
  return value ? `?${value}` : '';
}

export const catalogApi = {
  listMaterials: (query?: CatalogQuery) =>
    api.get<MaterialSummary[]>(`/api/v1/chemistry/materials${toQuery(query)}`),

  listEquipment: (query?: CatalogQuery) =>
    api.get<EquipmentSummary[]>(`/api/v1/chemistry/equipment${toQuery(query)}`),

  getEquipment: (identifier: string) =>
    api.get<EquipmentDetails>(`/api/v1/chemistry/equipment/${encodeURIComponent(identifier)}`),
};
