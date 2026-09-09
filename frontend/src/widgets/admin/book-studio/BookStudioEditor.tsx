'use client';

import { useEffect, useState, useCallback } from 'react';
import { useBookStudioStore } from './store/useBookStudioStore';
import { BookStudioTopBar } from './shell/BookStudioTopBar';
import { BookStudioIconRail } from './shell/BookStudioIconRail';
import { BookStudioAssetPanel } from './shell/BookStudioAssetPanel';
import { BookStudioInspector } from './shell/BookStudioInspector';
import { BookStudioBottomBar } from './shell/BookStudioBottomBar';
import { BookCanvasViewport } from './canvas/BookCanvasViewport';
import { useEditorKeyboard } from './hooks/useEditorKeyboard';
import { useAutoSave } from './hooks/useAutoSave';
import { SandboxWorkspace } from '@/widgets/sandbox/SandboxWorkspace';

export default function BookStudioEditor() {
  const { loadBooks, interactiveDraft, setInteractiveDraft, setInteractionMode, book, page, chapters } = useBookStudioStore();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [createModalTarget, setCreateModalTarget] = useState<'book' | 'chapter'>('book');

  useEffect(() => {
    void loadBooks();
  }, [loadBooks]);

  useEditorKeyboard();
  useAutoSave(3000);

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && interactiveDraft) {
        setInteractiveDraft(null);
        setInteractionMode('select');
      }
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [interactiveDraft, setInteractiveDraft, setInteractionMode]);

  const showNoBook = !book;
  const showNoChapter = Boolean(book) && chapters.length === 0;
  const showNoPage = Boolean(book) && chapters.length > 0 && !page;

  const openCreateModal = useCallback((target: 'book' | 'chapter') => {
    setCreateModalTarget(target);
    setShowCreateModal(true);
  }, []);

  return (
    <div className="flex h-screen flex-col overflow-hidden bg-[#090d16] text-slate-100">
      <BookStudioTopBar onCreateBook={() => openCreateModal('book')} />
      <div className="flex min-h-0 flex-1">
        <BookStudioIconRail />
        <BookStudioAssetPanel />
        <div className="relative flex-1 min-w-0">
          {book && page && <BookCanvasViewport />}
          {showNoBook && (
            <div className="absolute inset-0 z-[50] flex items-center justify-center bg-[#090d16]/80 backdrop-blur-sm">
              <div className="text-center">
                <p className="text-lg font-semibold text-white">No books yet</p>
                <p className="mt-2 text-sm text-slate-400">Create your first Book to get started.</p>
                <button
                  onClick={() => openCreateModal('book')}
                  className="mt-6 rounded-lg bg-violet-600 px-6 py-2.5 text-sm font-semibold text-white hover:bg-violet-500 transition-colors"
                >
                  + Create Book
                </button>
              </div>
            </div>
          )}
          {showNoChapter && (
            <div className="absolute inset-0 z-[50] flex items-center justify-center bg-[#090d16]/80 backdrop-blur-sm">
              <div className="text-center">
                <p className="text-lg font-semibold text-white">No chapters yet</p>
                <p className="mt-2 text-sm text-slate-400">Add your first Chapter to start building your Book.</p>
                <button
                  onClick={() => openCreateModal('chapter')}
                  className="mt-6 rounded-lg bg-violet-600 px-6 py-2.5 text-sm font-semibold text-white hover:bg-violet-500 transition-colors"
                >
                  + Create first Chapter
                </button>
              </div>
            </div>
          )}
          {showNoPage && (
            <div className="absolute inset-0 z-[50] flex items-center justify-center bg-[#090d16]/80 backdrop-blur-sm">
              <div className="text-center">
                <p className="text-lg font-semibold text-white">No pages yet</p>
                <p className="mt-2 text-sm text-slate-400">Add your first Page to this Chapter.</p>
                <button
                  onClick={() => {
                    const { createPage, chapters } = useBookStudioStore.getState();
                    const ch = chapters[0];
                    if (ch) void createPage(String(ch.id));
                  }}
                  className="mt-6 rounded-lg bg-violet-600 px-6 py-2.5 text-sm font-semibold text-white hover:bg-violet-500 transition-colors"
                >
                  + Add first Page
                </button>
              </div>
            </div>
          )}
        </div>
        <BookStudioInspector />
      </div>
      <BookStudioBottomBar />

      {showCreateModal && (
        <CreateEntityModal
          target={createModalTarget}
          onClose={() => setShowCreateModal(false)}
          onCreated={() => setShowCreateModal(false)}
        />
      )}

      {interactiveDraft && (
        <div className="fixed inset-0 z-[300] bg-slate-950/90 p-4 backdrop-blur" role="dialog" aria-modal="true" aria-label="Interactive Scenario preview">
          <div className="mx-auto flex h-full max-w-[1500px] flex-col overflow-hidden rounded-2xl border border-violet-400/30 bg-[#080c14]">
            <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
              <div>
                <p className="font-semibold">Interactive Scenario</p>
                <p className="text-xs text-slate-400">Sandbox runtime preview</p>
              </div>
              <button
                onClick={() => { setInteractiveDraft(null); setInteractionMode('select'); }}
                className="rounded-lg border border-white/10 bg-white/[.04] px-3 py-1.5 text-sm text-slate-200 hover:border-violet-400"
              >
                Close
              </button>
            </div>
            <div className="min-h-0 flex-1">
              <SandboxWorkspace previewDraft={interactiveDraft} embedded />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function CreateEntityModal({ target, onClose, onCreated }: { target: 'book' | 'chapter'; onClose: () => void; onCreated: () => void }) {
  const [title, setTitle] = useState('');
  const [slug, setSlug] = useState('');
  const [slugEdited, setSlugEdited] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const generatedSlug = title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
  const effectiveSlug = slugEdited ? slug : generatedSlug;

  const submit = async () => {
    if (!title.trim()) return;
    setBusy(true);
    setError('');
    try {
      if (target === 'book') {
        await useBookStudioStore.getState().createBook(title.trim(), effectiveSlug || generatedSlug);
      } else {
        const { book, createChapter } = useBookStudioStore.getState();
        if (book) await createChapter(String(book.id), title.trim());
      }
      onCreated();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to create');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[400] flex items-center justify-center bg-slate-950/60 backdrop-blur-sm" role="dialog" aria-modal="true">
      <div className="w-full max-w-md rounded-2xl border border-white/10 bg-[#0b101a] p-6 shadow-2xl">
        <h3 className="text-lg font-semibold text-white">{target === 'book' ? 'Create Book' : 'Create Chapter'}</h3>
        <div className="mt-4 space-y-3">
          <label className="block text-sm text-slate-400">Title
            <input value={title} onChange={e => setTitle(e.target.value)} className="mt-1 w-full rounded-lg border border-white/10 bg-[#080c14] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400" placeholder="Enter title" autoFocus />
          </label>
          {target === 'book' && (
            <label className="block text-sm text-slate-400">Slug
              <input value={effectiveSlug} onChange={e => { setSlug(e.target.value); setSlugEdited(true); }} className="mt-1 w-full rounded-lg border border-white/10 bg-[#080c14] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400" placeholder="auto-generated" />
            </label>
          )}
        </div>
        {error && <p className="mt-3 text-sm text-rose-300">{error}</p>}
        <div className="mt-6 flex justify-end gap-2">
          <button onClick={onClose} className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 hover:bg-white/5">Cancel</button>
          <button onClick={() => void submit()} disabled={busy || !title.trim()} className="rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white hover:bg-violet-500 disabled:opacity-40 transition-colors">
            {busy ? 'Creating...' : target === 'book' ? 'Create Book' : 'Create Chapter'}
          </button>
        </div>
      </div>
    </div>
  );
}
