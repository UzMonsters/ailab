import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export type AdminUser = {
  id: string;
  name: string;
  email: string;
  role: 'Student' | 'Teacher' | 'Admin';
  level: number;
  xp: number;
  favoriteSubject: string;
  lastActive: string;
  status: 'Active' | 'Blocked' | 'Offline';
};

export type Equipment = {
  id: string;
  name: string;
  subject: 'Chemistry' | 'Physics' | 'Biology' | 'Multiple';
  category: string;
  status: 'Available' | 'Maintenance' | 'Deprecated';
  uses: number;
};

export type Scenario = {
  id: string;
  title: string;
  subject: 'Chemistry' | 'Physics' | 'Biology';
  difficulty: 'Beginner' | 'Intermediate' | 'Advanced';
  status: 'Published' | 'Draft';
  completions: number;
};

export type Material = {
  id: string;
  name: string;
  type: 'Liquid' | 'Solid' | 'Gas' | 'Solution' | 'Biological Sample' | 'Physical Material';
  status: 'Available' | 'Maintenance' | 'Deprecated';
  uses: number;
};

export type SafetyRule = {
  id: string;
  title: string;
  description: string;
  category: 'General' | 'Chemical' | 'Biological' | 'Electrical' | 'Fire';
  severity: 'Low' | 'Medium' | 'High' | 'Critical';
};

interface AdminMockState {
  users: AdminUser[];
  equipment: Equipment[];
  materials: Material[];
  scenarios: Scenario[];
  safetyRules: SafetyRule[];
  
  // Actions
  updateUser: (id: string, data: Partial<AdminUser>) => void;
  deleteUser: (id: string) => void;
  updateEquipment: (id: string, data: Partial<Equipment>) => void;
  deleteEquipment: (id: string) => void;
  updateMaterial: (id: string, data: Partial<Material>) => void;
  deleteMaterial: (id: string) => void;
  addScenario: (data: Omit<Scenario, 'id' | 'completions'>) => void;
  updateScenario: (id: string, data: Partial<Scenario>) => void;
  deleteScenario: (id: string) => void;
  addSafetyRule: (data: Omit<SafetyRule, 'id'>) => void;
  updateSafetyRule: (id: string, data: Partial<SafetyRule>) => void;
  deleteSafetyRule: (id: string) => void;
  resetToDefaults: () => void;
}

const generateUsers = (): AdminUser[] => {
  const users: AdminUser[] = [];
  for (let i = 1; i <= 45; i++) {
    users.push({
      id: `u${i}`,
      name: `User ${i}`, // We'll populate realistic names later
      email: `user${i}@example.com`,
      role: i % 10 === 0 ? 'Teacher' : (i === 1 ? 'Admin' : 'Student'),
      level: Math.floor(Math.random() * 30) + 1,
      xp: Math.floor(Math.random() * 50000),
      favoriteSubject: i % 3 === 0 ? 'Biology' : (i % 2 === 0 ? 'Physics' : 'Chemistry'),
      lastActive: `${Math.floor(Math.random() * 60)} min ago`,
      status: i % 12 === 0 ? 'Blocked' : 'Active'
    });
  }
  // Replace first few with realistic names
  users[0].name = 'Jasur Karimov'; users[0].email = 'jasur@example.com';
  users[1].name = 'Aziza Rahimova'; users[1].email = 'aziza@example.com';
  users[2].name = 'Alex Morgan'; users[2].email = 'alex@jasscience.com';
  return users;
};

const defaultUsers = generateUsers();

const defaultEquipment: Equipment[] = [
  { id: 'eq1', name: 'Beaker', subject: 'Chemistry', category: 'Vessels', status: 'Available', uses: 15821 },
  { id: 'eq2', name: 'Erlenmeyer Flask', subject: 'Chemistry', category: 'Vessels', status: 'Available', uses: 13492 },
  { id: 'eq3', name: 'Thermometer', subject: 'Multiple', category: 'Measurement', status: 'Available', uses: 12284 },
  { id: 'eq4', name: 'Microscope', subject: 'Biology', category: 'Microscopy', status: 'Available', uses: 4120 },
];

const defaultScenarios: Scenario[] = [
  { id: 'sc1', title: 'Simple Distillation', subject: 'Chemistry', difficulty: 'Intermediate', status: 'Published', completions: 4200 },
  { id: 'sc2', title: 'Thermal Shock', subject: 'Chemistry', difficulty: 'Advanced', status: 'Published', completions: 2920 },
  { id: 'sc3', title: 'Ohm\'s Law', subject: 'Physics', difficulty: 'Beginner', status: 'Published', completions: 8400 },
  { id: 'sc4', title: 'Cell Classification', subject: 'Biology', difficulty: 'Intermediate', status: 'Draft', completions: 0 },
];

const defaultMaterials: Material[] = [
  { id: 'mat1', name: 'Water', type: 'Liquid', status: 'Available', uses: 23150 },
  { id: 'mat2', name: 'Sodium Chloride', type: 'Solid', status: 'Available', uses: 12040 },
  { id: 'mat3', name: 'Oxygen', type: 'Gas', status: 'Available', uses: 8092 },
  { id: 'mat4', name: 'Hydrochloric Acid (1M)', type: 'Solution', status: 'Available', uses: 9400 },
  { id: 'mat5', name: 'Onion Epidermis', type: 'Biological Sample', status: 'Available', uses: 1205 },
  { id: 'mat6', name: 'Copper Wire', type: 'Physical Material', status: 'Available', uses: 5120 },
];

const defaultSafetyRules: SafetyRule[] = [
  { id: 'sr1', title: 'Eye Protection', description: 'Safety goggles must be worn at all times in the laboratory.', category: 'General', severity: 'Critical' },
  { id: 'sr2', title: 'Acid Handling', description: 'Always add acid to water, never water to acid.', category: 'Chemical', severity: 'High' },
  { id: 'sr3', title: 'Fire Extinguisher', description: 'Know the location of the nearest fire extinguisher.', category: 'Fire', severity: 'Medium' },
];

export const useAdminStore = create<AdminMockState>()(
  persist(
    (set) => ({
      users: defaultUsers,
      equipment: defaultEquipment,
      materials: defaultMaterials,
      scenarios: defaultScenarios,
      safetyRules: defaultSafetyRules,
      
      updateUser: (id, data) => set((state) => ({
        users: state.users.map(u => u.id === id ? { ...u, ...data } : u)
      })),
      
      deleteUser: (id) => set((state) => ({
        users: state.users.filter(u => u.id !== id)
      })),

      updateEquipment: (id, data) => set((state) => ({
        equipment: state.equipment.map(e => e.id === id ? { ...e, ...data } : e)
      })),
      
      deleteEquipment: (id) => set((state) => ({
        equipment: state.equipment.filter(e => e.id !== id)
      })),

      updateMaterial: (id, data) => set((state) => ({
        materials: state.materials.map(m => m.id === id ? { ...m, ...data } : m)
      })),
      
      deleteMaterial: (id) => set((state) => ({
        materials: state.materials.filter(m => m.id !== id)
      })),

      addScenario: (data) => set((state) => ({
        scenarios: [...state.scenarios, { ...data, id: `sc${Date.now()}`, completions: 0 }]
      })),
      updateScenario: (id, data) => set((state) => ({
        scenarios: state.scenarios.map(s => s.id === id ? { ...s, ...data } : s)
      })),
      deleteScenario: (id) => set((state) => ({
        scenarios: state.scenarios.filter(s => s.id !== id)
      })),

      addSafetyRule: (data) => set((state) => ({
        safetyRules: [...state.safetyRules, { ...data, id: `sr${Date.now()}` }]
      })),
      updateSafetyRule: (id, data) => set((state) => ({
        safetyRules: state.safetyRules.map(sr => sr.id === id ? { ...sr, ...data } : sr)
      })),
      deleteSafetyRule: (id) => set((state) => ({
        safetyRules: state.safetyRules.filter(sr => sr.id !== id)
      })),
      
      resetToDefaults: () => set({
        users: defaultUsers,
        equipment: defaultEquipment,
        materials: defaultMaterials,
        scenarios: defaultScenarios,
        safetyRules: defaultSafetyRules,
      })
    }),
    {
      name: 'jasscience-admin-mock-storage',
    }
  )
);
