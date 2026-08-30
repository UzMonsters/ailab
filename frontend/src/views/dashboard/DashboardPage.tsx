'use client';
import Link from 'next/link';
import Image from 'next/image';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useState, useEffect, useCallback, useRef } from 'react';
import { FlaskConical, Atom, Search, MoreVertical, Star, Clock, Trash2, Copy, Pencil, Loader2, Plus, AlertCircle, X, LayoutGrid, Grid2X2, List, SlidersHorizontal, ChevronDown, Check } from 'lucide-react';
import { useTranslations } from 'next-intl';
import type { Workspace } from '@/types';
import OnboardingHint from '@/shared/ui/OnboardingHint';
import { errorMessage } from '@/shared/utils/errorMessage';

import { workspacesApi } from '@/entities/workspace/api/workspace.api';
const menuActions = [
  { key: 'open', label: 'open', icon: FlaskConical, danger: false },
  { key: 'rename', label: 'rename', icon: Pencil, danger: false },
  { key: 'duplicate', label: 'duplicate', icon: Copy, danger: false },
  { key: 'favorite', label: 'favorite', icon: Star, danger: false },
  { key: 'trash', label: 'moveToTrash', icon: Trash2, danger: true },
] as const;

function WorkspacePreview({ name, thumbnail }: { name: string; thumbnail?: string }) {
  if (thumbnail) return <Image src={thumbnail} alt={`${name} workspace preview`} fill unoptimized sizes="320px" className="object-cover" />;

  // Give the offline chemistry workspace a real visual preview instead of the
  // initials placeholder. Other workspaces keep the lightweight generated
  // fallback until they receive their own thumbnail.
  if (name.toLowerCase().includes('sandbox')) {
    return (
      <>
        <Image
          src="/workspace-previews/cartoon-chemistry-lab-light.png"
          alt={`${name} workspace preview`}
          fill
          unoptimized
          sizes="320px"
          className="object-cover dark:hidden"
        />
        <Image
          src="/workspace-previews/cartoon-chemistry-lab.png"
          alt=""
          fill
          unoptimized
          sizes="320px"
          className="hidden object-cover dark:block"
          aria-hidden="true"
        />
      </>
    );
  }
  
  const initials = name.substring(0, 2).toUpperCase();
  const hue = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0) % 360;

  return (
    <div className="absolute inset-0 overflow-hidden flex items-center justify-center bg-[var(--card)]">
      <div className="absolute inset-0 opacity-30 dark:opacity-40" style={{ background: `radial-gradient(120% 100% at 15% 0%, hsl(${hue}, 70%, 60%), transparent 55%), radial-gradient(120% 100% at 85% 100%, hsl(${(hue + 120) % 360}, 70%, 50%), transparent 55%)` }} />
      <div className="absolute inset-0 opacity-[0.03] dark:opacity-[0.05]" style={{ backgroundImage: 'radial-gradient(var(--foreground) 1px, transparent 1px)', backgroundSize: '18px 18px' }} />
      
      <div className="relative z-10 flex flex-col items-center justify-center">
        <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white/40 dark:bg-black/20 shadow-[0_8px_32px_rgba(0,0,0,0.05)] backdrop-blur-md border border-black/5 dark:border-white/10">
          <span className="text-2xl font-bold tracking-widest text-[var(--foreground)] opacity-80">{initials}</span>
        </div>
      </div>
      
      <FlaskConical className="absolute top-4 left-4 text-[var(--foreground)] opacity-[0.07]" size={24} />
      <LayoutGrid className="absolute bottom-6 right-6 text-[var(--foreground)] opacity-[0.07]" size={32} />
      <Atom className="absolute top-8 right-8 text-[var(--foreground)] opacity-[0.04]" size={48} />
    </div>
  );
}

function SortMenu({ sort, onChange, label }: { sort: 'updated' | 'name' | 'favorite'; onChange: (value: 'updated' | 'name' | 'favorite') => void; label: string }) {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);
  const options = [
    { value: 'updated' as const, label: 'Newest' },
    { value: 'name' as const, label: 'Name A-Z' },
    { value: 'favorite' as const, label: 'Favorites' },
  ];
  useEffect(() => {
    const close = (event: MouseEvent) => { if (ref.current && !ref.current.contains(event.target as Node)) setOpen(false); };
    document.addEventListener('mousedown', close);
    return () => document.removeEventListener('mousedown', close);
  }, []);
  const selected = options.find((option) => option.value === sort) || options[0];
  return <div ref={ref} className="relative">
    <button type="button" aria-label={label} aria-haspopup="menu" aria-expanded={open} onClick={() => setOpen((value) => !value)} className="flex h-9 items-center gap-2 rounded-[var(--radius-sm)] border border-[var(--border)] bg-[var(--input)] px-3 text-xs text-[var(--foreground)] hover:border-[var(--primary)]/50">
      <SlidersHorizontal size={13} className="text-[var(--muted-foreground)]" /><span>{selected.label}</span><ChevronDown size={13} className={`text-[var(--muted-foreground)] transition-transform ${open ? 'rotate-180' : ''}`} />
    </button>
    {open && <div role="menu" className="absolute right-0 top-full z-50 mt-1 min-w-[150px] overflow-hidden rounded-[var(--radius-sm)] border border-[var(--border)] bg-[var(--popover)] py-1 shadow-[0_15px_35px_rgba(0,0,0,.35)]">
      {options.map((option) => <button key={option.value} type="button" role="menuitem" onClick={() => { onChange(option.value); setOpen(false); }} className={`flex min-h-10 w-full items-center justify-between gap-3 px-3 text-left text-xs ${option.value === sort ? 'bg-[var(--accent)] text-[var(--foreground)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--accent)] hover:text-[var(--foreground)]'}`}>{option.label}{option.value === sort && <Check size={13} className="text-[var(--primary)]" />}</button>)}
    </div>}
  </div>;
}

export default function DashboardPage() {
  const pathname = usePathname();
  const router = useRouter();
  const searchParams = useSearchParams();
  const t = useTranslations('dashboard');
  const tc = useTranslations('common');
  const tn = useTranslations('nav');
  const locale = pathname.split('/')[1] || 'en';
  const view = searchParams.get('view') || 'home';

  const [workspaces, setWorkspaces] = useState<Workspace[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [hoveredCard, setHoveredCard] = useState<string | null>(null);
  const [menuOpen, setMenuOpen] = useState<string | null>(null);
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [newName, setNewName] = useState('');
  const [renameModalOpen, setRenameModalOpen] = useState<string | null>(null);
  const [renameValue, setRenameValue] = useState('');
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);
  const [layout, setLayout] = useState<'grid' | 'list'>('grid');
  const [sort, setSort] = useState<'updated' | 'name' | 'favorite'>('updated');
  const [menuIndex, setMenuIndex] = useState(0);
  const menuRef = useRef<HTMLDivElement>(null);

  const showToast = useCallback((message: string, type: 'success' | 'error' = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 2500);
  }, []);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => { if (e.key === 'Escape') setMenuOpen(null); };
    const onDown = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) setMenuOpen(null);
    };
    document.addEventListener('keydown', onKey);
    document.addEventListener('mousedown', onDown);
    return () => { document.removeEventListener('keydown', onKey); document.removeEventListener('mousedown', onDown); };
  }, []);

  const loadWorkspaces = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await workspacesApi.list();
      setWorkspaces(data);
    } catch (e: unknown) {
      setError(errorMessage(e, 'Failed to load workspaces'));
    } finally {
      setLoading(false);
    }
  }, []);
  useEffect(() => {
    const timer = window.setTimeout(() => { void loadWorkspaces(); }, 0);
    return () => window.clearTimeout(timer);
  }, [loadWorkspaces]);

  const filtered = workspaces.filter(w => w.name.toLowerCase().includes(search.toLowerCase())).sort((a, b) => sort === 'name' ? a.name.localeCompare(b.name) : sort === 'favorite' ? Number(b.isFavorite) - Number(a.isFavorite) : new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
  const recent = filtered.slice(0, 4);
  const favorites = filtered.filter(w => w.isFavorite);

  const visibleWorkspaces = view === 'recent' ? recent : view === 'favorites' ? favorites : filtered;

  const handleCreate = async () => {
    if (!newName.trim()) return;
    try {
      const ws = await workspacesApi.create(newName.trim());
      setWorkspaces(prev => [ws, ...prev]); setCreateModalOpen(false); setNewName(''); showToast(t('created'));
    } catch (e: unknown) { showToast(errorMessage(e, 'Error'), 'error'); }
  };

  const handleRename = async () => {
    if (!renameModalOpen || !renameValue.trim()) return;
    try {
      const ws = await workspacesApi.update(renameModalOpen, { name: renameValue.trim() });
      setWorkspaces(prev => prev.map(w => w.id === renameModalOpen ? ws : w));
      setRenameModalOpen(null); setRenameValue(''); showToast(t('renamed'));
    } catch (e: unknown) { showToast(errorMessage(e, 'Error'), 'error'); }
  };

  const handleDuplicate = async (id: string) => {
    const source = workspaces.find(w => w.id === id); if (!source) return;
    try {
      const ws = await workspacesApi.duplicate(id, `${source.name} Copy`);
      setWorkspaces(prev => [ws, ...prev]); setMenuOpen(null); showToast(t('duplicated'));
    } catch (e: unknown) { showToast(errorMessage(e, 'Error'), 'error'); }
  };

  const handleFavorite = async (ws: Workspace) => {
    try {
      const updated = await workspacesApi.update(ws.id, { isFavorite: !ws.isFavorite });
      setWorkspaces(prev => prev.map(w => w.id === updated.id ? updated : w)); setMenuOpen(null); showToast(updated.isFavorite ? t('addedToFavorites') : t('removedFromFavorites'));
    } catch (e: unknown) { showToast(errorMessage(e, 'Error'), 'error'); }
  };

  const handleTrash = async (id: string) => {
    try {
      await workspacesApi.delete(id);
      setWorkspaces(prev => prev.filter(w => w.id !== id)); setMenuOpen(null); showToast(t('movedToTrash'));
    } catch (e: unknown) { showToast(errorMessage(e, 'Error'), 'error'); }
  };

  const handleOpen = (id: string) => {
    router.push(`/${locale}/workspace/sandbox?workspace=${encodeURIComponent(id)}`);
  };

  const openMenu = (ws: Workspace, e: React.MouseEvent) => {
    e.stopPropagation();
    if (menuOpen === ws.id) {
      setMenuOpen(null);
    } else {
      setMenuOpen(ws.id); setMenuIndex(0);
    }
  };

  const runMenuAction = (ws: Workspace, action: typeof menuActions[number]['key']) => {
    if (action === 'open') handleOpen(ws.id);
    else if (action === 'rename') { setRenameModalOpen(ws.id); setRenameValue(ws.name); }
    else if (action === 'duplicate') handleDuplicate(ws.id);
    else if (action === 'favorite') handleFavorite(ws);
    else if (action === 'trash') handleTrash(ws.id);
    setMenuOpen(null);
  };

  const renderCard = (ws: Workspace) => (
    <div
      key={ws.id}
      className={`group relative border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] overflow-visible cursor-pointer transition-all duration-300 hover:border-[var(--ring)]/50 hover:shadow-[0_12px_35px_rgba(0,0,0,.15)] hover:-translate-y-[2px] ${layout === 'list' ? 'flex items-center' : ''}`}
      onMouseEnter={() => setHoveredCard(ws.id)}
      onMouseLeave={() => setHoveredCard(null)}
    >
      <div className={`${layout === 'list' ? 'h-24 w-40 shrink-0 rounded-l-[var(--radius-lg)]' : 'aspect-[16/10]'} relative overflow-hidden rounded-t-[var(--radius-lg)]`} onClick={() => handleOpen(ws.id)}>
        <WorkspacePreview name={ws.name} thumbnail={ws.thumbnail} />
        {ws.isFavorite && <Star size={14} className="absolute top-3 left-3 text-[#F59E0B] fill-[#F59E0B] z-[3]" aria-label={t('favorited')} />}
        {hoveredCard === ws.id && (
          <div className="absolute inset-0 bg-black/40 flex items-center justify-center backdrop-blur-[2px] z-[2]">
            <span className="text-sm font-medium text-white px-4 py-2 bg-[var(--primary)]/90 rounded-lg">{t('openWorkspace')}</span>
          </div>
        )}
      </div>

      {/* Kebab menu button — ALWAYS top-right, above overlay */}
      <button
        onClick={(e) => openMenu(ws, e)}
        aria-label={t('workspaceActions')}
        className="absolute top-3 right-3 z-[5] w-8 h-8 rounded-md bg-[var(--popover)]/60 backdrop-blur-sm border border-[var(--border)] flex items-center justify-center text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:border-[var(--ring)]/60 hover:bg-[var(--accent)] transition-all opacity-100 sm:opacity-0 group-hover:opacity-100 focus-visible:opacity-100"
      >
        <MoreVertical size={15} />
      </button>

      {/* Dropdown anchored under the kebab icon */}
      {menuOpen === ws.id && (
        <div ref={menuRef} role="menu" aria-label={`${ws.name} actions`} onKeyDown={(event) => { if (event.key === 'ArrowDown') { event.preventDefault(); setMenuIndex((value) => (value + 1) % menuActions.length); } if (event.key === 'ArrowUp') { event.preventDefault(); setMenuIndex((value) => (value - 1 + menuActions.length) % menuActions.length); } if (event.key === 'Enter') runMenuAction(ws, menuActions[menuIndex].key); if (event.key === 'Escape') setMenuOpen(null); }} className="absolute right-3 top-12 z-[90] w-48 bg-[var(--popover)] border border-[var(--border)] rounded-[var(--radius-sm)] shadow-[0_20px_45px_rgba(0,0,0,.15)] py-1">
          {menuActions.map((action) => (
            <button
              key={action.key}
              onClick={() => runMenuAction(ws, action.key)}
              role="menuitem" tabIndex={menuActions[menuIndex].key === action.key ? 0 : -1} autoFocus={menuActions[menuIndex].key === action.key}
              className={`min-h-11 w-full flex items-center gap-2 px-3 py-2 text-xs text-left transition-colors ${menuActions[menuIndex].key === action.key ? 'bg-[var(--accent)]' : ''} ${action.danger ? 'text-[#F43F5E] hover:bg-[#F43F5E]/10' : 'text-[var(--foreground)] hover:bg-[var(--accent)]'}`}
            >
              <action.icon size={12} className={action.key === 'favorite' && ws.isFavorite ? 'text-[#F59E0B]' : action.danger ? 'text-[#F43F5E]' : ''} />
              {action.key === 'favorite' ? (ws.isFavorite ? t('removeFavorites') : t('addFavorites')) : action.key === 'trash' ? t('moveToTrash') : tc(action.label)}
            </button>
          ))}
        </div>
      )}

      <div className="min-w-0 flex-1 p-3" onClick={() => handleOpen(ws.id)}>
        <div className="text-sm font-medium truncate text-[var(--foreground)]">{ws.name}</div>
        <div className="text-[10px] text-[var(--muted-foreground)] mt-0.5 capitalize">{ws.science}</div>
      </div>
    </div>
  );

  const sectionTitle = view === 'recent' ? tn('recent') : view === 'favorites' ? tn('favorites') : t('allWorkspaces');

  if (loading) {
    return (
      <div>
        <h1 className="text-2xl font-bold mb-6">{t('myWorkspaces')}</h1>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 2xl:grid-cols-4 gap-4">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] overflow-hidden animate-pulse">
              <div className="aspect-[16/10] bg-[var(--primary)]/5" />
              <div className="p-3 space-y-2">
                <div className="h-4 bg-[var(--primary)]/10 rounded w-3/4" />
                <div className="h-3 bg-[var(--primary)]/5 rounded w-1/3" />
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center py-20">
        <AlertCircle size={48} className="text-[#F43F5E]/50 mb-4" />
        <h2 className="text-lg font-semibold mb-2">{t('unableToConnect')}</h2>
        <p className="text-sm text-[var(--muted-foreground)] mb-4 text-center max-w-md">{error}</p>
        <button onClick={loadWorkspaces} className="px-4 py-2 border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] hover:bg-white/[0.05] transition-all flex items-center gap-2">
          <X size={14} /> {tc('retry')}
        </button>
      </div>
    );
  }

  return (
    <div className="dashboard-page min-w-0">
      {/* Header row 1: title + action */}
      <div className="flex items-center justify-between gap-4 mb-5 flex-wrap">
        <h1 className="text-2xl font-bold tracking-tight">{t('myWorkspaces')}</h1>
        <button onClick={() => { setCreateModalOpen(true); setNewName(''); }} className="dashboard-primary-action flex items-center gap-2 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white py-2.5 px-4 rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_8px_20px_rgba(139,92,246,.35)] hover:-translate-y-0.5 transition-all">
          <Plus size={16} /> {t('newWorkspace')}
        </button>
      </div>

      {/* Header row 2: search + sort + layout controls */}
      <div className="flex items-center justify-between gap-3 mb-6 flex-wrap">
        <div className="relative w-full sm:w-[300px] lg:w-[420px]">
          <input type="text" placeholder={t('searchWorkspaces')} value={search} onChange={(e) => setSearch(e.target.value)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] pl-10 py-2.5 pr-8 text-sm text-[var(--foreground)] outline-none focus:border-[#8B5CF6] transition-all" aria-label={t('searchWorkspaces')} />
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" />
          {search && <button onClick={() => setSearch('')} aria-label="Clear search" className="absolute right-2.5 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)] hover:text-[var(--foreground)]"><X size={14} /></button>}
        </div>
        <div className="flex items-center gap-2">
          <SortMenu sort={sort} onChange={setSort} label={tc('sort')} />
          <div className="flex items-center gap-0.5 h-9 px-1 rounded-[var(--radius-sm)] border border-[var(--border)] bg-[var(--input)]">
            <button aria-label={t('gridView')} onClick={() => setLayout('grid')} className={`w-7 h-7 grid place-items-center rounded-md ${layout === 'grid' ? 'bg-[var(--primary)]/15 text-[var(--primary)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--accent)]'}`}><Grid2X2 size={14} /></button>
            <button aria-label={t('listView')} onClick={() => setLayout('list')} className={`w-7 h-7 grid place-items-center rounded-md ${layout === 'list' ? 'bg-[var(--primary)]/15 text-[var(--primary)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--accent)]'}`}><List size={14} /></button>
          </div>
        </div>
      </div>

      {view === 'templates' ? <section className="max-w-3xl rounded-2xl border border-[var(--border)] bg-[var(--card)] p-8 text-center shadow-sm"><h2 className="text-xl font-bold">{t('templatesTitle')}</h2><p className="mt-2 text-sm text-[var(--muted-foreground)]">{t('templatesEmpty')}</p></section> : visibleWorkspaces.length === 0 ? (
        <div className="text-center py-20">
          <FlaskConical size={48} className="text-[var(--muted-foreground)]/30 mx-auto mb-4" />
          <h2 className="text-lg font-semibold mb-2">{t('emptyTitle')}</h2>
          <p className="text-sm text-[var(--muted-foreground)] mb-4">{t('emptyDesc')}</p>
          <button onClick={() => { setCreateModalOpen(true); setNewName(''); }} className="dashboard-primary-action inline-flex items-center gap-2 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white py-2.5 px-5 rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_8px_20px_rgba(139,92,246,.35)] hover:-translate-y-0.5 transition-all">
            <Plus size={16} /> {t('createWorkspaceButton')}
          </button>
        </div>
      ) : (
        <>
          <div className="mb-8">
            <h2 className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider mb-4">
              {view === 'home' && <Clock size={12} className="inline mr-1.5" />}
              {sectionTitle}
            </h2>
            <div className={layout === 'grid' ? 'grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4' : 'space-y-3'}>
              {visibleWorkspaces.map(renderCard)}
            </div>
          </div>

        </>
      )}

      {/* Create Modal */}
      {createModalOpen && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-[100] flex items-center justify-center p-4" onClick={() => setCreateModalOpen(false)}>
          <div className="bg-[var(--card)] backdrop-blur-xl border border-[var(--border)] rounded-[var(--radius-lg)] shadow-2xl w-full max-w-md p-6" onClick={(e) => e.stopPropagation()}>
            <h2 className="text-xl font-bold mb-6 text-[var(--foreground)]">{t('createWorkspaceTitle')}</h2>
            <div className="space-y-5">
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('workspaceName')}</label><input type="text" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)] focus:ring-1 focus:ring-[var(--ring)] transition-all" placeholder={t('untitledWorkspace')} value={newName} onChange={(e) => setNewName(e.target.value)} autoFocus onKeyDown={(e) => e.key === 'Enter' && handleCreate()} /></div>
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('science')}</label>
                <div className="space-y-2">
                  <div className="w-full flex items-center gap-3 p-3 rounded-lg border border-[var(--ring)]/30 bg-[var(--primary)]/10"><FlaskConical size={18} className="text-[var(--primary)]" /><span className="text-sm font-medium text-[var(--foreground)]">{t('chemistry')}</span><span className="ml-auto w-2 h-2 rounded-full bg-[#14F195]" /></div>
                  <div className="w-full flex items-center gap-3 p-3 rounded-lg border border-[var(--border)] bg-[var(--input)] opacity-50 cursor-not-allowed"><Atom size={18} className="text-[var(--muted-foreground)]" /><span className="text-sm font-medium text-[var(--muted-foreground)]">{t('physics')}</span><span className="ml-auto text-[10px] font-mono text-[var(--muted-foreground)] uppercase">{tc('soon')}</span></div>
                  <div className="w-full flex items-center gap-3 p-3 rounded-lg border border-[var(--border)] bg-[var(--input)] opacity-50 cursor-not-allowed"><Atom size={18} className="text-[var(--muted-foreground)]" /><span className="text-sm font-medium text-[var(--muted-foreground)]">{t('biology')}</span><span className="ml-auto text-[10px] font-mono text-[var(--muted-foreground)] uppercase">{tc('soon')}</span></div>
                </div>
              </div>
            </div>
            <div className="flex items-center justify-end gap-3 mt-8">
              <button onClick={() => setCreateModalOpen(false)} className="px-5 py-2.5 border border-[var(--border)] bg-[var(--input)] text-[var(--foreground)] rounded-[var(--radius-md)] text-sm hover:bg-[var(--accent)] transition-all">{tc('cancel')}</button>
              <button onClick={handleCreate} disabled={!newName.trim()} className="dashboard-primary-action px-5 py-2.5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_4px_15px_rgba(139,92,246,.25)] disabled:opacity-50 disabled:cursor-not-allowed transition-all">{t('createWorkspaceButton')}</button>
            </div>
          </div>
        </div>
      )}

      {/* Rename Modal */}
      {renameModalOpen && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-[100] flex items-center justify-center p-4" onClick={() => setRenameModalOpen(null)}>
          <div className="bg-[var(--card)] backdrop-blur-xl border border-[var(--border)] rounded-[var(--radius-lg)] shadow-2xl w-full max-w-sm p-6" onClick={(e) => e.stopPropagation()}>
            <h2 className="text-lg font-bold mb-4 text-[var(--foreground)]">{t('renameWorkspace')}</h2>
            <input type="text" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)] focus:ring-1 focus:ring-[var(--ring)] transition-all" value={renameValue} onChange={(e) => setRenameValue(e.target.value)} autoFocus onKeyDown={(e) => e.key === 'Enter' && handleRename()} />
            <div className="flex items-center justify-end gap-3 mt-4">
              <button onClick={() => setRenameModalOpen(null)} className="px-4 py-2 border border-[var(--border)] bg-[var(--input)] text-[var(--foreground)] rounded-[var(--radius-sm)] text-sm hover:bg-[var(--accent)]">{tc('cancel')}</button>
              <button onClick={handleRename} className="dashboard-primary-action px-4 py-2 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-sm)] text-sm font-semibold disabled:opacity-50">{tc('rename')}</button>
            </div>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && (
        <div className={`fixed bottom-6 right-6 z-[200] px-4 py-3 rounded-[var(--radius-md)] border shadow-lg flex items-center gap-3 transition-all ${toast.type === 'error' ? 'border-[#F43F5E]/30 bg-[#F43F5E]/10 text-[#F43F5E]' : 'border-[#14F195]/30 bg-[#14F195]/10 text-[#14F195]'}`}>
          <span className="text-sm font-medium">{toast.message}</span>
          <button onClick={() => setToast(null)} className="opacity-60 hover:opacity-100"><X size={14} /></button>
        </div>
      )}
      <OnboardingHint storageKey="dashboard-onboarding-v2" locale={locale} kind="dashboard" />
    </div>
  );
}
