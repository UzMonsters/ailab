'use client';

import { create } from 'zustand';
import { adminBookApi } from '@/entities/book/api/book.api';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { sanitizeSvgMarkup } from '@/shared/lib/sanitizeSvg';
import { getApiBaseUrl } from '@/shared/api/client';
import type { JsonObject } from '@/shared/api/contracts/platform';
import type { Locale } from '@/shared/types/catalog';
import { hydrateBookPageBlocks, type BookBlockKind, type BookPageBlock } from '../BookPageRenderer';
import type { ScenarioDraft } from '../../scenario/scenario.types';
import { scenarioDraft } from '../../scenario/scenario.model';

export type Block = BookPageBlock;
export type Kind = BookBlockKind;

export type EditorTab = 'layouts' | 'text' | 'graphics' | 'scientists' | 'equipment' | 'materials' | 'reactions' | 'experiments' | 'formulas' | 'data' | 'media' | 'files' | 'theme' | null;
export type InspectorTab = 'properties' | 'layers' | 'pages';
export type InteractionMode = 'select' | 'text-edit' | 'crop' | 'interactive';

const obj = (v: unknown): JsonObject => v && typeof v === 'object' && !Array.isArray(v) ? v as JsonObject : {};
const items = (v: unknown): JsonObject[] => Array.isArray(v) ? v.filter(i => i && typeof i === 'object') as JsonObject[] : [];
const n = (v: unknown, fb = 0) => Number.isFinite(Number(v)) ? Number(v) : fb;

function extractLabel(o: JsonObject): string {
  const translations = obj(o.translations);
  for (const locale of ['en', 'ru', 'uz'] as const) {
    const loc = obj(translations[locale]);
    if (typeof loc === 'string') return loc;
    if (typeof loc.name === 'string' && loc.name.trim()) return loc.name;
    if (typeof loc.title === 'string' && loc.title.trim()) return loc.title;
    if (typeof loc.content === 'string' && loc.content.trim()) return loc.content;
  }
  if (typeof o.title === 'string' && o.title.trim()) return o.title;
  if (typeof o.name === 'string' && o.name.trim()) return o.name;
  if (typeof o.slug === 'string' && o.slug.trim()) return o.slug;
  if (typeof o.code === 'string' && o.code.trim()) return o.code;
  if (o.id != null) return String(o.id);
  return 'Untitled';
}

const label = extractLabel;
const localizedText = (translations: JsonObject, locale: Locale): string => {
  const loc = obj(translations[locale]);
  if (typeof loc === 'string') return loc;
  if (typeof loc.content === 'string') return loc.content;
  return '';
};

interface BookStudioState {
  books: JsonObject[];
  book: JsonObject | null;
  page: JsonObject | null;
  scenarios: JsonObject[];
  blocks: Block[];
  selectedBlockId: string;
  history: Block[][];
  future: Block[][];
  contentLocale: Locale;
  activeLeftTab: EditorTab;
  inspectorTab: InspectorTab;
  interactionMode: InteractionMode;
  zoom: number;
  panX: number;
  panY: number;
  showGrid: boolean;
  showGuides: boolean;
  previewMode: 'none' | 'two-page' | 'full';
  interactiveDraft: ScenarioDraft | null;
  busy: boolean;
  notice: string;
  error: string;
  dirty: boolean;
  clipboard: Block[];
  isPanning: boolean;
  rightPanelOpen: boolean;
  chapters: JsonObject[];
  pages: JsonObject[];
  pageDrafts: Record<string, Partial<Record<Locale, Block[]>>>;

  loadBooks: () => Promise<void>;
  loadBook: (id: string, retainPageId?: string) => Promise<void>;
  selectPage: (page: JsonObject) => void;
  switchBook: (id: string) => Promise<boolean>;
  commit: (next: Block[]) => void;
  patchBlock: (id: string, patch: Partial<Block>, record?: boolean) => void;
  addBlock: (kind: Kind, overrides?: Partial<Block>) => void;
  replaceBlocks: (next: Block[]) => void;
  deleteBlock: (id: string) => void;
  duplicateBlock: (id: string) => void;
  bringForward: (id: string) => void;
  sendBackward: (id: string) => void;
  bringToFront: (id: string) => void;
  sendToBack: (id: string) => void;
  undo: () => void;
  redo: () => void;
  save: () => Promise<void>;
  publishBook: () => Promise<void>;
  setSelected: (id: string) => void;
  setContentLocale: (locale: Locale) => void;
  setActiveLeftTab: (tab: EditorTab) => void;
  setInspectorTab: (tab: InspectorTab) => void;
  setZoom: (z: number) => void;
  setPan: (x: number, y: number) => void;
  setShowGrid: (v: boolean) => void;
  setShowGuides: (v: boolean) => void;
  setPreviewMode: (m: 'none' | 'two-page' | 'full') => void;
  setInteractionMode: (m: InteractionMode) => void;
  setInteractiveDraft: (d: ScenarioDraft | null) => void;
  setIsPanning: (v: boolean) => void;
  setRightPanelOpen: (v: boolean) => void;
  setNotice: (msg: string) => void;
  setError: (msg: string) => void;
  uploadImage: (file: File) => Promise<void>;
  uploadSvg: (file: File) => Promise<void>;
  copyBlock: (id: string) => void;
  pasteBlock: () => void;
  discardDraft: () => void;
  createBook: (title: string, slug: string) => Promise<void>;
  createChapter: (bookId: string, title: string) => Promise<void>;
  createPage: (chapterId: string) => Promise<void>;
}

export const useBookStudioStore = create<BookStudioState>((set, get) => ({
  books: [],
  book: null,
  page: null,
  scenarios: [],
  blocks: [],
  selectedBlockId: '',
  history: [],
  future: [],
  contentLocale: 'en',
  activeLeftTab: null,
  inspectorTab: 'properties',
  interactionMode: 'select',
  zoom: 0.75,
  panX: 0,
  panY: 0,
  showGrid: false,
  showGuides: true,
  previewMode: 'none',
  interactiveDraft: null,
  busy: false,
  notice: '',
  error: '',
  dirty: false,
  clipboard: [],
  isPanning: false,
  rightPanelOpen: true,
  chapters: [],
  pages: [],
  pageDrafts: {},

  loadBooks: async () => {
    set({ busy: true, error: '' });
    try {
      const [bookList, scenarioList] = await Promise.all([
        adminBookApi.list({ size: 100 }),
        adminPlatformApi.scenarios.list({ size: 100, status: 'PUBLISHED' }),
      ]);
      set({ books: bookList.items, scenarios: scenarioList.items });
      if (bookList.items[0]) {
        await get().loadBook(String(bookList.items[0].id));
      }
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'Unable to load Book Studio' });
    } finally {
      set({ busy: false });
    }
  },

  loadBook: async (id: string, retainPageId?: string) => {
    set({ busy: true, error: '' });
    try {
      const detail = await adminBookApi.get(id);
      const chapters = items(detail.chapters);
      const allPages = chapters.flatMap(ch => items(ch.pages));

      let targetPage: JsonObject | null = null;
      if (retainPageId) {
        targetPage = allPages.find(p => String(p.id) === retainPageId) || null;
      }
      if (!targetPage) {
        targetPage = allPages[0] || null;
      }

      const locale = get().contentLocale;
      const serverBlocks = hydrateBookPageBlocks(targetPage?.blocks, locale);

      set(state => ({
        book: detail,
        chapters,
        pages: allPages,
        page: targetPage,
        blocks: serverBlocks,
        history: [],
        future: [],
        selectedBlockId: '',
        dirty: false,
        pageDrafts: retainPageId ? state.pageDrafts : {},
      }));
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'Failed to load book' });
    } finally {
      set({ busy: false });
    }
  },

  selectPage: (page: JsonObject) => {
    const { page: currentPage, blocks, dirty, contentLocale, pageDrafts, book } = get();
    const currentPageId = currentPage ? String(currentPage.id) : null;
    const nextPageId = String(page.id);

    if (currentPageId && dirty && currentPageId !== nextPageId) {
      const draftKey = `${book?.id}:${currentPageId}`;
      set({
        pageDrafts: {
          ...pageDrafts,
          [draftKey]: {
            ...(pageDrafts[draftKey] || {}),
            [contentLocale]: blocks,
          },
        },
      });
    }

    const draftKey = book ? `${String(book.id)}:${nextPageId}` : null;
    const savedDraft = draftKey ? pageDrafts[draftKey] : null;
    const draftBlocks = savedDraft?.[contentLocale];

    set({
      page,
      blocks: draftBlocks || hydrateBookPageBlocks(page.blocks, contentLocale),
      selectedBlockId: '',
      history: [],
      future: [],
      dirty: Boolean(draftBlocks),
    });
  },

  switchBook: async (id: string) => {
    const { dirty, book } = get();
    if (dirty && book) {
      const { page, blocks, contentLocale, pageDrafts } = get();
      if (page) {
        const draftKey = `${String(book.id)}:${String(page.id)}`;
        set({
          pageDrafts: {
            ...pageDrafts,
            [draftKey]: {
              ...(pageDrafts[draftKey] || {}),
              [contentLocale]: blocks,
            },
          },
        });
      }
    }
    await get().loadBook(id);
    return true;
  },

  commit: (next: Block[]) => {
    const { blocks } = get();
    set(state => ({
      history: [...state.history, blocks],
      future: [],
      blocks: next,
      dirty: true,
    }));
  },

  patchBlock: (id, patch, record = true) => {
    const { blocks } = get();
    const next = blocks.map(b => b.id === id ? { ...b, ...patch } : b);
    if (record) {
      get().commit(next);
    } else {
      set({ blocks: next, dirty: true });
    }
  },

  addBlock: (kind, overrides) => {
    const { blocks, scenarios, page } = get();
    if (!page) return;
    const top = Math.max(0, ...blocks.map(b => b.z)) + 1;
    const defaults: Record<string, { w: number; h: number; text?: string; formula?: string; scenarioId?: string }> = {
      RICH_TEXT: { w: 420, h: 160, text: '<p>New text</p>' },
      IMAGE: { w: 300, h: 220 },
      SVG: { w: 300, h: 220 },
      FORMULA: { w: 280, h: 100, formula: 'H_2O' },
      INTERACTIVE_EXPERIMENT_LINK: { w: 280, h: 210, scenarioId: String(scenarios[0]?.id || '') },
      EQUIPMENT_REFERENCE: { w: 180, h: 200 },
      MATERIAL_REFERENCE: { w: 180, h: 180 },
      REACTION_REFERENCE: { w: 300, h: 150 },
      SHAPE: { w: 200, h: 200 },
      DATA_WIDGET: { w: 220, h: 180 },
      TABLE: { w: 400, h: 200 },
    };
    const d = defaults[kind] || { w: 280, h: 210 };
    const created: Block = {
      id: crypto.randomUUID(),
      kind,
      x: 60,
      y: 60,
      w: d.w,
      h: d.h,
      z: top,
      text: d.text || '',
      formula: d.formula || '',
      scenarioId: d.scenarioId || '',
      ...overrides,
    };
    get().commit([...blocks, created]);
    set({ selectedBlockId: created.id });
  },

  replaceBlocks: (next: Block[]) => {
    const { blocks } = get();
    set(state => ({
      history: [...state.history, blocks],
      future: [],
      blocks: next,
      dirty: true,
    }));
    set({ selectedBlockId: '' });
  },

  deleteBlock: (id) => {
    const { blocks } = get();
    get().commit(blocks.filter(b => b.id !== id));
    set({ selectedBlockId: '' });
  },

  duplicateBlock: (id) => {
    const { blocks } = get();
    const block = blocks.find(b => b.id === id);
    if (!block) return;
    const top = Math.max(0, ...blocks.map(b => b.z)) + 1;
    const dup: Block = {
      ...block,
      id: crypto.randomUUID(),
      x: block.x + 16,
      y: block.y + 16,
      z: top,
    };
    get().commit([...blocks, dup]);
    set({ selectedBlockId: dup.id });
  },

  bringForward: (id) => {
    const { blocks } = get();
    const maxZ = Math.max(...blocks.map(b => b.z));
    get().commit(blocks.map(b => b.id === id ? { ...b, z: maxZ + 1 } : b));
  },

  sendBackward: (id) => {
    const { blocks } = get();
    const minZ = Math.min(...blocks.map(b => b.z));
    get().commit(blocks.map(b => b.id === id ? { ...b, z: minZ - 1 } : b));
  },

  bringToFront: (id) => {
    const { blocks } = get();
    const maxZ = Math.max(...blocks.map(b => b.z));
    get().commit(blocks.map(b => b.id === id ? { ...b, z: maxZ + 1 } : b));
  },

  sendToBack: (id) => {
    const { blocks } = get();
    const minZ = Math.min(...blocks.map(b => b.z));
    get().commit(blocks.map(b => b.id === id ? { ...b, z: minZ - 1 } : b));
  },

  undo: () => {
    const { history, blocks } = get();
    const prev = history.at(-1);
    if (!prev) return;
    set(state => ({
      future: [blocks, ...state.future],
      blocks: prev,
      history: state.history.slice(0, -1),
      dirty: state.history.length > 1,
    }));
  },

  redo: () => {
    const { future, blocks } = get();
    const next = future[0];
    if (!next) return;
    set(state => ({
      history: [...state.history, blocks],
      blocks: next,
      future: state.future.slice(1),
      dirty: true,
    }));
  },

  save: async () => {
    const { book, page, blocks, contentLocale, pageDrafts } = get();
    if (!book || !page) return;
    const pageId = String(page.id);
    set({ busy: true, error: '' });
    try {
      await adminBookApi.saveBlocks(String(book.id), pageId, {
        version: n(page.version, 1),
        blocks: [...blocks]
          .sort((a, b) => a.z - b.z)
          .map(b => ({
            id: b.id,
            type: b.kind,
            translations: b.kind === 'RICH_TEXT' ? { ...b.translations, [contentLocale]: { content: b.text || '' } } : (b.translations || {}),
            data: {
              src: b.src,
              assetId: b.assetId,
              svg: b.svg,
              latex: b.formula,
              scenarioId: b.scenarioId,
              equipmentId: b.equipmentId,
              rendererKey: b.rendererKey,
              materialId: b.materialId,
              reactionId: b.reactionId,
              shapeType: b.shapeType,
              fillColor: b.fillColor,
              strokeColor: b.strokeColor,
              strokeWidth: b.strokeWidth,
              borderRadius: b.borderRadius,
              fontFamily: b.fontFamily,
              fontSize: b.fontSize,
              color: b.color,
              backgroundColor: b.backgroundColor,
              fit: b.fit,
              alt: b.alt,
              displayMode: b.displayMode,
              showLabel: b.showLabel,
              showFormula: b.showFormula,
              showName: b.showName,
              showTitle: b.showTitle,
              showDescription: b.showDescription,
              widgetType: b.widgetType,
              widgetValue: b.widgetValue,
              widgetLabel: b.widgetLabel,
              rotation: b.rotation,
              opacity: b.opacity,
              visible: b.visible,
              locked: b.locked,
              layout: { x: b.x, y: b.y, width: b.w, height: b.h, zIndex: b.z },
            },
          })),
      });

      const newDrafts = { ...pageDrafts };
      const draftKey = `${String(book.id)}:${pageId}`;
      delete newDrafts[draftKey];

      set({ notice: 'Page saved', dirty: false, pageDrafts: newDrafts });
      await get().loadBook(String(book.id), pageId);
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'Save failed' });
    } finally {
      set({ busy: false });
    }
  },

  publishBook: async () => {
    if (get().dirty) await get().save();
    if (get().dirty) return;
    const book = get().book;
    if (!book) return;
    const bookId = String(book.id);
    const version = n(book.draftVersion, 1);
    set({ busy: true, error: '', notice: '' });
    try {
      const validation = await adminBookApi.validate(bookId, { version });
      if (validation.valid === false) {
        const errors = items(validation.errors);
        throw new Error(errors.map(item => String(item.message ?? item.code ?? 'Validation error')).join('; ') || 'Book validation failed');
      }
      await adminBookApi.publish(bookId, {
        version,
        idempotencyKey: crypto.randomUUID(),
        releaseNote: 'Published from Book Studio',
      });
      await get().loadBook(bookId);
      set({ notice: 'Book published' });
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'Book publish failed' });
    } finally {
      set({ busy: false });
    }
  },

  setSelected: (id) => set({ selectedBlockId: id }),

  setContentLocale: (locale) => {
    const { page, book, pageDrafts, contentLocale: prevLocale } = get();
    if (!page || !book) {
      set({ contentLocale: locale });
      return;
    }

    const pageId = String(page.id);
    const draftKey = `${String(book.id)}:${pageId}`;

    const currentDrafts = { ...pageDrafts };
    if (!currentDrafts[draftKey]) currentDrafts[draftKey] = {} as Record<Locale, Block[]>;
    currentDrafts[draftKey][prevLocale] = get().blocks;

    const savedBlocks = currentDrafts[draftKey][locale];

    set({
      contentLocale: locale,
      blocks: savedBlocks || hydrateBookPageBlocks(page.blocks, locale),
      selectedBlockId: '',
      history: [],
      future: [],
      dirty: Boolean(savedBlocks),
      pageDrafts: currentDrafts,
    });
  },

  setActiveLeftTab: (tab) => set(state => ({ activeLeftTab: state.activeLeftTab === tab ? null : tab })),
  setInspectorTab: (tab) => set({ inspectorTab: tab }),
  setZoom: (z) => set({ zoom: Math.min(2, Math.max(0.1, z)) }),
  setPan: (x, y) => set({ panX: x, panY: y }),
  setShowGrid: (v) => set({ showGrid: v }),
  setShowGuides: (v) => set({ showGuides: v }),
  setPreviewMode: (m) => set({ previewMode: m }),
  setInteractionMode: (m) => set({ interactionMode: m }),
  setInteractiveDraft: (d) => set({ interactiveDraft: d }),
  setIsPanning: (v) => set({ isPanning: v }),
  setRightPanelOpen: (v) => set({ rightPanelOpen: v }),
  setNotice: (msg) => set({ notice: msg }),
  setError: (msg) => set({ error: msg }),

  uploadImage: async (file) => {
    set({ busy: true, error: '' });
    try {
      const response = await adminPlatformApi.assets.uploadUrls({
        files: [{ filename: file.name, contentType: file.type, sizeBytes: file.size, kind: 'IMAGE' }],
      });
      const target = obj(Array.isArray(response.uploads) ? response.uploads[0] : {});
      if (!target.uploadUrl || !target.assetId) throw new Error('Upload target was not created.');
      const uploadUrl = String(target.uploadUrl).startsWith('/')
        ? `${getApiBaseUrl()}${target.uploadUrl}`
        : String(target.uploadUrl);
      const uploaded = await fetch(uploadUrl, { method: 'PUT', headers: { 'Content-Type': file.type }, body: file });
      if (!uploaded.ok) throw new Error(`Image upload failed (${uploaded.status}).`);
      let completedAsset: JsonObject = {};
      try {
        completedAsset = obj(await adminPlatformApi.assets.complete(String(target.assetId), {}));
      } catch {
        void 0;
      }
      const src = String(completedAsset.downloadUrl ?? target.downloadUrl ?? '');
      const assetId = String(target.assetId);
      get().addBlock('IMAGE', { src, assetId });
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'Image upload failed.' });
    } finally {
      set({ busy: false });
    }
  },

  uploadSvg: async (file) => {
    try {
      const safe = sanitizeSvgMarkup(await file.text());
      if (!safe) {
        set({ error: 'Unsafe or invalid SVG' });
        return;
      }
      get().addBlock('SVG', { svg: safe });
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'SVG upload failed.' });
    }
  },

  copyBlock: (id) => {
    const { blocks } = get();
    const block = blocks.find(b => b.id === id);
    if (block) set({ clipboard: [{ ...block }] });
  },

  pasteBlock: () => {
    const { clipboard, blocks } = get();
    if (!clipboard.length) return;
    const top = Math.max(0, ...blocks.map(b => b.z)) + 1;
    const pasted = clipboard.map((b, i) => ({
      ...b,
      id: crypto.randomUUID(),
      x: b.x + 16,
      y: b.y + 16,
      z: top + i,
    }));
    get().commit([...blocks, ...pasted]);
    set({ selectedBlockId: pasted[0]?.id || '' });
  },

  discardDraft: () => {
    const { page, contentLocale, book } = get();
    if (!page || !book) return;
    const pageId = String(page.id);
    const draftKey = `${String(book.id)}:${pageId}`;
    const newDrafts = { ...get().pageDrafts };
    delete newDrafts[draftKey];
    set({
      blocks: hydrateBookPageBlocks(page.blocks, contentLocale),
      dirty: false,
      selectedBlockId: '',
      history: [],
      future: [],
      pageDrafts: newDrafts,
    });
  },

  createBook: async (title: string, slug: string) => {
    set({ busy: true, error: '' });
    try {
      const defaultLocale = get().contentLocale;
      const book = await adminBookApi.create({
        slug,
        defaultLocale,
        translations: { [defaultLocale]: { title, description: '' } },
      });
      await get().loadBooks();
      await get().loadBook(String(book.id));
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'Failed to create book' });
      throw e;
    } finally {
      set({ busy: false });
    }
  },

  createChapter: async (bookId: string, title: string) => {
    set({ busy: true, error: '' });
    try {
      const locale = get().contentLocale;
      await adminBookApi.createChapter(bookId, {
        position: get().chapters.length + 1,
        translations: { [locale]: { title: title.trim(), description: '' } },
      });
      await get().loadBook(bookId);
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'Failed to create chapter' });
      throw e;
    } finally {
      set({ busy: false });
    }
  },

  createPage: async (chapterId: string) => {
    const { book } = get();
    if (!book) return;
    set({ busy: true, error: '' });
    try {
      const locale = get().contentLocale;
      const result = await adminBookApi.createPage(String(book.id), {
        chapterId,
        position: get().pages.filter(item => String(item.chapterId) === chapterId).length + 1,
        layout: 'single-page',
        translations: { [locale]: { title: 'Untitled page' } },
      });
      await get().loadBook(String(book.id), String(result.id));
    } catch (e) {
      set({ error: e instanceof Error ? e.message : 'Failed to create page' });
    } finally {
      set({ busy: false });
    }
  },
}));

export const labelEntity = label;
