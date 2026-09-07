'use client';

import {
  LayoutGrid, Type, Shapes, FlaskConical, Atom, Microscope,
  UserRound, Sigma, ChartNoAxesCombined, Image, FolderOpen, Palette,
} from 'lucide-react';
import type { BookBlockKind } from '../BookPageRenderer';
import type { LucideIcon } from 'lucide-react';

export interface BlockTypeConfig {
  kind: BookBlockKind;
  label: string;
  icon: LucideIcon;
  defaultSize: { w: number; h: number };
  canRotate: boolean;
  canResize: boolean;
}

export const blockRegistry: Record<BookBlockKind, BlockTypeConfig> = {
  RICH_TEXT: {
    kind: 'RICH_TEXT',
    label: 'Text',
    icon: Type,
    defaultSize: { w: 420, h: 160 },
    canRotate: false,
    canResize: true,
  },
  IMAGE: {
    kind: 'IMAGE',
    label: 'Image',
    icon: Image,
    defaultSize: { w: 300, h: 220 },
    canRotate: true,
    canResize: true,
  },
  SVG: {
    kind: 'SVG',
    label: 'SVG',
    icon: Shapes,
    defaultSize: { w: 300, h: 220 },
    canRotate: true,
    canResize: true,
  },
  FORMULA: {
    kind: 'FORMULA',
    label: 'Formula',
    icon: Sigma,
    defaultSize: { w: 280, h: 100 },
    canRotate: false,
    canResize: true,
  },
  INTERACTIVE_EXPERIMENT_LINK: {
    kind: 'INTERACTIVE_EXPERIMENT_LINK',
    label: 'Experiment',
    icon: Microscope,
    defaultSize: { w: 280, h: 210 },
    canRotate: false,
    canResize: true,
  },
  EQUIPMENT_REFERENCE: {
    kind: 'EQUIPMENT_REFERENCE',
    label: 'Equipment',
    icon: FlaskConical,
    defaultSize: { w: 180, h: 200 },
    canRotate: true,
    canResize: true,
  },
  MATERIAL_REFERENCE: {
    kind: 'MATERIAL_REFERENCE',
    label: 'Material',
    icon: Atom,
    defaultSize: { w: 180, h: 180 },
    canRotate: false,
    canResize: true,
  },
  REACTION_REFERENCE: {
    kind: 'REACTION_REFERENCE',
    label: 'Reaction',
    icon: FlaskConical,
    defaultSize: { w: 300, h: 150 },
    canRotate: false,
    canResize: true,
  },
  SHAPE: {
    kind: 'SHAPE',
    label: 'Shape',
    icon: Shapes,
    defaultSize: { w: 200, h: 200 },
    canRotate: true,
    canResize: true,
  },
  DATA_WIDGET: {
    kind: 'DATA_WIDGET',
    label: 'Data Widget',
    icon: ChartNoAxesCombined,
    defaultSize: { w: 220, h: 180 },
    canRotate: false,
    canResize: true,
  },
  TABLE: {
    kind: 'TABLE',
    label: 'Table',
    icon: LayoutGrid,
    defaultSize: { w: 400, h: 200 },
    canRotate: false,
    canResize: true,
  },
};

export type RailSection = {
  id: string;
  label: string;
  icon: LucideIcon;
  tabId: string;
};

export const railSections: RailSection[] = [
  { id: 'layouts', label: 'Layouts', icon: LayoutGrid, tabId: 'layouts' },
  { id: 'text', label: 'Text', icon: Type, tabId: 'text' },
  { id: 'graphics', label: 'Graphics', icon: Shapes, tabId: 'graphics' },
  { id: 'scientists', label: 'Scientists', icon: UserRound, tabId: 'scientists' },
  { id: 'equipment', label: 'Equipment', icon: FlaskConical, tabId: 'equipment' },
  { id: 'materials', label: 'Materials', icon: Atom, tabId: 'materials' },
  { id: 'reactions', label: 'Reactions', icon: FlaskConical, tabId: 'reactions' },
  { id: 'experiments', label: 'Experiments', icon: Microscope, tabId: 'experiments' },
  { id: 'formulas', label: 'Formulas', icon: Sigma, tabId: 'formulas' },
  { id: 'data', label: 'Data', icon: ChartNoAxesCombined, tabId: 'data' },
  { id: 'media', label: 'Media', icon: Image, tabId: 'media' },
  { id: 'files', label: 'My Files', icon: FolderOpen, tabId: 'files' },
  { id: 'theme', label: 'Theme', icon: Palette, tabId: 'theme' },
];
