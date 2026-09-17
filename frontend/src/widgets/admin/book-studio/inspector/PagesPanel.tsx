'use client';

'use client';

import { useBookStudioStore, labelEntity } from '../store/useBookStudioStore';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { Plus, ChevronDown, ChevronRight } from 'lucide-react';
import { useState } from 'react';

export function PagesPanel() {
  const { book, page, pages, selectPage, chapters, dirty, createChapter, createPage, renameChapter, renamePage } = useBookStudioStore();
  const [expandedChapters, setExpandedChapters] = useState<Set<string>>(new Set());
  const [showChapterModal, setShowChapterModal] = useState(false);
  const [chapterTitle, setChapterTitle] = useState('');
  const [createError, setCreateError] = useState('');
  const [creating, setCreating] = useState(false);
  const [pendingPageSwitch, setPendingPageSwitch] = useState<{ page: JsonObject } | null>(null);
  
  const [renamingId, setRenamingId] = useState<string | null>(null);
  const [renameValue, setRenameValue] = useState('');

  const toggleChapter = (id: string) => {
    setExpandedChapters(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const handleRenameSubmit = async (type: 'chapter' | 'page') => {
    if (!renamingId || !renameValue.trim()) {
      setRenamingId(null);
      return;
    }
    try {
      if (type === 'chapter') await renameChapter(renamingId, renameValue);
      else await renamePage(renamingId, renameValue);
    } catch (e) {
      console.error(e);
    }
    setRenamingId(null);
  };

  const addPage = async (chapterId: string) => {
    if (!book) return;
    try {
      await createPage(chapterId);
    } catch (e) {
      setCreateError(e instanceof Error ? e.message : 'Failed to create page');
      setTimeout(() => setCreateError(''), 4000);
    }
  };

  const addChapter = () => {
    setChapterTitle('');
    setCreateError('');
    setShowChapterModal(true);
  };

  const confirmCreateChapter = async () => {
    if (!book || !chapterTitle.trim()) return;
    setCreating(true);
    setCreateError('');
    try {
      await createChapter(String(book.id), chapterTitle.trim());
        </div>
      )}

      {showChapterModal && (
        <div className="fixed inset-0 z-[400] flex items-center justify-center bg-slate-950/60 backdrop-blur-sm" role="dialog" aria-modal="true" aria-label="Create chapter">
          <div className="w-full max-w-md rounded-2xl border border-white/10 bg-[#0b101a] p-6 shadow-2xl">
            <h3 className="text-lg font-semibold text-white">Create Chapter</h3>
            <p className="mt-1 text-sm text-slate-400">Enter a title for the new chapter.</p>
            <input
              autoFocus
              value={chapterTitle}
              onChange={e => setChapterTitle(e.target.value)}
              onKeyDown={e => { if (e.key === 'Enter') void confirmCreateChapter(); if (e.key === 'Escape') setShowChapterModal(false); }}
              className="mt-4 w-full rounded-lg border border-white/10 bg-[#080c14] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400"
              placeholder="Chapter title"
            />
            {createError && <p className="mt-2 text-xs text-rose-300">{createError}</p>}
            <div className="mt-4 flex justify-end gap-2">
              <button onClick={() => setShowChapterModal(false)} className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 hover:bg-white/5">Cancel</button>
              <button
                onClick={() => void confirmCreateChapter()}
                disabled={!chapterTitle.trim() || creating}
                className="rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white hover:bg-violet-500 disabled:opacity-50"
              >
                {creating ? 'Creating...' : 'Create Chapter'}
              </button>
            </div>
          </div>
        </div>
      )}

      {pendingPageSwitch && (
        <div className="fixed inset-0 z-[400] flex items-center justify-center bg-slate-950/60 backdrop-blur-sm" role="dialog" aria-modal="true" aria-label="Unsaved changes">
          <div className="w-full max-w-md rounded-2xl border border-white/10 bg-[#0b101a] p-6 shadow-2xl">
            <h3 className="text-lg font-semibold text-white">Unsaved changes</h3>
            <p className="mt-1 text-sm text-slate-400">You have unsaved changes on the current page. What would you like to do?</p>
            <div className="mt-6 flex justify-end gap-2">
              <button onClick={() => setPendingPageSwitch(null)} className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 hover:bg-white/5">Stay</button>
              <button onClick={() => confirmPageSwitch(false)} className="rounded-lg border border-rose-400/30 px-4 py-2 text-sm text-rose-200 hover:bg-rose-500/10">Discard</button>
              <button onClick={() => confirmPageSwitch(true)} className="rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white hover:bg-violet-500">Save</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
