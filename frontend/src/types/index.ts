export type Locale = 'en' | 'ru' | 'uz';

export interface Workspace {
  id: string;
  name: string;
  science: 'chemistry' | 'physics' | 'biology';
  thumbnail?: string;
  createdAt: string;
  updatedAt: string;
  isFavorite: boolean;
  isDeleted: boolean;
}

export interface User {
  id: string;
  name: string;
  email: string;
  avatar?: string;
  role: 'user' | 'admin';
  discipline?: string;
  isOnline: boolean;
}

export interface Element {
  symbol: string;
  number: number;
  name: string;
  mass: string;
  config: string;
  category: string;
  state: string;
  melting: string;
  boiling: string;
}

export interface Science {
  icon: string;
  name: string;
  copy: string;
  accent: string;
  formula: string;
  meta: Array<[string, string]>;
  image?: string;
}

export interface Molecule {
  formula: string;
  name: string;
  type: string;
  copy: string;
}
